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

    app.controller('CatalogController', ['$http', function($http){
        var catalog = this;
        catalog.categories = [];
        // TODO implement service to return several catalogs
        $http.get('rs/catalog/1/categories').success(function(data){
            catalog.categories=data;
        });
    }]);



    app.directive("catalogEditView", function() {
          return {
            restrict:"A",
            templateUrl: "catalog-edit-view.html"
          };
     });

    app.directive("categoryEditView", function() {
        return {
            restrict:"A",
            templateUrl: "category-edit-view.html"
        };
    });

})();