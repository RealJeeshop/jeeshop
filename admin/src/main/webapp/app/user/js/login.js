(function (){
    var app = angular.module('admin-login',[]);

    app.config(['$httpProvider', function($httpProvider){
        $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
    }]);

    app.controller('LoginController', ['AuthService','$scope', function(AuthService,$scope){
        var login = this;

        login.credentials = {};
        login.authenticationFailed = false;

        this.login = function (){
            AuthService.login(login.credentials);
        };

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

    app.factory('AuthService', ['$http', function ($http) {
        var auth = this;
        auth.wrapper = {
            logged:false,
            login:null,
            hasAuthenticationFailed:false
        };

        return {
            login: function (credentials) {
                var stringView= new StringView(credentials.login + ':' + credentials.password);
                var encoded = stringView.toBase64();
                $http.defaults.headers.common.Authorization = 'Basic ' + encoded;
                var success = false;
                $http.head('rs/users/administrators').
                    success(function(data){
                        auth.wrapper.logged = true;
                        auth.wrapper.login = credentials.login;
                        auth.wrapper.hasAuthenticationFailed = false;
                    }).
                    error(function(data){
                        auth.wrapper.hasAuthenticationFailed = true;
                    });
            },
            isAuthenticated: function () {
                return auth.wrapper.logged === true;
            },
            isNotAuthenticated: function () {
                return auth.wrapper.logged === false;
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