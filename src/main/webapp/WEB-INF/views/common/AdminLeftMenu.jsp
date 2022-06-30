<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>AdminLeftMenu</title>
</head>
<body>
	<nav>
		<div class="nav_wrap">
			<ul class="gnb" style="overflow:hidden;padding:18px;">
				<c:if test="${Authentication.sessOnlyDeptUser.usertype eq '01'}"><%-- 일반사용자 --%>
					<li><a href="${ctxRoot}/go/GroupUserMng"><img src="${image}/icon/icon_admin_03.png" alt="개인그릅관리"><span class="foldingMn"><spring:eval expression="@${lang}['GROUP']" /></span></a></li>
					<li><a href="${ctxRoot}/go/LogMng"><img src="${image}/icon/icon_admin_12.png" alt="로그"><span class="foldingMn"><spring:eval expression="@${lang}['LOG']" /></span></a></li>
				</c:if>

				<c:if test="${Authentication.sessOnlyDeptUser.usertype eq '02'}"><%-- 부서관리자 --%>
					<li><a href="${ctxRoot}/go/NodeDeptMng"><img src="${image}/icon/icon_admin_07.png" alt="부서 문서함 관리"><span class="foldingMn"><spring:eval expression="@${lang}['DEPARTMENT_FOLDER_BOX']" /></span></a></li>
					<li><a href="${ctxRoot}/go/GroupUserMng"><img src="${image}/icon/icon_admin_03.png" alt="그룹관리"><span class="foldingMn"><spring:eval expression="@${lang}['GROUP']" /></span></a></li>
				</c:if>

				<c:if test="${Authentication.sessOnlyDeptUser.usertype eq '03'}"><%-- 기업관리자 --%>
					<li><a href="${ctxRoot}/go/OrganMng"><img src="${image}/icon/icon_admin_01.png" alt="기업관리"><span class="foldingMn"><spring:eval expression="@${lang}['COMPANY_MANAGEMENT']"/></span></a></li>
					<li><a href="${ctxRoot}/go/DeptMng"><img src="${image}/icon/icon_admin_02.png" alt="조직관리"><span class="foldingMn"><spring:eval expression="@${lang}['DEPARTMENT_MANAGEMENT']"/></span></a></li>
					<li><a href="${ctxRoot}/go/GroupUserMng"><img src="${image}/icon/icon_admin_03.png" alt="그룹관리"><span class="foldingMn"><spring:eval expression="@${lang}['GROUP_MANAGEMENT']" /></span></a></li>
					<li><a href="${ctxRoot}/go/NodeMng"><img src="${image}/icon/icon_admin_04.png" alt="전사문서함"><span class="foldingMn"><spring:eval expression="@${lang}['COMPANY_FOLDER_BOX']" /></span></a></li>
					<li><a href="${ctxRoot}/go/TaskMng"><img src="${image}/icon/icon_admin_05.png" alt="업무 관리"><span class="foldingMn"><spring:eval expression="@${lang}['TASK_MANAGEMENT']" /></span></a></li>
					<li><a href="${ctxRoot}/go/CabinetMng"><img src="${image}/icon/icon_admin_06.png" alt="캐비닛 곤리"><span class="foldingMn"><spring:eval expression="@${lang}['CABINET_MANAGEMENT']" /></span></a></li>
					<li><a href="${ctxRoot}/go/ClassMng"><img src="${image}/icon/icon_admin_07.png" alt="분류체계 관리"><span class="foldingMn"><spring:eval expression="@${lang}['CLASSIFICATION_MANAGEMENT']" /></span></a></li>
					<li><a href="${ctxRoot}/go/DocTypeMng"><img src="${image}/icon/icon_admin_08.png" alt="문서 유형 관리"><span class="foldingMn"><spring:eval expression="@${lang}['DOC_TYPE_MANAGEMENT']" /></span></a></li>
					<li><a href="${ctxRoot}/go/FormatMng"><img src="${image}/icon/icon_admin_09.png" alt="파일 유형 관리"><span class="foldingMn"><spring:eval expression="@${lang}['FORMAT_MANAGE']" /></span></a></li>
					<li><a href="${ctxRoot}/go/CodeMng"><img src="${image}/icon/icon_admin_10.png" alt="코드관리"><span class="foldingMn"><spring:eval expression="@${lang}['CODE_MANAGEMENT']" /></span></a></li>
					<li><a href="${ctxRoot}/go/StatMng"><img src="${image}/icon/icon_admin_11.png" alt="현황 및 통계"><span class="foldingMn"><spring:eval expression="@${lang}['STAT_MANAGEMENT']" /></span></a></li>
					<li><a href="${ctxRoot}/go/ProcMon"><img src="${image}/icon/icon_admin_12.png" alt="프로세스 모니터링"><span class="foldingMn"><spring:eval expression="@${lang}['PROCESS_MON']" /></span></a></li>
					<li><a href="${ctxRoot}/go/EnvMng"><img src="${image}/icon/icon_admin_13.png" alt="환경설정"><span class="foldingMn"><spring:eval expression="@${lang}['PREFERENCE']" /></span></a></li>
					<li><a href="${ctxRoot}/go/LogMng"><img src="${image}/icon/icon_admin_12.png" alt="로그"><span class="foldingMn"><spring:eval expression="@${lang}['LOG']" /></span></a></li>
				</c:if>


				<c:if test="${Authentication.sessOnlyDeptUser.usertype eq '04'}"><%-- 전체관리자 --%>
					<li><a href="${ctxRoot}/go/OrganMng"><img src="${image}/icon/icon_admin_01.png" alt="기업관리"><span class="foldingMn"><spring:eval expression="@${lang}['COMPANY_MANAGEMENT']"/></span></a></li>
					<li><a href="${ctxRoot}/go/TaskMng"><img src="${image}/icon/icon_admin_05.png" alt="업무 관리"><span class="foldingMn"><spring:eval expression="@${lang}['TASK_MANAGEMENT']" /></span></a></li>
					<li><a href="${ctxRoot}/go/CabinetMng"><img src="${image}/icon/icon_admin_06.png" alt="캐비닛 곤리"><span class="foldingMn"><spring:eval expression="@${lang}['CABINET_MANAGEMENT']" /></span></a></li>
					<li><a href="${ctxRoot}/go/FormatMng"><img src="${image}/icon/icon_admin_09.png" alt="파일 유형 관리"><span class="foldingMn"><spring:eval expression="@${lang}['FORMAT_MANAGE']" /></span></a></li>
				</c:if>
				
			</ul>
			<p id="footer" class="foldingMn">
				서울특별시 금천구 벚꽃로 278 SJ테크노빌 704호<br />  
				TEL 02-6244-4000 / FAX 02-6224-4001<br />  
				Copyright © Zenith Solution Technology
			</p>
		</div>
		<div class="nav_btn">
			<img src="${image}/icon/menu_r_02.png" alt="">
		</div>
	</nav>
</body>
</html>