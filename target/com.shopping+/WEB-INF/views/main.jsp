<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
	  <meta charset="utf-8">
	  <meta http-equiv="X-UA-Compatible" content="IE=edge">
	  <meta name="viewport" content="width=device-width, initial-scale=1">
	  <title>基于在线推荐购物系统</title>
	  <%--<link href="/Shopping../static/css/bootstrap.min.css" rel="stylesheet">
	  <link href="/Shopping../static/css/style.css" rel="stylesheet">

	  <script src="/Shopping../static/js/jquery.min.js" type="text/javascript"></script>
	  <script src="/Shopping../static/js/bootstrap.min.js" type="text/javascript"></script>
	  <script src="/Shopping../static/js/layer.js" type="text/javascript"></script>
    <!--[if lt IE 9]>
      <script src="/Shopping../static/js/html5shiv.min.js"></script>
      <script src="/Shopping../static/js/respond.min.js"></script>
    <![endif]-->--%>


	  <link href="/Shopping/css/bootstrap.min.css" rel="stylesheet">
	  <link href="/Shopping/css/style.css" rel="stylesheet">
	  <script src="/Shopping/js/jquery.min.js" type="text/javascript"></script>
	  <script src="/Shopping/js/bootstrap.min.js" type="text/javascript"></script>
	  <script src="/Shopping/js/layer.js" type="text/javascript"></script>
	  <script src="/Shopping/js/html5shiv.min.js" type="text/javascript"></script>
	  <script src="/Shopping/js/respond.min.js" type="text/javascript"></script>
	  <link id="layuicss-skinlayercss" href="http://localhost:8080//Shopping/js/skin/default/layer.css?v=3.0.11110" rel="stylesheet">

  </head>
  <body>
    <!--导航栏部分-->
	<jsp:include page="include/header.jsp"/>
	<!-- 中间内容 -->
	<div class="container-fluid">
		<div class="row">
			<!-- 控制栏 -->
			<div class="col-sm-3 col-md-2 sidebar sidebar-1">
				<ul class="nav nav-sidebar">
					<li class="list-group-item-diy"><a href="#productArea1">衣服配饰 <span class="sr-only">(current)</span></a></li>
					<li class="list-group-item-diy"><a href="#productArea2">数码产品</a></li>
					<li class="list-group-item-diy"><a href="#productArea3">书籍办公</a></li>
					<li class="list-group-item-diy"><a href="#productArea4">游戏周边</a></li>
					<li class="list-group-item-diy"><a href="#productArea5">生活用品</a></li>
					<li class="list-group-item-diy"><a href="#productArea6">化妆用品</a></li>
					<li class="list-group-item-diy"><a href="#productArea7">运动用品</a></li>
					<li class="list-group-item-diy"><a href="#productArea00"></a></li>
					<li class="list-group-item-diy"><a href="#productArea8">个性化推荐</a></li>
				</ul>
			</div>
			<!-- 控制内容 -->
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
				<div class="jumbotron">
					<h1>欢迎来到在线推荐购物系统</h1>
					<p>结合大数据处理技术,设计并实现了一个推荐系统,详细阐述了其主要功能的实现方法，设计并实现了一个高效的数据仓库,作为原始数据及推荐引擎离线计算结果的存储仓库。</p>
				</div>

				<div name="productArea1" class="row pd-10" id="productArea1">
				</div>

				<div name="productArea2" class="row" id="productArea2">
				</div>

				<div name="productArea3" class="row" id="productArea3">
				</div>

                <div name="productArea4" class="row" id="productArea4">
				</div>

				<div name="productArea5" class="row" id="productArea5">
				</div>

				<div name="productArea6" class="row" id="productArea6">
				</div>

				<div name="productArea7" class="row" id="productArea7">
				</div>
				<div name="productArea8" class="row" id="productArea8">
				</div>

			</div>
			<div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2">
				<jsp:include page="include/foot.jsp"/>
			</div>
		</div>
	</div>

  <script type="text/javascript">

	  var loading = layer.load(0);

      var productType = new Array;
      productType[1] = "衣服配饰";
      productType[2] = "数码产品";
      productType[3] = "书籍办公";
      productType[4] = "游戏周边";
      productType[5] = "生活用品";
      productType[6] = "化妆用品";
      productType[7] = "运动用品";
      productType[8] = "个性化推荐";

	  listProducts();

	  function listProducts() {
          if("${currentUser.id}" == null || "${currentUser.id}" == undefined || "${currentUser.id}" ==""){
              var allProduct = getAllProducts();
          }else if (" ${currentUser.id}"!=null&&"${currentUser.id}" !=""){
              var allProduct = getAllProductsandRecommand();
          }else {
              var allProduct = getAllProducts();
          }
          var mark = new Array;
          mark[1] = 0;
          mark[2] = 0;
          mark[3] = 0;
          mark[4] = 0;
          mark[5] = 0;
          mark[6] = 0;
          mark[7] = 0;
          mark[8] = 0;
          for(var i=0;i<allProduct.length;i++){
              if(allProduct[i].type == null || allProduct[i].type == undefined || allProduct[i].type =="") {

              }else {

              var html = "";
			  var imgURL = "/Shopping/img/"+allProduct[i].id+".jpg";
			  html += '<div class="col-sm-4 col-md-4" >'+
					  '<div class="boxes pointer" onclick="productDetail('+allProduct[i].id+')">'+
					  '<div class="big bigimg">'+
					  '<img src="'+imgURL+'">'+
					  '</div>'+
					  '<p class="product-name">'+allProduct[i].name+'</p>'+
					  '<p class="product-price">¥'+allProduct[i].price+'</p>'+
					  '</div>'+
					  '</div>';
              var id = "productArea"+allProduct[i].type;
              var productArea = document.getElementById(id);
              if(mark[allProduct[i].type] == 0){
                  html ='<hr/><h1>'+productType[allProduct[i].type]+'</h1><hr/>'+html;
                  mark[allProduct[i].type] = 1;
              }
              productArea.innerHTML += html;
              }
		  }
		  layer.close(loading);
	  }
	  function getAllProducts() {
		  var allProducts = null;
		  var nothing = {};
		  $.ajax({
			  async : false, //设置同步
			  type : 'POST',
			  /*url : '/Shopping/getAllProducts',*/
              url : '/Shopping/getAllProducts',
			  data : nothing,
			  dataType : 'json',
			  success : function(result) {
				  if (result!=null) {
					  allProducts = result.allProducts;
				  }
				  else{
					  layer.alert('查询错误');
				  }
			  },
			  error : function(resoult) {
				  layer.alert('查询错误');
			  }
		  });
		  //划重点划重点，这里的eval方法不同于prase方法，外面加括号
		  allProducts = eval("("+allProducts+")");
		  return allProducts;
	  }

      function getAllProductsandRecommand() {
          var allProducts = null;
          var nothing = {};
          var user = {};
          user.userId = "${currentUser.id}";
          $.ajax({
              async : false, //设置同步
              type : 'POST',
              /*url : '/Shopping/getAllProducts',*/
              url : '/Shopping/getAllProductsandRecomand',
              data : user,
              dataType : 'json',
              success : function(result) {
                  if (result!=null) {
                      allProducts = result.allProducts;
                  }
                  else{
                      layer.alert('查询错误');
                  }
              },
              error : function(resoult) {
                  layer.alert('查询错误');
              }
          });
          //划重点划重点，这里的eval方法不同于prase方法，外面加括号
          allProducts = eval("("+allProducts+")");
          return allProducts;
      }



	  function productDetail(id) {
		  var product = {};
		  var jumpResult = '';
		  product.id = id;
		  $.ajax({
			  async : false, //设置同步
			  type : 'POST',
			  url : '/Shopping/productDetail',
			  data : product,
			  dataType : 'json',
			  success : function(result) {
				  jumpResult = result.result;
			  },
			  error : function(resoult) {
				  layer.alert('查询错误');
			  }
		  });

		  if(jumpResult == "success"){
			  window.location.href = "/Shopping/product_detail";
		  }
	  }

  </script>

	<%--<script type="text/javascript">

        var loading = layer.load(0);

        var productType = new Array;
        productType[1] = "衣服配饰";
        productType[2] = "数码产品";
        productType[3] = "书籍办公";
        productType[4] = "游戏周边";
        productType[5] = "生活用品";
        productType[6] = "化妆用品";
        productType[7] = "运动用品";
        productType[8] = "个性化推荐";

        listProducts();

        function listProducts() {
            var allProduct = getAllProducts();
            var mark = new Array;
            mark[1] = 0;
            mark[2] = 0;
            mark[3] = 0;
            mark[4] = 0;
            mark[5] = 0;
            mark[6] = 0;
            mark[7] = 0;
            for(var i=0;i<allProduct.length;i++){
                var html = "";
                var imgURL = "/Shopping/img/"+allProduct[i].id+".jpg";
                html += '<div class="col-sm-4 col-md-4" >'+
                    '<div class="boxes pointer" onclick="productDetail('+allProduct[i].id+')">'+
                    '<div class="big bigimg">'+
                    '<img src="'+imgURL+'">'+
                    '</div>'+
                    '<p class="product-name">'+allProduct[i].name+'</p>'+
                    '<p class="product-price">¥'+allProduct[i].price+'</p>'+
                    '</div>'+
                    '</div>';
                var id = "productArea"+allProduct[i].type;
                var productArea = document.getElementById(id);
                if(mark[allProduct[i].type] == 0){
                    html ='<hr/><h1>'+productType[allProduct[i].type]+'</h1><hr/>'+html;
                    mark[allProduct[i].type] = 1;
                }
                productArea.innerHTML += html;
            }
            layer.close(loading);
        }
        function getAllProducts() {
            var allProducts = null;
            var nothing = {};
            $.ajax({
                async : false, //设置同步
                type : 'POST',
                url : '/Shopping/getAllProducts',
                data : nothing,
                dataType : 'json',
                success : function(result) {
                    if (result!=null) {
                        allProducts = result.allProducts;
                    }
                    else{
                        layer.alert('查询错误');
                    }
                },
                error : function(resoult) {
                    layer.alert('查询错误');
                }
            });
            //划重点划重点，这里的eval方法不同于prase方法，外面加括号
            allProducts = eval("("+allProducts+")");
            return allProducts;
        }

        function productDetail(id) {
            var product = {};
            var jumpResult = '';
            product.id = id;
            $.ajax({
                async : false, //设置同步
                type : 'POST',
                url : '/Shopping/productDetail',
                data : product,
                dataType : 'json',
                success : function(result) {
                    jumpResult = result.result;
                },
                error : function(resoult) {
                    layer.alert('查询错误');
                }
            });

            if(jumpResult == "success"){
                window.location.href = "/Shopping/product_detail";
            }
        }

	</script>--%>

	<div class="layui-layer-move"></div>


  </body>
</html>