app.controller("userController",function ($scope,userService) {

    //用户注册
    $scope.register=function (htmlCode) {
        userService.register($scope.entity,htmlCode).success(function (response) {
            if (response.success){
                alert(response.message)
            } else{
                alert(response.message)
            }
        })
    }

    //发送验证码
    $scope.createSmsCode=function (phone) {
        userService.createCode(phone).success(function (response) {
            if (response.success){
                alert(response.message)
            } else{
                alert(response.message);
                $scope.entity.phone="";
            }
        })
    };


    //判断上下密码是否一致
    $scope.isPassword=function (htmlCode) {
        if ($scope.entity.password==$scope.pword){
            $scope.register(htmlCode);
        } else{
            alert("两次输入的密码不一致")
        }
    }


    $scope.htmlCode='';
    $scope.pword='';
});