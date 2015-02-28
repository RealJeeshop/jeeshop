(function () {
    var app = angular.module('admin-order', []);

    app.directive("orderForm", function () {
        return {
            restrict: "A",
            templateUrl: "modules/order/order-form.html"
        };
    });

    app.directive("orderEntries", function () {
        return {
            restrict: "A",
            templateUrl: "modules/order/order-entries.html"
        };
    });

    app.directive("orderOperations", function () {
        return {
            restrict: "A",
            templateUrl: "modules/order/order-operations.html"
        };
    });

    app.controller('OrderTabController', ['$scope', function ($scope) {
        this.tabId = 1;

        this.selectTab = function (setId) {
            this.tabId = setId;
        };

        this.isSelected = function (checkId) {
            return this.tabId === checkId;
        };
    }]);


    app.controller('OrderOperationController', ['$http', '$modal', function ($http) {

        var ctrl = this;
        ctrl.alerts = [];
        ctrl.isProcessing = false;
        ctrl.skuId = null;

        ctrl.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };

        var escapeCSVField = function (field) {
            if (field==null){
                return "";
            }
            var escaped =  field.replace(/"/g,'');
            escaped = escaped.replace(/<br\/*>|<p>/g,' ');
            escaped = escaped.replace(/<.*>/g,''); // remove html tags
            return escaped;
        };

        ctrl.payedOrdersAsCSV = function () {
            ctrl.isProcessing = true;
            ctrl.alerts = [];

            var paymentValidatedStatus = "PAYMENT_VALIDATED";

            var uri = 'rs/orders?status='+paymentValidatedStatus+'&orderBy=id&isDesc=false&enhanced=true';
            if (ctrl.skuId != null){
                uri += '&skuId='+ctrl.skuId;
            }
            $http.get(uri).success(function (orders) {
                ctrl.isProcessing = false;

                if (orders.length == 0){
                    ctrl.alerts.push({type: 'warning', msg: 'No orders found matching criteria with status '+paymentValidatedStatus});
                    return;
                }

                var csvContent = "Order ID;Order Reference;Company;Gender;First name;Last name;Address;Zip Code;City;Country code (ISO 3166);Phone;E-mail;Product name;Product reference;Quantity\n";
                for (i in orders){
                    var order = orders[i];
                    for (j in order.items){
                        var orderItem = order.items[j];
                        csvContent += order.id
                        + ";" + order.reference
                        + ";" + escapeCSVField(order.deliveryAddress.company)
                        + ";" + escapeCSVField(order.deliveryAddress.gender)
                        + ";" + escapeCSVField(order.deliveryAddress.firstname)
                        + ";" + escapeCSVField(order.deliveryAddress.lastname)
                        + ';' + escapeCSVField(order.deliveryAddress.street)
                        + ';' + escapeCSVField(order.deliveryAddress.zipCode)
                        + ';' + escapeCSVField(order.deliveryAddress.city)
                        + ';' + escapeCSVField(order.deliveryAddress.countryIso3Code)
                        + ';' + escapeCSVField(order.user.phoneNumber)
                        + ';' + escapeCSVField(order.user.login)
                        + ';' + escapeCSVField(orderItem.displayName)
                        + ';' + escapeCSVField(orderItem.skuReference)
                        + ';' + orderItem.quantity+'\n';
                    }
                }


                var encodedUri = encodeURI('data:text/csv;charset=utf-8,'+csvContent);
                window.open(encodedUri);

            }).error(function(){
                if (orders.length == 0){
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                }
            });
        };
    }]);

    app.controller('OrdersController', ['$http', '$modal', function ($http, $modal) {
        var ctrl = this;
        ctrl.alerts = [];
        ctrl.entries = [];
        ctrl.currentPage = 1;
        ctrl.totalCount = null;
        ctrl.pageSize = 10;
        ctrl.searchValue = null;
        ctrl.isProcessing = false;
        ctrl.entry = {};
        ctrl.isEditionModeActive = false;
        ctrl.isCreationModeActive = false;
        ctrl.searchOnlyPaymentValidated = false;

        ctrl.skuPerId = [];
        ctrl.discountPerId = [];

        ctrl.getOrderItemsSKUs = function(){
            if (ctrl.entry.items == null){
                return;
            }

            for (i in ctrl.entry.items) {
                $http.get('rs/skus/' + ctrl.entry.items[i].skuId)
                    .success(function (data) {
                        ctrl.skuPerId[data.id] = data;
                    });

            }
        };

        ctrl.getOrderDiscounts = function(){
            if (ctrl.entry.orderDiscounts == null){
                return;
            }

            for (i in ctrl.entry.orderDiscounts) {
                $http.get('rs/discounts/' + ctrl.entry.orderDiscounts[i].discountId)
                    .success(function (data) {
                        ctrl.discountPerId[data.id] = data;
                    });

            }
        };

        ctrl.findEntries = function (orderBy, isDesc) {
            ctrl.isProcessing = true;
            ctrl.alerts = [];
            var offset = ctrl.pageSize * (ctrl.currentPage - 1);

            var uri = 'rs/orders?start=' + offset + '&size=' + ctrl.pageSize;
            var countURI = 'rs/orders/count';
            if (ctrl.searchValue != null && !(ctrl.searchValue === "")) {
                var searchArg = '&search=' + ctrl.searchValue;
                uri = uri + searchArg;
                countURI = countURI + '?' + searchArg;
            }

            if (ctrl.searchOnlyPaymentValidated) {
                var searchArg = '&status=PAYMENT_VALIDATED';
                uri = uri + searchArg;
                countURI = countURI + '?' + searchArg;
            }

            if (orderBy != null && isDesc !=null){
                uri = uri + '&orderBy=' + orderBy+'&isDesc='+isDesc;
            }

            $http.get(uri).success(function (data) {
                ctrl.entries = data;
                ctrl.isProcessing = false;
            });

            $http.get(countURI).success(function (data) {
                ctrl.totalCount = data;
                ctrl.isProcessing = false;
            });

        };


        ctrl.findEntries();

        ctrl.pageChanged = function () {
            ctrl.findEntries();
        };

        ctrl.delete = function (index, message) {
            var modalInstance = $modal.open({
                templateUrl: 'util/confirm-delete-danger.html',
                controller: function ($modalInstance, $scope) {
                    $scope.modalInstance = $modalInstance;
                    $scope.confirmMessage = message;
                },
                size: 'sm'});
            modalInstance.result.then(function () {
                ctrl.alerts = [];
                $http.delete('rs/orders/' + ctrl.entries[index].id)
                    .success(function (data) {
                        ctrl.entries.splice(index, 1);
                        ctrl.findEntries();
                    })
                    .error(function (data) {
                        ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                    });

            }, function () {

            });
        };

        this.activateCreationMode = function () {
            ctrl.isCreationModeActive = true;
        };

        ctrl.selectEntry = function (id) {
            ctrl.skuPerId = [];
            ctrl.discountPerId = [];
            $http.get('rs/orders/' + id)
                .success(function (data) {
                    ctrl.isEditionModeActive = true;
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                    ctrl.getOrderItemsSKUs();
                    ctrl.getOrderDiscounts();
                });
        };

        ctrl.createOrEdit = function () {

            if (ctrl.isCreationModeActive) {
                ctrl.create();
            } else if (ctrl.isEditionModeActive) {
                ctrl.edit();
            }
        };


        ctrl.create = function () {
            $http.post('rs/orders', ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                    ctrl.alerts.push({type: 'success', msg: 'Creation complete'});
                    ctrl.isCreationModeActive = false;
                    ctrl.isEditionModeActive = true;
                })
                .error(function (data) {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                });
        };

        ctrl.edit = function () {
            $http.put('rs/orders', ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                    ctrl.alerts.push({type: 'success', msg: 'Update complete'})
                })
                .error(function (data) {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                });
        };

        ctrl.convertEntryDates = function () {
            // hack for dates returned as timestamp by service
            ctrl.entry.creationDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
            ctrl.entry.updateDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
            ctrl.entry.paymentDate = ctrl.entry.paymentDate != null ? new Date(ctrl.entry.paymentDate) : null;
            ctrl.entry.deliveryDate = ctrl.entry.deliveryDate != null ? new Date(ctrl.entry.deliveryDate) : null;

        };

        ctrl.leaveEditView = function () {
            ctrl.findEntries();
            ctrl.isEditionModeActive = false;
            ctrl.isCreationModeActive = false;
            ctrl.entry = {};
            ctrl.alerts = [];
        };

        ctrl.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };
    }]);

})();