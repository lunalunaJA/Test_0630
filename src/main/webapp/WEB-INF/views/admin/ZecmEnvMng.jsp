<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['PREFERENCE']" /></title>
<link rel="stylesheet" href="${css}/reset.css">
<link rel="stylesheet" href="${css}/common.css">
<link rel="stylesheet" href="${css}/style.css">
<link rel="stylesheet" href="${js}/jquery-ui-1.13.1/jquery-ui.min.css">

<script src="${js}/jquery-1.12.2.min.js"></script>
<script src="${js}/jquery-ui-1.13.1/jquery-ui.min.js"></script>
<script src="${js}/jquery.noty.packaged.min.js"></script>
<script src="${js}/jquery-ui.js"></script>
<script src="${js}/common.js"></script>
<script type="text/javascript">
var inputObjs = "";		// Input 전역 변수
var downValue = "";		// 키 다운시 입력 값
$(document).ready(function() {
	inputObjs = $("input");

	$("input[type='radio']").click(function() {
		var envid = $(this).attr("envid");
		var currentVal = $(this).val();
		changeEnvInfo(envid, currentVal);
	});

	$("input[type='text']").focusout(function() {
		var envid = $(this).attr("envid");
		var currentVal = $(this).val();
		if (downValue !== currentVal && currentVal.length > 0) {
			if (downValue != "") {
				changeEnvInfo(envid, currentVal);
				downValue = "";
			}
		}
	});
	
	//택스트 박스 키인 이벤트
	$("input[type='text']").on("keydown", function() {
		downValue = $(this).val();
	});

	//환경설정 등로정보 조회
	selectEnvList();
});

/*********************************
Name   : selectEnvList
Desc   : 환경설정 등록정보 조회
Param  : 없음
**********************************/
var selectEnvList = function() {
	$.ajax({
		url : "${ctxRoot}/api/system/env/list",
		type : "POST",
		dataType : "json",
		contentType : 'application/json',
		async : false,
		data : JSON.stringify({
			"envid" : ""
			, "companyid" : companyid
			, "userid" : companyid		//시스템 정보만 조회하기
		}),
		success : function(data) {
			//console.log("selectEnvList data :", JSON.stringify(data));
			if (data.status == "0000") {
				if (objectIsEmpty(data.result)) {
					return;
				}
				for (var i = 0; i < data.result.length; i++) {
					setEnv(data.result[i]);
				}
			}
		},
		error : function(request, status, error) {
			alertNoty(request, status, error);
		}
	})
};

/*********************************
Name   : setEnv
Desc   : 서버에서 환경설정 값을 가져와서 값을 셋팅해준다.
Param  : item (환경설정 상세값)
**********************************/
var setEnv = function(item) {
	inputObjs.each(function(index) {
		var name = $(this).attr("name");
		if (item.envkey.indexOf(name) >= 0) {
			var inputType = $(this).attr('type');
			
			//PC 모드 처리
			$(this).attr("envid", item.envid);
			$(this).prop('disabled', (item.editable == 'N'));
			if (inputType == "radio") {
				$(":input[name='" + name + "']:input[value='" + item.setval + "']").prop('checked', true);
			} else {
				$("input[name='" + name + "']").val(item.setval);
			}
			
			//모바일 모드 처리
			$("input[name='MOB" + name + "']").attr("envid", item.envid);
			$("input[name='MOB" + name + "']").prop('disabled', (item.editable == 'N'));
			if (inputType == "radio") {
				$(":input[name='MOB" + name + "']:input[value='" + item.setval + "']").prop('checked', true);
			} else {
				$("input[name='MOB" + name + "']").val(item.setval);
			}
			
			return;
		}
	});
}

/*********************************
Name   : changeEvnInfo
Desc   : 설정된 환경 값을 변경
Param  : id(envid), val(input value)
**********************************/
var changeEnvInfo = function(id, val) {
	var sendData = {
		"envid" : id,
		"setval" : val
	};
	//console.log("changeEnvInfo sendData :", JSON.stringify(sendData));
	$.ajax({
		type : 'POST',
		url : "${ctxRoot}/api/system/env/change",
		dataType : 'JSON',
		contentType : 'application/json',
		async : false,
		data : JSON.stringify(sendData),
		success : function(data) {
			//console.log("changeEnvInfo data :", JSON.stringify(data));
			if (data.status == "0000") {
				selectEnvList();
				alert("<spring:eval expression="@${msgLang}['CHANGED_SETTING_VALUE']"/>");
			} else {
				alert(data.status);
			}
		},
		error : function(request, status, error) {
			alertNoty(request, status, error);
		}
	});
}

</script>
</head>
<body>

	<!--header stard-->
	<c:import url="../common/TopPage.jsp" />
	<!--header end-->
	<main>
		<div class="flx">
			<c:import url="../common/AdminLeftMenu.jsp" />
		
			<section id="content">
				<div class="innerWrap innerWrap_scroll" style="overflow:auto;">
					<div class="full-content">
						<h2 class="pageTit"><img src="${image}/icon/icon_b04.png" alt=""><spring:eval expression="@${lang}['PREFERENCE']" /></h2>
						<div class="wdt100_Env">
							<h3 class="innerTit"><spring:eval expression="@${lang}['AUTHORITY']" /></h3>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="33%">
									<col width="34%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['CONTENT_AUTHORITY_OR_NOT']" /></th>
									<th></th>
									<th></th>
								</thead>
								<tbody>
									<tr>
										<td>
											<input type="radio" value="Y" name="CONTENTACL_YN" id="CONTENTACL_YN_Y" /><spring:eval expression="@${lang}['YES']" /> &nbsp;
											<input type="radio" value="N" name="CONTENTACL_YN" id="CONTENTACL_YN_N"	checked /><spring:eval expression="@${lang}['NO']" />
										</td>
										<td></td>
										<td></td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['CONTENT_AUTHORITY_OR_NOT']" /></p>
								<input type="radio" value="Y" name="MOBCONTENTACL_YN" id="MOBCONTENTACL_YN_Y" /><spring:eval expression="@${lang}['YES']" /> &nbsp;
								<input type="radio" value="N" name="MOBCONTENTACL_YN" id="MOBCONTENTACL_YN_N"	checked /><spring:eval expression="@${lang}['NO']" />
							</div>                                
						</div> 
						<div class="wdt100_Env">
							<h3 class="innerTit"><spring:eval expression="@${lang}['FILE']" /></h3>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="33%">
									<col width="34%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['CIPHER_OR_NOT']" /></th>
									<th></th>
									<th></th>
								</thead>
								<tbody>
									<tr>
										<td>
											<input type="radio" value="Y" name="ENCRYPTION_YN" id="ENCRYPTION_YN_Y" /><spring:eval expression="@${lang}['YES']" /> &nbsp; 
											<input type="radio" value="N" name="ENCRYPTION_YN" id="ENCRYPTION_YN_N" checked /> <spring:eval expression="@${lang}['NO']" />										
										</td>
										<td></td>
										<td></td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['CIPHER_OR_NOT']" /></p>
								<input type="radio" value="Y" name="MOBENCRYPTION_YN" id="MOBENCRYPTION_YN_Y" /><spring:eval expression="@${lang}['YES']" /> &nbsp; 
								<input type="radio" value="N" name="MOBENCRYPTION_YN" id="MOBENCRYPTION_YN_N" checked /> <spring:eval expression="@${lang}['NO']" />										
							</div>                                
						</div> 
						<div class="wdt100_Env">
							<h3 class="innerTit"><spring:eval expression="@${lang}['VERSION']" /></h3>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="33%">
									<col width="34%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['VERSION_OR_NOT']" /></th>
									<th><spring:eval expression="@${lang}['UPPER_VERSION_OR_NOT']" /></th>
									<th><spring:eval expression="@${lang}['VERSION_UP_BASEED_ON_THE_SAME_FILE_HASH_VALUE']" /></th>
								</thead>
								<tbody>
									<tr>
										<td>
											<input type="radio" value="Y" name="VERSION_YN" id="VERSION_YN_Y" /> <spring:eval expression="@${lang}['YES']" /> &nbsp; 
											<input type="radio"	value="N" name="VERSION_YN" id="VERSION_YN_N" checked /> <spring:eval expression="@${lang}['NO']" />
										</td>
										<td>
											<input type="radio" value="Y" name="VERSION_UPONLYHIGH_YN" id="VERSION_UPONLYHIGH_YN_Y" /> <spring:eval expression="@${lang}['YES']" /> &nbsp; 
											<input type="radio"	value="N" name="VERSION_UPONLYHIGH_YN" id="VERSION_UPONLYHIGH_YN_N" checked /> <spring:eval expression="@${lang}['NO']" />
										</td>
										<td>
											<input type="radio" value="Y" name="VERSION_UPWITHNOSAMEHASH_YN" id="VERSION_UPWITHNOSAMEHASH_YN_Y" /> <spring:eval expression="@${lang}['USE']" /> &nbsp; 
											<input type="radio"	value="N" name="VERSION_UPWITHNOSAMEHASH_YN" id="VERSION_UPWITHNOSAMEHASH_YN_N" checked /> <spring:eval expression="@${lang}['NOT_USE']" />
										</td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['VERSION_OR_NOT']" /></p>
								<input type="radio" value="Y" name="MOBVERSION_YN" id="MOBVERSION_YN_Y" > <spring:eval expression="@${lang}['YES']" /> &nbsp; 
								<input type="radio"	value="N" name="MOBVERSION_YN" id="MOBVERSION_YN_N" checked > <spring:eval expression="@${lang}['NO']" /><br>
								<p><spring:eval expression="@${lang}['UPPER_VERSION_OR_NOT']" /></p>
								<input type="radio" value="Y" name="MOBVERSION_UPONLYHIGH_YN" id="MOBVERSION_UPONLYHIGH_YN_Y" > <spring:eval expression="@${lang}['YES']" /> &nbsp; 
								<input type="radio"	value="N" name="MOBVERSION_UPONLYHIGH_YN" id="MOBVERSION_UPONLYHIGH_YN_N" checked > <spring:eval expression="@${lang}['NO']" /><br>
								<p><spring:eval expression="@${lang}['VERSION_UP_BASEED_ON_THE_SAME_FILE_HASH_VALUE']" /></p>
								<input type="radio" value="Y" name="MOBVERSION_UPWITHNOSAMEHASH_YN" id="MOBVERSION_UPWITHNOSAMEHASH_YN_Y" > <spring:eval expression="@${lang}['USE']" /> &nbsp; 
								<input type="radio"	value="N" name="MOBVERSION_UPWITHNOSAMEHASH_YN" id="MOBVERSION_UPWITHNOSAMEHASH_YN_N" checked > <spring:eval expression="@${lang}['NOT_USE']" />								
							</div>  
						</div> 
						<!-- 기능 미구현 주석 처리 
						<div class="wdt100_Env">
							<h3 class="innerTit"><spring:eval expression="@${lang}['INTEGRITY']" /></h3>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="33%">
									<col width="34%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['DOC_INTERGIRTY_LIST']" /></th>
									<th></th>
									<th></th>
								</thead>
								<tbody>
									<tr>
										<td>
											<input type="radio" value="1" name="DOC_INTEGRITY" id="DOC_INTEGRITY_1" />PK&nbsp; 
											<input type="radio" value="2" name="DOC_INTEGRITY" id="DOC_INTEGRITY_2" checked /> <spring:eval expression="@${lang}['DOC_NO']" />
										</td>
										<td></td>
										<td></td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['DOC_INTERGIRTY_LIST']" /></p>
								<input type="radio" value="1" name="MOBDOC_INTEGRITY" id="MOBDOC_INTEGRITY_1" />PK&nbsp; 
								<input type="radio" value="2" name="MOBDOC_INTEGRITY" id="MOBDOC_INTEGRITY_2" checked /> <spring:eval expression="@${lang}['DOC_NO']" />
							</div>                                
						</div>  -->
						<div class="wdt100_Env">
							<h3 class="innerTit"><spring:eval expression="@${lang}['LIST']" /></h3>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="33%">
									<col width="34%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['RECENT_LIST_INQUIRY_DATE']" /></th>
									<th><spring:eval expression="@${lang}['LISTS_PER_PAGE']" /></th>
									<th><spring:eval expression="@${lang}['LIST_QUERY_TYPE']" /></th>
								</thead>
								<tbody>
									<tr>
										<td>
											<input type="text" value="" name="LIST_RECENT_DAY" id="LIST_RECENT_DAY" onkeydown='return onlyNumber(event)' maxlength="2"> <spring:eval expression="@${lang}['Day']" />
										</td>
										<td>
											<input type="text" value="" name="CNT_PER_PAGE" id="CNT_PER_PAGE" onkeydown='return onlyNumber(event)' maxlength="3">
										</td>
										<td>
											<input type="radio" value="A" name="LIST_QUERY_OBJECT" id="LIST_QUERY_OBJECT_A" />
											<spring:eval expression="@${lang}['THE_ENTIRE']" />&nbsp&nbsp
											<input type="radio" value="B" name="LIST_QUERY_OBJECT" id="LIST_QUERY_OBJECT_B" />
											<spring:eval expression="@${lang}['BUNDLE']" />&nbsp&nbsp
											<input type="radio" value="F" name="LIST_QUERY_OBJECT" id="LIST_QUERY_OBJECT_F" checked />
											<spring:eval expression="@${lang}['FILE']" />
										</td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['RECENT_LIST_INQUIRY_DATE']" /></p>
								<input type="text" value="" name="MOBLIST_RECENT_DAY" id="MOBLIST_RECENT_DAY" maxlength="2"> <spring:eval expression="@${lang}['Day']" />
								<p><spring:eval expression="@${lang}['LISTS_PER_PAGE']" /></p>
								<input type="text" value="" name="MOBCNT_PER_PAGE" id="MOBCNT_PER_PAGE" maxlength="3">
								<p><spring:eval expression="@${lang}['LIST_QUERY_TYPE']" /></p>
								<input type="radio" value="A" name="MOBLIST_QUERY_OBJECT" id="MOBLIST_QUERY_OBJECT_A" />
								<spring:eval expression="@${lang}['THE_ENTIRE']" />&nbsp&nbsp
								<input type="radio" value="B" name="MOBLIST_QUERY_OBJECT" id="MOBLIST_QUERY_OBJECT_B" />
								<spring:eval expression="@${lang}['BUNDLE']" />&nbsp&nbsp
								<input type="radio" value="F" name="MOBLIST_QUERY_OBJECT" id="MOBLIST_QUERY_OBJECT_F" checked />
								<spring:eval expression="@${lang}['FILE']" />
							</div>                                
						</div> 
						<!-- 기능 미구현 주석 처리 
						<div class="wdt100_Env">
							<h3 class="innerTit"><spring:eval expression="@${lang}['DBMS']" /></h3>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="33%">
									<col width="34%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['JSON_OR_NOT']" /></th>
									<th></th>
									<th></th>
								</thead>
								<tbody>
									<tr>
										<td>
											<input type="radio" value="Y" name="ALLOW_JSON" id="ALLOW_JSON_Y" /> <spring:eval expression="@${lang}['YES']" /> &nbsp; 
											<input type="radio" value="N" name="ALLOW_JSON" id="ALLOW_JSON_N" checked /> <spring:eval expression="@${lang}['NO']" />
										</td>
										<td></td>
										<td></td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['JSON_OR_NOT']" /></p>
								<input type="radio" value="Y" name="MOBALLOW_JSON" id="MOBALLOW_JSON_Y" /> <spring:eval expression="@${lang}['YES']" /> &nbsp; 
								<input type="radio" value="N" name="MOBALLOW_JSON" id="MOBALLOW_JSON_N" checked /> <spring:eval expression="@${lang}['NO']" />
							</div>                                
						</div> -->
						<div class="wdt100_Env">
							<h3 class="innerTit"><spring:eval expression="@${lang}['LOG']" /></h3>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="33%">
									<col width="34%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['ACCESS_LOG_OR_NOT']" /></th>
									<th><spring:eval expression="@${lang}['CONTENT_LOG_OR_NOT']" /></th>
									<th><spring:eval expression="@${lang}['SYSTEM_LOG_OR_NOT']" /></th>
								</thead>
								<tbody>
									<tr>
										<td>
											<input type="radio" value="Y" name="LOG_ACCESS_YN" id="LOG_ACCESS_YN_Y" /><spring:eval expression="@${lang}['YES']" /> &nbsp; 
											<input type="radio" value="N" name="LOG_ACCESS_YN" id="LOG_ACCESS_YN_N" checked /> <spring:eval expression="@${lang}['NO']" />
										</td>
										<td>
											<input type="radio" value="Y" name="LOG_CONTENT_YN" id="LOG_CONTENT_YN_Y" /> <spring:eval expression="@${lang}['YES']" /> &nbsp; 
											<input type="radio"value="N" name="LOG_CONTENT_YN" id="LOG_CONTENT_YN_N" checked /><spring:eval expression="@${lang}['NO']" />
										</td>
										<td>
											<input type="radio" value="Y" name="LOG_SYSTEM_YN" id="LOG_SYSTEM_YN_Y" /><spring:eval expression="@${lang}['YES']" /> &nbsp;
											<input type="radio" value="N" name="LOG_SYSTEM_YN" id="LOG_SYSTEM_YN_N" checked /><spring:eval expression="@${lang}['NO']" />
										</td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['ACCESS_LOG_OR_NOT']" /></p>
								<input type="radio" value="Y" name="MOBLOG_ACCESS_YN" id="MOBLOG_ACCESS_YN_Y" /><spring:eval expression="@${lang}['YES']" /> &nbsp; 
								<input type="radio" value="N" name="MOBLOG_ACCESS_YN" id="MOBLOG_ACCESS_YN_N" checked /> <spring:eval expression="@${lang}['NO']" /><br>
								<p><spring:eval expression="@${lang}['CONTENT_LOG_OR_NOT']" /></p>
								<input type="radio" value="Y" name="MOBLOG_CONTENT_YN" id="MOBLOG_CONTENT_YN_Y" /> <spring:eval expression="@${lang}['YES']" /> &nbsp; 
								<input type="radio"value="N" name="MOBLOG_CONTENT_YN" id="MOBLOG_CONTENT_YN_N" checked /><spring:eval expression="@${lang}['NO']" /><br>
								<p><spring:eval expression="@${lang}['SYSTEM_LOG_OR_NOT']" /></p>
								<input type="radio" value="Y" name="MOBLOG_SYSTEM_YN" id="MOBLOG_SYSTEM_YN_Y" /><spring:eval expression="@${lang}['YES']" /> &nbsp;
								<input type="radio" value="N" name="MOBLOG_SYSTEM_YN" id="MOBLOG_SYSTEM_YN_N" checked /><spring:eval expression="@${lang}['NO']" />								
							</div>                                
						</div> 
						<!-- 기능 미구현 주석 처리 
						<div class="wdt100_Env">
							<h3 class="innerTit"><spring:eval expression="@${lang}['E-MAIL']" /></h3>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="33%">
									<col width="34%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['SEND_AUTHORIZED_MAIL_OR_NOT']" /></th>
									<th></th>
									<th></th>
								</thead>
								<tbody>
									<tr>
										<td>
											<input type="radio" value="Y" name="MAIL_SEND_APPROVAL_YN" id="MAIL_SEND_APPROVAL_YN_Y" /><spring:eval expression="@${lang}['YES']" />&nbsp;
											<input type="radio" value="N" name="MAIL_SEND_APPROVAL_YN" id="MAIL_SEND_APPROVAL_YN_N" checked /><spring:eval expression="@${lang}['NO']" />
										</td>
										<td>
										</td>
										<td>
										</td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['SEND_AUTHORIZED_MAIL_OR_NOT']" /></p>
								<input type="radio" value="Y" name="MOBMAIL_SEND_APPROVAL_YN" id="MOBMAIL_SEND_APPROVAL_YN_Y" /><spring:eval expression="@${lang}['YES']" />&nbsp;
								<input type="radio" value="N" name="MOBMAIL_SEND_APPROVAL_YN" id="MOBMAIL_SEND_APPROVAL_YN_N" checked /><spring:eval expression="@${lang}['NO']" />
							</div>                                
						</div>  -->
					</div>
				</div><!--innerWrap//-->
			</section>
		</div>
	</main>
</body>
</html>