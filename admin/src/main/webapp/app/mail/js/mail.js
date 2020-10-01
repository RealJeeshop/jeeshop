(function () {
    var app = angular.module('admin-mail', []);

    app.controller('MailTemplatesController', ['$http', '$uibModal', function ($http, $uibModal) {
        var ctrl = this;

        ctrl.entries = [];
        ctrl.alerts = [];
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

            var uri = 'rs/mailtemplates';

            var countURI = 'rs/mailtemplates/count';

            if (ctrl.searchValue != null && !(ctrl.searchValue === "")) {
                uri = uri + '?name=' + ctrl.searchValue;
                $http.get(countURI).then(function (data) {
                    ctrl.totalCount = data;
                    ctrl.isProcessing = false;
                });
            } else {
                uri = uri + '?start=' + offset + '&size=' + ctrl.pageSize;
            }

            if (orderBy != null) {
                ctrl.orderBy = orderBy;
                ctrl.orderDesc = !ctrl.orderDesc;
                uri += '&orderBy=' + orderBy + '&isDesc=' + ctrl.orderDesc;
            }

            $http.get(uri).then(function (data) {
                ctrl.entries = data;
                ctrl.isProcessing = false;
            });
        };

        ctrl.findEntries();

        ctrl.pageChanged = function () {
            ctrl.findEntries();
        };

        ctrl.delete = function (index, message) {
            var modalInstance = $uibModal.open({
                templateUrl: 'app/util/confirm-dialog.html',
                controller: ['$uibModalInstance','$scope', function ($uibModalInstance, $scope) {
                    $scope.modalInstance = $uibModalInstance;
                    $scope.confirmMessage = message;
                }],
                size: 'sm'
            });
            modalInstance.result.then(function () {
                ctrl.alerts = [];
                $http.delete('rs/mailtemplates/' + ctrl.entries[index].id)
                    .then(function (data) {
                        ctrl.entries.splice(index, 1);
                        ctrl.findEntries();
                    })
                    .catch(function (data) {
                        ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                    });

            }, function () {

            });
        };

        ctrl.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };

    }]);

    app.controller('MailTemplateController', ['$http', '$stateParams', '$state', 'LocalesService', function ($http, $stateParams, $state, LocalesService) {

        var ctrl = this;

        ctrl.availableLocales = LocalesService.allLocales();
        ctrl.entry = {};
        ctrl.entryChilds = {};
        ctrl.isEditionMode = ($stateParams.mailId != "");
        ctrl.alerts = [];

        ctrl.findMailTemplate = function () {
            if (!ctrl.isEditionMode)
                return;
            $http.get('rs/mailtemplates/' + $stateParams.mailId)
                .then(function (data) {
                    ctrl.entry = data;
                    ctrl.convertEntryDates();
                });
        };

        ctrl.createOrEdit = function () {
            ctrl.alerts = [];
            if (ctrl.isEditionMode) {
                ctrl.edit();
            } else {
                ctrl.create();
            }
        };

        ctrl.create = function () {
            $http.post('rs/mailtemplates', ctrl.entry)
                .then(function (data) {
                    ctrl.entry = data;
                    ctrl.alerts.push({type: 'success', msg: 'Creation complete'});
                    ctrl.convertEntryDates();
                })
                .catch(function (data, status) {
                    if (status == 409) {
                        ctrl.alerts.push({
                            type: 'danger',
                            msg: 'An e-mail template with same locale and name already exists'
                        })
                    } else if (status == 403) {
                        ctrl.alerts.push({type: 'warning', msg: 'Operation not allowed'})
                    } else {
                        ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                    }
                });
        };

        ctrl.edit = function () {
            $http.put('rs/mailtemplates', ctrl.entry)
                .then(function (data) {
                    ctrl.entry = data;
                    ctrl.alerts.push({type: 'success', msg: 'Update complete'});
                    ctrl.convertEntryDates();
                })
                .catch(function (data, status) {
                    if (status == 409) {
                        ctrl.alerts.push({
                            type: 'danger',
                            msg: 'An e-mail template with same locale and name already exists'
                        });
                    } else if (status == 403) {
                        ctrl.alerts.push({type: 'warning', msg: 'Operation not allowed'});
                    } else {
                        ctrl.alerts.push({type: 'danger', msg: 'Technical error'})
                    }
                });
        };

        ctrl.convertEntryDates = function () {
            // hack for dates returned as timestamp by service
            ctrl.entry.creationDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
            ctrl.entry.updateDate = ctrl.entry.creationDate != null ? new Date(ctrl.entry.creationDate) : null;
        };

        ctrl.exitDetailView = function () {
            $state.go('^', {}, {reload: true});
        };

        ctrl.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };

        ctrl.findMailTemplate();

    }]);

    app.controller('MailOperationController', ['$http', 'LocalesService', function ($http, LocalesService) {

        var ctrl = this;
        ctrl.alerts = [];
        ctrl.isProcessing = false;
        ctrl.recipient = null;
        ctrl.mailTemplateName = null;
        ctrl.locale = null;
        ctrl.mailTemplateProperties = {};
        ctrl.availableLocales = LocalesService.allLocales();

        var testUser = {
            "id": 5,
            "login": "fakelogin@test.com",
            "password": "Fake password",
            "firstname": "John",
            "lastname": "Doe",
            "gender": "M.",
            "phoneNumber": "0101010101",
            "birthDate": "1982-10-15T23:00:00.000Z",
            "age": 33,
            "creationDate": "2015-03-09T21:06:02.000Z",
            "updateDate": "2015-03-09T21:06:02.000Z",
            "disabled": false,
            "activated": true,
            "preferredLocale": "fr",
            "newslettersSubscribed": true,
            "actionToken": "01010101-2e01-0101-889e-a40dd9010101"
        };

        var testAdress = {
            "id": 14,
            "street": "125 rue de la paix",
            "city": "Paris",
            "zipCode": "75001",
            "firstname": "John",
            "lastname": "Doe",
            "gender": "M.",
            "company": "Fake company",
            "countryIso3Code": "FRA"
        };

        var testOrder = {
            "id": 7,
            "items": [{
                "id": 1,
                "productId": 1,
                "skuId": 1,
                "quantity": 1,
                "price": 10,
                "displayName": "Fake product",
                "skuReference": "1234",
                "presentationImageURI": "http://localhost/fakeimage.jpg"
            }],
            "orderDiscounts": [],
            "status": "PAYMENT_VALIDATED",
            "creationDate": "2015-02-23T01:54:10.000Z",
            "updateDate": "2015-02-23T01:54:10.000Z",
            "price": 22,
            "transactionId": "450344",
            "parcelTrackingKey": "Fake parcel tracking ID",
            "deliveryDate": null,
            "paymentDate": "2015-02-22T23:00:00.000Z",
            "deliveryFee": 12.0,
            "vat": 20.0,
            "reference": "02232015-7-450344"
        };

        testOrder["billingAddress"] = testAdress;
        testOrder["deliveryAddress"] = testAdress;
        testOrder["user"] = testUser;

        ctrl.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };

        ctrl.sendMail = function () {
            ctrl.isProcessing = true;
            ctrl.alerts = [];

            if (ctrl.mailTemplateName == "userRegistration" || ctrl.mailTemplateName == 'userActivation' ||
                ctrl.mailTemplateName == 'userResetPassword') {
                ctrl.mailTemplateProperties = testUser;

            } else if (ctrl.mailTemplateName == "orderAccepted" || ctrl.mailTemplateName == 'orderValidated' ||
                ctrl.mailTemplateName == 'orderShipped') {
                ctrl.mailTemplateProperties = testOrder;
            }

            var uri = 'rs/mailtemplates/test/' + ctrl.recipient + '?templateName=' + ctrl.mailTemplateName + '&locale=' + ctrl.locale;

            $http.post(uri, ctrl.mailTemplateProperties).then(function (orders) {
                ctrl.isProcessing = false;
                ctrl.alerts.push({type: 'success', msg: 'Mail has been sent successfully'});
            }).catch(function (data, status) {
                ctrl.isProcessing = false;
                if (status == 404) {
                    ctrl.alerts.push({
                        type: 'warning',
                        msg: 'No mail template found matching mail template and locale'
                    });
                }
                if (status == 403) {
                    ctrl.alerts.push({type: 'warning', msg: 'Operation not allowed'});
                } else {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                }
            });
        };
    }]);

})();