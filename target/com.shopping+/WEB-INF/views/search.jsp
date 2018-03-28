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
    <title>基于在线推荐购物系统 搜索结果</title>
	  <%--<link href="/Shopping/css/bootstrap.min.css" rel="stylesheet">
	  <link href="/Shopping/css/style.css" rel="stylesheet">

	  <script src="/Shopping/js/jquery.min.js" type="text/javascript"></script>
	  <script src="/Shopping/js/bootstrap.min.js" type="text/javascript"></script>
	  <script src="/Shopping/js/layer.js" type="text/javascript"></script>
    <!--[if lt IE 9]>
      <script src="/Shopping/js/html5shiv.min.js"></script>
      <script src="/Shopping/js/respond.min.js"></script>
    <![endif]-->--%>



	  <link href="/Shopping/css/bootstrap.min.css" rel="stylesheet">
	  <link href="/Shopping/css/style.css" rel="stylesheet">

	  <script src="/Shopping/js/jquery.min.js" type="text/javascript"></script>
	  <script src="/Shopping/js/bootstrap.min.js" type="text/javascript"></script>
	  <script src="/Shopping/js/layer.js" type="text/javascript"></script>
	  <!--[if lt IE 9]>
	  <script src="/Shopping/js/html5shiv.min.js"></script>
	  <script src="/Shopping/js/js/respond.min.js"></script>
	  <![endif]-->


  </head>
  <body>
    <!--导航栏部分-->
	<jsp:include page="include/header.jsp"/>

	<!-- 中间内容 -->
	<div class="container">
		<div class="row margin-t">
			<div class="form-horizontal">
				<div class="col-md-2 col-sm-2"></div>
				<div class="form-group form-group-lg col-sm-6 col-md-6">
					<input type="text" class="form-control" id="newSearchKeyWord" placeholder="请输入商品的关键词！" />
				</div>
				<button class="btn btn-primary btn-lg col-sm-2 col-md-2 big-button" onclick="searchPre()">查找商品</button>
			</div>
		</div>
	</div>
	<div class="container">
		<div class="row">
			<div id = "searchResultArea">
            </div>
		</div>

	</div>


	<!-- 尾部 -->
	<jsp:include page="include/foot.jsp"/>

<%--  <script type="text/javascript">

	  searchInit();

	  function searchInit() {
		  var searchKeyWord = "${searchKeyWord}";
		  if(searchKeyWord != "" && searchKeyWord != undefined && searchKeyWord != null){
			  updateList(searchKeyWord);
		  }
	  }

	  function searchPre() {
		  var searchKeyWord = document.getElementById("newSearchKeyWord").value;
		  updateList(searchKeyWord);
	  }

	  function search(searchKeyWord) {
		  var search = {};
		  search.searchKeyWord = searchKeyWord;
		  var searchResult = "";
		  $.ajax({
			  async : false,
			  type : 'POST',
			  url : '/Shopping/searchProduct',
			  data : search,
			  dataType : 'json',
			  success : function(result) {
				  searchResult = result.result;
			  },
			  error : function() {
				  layer.alert('查询错误');
			  }
		  });
		  searchResult = eval("("+searchResult+")");
		  return searchResult;
	  }

	  function updateList(searchKeyWord) {
		  var searchResultArea = document.getElementById("searchResultArea");
		  var searchResult = search(searchKeyWord);
		  var html = "";
		  searchResultArea.innerHTML = "";
		  for(var i=0;i<searchResult.length;i++){
			  var imgURL = "/Shopping/img/"+searchResult[i].id+".jpg";
			  html+= '<div class="col-sm-3 col-md-3 search-padding">'+
					  '<div class="boxes pointer" onclick="productDetail('+searchResult[i].id+')">'+
					  '<div class="big bigimg">'+
					  '<img src="'+imgURL+'"/>'+
					  '</div>'+
					  '<p class="product-name">'+searchResult[i].name+'</p>'+
					  '<p class="product-price">¥'+searchResult[i].price+'</p>'+
					  '</div>'+
					  '</div>'+
                      '<div class="col-sm-1 col-md-1"></div>';
		  }
		  searchResultArea.innerHTML += html;
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
			  error : function() {
				  layer.alert('查询错误');
			  }
		  });

		  if(jumpResult == "success"){
			  window.location.href = "/Shopping/product_detail";
		  }
	  }
  </script>--%>

	<script type="text/javascript">

        searchInit();

        function searchInit() {
            var searchKeyWord = "";
            if(searchKeyWord != "" && searchKeyWord != undefined && searchKeyWord != null){
                updateList(searchKeyWord);
            }
        }

        function searchPre() {
            var searchKeyWord = document.getElementById("newSearchKeyWord").value;
            updateList(searchKeyWord);
        }

        function search(searchKeyWord) {
            var search = {};
            search.searchKeyWord = searchKeyWord;
            var searchResult = "";
            $.ajax({
                async : false,
                type : 'POST',
                url : '/Shopping/searchProduct',
                data : search,
                dataType : 'json',
                success : function(result) {
                    searchResult = result.result;
                },
                error : function() {
                    layer.alert('查询错误');
                }
            });
            searchResult = eval("("+searchResult+")");
            return searchResult;
        }

        function updateList(searchKeyWord) {
            var searchResultArea = document.getElementById("searchResultArea");
            var searchResult = search(searchKeyWord);
            var html = "";
            searchResultArea.innerHTML = "";
            for(var i=0;i<searchResult.length;i++){
                var imgURL = "/Shopping/img/"+searchResult[i].id+".jpg";
                html+= '<div class="col-sm-3 col-md-3 search-padding">'+
                    '<div class="boxes pointer" onclick="productDetail('+searchResult[i].id+')">'+
                    '<div class="big bigimg">'+
                    '<img src="'+imgURL+'"/>'+
                    '</div>'+
                    '<p class="product-name">'+searchResult[i].name+'</p>'+
                    '<p class="product-price">¥'+searchResult[i].price+'</p>'+
                    '</div>'+
                    '</div>'+
                    '<div class="col-sm-1 col-md-1"></div>';
            }
            searchResultArea.innerHTML += html;
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
                error : function() {
                    layer.alert('查询错误');
                }
            });

            if(jumpResult == "success"){
                window.location.href = "/Shopping/product_detail";
            }
        }
	</script>


  </body>
</html>