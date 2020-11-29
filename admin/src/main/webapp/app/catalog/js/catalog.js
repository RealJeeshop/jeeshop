(function () {

    var app = angular.module('admin-catalog', []);

    app.directive("commonCatalogFormFields", function () {
        return {
            restrict: "A",
            templateUrl: "app/catalog/common-catalog-form-fields.html"
        };
    });

    app.directive("presentationsAccordion", function () {
        return {
            restrict: "E",
            templateUrl: "app/catalog/presentations-accordion.html"
        };
    });

    app.directive("featuresAccordion", function () {
        return {
            restrict: "E",
            templateUrl: "app/catalog/pres-features-accordion.html"
        };
    });

    app.directive("catalogRelationshipsForm", function () {
        function link(scope, element, attrs) {
            attrs.$observe('relationshipsTitle', function (value) {
                scope.relationshipsTitle = value;
            });

            attrs.$observe('resource', function (value) {
                scope.relationshipsResource = value;
            });

            attrs.$observe('relationshipsProperty', function (value) {
                scope.relationshipsProperty = value;
            });

        }

        return {
            restrict: "A",
            scope: true,
            templateUrl: "app/catalog/relationships-accordion.html",
            link: link
        };
    });

    app.controller("CatalogEntriesController", ['$http', '$uibModal', '$scope', '$state', '$stateParams',
        function ($http, $uibModal, $scope, $state, $stateParams) {

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

            ctrl.isDetailState = function () {
                return $state.includes('detail');
            };

            ctrl.findEntries = function (orderBy) {
                ctrl.isProcessing = true;
                ctrl.alerts = [];
                var offset = ctrl.pageSize * (ctrl.currentPage - 1);

                var uri = 'rs/' + $stateParams.resource + "?start=" + offset + "&size=" + ctrl.pageSize;

                if (orderBy != null) {
                    ctrl.orderBy = orderBy;
                    ctrl.orderDesc = !ctrl.orderDesc;
                    uri += '&orderBy=' + orderBy + '&isDesc=' + ctrl.orderDesc;
                }

                var countURI = 'rs/' + $stateParams.resource + '/count';
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
                var modalInstance = $uibModal.open({
                    templateUrl: 'app/util/confirm-dialog.html',
                    controller: ['$uibModalInstance', '$scope', function ($uibModalInstance, $scope) {
                        $scope.modalInstance = $uibModalInstance;
                        $scope.confirmMessage = message;
                    }],
                    size: 'sm'
                });
                modalInstance.result.then(function () {
                    ctrl.alerts = [];
                    $http.delete('rs/' + $stateParams.resource + "/" + ctrl.entries[index].id)
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
        }]);

    app.controller('CatalogEntryController', ['$http', '$scope', '$stateParams', '$state',
        function ($http, $scope, $stateParams, $state) {
            var ctrl = this;

            ctrl.alerts = [];
            ctrl.entry = {};
            ctrl.entryChilds = {};

            $scope.isEditionMode = ($stateParams.itemId != "");

            ctrl.closeAlert = function (index) {
                ctrl.alerts.splice(index, 1);
            };

            ctrl.createOrEdit = function () {

                if ($scope.isEditionMode) {
                    ctrl.edit();
                } else {
                    ctrl.create();
                }
            };


            ctrl.create = function () {
                $http.post('rs/' + $stateParams.resource, ctrl.entry)
                    .success(function (data) {
                        ctrl.entry = data;
                        ctrl.convertEntryDates();
                        ctrl.alerts.push({type: 'success', msg: 'Creation complete'})
                    })
                    .error(function (data, status) {
                        if (status == 403)
                            ctrl.alerts.push({type: 'warning', msg: 'Operation not allowed'});
                        else
                            ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                    });
            };

            ctrl.edit = function () {
                var updatedResource  = ctrl.entry
                delete updatedResource.rootCategories
                delete updatedResource.presentationByLocale
                $http.put('rs/' + $stateParams.resource, ctrl.entry)
                    .success(function (data) {
                        ctrl.entry = data;
                        ctrl.convertEntryDates();
                        ctrl.alerts.push({type: 'success', msg: 'Update complete'})
                    })
                    .error(function (data, status) {
                        if (status == 403)
                            ctrl.alerts.push({type: 'warning', msg: 'Operation not allowed'});
                        else
                            ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                    });
            };

            ctrl.convertEntryDates = function () {
                // hack for dates returned as timestamp by service
                ctrl.entry.startDate = ctrl.entry.startDate != null ? new Date(ctrl.entry.startDate) : null;
                ctrl.entry.endDate = ctrl.entry.endDate != null ? new Date(ctrl.entry.endDate) : null;
            };

            ctrl.exitDetailView = function () {
                $state.go('^', {}, {reload: true});
            };

            if ($scope.isEditionMode) {
                $http.get('rs/' + $stateParams.resource + '/' + $stateParams.itemId)
                    .success(function (data) {
                        ctrl.entry = data;
                        ctrl.convertEntryDates();
                    });
            } else {
                ctrl.alerts.push({
                    type: 'info',
                    msg: 'Save this item to access localized content (texts, images, ...) configuration'
                });
            }


        }]);

    app.controller('CatalogRelationshipsController', ['$http', '$scope', '$uibModal', '$log', '$stateParams',
    function ($http, $scope, $uibModal, $log, $stateParams) {
        var ctrl = this;
        $scope.itemsIds = [];
        $scope.items = [];

        $scope.accordion = {
            open: false
        };

        $scope.initRelationshipsIdsProperty = function () {
            $scope.itemsIds = [];
            for (i in $scope.items) {
                $scope.itemsIds[i] = $scope.items[i].id;
            }
            $scope.catalogEntryCtrl.entry[$scope.relationshipsProperty] = $scope.itemsIds;
        };

        $scope.removeItem = function (index) {
            $scope.items.splice(index, 1);
            $scope.initRelationshipsIdsProperty();
        };

        $scope.$watch('catalogEntryCtrl.entry', function () {
            $scope.accordion.open = false;
        });

        $scope.$watch('accordion.open', function (isOpen) {
            if (isOpen) {
                if ($scope.catalogEntryCtrl.entry.id == null) {
                    return;
                }
                $http.get('rs/' + $stateParams.resource + '/' + $stateParams.itemId + '/' + $scope.relationshipsResource)
                    .success(function (data) {
                        $scope.items = data;
                        $scope.initRelationshipsIdsProperty();
                    });
            }
        });

        $scope.open = function (size) {
            var modalInstance = $uibModal.open({
                templateUrl: 'relationshipsSelector.html',
                controller: 'CatalogRelationshipsModalController',
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
                    }
                }
            });

            modalInstance.result.then(function (selectedItems) {

                for (i in selectedItems) {
                    $scope.items.push(selectedItems[i]);
                }
                $scope.initRelationshipsIdsProperty();
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

    }]);

    app.controller('CatalogRelationshipsModalController',

        ['$http', '$scope', '$uibModalInstance', '$stateParams', 'items', 'relationshipsResource',
            function ($http, $scope, $uibModalInstance, $stateParams, items, relationshipsResource) {

                ctrl = this;
                $scope.items = items;
                $scope.relationshipsResource = relationshipsResource;
                $scope.results = [];
                $scope.selected = [];
                $scope.currentPage = 1;
                $scope.totalCount = 0;
                $scope.pageSize = 10;

                $scope.search = function () { // TODO merge with catalogEntriesCtrl listing and add search to it
                    var offset = $scope.pageSize * ($scope.currentPage - 1);
                    var uri = 'rs/' + $scope.relationshipsResource + "?start=" + offset + "&size=" + $scope.pageSize;
                    var countURI = 'rs/' + $scope.relationshipsResource + '/count';
                    if ($scope.searchValue != null && !($scope.searchValue === "")) {
                        var searchArg = 'search=' + $scope.searchValue;
                        uri = uri + '&' + searchArg;
                        countURI = countURI + '?' + searchArg;
                    }

                    $http.get(uri).success(function (data) {
                        $scope.results = data;
                    });

                    $http.get(countURI).success(function (data) {
                        $scope.totalCount = data;
                    });

                };

                $scope.pageChanged = function () {
                    $scope.search();
                };

                $scope.isAlreadyLinked = function (itemId) {
                    var isLinked = false;
                    for (i in $scope.items) {
                        if ($scope.items[i].id === itemId) {
                            isLinked = true;
                            break;
                        }
                    }
                    return isLinked;
                };

                $scope.ok = function () {
                    var selectedItems = [];
                    for (i in $scope.results) {
                        if ($scope.selected[$scope.results[i].id] != null && $scope.selected[$scope.results[i].id] === true) {
                            selectedItems.push($scope.results[i]);
                        }
                    }
                    $uibModalInstance.close(selectedItems);
                };

                $scope.cancel = function () {
                    $uibModalInstance.dismiss('cancel');
                };

            }]);

    app.controller('PresentationsController', ['$http', '$scope', '$uibModal', '$log', '$stateParams', 'LocalesService',
    function ($http, $scope, $uibModal, $log, $stateParams, LocalesService) {

        var ctrl = this;

        var getAvailableLocales = function () {
            $http.get('rs/' + $stateParams.resource + '/' + $stateParams.itemId + '/presentationslocales')
                .success(function (data) {
                    $scope.locales = data;
                });
        };

        $scope.isEditionMode = ($stateParams.itemId != "");
        $scope.itemId = $stateParams.itemId;
        $scope.resource = $stateParams.resource;

        $scope.locales = [];
        $scope.locale = null; // edition mode

        $scope.accordion = {
            open: false
        };

        $scope.removeItem = function (index, message) {

            var modalInstance = $uibModal.open({
                templateUrl: 'app/util/confirm-dialog.html',
                controller: ['$uibModalInstance', '$scope', function ($uibModalInstance, $scope) {
                    $scope.modalInstance = $uibModalInstance;
                    $scope.confirmMessage = message;
                }],
                size: 'sm'
            });
            modalInstance.result.then(function () {
                $scope.catalogEntryCtrl.alerts = [];
                $http.delete('rs/' + $scope.resource + '/' + $scope.itemId + '/presentations/' + $scope.locales[index])
                    .success(function (data) {
                        $scope.locales.splice(index, 1);
                        $scope.catalogEntryCtrl.alerts.push({type: 'success', msg: 'Presentations update complete'});
                    })
                    .error(function (data) {
                        $scope.catalogEntryCtrl.alerts.push({type: 'danger', msg: 'Technical error'});
                    });
            }, function () {

            });

        };

        $scope.$watch('catalogEntryCtrl.entry', function () {
            $scope.accordion.open = false;
        });

        $scope.$watch('accordion.open', function (isOpen) {
            if (isOpen) {
                if ($scope.catalogEntryCtrl.entry.id == null) {
                    $scope.locales = [];
                }
                getAvailableLocales();
            }
        });

        $scope.addOrEditPresentation = function (size, locale) {
            $scope.locale = locale;
            var modalInstance = $uibModal.open({
                templateUrl: 'addOrEditPresentation.html',
                controller: 'PresentationsModalController',
                size: size,
                resolve: {
                    locales: function () {
                        return $scope.locales;
                    },
                    locale: function () {
                        return $scope.locale;
                    },
                    presentationsResourceURI: function () {
                        return 'rs/' + $stateParams.resource + '/' + $scope.catalogEntryCtrl.entry.id + '/presentations';
                    },
                    entryId: function () {
                        return $scope.catalogEntryCtrl.entry.id;
                    }
                }
            });

            modalInstance.result.then(function (errors) {
                $scope.catalogEntryCtrl.alerts = [];
                if (errors != null && errors.length > 0) {
                    $scope.catalogEntryCtrl.alerts.push(errors[0]);
                    getAvailableLocales();
                }

            }, function () {

            });
        };


    }]);

    app.controller('PresentationsModalController',

        ['$http', '$scope', '$uibModalInstance', '$stateParams', '$upload', 'LocalesService', 'locales', 'locale', 'presentationsResourceURI', 'entryId',
            function ($http, $scope, $uibModalInstance, $stateParams, $upload, LocalesService, locales, locale, presentationsResourceURI, entryId) {

                ctrl = this;

                $scope.locales = locales;
                $scope.locale = locale; // set when edition mode
                $scope.isEditionMode = ($scope.locale != null);
                $scope.selectedLocale = null;
                $scope.presentation = {};
                $scope.entryId = entryId;
                $scope.isProcessing = {
                    thumbnail: false,
                    largeImage: false,
                    smallImage: false
                };

                $scope.feature = {};

                $scope.isNewLocaleSelected = function () {
                    return $scope.presentation.id == null
                };

                $scope.addFeature = function (feature) {
                    $scope.presentation.features[feature.name] = feature.value;
                    $scope.feature = {};
                };

                $scope.removeFeature = function (name) {
                    delete $scope.presentation.features[name];
                };

                var getPresentationByLocale = function (locale) {
                    $scope.selectedLocale = locale;
                    $http.get(presentationsResourceURI + "/" + locale)
                        .success(function (data) {
                            $scope.presentation = data;
                            $scope.locale = locale;
                        })
                        .error(function (data) {
                            $scope.presentation = {};
                            $scope.locale = null;
                        });
                };

                if (locale != null) {
                    getPresentationByLocale(locale);
                }

                $scope.isLocaleSelected = function () {
                    return $scope.selectedLocale != null;
                };

                $scope.selectLocale = function () {
                    getPresentationByLocale($scope.selectedLocale);
                };

                $scope.removePresentationMedia = function (presentationPropertyName) {
                    $scope.presentation[presentationPropertyName] = null;
                };

                $scope.getPresentationMediaURI = function (presentationPropertyName) {
                    if ($scope.presentation[presentationPropertyName] != null) {
                        return 'rs/medias/' + $stateParams.resource + '/' + $scope.entryId + '/' + $scope.selectedLocale + '/'
                            + $scope.presentation[presentationPropertyName].uri + '?refresh=' + $scope.isProcessing[presentationPropertyName];
                    }
                };

                $scope.onFileSelect = function ($files, presentationPropertyName) {

                    $scope.isProcessing[presentationPropertyName] = true;
                    var file = $files[0];

                    var presentationMedia = {
                        uri: file.name
                    };
                    $scope.presentation[presentationPropertyName] = presentationMedia;

                    $scope.upload = $upload.upload({
                        url: 'rs/medias/' + $stateParams.resource + '/' + $scope.entryId + '/' + $scope.selectedLocale + '/upload',
                        method: 'POST',
                        //headers: {'header-key': 'header-value'},
                        withCredentials: true,
                        file: file
                    }).progress(function (evt) {

                    }).success(function (data, status, headers, config) {
                        $scope.isProcessing[presentationPropertyName] = false;
                    })
                        .error(function (data, status, headers, config) {
                            $scope.isProcessing[presentationPropertyName] = false;
                        });
                };

                $scope.save = function () {
                    var messages = [];
                    $http.put(presentationsResourceURI + "/" + locale, $scope.presentation)
                        .success(function (data) {
                            $scope.presentation = data;
                            messages.push({type: 'success', msg: 'Presentations update complete'});
                            $uibModalInstance.close(messages);
                        })
                        .error(function (data, status) {
                            if (status == 403)
                                messages.push({type: 'warning', msg: 'Operation not allowed'});
                            else
                                messages.push({type: 'danger', msg: 'Technical error'});
                            $uibModalInstance.close(messages);
                        });
                };

                $scope.add = function () {
                    var messages = [];
                    $scope.presentation.locale = $scope.selectedLocale;
                    $http.post(presentationsResourceURI + "/" + $scope.selectedLocale, $scope.presentation)
                        .success(function (data) {
                            $scope.presentation = data;
                            messages.push({type: 'success', msg: 'Presentation creation complete'});
                            $uibModalInstance.close(messages);

                        })
                        .error(function (data, status) {
                            if (status == 403)
                                messages.push({type: 'warning', msg: 'Operation now allowed'});
                            else
                                messages.push({type: 'danger', msg: 'Technical error'});

                            $uibModalInstance.close(messages);

                        });
                };

                $scope.cancel = function () {
                    $uibModalInstance.dismiss('cancel');
                };

                $scope.availableLocales = LocalesService.allLocales();

            }]);

})();