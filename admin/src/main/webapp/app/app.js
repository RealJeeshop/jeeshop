require('../node_modules/jquery/dist/jquery.min.js');
require('../node_modules/angular-file-upload-shim/dist/angular-file-upload-html5-shim.js');
require('../node_modules/angular/angular.js');
require('../node_modules/angular-sanitize/angular-sanitize.js');
require('../node_modules/angular-ui-router/release/angular-ui-router.js');
require('../node_modules/angular-file-upload-shim/dist/angular-file-upload.js');
require('../node_modules/angular-ui-bootstrap/dist/ui-bootstrap-tpls.js');

require ('./util/js/util.js');
require ('./user/js/login.js');
require ('./user/js/user.js');
require ('./order/js/order.js');
require ('./mail/js/mail.js');
require ('./catalog/js/catalog.js');

(function () {


    var app = angular.module('admin', ['ui.bootstrap', 'ui.router', 'angularFileUpload', 'admin-util', 'admin-catalog', 'admin-login', 'admin-user', 'admin-mail', 'admin-order']);

    app.controller('RestrictedAccessController', ['$state', 'AuthService', function ($state, AuthService) {
        var ctrl = this;

        ctrl.hasAccess = function () {
            if (!AuthService.isAuthenticated()) {
                $state.go('home');
                return false;
            }
            return true;
        };
    }]);

    app.controller('DatepickerDemoCtrl', ['$scope', function ($scope) {

        $scope.today = function () {
            $scope.dt = new Date();
        };
        $scope.today();

        $scope.open = function ($event) {
            $event.preventDefault();
            $event.stopPropagation();

            $scope.opened = true;
        };

        $scope.dateOptions = {
            startingDay: 1
        };
    }]);



    app.config(['$stateProvider', '$urlRouterProvider', '$locationProvider', function ($stateProvider, $urlRouterProvider, $locationProvider) {
        // For any unmatched url, redirect to /home
        $urlRouterProvider.otherwise("home");

        $locationProvider.hashPrefix("!");

        $stateProvider
            .state('home', {
                url: "/home",
                templateUrl: "app/home/index.html"
            })
            .state('index', {
                url: "",
                templateUrl: "app/home/index.html"
            })
            .state('catalog', {
                url: "/catalog",
                templateUrl: 'app/catalog/index.html'
            })
            .state('catalog.items', {
                url: "/:resource",
                templateUrl: 'app/catalog/catalog-entries.html'
            })
            .state('catalog.items.detail', {
                url: "/:itemId",
                templateUrl: function ($stateParams) {
                    return 'app/catalog/' + $stateParams.resource + '-form.html';
                }
            })
            .state('user', {
                url: "/user",
                templateUrl: 'app/user/index.html'
            })
            .state('user.users', {
                url: "/users",
                templateUrl: 'app/user/user-entries.html'
            })
            .state('user.users.detail', {
                url: "/:userId",
                templateUrl: 'app/user/user-form.html'
            })
            .state('order', {
                url: "/order",
                templateUrl: 'app/order/index.html'
            })
            .state('order.orders', {
                url: "/orders",
                templateUrl: 'app/order/order-entries.html'
            })
            .state('order.operations', {
                url: "/operations",
                templateUrl: 'app/order/order-operations.html'
            })
            .state('order.orders.detail', {
                url: "/:orderId",
                templateUrl: 'app/order/order-form.html'
            })
            .state('mail', {
                url: "/mail",
                templateUrl: "app/mail/index.html"
            })
            .state('mail.entries', {
                url: "/rs/:resource",
                templateUrl: function ($stateParams) {
                    return 'app/mail/mail-entries.html';
                }
            })
            .state('mail.operations', {
                url: "/mail/operations",
                templateUrl: "app/mail/mail-operations.html"
            })
            .state('mail.entries.detail', {
                url: "/:itemId",
                templateUrl: function ($stateParams) {
                    return 'app/mail/' + $stateParams.resource + '-form.html';
                }
            })
            .state('statistic', {
                url: "/statistic",
                templateUrl: 'app/statistic/index.html'
            });

    }]);


})();
