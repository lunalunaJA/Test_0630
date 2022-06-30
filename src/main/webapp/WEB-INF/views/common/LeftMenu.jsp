<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript">

</script>
<title>LeftMenu</title>
</head>
<body>

	<div class="sepage_l_admin">
		<div class="lnbttl" style="background-color: #337ab7; color:#FFFFFF; padding-left:10px;">
		<spring:eval expression="@${lang}['SETTINGS']"/>
		<!-- 
			<c:choose>
				<c:when test="${Authentication.sessOnlyDeptUser.usertype eq '03' or Authentication.sessOnlyDeptUser.usertype eq '04'}">
				설정 관리
				<a href ="${ctxRoot}/DocClass"><img style="margin-top:17px; margin-right:10px; float:right;" src="${image}/iconext/ic_25_setting.png"/></a>
				</c:when>
				<c:otherwise>
				문서관리
				<a href ="${ctxRoot}/DocTrash"><img style="margin-top:17px; margin-right:10px; float:right;" src="${image}/iconext/ic_25_setting.png"/></a>
				</c:otherwise>
			</c:choose>
		 -->
			<a href ="${ctxRoot}/go/docMain"><img style="margin-top:17px; margin-right:5px; float:right;" src="${image}/iconext/ic_25_home.png"/></a>
		</div>		
		<%-- 일반사용자 01, 부서관리자 02, 기업관리자 03, 전체관리자 04 --%>
		<ul id="lnb">
			<c:if test="${Authentication.sessOnlyDeptUser.usertype eq '04'}"><%-- 전체관리자 --%>
				<li id="menu01"><a href="${ctxRoot}/go/OrganMng"><spring:eval expression="@${lang}['COMPANY_MANAGEMENT']"/></a></li>
				<li id="menu12"><a href="${ctxRoot}/go/TaskMng"><spring:eval expression="@${lang}['TASK_MANAGEMENT']" /></a></li>
				<li id="menu13"><a href="${ctxRoot}/go/CabinetMng"><spring:eval expression="@${lang}['CABINET_MANAGEMENT']" /></a></li>
				<li id="menu14"><a href="${ctxRoot}/go/FormatMng"><spring:eval expression="@${lang}['FILE_TYPE_MANAGE']" /></a></li>
			</c:if>
			
			<c:if test="${Authentication.sessOnlyDeptUser.usertype eq '03'}"><%-- 기업관리자 --%>
                <li id="menu01"><a href="${ctxRoot}/go/OrganMng"><spring:eval expression="@${lang}['COMPANY_MANAGEMENT']"/></a></li>
				<li id="menu02"><a href="${ctxRoot}/go/DeptMng"><spring:eval expression="@${lang}['DEPARTMENT_MANAGEMENT']"/></a></li>
				<li id="menu03"><a href="${ctxRoot}/go/GroupUserMng"><spring:eval expression="@${lang}['GROUP_MANAGEMENT']" /></a></li>
				<li id="menu04"><a href="${ctxRoot}/go/NodeMng"><spring:eval expression="@${lang}['COMPANY_FOLDER_BOX']" /></a></li>
				<li id="menu12"><a href="${ctxRoot}/go/TaskMng"><spring:eval expression="@${lang}['TASK_MANAGEMENT']" /></a></li>
				<li id="menu13"><a href="${ctxRoot}/go/CabinetMng"><spring:eval expression="@${lang}['CABINET_MANAGEMENT']" /></a></li>
				<%-- <li id="menu14"><a href="${ctxRoot}/go/FormatMng"><spring:eval expression="@${lang}['FILE_TYPE_MANAGE']" /></a></li> --%>
				<%-- <li id="menu04-2"><a href="${ctxRoot}/go/NodeDocMng"><spring:eval expression="@${lang}['COLLABORATIVE_FOLDER_BOX']" /></a></li> --%>
				<li id="menu05"><a href="${ctxRoot}/go/ClassMng"><spring:eval expression="@${lang}['CLASSIFICATION_MANAGEMENT']" /></a></li>
				<li id="menu06"><a href="${ctxRoot}/go/DocTypeMng"><spring:eval expression="@${lang}['DOC_TYPE_MANAGEMENT']" /></a></li>
				<li id="menu14"><a href="${ctxRoot}/go/FormatMng"><spring:eval expression="@${lang}['FILE_TYPE_MANAGE']" /></a></li>
				<li id="menu10"><a href="${ctxRoot}/go/CodeMng"><spring:eval expression="@${lang}['CODE_MANAGEMENT']" /></a></li>
				<li id="menu15"><a href="${ctxRoot}/go/StatMng"><spring:eval expression="@${lang}['STAT_MANAGEMENT']" /></a></li>
				<li id="menu16"><a href="${ctxRoot}/go/ProcMon"><spring:eval expression="@${lang}['PROCESS_MON']" /></a></li>
               <%--  <li id="menu10"><a href="${ctxRoot}/go/CodeMng2"><spring:eval expression="@${lang}['CODE_MANAGEMENT']" /></a></li> --%>
				<li id="menu11"><a href="${ctxRoot}/go/EnvMng"><spring:eval expression="@${lang}['PREFERENCE']" /></a></li>
			</c:if>
			
			<c:if test="${Authentication.sessOnlyDeptUser.usertype eq '02'}"><%-- 부서관리자 --%>
				<%-- <li id="menu02"><a href="${ctxRoot}/go/DocMng">문서관리</a></li> --%>
				<%-- <li id="menu03"><a href="${ctxRoot}/go/GroupUserMng"><spring:eval expression="@${lang}['GROUP_MANAGEMENT']" /></a></li> --%>
				<li id="menu04-1"><a href="${ctxRoot}/go/NodeDeptMng"><spring:eval expression="@${lang}['DEPARTMENT_FOLDER_BOX']" /></a></li>
				<li id="menu03"><a href="${ctxRoot}/go/GroupUserMng"><spring:eval expression="@${lang}['GROUP']" /></a></li>
            	<li id="menu04"><a href="${ctxRoot}/go/LogMng"><spring:eval expression="@${lang}['LOG']" /></a></li>
			</c:if>
			
			<c:if test="${Authentication.sessOnlyDeptUser.usertype eq '01'}"><%-- 일반사용자 --%>
				<li id="menu03"><a href="${ctxRoot}/go/GroupUserMng"><spring:eval expression="@${lang}['GROUP']" /></a></li>
            	<li id="menu04"><a href="${ctxRoot}/go/LogMng"><spring:eval expression="@${lang}['LOG']" /></a></li>
			</c:if>
		</ul>
	</div>	
</body>
</html>