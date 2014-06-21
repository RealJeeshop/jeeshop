(function (){
    var app = angular.module('admin-login',[]);

    app.controller('LoginController', ['AuthService', function(AuthService){
        var login = this;

        login.credentials = {}
        login.authenticationFailed = false;

        this.login = function (){
            AuthService.login(login.credentials);
        };

        this.logout = function (){
            AuthService.logout(login.credentials);
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
                $http.head('rs/user').
                    success(function(data){
                        auth.wrapper.logged = true;
                        auth.wrapper.login = credentials.login;
                    }).
                    error(function(data){
                        auth.wrapper.hasAuthenticationFailed = true;
                    });
            },
            isAuthenticated: function () {
                return auth.wrapper.logged === true;
            },
            hasAuthenticationFailed: function () {
                return auth.wrapper.hasAuthenticationFailed;
            },
            logout: function () {
                auth.wrapper.logged = false;
                auth.wrapper.login = null;
            }
        };
    }]);



})();