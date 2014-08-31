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
                ctrl.isProcessing = true;

                $scope.findEntries = function () {
                    ctrl.isProcessing = true;
                    ctrl.alerts = [];
                    var offset = ctrl.pageSize * (ctrl.currentPage - 1);

                    var uri = 'rs/' + $scope.resource + "?start=" + offset + "&size=" + ctrl.pageSize;
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
                            })
                            .error(function (data) {
                                ctrl.alerts.push({type: 'danger', msg: 'Technical error'});
                            });
                        $scope.findEntries();
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
                if (errors.length > 0) {
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
                    return 'rs/medias/'+$scope.resource+'/'+$scope.entryId+'/'+$scope.locale+'/'
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
                    url: 'rs/medias/' + resource + '/' + entryId + '/' + $scope.locale + '/upload', //upload.php script, node.js route, or servlet url
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

            $scope.availableLocales = [
                {displayName: "Albanian (Albania)", name: "sq_AL"},
                {displayName: "Albanian", name: "sq"},
                {displayName: "Arabic (Algeria)", name: "ar_DZ"},
                {displayName: "Arabic (Bahrain)", name: "ar_BH"},
                {displayName: "Arabic (Egypt)", name: "ar_EG"},
                {displayName: "Arabic (Iraq)", name: "ar_IQ"},
                {displayName: "Arabic (Jordan)", name: "ar_JO"},
                {displayName: "Arabic (Kuwait)", name: "ar_KW"},
                {displayName: "Arabic (Lebanon)", name: "ar_LB"},
                {displayName: "Arabic (Libya)", name: "ar_LY"},
                {displayName: "Arabic (Morocco)", name: "ar_MA"},
                {displayName: "Arabic (Oman)", name: "ar_OM"},
                {displayName: "Arabic (Qatar)", name: "ar_QA"},
                {displayName: "Arabic (Saudi Arabia)", name: "ar_SA"},
                {displayName: "Arabic (Sudan)", name: "ar_SD"},
                {displayName: "Arabic (Syria)", name: "ar_SY"},
                {displayName: "Arabic (Tunisia)", name: "ar_TN"},
                {displayName: "Arabic (United Arab Emirates)", name: "ar_AE"},
                {displayName: "Arabic (Yemen)", name: "ar_YE"},
                {displayName: "Arabic", name: "ar"},
                {displayName: "Belarusian (Belarus)", name: "be_BY"},
                {displayName: "Belarusian", name: "be"},
                {displayName: "Bulgarian (Bulgaria)", name: "bg_BG"},
                {displayName: "Bulgarian", name: "bg"},
                {displayName: "Catalan (Spain)", name: "ca_ES"},
                {displayName: "Catalan", name: "ca"},
                {displayName: "Chinese (China)", name: "zh_CN"},
                {displayName: "Chinese (Hong Kong)", name: "zh_HK"},
                {displayName: "Chinese (Singapore)", name: "zh_SG"},
                {displayName: "Chinese (Taiwan)", name: "zh_TW"},
                {displayName: "Chinese", name: "zh"},
                {displayName: "Croatian (Croatia)", name: "hr_HR"},
                {displayName: "Croatian", name: "hr"},
                {displayName: "Czech (Czech Republic)", name: "cs_CZ"},
                {displayName: "Czech", name: "cs"},
                {displayName: "Danish (Denmark)", name: "da_DK"},
                {displayName: "Danish", name: "da"},
                {displayName: "Dutch (Belgium)", name: "nl_BE"},
                {displayName: "Dutch (Netherlands)", name: "nl_NL"},
                {displayName: "Dutch", name: "nl"},
                {displayName: "English (Australia)", name: "en_AU"},
                {displayName: "English (Canada)", name: "en_CA"},
                {displayName: "English (India)", name: "en_IN"},
                {displayName: "English (Ireland)", name: "en_IE"},
                {displayName: "English (Malta)", name: "en_MT"},
                {displayName: "English (New Zealand)", name: "en_NZ"},
                {displayName: "English (Philippines)", name: "en_PH"},
                {displayName: "English (Singapore)", name: "en_SG"},
                {displayName: "English (South Africa)", name: "en_ZA"},
                {displayName: "English (United Kingdom)", name: "en_GB"},
                {displayName: "English (United States)", name: "en_US"},
                {displayName: "English", name: "en"},
                {displayName: "Estonian (Estonia)", name: "et_EE"},
                {displayName: "Estonian", name: "et"},
                {displayName: "Finnish (Finland)", name: "fi_FI"},
                {displayName: "Finnish", name: "fi"},
                {displayName: "French (Belgium)", name: "fr_BE"},
                {displayName: "French (Canada)", name: "fr_CA"},
                {displayName: "French (France)", name: "fr_FR"},
                {displayName: "French (Luxembourg)", name: "fr_LU"},
                {displayName: "French (Switzerland)", name: "fr_CH"},
                {displayName: "French", name: "fr"},
                {displayName: "German (Austria)", name: "de_AT"},
                {displayName: "German (Germany)", name: "de_DE"},
                {displayName: "German (Greece)", name: "de_GR"},
                {displayName: "German (Luxembourg)", name: "de_LU"},
                {displayName: "German (Switzerland)", name: "de_CH"},
                {displayName: "German", name: "de"},
                {displayName: "Greek (Cyprus)", name: "el_CY"},
                {displayName: "Greek (Greece)", name: "el_GR"},
                {displayName: "Greek", name: "el"},
                {displayName: "Hebrew (Israel)", name: "iw_IL"},
                {displayName: "Hebrew", name: "iw"},
                {displayName: "Hindi (India)", name: "hi_IN"},
                {displayName: "Hindi", name: "hi"},
                {displayName: "Hungarian (Hungary)", name: "hu_HU"},
                {displayName: "Hungarian", name: "hu"},
                {displayName: "Icelandic (Iceland)", name: "is_IS"},
                {displayName: "Icelandic", name: "is"},
                {displayName: "Indonesian (Indonesia)", name: "in_ID"},
                {displayName: "Indonesian", name: "in"},
                {displayName: "Irish (Ireland)", name: "ga_IE"},
                {displayName: "Irish", name: "ga"},
                {displayName: "Italian (Italy)", name: "it_IT"},
                {displayName: "Italian (Switzerland)", name: "it_CH"},
                {displayName: "Italian", name: "it"},
                {displayName: "Japanese (Japan)", name: "ja_JP"},
                {displayName: "Japanese (Japan,JP)", name: "ja_JP_JP_#u-ca-japanese"},
                {displayName: "Japanese", name: "ja"},
                {displayName: "Korean (South Korea)", name: "ko_KR"},
                {displayName: "Korean", name: "ko"},
                {displayName: "Latvian (Latvia)", name: "lv_LV"},
                {displayName: "Latvian", name: "lv"},
                {displayName: "Lithuanian (Lithuania)", name: "lt_LT"},
                {displayName: "Lithuanian", name: "lt"},
                {displayName: "Macedonian (Macedonia)", name: "mk_MK"},
                {displayName: "Macedonian", name: "mk"},
                {displayName: "Malay (Malaysia)", name: "ms_MY"},
                {displayName: "Malay", name: "ms"},
                {displayName: "Maltese (Malta)", name: "mt_MT"},
                {displayName: "Maltese", name: "mt"},
                {displayName: "Norwegian (Norway)", name: "no_NO"},
                {displayName: "Norwegian (Norway,Nynorsk)", name: "no_NO_NY"},
                {displayName: "Norwegian", name: "no"},
                {displayName: "Polish (Poland)", name: "pl_PL"},
                {displayName: "Polish", name: "pl"},
                {displayName: "Portuguese (Brazil)", name: "pt_BR"},
                {displayName: "Portuguese (Portugal)", name: "pt_PT"},
                {displayName: "Portuguese", name: "pt"},
                {displayName: "Romanian (Romania)", name: "ro_RO"},
                {displayName: "Romanian", name: "ro"},
                {displayName: "Russian (Russia)", name: "ru_RU"},
                {displayName: "Russian", name: "ru"},
                {displayName: "Serbian (Bosnia and Herzegovina)", name: "sr_BA"},
                {displayName: "Serbian (Latin)", name: "sr__#Latn"},
                {displayName: "Serbian (Latin,Bosnia and Herzegovina)", name: "sr_BA_#Latn"},
                {displayName: "Serbian (Latin,Montenegro)", name: "sr_ME_#Latn"},
                {displayName: "Serbian (Latin,Serbia)", name: "sr_RS_#Latn"},
                {displayName: "Serbian (Montenegro)", name: "sr_ME"},
                {displayName: "Serbian (Serbia and Montenegro)", name: "sr_CS"},
                {displayName: "Serbian (Serbia)", name: "sr_RS"},
                {displayName: "Serbian", name: "sr"},
                {displayName: "Slovak (Slovakia)", name: "sk_SK"},
                {displayName: "Slovak", name: "sk"},
                {displayName: "Slovenian (Slovenia)", name: "sl_SI"},
                {displayName: "Slovenian", name: "sl"},
                {displayName: "Spanish (Argentina)", name: "es_AR"},
                {displayName: "Spanish (Bolivia)", name: "es_BO"},
                {displayName: "Spanish (Chile)", name: "es_CL"},
                {displayName: "Spanish (Colombia)", name: "es_CO"},
                {displayName: "Spanish (Costa Rica)", name: "es_CR"},
                {displayName: "Spanish (Cuba)", name: "es_CU"},
                {displayName: "Spanish (Dominican Republic)", name: "es_DO"},
                {displayName: "Spanish (Ecuador)", name: "es_EC"},
                {displayName: "Spanish (El Salvador)", name: "es_SV"},
                {displayName: "Spanish (Guatemala)", name: "es_GT"},
                {displayName: "Spanish (Honduras)", name: "es_HN"},
                {displayName: "Spanish (Mexico)", name: "es_MX"},
                {displayName: "Spanish (Nicaragua)", name: "es_NI"},
                {displayName: "Spanish (Panama)", name: "es_PA"},
                {displayName: "Spanish (Paraguay)", name: "es_PY"},
                {displayName: "Spanish (Peru)", name: "es_PE"},
                {displayName: "Spanish (Puerto Rico)", name: "es_PR"},
                {displayName: "Spanish (Spain)", name: "es_ES"},
                {displayName: "Spanish (United States)", name: "es_US"},
                {displayName: "Spanish (Uruguay)", name: "es_UY"},
                {displayName: "Spanish (Venezuela)", name: "es_VE"},
                {displayName: "Spanish", name: "es"},
                {displayName: "Swedish (Sweden)", name: "sv_SE"},
                {displayName: "Swedish", name: "sv"},
                {displayName: "Thai (Thailand)", name: "th_TH"},
                {displayName: "Thai (Thailand,TH)", name: "th_TH_TH_#u-nu-thai"},
                {displayName: "Thai", name: "th"},
                {displayName: "Turkish (Turkey)", name: "tr_TR"},
                {displayName: "Turkish", name: "tr"},
                {displayName: "Ukrainian (Ukraine)", name: "uk_UA"},
                {displayName: "Ukrainian", name: "uk"},
                {displayName: "Vietnamese (Vietnam)", name: "vi_VN"}
            ];
        };

    }]);

})();