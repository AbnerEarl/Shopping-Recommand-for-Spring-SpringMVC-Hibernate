<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="cp" value="${pageContext.request.contextPath}"/>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <!--假装我是代码，代码一定要对齐，对齐，对齐！-->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>基于在线推荐购物系统</title>
    <%--<link href="/Shopping/css/bootstrap.min.css" rel="stylesheet">
    <link href="/Shopping/css/style.css" rel="stylesheet">

    <script src="/Shopping/js/bootstrap.min.js" type="text/javascript"></script>
    <script src="/Shopping/js/layer.js" type="text/javascript"></script>
    <!--[if lt IE 9]>
    <script src="/Shopping/js/html5shiv.min.js"></script>
    <script src="/Shopping/js/js/respond.min.js"></script>
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
<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Online recommendation system</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/Shopping/main">首页</a>
        </div>

        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">

            <ul class="nav navbar-nav navbar-right">
                <c:if test="${empty currentUser}">
                    <li><a href="/Shopping/register" methods="post">注册</a></li>
                    <li><a href="/Shopping/login" methods="post">登录</a></li>
                </c:if>
                <c:if test="${not empty currentUser}">
                    <c:if test="${currentUser.role == 1}">
                        <li><a href="/Shopping/control" methods="post">控制台</a></li>
                    </c:if>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                                ${currentUser.nickName}
                            <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a href="/Shopping/shopping_car">购物车</a></li>
                            <li><a href="/Shopping/shopping_record">订单状态</a></li>
                            <c:if test="${currentUser.role == 1}">
                                <li><a href="/Shopping/shopping_handle">处理订单</a></li>
                            </c:if>
                            <li role="separator" class="divider"></li>
                            <li><a href="/Shopping/amend_info">个人资料修改</a></li>
                            <li><a href="/Shopping/doLogout">注销登录</a></li>
                        </ul>
                    </li>
                </c:if>
            </ul>

            <div class="navbar-form navbar-right">
                <div class="form-group">
                    <input type="text" class="form-control" placeholder="输入商品名称！" id="searchKeyWord"/>
                </div>
                <button class="btn btn-default" onclick="searchProduct();">查找商品</button>
            </div>
        </div>
    </div>
</nav>
<%--<script type="text/javascript">
    function searchProduct() {
        var search = {};
        search.searchKeyWord = document.getElementById("searchKeyWord").value;
        var searchResult = "";
        $.ajax({
            async : false,
            type : 'POST',
            url : '/Shopping/searchPre',
            data : search,
            dataType : 'json',
            success : function(result) {
                searchResult = result.result;
            },
            error : function(result) {
                layer.alert('查询错误');
            }
        });
        if(searchResult == "success")
            window.location.href = "/Shopping/search";
    }
</script>--%>

<script type="text/javascript">
    function searchProduct() {
        var search = {};
        search.searchKeyWord = document.getElementById("searchKeyWord").value;
        var searchResult = "";
        $.ajax({
            async : false,
            type : 'POST',
            url : '/Shopping/searchPre',
            data : search,
            dataType : 'json',
            success : function(result) {
                searchResult = result.result;
            },
            error : function(result) {
                layer.alert('查询错误');
            }
        });
        if(searchResult == "success")
            window.location.href = "/Shopping/search";
    }
</script>

</body>
</html>