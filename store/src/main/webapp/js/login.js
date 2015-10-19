(function (){
    var app = angular.module('store-login',['ngSanitize', 'ui.router']);

    app.config(['$httpProvider', function($httpProvider){
        $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
    }]);

    app.controller('LoginController', ['AuthService','$scope', '$state', function(AuthService,$scope, $state){
        var login = this;

        login.credentials = {};
        login.authenticationFailed = false;

        this.logout = function (){
            AuthService.logout(login.credentials);
            login.credentials={};
        };

        this.isAuthenticated = function(){
            return AuthService.isAuthenticated();
        };

        this.hasAuthenticationFailed = function(){
            return AuthService.hasAuthenticationFailed();
        };
    }]);

    app.factory('AuthService', ['$http','$state', function ($http,$state) {
        var auth = this;
        auth.wrapper = {
            logged:false,
            login:null,
            hasAuthenticationFailed:false
        };

        return {
            login: function (credentials, $modalInstance) {
                var stringView= new StringView(credentials.login + ':' + credentials.password);
                var encoded = stringView.toBase64();
                $http.defaults.headers.common.Authorization = 'Basic ' + encoded;
                var success = false;
                $http.head('rs/users').
                    success(function(data){
                        auth.wrapper.logged = true;
                        auth.wrapper.login = credentials.login;
                        auth.wrapper.hasAuthenticationFailed = false;
                        $modalInstance.close();
                        $state.go('categories');
                    }).
                    error(function(data, status){
                        auth.wrapper.hasAuthenticationFailed = true;
                    });
            },
            isAuthenticated: function () {
                return auth.wrapper.logged === true;
            },

            getLogin: function () {
                return auth.wrapper.login;
            },

            hasAuthenticationFailed: function () {
                return auth.wrapper.hasAuthenticationFailed;
            },
            logout: function () {
                auth.wrapper.logged = false;
                auth.wrapper.login = null;
                $http.defaults.headers.common.Authorization=null;
            }
        };
    }]);



})();