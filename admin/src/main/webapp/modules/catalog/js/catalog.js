(function () {

    var app = angular.module('admin-catalog', []);

    /*----- Directives -----*/

    app.directive("commonCatalogFormFields", function () {
        return {
            restrict: "A",
            templateUrl: "modules/catalog/common-catalog-form-fields.html"
        };
    });

    app.directive("presentationsAccordion", function () {
        return {
            restrict: "E",
            templateUrl: "modules/catalog/presentations-accordion.html"
        };
    });

    app.directive("featuresAccordion", function () {
        return {
            restrict: "E",
            templateUrl: "modules/catalog/pres-features-accordion.html"
        };
    });

    app.directive("catalogRelationshipsForm", function () {
        function link(scope, element, attrs) {
            attrs.$observe('relationshipsTitle', function (value) {    // xxx est le nom de l'attribut
                scope.relationshipsTitle = value;
            });

            attrs.$observe('resource', function (value) {    // xxx est le nom de l'attribut
                scope.relationshipsResource = value;
            });

            attrs.$observe('relationshipsProperty', function (value) {    // xxx est le nom de l'attribut
                scope.relationshipsProperty = value;
            });


            /*scope.$watch(attrs.relationshipsProperty, function(value) {
             scope.relationshipsProperty = value;
             });*/

        }

        return {
            restrict: "A",
            scope: true,
            templateUrl: "modules/catalog/relationships-accordion.html",
            link: link
        };
    });

    app.directive("getCatalogEntries", ['$http', '$modal', function ($http, $dialog, $scope) {
        return {
            restrict: "A",
            scope: {
                resource: "@resourceType"
            },
            controller: function ($http, $scope, $modal) {
                var ctrl = this;
                ctrl.alerts = [];
                ctrl.entries = [];
                ctrl.resourceType = $scope.resource;
                ctrl.currentPage = 1;
                ctrl.totalCount = null;
                ctrl.pageSize = 10;
                ctrl.searchValue = null;
                ctrl.isProcessing = false;
                ctrl.orderBy = null;
                ctrl.orderDesc = false;

                $scope.findEntries = function (orderBy) {
                    ctrl.isProcessing = true;
                    ctrl.alerts = [];
                    var offset = ctrl.pageSize * (ctrl.currentPage - 1);

                    var uri = 'rs/' + $scope.resource + "?start=" + offset + "&size=" + ctrl.pageSize;

                    if (orderBy != null){
                        ctrl.orderBy = orderBy;
                        ctrl.orderDesc = ! ctrl.orderDesc;
                        uri += '&orderBy='+orderBy+'&isDesc='+ctrl.orderDesc;
                    }

                    var countURI = 'rs/' + $scope.resource + '/count';
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

                $scope.findEntries();

                this.pageChanged = function () {
                    $scope.findEntries();
                };

                this.delete = function (index, message) {
                    var modalInstance = $modal.open({
                        templateUrl: 'util/confirm-dialog.html',
                        controller: function ($modalInstance, $scope) {
                            $scope.modalInstance = $modalInstance;
                            $scope.confirmMessage = message;
                        },
                        size: 'sm'});
                    modalInstance.result.then(function () {
                        ctrl.alerts = [];
                        $http.delete('rs/' + $scope.resource + "/" + ctrl.entries[index].id)
                            .success(function (data) {
                                ctrl.entries.splice(index, 1);
                                $scope.findEntries();
                            })
                            .error(function (data) {
                                ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                            });

                    }, function () {

                    });
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
        ctrl.entryChilds = {};

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

        this.createOrEdit = function () {

            if (ctrl.isCreationModeActive) {
                ctrl.create();
            } else if (ctrl.isEditionModeActive) {
                ctrl.edit();
            }
        };


        this.create = function () {
            $http.post('rs/' + $scope.resource, ctrl.entry)
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

        this.convertEntryDates = function () {
            // hack for dates returned as timestamp by service
            ctrl.entry.startDate = ctrl.entry.startDate != null ? new Date(ctrl.entry.startDate) : null;
            ctrl.entry.endDate = ctrl.entry.endDate != null ? new Date(ctrl.entry.endDate) : null;
        };

        this.activateCreationMode = function () {
            ctrl.alerts.push({type: 'info', msg: 'Save this item to access localized content (texts, images, ...) configuration'});
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
                $http.get('rs/' + $scope.resource + '/' + $scope.catalogEntryCtrl.entry.id + '/' + $scope.relationshipsResource)
                    .success(function (data) {
                        $scope.items = data;
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

        var ModalInstanceCtrl = function ($http, $scope, $modalInstance, items, relationshipsResource) {

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
            }

            $scope.ok = function () {
                var selectedItems = new Array();
                for (i in $scope.results) {
                    if ($scope.selected[$scope.results[i].id] != null && $scope.selected[$scope.results[i].id] === true) {
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

    app.controller('PresentationsController', ['$http', '$scope', '$modal', '$log', function ($http, $scope, $modal, $log) {

        var ctrl = this;

        var getAvailableLocales = function () {
            $http.get('rs/' + $scope.resource + '/' + $scope.catalogEntryCtrl.entry.id + '/presentationslocales')
                .success(function (data) {
                    $scope.locales = data;
                });
        };

        $scope.locales = [];
        $scope.locale = null; // set when edition mode

        $scope.accordion = {
            open: false
        };

        $scope.removeItem = function (index, message) {

            var modalInstance = $modal.open({
                templateUrl: 'util/confirm-dialog.html',
                controller: function ($modalInstance, $scope) {
                    $scope.modalInstance = $modalInstance;
                    $scope.confirmMessage = message;
                },
                size: 'sm'});
            modalInstance.result.then(function () {
                $scope.catalogEntryCtrl.alerts = [];
                $http.delete('rs/' + $scope.resource + '/' + $scope.catalogEntryCtrl.entry.id + '/presentations/' + $scope.locales[index])
                    .success(function (data) {
                        $scope.locales.splice(index, 1);
                    })
                    .error(function (data) {
                        $scope.catalogEntryCtrl.alerts.push({type: 'danger', msg: 'Technical error'});
                    });
            }, function () {

            });

        };

        $scope.isEditionMode = function () {
            return $scope.locale != null;
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
            var modalInstance = $modal.open({
                templateUrl: 'addOrEditPresentation.html',
                controller: ModalInstanceCtrl,
                size: size,
                resolve: {
                    locales: function () {
                        return $scope.locales;
                    },
                    locale: function () {
                        return $scope.locale;
                    },
                    presentationsResourceURI: function () {
                        return 'rs/' + $scope.resource + '/' + $scope.catalogEntryCtrl.entry.id + '/presentations';
                    },
                    resource: function () {
                        return $scope.resource;
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
                }
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        var ModalInstanceCtrl = function ($http, $scope, $modalInstance, locales, locale, presentationsResourceURI, $upload, entryId, resource) {

            ctrl = this;

            $scope.locales = locales;
            $scope.locale = locale; // set when edition mode
            $scope.selectedLocale = null;
            $scope.presentation = {};
            $scope.entryId = entryId;
            $scope.resource = resource;
            $scope.isProcessing = {
                thumbnail:false,
                largeImage:false,
                smallImage:false
            };


            $scope.feature={};

            $scope.addFeature = function(feature){
                $scope.presentation.features[feature.name] = feature.value;
                $scope.feature={};
            };

            $scope.removeFeature = function(name){
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


            $scope.isEditionMode = function () {
                return $scope.locale != null;
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

            $scope.removePresentationMedia = function (presentationPropertyName){
                $scope.presentation[presentationPropertyName] = null;
            };

            $scope.getPresentationMediaURI = function (presentationPropertyName){
                if ($scope.presentation[presentationPropertyName] != null){
                    return 'rs/medias/'+$scope.resource+'/'+$scope.entryId+'/'+$scope.selectedLocale+'/'
                        +$scope.presentation[presentationPropertyName].uri+'?refresh='+$scope.isProcessing;
                }
            };

            $scope.onFileSelect = function ($files, presentationPropertyName) {
                //$files: an array of files selected, each file has name, size, and type.
                //for (var i = 0; i < $files.length; i++) {
                $scope.isProcessing[presentationPropertyName] = true;
                var file = $files[0];

                var presentationMedia = {
                    uri: file.name
                };
                $scope.presentation[presentationPropertyName] = presentationMedia;

                $scope.upload = $upload.upload({
                    url: 'rs/medias/' + resource + '/' + entryId + '/' + $scope.selectedLocale + '/upload', //upload.php script, node.js route, or servlet url
                    method: 'POST',
                    //headers: {'header-key': 'header-value'},
                    withCredentials: true,
                    file: file // or list of files ($files) for html5 only
                    //fileName: 'doc.jpg' or ['1.jpg', '2.jpg', ...] // to modify the name of the file(s)
                    // customize file formData name ('Content-Disposition'), server side file variable name.
                    //fileFormDataName: myFile, //or a list of names for multiple files (html5). Default is 'file'
                    // customize how data is added to formData. See #40#issuecomment-28612000 for sample code
                    //formDataAppender: function(formData, key, val){}
                }).progress(function (evt) {

                }).success(function (data, status, headers, config) {
                    $scope.isProcessing[presentationPropertyName] = false;
                })
                .error(function (data, status, headers, config){
                    $scope.isProcessing[presentationPropertyName] = false;
                });
                //.then(success, error, progress);
                // access or attach event listeners to the underlying XMLHttpRequest.
                //.xhr(function(xhr){xhr.upload.addEventListener(...)})
                //}
                /* alternative way of uploading, send the file binary with the file's content-type.
                 Could be used to upload files to CouchDB, imgur, etc... html5 FileReader is needed.
                 It could also be used to monitor the progress of a normal http post/put request with large data*/
                //$scope.upload = $upload.http({...})  see 88#issuecomment-31366487 for sample code.
            };

            $scope.save = function () {
                var errors = new Array();
                $http.put(presentationsResourceURI + "/" + locale, $scope.presentation)
                    .success(function (data) {
                        $scope.presentation = data;
                        $modalInstance.close(errors);
                    })
                    .error(function (data) {
                        errors.push({type: 'danger', msg: 'Technical error'});
                        $modalInstance.close(errors);
                    });
            };

            $scope.add = function () {
                $scope.presentation.locale = $scope.selectedLocale;
               $http.post(presentationsResourceURI + "/" + $scope.selectedLocale, $scope.presentation)
                    .success(function (data) {
                        $scope.presentation = data;
                        getAvailableLocales();
                        $modalInstance.close();
                    })
                    .error(function (data) {
                        $modalInstance.close({type: 'danger', msg: 'Technical error'});
                    });
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };

            $scope.availableLocales = allLocales();
        };

    }]);

})();