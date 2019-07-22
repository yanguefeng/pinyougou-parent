 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadFileService2,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id =$location.search().id;
		if (id == null){ //判断是否是添加，
			return ;
		}
		//不是添加就查询回显内容
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//复文本框内容回显
                editor.html(response.goodsDesc.introduction);
				//商品图片的显示,返回的数据为字符串，需要转JSON
				$scope.entity.goodsDesc.itemImages=JSON.parse(response.goodsDesc.itemImages);
				//扩展信息的回显
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.goodsDesc.customAttributeItems);
				//规格的转换
                $scope.entity.goodsDesc.specificationItems=JSON.parse(response.goodsDesc.specificationItems);
				//sku的显示返回的sku列表中的spec属性为字符串需要进行json转换
                var itemls =response.itemList;
                for (var i = 0; i < itemls.length; i++) {
                	//给每个itemList的spec属性进行转json操作在赋值给itemList.spec
					itemls[i].spec=JSON.parse(itemls[i].spec)
                }
			}
		);				
	};


	//定义方法完成规格数据回显的选定
	$scope.updateChecked=function(specName,optionName){
		var items=$scope.entity.goodsDesc.specificationItems;
        /*
        调用方法判断是否存在规格对象
         */
        var obj =$scope.findByKeyObject(items,"attributeName",specName);
        if (obj !=null){
        	/*
        	判断值是否存在，存在返回true
        	 */
        	var index = obj.attributeValue.indexOf(optionName);
        	return index>=0;
		}
		return false;
	};


	
	//保存 
	$scope.save=function(){

        $scope.entity.goodsDesc.introduction=editor.html();//获取文本复选框内容
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
                if(response.success){
                    //重新查询
                    alert(response.message);//重新加载
                }else{
                    alert(response.message);
                }
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};


	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[],customAttributeItems:[]},itemList:[]};

	$scope.add_image_entity=function(){
		//添加图片列表
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity)
	};

	//删除图片列表
	$scope.dele_image_entity=function(index){
        $scope.entity.goodsDesc.itemImages.splice(index,1)
	};



    //上传文件
    $scope.upload=function () {
    	alert(1);
        uploadFileService2.upload().success(function (respones) {
            if (respones.success){
                $scope.image_entity.url=respones.message;//设置文件地址
            }else{
            	alert(respones.message)
			}
        });
    };


    //页面加载完成显示一级分类
    $scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(function (response) {
			$scope.itemCat1List=response;
        })
    };
	//完成联动我们需要监测下拉列表的变量是否发生改变，发生改变调用方法查出对应的下级信息，从而在上级下拉列表改变之后，下级的下拉列表的显示
    //监测下拉列表中变量值的改变出发查询方法，获取二级下拉列表框获取内容
	$scope.$watch("entity.goods.category1Id",function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List=response;
        })
    });


    //当变量改变三级下拉列表框获取内容
    $scope.$watch("entity.goods.category2Id",function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List=response;
        })
    });

    //三级下拉列表框绑定的变量发生改变获取模板id
    $scope.$watch("entity.goods.category3Id",function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId=response.typeId;
        })
    });

    //当模板id改变之后获得品牌信息
    $scope.$watch("entity.goods.typeTemplateId",function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
			$scope.brandList=JSON.parse(response.brandIds);
			if ($location.search().id == null) {//判断等于null的时候为添加，执行下边的方法
                //在模板id改变之后我们要在模板表中获取到扩展属性
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems);
			}

        });

		//模板id改变之后加载规格个规格列表
		typeTemplateService.findSepcList(newValue).success(function (response) {
			$scope.specList=response;
        });

    });


    //定义方法，是选中的复选框的值一固定的形式添加到对象中
    $scope.updateSpecAttribute=function ($event,specName,value) {
		var obj =$scope.findByKeyObject($scope.entity.goodsDesc.specificationItems,"attributeName",specName);
		if (obj != null) {//判断集合中是否存在对象
			if ($event.target.checked){//判断复选框是否被选中
				//选中我么添加值
                obj.attributeValue.push(value);
			}else{
				//没有选中我们移除值
                var index =obj.attributeValue.indexOf(value);
                obj.attributeValue.splice(index,1);
                if (obj.attributeValue.length ==0){
					//当obj.attributeValue的长度为零的时候我们移除对象
					var o_inedex=$scope.entity.goodsDesc.specificationItems.indexOf(obj);
                    $scope.entity.goodsDesc.specificationItems.splice(o_inedex,1);
				}
			}
		}else{
			//对象为空我们添加对象
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[value]});
		}

    };


    /**
	 * //定义方法完成sku列表的生成，
	 * 1.初始化itemList集合称为我们想要的格式
	 * 2.遍历specificationItems获取attributeName的值，和attributeValue的值
	 * 3.创建一个新的集合，遍历初始化对象
	 * 4.获取第i个对象，赋给一个变量
	 * 5.遍历获取的attributeValue
	 * 6.通过JSON.parse（JSON.srtingify(初始化集合里的对象)）实现深克隆对象
	 * 7.给克隆的对象key赋值
	 * 8.把克隆的对象添加到新的集合中
	 * 9.给初始化的集合赋值我们新的集合
     */
    $scope.crateItemList=function () {
		//初始化数组
        $scope.entity.itemList=[{spec:{},price:'0',num:'0',status:'0',isDefault:'0'}];
        //定义集合变量赋值
		var specification =$scope.entity.goodsDesc.specificationItems;
		//遍历集合
        for (var i = 0; i < specification.length; i++) {
        	//拿到第一个对象
			var keyName =specification[i].attributeName;
			//获取集合的attributeValue属性
			var keyValue =specification[i].attributeValue;

			//调用方法给初始化集合赋值操作完成后的集合
			$scope.entity.itemList=addColumn($scope.entity.itemList,keyName,keyValue);
        }
    };

    //定义方法添加列
	addColumn=function (list,keyName,keyValue) {
		var newList=[];//定义新的集合
        for (var i = 0; i < list.length; i++) {
        	//把遍历的初始化的集合赋值给一个变量
			var oldRow=list[i];
            for (var j = 0; j < keyValue.length; j++) {
				var newRow=JSON.parse(JSON.stringify(oldRow));//深克隆初始化集合对象
				//给克隆的集合key赋值
				newRow.spec[keyName]=keyValue[j];
				//把新的对象添加到新的集合中
				newList.push(newRow);
            }
        }
        //返回新的集合
		return newList;
    }


    //定义一个数组申请显示状态
	$scope.auditStatusArray=['未审核','审核通过','审核未通过','已关闭'];

	//定义一个方法用于分级的展示
	$scope.itemCatList=[];
    /**
	 * 此方法通过查询所有的分类信息，把查到的数据id当做索引，name当做值来构成一个数据，在页面通过索引取值显示等级信息
     */
	$scope.findItemCatList=function () {
		itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
				//把查询到分级数据的id当做索引，name当做索引对应的值
				$scope.itemCatList[response[i].id]=response[i].name;
            }
        })
    }

    //商品的上下架
    $scope.uplateIsMarketable=function (status){
		goodsService.uplateIsMarketable($scope.selectIds,status).success(function (response) {
			if (response.success){
                alert(response.message);
				$scope.reloadList();
                $scope.selectIds=[];
			} else{
				alert(response.message)
			}
        })
	}

	//商品删除
	$scope.uplateIsDelete=function () {
		goodsService.uplateIsDelete($scope.selectIds).success(function (response) {
            if (response.success){
                alert(response.message);
                $scope.reloadList();
                $scope.selectIds=[];
            } else{
                alert(response.message)
            }
        })
    }
});	
