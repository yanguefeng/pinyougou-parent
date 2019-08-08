app.service("seckillService",function ($http) {


    //查询秒杀的商品信息
    this.findList=function () {
        return $http.get('../seckillGoods/findList.so')
    }

    //存缓存中查询数据
    this.findOne=function (id) {
        return $http.get('../seckillGoods/findOneFromRedis.do?id='+id)
    }

    //提交订单
    this.submitOrder=function(seckillId){
        return $http.get('seckillOrder/submitOrder.do?seckillId='+seckillId);
    }
});