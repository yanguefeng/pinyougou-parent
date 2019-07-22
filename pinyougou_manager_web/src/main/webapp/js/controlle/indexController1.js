app.controller("indexController",function ($scope,loginService) {

    $scope.showLoginName=function () {
        loginService.loginName().success(function (respones) {
            $scope.loginName=respones.loginName;
        })
    }
});