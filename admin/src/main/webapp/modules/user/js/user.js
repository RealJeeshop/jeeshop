(function () {
    var app = angular.module('admin-user', []);

    app.directive("userForm", function () {
        return {
            restrict: "A",
            templateUrl: "modules/user/user-form.html"
        };
    });

    app.controller('UsersController', ['$http', '$modal', function ($http, $modal) {

        var ctrl = this;
        ctrl.alerts = [];
        ctrl.entries = [];
        ctrl.currentPage = 1;
        ctrl.totalCount = null;
        ctrl.pageSize = 10;
        ctrl.searchValue = null;
        ctrl.isProcessing = false;
        ctrl.orderBy = null;
        ctrl.orderDesc = false;

        ctrl.findEntries = function (orderBy) {
            ctrl.isProcessing = true;
            ctrl.alerts = [];
            var offset = ctrl.pageSize * (ctrl.currentPage - 1);

            var uri = 'rs/users?start=' + offset + '&size=' + ctrl.pageSize;
            if (orderBy != null) {
                ctrl.orderBy = orderBy;
                ctrl.orderDesc = !ctrl.orderDesc;
                uri += '&orderBy=' + orderBy + '&isDesc=' + ctrl.orderDesc;
            }
            var countURI = 'rs/users/count';
            if (ctrl.searchValue != null && !(ctrl.searchValue === "")) {
                var searchArg = 'search=' + ctrl.searchValue;
                uri = uri + '&' + searchArg;
                countURI = countURI + '?' + searchArg;
            }

            $http.get(uri).success(function (data) {
                ctrl.entries = data;
                ctrl.isProcessing = false;
            });

            $http.get(countURI).success(function (data) {
                ctrl.totalCount = data;
                ctrl.isProcessing = false;
            });

        };

        ctrl.findEntries();

        ctrl.pageChanged = function () {
            ctrl.findEntries();
        };

        ctrl.delete = function (index, message) {
            var modalInstance = $modal.open({
                templateUrl: 'util/confirm-delete-danger.html',
                controller: function ($modalInstance, $scope) {
                    $scope.modalInstance = $modalInstance;
                    $scope.confirmMessage = message;
                },
                size: 'sm'
            });
            modalInstance.result.then(function () {
                ctrl.alerts = [];
                $http.delete('rs/users/' + ctrl.entries[index].id)
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

        ctrl.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };
    }]);

    app.controller('UserController', ['$http', '$stateParams', function ($http, $stateParams) {

        var ctrl = this;

        ctrl.alerts = [];
        ctrl.entry = {};

        ctrl.isEditionMode = ($stateParams.userId != "");

        ctrl.findUser = function () {
            if ($stateParams.userId == "")
                return;
            $http.get('rs/users/' + $stateParams.userId)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                });
        };

        ctrl.createOrEdit = function () {
            if (ctrl.isEditionMode) {
                ctrl.edit();
            } else {
                ctrl.create();
            }
        };

        ctrl.create = function () {
            $http.post('rs/users', ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                    ctrl.alerts.push({type: 'success', msg: 'Creation complete'});
                })
                .error(function (data, status) {
                    if (status == 403)
                        ctrl.alerts.push({type: 'warning', msg: 'Operation not allowed'});
                    else
                        ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                });
        };

        ctrl.edit = function () {
            $http.put('rs/users', ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                    ctrl.alerts.push({type: 'success', msg: 'Update complete'})
                })
                .error(function (data, status) {
                    if (status == 403)
                        ctrl.alerts.push({type: 'warning', msg: 'Operation not allowed'});
                    else
                        ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                });
        };

        var modalInstanceCtrl = function ($modalInstance, $scope, login) {

            $scope.modalInstance = $modalInstance;

            $scope.submitForm = function () {

                var newPassword = $scope.newPassword;
                var confirmNewPassword = $scope.confirmNewPassword;

                if (newPassword === confirmNewPassword) {

                    var uri = 'rs/users/' + login + '/password';
                    $http.put(uri, newPassword)
                        .success(function () {
                            ctrl.isProcessing = false;
                            ctrl.alerts.push({type: 'success', msg: 'Password successfully updated'});
                        })
                        .error(function (data, status) {
                            if (status == 403)
                                ctrl.alerts.push({type: 'warning', msg: 'Operation not allowed'});
                            else
                                ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                        });

                    $scope.modalInstance.dismiss('close');

                } else {
                    $scope.nomatch = true;
                }
            };

            $scope.cancelForm = function () {
                $scope.modalInstance.dismiss('close');
            };
        };

        ctrl.convertEntryDates = function () {
            // hack for dates returned as timestamp by service
            ctrl.entry.birthDate = ctrl.entry.birthDate != null ? new Date(ctrl.entry.birthDate) : null;
            ctrl.entry.creationDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
            ctrl.entry.updateDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
        };

        ctrl.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };

        ctrl.findUser();

    }]);


})();
