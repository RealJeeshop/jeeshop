(function () {
    var app = angular.module('store-catalog', ['ngSanitize', 'ui.router']);

    app.directive("rootCategories", function () {
        return {
            restrict: "E",
            templateUrl: "views/root-categories.html"
        };
    });

    app.directive("orderDiscounts", function(){
        return {
            restrict: "E",
            templateUrl: "views/order-discounts.html"
        }
    });

    app.directive("categoryChilds", function () {
        return {
            restrict: "E",
            templateUrl: "views/category-childs.html",
            scope: {
                items: '=',
                itemtype: '='
            }
        }
    });


    // Root categories controller
    app.controller('RootCategoriesCtrl', function ($scope, $http, $translate, $state) {

        var ctrl = this;
        ctrl.rootCategories = [];

        var locale = $translate.use();

        var findRootCategories = function () {

            $http.get('rs/catalogs/1/categories?locale=' + locale)
                .success(function (data) {
                    ctrl.rootCategories = data;

                })
                .error(function (data, status) {
                    //TODO
                });
        };

        ctrl.openCategory = function (categoryId) {
            $state.go('category', {'categoryId': categoryId});
        };

        findRootCategories();

    });

    // Category controller
    app.controller('CategoryCtrl', function ($scope, $http, $translate, $stateParams) {

        var ctrl = this;

        var locale = $translate.use();

        ctrl.category = {};

        $scope.childCategories = [];
        $scope.childProducts = [];

        var findCategory = function () {

            $http.get('rs/categories/' + $stateParams.categoryId + '?locale=' + locale)
                .success(function (data) {
                    ctrl.category = data;
                })
                .error(function (data, status) {
                    // TODO
                });
        };

        /*        ctrl.getEligibleDiscounts = function () {
         $http.get('rs/discounts/eligible?locale=' + $locale.id)
         .success(function (discounts) {
         ctrl.discounts = discounts;
         });
         };*/

        var findChildCategories = function () {

            $http.get('rs/categories/' + $stateParams.categoryId + '/categories?locale=' + locale)
                .success(function (data) {
                    $scope.childCategories = data;
                })
                .error(function (data, status) {
                    // TODO
                });
        };

        var findChildProducts = function () {

            $http.get('rs/categories/' + $stateParams.categoryId + '/products?locale=' + locale)
                .success(function (data) {
                    $scope.childProducts = data;
                })
                .error(function (data, status) {
                    // TODO
                });
        };

        ctrl.hasChildCategories = function () {
            return ($scope.childCategories.length > 0);
        };

        ctrl.hasChildProducts = function () {
            return ($scope.childProducts.length > 0);
        };

        findCategory();
        findChildCategories();

        if (!ctrl.hasChildCategories()) {
            findChildProducts();
        }

        //ctrl.getEligibleDiscounts();

    });

    // Category childs elements controller
    app.controller('CategoryChildsCtrl', function ($state, $scope) {

        var ctrl = this;

        var itemType = $scope.itemtype;

        ctrl.openCategory = function (item) {
            if (itemType == "categories") {
                $state.go('category', {'categoryId': item.id});
            } else if (itemType == "products") {
                $state.go('product', {'productId': item.id});
            }
        };

        ctrl.getLargeImageUri = function (item) {
            return "/jeeshop-media/" + itemType + "/" + item.id + "/" + item.localizedPresentation.locale + "/" + item.localizedPresentation.largeImage.uri;
        };
    });

    // Product controller
    app.controller('ProductCtrl', function ($scope, $http, $translate, $stateParams) {

        var ctrl = this;

        var locale = $translate.use();

        ctrl.product = {};
        ctrl.skus = [];

        ctrl.selectedSku = null;

        var findProduct = function () {

            $http.get('rs/products/' + $stateParams.productId + '?locale=' + locale)
                .success(function (data) {
                    ctrl.product = data;
                    findProductSkus();
                })
                .error(function (data, status) {
                    // TODO
                });
        };

        // One sku per product for now
        var findProductSkus = function () {

            $http.get('rs/products/' + $stateParams.productId + '/skus?locale=' + locale)
                .success(function (data) {
                    ctrl.skus = data;
                    ctrl.selectedSku = ctrl.skus[0];
                })
                .error(function (data, status) {
                    // TODO
                });
        };

        ctrl.getLargeImageUri = function () {
            return "/jeeshop-media/products/" + ctrl.product.id + "/" + ctrl.product.localizedPresentation.locale + "/" + ctrl.product.localizedPresentation.largeImage.uri;
        };

        findProduct();

    });

    app.controller('DiscountsController', ['$http', '$translate', function ($http, $translate) {
        var ctrl = this;

        ctrl.discounts = [];

        var locale = $translate.use();

        ctrl.fillVisibleOrderDiscounts = function () {
            $http.get('rs/discounts/visible?applicableTo=ORDER&locale=' + locale)
                .success(function (discounts) {
                        ctrl.discounts = discounts;
                })
                .error(function (data, status) {
                    //TODO Handle case Discounts are not retrievable ?
                });
        };


        ctrl.fillVisibleOrderDiscounts();

    }]);
})();

