(function () {

    var app = angular.module('admin-catalog', []);

    /*----- Directives -----*/

    app.directive("commonCatalogFormFields", function () {
            return {
                restrict: "A",
                templateUrl: "modules/catalog/common-catalog-form-fields.html"
            };
        });

    app.directive("catalogRelationshipsForm",function () {
            return {
                restrict: "A",
                scope: {
                    relationshipsTitle: "@relationshipsTitle",
                    relationshipsProperty: "=",
                    resource: "@resource",
                    relationshipsParentResource: "=",
                    relationshipsParentId: "="
                },
                templateUrl: "modules/catalog/catalog-relationships-accordion.html"
            };
        });

    app.directive("getCatalogEntries", ['$http', function ($http, $scope) {
        return {
            restrict: "A",
            scope: {
                resource: "@resourceType"
            },
            controller: function ($http, $scope) {
                var ctrl = this;
                ctrl.alerts=[];
                ctrl.entries = [];
                ctrl.resourceType = $scope.resource;
                ctrl.currentPage = 1;
                ctrl.totalCount = null;
                ctrl.pageSize = 10;

                $scope.findEntries = function (){
                    alerts = [];
                    offset = ctrl.pageSize *(ctrl.currentPage -1);
                    $http.get('rs/' + $scope.resource+"?start="+offset+"&size="+ctrl.pageSize).success(function (data) {
                        ctrl.entries = data;
                    });

                    $http.get('rs/' + $scope.resource+'/count').success(function (data) {
                        ctrl.totalCount = data;
                    });
                }

                $scope.findEntries();

                this.pageChanged = function (){
                    $scope.findEntries();
                };

                this.delete = function (index) {
                    alerts = [];
                    $http.delete('rs/' + $scope.resource+"/"+ctrl.entries[index].id)
                        .success(function (data) {
                            ctrl.entries.splice(index);
                        })
                        .error(function (data) {
                            ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                    });
                    $scope.findEntries();
                };
            },
            controllerAs: 'catalogEntriesCtrl',
            templateUrl: "modules/catalog/catalog-entries.html"
        };
    }]);


    /*----- Controllers -----*/

    app.controller('TabController', ['$scope', function ($scope) {
        this.tabId = 1;

        this.selectTab = function (setId) {
            this.tabId = setId;
        };

        this.isSelected = function (checkId) {
            return this.tabId === checkId;
        };
    }]);

    app.controller('CatalogEntryController', ['$http', '$scope', function ($http, $scope) {
        var ctrl = this;

        ctrl.alerts = [];
        ctrl.isEditionModeActive = false;
        ctrl.isCreationModeActive = false;
        ctrl.entry = {};
        ctrl.entryChilds={};

        this.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };

        this.selectEntry = function (id) {
            $http.get('rs/' + $scope.resource + '/' + id)
                .success(function (data) {
                    ctrl.isEditionModeActive = true;
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                });

            ctrl.entryChilds.rootCategories = [];
            ctrl.entryChilds.childCategories = [];
            ctrl.entryChilds.childProducts = [];
            ctrl.entryChilds.childSKUs = [];
            ctrl.entryChilds.discounts = [];

        };

        this.createOrEdit = function(){

            if ($scope.resource === 'catalogs'){
                ctrl.entry.rootCategoriesIds = new Array();
                for (i in ctrl.entryChilds.rootCategories){
                    ctrl.entry.rootCategoriesIds.push(ctrl.entryChilds.rootCategories[i].id);
                }
            }

            if ($scope.resource === 'categories'){
                ctrl.entry.childCategoriesIds = new Array();
                for (i in ctrl.entryChilds.childCategories){
                    ctrl.entry.childCategoriesIds.push(ctrl.entryChilds.childCategories[i].id);
                }

                ctrl.entry.childProductsIds = new Array();
                for (i in ctrl.entryChilds.childProducts){
                    ctrl.entry.childProductsIds.push(ctrl.entryChilds.childProducts[i].id);
                }
            }

            if ($scope.resource === 'products'){
                ctrl.entry.discountsIds = new Array();
                for (i in ctrl.entryChilds.discounts){
                    ctrl.entry.discountsIds.push(ctrl.entryChilds.discounts[i].id);
                }

                ctrl.entry.childSKUsIds = new Array();
                for (i in ctrl.entryChilds.childSKUs){
                    ctrl.entry.childSKUsIds.push(ctrl.entryChilds.childSKUs[i].id);
                }
            }

            if ($scope.resource === 'skus'){
                ctrl.entry.discountsIds = new Array();
                for (i in ctrl.entryChilds.discounts){
                    ctrl.entry.discountsIds.push(ctrl.entryChilds.discounts[i].id);
                }
            }

            if (ctrl.isCreationModeActive){
                ctrl.create();
            }else if (ctrl.isEditionModeActive){
                ctrl.edit();
            }
        };


        this.create = function () {
            $http.post('rs/' + $scope.resource,ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                    ctrl.alerts.push({type: 'success', msg: 'Creation complete'})
                })
                .error(function (data) {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
            });
        };

        this.edit = function () {
            $http.put('rs/' + $scope.resource, ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                    ctrl.alerts.push({type: 'success', msg: 'Update complete'})
                })
                .error(function (data) {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
            });
        };

        this.convertEntryDates = function(){
            // hack for dates returned as timestamp by service
            ctrl.entry.startDate = ctrl.entry.startDate != null ? new Date(ctrl.entry.startDate) : null;
            ctrl.entry.endDate = ctrl.entry.endDate != null ? new Date(ctrl.entry.endDate) : null;
        };

        this.activateCreationMode = function () {
            ctrl.isCreationModeActive = true;
        }

        this.leaveEditView = function () {
            $scope.findEntries();
            ctrl.isEditionModeActive = false;
            ctrl.isCreationModeActive = false;
            ctrl.entry = {};
            ctrl.entryChilds = {};
            ctrl.alerts = [];
        };
    }]);

    app.controller('CatalogRelationshipsController', ['$http', '$scope', '$modal', '$log', function ($http, $scope, $modal, $log) {
        var ctrl = this;

        $scope.accordion = {
            open : false
        };

        $scope.$watch('accordion.open', function(isOpen){
            if (isOpen) {
              $http.get('rs/' + $scope.relationshipsParentResource + '/' + $scope.relationshipsParentId +'/'+$scope.resource)
                  .success(function (data) {
                  for (i in data){
                      $scope.relationshipsProperty.push(data[i]);
                  }

                  });
            }
          });

        $scope.open = function (size) {
            var modalInstance = $modal.open({
                templateUrl: 'relationshipsSelector.html',
                controller: ModalInstanceCtrl,
                scope:$scope,
                size: size
            });

            modalInstance.result.then(function (selectedItems) {
                for (i in selectedItems){
                    while(A.length > 0) {
                        $scope.relationshipsProperty.pop();
                    }
                    $scope.relationshipsProperty.push(
                    [i]);
                }
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        var ModalInstanceCtrl = function ($http,$scope, $modalInstance) {

            ctrl = this;
            $scope.results = [];
            $scope.selected = [];

            $scope.search = function(){
                var uri = 'rs/' + $scope.resource;
                if ($scope.searchValue != null && !($scope.searchValue ==="")){
                     uri = uri + '?search='+ $scope.searchValue;
                }
                $http.get(uri)
                    .success(function (data) {
                        $scope.results = data;
                    });
            };

            $scope.isAlreadyLinked = function(itemId){
                var isLinked = false;
                for (i in $scope.relationshipsProperty){
                    if ($scope.relationshipsProperty[i].id ===itemId){
                        isLinked = true;
                        break;
                    }
                }
                return isLinked;
            }

            $scope.ok = function () {
                var selectedItems = new Array();
                for (i in $scope.results){
                    if ($scope.selected[$scope.results[i].id] != null && $scope.selected[$scope.results[i].id] === true){
                        selectedItems.push($scope.results[i]);
                    }
                }
                $modalInstance.close(selectedItems);
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };
        };

    }]);

})();