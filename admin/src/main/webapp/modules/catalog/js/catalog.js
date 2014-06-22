(function (){
    var app = angular.module('admin-catalog',[]);

    app.controller('TabController', function(){
        this.tabId = 1;

        this.selectTab = function(setId){
            this.tabId = setId;
        };

        this.isSelected = function(checkId){
            return this.tabId === checkId;
        };
    });

    app.controller('CatalogsController', ['$http', function($http){
        var ctrl = this;
        ctrl.catalogs = [];
        $http.get('rs/catalogs').success(function(data){
            ctrl.catalogs=data;
        });
    }]);

    app.controller('CategoriesController', ['$http', function($http){
        var ctrl = this;
        ctrl.categories = [];
        $http.get('rs/categories').success(function(data){
            ctrl.categories=data;
        });
    }]);

    app.controller('ProductsController', ['$http', function($http){
        var ctrl = this;
        ctrl.products = [];
        $http.get('rs/products').success(function(data){
            ctrl.products=data;
        });
    }]);

    app.controller('SKUsController', ['$http', function($http){
        var ctrl = this;
        ctrl.skus = [];
        $http.get('rs/skus').success(function(data){
            ctrl.skus=data;
        });
    }]);

    app.directive("catalogs", function() {
          return {
            restrict:"A",
            templateUrl: "modules/catalog/catalogs.html"
          };
     });

    app.directive("categories", function() {
        return {
            restrict:"A",
            templateUrl: "modules/catalog/categories.html"
        };
    });

    app.directive("products", function() {
        return {
            restrict:"A",
            templateUrl: "modules/catalog/products.html"
        };
    });

    app.directive("skus", function() {
        return {
            restrict:"A",
            templateUrl: "modules/catalog/skus.html"
        };
    });

})();