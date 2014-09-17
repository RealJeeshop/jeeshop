(function () {
    var app = angular.module('admin-newsletter', []);

    app.directive("newsletterForm", function () {
        return {
            restrict: "A",
            templateUrl: "modules/newsletter/newsletter-form.html"
        };
    });

    app.controller('NewslettersController', ['$http', '$modal', function ($http, $modal) {
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

        ctrl.findEntries = function () {
            ctrl.isProcessing = true;
            ctrl.alerts = [];
            var offset = ctrl.pageSize * (ctrl.currentPage - 1);

            var uri = 'rs/newsletters';
            var countURI = 'rs/newsletters/count';

            if (ctrl.searchValue != null && !(ctrl.searchValue === "")) {
                uri = uri + '?name=' + ctrl.searchValue;
                $http.get(countURI).success(function (data) {
                    ctrl.totalCount = data;
                    ctrl.isProcessing = false;
                });
            }else{
                uri = uri+'?start=' + offset + '&size=' + ctrl.pageSize;
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
                size: 'sm'});
            modalInstance.result.then(function () {
                ctrl.alerts = [];
                $http.delete('rs/newsletters/' + ctrl.entries[index].id)
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
        }

        ctrl.selectEntry = function (id) {
            $http.get('rs/newsletters/' + id)
                .success(function (data) {
                    ctrl.isEditionModeActive = true;
                    ctrl.entry = data;
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
            $http.post('rs/newsletters', ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.alerts.push({type: 'success', msg: 'Creation complete'})
                })
                .error(function (data) {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                });
        };

        ctrl.edit = function () {
            $http.put('rs/newsletters', ctrl.entry)
                .success(function (data) {
                    ctrl.entry = data;
                    ctrl.alerts.push({type: 'success', msg: 'Update complete'})
                })
                .error(function (data) {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
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

        //setup editor options
         ctrl.editorOptions = {
         height:'6em',
         toolbar:null,
         toolbar_full:null
         /*toolbarGroups:[
         { name: 'basicstyles', groups: [ 'basicstyles' ] },
         { name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align' ] },
         { name: 'links' },
         { name: 'tools' },
         { name: 'insert' },
         { name: 'styles' },
         { name: 'colors' },
         { name: 'document', groups: [ 'mode'] }]
          */
         };
    }]);

})();