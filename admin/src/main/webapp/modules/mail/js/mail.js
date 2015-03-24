(function () {
    var app = angular.module('admin-mail', []);

    app.directive("mailtemplateForm", function () {
        return {
            restrict: "A",
            templateUrl: "modules/mail/mailtemplate-form.html"
        };
    });

    app.directive("mailEntries", function () {
        return {
            restrict: "A",
            templateUrl: "modules/mail/mail-entries.html"
        };
    });

    app.directive("mailOperations", function () {
        return {
            restrict: "A",
            templateUrl: "modules/mail/mail-operations.html"
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
        ctrl.orderBy = null;
        ctrl.orderDesc = false;

        ctrl.availableLocales = allLocales();

        ctrl.findEntries = function (orderBy) {
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

            if (orderBy != null) {
                ctrl.orderBy = orderBy;
                ctrl.orderDesc = !ctrl.orderDesc;
                uri += '&orderBy=' + orderBy + '&isDesc=' + ctrl.orderDesc;
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

    app.controller('MailTabController', ['$scope', function ($scope) {
        this.tabId = 1;

        this.selectTab = function (setId) {
            this.tabId = setId;
        };

        this.isSelected = function (checkId) {
            return this.tabId === checkId;
        };
    }]);

    app.controller('MailOperationController', ['$http', '$modal', function ($http) {

        var ctrl = this;
        ctrl.alerts = [];
        ctrl.isProcessing = false;
        ctrl.recipient = null;
        ctrl.mailTemplateName = null;
        ctrl.locale = null;
        ctrl.mailTemplateProperties = {};
        ctrl.availableLocales = allLocales();

        var testUser = {
            "id": 5,
            "login": ctrl.recipient,
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
            "actionToken": "33546011-2e68-4862-889e-a40dd9273866"
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

            $http.post(uri, ctrl.mailTemplateProperties).success(function (orders) {
                ctrl.isProcessing = false;
                ctrl.alerts.push({type: 'success', msg: 'Mail has been sent successfully'});
            }).error(function (data, status) {
                ctrl.isProcessing = false;
                if (status == 404) {
                    ctrl.alerts.push({
                        type: 'warning',
                        msg: 'No mail template found matching mail template and locale'
                    });
                } else {
                    ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                }
            });
        };
    }]);

})();