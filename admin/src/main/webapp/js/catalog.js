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
        var catalog = this;
        catalog.catalogs = [];
        $http.get('rs/catalogs').success(function(data){
            catalog.catalogs=data;
        });
    }]);

    app.controller('CategoriesController', ['$http', function($http){
        var catalog = this;
        catalog.categories = [];
        $http.get('rs/categories').success(function(data){
            catalog.categories=data;
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

})();