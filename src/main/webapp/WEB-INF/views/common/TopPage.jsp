<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="./CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<script type="text/javascript">
	var flag = false;
	var currDeptid = "";
	
	$(document).ready(function() {
		currDeptid = deptid;
		
		$("#current_passwdImg").on('click', function(e) {
			if ($("#PopupNewPassword").attr('type') == "password") {
				$("#PopupNewPassword").attr('type', 'text');
				$("#current_passwdImg").attr('src', '${image}/icon/eye_off_icon.png');
				$("#current_passwdImg").css('width', '30px');
				$("#PopupNewPassword").css('height', '35px');
			} else {
				$("#PopupNewPassword").attr('type', 'password');
				$("#current_passwdImg").attr('src', '${image}/icon/eye_icon.png');
				$("#current_passwdImg").css('width', '30px');
				$("#PopupNewPassword").css('height', '35px');
			}
		});

		$("#confirm_new_passwdImg").on('click', function(e) {
			if ($("#PopupConfirmNewPassword").attr('type') == "password") {
				$("#PopupConfirmNewPassword").attr('type', 'text');
				$("#confirm_new_passwdImg").attr('src', '${image}/icon/eye_off_icon.png');
				$("#confirm_new_passwdImg").css('width', '30px');
				$("#PopupConfirmNewPassword").css('height', '35px');
			} else {
				$("#PopupConfirmNewPassword").attr('type', 'password');
				$("#confirm_new_passwdImg").attr('src', '${image}/icon/eye_icon.png');
				$("#confirm_new_passwdImg").css('width', '30px');
				$("#PopupConfirmNewPassword").css('height', '35px');
			}
		});

		$("#PopupNewPassword").keyup(function(e) {
			var inputBox = $("#PopupNewPassword").val();
			var pattern = /\s/g;
			var koreanExp = /[ㄱ-ㅎㅏ-ㅣ가-힣]/g;
			if (HangulCheck(inputBox))
				$("#PopupNewPassword").val(inputBox.replace(koreanExp, ""));

			if (inputBox.match(pattern)) {
				$("#PopupNewPassword").val("");
				if (flag == false) {
					alert("<spring:eval expression="@${msgLang}['NO_SPACE_KEY']" />");
					flag = true;
				}
				setTimeout(function() {
					flag = false;
				}, 2000)
			}
		});

		$("#PopupNewPassword").keypress(function(e) {
			CapsLockCheck(e);
		});

		$("#PopupConfirmNewPassword").keyup(function(e) {
			var inputBox = $("#PopupConfirmNewPassword").val();
			var pattern = /\s/g;
			var koreanExp = /[ㄱ-ㅎㅏ-ㅣ가-힣]/g;
			if (HangulCheck(inputBox))
				$("#PopupConfirmNewPassword").val(inputBox.replace(koreanExp, ""));

			if (inputBox.match(pattern)) {
				$("#PopupConfirmNewPassword").val("");
				if (flag == false) {
					alert("<spring:eval expression="@${msgLang}['NO_SPACE_KEY']" />");
					flag = true;
				}
				setTimeout(function() {
					flag = false;
				}, 2000)
			}
		});

		$("#PopupConfirmNewPassword").keypress(function(e) {
			CapsLockCheck(e);
		});
		
		// 겸직여부 조회
		chkMultiDept();
		
		$("#myPage").click(function(){
			if($(".popupContent").css("display") ==='none'){
				$(".popupContent").show();
			}else{
				$(".popupContent").hide();
			}
		})

		//관리자 페이지 호출시 시작 페이지 분기
		$("#adminhome").click(function(){
			if(userType ==='01'){		// 사용자
				location.href = "${ctxRoot}/go/GroupUserMng?lang=${sysLang }";
			}else if(userType ==='02'){ //부서관리자
				location.href = "${ctxRoot}/go/NodeDeptMng?lang=${sysLang }";
			}else if(userType ==='03'){ //기관 관리자
				location.href = "${ctxRoot}/go/OrganMng?lang=${sysLang }";
			}else if(userType ==='04'){ //시스템 관리자
				location.href = "${ctxRoot}/go/OrganMng?lang=${sysLang }";
			}
		})
		
		
		$("#Manual").click(function(){
			window.open("${ctxRoot}/go/pdfManual?type="+userType, "PDF Viewer", 'status=no, toolbar=no, menubar=no, scrollbars=no, resizable=no' );
		})
		
		
	});

	//로그아웃처리
	var logOut = function() {
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/auth/disconnect',
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify({
				objIsTest : "N",
				objCompanyid : "${Authentication.objCompanyid}",
				objLoginid : "${Authentication.objLoginid}"
			}),
			success : function(data) {
				if (data.status == "0000") {
					location.href = "${ctxRoot}/go/login";
				} else {
					alertErr(data.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		});
	}
	
	
	var pwdChange = function(){
		$("#PopupNewPassword").empty();
		$("#PopupConfirmNewPassword").empty();
		$(".bg").fadeIn();
		$("#udocmodal").fadeIn();
	}
	
	var popClose = function(){
		$(".bg").fadeOut();
		$("#udocmodal").fadeOut();
	}
	
	var userEdit = function() {
		var data = {};
		var alphanumeric = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{6,12}$/; //숫자 + 영문 체크
		var engSpNum = /(?=.*\d{1,50})(?=.*[~`!@#$%\^&*()-+=]{1,50})(?=.*[a-zA-Z]{2,50}).{6,12}$/; //숫자 + 영문 + 특문 체크

		var currentPasswd = $("#PopupNewPassword").val();
		var confirmPasswd = $("#PopupConfirmNewPassword").val();
		if (currentPasswd.length == 0 || confirmPasswd.length == 0) {
			alert("<spring:eval expression="@${msgLang}['ENTER_PASSWORD']" />");
			return;
		}
		var pattern1 = /[0-9]/; // 숫자 
		var pattern2 = /[a-zA-Z]/; // 문자 
		var pattern3 = /[~`!@#$%\^&*()-+=]/; // 특수문자

		var num = currentPasswd.search(pattern1);
		var eng = currentPasswd.search(pattern2);
		var spe = currentPasswd.search(pattern3);

		if ((num < 0 && eng < 0) || (eng < 0 && spe < 0) || (spe < 0 && num < 0)
				|| !(currentPasswd.length >= 6 && currentPasswd.length <= 12)) {
			alert("<spring:eval expression="@${msgLang}['PASSWD_RULE']" />");
			return false;
		}
		if (currentPasswd == confirmPasswd) {
			data = {
				"userid" : $.trim("${Authentication.sessUser.userid}"),
				"passwd" : $.trim($("#PopupNewPassword").val()),
				"companyid" : companyid,
				"empno" : $.trim($("#userEmpNo").text()),
				"email" : $("#userEmail").text(),
				"loginid" : $.trim($("#userId").text()),
				"name" : $.trim($("#userName").text())
			}
		} else {
			alert("<spring:eval expression="@${msgLang}['INVALID_PASSWORD']" />");
			return;
		}
		noty({
			layout : "center",
			text : "<spring:eval expression="@${msgLang}['DO_YOU_MODIFY_USER']"/>",
			buttons : [{
						addClass : 'b_btn',
						text : "Ok",
						onClick : function($noty) {
							$noty.close();
							console.log("PWD CHANGE data", data);
							$.ajax({type : 'POST',
								url : "${ctxRoot}/api/organ/user/changepwd",
								dataType : 'json',
								contentType : 'application/json',
								async : false,
								data : JSON.stringify(data),
								success : function(data) {
									console.log("data : ", data);
									if (data.status == "0000") {
										alert("<spring:eval expression="@${msgLang}['OPERATION_IS_COMPLETED']" />");
										popClose();
									} else {
										alertErr(data.message);
									}
								},
								error : function(request, status, error) {
									alertNoty(request, status, error);
								}
							});
						}
					}, {
						addClass : 'btbase',
						text : "Cancel",
						onClick : function($noty) {
							$noty.close();
						}
					} ],
			type : "information",
			killer : true
		});
	}
	var ChangePasswd = function() {
		$("#Uhead").text("<spring:eval expression="@${lang}['CHANGE_PASSWORD']" />");
		$("#btnChangePassword").hide();
		$("#btnUEditSave").show();
		$("#current_passwd").show();
		$("#confirm_new_passwd").show();
	};

	var CapsLockCheck = function(e) {
		var text = String.fromCharCode(e.which);
		if (text.toUpperCase() === text && text.toLowerCase() !== text && !e.shiftKey) {
			if (flag == false) {
				alert("<spring:eval expression="@${msgLang}['CPASLOCK_ON']" />");
				flag = true;
			}
			setTimeout(function() {
				flag = false;
			}, 2000)
		}
	};

	var HangulCheck = function(inputKey) {
		var notEngExp = /[^A-Za-z]/g;
		var notEng = notEngExp.test(inputKey);
		return notEng;
	}

	var chkMultiDept = function() {
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/auth/checkMultiDept',
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify({
				objCompanyid : companyid,
				objLoginid : loginid
			}),
			success : function(data) {
				if (!data.message) {
					if (data.result.length > 1) { // 겸직 존재
						var $select = $("<select id='selectDeptid' onchange='changeDept();'></select>");
						var option = "";
						$.each(data.result, function(idx, result) {
							var tmpDeptid = result.zappDept.deptid;
							var tmpDeptName = result.zappDept.name;
							
							if (currDeptid == tmpDeptid) {
								option += "<option id='deptid' value='" + tmpDeptid + "' selected>" + tmpDeptName + "</option>";
							} else {
								option += "<option id='deptid' value='" + tmpDeptid + "'>" + tmpDeptName + "</option>";
							}
						});
						$select.append(option);
						$("#DeptName").append($select);
					} else {
						var deptName = data.result[0].zappDept.name;
						$("#DeptName").append(deptName);						
					}
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		});
	}
	
	var changeDept = function() {
		var depts = document.getElementById("selectDeptid").options;
		var deptId = depts[depts.selectedIndex].value;
		var deptName = depts[depts.selectedIndex].text;
		console.log("=== changeDept deptId:" + deptId + ", deptName:" + deptName);

		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/auth/connect_to_otherjob',
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify({
				objDeptid : deptId
			}),
			success : function(data) {
				console.log("=== connect_to_otherjob data:", data);
				if (data.status == "0000") {
					console.log("=== connect_to_otherjob OK");
					window.location.reload();
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		});
	}
	
	var perGorupSetting = function(){
		$("#openPop").empty();
       	$("#openPop").load("${ctxRoot}/go/PerGroupMng?lang=${sysLang }"); 
       	fn_openPop("openPop");
	}
	
</script>
</head>
<body>
	<header>
		<div class="header_bg"></div>
        <div class="header_wrap">
        <div class="header_logo">
        	<a href="index.html" title="메인페이지로 이동"><img src="${image }/logo/logo.png" alt=""></a>
		</div>
		<div class="header_name mob_none">
			<p><span class="bold">${Authentication.sessDeptUser.zappDept.name} 소속  ${Authentication.sessUser.name}&nbsp;(${Authentication.sessUser.loginid}) </span>님 환영합니다</p>
		</div>
		<div class="header_ui mob_none">
			<c:if test="${Authentication.sessOnlyDeptUser.usertype ne '01'}"><%-- 일반사용자 --%>
				<a href="${ctx }/ecm40/go/docMain?lang=${sysLang }" title="사용자 홈" id="userhome"><img src="${image }/icon/Group 199.png" style="vertical-align: middle;" alt="사용자"></a>		
			    <a href="#none" title="관리자 홈" id="adminhome"><img src="${image }/icon/Group 67.png" style="vertical-align: middle;" alt="관리자"></a>
			</c:if>
			<button type="button" class="logbtn" onclick="logOut()"><spring:eval expression="@${lang}['LOGOUT']" /></button>
			<a href="#none" title="마이페이지" id="myPage"><img src="${image }/icon/Group 48.png" alt="마이페이지"></a>
			<a href="#none" title="마이페이지" id="Manual"><img src="${image }/icon/question-mark.png" alt="" style="width: 36px; height: 36px; transform: translateY(12px);"></a>
		</div>
		<div class="header_bar pc_none">
		    <a href="#none"><img src="${image }/icon/bar.png"></a>
		<div class="myPage">
		    <button type="button" class="closebtn"></button>
		    <ul>
		        <li><a href="#none" title="">가나다라</a></li>
		        <li><a href="#none" title="">가나다라</a></li>
		        <li><a href="#none" title="">가나다라</a></li>
		        <li><a href="#none" title="">가나다라</a></li>
		        <li><a href="#none" title=""onclick="logOut()">로그아웃</a></li>
		            </ul>
		        </div>
		    </div>
		</div>
	</header>
	<div class="popupContent" style="display:none">
		<p class="popTit">이름</p>
		<p class="popSub" id="userName">${Authentication.sessUser.name}</p>
		<p class="popTit">사원번호</p>
		<p class="popSub" id ="userEmpNo">${Authentication.sessUser.empno}</p>
		<p class="popTit">부서명</p>
		<p class="popSub" id = "DeptName"></p>
		<p class="popTit">이메일</p>
		<p class="popSub" id = "userEmail">${Authentication.sessUser.email}</p>
		<p class="popTit">아이디</p>
		<p class="popSub" id = "userId">${Authentication.sessUser.loginid}</p>
		<!--<p class="popTit">개인그룹</p>
		 <button type ="button" style = "height: 21px; padding: 0 17px; border: 1px solid #acacac; border-radius: 5px; box-shadow: 0 2px 3px 0 rgb(0 0 0 / 16%); background-color: #fff; margin-bottom: 5px;" onclick="perGorupSetting();">설정</button> -->
		<p class="popTit">언어</p>
		<select class="popSub">
		    <option value="">한국어</option>
		    <option value="">영어</option>
		</select>
		<div class="btn_wrap">
	    	<button type="button" onclick = "logOut()">로그아웃</button>
	        <button type="button" onclick = "pwdChange();">비밀번호 변경</button>
	    </div>
	</div>
	
	<!-- 비밀번호 변경 팝업 -->
	<div id = "udocmodal" style="display: none;">
		<div>
			<table class="pwdchangePop">
				<tr>
					<td style="width: 120px;">새 비밀번호</td>
					<td><input type="password" id="PopupNewPassword"></td>
				</tr>		
				<tr>
					<td style="width: 120px;">새 비밀번호 확인</td>
					<td><input type="password"  id="PopupConfirmNewPassword"></td>
				</tr>
			</table>
		</div>
		<div style="text-align: center;margin-top:10px;">
			<button class="btbase" onclick="userEdit();">변경</button>
			<button class="btbase" onclick="popClose();">닫기</button>
		</div>
	</div>
</body>
</html>
