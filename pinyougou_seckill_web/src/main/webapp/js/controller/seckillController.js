app.controller("seckillController",function ($scope,seckillService,$location) {


    //查询秒杀的商品信息
    $scope.findList=function () {
        seckillService.findList().success(function (response) {
            $scope.seckillOrderList=response;
        })
    };


    //从缓存中查询数据
    $scope.findOne=function () {
        var id =$location.search()['id'];
        seckillService.findOne(id).success(function (response) {
            $scope.entity=response;

            //获取秒杀商品的总毫秒数
            allsecond =Math.floor( (  new Date($scope.entity.endTime).getTime()- (new Date().getTime())) /1000); //总秒数
            time= $interval(function(){
                if(second>0){
                    second =second-1;
                    $scope.timeString=convertTimeString(allsecond);//转换时间字符串
                }else{
                    $interval.cancel(time);
                    alert("秒杀服务已结束");
                }
            },1000);
        })
    };


    //转换秒为   天小时分钟秒格式  XXX天 10:22:33
    convertTimeString=function(allsecond){
        var days= Math.floor( allsecond/(60*60*24));//天数
        var hours= Math.floor( (allsecond-days*60*60*24)/(60*60) );//小数数
        var minutes= Math.floor(  (allsecond -days*60*60*24 - hours*60*60)/60    );//分钟数
        var seconds= allsecond -days*60*60*24 - hours*60*60 -minutes*60; //秒数
        var timeString="";
        if(days>0){
            timeString=days+"天 ";
        }
        return timeString+hours+":"+minutes+":"+seconds;
    };

    //提交订单
    $scope.submitOrder=function(){
        seckillGoodsService.submitOrder($scope.entity.id).success(function(response){
                if(response.success){
                    alert("下单成功，请在1分钟内完成支付");
                    location.href="pay.html";
                }else{
                    alert(response.message);
                }
            }
        );
    }
});