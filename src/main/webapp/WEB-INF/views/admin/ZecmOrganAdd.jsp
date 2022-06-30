<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<%
	//String[] uAuth = Utility.split(sessUserAuth, "|");
   	session.setAttribute("validLic", true);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>NADi4.0 :: 메인</title>
<link type="text/css" rel="stylesheet" href="${css}/common.css" />
<link type="text/css" rel="stylesheet" href="${css}/jquery-ui-1.11.0.min.css" />
<link type="text/css" rel="stylesheet" href="${css}/jstree.css" />
<script type="text/javascript" src="${js}/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="${js}/jquery-ui-1.11.0.js"></script>
<script type="text/javascript" src="${js}/jquery.noty.packaged.min.js"></script>
<script type="text/javascript" src="${js}/jstree.js"></script>
<script type="text/javascript" src="${js}/common.js"></script>
<script type="text/javascript">
	
</script>

<style>
input[type="text"]:disabled {
	background-color: #EAEAEA;
}

sepage_data {
	overflow: auto;
}

table {
	min-width: 500px;
}

.board_data td {
	min-width: 100px;
}

.sepage_r_admin {
	overflow: auto;
	min-width: 500px;
}

.wrap {
	overflow: auto;
}

th {
	position: sticky;
	top: 0;
	z-index: 2;
}
</style>
<script type="text/javascript">
	var fn_Organ = {
		id : companyid,
		select : {},
		save : function(type) {
			console.log("=== save1");
			if (!$.trim($("#CompanyName").val())) {
				alert("<spring:eval expression="@${msgLang}['ENTER_NAME_ORGANIZATION']"/>");
				$("#CompanyName").focus();
				return;
			}
			console.log("=== save2");
			if (!$.trim($("#CompanyCode").val())) {
				alert("<spring:eval expression="@${msgLang}['ENTER_ORGAN_CODE']"/>");
				$("#CompanyCode").focus();
				return;
			}
			console.log("=== save3");
			var sendData = {};
			sendData.objIsTest = "N";
			sendData.objSkipAcl = true;
			sendData.name = $.trim($("#CompanyName").val());
			sendData.code = $.trim($("#CompanyCode").val());
			sendData.abbrname = $.trim($("#CompanyCode").val());
			sendData.tel = $.trim($("#CompanyTel").val());
			sendData.address = $.trim($("#CompanyAddr").val());
			console.log("=== sendData", sendData);
			
			$.ajax({
				type : 'POST',
				url : "${ctxRoot}/api/organ/company/add",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(sendData),
				success : function(data) {
					console.log("=== data", data);
					if (data.status == "0000") {
						messageNotice(sendData.type);
						fn_Organ.getCompanyList();
					} else {
						alertMessage = (type == 'disable') ? '<spring:eval expression="@${msgLang}['FAILED_AGENCY_INSTITUTION']"/>'
								: '<spring:eval expression="@${msgLang}['FAILED_DELETE_INSTITUTION']"/>';
						alertErr(data.message);
					}
				},
				error : function(request, status, error) {
					alertNoty(request, status, error);
					resetInput();
				},
				complete : function() {
					resetInput();
				}
			});
		},
		disable : function() {
		},
		restore : function() {
		},
		discard : function() {
		},
		getCompanyList : function() {
			var sendData = {
				"objIsTest" : "N",
				"objDebugged" : false,
				"isactive" : "Y"
			};
			$.ajax({
				type : 'POST',
				url : '${ctxRoot}/api/organ/company/list',
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(sendData),
				success : function(data) {
					console.log("===getCompanyList : ", data);
					if (data.status == "0000") {
						if (objectIsEmpty(data.result)) {
							return;
						}
						fn_Organ.select.list = data.result;
						$.each(data.result, function(index, result) {
							var inHtml = "";
							var $tr = $("<tr></tr>");
							if (result.isactive == 'N') {
								$tr.css("opacity", "0.3");
							}
							inHtml += "<td>" + (index + 1) + "</td>";
							inHtml += "<td>" + (result.name) + "</td>";
							inHtml += "<td>" + (result.code) + "</td>";
							inHtml += "<td>" + (result.tel) + "</td>";
							inHtml += "<td>" + (result.address) + "</td>";
							inHtml += "<td>" + ((result.isactive == "Y") ? "<spring:eval expression="@${lang}['USE']"/>"
											: "<spring:eval expression="@${lang}['NOT_USE']"/>") + "</td>";
							$tr.append(inHtml);
							$tr.data('meta', result);
							$("#CompanyList").append($tr);
						});
					} else {
						alert(alertMessage);
					}
				},
				error : function(request, status, error) {
					alertNoty(request, status, error);
				},
				complete : function() {
					resetInput();
				}
			});
		}
	}
	var resetInput = function() {
		$(".board_data input").removeProp("disabled");
		$("input[id^='Com']").val('');
		$("a[id^='btn']").hide();
		$("#btnInit").show();
		$("#btnNew").show();
		fn_Organ.select = {};
	}
	$(document).ready(function() {
		$("#menu01").addClass("on");
		resetInput();

		$("#btnInit").click(function() {
			resetInput();
		});
		$("#btnNew").click(function() {
			if (objectIsEmpty(fn_Organ.select)) {

				
				fn_Organ.save("add");
			} else {
				fn_Organ.save("change");
			}
		});
		$("#btnDel").click(function() {
			fn_Organ.disable();
		});
		$("#btnDis").click(function() {
			fn_Organ.discard();
		});

		//기관 리스트 클릭
		$(document.body).delegate('#CompanyList tr', 'click', function() {
			$("input[id^='Com']").val('');
			var meta = $(this).data("meta");
			fn_Organ.select = meta;
			$("#CompanyCode").prop("disabled", true);
			$("#CompanyName").val(meta.name);
			$("#CompanyCode").val(meta.code);
			$("#CompanyTel").val(meta.tel);
			$("#CompanyAddr").val(meta, address);
			if (result.isactive == "Y") {
				$("#btnDis").show();
			} else {
				$("#btnDel").show();
			}
		});

		//기관 목록 가져오기
		fn_Organ.getCompanyList();
	});

	/* 
	 Name : findOrgan
	 Desc : 기관 리스트에서 기관명이나 기관코드가 같은 item이 있는지 체크
	 Param : type (add, change)
	 */
	function findOrgan(type) {

		if (objectIsEmpty(fn_Organ.select.list)) {
			return "<spring:eval expression="@${msgLang}['NO_LIST_INSTITUTIONS']"/>";
		}

		var info = {
			name : $.trim($("#CompanyName").val()),
			code : $.trim($("#CompanyCode").val())
		};

		//기관이름 체크
		var findName = fn_Organ.select.list.filter(function(x) {
			return x.name === info.name
		})[0];

		console.log("findName : " + findName);
		if (findName) {
			if (type == 'change') {
				return;
			}
			return "<spring:eval expression="@${msgLang}['DUPLICATE_AGENCY_NAME']"/>";
		} else {
			if (type != 'change') {
				//기관코드 체크
				var findCode = fn_Organ.select.list.filter(function(x) {
					return x.code === info.code
				})[0];
				console.log("findCode : " + findCode);
				if (findCode)
					return "<spring:eval expression="@${msgLang}['DUPLICATE_ORGAN_CODE']"/>";
			}
		}
		return '';
	}

	/* 
	 Name : messageNotice
	 Desc : 등록 수정 삭제 폐기 시 해당 정보를 알럿창으로 보여줌
	 Param : type (add change discard disable )
	 */
	function messageNotice(type) {

		var message = '';
		if (type == 'add') {
			message = "<spring:eval expression="@${msgLang}['REGISTERED_AGENCY']"/>";
		} else if (type == 'change') {
			message = "<spring:eval expression="@${msgLang}['MODIFIED_AGENCY_NAME']"/>";
		} else if (type == 'disable') {
			message = "<spring:eval expression="@${msgLang}['DISABLED_CHANGE_AGENCY']"/>";
		} else if (type == 'discard') {
			message = "<spring:eval expression="@${msgLang}['DELETED_AGENCY']"/>";
		}
		if (message) {
			alert(message);
		}
	}
</script>
</head>
<body>
	<input type="hidden" id="CompanyID" value="" />
	<!-- 기관 아이디 -->
	<input type="hidden" id="CompanySeqNo" value="" />
	<!-- 기관 고유 번호 -->
	<div id="wrap">
		<!--header stard-->
		<c:import url="../common/TopPage.jsp" />
		<!--header end-->

		<!--content stard-->
		<div id="container">
			<!--Left Menu stard-->
			<!--Left Menu end-->

			<div>
				<div class="sepage_ttl">
					<img src="${ctxRoot}/resources/images/iconext/icon_arrow.png" style="margin-right: 15px;" />
					<spring:eval expression="@${lang}['COMPANY_MANAGEMENT']" />
				</div>

				<div class="sepage_data">
					<div class="mgt5 cb">
						<div style="float: left; margin-top: 10px;">
							<img src="${ctxRoot}/resources/images/iconext/arrow_icon.png" />
							<span style="font-weight: bold;"> <spring:eval expression="@${lang}['COMPANY_INFO']" /></span>
						</div>

						<div class="listpage_data" style="height: 25px;">
							<div class="fr">
								<a href="#" class="b_btn mgr5" id="btnInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></a>
								<a href="#" class="btn_dg" id="btnNew" style="display: none"><spring:eval expression="@${lang}['SAVE']" /></a>&nbsp;
							</div>
						</div>
					</div>

					<table class="board_data" summary="">
						<tr>
							<th scope="col" style="width: 100px; min-width: 50px;"><spring:eval expression="@${lang}['COMPANY_NAME']" /></th>
							<td scope="col"><input type="text" value="" id="CompanyName" style="width: 100%;" onkeyup='pubByteCheckTextarea(event,20)' />
							</td>

							<th scope="col" style="width: 100px; min-width: 50px;"><spring:eval expression="@${lang}['CODE']" /></th>
							<td scope="col"><input type="text" value="" id="CompanyCode" style="width: 100%;" onkeyup='pubByteCheckTextarea(event,30)' />
							</td>
							<th scope="col" style="width: 100px; min-width: 50px;"><spring:eval expression="@${lang}['ADDRESS']" /></th>
							<td scope="col"><input type="text" value="" id="CompanyAddr" style="width: 100%;" maxlength="100"
								onkeyup='pubByteCheckTextarea(event,500)' /></td>
							<th scope="col" style="width: 110px; min-width: 50px;"><spring:eval expression="@${lang}['TELEPHONE_NUMBER']" /></th>
							<td scope="col"><input type="text" value="" id="CompanyTel" style="width: 100%;" onkeydown='return onlyNumber(event)'
								onkeyup='removeChar(event)' placeholder="<spring:eval expression="@${lang}['ENTER_ONLY_NUMBERS']"/>" />
							</td>
						</tr>
					</table>

					<div style="height: 610px; overflow-y: auto; margin-top: 20px;" class="info_tb">
						<table style="width: 100%;">
							<colgroup>
								<col width="5%">
								<col width="25%">
								<col width="15%">
								<col width="20%">
								<col width="30%">
								<col width="5%">
							</colgroup>
							<thead>
								<tr>
									<th style="min-width: 50px;"><spring:eval expression="@${lang}['ORDER']" /></th>
									<th style="min-width: 100px;"><spring:eval expression="@${lang}['COMPANY_NAME']" /></th>
									<th style="min-width: 100px;"><spring:eval expression="@${lang}['CODE']" /></th>
									<th style="min-width: 100px;"><spring:eval expression="@${lang}['TELEPHONE_NUMBER']" /></th>
									<th style="min-width: 100px;"><spring:eval expression="@${lang}['ADDRESS']" /></th>
									<th style="min-width: 65px;"><spring:eval expression="@${lang}['USE_OR_NOT']" /></th>
								</tr>
							</thead>
							<tbody id="CompanyList" style="overflow: auto;">

							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<!--Footer stard-->
		<c:import url="../common/Footer.jsp" />
		<!--Footer end-->
	</div>
</body>
</html>