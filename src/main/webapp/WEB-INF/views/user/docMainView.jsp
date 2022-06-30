<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>
<%
	//String[] uAuth = Utility.split(sessUserAuth, "|");
%>
<!DOCTYPE html>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>NADi4.0 :: 메인</title>
<link type="text/css" rel="stylesheet" href="${css}/common.css" />
<link type="text/css" rel="stylesheet" href="${css}/jquery-ui-1.11.0.min.css" />
<link type="text/css" rel="stylesheet" href="${css}/jstree.css" />
<script type="text/javascript" src="${js}/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="${js}/jquery-ui-1.11.0.js"></script>
<script type="text/javascript" src="${js}/jstree.js"></script>

<script type="text/javascript">
var modalZIndex = 1000;
var infoOpen = true;

$(document).ready(function() {
	var sessData = "${Authentication.sessUser}";
	console.log("sess data : "+JSON.stringify(sessData));
    // 문서 분류수정 팝업 닫기
    $("#btnDocUpdate_close").click(function() {
        $("#updateDocTypeModal").hide();

        $(".modalLayer:last-of-type").remove();
    });

    // 테스트 팝업 닫기
    $("#modalClose").click(function() {
        $("#TestModal").hide();
		
        $(".modalLayer:last-of-type").remove();
    });
    
    $("#infoBtn").click(function() {
		var containerWidth = parseInt($('#container').css('width').replace(/[^-\d\.]/g, '')); 	// body width
		var leftPixel = parseInt($('.sepage_l_Tree').css('width').replace(/[^-\d\.]/g, '')); 	// left width 
		var lWidth = Math.round(leftPixel / containerWidth * 1000) / 10;
		
    	if (infoOpen) {
    		leftM = 98 - 19 - lWidth + "%";
    		$(".sepage_r").animate({ 'width': leftM });
    		$("#rigthMenu").css({
    			"width":"19%",
    			"display":"block"
    		});
    		infoOpen = false;
    	} else {
    		leftM = 98 - lWidth + "%";
    		$(".sepage_r").animate({ 'width': leftM });
    		$("#rigthMenu").css({
    			"width":"0%",
    			"display":"none"
    		});
    		infoOpen = true;
    	}
    });
});

var addModal = function() {
    $('.sepage_data').append("<div class = 'modalLayer' style = 'position: fixed;z-index: " + modalZIndex + ";left: 0;top: 0;width: 100%;height: 100%;overflow: auto;background-color: rgb(0,0,0);background-color: rgba(0,0,0,0.4);'></div>");
}

var fn_init = function() {
    $("#holdArea").hide();
};

var fn_openPop = function() {

    //레이어팝업 중앙에 띄우기
    var $layerPopupObj = $('#updateDocTypeModal');
    var left = ($(window).scrollLeft() + ($(window).width() - $layerPopupObj.width()) / 2);
    var top = ($(window).scrollTop() + ($(window).height() - $layerPopupObj.height()) / 2);
    $layerPopupObj.css({
        'left': left,
        'top': top,
        'position': 'absolute'
    });

    addModal();

    $("#updateDocTypeModal").show();

    $("#updateDocTypeModal").draggable({
        containment: 'body',
        scroll: false
    });

    $("#updateDocTypeModal").css('z-index', modalZIndex + 100);
    modalZIndex = modalZIndex + 1000;

};

var popupOpen = function() {
    //레이어팝업 중앙에 띄우기
    var $layerPopupObj = $('#TestModal');
    var left = ($(window).scrollLeft() + ($(window).width() - $layerPopupObj.width()) / 2);
    var top = ($(window).scrollTop() + ($(window).height() - $layerPopupObj.height()) / 2);
    $layerPopupObj.css({
        'left': left,
        'top': top,
        'position': 'absolute'
    });

    addModal();

    $("#TestModal").show();
    $("#TestModal").draggable({
        containment: 'body',
        scroll: false
    });
    $("#TestModal").css('z-index', modalZIndex + 100);
    modalZIndex = modalZIndex + 1000;
}

// 우측 메뉴 리사이징
$(function() {
	$("#rigthMenu").resizable({
		minWidth: 10,
		maxWidth: 600, 
		handles: "w", 
		resize: function( e, ui){
			var duration = setTimeout(function(){
				var containerWidth = parseInt($('#container').css('width').replace(/[^-\d\.]/g, '')); 	// body width
				
				var leftPixel = parseInt($('.sepage_l_Tree').css('width').replace(/[^-\d\.]/g, '')); 	// left width 
				var middlePixel = parseInt($('.sepage_r').css('width').replace(/[^-\d\.]/g, '')); 		// middle width
				var rightPixel = ui.size.width;	// right width
				
				var lWidth = Math.round(leftPixel / containerWidth * 1000) / 10;
				var mWidth = Math.round(middlePixel / containerWidth * 1000) / 10;
				var rWidth = Math.round(rightPixel / containerWidth * 1000) / 10;
				
				console.log("lWidth : " + lWidth);
				console.log("mWidth : " + mWidth);
				console.log("rWidth : " + rWidth);
				console.log("");

				$("#rigthMenu").css("width", "calc(98% - "+ mWidth +"% - " + lWidth + "%)");
				$(".sepage_r").css("width", "calc(98% - "+ lWidth +"% - " + rWidth + "%)");
			},30);
		}
	}); 
});

// 좌축 트리 리사이징
$(function() {
	$("#resizable").resizable({
		minWidth: 300, 
		maxWidth: 600, 
		handles: "e", 
		resize: function( e, ui){
			var duration = setTimeout(function(){
				var containerWidth = parseInt($('#container').css('width').replace(/[^-\d\.]/g, '')); // body width
				
				var leftPixel = ui.size.width; // resizing width
				var middlePixel = parseInt($('.sepage_r').css('width').replace(/[^-\d\.]/g, '')); // middle width
				var rightPixel = parseInt($('#rigthMenu').css('width').replace(/[^-\d\.]/g, '')); // right width

				var lWidth = Math.round(leftPixel / containerWidth * 1000) / 10;
				var mWidth = Math.round(middlePixel / containerWidth * 1000) / 10;
				var rWidth = Math.round(rightPixel / containerWidth * 1000) / 10;
				
				console.log("lWidth : " + lWidth);
				console.log("mWidth : " + mWidth);
				console.log("rWidth : " + rWidth);
				console.log("");

				$(".sepage_l_Tree").css("width", "calc(98% - "+ mWidth +"% - " + rWidth + "%)");
				$(".sepage_r").css("width", "calc(98% - "+ lWidth +"% - " + rWidth + "%)");
			},30);
		}
	}); 
});


</script>
</head>

<body>
<div id="wrap">
    <!--header stard-->
    <c:import url="../common/TopPage.jsp" />
    <!--header end-->
    <!--content stard-->
    <div id="container">
        <div id="resizable" class="sepage_l_Tree">
            <div style="height: 60px; width: 100%; overflow: hidden;">
                <div style="height: 60px; width: 100%; background-color: #337ab7; color: #FFFFFF; font-size: 20px; font-weight: bold; font-family: 'Nanum Gothic', '나눔고딕', dotum, gulim; text-align: left; float: left; padding-left: 10px; padding-top: 20px; overflow: hidden;">
                    문서 분류
                    <!--<a href="${ctxRoot}/DocClass"><img style="margin-right: 20px; margin-top; float: right; width: 25px; height: 25px;"src="${image}/ic_45_setting.png" /></a>-->
                </div>
            </div>
            <div class="popup_container" style="height: 99%;">
                <div>
                    <ul class="p_tab">
                        <li><a id="Btn_Env1" class="current" href="#">현장</a></li>
                        <li><a id="Btn_Env2" href="#">서울</a></li>
                        <li><a id="Btn_Env3" href="#">제주도</a></li>
                    </ul>
                </div>
                <div id="Div_Env1" class="p_tab_box" style="height: 85%; display: none; padding: 10px 10px;">
                    <div class="All_Tree" style="border: 1px solid #d2d2d2; background-color: rgb(245, 245, 245); height: 100%; overflow: auto;">
                        <ul id="znTree"></ul>
                    </div>
                </div>
                <div id="Div_Env2" class="p_tab_box" style="height: 85%; display: none; padding: 10px 10px;">
                    <div class="Fvt_Tree" style="border: 1px solid #d2d2d2; background-color: rgb(245, 245, 245); height: 100%; overflow: auto;">
                        <ul id="fvtznTree"></ul>
                    </div>
                </div>
                <div id="Div_Env3" class="p_tab_box" style="height: 85%; display: none; padding: 10px 10px;">
                    <div class="Lab_Tree" style="border: 1px solid #d2d2d2; background-color: rgb(245, 245, 245); height: 100%; overflow: auto;">
                        <ul id="LabznTree"></ul>
                    </div>
                </div>
                <div id="Div_Env4" class="p_tab_box" style="height: 85%; display: none; padding: 10px 10px;">
                    <div class="GMP_Tree" style="border: 1px solid #d2d2d2; background-color: rgb(245, 245, 245); height: 100%; overflow: auto;">
                        <ul id="GmpznTree"></ul>
                    </div>
                </div>
            </div>
        </div>
        <div id="docMainView" class="sepage_r" style="height: 99%;">
            <div style="margin-left: 20px; margin-top: 15px; font-size: 12px; font-weight: bold; padding-bottom: 8px; border-bottom: 1px solid #d2d2d2;" class="Div_SelectText">
                <img alt="" src="${image}/iconext/icon_navigation.png" style="width: 17px; height: 17px;"> <span id="SelectText">공용문서
                    > 현장</span>
            </div>
            <div class="sepage_ttl" style="padding-top: 0px;">
                <div style="float: left; margin-bottom: 5px;">
                    <p>
                        검색기간 : <input type="text" style="width: 100px;" id="RegSDate"><a href="javascript:goStart()"><img alt="" src="${image }/com/icon_btn_cal.png" style="height: 30px; margin-top: -3px; margin-left: -30px; margin-right: 15px;"></a>
                        ~ <input type="text" style="width: 100px;" id="RegEDate"><a href="javascript:goEnd()"><img alt="" src="${image }/com/icon_btn_cal.png" style="height: 30px; margin-top: -3px; margin-left: -30px; margin-right: 35px;"></a>
                        파일이름 : <input type="text" style="width: 150px; margin-right: 25px;" id="SearchFileName">
                        등록자 : <input type="text" style="width: 100px; margin-right: 30px;" id="SearchUserName">
                        <input type="button" class="btn_dg" style="margin-right: 5px;" id="SearchBtn" value="조회">
                    </p>
                </div>
                <div id="topmenu" style="float: right; margin-top: 5px; margin-bottom: 5px;">
                    <!-- <input type="button" class="b_btn" style="margin-right: 5px;" id="testt" value="테스트" onclick="testtt()"> -->
                    <!-- <input type="button" class="b_btn" style="margin-right: 5px; visibility: hidden;" id="BundleAprov" value="일괄결재"> -->
                    <input type="button" class="b_btn" style="margin-right: 5px;" id="infoBtn" value="문서정보">
                    <input type="button" class="b_btn" style="margin-right: 5px;" id="RegBtn" value="문서등록">
                    <!-- <input type="button" class="b_btn" style="margin-right: 5px; display: inline;" id="CopyFolderBtn" value="폴더링크">
		<input type="button" class="b_btn" style="margin-right: 5px; display: none;" id="DelFolderBtn" value="폴더링크삭제">
		<input type="button" class="b_btn" style="margin-right: 5px;" id="CopyDocBtn" value="문서링크"> -->

                    <input type="button" class="btn_dg" style="margin-right: 5px;" id="DownBtn" value="다운로드"> <input type="button" class="btn_dg" id="DelBtn" value="삭제">
                </div>

                <div id="topmenu2" style="float: right; margin-top: 5px; margin-bottom: 5px; display: none;">
                    <input type="button" class="b_btn" style="margin-right: 5px;" id="CopyDocBtn2" value="문서링크">
                </div>
            </div>
            <div style="clear: both;"></div>

            <div>
                <div class="sepage_data" style="magin-top: 20px;">
                    <table class="board_list">
                        <thead>
                            <tr>
                                <th style="width: 3%;"><input type="checkbox" /></th>
                                <th style="width: 67%;">문서명</th>
                                <th style="width: 10%;">문서유형</th>
                                <th style="width: 10%;">등록자</th>
                                <th style="width: 10%;">등록일</th>
                            </tr>
                        </thead>
                        <tbody id="codeList" style="overflow: auto;">
                            <tr>
                                <td><input type="checkbox" /></td>
                                <td onclick="fn_openPop()">실적 문서</td>
                                <td>기안문</td>
                                <td>홍길동</td>
                                <td>2020-01-01</td>
                            </tr>
                            <tr>
                                <td><input type="checkbox" /></td>
                                <td>2020년 상반기 실적 현황분석</td>
                                <td>보고서</td>
                                <td>홍길동</td>
                                <td>2020-06-01</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <!-- 문서 분류수정 팝업 -->
                <div id="updateDocTypeModal" style="display: none; width: 400px; height: 440px; padding: 20px; background-color: #fefefe; border: 1px solid #888;">
                    <div id="docmodal">
                        <div style="height: 20px; font-size: 13px; font-weight: bold; color: #000; margin: 0px 2px;">분류수정</div>
                        <div style="padding-top: 5px;"></div>
                        <div id='divDocListImgPop_u' class='msfile_list_w' style="height: 280px; color: #000; font-size: 12px; font-family: Nanum Gothic;">
                            <ul id="znTreeImgPop_u"></ul>
                        </div>
                        <div style="padding-top: 1px;">
                            <input type="button" style="float: right; margin-right: 10px; cursor: pointer;" class="btn_dg" value="팝업" onclick="popupOpen()"> <input type="button" style="float: right; margin-right: 10px; cursor: pointer;" class="btn_dg" id="btnDocUpdate_close" value="닫기"> <input type="button" style="float: right; margin-right: 10px; cursor: pointer;" class="btn_bl" id="btnDocUpdateProc" value="저장">
                        </div>
                    </div>
                </div>


                <!-- 문서 분류수정 팝업 -->
                <div id="TestModal" style="display: none; width: 300px; height: 350px; padding: 20px; background-color: #fefefe; border: 1px solid #888;">
                    <div id="testmodaldiv">
                        <div style="height: 20px; font-size: 13px; font-weight: bold; color: #000; margin: 0px 2px;">modal test</div>
                        <div style="padding-top: 5px;"></div>
                        <div style="padding-top: 1px;">
                            <input type="button" style="float: right; margin-right: 10px; cursor: pointer;" class="btn_dg" id="modalClose" value="닫기">
                        </div>
                    </div>
                </div>

                <div id="holdArea" class="popupModal"></div>
            </div>
        </div>

		<div id = "rigthMenu" style="height: 100%; width: 0%; border-left:1px solid #d4d4d4; float:right; display:none;">
			
		</div>



    </div>
    <!--Footer stard-->
    <c:import url="../common/Footer.jsp" />
    <!--Footer end-->
</div>

</body>

</html>