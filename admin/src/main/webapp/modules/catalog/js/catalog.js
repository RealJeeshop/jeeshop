(function () {

    var app = angular.module('admin-catalog', []);

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
                    // hack for dates returned as timestamp by service
                    ctrl.entry.startDate = ctrl.entry.startDate != null ? new Date(ctrl.entry.startDate) : null;
                    ctrl.entry.endDate = ctrl.entry.endDate != null ? new Date(ctrl.entry.endDate) : null;
                });

            if ($scope.resource === 'catalogs'){
                $http.get('rs/' + $scope.resource + '/' + id+'/categories')
                    .success(function (data) {
                        ctrl.entryChilds.rootCategories = data;
                    });
            }

            if ($scope.resource === 'categories'){
                $http.get('rs/' + $scope.resource + '/' + id+'/categories')
                    .success(function (data) {
                        ctrl.entryChilds.childCategories = data;
                    });

                $http.get('rs/' + $scope.resource + '/' + id+'/products')
                    .success(function (data) {
                        ctrl.entryChilds.childProducts = data;
                    });
            }

            if ($scope.resource === 'products'){
                $http.get('rs/' + $scope.resource + '/' + id+'/skus')
                    .success(function (data) {
                        ctrl.entryChilds.childSKUs = data;
                    });

                $http.get('rs/' + $scope.resource + '/' + id+'/discounts')
                    .success(function (data) {
                        ctrl.entryChilds.discounts = data;
                    });
            }

            if ($scope.resource === 'skus'){
                $http.get('rs/' + $scope.resource + '/' + id+'/discounts')
                    .success(function (data) {
                        ctrl.entryChilds.discounts = data;
                    });
            }
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
                    ctrl.alerts.push({type: 'success', msg: 'Update complete'})
                })
                .error(function (data) {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
            });
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

    app.directive("commonCatalogFormFields", function () {
        return {
            restrict: "A",
            templateUrl: "modules/catalog/common-catalog-form-fields.html"
        };
    });

    app.directive("catalogRelationshipsForm", function () {
            return {
                restrict: "A",
                scope: {
                    relationshipsTitle: "@relationshipsTitle",
                    relationshipsProperty: "="
                },
                templateUrl: "modules/catalog/catalog-relationships-form.html"
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

})();