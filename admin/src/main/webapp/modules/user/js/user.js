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
        ctrl.entry = {};
        ctrl.entryChilds = {};
        ctrl.isEditionModeActive = false;
        ctrl.isCreationModeActive = false;
        ctrl.orderBy = null;
        ctrl.orderDesc = false;

      ctrl.resetPassword = function (login) {

        $modal.open({
          templateUrl: 'util/reset-password-dialog.html',
          size: 'lg',
          controller: modalInstanceCtrl,
          resolve: {
            login: function () {
              return login;
            }
          }
        });
      };

      var modalInstanceCtrl = function ($modalInstance, $scope, login) {

        $scope.modalInstance = $modalInstance;

        $scope.submitForm = function () {

          var newPassword = $scope.newPassword;
          var confirmNewPassword = $scope.confirmNewPassword;

          if (newPassword === confirmNewPassword) {

            var uri = 'rs/users/' + login + '/' + newPassword;
            $http.put(uri).success(function (data) {
              ctrl.isProcessing = false;
            }).error(function (data) {
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

        ctrl.findEntries = function (orderBy) {
            ctrl.isProcessing = true;
            ctrl.alerts = [];
            var offset = ctrl.pageSize * (ctrl.currentPage - 1);

            var uri = 'rs/users?start=' + offset + '&size=' + ctrl.pageSize;
            if (orderBy != null){
                ctrl.orderBy = orderBy;
                ctrl.orderDesc = ! ctrl.orderDesc;
                uri += '&orderBy='+orderBy+'&isDesc='+ctrl.orderDesc;
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
                size: 'sm'});
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

        this.activateCreationMode = function () {
            ctrl.isCreationModeActive = true;
          ctrl.isEditionModeActive = false;
        };

        ctrl.selectEntry = function (id) {
            $http.get('rs/users/' + id)
                .success(function (data) {
                ctrl.isCreationModeActive = false;
                    ctrl.isEditionModeActive = true;
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                });
        };

        ctrl.createOrEdit = function () {

            if (ctrl.isCreationModeActive) {
                ctrl.create();
            } else if (ctrl.isEditionModeActive) {
                ctrl.edit();
            }
        };


        ctrl.create = function () {
            $http.post('rs/users', ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                    ctrl.alerts.push({type: 'success', msg: 'Creation complete'});
                    ctrl.isCreationModeActive = false;
                    ctrl.isEditionModeActive = true;
                })
                .error(function (data) {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                });
        };

        ctrl.edit = function () {
            $http.put('rs/users', ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                    ctrl.alerts.push({type: 'success', msg: 'Update complete'})
                })
                .error(function (data) {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                });
        };

        ctrl.convertEntryDates = function () {
            // hack for dates returned as timestamp by service
            ctrl.entry.birthDate = ctrl.entry.birthDate != null ? new Date(ctrl.entry.birthDate) : null;
            ctrl.entry.creationDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
            ctrl.entry.updateDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
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
    }]);

})();
