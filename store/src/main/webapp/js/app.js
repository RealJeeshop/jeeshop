(function () {

    var app = angular.module('store', ['ui.bootstrap', 'ngCookies', 'ngSanitize', 'ui.router', 'pascalprecht.translate', 'store-user', 'store-order', 'store-catalog']);

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

        $state.go('account.data');

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
            .state('signup', {
                url: "/signup",
                templateUrl: "views/sign-up.html",
                controller: function ($translatePartialLoader, $translate) {
                    $translatePartialLoader.addPart('signup');
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
            .state('login', {
                url: "/signin",
                templateUrl: "views/sign-in.html",
                controller: function ($translatePartialLoader, $translate) {
                    $translatePartialLoader.addPart('login');
                    $translate.refresh();
                }
            })
            .state('initresetpasswd', {
                url: "/initresetpasswd",
                templateUrl: "views/init-reset-password.html",
                controller: function ($translatePartialLoader, $translate) {
                }
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
                templateUrl: "views/user-account.html",
                controller: function ($translatePartialLoader, $translate) {
                    $translatePartialLoader.addPart('account');
                    $translate.refresh();
                }
            })
            .state('account.data', {
                url: "/account/data",
                templateUrl: "views/user-account-data.html"
            })
            .state('account.orders', {
                url: "/account/orders",
                templateUrl: "views/user-account-orders.html"
            })
            .state('delivery', {
                url: "/delivery",
                templateUrl: "views/delivery.html",
                controller: function ($translatePartialLoader, $translate) {
                    $translatePartialLoader.addPart('delivery');
                    $translate.refresh();
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
            var availableLang = ['en', 'fr'];
            var nav = window.navigator;
            var locale = ((angular.isArray(nav.languages) ? nav.languages[0] : nav.language || nav.browserLanguage || nav.systemLanguage || nav.userLanguage) || '').split('-').join('_');

            var lang_id = locale.substr(0, 2);
            if (!locale || locale.length == 0 || availableLang.indexOf(lang_id) == -1) {
                lang_id = 'en';
            }
            return lang_id;
        };

        $translatePartialLoaderProvider.addPart('common');
        $translatePartialLoaderProvider.addPart('home');
        $translatePartialLoaderProvider.addPart('login');
        $translatePartialLoaderProvider.addPart('signup');
        $translatePartialLoaderProvider.addPart('activation');
        $translatePartialLoaderProvider.addPart('product');
        $translatePartialLoaderProvider.addPart('delivery');
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