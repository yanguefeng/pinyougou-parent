app.controller("itemController", function ($scope,$http) {

    $scope.num = 1;
    //�������������
    $scope.addNum = function (num) {
        $scope.num += num;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    }


    //���������
    $scope.specification = {};
    //�������������ֵ
    $scope.selectSpecification = function (key, value) {
        $scope.specification[key] = value;

    }
    //�ж��Ƿ񱻵��������
    $scope.isSelected = function (key, value) {
        return $scope.specification[key] == value;
    }


    //����sku�б����
    $scope.sku = [];

    //���巽������ɳ�ʼ����ʱ��ʹ��Ĭ�ϵĹ��
    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        //Ϊ�˲�Ӱ����ֵ�ı仯ʹ�����¡���������������
        $scope.specification = JSON.parse(JSON.stringify($scope.sku.spec));
    }


    //���巽���ж����������Ƿ����
    matchObject = function (map1, map2) {
        //ѭ���������Ƿ����
        for (var k in map1) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }

        //�鿴���������Ԫ�ظ����Ƿ����
        for (var k in map2) {
            if (map2[k] != map1[k]) {
                return false;
            }
        }

        return true;

    }

    //���巽���������ϣ��鿴�Ƿ�����֮ƥ��Ķ���
    $scope.searchSku = function () {

        for (var i = 0; i < skuList.length; i++) {
            //�жϵ��ѡ�еĹ���б�����ɵ��Ƿ����
            if (matchObject(skuList[i].spec, $scope.specification)) {
                $scope.sku = skuList[i];
                return;
            }
        }
    };
    //��ӵ����ﳵ
    $scope.addToCart=function () {
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+"&num="+$scope.num,
            {"withCredentials":true}).success(
            function (response) {
                if (response.success){
                    //�ɹ���ת�����ﳵҳ��
                    location.href="http://localhost:9107/cart.html";
                } else{
                    alert(response.message)
                }
            }
        )
    }
});