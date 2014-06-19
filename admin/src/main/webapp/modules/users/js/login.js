(function (){
    var app = angular.module('admin-login',[]);

    app.controller('LoginController', ['$http', function($http){
        var login = this;
        login.authWrapper = {
            logged:false,
            login:null,
            authorization:null
        };

        login.credentials = {}

        this.login = function (){
            var stringView= new StringView(login.credentials.login + ':' + login.credentials.password);
            var encoded = stringView.toBase64();
            $http.defaults.headers.common.Authorization = 'Basic ' + encoded;

            $http.head('rs/user').success(function(data){
                login.authWrapper.logged = true;
                login.authWrapper.login = login.credentials.login;
            });
        };

        this.logout = function (){
            login.authWrapper.logged = false;
            login.authWrapper.login = login.credentials.login;
            $http.defaults.headers.common.Authorization = null;
        };
    }]);

})();