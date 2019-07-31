app.service("userService",function ($http) {


    this.register=function (entity,htmlCode) {
        return $http.post("../user/register.do?htmlCode="+htmlCode,entity)
    };


    this.createCode=function (phone) {
        return $http.get("../user/createSmsCode.do?phone="+phone)
    }
});