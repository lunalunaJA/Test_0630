<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECM4.0 :: <spring:eval expression="@${lang}['LOGIN']" /></title>
<link rel="stylesheet" href="${css}/reset.css">
<link rel="stylesheet" href="${css}/common.css">
<link rel="stylesheet" href="${css}/style.css">
<link rel="stylesheet" href="${css}/jquery-ui.css" />

<script type="text/javascript" src="${js}/jquery-1.12.2.min.js"></script>
<script type="text/javascript" src="${js}/jquery.ajax.common.js"></script>
<script type="text/javascript" src="${js}/jquery.noty.packaged.min.js"></script>
<script type="text/javascript">
var g_CookieCode = "SecmCode";
var g_CookieName = "SecmId";
var g_CookieLang = "SecmLang";
var g_CookiePeriod = 31;

$('document').ready(function() {
	// 기관코드 쿠키 가져오기
	var getCookieCodeInIe = getCookie(g_CookieCode);
	// 사용자ID 쿠키 가져오기
	var getCookieNameInIe = getCookie(g_CookieName);
	// 언어 키 가져오기
	var getCookieLangInIe = getCookie(g_CookieLang);
	
	if (getCookieNameInIe) {
		$("#Txt_Code").val(getCookieCodeInIe);
		$("#Txt_UsrId").val(getCookieNameInIe);
		$('#Txt_Password').focus();
		$("#keep_btn").attr("checked",true);
	} else {
		$('#Txt_Code').focus();
	}
	
	// 아이디 체크 이벤트
	$("#keep_btn").change(function(){
		if($(this).is(":checked")){
			setCookie(g_CookieCode, $("Txt_Code").val() ,g_CookiePeriod);
			setCookie(g_CookieName, $("Txt_UsrId").val() ,g_CookiePeriod);
		}else{
			delCookie(g_CookieCode);
			delCookie(g_CookieName);
		}
	})

	// 비밀번호 입력 이벤트
	$('#Txt_Password').keydown(function(event) {
		if (event.keyCode == 13)
			pageEvent.Loginbtn();
		if (!$('#Txt_UsrId').val()) {
			alert("<spring:eval expression="@${msgLang}['ENTER_FIRST_ID']"/>");
			$('#Txt_UsrId').focus();
		}
	});
	
	if (getCookieLangInIe) {
		if (getCookieLangInIe == "ko") {
			$("#langBtn").removeClass('langChange');
			$("#langBtn").find('img').attr('src','${image}/icon/bt_Language_kr.png');
	        $("#langBtn").data('lang','ko');
	        getCookieLangInIe = 'ko';
		} else {
			$("#langBtn").addClass('langChange');
			$("#langBtn").find('img').attr('src','${image}/icon/bt_Language_en.png');
		    $("#langBtn").data('lang','en');
		    getCookieLangInIe = 'en';
		}
		var url = new URL(location.href);
		const urlParams = url.searchParams;
	 	if (urlParams.get('lang') != getCookieLangInIe) {
			location.href = "${ctxRoot}/go/login?lang="	+ getCookieLangInIe;
		}
	} else {
		if (lang == "ko") {
			console.log("lang : ko");
			$(this).find('img').attr('src','${image}/icon/bt_Language_kr.png');
	        $(this).data('lang','ko');
		} else {
			console.log("lang : en");
			 $(this).find('img').attr('src','${image}/icon/bt_Language_en.png');
		     $(this).data('lang','en');
		}
	}

	//언어 선택
	$('#langBtn').click(function(){
	    $(this).toggleClass('langChange');
	    if($(this).hasClass('langChange')){
	        $(this).find('img').attr('src','${image}/icon/bt_Language_en.png');
	        $(this).data('lang','en');
	    } else {
	        $(this).find('img').attr('src','${image}/icon/bt_Language_kr.png');
	        $(this).data('lang','ko');
	    }
	    var lang = $(this).data('lang');
	    setCookie(g_CookieLang, lang, g_CookiePeriod);
	  	location.href = "${ctxRoot}/go/login?lang=" + lang;
	})

});
/*****************************************************************************
 Desc   : 전역변수 관련 선언
 gbLoginFlag - 로그인 버튼 클릭 여부 flag
 *****************************************************************************/
var global = {
	gbLoginFlag : false
};

var pageEvent = {
	/********************************************************************
	    Name   : fn_LoginHandle
	    Desc   : 로그인을 처리한다.
	    Param  : 없음
	 ********************************************************************/
	Loginbtn : function() {
		event.preventDefault();
		var lang = "";		// 선택 언어
		var UserID = $('#Txt_UsrId').val();		// 로그인 아이디
		var UserPWD = $('#Txt_Password').val();	// 로그인 패스워드

		if (UserID == "" || UserID == null) {
			alert("<spring:eval expression="@${msgLang}['ENTER_ID']"/>");
			return;
		}
		if (UserPWD == "" || UserPWD == null) {
			alert("<spring:eval expression="@${msgLang}['ENTER_PASSWORD']"/>");
			return;
		}

		var CompanyCode = $('#Txt_Code').val();	//입력 기관 코드
		
		if($('#langBtn').hasClass('langChange')){
			lang ="en";
		}else{
			lang ="ko";
		}
		
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/auth/connect',
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify({
				objIsTest : "N",
				objLoginid : UserID,
				objPasswd : UserPWD,
				objCompanyid : CompanyCode,
				objlang : lang
			}),
			success : function(data) {
				console.log("Loginbtn data:"+JSON.stringify(data));
				if (data.status == "0000") {
					if($("#keep_btn").is(":checked")){
						setCookie(g_CookieName, $("#Txt_UsrId").val().trim(), g_CookiePeriod);
						setCookie(g_CookieLang, lang, g_CookiePeriod);
						setCookie(g_CookieCode, $("#Txt_Code").val().trim(), g_CookiePeriod);
					}else{
						delCookie(g_CookieName);
						delCookie(g_CookieLang);
						delCookie(g_CookieCode);
					}
					
					if (data.result.usertype == USERTYPES["SYSTEM"]) {
						location.href = "${ctxRoot}/go/OrganMng?lang="+ lang;
					} else {
						location.href = "${ctxRoot}/go/docMain?lang="+ lang;
					}
				} else {
					alertErr(data.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		});
	}
};

/*
 * 브라우저 쿠키 읽기
 */
var getCookie = function(inCookieName) {
	var cookieName = inCookieName + "=";
	var cookieData = document.cookie;
	var startIdx = cookieData.indexOf(cookieName);
	var cookieValue = "";
	if (startIdx != -1) {
		startIdx += cookieName.length;
		var endIdx = cookieData.indexOf(";", startIdx);
		if (endIdx == -1) {
			endIdx = cookieData.length;
		}
		cookieValue = cookieData.substring(startIdx, endIdx);
	}
	return unescape(cookieValue);
}

/*
 * 브라우저 쿠키 저장
 */
var setCookie = function(inCookieName, inValue, inExdays) {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + inExdays);
	var cookieValue = escape(inValue)+ ((inExdays == null) ? "" : "; expires="+ exdate.toGMTString());
	document.cookie = inCookieName + "=" + cookieValue;
}
/*
 * 브라우저 쿠키 제거
 */
var delCookie = function(inCookieName) {
	var expireDate = new Date();
	expireDate.setDate(expireDate.getDate() - 1);
	document.cookie = inCookieName + "=" + "; expires="	+ expireDate.toGMTString();
}
</script>

</head>
<body>
	<main>
		<section id="lg_bg">
			<div>
				<h1>WELCOME!</h1>
				<P>Enterprise Content Management</P>
			</div>
			</section> <section id="lg_input">
			<div>
				<h3>(주)제니스에스티</h3>
				<form onsubmit="pageEvent.Loginbtn()">
					<input type="text" id ="Txt_Code" placeholder="기관코드">
					<input type="text" id ="Txt_UsrId" placeholder="아이디">
					<input type="password" id ="Txt_Password" placeholder="패스워드">
					<input type="checkbox" name="keep_chk" id="keep_btn">
                    <label for="keep_btn"></label>
                    <label for="keep_btn" id="keep_txt">아이디 기억하기</label>
					<input type="submit" value="로그인" class="btn"">
				</form>
				<!-- 
				<ul>
                	<li><a href="#none">아이디 찾기</a></li>
                    <li><a href="#none">비밀번호 찾기</a></li>
                    <li><a href="#none">회원가입</a></li>
                </ul> -->
				<button type="button" id="langBtn"><img src="${image }/icon/bt_Language_kr.png" alt="언어설정_한국어"></button>
			</div>
		
		</section>
	</main>
</body>
</html>