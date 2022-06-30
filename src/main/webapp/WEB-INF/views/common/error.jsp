<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Content-Script-Type" content="text/javascript" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>Error</title>
</head>
<body>
<div class="wrap01">
    <div> 
        <h1>ERROR</h1>
        <div>
            <div>
                <h2>${code}</h2>
                <c:choose>
                	<c:when test="${code eq 301}">
                	<p>Session Closed.<br/><br/>
                	</c:when>
                	<c:when test="${code eq 404}">
                	<p>Page Is Not Found.<br/><br/>
                	</c:when>
                	<c:when test="${code eq 500}">
                	<p>Internal Server Error.<br/><br/>
                	</c:when>
                	<c:otherwise>
                	<p>Error.<br/><br/>
                	</c:otherwise>                
                </c:choose>                
               		Click the home button to move to the login page.<br /><br/><br/>               		              		
                <p class="ption mgt45"><a href="${ctxRoot }/go/login" class="btn_dft_s02 w60">HOME</a></p>
            </div>
        </div>
    </div>
<!--// wrap -->
</div>
</body>
</html>