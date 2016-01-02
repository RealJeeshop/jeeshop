(function () {

    var app = angular.module('store', ['ui.bootstrap', 'ngCookies', 'ngSanitize', 'ui.router', 'pascalprecht.translate', 'store-login', 'store-order', 'store-catalog']);

    app.controller('LoginResetModalController', ['$modal', function ($modal) {

        var ctrl = this;

        ctrl.openLoginModal = function () {

            var modalInstance = $modal.open({
                templateUrl: 'views/login.html',
                controller: LoginOrResetPasswordCtrl,
                size: 'md'
            });
            modalInstance.result.then(function () {
            }, function () {

            });

        };

        var LoginOrResetPasswordCtrl = function ($scope, $state, $modalInstance, AuthService, $http) {

            $scope.isResetPasswordFormDisplayed = false;

            $scope.hasError = false;
            $scope.errorKey = null;

            $scope.credentials = {};
            $scope.authenticationFailed = false;
            $scope.isProcessing = false;

            $scope.login = function () {
                $scope.isProcessing = true;
                AuthService.login($scope.credentials, $modalInstance);
                $scope.isProcessing = false;
            };

            $scope.showResetPasswordForm = function () {
                $scope.isResetPasswordFormDisplayed = true;
            };

            $scope.hasAuthenticationFailed = function () {
                return AuthService.hasAuthenticationFailed();
            };

            $scope.resetPassword = function () {
                $scope.isProcessing = true;
                $http.post('rs/users/' + $scope.credentials.login + '/password')
                    .success(function () {
                        $scope.isProcessing = false;
                        $modalInstance.close();
                        $state.go("resetPassword")
                    })
                    .error(function (data, status) {
                        $scope.isProcessing = false;
                        $scope.hasError = true;
                        if (status == 404) {
                            $scope.errorKey = "error.user.unknown";
                        } else {
                            $scope.errorKey = "error.technical";
                        }

                    });

            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };
        };

    }]);

    app.controller('AccountManagementController', ['$http', '$scope', 'AuthService', function ($http, $scope, AuthService) {

        var ctrl = this;

        ctrl.existingPassword = null;
        ctrl.newPassword = null;
        ctrl.confirmPassword = null;

        ctrl.confirmPasswordDoesNotMatch = false;

        ctrl.alerts = [];

        ctrl.user = {};

        ctrl.closeAlert = function (index) {
            $scope.alerts.splice(index, 1);
        };

        ctrl.getCurrentUserInfo = function () {
            $http.get('rs/users/current').success(function (user) {
                ctrl.user = user;
                // Hack for dates
                ctrl.user.birthDate = ctrl.user.birthDate != null ? new Date(ctrl.user.birthDate) : null;
                ctrl.user.creationDate = ctrl.user.creationDate != null ? new Date(ctrl.user.creationDate) : null;
                ctrl.user.updateDate = ctrl.user.creationDate != null ? new Date(ctrl.user.creationDate) : null;
            })
        };

        ctrl.updateUserData = function () {
            ctrl.alerts = [];
            $http.put('rs/users', ctrl.user)
                .success(function () {
                    ctrl.alerts.push({type: 'success', msg: 'data.change.success'});
                    ctrl.confirmPassword = null;
                    ctrl.newPassword = null;
                    ctrl.existingPassword = null;
                    $scope.updatePasswordForm.$setPristine();

                }).error(function () {
                    $scope.updatePasswordForm.$setPristine();
                    ctrl.alerts.push({type: 'danger', msg: 'error.technical'});
                });
        };

        ctrl.changePassword = function () {
            ctrl.alerts = [];
            $http.put('rs/users/' + AuthService.getLogin() + '/password', ctrl.newPassword)
                .success(function () {
                    ctrl.alerts.push({type: 'success', msg: 'password.change.success'});
                    ctrl.confirmPassword = null;
                    ctrl.newPassword = null;
                    ctrl.existingPassword = null;
                    $scope.updatePasswordForm.$setPristine();

                }).error(function () {
                    $scope.updatePasswordForm.$setPristine();
                    ctrl.alerts.push({type: 'danger', msg: 'error.technical'});
                });
        };

        ctrl.confirmPasswordCheck = function () {
            ctrl.confirmPasswordDoesNotMatch = ctrl.confirmPassword !== ctrl.newPassword;
        };

        ctrl.getCurrentUserInfo();

    }]);


    app.controller('SubscriptionModalController', ['$modal', function ($modal) {

        var ctrl = this;

        ctrl.openSubscriptionModal = function () {

            var modalInstance = $modal.open({
                templateUrl: 'views/subscription.html',
                controller: SubscriptionFormController,
                size: 'md'
            });
            modalInstance.result.then(function () {
                ctrl.alerts = [];
            }, function () {

            });

        };

        var SubscriptionFormController = function ($http, $scope, $modalInstance, $locale, $state) {

            $scope.alerts = [];
            $scope.user = {};
            $scope.agreement = false;
            $scope.isProcessing = false;

            $scope.confirmPasswordDoesNotMatch = false;

            $scope.closeAlert = function (index) {
                $scope.alerts.splice(index, 1);
            };

            $scope.confirmPasswordCheck = function () {
                $scope.confirmPasswordDoesNotMatch = ctrl.confirmPassword !== ctrl.user.password;
            };

            $scope.register = function () {
                $scope.user.preferredLocale = $locale.id;
                $scope.alerts = [];

                $scope.isProcessing = true;
                $http.post('rs/users', $scope.user)
                    .success(function (data) {
                        $scope.isProcessing = false;
                        $scope.user = data; // update user object

                        // hack for dates returned as timestamp by service
                        $scope.user.birthDate = $scope.user.birthDate != null ? new Date($scope.user.birthDate) : null;
                        $scope.user.creationDate = $scope.user.creationDate != null ? new Date($scope.user.creationDate) : null;
                        $scope.user.updateDate = $scope.user.creationDate != null ? new Date($scope.user.creationDate) : null;

                        $modalInstance.close();
                        $state.go("activation")
                    })
                    .error(function (data, status) {
                        $scope.isProcessing = false;
                        if (status == 409) {
                            $scope.alerts.push({type: 'warning', msg: 'mailAlreadyExist'})
                        } else {
                            $scope.alerts.push({type: 'danger', msg: 'technical'})
                        }

                    });
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };
        };

    }]);

    app.controller('ActivationController', ['$http', '$stateParams', '$state', function ($http, $stateParams) {

        var ctrl = this;

        ctrl.succeed = false;
        ctrl.isProcessing = false;

        ctrl.activate = function () {
            ctrl.isProcessing = true;
            $http.put('rs/users/' + $stateParams.email, $stateParams.token)
                .success(function (data) {
                    ctrl.isProcessing = false;
                    ctrl.succeed = true;
                })
                .error(function (data, status) {
                    ctrl.isProcessing = false;
                    //$state.go("home");
                });
        };

    }]);


    app.controller('ResetPasswordController', ['$http', '$stateParams', '$state', function ($http, $stateParams, $state) {

        var ctrl = this;

        ctrl.hasError = false;
        ctrl.errorKey = "error.technical";
        ctrl.isProcessing = false;

        ctrl.password = null;
        ctrl.confirmPassword = null;
        ctrl.confirmPasswordDoesNotMatch = false;

        ctrl.succeed = false;

        ctrl.showResetPasswordForm = function () {
            return ($stateParams.token !== null);
        };

        ctrl.confirmPasswordCheck = function () {
            ctrl.confirmPasswordDoesNotMatch = ctrl.confirmPassword !== ctrl.password;
        };

        ctrl.reset = function () {

            ctrl.isProcessing = true;

            $http.put('rs/users/' + $stateParams.email + '/password?token=' + $stateParams.token, ctrl.password)
                .success(function (data) {
                    ctrl.isProcessing = false;
                    ctrl.succeed = true;
                })
                .error(function (data, status) {
                    ctrl.isProcessing = false;
                    ctrl.hasError = true;
                    if (status == 404) {
                        ctrl.errorKey = "error.token.unknown";
                    }
                });
        };

    }]);

    app.controller('RestrictedAreaController', function ($state, AuthService) {
        if (!AuthService.isAuthenticated()) {
            $state.go('home');
        }
    });

    app.controller('DiscountsController', ['$http', '$locale', function ($http, $locale) {
        var ctrl = this;

        ctrl.discounts = [];

        ctrl.fillVisibleOrderDiscounts = function () {
            $http.get('rs/discounts/visible?applicableTo=ORDER&locale=' + $locale.id)
                .success(function (discounts) {
                    var discountsBy3 = [];
                    for (i = 0; i < discounts.length; i++) {

                        discountsBy3.push(discounts[i]);

                        if ((discountsBy3.length == 3) || (i == discounts.length - 1)) {
                            ctrl.discounts.push(discountsBy3);
                            discountsBy3 = [];
                        }
                    }
                })
                .error(function (data, status) {
                    //TODO Handle case Discounts are not retrievable ?
                });
        };


        ctrl.fillVisibleOrderDiscounts();

    }]);



    app.controller('DatepickerCtrl', function ($scope) {

        var date = new Date();

        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.opened = true;
        };

        $scope.maxDate = date.getFullYear() + '-' + (date.getMonth() + 1) + '-' + date.getDate();

    });

    app.controller('MenuController', function ($state) {

        var ctrl = this;

        ctrl.isState = function (state, params) {
            return $state.is(state, params) || (state == 'home' && $state.is('index'));
        };

    });

    app.controller('AccountMenuController', function ($state) {

        var ctrl = this;

        ctrl.isState = function (state) {
            return $state.is(state);
        };

    });


    app.config(function ($stateProvider, $urlRouterProvider, $translateProvider, $translatePartialLoaderProvider, $locationProvider) {
        // For any unmatched url, redirect to /state1
        $urlRouterProvider.otherwise("home");

        $locationProvider.hashPrefix("!");

        // Now set up the states
        $stateProvider
            .state('home', {
                url: "/home",
                templateUrl: "views/home.html",
                controller: function ($translatePartialLoader, $translate) {
                    $translatePartialLoader.addPart('home');
                    $translate.refresh();
                }
            })
            .state('index', {
                url: "",
                templateUrl: "views/home.html",
                controller: function ($translatePartialLoader, $translate) {
                    $translatePartialLoader.addPart('home');
                    $translate.refresh();
                }
            })
            .state('activation', {
                url: "/activation",
                templateUrl: "views/activation.html",
                controller: function ($translatePartialLoader, $translate) {
                    $translatePartialLoader.addPart('activation');
                    $translate.refresh();
                }
            })
            .state('activation.activate', {
                url: "/:email/:token",
                templateUrl: 'views/activation-status.html'
            })
            .state('resetpassword', {
                url: "/resetpassword/:email/:token",
                templateUrl: "views/reset-password.html",
                controller: function ($translatePartialLoader, $translate) {
                    $translatePartialLoader.addPart('resetpassword');
                    $translate.refresh();
                }
            })
            .state('category', {
                url: "/category/:categoryId",
                templateUrl: 'views/category.html'
            })
            .state('product', {
                url: "/product/:productId",
                templateUrl: 'views/product.html',
                controller: function ($translatePartialLoader, $translate) {
                    $translatePartialLoader.addPart('product');
                    $translate.refresh();
                }
            })
            .state('account', {
                url: "/account",
                templateUrl: "views/account-management.html",
                controller: function ($translatePartialLoader, $translate) {
                    $translatePartialLoader.addPart('account');
                    $translate.refresh();
                }
            })
            .state('account.data', {
                url: "/data",
                templateUrl: "views/account-management-data.html"
            })
            .state('account.orders', {
                url: "/orders",
                templateUrl: "views/account-management-orders.html"
            })
            .state('delivery', {
                url: "/delivery",
                templateUrl: "views/delivery.html",
                controller: function ($translatePartialLoader, $translate) {
                }
            })
            .state('orderconfirm', {
                url: "/orderconfirm",
                templateUrl: "views/order-confirm.html",
                controller: function ($translatePartialLoader, $translate) {
                }
            }).state('payment', {
                url: "/payment",
                templateUrl: "views/payment.html",
                controller: function ($translatePartialLoader, $translate) {
                }
            });

        // TODO pull request to angular-translate
        var getLocale = function () {
            var availableLang = ['fr'];
            var nav = window.navigator;
            var locale = ((angular.isArray(nav.languages) ? nav.languages[0] : nav.language || nav.browserLanguage || nav.systemLanguage || nav.userLanguage) || '').split('-').join('_');

            var lang_id = locale.substr(0, 2);
            if (!locale || locale.length == 0 || availableLang.indexOf(lang_id) == -1) {
                lang_id = 'fr';
            }
            return lang_id;
        };

        $translatePartialLoaderProvider.addPart('common');
        $translatePartialLoaderProvider.addPart('home');
        $translatePartialLoaderProvider.addPart('activation');
        $translatePartialLoaderProvider.addPart('product');
        $translatePartialLoaderProvider.addPart('account');
        $translatePartialLoaderProvider.addPart('resetpassword');

        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate: 'i18n/{part}/locale-{lang}.json'
        });

        $translateProvider.determinePreferredLanguage(function () {
            return getLocale();
        });

    });
})();