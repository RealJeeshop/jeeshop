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

    app.controller('CatalogEntryController', function(){

        this.entry = {};

        this.editEntry = function(){
            alert("to be implemented");
        };
    });

    app.directive("getCatalogEntries", ['$http', function($http) {
        return {
            restrict:"A",
            scope: {
                resource: "@resourceType"
            },
            controller: function($http, $scope) {
                var ctrl = this;
                ctrl.mode = 'list';
                ctrl.entries = [];
                ctrl.entryId = null;
                ctrl.currentEntry={};
                ctrl.resourceType = $scope.resource;

                $http.get('rs/'+$scope.resource).success(function(data){
                    ctrl.entries=data;
                });

                this.selectEntry = function(id){
                    ctrl.mode='edit';
                    $http.get('rs/'+ctrl.resourceType+'/'+id).success(function(data){
                        ctrl.currentEntry=data;
                    });
                }
            },
            controllerAs: 'catalogEntriesCtrl',
            templateUrl: "modules/catalog/catalog-entries.html"
        };
     }]);

})();