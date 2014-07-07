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

    app.controller('CatalogEntryController', ['$http','$scope', function($http,$scope){
        var ctrl = this;

        ctrl.editing=false;
        ctrl.hasSucceed=false;

        this.selectEntry = function(id){
            ctrl.editing=true;
            $http.get('rs/'+$scope.resource+'/'+id)
            .success(function(data){
                ctrl.entry=data;
            })
            .error(function(data){
                ctrl.entry={};
            });
        };

        this.editEntry = function(){
            $http.put('rs/'+$scope.resource,ctrl.entry)
            .success(function(data){
                ctrl.entry=data;
                ctrl.hasSucceed=true;
            })
            .error(function(data){
                ctrl.entry={};
                ctrl.hasSucceed=false;
            });
        };

        this.exit = function(){
            ctrl.entry={};
            ctrl.editing = false;
            ctrl.hasSucceed=false;
        };
    }]);

    app.directive("commonCatalogEditFields", function() {
        return {
            restrict:"A",
            templateUrl: "modules/catalog/common-catalog-edit-fields.html"
        };
     });

    app.directive("getCatalogEntries", ['$http', function($http, $scope) {
        return {
            restrict:"A",
            scope: {
                resource: "@resourceType"
            },
            controller: function($http, $scope) {
                var ctrl = this;
                ctrl.entries = [];
                ctrl.resourceType = $scope.resource;

                $http.get('rs/'+$scope.resource).success(function(data){
                    ctrl.entries=data;
                });
            },
            controllerAs: 'catalogEntriesCtrl',
            templateUrl: "modules/catalog/catalog-entries.html"
        };
     }]);

})();