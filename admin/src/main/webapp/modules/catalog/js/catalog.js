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
        function link(scope, element, attrs) {
              attrs.$observe('relationshipsTitle', function(value) {    // xxx est le nom de l'attribut
                  scope.relationshipsTitle = value;
              });

              attrs.$observe('resource', function(value) {    // xxx est le nom de l'attribut
                    scope.relationshipsResource = value;
                });

                attrs.$observe('relationshipsProperty', function(value) {    // xxx est le nom de l'attribut
                    scope.relationshipsProperty = value;
                });


              /*scope.$watch(attrs.relationshipsProperty, function(value) {
                  scope.relationshipsProperty = value;
                });*/

            }

        return {
            restrict: "A",
            scope:true,
            templateUrl: "modules/catalog/catalog-relationships-accordion.html",
            link: link
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
                            ctrl.entries.splice(index,1);
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

        };

        this.createOrEdit = function(){

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
        $scope.itemsIds = [];
        $scope.items = [];

        $scope.accordion = {
            open : false
        };

        $scope.initRelationshipsIdsProperty = function(){
            $scope.itemsIds=[];
            for (i in $scope.items){
                $scope.itemsIds[i] = $scope.items[i].id;
            }
            $scope.catalogEntryCtrl.entry[$scope.relationshipsProperty] = $scope.itemsIds;
        };

        $scope.removeItem = function(index){
            $scope.items.splice(index,1);
            $scope.initRelationshipsIdsProperty();
        };

        $scope.$watch('catalogEntryCtrl.isEditionModeActive', function(){
            $scope.accordion.open = false;
          });

        $scope.$watch('accordion.open', function(isOpen){
            if (isOpen) {
                if ($scope.catalogEntryCtrl.entry.id == null){
                    return;
                }
                $http.get('rs/' + $scope.resource + '/' + $scope.catalogEntryCtrl.entry.id+'/'+$scope.relationshipsResource)
                    .success(function (data) {
                        $scope.items=data;
                        $scope.initRelationshipsIdsProperty();
                    });
            }
          });

        $scope.open = function (size) {
            var modalInstance = $modal.open({
                templateUrl: 'relationshipsSelector.html',
                controller: ModalInstanceCtrl,
                size: size,
                resolve: {
                    items: function () {
                        return $scope.items;
                    },
                    itemsIds: function () {
                        return $scope.itemsIds;
                    },
                    relationshipsResource: function () {
                        return $scope.relationshipsResource;
                    },
                }
            });

            modalInstance.result.then(function (selectedItems) {

                for (i in selectedItems){
                    $scope.items.push(selectedItems[i]);
                }
                $scope.initRelationshipsIdsProperty();
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        var ModalInstanceCtrl = function ($http,$scope, $modalInstance, items, relationshipsResource) {

            ctrl = this;
            $scope.items = items;
            $scope.relationshipsResource = relationshipsResource;
            $scope.results = [];
            $scope.selected = [];

            $scope.search = function(){
                var uri = 'rs/' + $scope.relationshipsResource;
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
                for (i in $scope.items){
                    if ($scope.items[i].id ===itemId){
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