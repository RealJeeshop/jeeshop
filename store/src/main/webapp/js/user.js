(function () {
    var app = angular.module('store-user', ['ngSanitize', 'ui.router']);

    app.config(['$httpProvider', function ($httpProvider) {
        $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
    }]);

    app.factory('AuthService', ['$http', '$state', function ($http, $state) {
        var auth = this;
        auth.wrapper = {
            logged: false,
            login: null,
            hasAuthenticationFailed: false
        };

        return {
            login: function (credentials) {
                var stringView = new StringView(credentials.login + ':' + credentials.password);
                var encoded = stringView.toBase64();
                $http.defaults.headers.common.Authorization = 'Basic ' + encoded;
                $http.head('rs/users').
                success(function () {
                    auth.wrapper.logged = true;
                    auth.wrapper.login = credentials.login;
                    auth.wrapper.hasAuthenticationFailed = false;
                    $state.go('account');
                }).
                error(function () {
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
                $http.defaults.headers.common.Authorization = null;
            }
        };
    }]);

    app.controller('LoginController', ['AuthService', '$state', function (AuthService, $state) {

        var ctrl = this;

        ctrl.hasError = false;
        ctrl.errorKey = null;
        ctrl.credentials = {};
        ctrl.isProcessing = false;

        ctrl.logout = function () {
            AuthService.logout(ctrl.credentials);
            ctrl.credentials = {};
        };

        ctrl.isAuthenticated = function () {
            return AuthService.isAuthenticated();
        };

        ctrl.hasAuthenticationFailed = function () {
            return AuthService.hasAuthenticationFailed();
        };

        ctrl.login = function () {
            ctrl.isProcessing = true;
            AuthService.login(ctrl.credentials);
            ctrl.isProcessing = false;
        };

        ctrl.cancel = function () {
            $state.go('home');
        };
    }]);


    app.controller('ResetPasswordController', ['$http', '$stateParams', '$state', function ($http, $stateParams, $state) {

        var ctrl = this;

        ctrl.hasError = false;
        ctrl.errorKey = "error.technical";
        ctrl.isProcessing = false;
        ctrl.credentials = {};
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
                .success(function () {
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

        ctrl.initReset = function () {
            ctrl.isProcessing = true;
            $http.post('rs/users/' + ctrl.credentials.login + '/password')
                .success(function () {
                    ctrl.isProcessing = false;
                    $state.go("resetPassword")
                })
                .error(function (data, status) {
                    ctrl.isProcessing = false;
                    ctrl.hasError = true;
                    if (status == 404) {
                        ctrl.errorKey = "error.user.unknown";
                    } else {
                        ctrl.errorKey = "error.technical";
                    }

                });

        };

    }]);

    app.controller('ActivationController', ['$http', '$stateParams', function ($http, $stateParams) {

        var ctrl = this;

        ctrl.succeed = false;
        ctrl.isProcessing = false;

        ctrl.activate = function () {
            ctrl.isProcessing = true;
            $http.put('rs/users/' + $stateParams.email, $stateParams.token)
                .success(function () {
                    ctrl.isProcessing = false;
                    ctrl.succeed = true;
                })
                .error(function () {
                    ctrl.isProcessing = false;
                });
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
            ctrl.alerts.splice(index, 1);
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

    app.controller('SignUpController', function ($http, $locale, $state) {

        var ctrl = this;

        ctrl.alerts = [];
        ctrl.user = {};
        ctrl.isProcessing = false;

        ctrl.confirmPasswordDoesNotMatch = false;

        ctrl.closeAlert = function (index) {
            ctrl.alerts.splice(index, 1);
        };

        ctrl.confirmPasswordCheck = function () {
            ctrl.confirmPasswordDoesNotMatch = ctrl.confirmPassword !== ctrl.user.password;
        };

        ctrl.register = function () {
            ctrl.user.preferredLocale = $locale.id;
            ctrl.alerts = [];

            ctrl.isProcessing = true;
            $http.post('rs/users', ctrl.user)
                .success(function (data) {
                    ctrl.isProcessing = false;
                    ctrl.user = data; // update user object

                    // hack for dates returned as timestamp by service
                    ctrl.user.birthDate = ctrl.user.birthDate != null ? new Date(ctrl.user.birthDate) : null;
                    ctrl.user.creationDate = ctrl.user.creationDate != null ? new Date(ctrl.user.creationDate) : null;
                    ctrl.user.updateDate = ctrl.user.creationDate != null ? new Date(ctrl.user.creationDate) : null;

                    $state.go("activation")
                })
                .error(function (data, status) {
                    ctrl.isProcessing = false;
                    if (status == 409) {
                        ctrl.alerts.push({type: 'warning', msg: 'mailAlreadyExist'})
                    } else {
                        ctrl.alerts.push({type: 'danger', msg: 'technical'})
                    }

                });
        };

        ctrl.cancel = function () {
            $state.go('home');
        };

    });

    app.controller('RestrictedAccessController', function ($state, AuthService) {
        var ctrl = this;

        ctrl.hasAccess = function () {
            if (!AuthService.isAuthenticated()) {
                $state.go('home');
                return false;
            }
            return true;
        };
    });

})();