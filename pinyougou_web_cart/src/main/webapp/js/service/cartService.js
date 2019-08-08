app.service("cartService",function ($http) {

    this.findCookieCartList=function () {
        return $http.get("../cart/findCookieCartList.do")
    }


    this.addGoodsToCartList=function (itemId, num) {
        return $http.get("../cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num)
    };


    //该方法用于金额的总计，和数量的总计
    this.sum=function (cartList) {
        //定义一个变量用来接收金额的总数，和数量的总计
        var totalValue={totalNum:0,totalPrice:0}
        //遍历购物车列表
        for (var i = 0; i < cartList.length; i++) {
            //获取购物车
            var cart=cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                //获取商品信息
                var orderItem=cart.orderItemList[j];
                //获取数量的总和
                totalValue.totalNum +=orderItem.num;
                //获取金额的总计
                totalValue.totalPrice +=orderItem.totalFee;
            }
        }
        return totalValue;
    };

    this.findAddressList=function () {
        return $http.get('../address/findAddressListByUsername.do?')
    }

    this.submitOrder=function (order) {
        return $http.post('../order/addOrder.do',order)
    }
});