(function () {
    var app = angular.module('admin-order', []);

    app.directive("orderForm", function () {
        return {
            restrict: "A",
            templateUrl: "modules/order/order-form.html"
        };
    });

    app.controller('OrdersController', ['$http', '$modal', function ($http, $modal) {
        var ctrl = this;
        ctrl.alerts = [];
        ctrl.entries = [];
        ctrl.currentPage = 1;
        ctrl.totalCount = null;
        ctrl.pageSize = 10;
        ctrl.searchValue = null;
        ctrl.isProcessing = false;
        ctrl.entry = {};
        ctrl.isEditionModeActive = false;
        ctrl.isCreationModeActive = false;
        ctrl.searchOnlyPaymentValidated = false;

        ctrl.skuPerId = [];
        ctrl.discountPerId = [];

        ctrl.getOrderItemsSKUs = function(){
            if (ctrl.entry.items == null){
                return;
            }

            for (i in ctrl.entry.items) {
                $http.get('rs/skus/' + ctrl.entry.items[i].skuId)
                    .success(function (data) {
                        ctrl.skuPerId[data.id] = data;
                    });

            }
        };

        ctrl.getOrderDiscounts = function(){
            if (ctrl.entry.discountIds == null){
                return;
            }

            for (i in ctrl.entry.discountIds) {
                $http.get('rs/discounts/' + ctrl.entry.discountIds[i])
                    .success(function (data) {
                        ctrl.discountPerId[data.id] = data;
                    });

            }
        };

        ctrl.findEntries = function (orderBy, isDesc) {
            ctrl.isProcessing = true;
            ctrl.alerts = [];
            var offset = ctrl.pageSize * (ctrl.currentPage - 1);

            var uri = 'rs/orders?start=' + offset + '&size=' + ctrl.pageSize;
            var countURI = 'rs/orders/count';
            if (ctrl.searchValue != null && !(ctrl.searchValue === "")) {
                var searchArg = '&search=' + ctrl.searchValue;
                uri = uri + searchArg;
                countURI = countURI + '?' + searchArg;
            }

            if (ctrl.searchOnlyPaymentValidated) {
                var searchArg = '&status=PAYMENT_VALIDATED';
                uri = uri + searchArg;
                countURI = countURI + '?' + searchArg;
            }

            if (orderBy != null && isDesc !=null){
                uri = uri + '&orderBy=' + orderBy+'&isDesc='+isDesc;
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
                $http.delete('rs/orders/' + ctrl.entries[index].id)
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
            ctrl.skuPerId = [];
            ctrl.discountPerId = [];
            $http.get('rs/orders/' + id)
                .success(function (data) {
                    ctrl.isEditionModeActive = true;
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                    ctrl.getOrderItemsSKUs();
                    ctrl.getOrderDiscounts();
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
            $http.post('rs/orders', ctrl.entry)
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
            $http.put('rs/orders', ctrl.entry)
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
            ctrl.entry.creationDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
            ctrl.entry.updateDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
            ctrl.entry.paymentDate = ctrl.entry.paymentDate != null ? new Date(ctrl.entry.paymentDate) : null;
            ctrl.entry.deliveryDate = ctrl.entry.deliveryDate != null ? new Date(ctrl.entry.deliveryDate) : null;

        };

        ctrl.leaveEditView = function () {
            ctrl.findEntries();
            ctrl.isEditionModeActive = false;
            ctrl.isCreationModeActive = false;
            ctrl.entry = {};
            ctrl.alerts = [];
        };

        ctrl.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };
    }]);

})();