(function (){
    var app = angular.module('admin',['admin-catalog','admin-login']);

    app.controller('SideMenuController', function(){
        this.entryId = 'overview';

        this.selectEntry = function(setId){
            this.entryId = setId;
        };

        this.isSelected = function(checkId){
            return this.entryId === checkId;
        };
    });

})();