app.controller("cartController",function ($scope,cartService) {

    //查询购物车信息
    $scope.findCookieCartList=function () {
        cartService.findCookieCartList().success(function (response) {
            $scope.cartList=response;
        })
    };

    //添加购物车信息
    $scope.addGoodsToCartList=function (itemId, num) {
        cartService.addGoodsToCartList(itemId,num).success(function (response) {
            if (response.success){
                alert(response.message)
            } else{
                alert(response.message)
            }
        })
    }
});