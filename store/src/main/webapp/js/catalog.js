(function () {
    var app = angular.module('store-catalog', ['ngSanitize', 'ui.router']);

    app.directive("rootCategories", function () {
        return {
            restrict: "E",
            templateUrl: "views/root-categories.html"
        };
    });

    app.directive("categoryChilds", function(){
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
    app.controller('RootCategoriesCtrl', function ($scope, $http, $locale, $state) {

        var ctrl = this;
        ctrl.rootCategories = [];

        var findRootCategories = function () {

            $http.get('rs/catalogs/1/categories?locale=' + $locale.id)
                .success(function (data) {
                    var categories = data;
                    ctrl.rootCategories = categories;

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
    app.controller('CategoryCtrl', function ($scope, $http, $locale, $stateParams) {

        var ctrl = this;

        ctrl.category = {};

        $scope.childCategories = [];
        $scope.childProducts = [];

        var findCategory = function () {

            $http.get('rs/categories/' + $stateParams.categoryId + '?locale=' + $locale.id)
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

            $http.get('rs/categories/' + $stateParams.categoryId + '/categories?locale=' + $locale.id)
                .success(function (data) {
                    $scope.childCategories = data;
                })
                .error(function (data, status) {
                    // TODO
                });
        };

        var findChildProducts = function () {

            $http.get('rs/categories/' + $stateParams.categoryId + '/products?locale=' + $locale.id)
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
            if (itemType == "categories"){
                $state.go('category', {'categoryId': item.id});
            }else if (itemType == "products"){
                $state.go('product', {'productId': item.id});
            }
        };

        ctrl.getLargeImageUri = function(item){
            return "/jeeshop-media/"+itemType+"/" + item.id + "/" + item.localizedPresentation.locale + "/" + item.localizedPresentation.largeImage.uri;
        };
    });

        // Product controller
    app.controller('ProductCtrl', function ($scope, $http, $locale, $stateParams) {

        var ctrl = this;

        var slides = $scope.slides = []; // Array of products for slide displaying
        var products3Column = $scope.products3Column = []; // Array of products arranged by 3

        ctrl.productsSku = [];

        // One sku per product for now
        var getProductSkus = function (i, products) {

            var productId = products[i].id;

            $http.get('rs/products/' + productId + '/skus?locale=' + $locale.id)
                .success(function (data) {
                    var skus = data;
                    var sku = null;
                    if (skus.length >= 1) {
                        sku = skus[0];
                    }

                    if (sku == null || !sku.available) {
                        return;
                    }

                    $scope.productsSku[productId] = sku;

                    sku.numberOfBottles = parseInt(sku.name.substr(sku.name.length - 1)); // Enhance sku with number of bottles from sku name

                    slides.push(products[i]);

                    if (products3Column[products3Column.length - 1] == null || products3Column[products3Column.length - 1].length == 3) {
                        products3Column.push([]);
                    }

                    products3Column[products3Column.length - 1].push(products[i]);

                })
                .error(function (data, status) {
                    // TODO
                });
        };
    });
})();

