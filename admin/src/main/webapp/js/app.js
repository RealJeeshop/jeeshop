(function (){

    var app = angular.module('admin',['ui.bootstrap','angularFileUpload','admin-catalog','admin-login', 'admin-user','admin-newsletter', 'ngCkeditor']);

    app.controller('SideMenuController', function(){
        this.entryId = 'overview';

        this.navCollapsed=false;

        this.selectEntry = function(setId){
            this.entryId = setId;
            this.navCollapsed = false;
        };

        this.isSelected = function(checkId){
            return this.entryId === checkId;
        };

    });

    app.controller('DatepickerDemoCtrl',function ($scope) {

        $scope.today = function() {
            $scope.dt = new Date();
        };
        $scope.today();

        $scope.open = function($event) {
         $event.preventDefault();
         $event.stopPropagation();

         $scope.opened = true;
        };

        $scope.dateOptions = {
         startingDay: 1
        };
    });
})();