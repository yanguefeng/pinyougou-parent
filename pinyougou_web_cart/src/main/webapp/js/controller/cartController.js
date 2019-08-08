app.controller("cartController",function ($scope,cartService) {

    //查询购物车信息
    $scope.findCookieCartList=function () {
        cartService.findCookieCartList().success(function (response) {
            $scope.cartList=response;
            $scope.total=cartService.sum(response);
        })
    };

    //添加购物车信息
    $scope.addGoodsToCartList=function (itemId, num) {
        cartService.addGoodsToCartList(itemId,num).success(function (response) {
            if (response.success){
                $scope.findCookieCartList();
            } else{
                alert(response.message)
            }
        })
    }

    //获取地址信息
    $scope.findAddressList=function () {
        cartService.findAddressList().success(function (response) {
            $scope.addressList=response;
            //默认地址的样式添加
            for (var i = 0; i < response.length; i++) {
                var address=response[i];
                if (address.isDefault == "1") {
                    $scope.address=address;
                }
            }
        })
    };

    //记录信息
    $scope.address={};
    $scope.selectAddress=function (address) {
        $scope.address=address;
    };

    //默认地址的样式选中
    $scope.isSelectAddress=function (address) {
        return $scope.address==address;
    };

    //封装订单信息的对象、
    $scope.order = {paymentType:'1'};
    //选择支付方式
    $scope.selectPaymentType=function (value) {
        $scope.order.paymentType=value;
    };

    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;//设置地址
        $scope.order.receiverMobile=$scope.address.mobile;//设置电话号码
        $scope.order.receiver=$scope.address.contact;//设置姓名
        cartService.submitOrder($scope.order).success(function (response) {
            if (response.success){
                //页面跳转
                if ($scope.order.paymentType=='1'){//微信支付条状到支付页面
                    location.href="pay.html";
                }else{//如果是货到付款，跳转到提示页面
                    location.href="paysuccess.html";
                }
            }else{
                alert(response.message)
            }
        })
    }
});