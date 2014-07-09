(function () {

    var app = angular.module('admin-catalog', []);

    app.controller('TabController', function () {
        this.tabId = 1;

        this.selectTab = function (setId) {
            this.tabId = setId;
        };

        this.isSelected = function (checkId) {
            return this.tabId === checkId;
        };
    });

    app.controller('CatalogEntryController', ['$http', '$scope', function ($http, $scope) {
        var ctrl = this;

        ctrl.alerts = [];
        ctrl.isEditionModeActive = false;
        ctrl.entry = {};

        this.closeAlert = function(index) {
            ctrl.alerts.splice(index, 1);
        };

        this.selectEntry = function (id) {
            ctrl.isEditionModeActive = true;
            $http.get('rs/' + $scope.resource + '/' + id)
                .success(function (data) {
                    ctrl.entry = data;
                    // hack for dates returned as timestamp by service
                    ctrl.entry.startDate = ctrl.entry.startDate != null? new Date(ctrl.entry.startDate):null;
                    ctrl.entry.endDate = ctrl.entry.endDate != null? new Date(ctrl.entry.endDate):null;
                })
                .error(function (data) {
                });
        };

        this.editEntry = function () {
            $http.put('rs/' + $scope.resource, ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.alerts.push({type: 'success', msg: 'Update complete'})
                })
                .error(function (data) {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                });
        };

        this.leaveEditView = function () {
            ctrl.isEditionModeActive = false;
            ctrl.entry = {};
            ctrl.alerts = [];
        };
    }]);

    app.directive("commonCatalogEditFields", function () {
        return {
            restrict: "A",
            templateUrl: "modules/catalog/common-catalog-edit-fields.html"
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
                ctrl.entries = [];
                ctrl.resourceType = $scope.resource;

                $http.get('rs/' + $scope.resource).success(function (data) {
                    ctrl.entries = data;
                });
            },
            controllerAs: 'catalogEntriesCtrl',
            templateUrl: "modules/catalog/catalog-entries.html"
        };
    }]);

})();