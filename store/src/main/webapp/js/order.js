(function () {
    var app = angular.module('store-order', ['ngSanitize', 'ui.router']);

    app.controller('ShoppingCartController', ['$modal', 'ShoppingCart', '$scope', '$state', function ($modal, ShoppingCart, $scope, $state) {
        var ctrl = this;
        ctrl.skusQuantity = {};

        ctrl.getSkuQuantity = function (skuId) {

            if (ctrl.skusQuantity[skuId] == null) {
                ctrl.skusQuantity[skuId] = 0;
            }

            return ctrl.skusQuantity[skuId];
        };

        ctrl.addItemToCart = function (skuId, productId) {

            if (ctrl.skusQuantity[skuId] > 0) {
                ShoppingCart.addItem(skuId, productId, ctrl.skusQuantity[skuId]);
                ctrl.openShoppingCartModal();
            }
        };

        ctrl.incrementSkuQuantity = function (skuId) {
            var quantity = ctrl.skusQuantity[skuId];

            ctrl.skusQuantity[skuId] = quantity + 1;

        };

        ctrl.isIncrementable = function (sku) {
            if (sku == null) {
                return false;
            }
            return (ctrl.skusQuantity[sku.id] < (sku.quantity - sku.threshold));
        };

        ctrl.decrementSkuQuantity = function (skuId) {
            var quantity = ctrl.skusQuantity[skuId];

            if (quantity >= 1) {
                ctrl.skusQuantity[skuId] = quantity - 1;
            }
        };

        ctrl.isDecrementable = function (skuId) {
            return (ctrl.skusQuantity[skuId] > 0);
        };

        ctrl.getItemsQuantity = function () {
            return ShoppingCart.getItemsQuantity();
        };

        ctrl.openShoppingCartModal = function () {

            var modalInstance = $modal.open({
                templateUrl: 'views/shopping-cart.html',
                controller: ShoppingCartModalCtrl,
                size: 'lg'
            });
            modalInstance.result.then(function () {
            }, function () {

            });

        };

        var ShoppingCartModalCtrl = function ($scope, $state, $modalInstance, ShoppingCart, $http, $locale, AuthService) {

            $scope.isProcessing = false;

            $scope.shippingFee = 0.0;
            $scope.vat = null;
            $scope.discounts = [];

            var ctrl = this;

            // array of {sku,quantity,product}
            $scope.items = ShoppingCart.getEnhancedItems();


            $http.get('rs/fees/shipping')
                .success(function (data) {
                    $scope.shippingFee = parseFloat(data);
                });
            $http.get('rs/fees/vat')
                .success(function (data) {
                    $scope.vat = parseFloat(data);
                });


            if (AuthService.isAuthenticated()){
                $http.get('rs/discounts/eligible?locale=' + $locale.id)
                    .success(function (discounts) {
                        $scope.discounts = discounts;
                    });
            }

            $scope.validateShoppingCart = function () {
                $state.go("delivery");
                $modalInstance.close();
            };

            $scope.updateItemsQuantity = function (skuId, quantity) {
                var indexToRemove = -1;

                for (var i in $scope.items) {
                    if ($scope.items[i].sku.id == skuId) {
                        $scope.items[i].quantity = quantity;

                        if (quantity == 0) {
                            indexToRemove = i;
                        }
                    }
                }

                if (indexToRemove > -1) {
                    $scope.items.splice(indexToRemove, 1);
                }
            };

            $scope.incrementSkuQuantity = function (skuId) {
                var quantity = ShoppingCart.getItemQuantity(skuId);
                ShoppingCart.setItemQuantity(skuId, quantity + 1);
                $scope.updateItemsQuantity(skuId, quantity + 1);
            };

            $scope.isIncrementable = function (sku) {
                if (sku == null) {
                    return false;
                }
                return (ShoppingCart.getItemQuantity(sku.id) < (sku.quantity - sku.threshold));
            };

            $scope.decrementSkuQuantity = function (skuId) {
                var quantity = ShoppingCart.getItemQuantity(skuId);
                if (quantity >= 1) {
                    ShoppingCart.setItemQuantity(skuId, quantity - 1);
                }

                if (quantity == 1) {
                    ShoppingCart.removeItem(skuId);
                }
                $scope.updateItemsQuantity(skuId, quantity - 1);

                if ($scope.items.length == 0){
                    $modalInstance.close();
                }

            };

            $scope.isDecrementable = function (skuId) {
                return (ShoppingCart.getItemQuantity(skuId) > 0);
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

            $scope.getItemsPrice = function () { // TODO Duplicated!!
                var price = 0.0;
                for (var i in $scope.items) {
                    price += $scope.items[i].sku.price * $scope.items[i].quantity;
                }

                return price;
            };

            $scope.getOrdersPrice = function () { // TODO Duplicated!!
                var price = 0.0;
                for (var i in $scope.items) {
                    price += $scope.items[i].sku.price * $scope.items[i].quantity;
                }

                var originalPrice = price;

                for (var i in $scope.discounts) {
                    var discount = $scope.discounts[i];

                    if (discount.triggerRule != 'AMOUNT' || discount.triggerValue <= price) {

                        if (discount.type == "DISCOUNT_RATE") {
                            price = price - originalPrice * discount.discountValue / 100;

                        } else if (discount.type == "ORDER_DISCOUNT_AMOUNT" || discount.type == "SHIPPING_FEE_DISCOUNT_AMOUNT") {
                            price = price - discount.discountValue;
                        }
                    }
                }

                return price + $scope.shippingFee;

            };
        };


    }]);

    app.factory('ShoppingCart', ['$http', '$locale', function ($http, $locale) {
        var cart = this;
        cart.items = []; //Array of [skuId->{quantity, productId}]
        cart.order = {}; // The order to be created
        cart.paymentFormResponse = "";
        cart.itemsQuantity = 0;
        cart.orderCreated = false; // true means created in backend

        var computeShoppingCartQuantity = function () {
            var count = 0;
            for (var skuId in cart.items) {
                count += cart.items[skuId].quantity;
            }
            cart.itemsQuantity = count;
        };

        return {
            getItems: function () {
                return cart.items;
            },
            getItemQuantity: function (skuId) {
                if (cart.items[skuId] == null) {
                    return 0;
                }
                return cart.items[skuId].quantity;
            },
            getItemsQuantity: function () {
                return cart.itemsQuantity;
            },
            setItemQuantity: function (skuId, quantity) {
                cart.items[skuId].quantity = quantity;
                computeShoppingCartQuantity();
            },

            addItem: function (skuId, productId, quantity) {

                if (quantity > 0) {
                    cart.items[skuId] = {quantity: quantity, productId: productId};
                }
                computeShoppingCartQuantity();
            },
            removeItem: function (skuId) {
                delete cart.items[skuId];
                computeShoppingCartQuantity();
            },
            getOrderItems: function () {
                var items = [];
                for (var skuId in cart.items) {
                    items.push({
                        skuId: skuId,
                        productId: cart.items[skuId].productId,
                        quantity: cart.items[skuId].quantity
                    });
                }
                return items;
            },
            getEnhancedItems: function () {

                var enhancedItems = [];

                for (var skuId in cart.items) {
                    $http.get('rs/skus/' + skuId + '?locale=' + $locale.id)
                        .success(function (sku) {

                            sku.numberOfBottles = parseInt(sku.name.substr(sku.name.length - 1)); // Enhance sku with number of bottles from sku name

                            $http.get('rs/products/' + cart.items[sku.id].productId + '?locale=' + $locale.id)
                                .success(function (product) {
                                    enhancedItems.push({
                                        sku: sku,
                                        product: product,
                                        quantity: cart.items[sku.id].quantity
                                    });

                                }).error(function (data, status) {
                                    // TODO ERROR
                                });

                        })
                        .error(function (data, status) {
                            // TODO ERROR
                        });
                }

                return enhancedItems;
            },

            addOrder: function (order) {
                cart.order = order;
            },
            getOrder: function () {
                return cart.order;
            },
            isOrderCreated: function () {
                return cart.orderCreated;
            },
            setOrderCreated: function () {
                cart.orderCreated = true;
            },
            cleanUp : function(){
                cart.items = []; //Array of [skuId->{quantity, productId}]
                cart.order = {}; // The order to be created
                cart.paymentFormResponse = "";
                cart.itemsQuantity = 0;
                cart.orderCreated = false;
            }
        };
    }]);

    app.controller('OrderContactInfoController', ['ShoppingCart', '$state', '$http', '$scope', function (ShoppingCart, $state, $http,$scope) {

        var ctrl = this;

        ctrl.showBillingAddress = false;
        ctrl.order = ShoppingCart.getOrder();
        ctrl.items = [];

        ctrl.shippingFee = null;

        ctrl.getEnhancedItems = function () {
            ctrl.items = ShoppingCart.getEnhancedItems();
        };

        ctrl.getEnhancedItems();

        ctrl.createOrderWithContactInfo = function () { // Initialize order with contactInfo

            if (ShoppingCart.isOrderCreated()) {
                window.history.forward();
                return;
            }

            if (!ctrl.showBillingAddress) {
                ctrl.order.billingAddress = ctrl.order.deliveryAddress;
            }

            ShoppingCart.addOrder(ctrl.order);
            $state.go("orderconfirm");
        };

        ctrl.getShippingFee = function () {

            $http.get('rs/fees/shipping')
                .success(function (data) {
                    ctrl.shippingFee = data;
                });
        };

        ctrl.getShippingFee();

        ctrl.cleanOrder = function(){
            ShoppingCart.cleanUp();
            $state.go('categories');
        };

        ctrl.isOrderCreated = function(){
            return ShoppingCart.isOrderCreated();
        }


    }]);

    app.controller('OrderConfirmController', ['ShoppingCart', '$state', '$http', '$scope', '$sce', '$locale', function (ShoppingCart, $state, $http, $scope, $sce, $locale) {

        var ctrl = this;

        ctrl.shippingFee = 0.0;
        ctrl.vat = 0.0;
        ctrl.discounts = [];

        ctrl.order = {};
        $scope.isProcessing = false;
        ctrl.items = [];
        ctrl.paymentFormResponse = ShoppingCart.paymentFormResponse;
        ctrl.orderCreated = false;

        ctrl.transactionProcessed = ShoppingCart.transactionProcessed;

        ctrl.order = ShoppingCart.getOrder();

        ctrl.getEnhancedItems = function () {
            ctrl.items = ShoppingCart.getEnhancedItems();
        };

        ctrl.continueShopping = function () {
            $state.go("categories");
        };

        ctrl.getEnhancedItems(); // Initialize order items

        ctrl.confirmOrder = function () { // Create order in backend

            if (ShoppingCart.isOrderCreated()) {
                window.history.forward();
                return;
            }

            $scope.isProcessing = true;
            ctrl.order.items = ShoppingCart.getOrderItems();
            $http.post('rs/orders', ctrl.order)
                .success(function (data) {
                    $scope.isProcessing = false;
                    ctrl.order = data;
                    ShoppingCart.paymentFormResponse = $sce.trustAsHtml(
                        "<script type=\"text/javascript\">$(\"#payment-wrapper form\").submit(function () {angular.element('#orderconfirmcontroller').controller().setTransactionProcessedToTrue();$(\"#payment-wrapper\").hide();}); </script>"
                        +ctrl.order.paymentInfo.paymentFormResponse);

                    ShoppingCart.setOrderCreated();
                    $state.go("payment");
                })
                .error(function (data, status) {
                    $scope.isProcessing = false; // TODO
                });
        };

        ctrl.getShippingFeeAndVAT = function () {

            $http.get('rs/fees/shipping')
                .success(function (data) {
                    ctrl.shippingFee = parseFloat(data);
                });
            $http.get('rs/fees/vat')
                .success(function (data) {
                    ctrl.vat = parseFloat(data);
                });
        };

        ctrl.getEligibleDiscounts = function () {

            $http.get('rs/discounts/eligible?locale=' + $locale.id)
                .success(function (discounts) {
                    ctrl.discounts = discounts;
                });
        };

        ctrl.getShippingFeeAndVAT();
        ctrl.getEligibleDiscounts();

        ctrl.getItemsPrice = function () {
            var price = 0.0;
            for (var i in ctrl.items) {
                price += (ctrl.items[i].sku.price * ctrl.items[i].quantity);
            }

            return price;
        };

        ctrl.getOrdersPrice = function () {
            var price = 0.0;
            for (var i in ctrl.items) {
                price += (ctrl.items[i].sku.price * ctrl.items[i].quantity);
            }

            var originalPrice = price;

            for (var i in ctrl.discounts) {
                var discount = ctrl.discounts[i];
                if (discount.triggerRule != 'AMOUNT' || discount.triggerValue <= price) {
                    if (discount.type == "DISCOUNT_RATE") {
                        price = price - originalPrice * discount.discountValue / 100;

                    } else if (discount.type == "ORDER_DISCOUNT_AMOUNT" || discount.type == "SHIPPING_FEE_DISCOUNT_AMOUNT") {
                        price = price - discount.discountValue;
                    }
                }
            }

            return price + ctrl.shippingFee;
        };

        ctrl.setTransactionProcessedToTrue = function(){
            ShoppingCart.paymentFormResponse = "";
            ctrl.transactionProcessed = true;
        };

        ctrl.cleanOrder = function(){
            ShoppingCart.cleanUp();
            $state.go('categories');
        };

        ctrl.goBackToPaymentPlatform = function(){
            window.history.forward();
            return;
        }

    }]);


    app.controller('OrdersHistoryController', ['$http', '$scope', '$modal', function ($http, $scope, $modal) {

        var ctrl = this;

        ctrl.orders = [];
        $scope.isProcessing = false;

        ctrl.getUserOrders = function () {

            $scope.isProcessing = true;

            $http.get('rs/orders?orderBy=creationDate&isDesc=true')
                .success(function (orders) {
                    $scope.isProcessing = false;
                    ctrl.orders = orders;
                })
                .error(function (data, status) {
                    $scope.isProcessing = false; // TODO
                });
        };

        ctrl.openOrderHistoryModal = function (orderId) {

            var modalInstance = $modal.open({
                templateUrl: 'views/order-modal.html',
                controller: OrderHistoryModalController,
                resolve: {
                    orderId: function () {
                        return orderId;
                    }
                },
                size: 'lg'
            });
            modalInstance.result.then(function () {
            }, function () {
            });

        };

        var OrderHistoryModalController = function ($scope, $state, $modalInstance, $http, orderId) {

            $scope.isProcessing = false;
            $scope.order = {};
            $scope.shippingFee = 0.0;
            $scope.vat = 0.0;

            var ctrl = this;

            ctrl.getOrderById = function () {

                $scope.isProcessing = true;

                $http.get('rs/orders/' + orderId + '?enhanced=true')
                    .success(function (order) {
                        $scope.isProcessing = false;
                        $scope.order = order;
                    })
                    .error(function (data, status) {
                        $scope.isProcessing = false;
                    });

                $http.get('rs/fees/shipping')
                    .success(function (data) {
                        $scope.shippingFee = parseFloat(data);
                    });
                $http.get('rs/fees/vat')
                    .success(function (data) {
                        $scope.vat = parseFloat(data);
                    });
            };

            $scope.close = function () {
                $modalInstance.dismiss('close');
            };

            ctrl.getOrderById();

        };

        ctrl.getUserOrders();

    }]);

})();