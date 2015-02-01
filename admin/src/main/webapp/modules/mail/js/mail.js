(function () {
    var app = angular.module('admin-mail', []);

    app.directive("mailtemplateForm", function () {
        return {
            restrict: "A",
            templateUrl: "modules/mail/mailtemplate-form.html"
        };
    });

    app.controller('MailTemplatesController', ['$http', '$modal', function ($http, $modal) {
        var ctrl = this;
        ctrl.alerts = [];
        ctrl.entries = [];
        ctrl.currentPage = 1;
        ctrl.totalCount = null;
        ctrl.pageSize = 10;
        ctrl.searchValue = null;
        ctrl.isProcessing = false;
        ctrl.entry = {};
        ctrl.entryChilds = {};
        ctrl.isEditionModeActive = false;
        ctrl.isCreationModeActive = false;

        ctrl.availableLocales = allLocales();

        ctrl.findEntries = function () {
            ctrl.isProcessing = true;
            ctrl.alerts = [];
            var offset = ctrl.pageSize * (ctrl.currentPage - 1);

            var uri = 'rs/mailtemplates';
            var countURI = 'rs/mailtemplates/count';

            if (ctrl.searchValue != null && !(ctrl.searchValue === "")) {
                uri = uri + '?name=' + ctrl.searchValue;
                $http.get(countURI).success(function (data) {
                    ctrl.totalCount = data;
                    ctrl.isProcessing = false;
                });
            } else {
                uri = uri + '?start=' + offset + '&size=' + ctrl.pageSize;
            }

            $http.get(uri).success(function (data) {
                ctrl.entries = data;
                ctrl.isProcessing = false;
            });
        };

        ctrl.findEntries();

        ctrl.pageChanged = function () {
            ctrl.findEntries();
        };

        ctrl.delete = function (index, message) {
            var modalInstance = $modal.open({
                templateUrl: 'util/confirm-dialog.html',
                controller: function ($modalInstance, $scope) {
                    $scope.modalInstance = $modalInstance;
                    $scope.confirmMessage = message;
                },
                size: 'sm'
            });
            modalInstance.result.then(function () {
                ctrl.alerts = [];
                $http.delete('rs/mailtemplates/' + ctrl.entries[index].id)
                    .success(function (data) {
                        ctrl.entries.splice(index, 1);
                        ctrl.findEntries();
                    })
                    .error(function (data) {
                        ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                    });

            }, function () {

            });
        };

        this.activateCreationMode = function () {
            ctrl.isCreationModeActive = true;
        };

        ctrl.selectEntry = function (id) {
            $http.get('rs/mailtemplates/' + id)
                .success(function (data) {
                    ctrl.isEditionModeActive = true;
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                });
        };


        ctrl.createOrEdit = function () {

            ctrl.alerts = [];

            if (ctrl.isCreationModeActive) {
                ctrl.create();
            } else if (ctrl.isEditionModeActive) {
                ctrl.edit();
            }
        };


        ctrl.create = function () {
            $http.post('rs/mailtemplates', ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.alerts.push({type: 'success', msg: 'Creation complete'});
                    ctrl.isCreationModeActive = false;
                    ctrl.isEditionModeActive = true;
                    ctrl.convertEntryDates();
                })
                .error(function (data, status) {
                    if (status == 409) {
                        ctrl.alerts.push({
                            type: 'danger',
                            msg: 'An e-mail template with same locale and name already exists'
                        })
                    } else {
                        ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                    }
                });
        };

        ctrl.edit = function () {
            $http.put('rs/mailtemplates', ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.alerts.push({type: 'success', msg: 'Update complete'});
                    ctrl.convertEntryDates();
                })
                .error(function (data, status) {
                    if (status == 409) {
                        ctrl.alerts.push({
                            type: 'danger',
                            msg: 'An e-mail template with same locale and name already exists'
                        })
                    } else {
                        ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                    }
                });
        };

        ctrl.leaveEditView = function () {
            ctrl.findEntries();
            ctrl.isEditionModeActive = false;
            ctrl.isCreationModeActive = false;
            ctrl.entry = {};
            ctrl.entryChilds = {};
            ctrl.alerts = [];
        };

        ctrl.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };

        ctrl.convertEntryDates = function () {
            // hack for dates returned as timestamp by service
            ctrl.entry.creationDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
            ctrl.entry.updateDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
        };

    }]);

})();