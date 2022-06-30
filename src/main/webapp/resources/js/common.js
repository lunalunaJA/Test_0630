/*
* 함수그룹
*	zXxxXxx : common 약어 z 로 시작
* ----- 사용된 객체 목록 -----------
* zChkLgth 		: 문자 길이 반환
* zChkString 	: 문숫자 공백을 체크
* zReplace		: replace, remove 기능 - 문자열에 포함된 pcChar1 문자를 모두 pcChar2 로 변환한다.
* zDateUtil		: 날짜관련 정의
* zFormatter	: 특정(주민번호, 날짜 등)의 마스크 ON/Off 처리
*/

// zReplace, zChkString 에서 사용하는 전역 변수
var Gst_NumberStr        = /[0-9]/g;
var Gst_NotNumberStr     = /[^0-9]/g;
var Gst_NotNumOrNotSprtr = /[^0-9-\/,:]/g;
var Gst_SeparatorStr     = /[-\/:]/g;
var Gst_SpaceStr         = /[\s]/g;
var Gst_AlphabatStr      = /[a-zA-Z]/g;
var Gst_NotAlphabatStr   = /[^a-zA-Z]/g;
var Gst_TimeSeparatorStr = /:/g;
var Gst_DtTmSeparatorStr = /[-:]/g;

// String length 체크
var zChkLgth = {
	fn_notEqual : function(pstStr, pitLength) {
		if(zReplace.fn_removeSeparatorWithSpace(pstStr).length !== parseInt(pitLength))
			return true;
		return false;
	}
	,
	fn_equal : function(pstStr, pilLgth) {
		if(zReplace.fn_removeSeparatorWithSpace(pstStr).length === parseInt(pilLgth))
			return true;
		return false;
	}
};

// String value 체크
var zChkString = {
	// 숫자 여부 체크
	fn_isNumber : function(pstStr) {
		return Gst_NumberStr.test(pstStr);
	}
	,
	// 구분자 체크
	fn_isSeparator : function(pstStr) {
		return Gst_SeparatorStr.test(pchChar);
	}
	,
	// 공백 체크
	fn_isEmpty : function(pstStr) {
		if(pstStr === undefined || pstStr === null ||  pstStr === "" || pstStr.length < 1)
			return true;
		return false;
	}
	,
	// 공백 체크
	fn_isNotEmpty : function(pstStr) {
		return !(this.fn_isEmpty(pstStr));
	}
};

// replace, remove
var zReplace = {
	/*
	 * @Function	: fn_replaceAllChar
	 * @Description : 문자열에 포함된 pcChar1 문자를 모두 pcChar2 로 변환한다.
	 * @Parameter	: pstStr = 문자열, pcChar1 = 대체되는 문자, pcChar2 = 대체 문자
	 * @Return		: 전환된 문자열
	 */
	fn_replaceAllChar : function(pstStr, pcChar1, pcChar2) {
		return pstStr.replace(new RegExp('[' + pcChar1 + ']', 'g'), pcChar2);
	}
	,
	/*
	 * @Function	: fn_removeSeparatorWithSpace
	 * @Description : 문자열에 구분자와 공백을 모두 제거한다.
	 * @Parameter	: pstStr = 문자열
	 * @Return		: 전환된 문자열
	 */
	fn_removeSeparatorWithSpace : function(pstStr) {
		if(zChkString.fn_isEmpty(pstStr)) return "";
		return zReplace.fn_removeSpace(zReplace.fn_removeSeparator(pstStr));
	}
	,
	/*
	 * @Function	: fn_removeSpace
	 * @Description : 문자열에 포함된 공백을 모두 제거한다.
	 * @Parameter	: pstStr = 문자열
	 * @Return		: 전환된 문자열
	 */
	fn_removeSpace : function(pstStr) {
		if(zChkString.fn_isEmpty(pstStr)) return "";
		return pstStr.valueOf().replace(Gst_SpaceStr, "");
	}
	,
	/*
	 * @Function	: fn_removeSeparator
	 * @Description : 문자열에 포함된 구분자를 모두 제거한다. 구분자 (  -, /, : )
	 * @Parameter	: pstStr = 문자열
	 * @Return		: 전환된 문자열
	 */
	fn_removeSeparator : function(pstStr) {
		if(zChkString.fn_isEmpty(pstStr)) return "";
		return (pstStr.valueOf()).replace(Gst_SeparatorStr, "");
	}
	,
	/*
	 * @Function	: fn_removeAllChar
	 * @Description : 문자열에 포함된 특정문자를 제거한다.
	 * @Parameter	: pstStr = 문자열, pcChar = 제거할 문자
	 * @Return		: 전환된 문자열
	 */
	fn_removeAllChar : function(pstStr, pcChar) {
		return pstStr.replace(new RegExp('[' + pcChar + ']', 'g'), '');
	}
	,
	/*
	 * @Function	: fn_trim
	 * @Description : 문자열에 앞/뒤의 공백을 제거한다.
	 * @Parameter	: str = 문자열
	 * @Return		: 전환된 문자열
	 */
	fn_trim : function(str) {
	    if (str == null) return "";
	    return str.replace(/(^\s*)|(\s*$)/g, "");
	}
};

var zDateUtil =
{
	Gch_TmSprtr : ':'
	,
	Gch_DtSprtr : '-'
	,

	/*
	 * =========================================================
	 * 날짜와 시간을 분리한다.
	 * =========================================================
	 * yyyyMMddHHmmss를 아래와 같이 분리
	 * Array[0] = yyyyMMdd
	 * Array[1] = HHmmss
	 * =========================================================
	 */
	fn_splitDtTm : function(pstStr) {
		var temp = zReplace.fn_removeSeparatorWithSpace(pstStr);
		var larDate = new Array();
		if(zChkLgth.fn_equal(temp, 14))
		{
			larDate.push(temp.substring(0,8));
			larDate.push(temp.substring(8,14));
		}
		else if(zChkLgth.fn_equal(temp, 12))
		{
			larDate.push(temp.substring(0,8));
			larDate.push(temp.substring(8,12));
		}
		return larDate;
	}
	,
	/*
	 * =========================================================
	 * 날짜를 분리하여 배열로 Return
	 * =========================================================
	 * Array[0] = 년도 4자리
	 * Array[1] = 월 2자리
	 * Array[2] = 일 2자리
	 * =========================================================
	 */
	fn_splitDt : function(pstStr) {
		var temp = zReplace.fn_removeSeparatorWithSpace(pstStr);
		var larDate = new Array();
		if (zChkLgth.fn_equal(temp, 8))
		{
			larDate.push(temp.substring(0,4));
			larDate.push(temp.substring(4,6));
			larDate.push(temp.substring(6,8));
		}
		return larDate;
	}
	,
	/*
	 * =========================================================
	 * 시간를 분리하여 배열로 Return
	 * =========================================================
	 * Array[0] = 시간 2자리
	 * Array[1] = 분 2자리
	 * Array[2] = 초 2자리
	 * =========================================================
	 */
	fn_splitTm : function(pstStr) {
		var temp = zReplace.fn_removeSeparatorWithSpace(pstStr);
		var larTmp = new Array();
		if (zChkLgth.fn_equal(temp, 6))
		{
			larTmp.push(temp.substring(0,2));
			larTmp.push(temp.substring(2,4));
			larTmp.push(temp.substring(4,6));
		}
		else if(zChkLgth.fn_equal(temp, 4))
		{
			larTmp.push(temp.substring(0,2));
			larTmp.push(temp.substring(2,4));
		}
		return larTmp;
	}
	,
	/*
	 * =========================================================
	 * 날짜에 구분자 삽입 (yyyy-MM-dd)
	 * =========================================================
	 */
	fn_insrtSprtrToDt : function(pstStr) {
		if(zChkString.fn_isEmpty(pstStr)) return "";

		if(zChkLgth.fn_notEqual(pstStr, 8))
			return pstStr;
		return this.fn_splitDt(pstStr).join(this.Gch_DtSprtr);
	}
	,
	/*
	 * =========================================================
	 * 날짜에서 구분자 "-" 제거 (yyyyMMdd)
	 * =========================================================
	 */
	fn_replaceSprtrToDt : function(pstStr)	{
		if(zChkString.fn_isEmpty(pstStr)) return "";
		return zReplace.fn_removeAllChar(pstStr, this.Gch_DtSprtr);
	}
	,

	/*
	 * =========================================================
	 * 시간에 구분자 삽입 (HH:mm:ss)
	 * =========================================================
	 */
	fn_insrtSprtrToTm : function(pstStr) {
		var lstTemp = "";
		if(zChkLgth.fn_equal(pstStr, 6))
			lstTemp = this.fn_splitTm(pstStr).join(this.Gch_TmSprtr);
		else if(zChkLgth.fn_equal(pstStr, 4))
			lstTemp = this.fn_splitTm(pstStr).join(this.Gch_TmSprtr);
		return lstTemp;
	}
	,
	// 현재 날짜 구하기
	fn_getToDate : function(pSprtr)	{
		var now_date = new Date();
		var s_year	 = now_date.getYear();
		var s_month  = this.return0(now_date.getMonth()+1);	// JS의 Date.getMonth()는 0 to 11
		var s_day 	 = this.return0(now_date.getDate());		// 1 to 31
		return s_year + pSprtr + s_month + pSprtr + s_day;
	}
	,
	// 현재 날짜 년월 구하기 (YYYY-MM)
	fn_getToDateYM : function(pSprtr) {
		var now_date = new Date();
		var s_year	 = now_date.getYear();
		var s_month  = this.return0(now_date.getMonth()+1);	// JS의 Date.getMonth()는 0 to 11

		return s_year + pSprtr + s_month;
	}
	,
	// 현재 시간 구하기
	fn_getNowTime : function(pSprtr) {
		// 시간 구하기
		var time = new Date();
		var s_hour = time.getHours();
		var s_min  = time.getMinutes();
		var s_sec  = time.getSeconds();
		if(s_hour<10) s_hour = "0" + s_hour;
		if(s_min<10)  s_min  = "0" + s_min;
		if(s_sec<10)  s_sec  = "0" + s_sec;

		return s_hour + pSprtr + s_min + pSprtr + s_sec;
	}
	,
	return0 : function(str) {
		str=""+str;
		if (str.length == 1)
			str="0"+str;
		return str;
	}
	,
	fn_captionDate : function (date,num) {
		var year = Number(date.substring(0,4));
		var month = Number(date.substring(4,6));
		var returnStr ;

		month = month + Number(num);
		if (month > 12) {
			month = month - 12;
			year = year + 1;
		}
		returnStr = new Array(String(year),String(month));
		return returnStr;
	}
	,
	fn_addDay : function (addDay, pSprtr) {
		var now_date	= new Date();
		var add_dt		= new Date(Date.parse(now_date) + (addDay * 1000 * 60 * 60 * 24) );
		
		/*console.log('(zDateUtil.fn_addDay)'
				+ '  now_date[' + now_date + ']'
				+ ', add_dt[' + add_dt + ']'
				);*/

		var rtn_year	= add_dt.getYear();
		var rtn_month  	= this.return0(add_dt.getMonth() + 1);	// JS의 Date.getMonth()는 0 to 11
		var rtn_day 	= this.return0(add_dt.getDate());		// 1 to 31		
		return rtn_year + pSprtr + rtn_month + pSprtr + rtn_day;
	}
	,
	fn_diff : function (sdt, edt) {
		sdt = zReplace.fn_removeAllChar(sdt, '-');
		edt = zReplace.fn_removeAllChar(edt, '-');
		
		if (sdt.length != 8 || edt.length != 8) {
			return -1;
		} else {
			var sdt_y	= sdt.substr(0, 4);
			var sdt_m	= sdt.substr(4, 2);
			var sdt_d	= sdt.substr(6, 2);
			var edt_y	= edt.substr(0, 4);
			var edt_m	= edt.substr(4, 2);
			var edt_d	= edt.substr(6, 2);

			var new_sdt	= new Date(sdt_y, sdt_m, sdt_d).valueOf();
			var new_edt	= new Date(edt_y, edt_m, edt_d).valueOf();
			var diff	= (new_edt - new_sdt) / (1000 * 60 * 60 * 24);
			var rtn		= parseInt(diff, 10);
			
			/*console.log('(zDateUtil.fn_diff)'
					+ '  sdt[' + sdt + ']'
					+ ', sdt_y[' + sdt_y + ']'
					+ ', sdt_m[' + sdt_m + ']'
					+ ', sdt_d[' + sdt_d + ']'
					+ ', edt[' + edt + ']'
					+ ', edt_y[' + edt_y + ']'
					+ ', edt_m[' + edt_m + ']'
					+ ', edt_d[' + edt_d + ']'
					+ '\n new_sdt[' + new_sdt + ']'
					+ ', new_edt[' + new_edt + ']'
					+ ', diff[' + diff + ']'
					+ ', rtn[' + rtn + ']'
					);*/
			
			return rtn;
		}
	}
};


var zFormatter = {
	// 금액/숫자		: momey     999,999,999
	// 일자                  : date      9999-99-99     (4)-(2)-(2)
	// 주민번호		: rno		999999-9999999 (6)-(7)
	// 사업자번호		: bzNo		999-99-99999   (3)-(2)-(5)
	// 법인등록번호	: corpGrno	999999-9999999 (6)-(7)

	// 금액/숫자 mask on
	fn_moneyMaskOn : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";

		var lstTmp  = new String(psStr).replace(/\..*/g, '');		// 소숫점 뒤로 버림 (천단위)
		var liLngth = parseInt(lstTmp.length / 3)+1;
		var liTmp   = lstTmp.length % 3;
		var llTmp   = new Array();
		var liCnt   = 0;
		for(var i=0; i < liLngth; i++) {
			llTmp.push(lstTmp.substring(liCnt, (i*3)+liTmp));
			liCnt = (i*3)+liTmp;
		}
		if(liTmp === 0)
			psStr = llTmp.join(",").substring(1);
		else
			psStr = llTmp.join(",");

		return psStr;
	}
	,
	// 금액/숫자 mask off
	fn_moneyMaskOff : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		psStr = psStr.replace(/,/g, "");
		return psStr;
	}
	,
	// 날짜 mask on
	fn_dateMaskOn : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		return zDateUtil.fn_insrtSprtrToDt(psStr);
	}
	,
	// 날짜 mask off
	fn_dateMaskOff : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		return zDateUtil.fn_replaceSprtrToDt(psStr);
	}
	,
	// 연월일시 mask on
	fn_dateTimeMaskOn : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		var arrDate = new Array();
		arrDate = zDateUtil.fn_splitDtTm(psStr); // 날짜와 시간을 분리 (14자리 or 12자리)
		if (arrDate.length == 2) {
			return zDateUtil.fn_insrtSprtrToDt(arrDate[0]) + " " + zDateUtil.fn_insrtSprtrToTm(arrDate[1]);
		}
		else {
			return psStr;
		}
	}
	,
	// 연월일시 mask off
	fn_dateTimeMaskOff : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		return zReplace.fn_removeSeparatorWithSpace(psStr);
	}
	,
	// 주민/사업자번호 mask on (자릿수에 따라 주민번호, 사업자번호 mask 처리)
	fn_rgNoMaskOn : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		if (psStr.length == 13) {
			return this.fn_rnoMaskOn(psStr);  // 주민번호
		}
		else if (psStr.length == 10) {
			return this.fn_bzNoMaskOn(psStr);  // 사업자번호
		}
		return psStr;
	}
	,
	// 주민/사업자번호 mask off
	fn_rgNoMaskOff : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		var tmp = psStr.split("-");
	    tmp = tmp.join("");
	    return tmp;
	}
	,
	// 주민번호 mask on
	fn_rnoMaskOn : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		if (psStr.length == 13) {
			psStr = psStr.substr(0, 6) + "-" + psStr.substr(6, 7);
		}
		return psStr;
	}
	,
	// 주민번호 mask off
	fn_rnoMaskOff : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		var tmp = psStr.split("-");
	    tmp = tmp.join("");
	    return tmp;
	}
	,
	// 사업자번호  mask on
	fn_bzNoMaskOn : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		if (psStr.length == 10) {
			psStr = psStr.substr(0, 3) + "-" + psStr.substr(3, 2) + "-" + psStr.substr(5, 5);
		}
		return psStr;
	}
	,
	// 사업자번호  mask off
	fn_bzNoMaskOff : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		var tmp = psStr.split("-");
	    tmp = tmp.join("");
	    return tmp;
	}
	, 
	// 전화번호(팩스번호) mask on
	fn_telNoMaskOn : function(psStr) {
		var startNo2 = "";
	    if (psStr.length > 2) startNo2 = psStr.substr(0, 2);	
	    
	    var startNo3 = "";
	    if (psStr.length > 3) startNo3 = psStr.substr(0, 3);	
	    
		var TmpStr = new Array();
		if (psStr.length == 12) {
			TmpStr.push(psStr.substr(0, 4));
			TmpStr.push(psStr.substr(4, 4));
			TmpStr.push(psStr.substr(8));
			return TmpStr.join("-");
		}
		else if (psStr.length == 11 && startNo3 == "050") {
			TmpStr.push(psStr.substr(0, 4));
			TmpStr.push(psStr.substr(4, 3));
			TmpStr.push(psStr.substr(7));
			return TmpStr.join("-");
		}
		else if (psStr.length == 11) {
			TmpStr.push(psStr.substr(0, 3));
			TmpStr.push(psStr.substr(3, 4));
			TmpStr.push(psStr.substr(7));
			return TmpStr.join("-");
		}
		else if (psStr.length == 10 && startNo2 == "02") {
			TmpStr.push(psStr.substr(0, 2));
			TmpStr.push(psStr.substr(2, 4));
			TmpStr.push(psStr.substr(6));
			return TmpStr.join("-");
		}
		else if (psStr.length == 10) {
			TmpStr.push(psStr.substr(0, 3));
			TmpStr.push(psStr.substr(3, 3));
			TmpStr.push(psStr.substr(6));
			return TmpStr.join("-");
		}
		else if (psStr.length == 9) {
			TmpStr.push(psStr.substr(0, 2));
			TmpStr.push(psStr.substr(2, 3));
			TmpStr.push(psStr.substr(5));
			return TmpStr.join("-");
		}
		else if (psStr.length == 8) {
			TmpStr.push(psStr.substr(0, 4));
			TmpStr.push(psStr.substr(4));
			return TmpStr.join("-");
		}
		else {
			return psStr;
		}
	}
	,
	// 전화번호(팩스번호) mask off
	fn_telNoMaskOff : function(psStr) {
		if(zChkString.fn_isEmpty(psStr)) return "";
		var tmp = psStr.split("-");
	    tmp = tmp.join("");
	    return tmp;
	}
};

//숫자만 입력하기
var onlyNumber = function(event){
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( (keyID >= 48 && keyID <= 57) || (keyID >= 96 && keyID <= 105) || keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 ) 
		return;
	else
		return false;
};

var removeChar = function(event){
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 ) 
		return;
	else
		event.target.value = event.target.value.replace(/[^0-9]/g, "");
};


/**
	* Name : pubByteCheckTextarea
	 *Desc : 한글 숫자 영문 입력시 byte로 크기 체크해서 알럿창을 띄워줌, IE 와 Chrome는 다르게 동작하여 브라우저 체크 필요
	 *Param : event
	*/
var agent = navigator.userAgent.toLowerCase();
var browserIE = false;
if ( (navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1) ) {
	browserIE =true;
}

var pubByteCheckTextarea =function(event, length) {
		//console.log(event);
		var str = event.target.value, _byte = 0, strLength = 0, charStr = '', cutStr = '';
		var maxLength = event.target.maxLength; //속성에 있는 maxLength를 사용할경우
		//console.log("str : "+str+", length : "+str.length);
		//console.log("maxLength : "+maxLength+", length : "+length);
		//maxLength 속성 미등록시 ie : 2147483647, chrome : -1
		if(maxLength == -1 || maxLength == 2147483647){
			//maxLength = 10; // test 용으로 10자리 체크
			//console.log("length : "+length);
			if(length){
				maxLength =length;
			}else{
				maxLength=100;
			}
		}
		if (str.length <= 0) {
			return;
		}
		for (var i = 0; i < str.length; i++) {
			charStr = str.charAt(i);
			if (escape(charStr).length > 4) {
				_byte += 2;
			} else {
				_byte++;
			}
			if (_byte <= maxLength) {
				strLength = i + 1;
			}
		}
		//console.log("_byte : "+_byte+", maxLength : "+maxLength);
		if (_byte > maxLength) {
			cutStr = str.substr(0, strLength);
			event.target.value = cutStr;

			/*
			IE는 event.isComposing 없어서 undefined가 떨어짐 
			IE는 알럿창이 1번 호출되지만 기타 브라우저는 2번이 호출되어 브라우저 체크후 isComposing체크하여 한번만 호출하도록 코드 추가
			숫자나 영문 입력시에 alert호출이 안되는 경우가 있어서 마지막글자 체크하여 알럿창이 호출되도록 추가
			 */
			// console.log("_byte : "+_byte+", maxLength : "+maxLength);
			if (!browserIE && !event.isComposing) {
				var lastChar = str.substr(-1, 1);
				var currentByte = lastChar.charCodeAt(0);
				if (currentByte <= 128) {
					alert(maxLength + "자를 초과하였습니다.");
				}
				return;
			}
			alert(maxLength + "자를 초과하였습니다.");
		}
	}


// 넘어온 값이 빈값인지 체크합니다. 
// !value 하면 생기는 논리적 오류를 제거하기 위해 
// 명시적으로 value == 사용 
// [], {} 도 빈값으로 처리
var objectIsEmpty = function(value){ 
	if( value == "" || value == null || value == undefined || ( value != null && typeof value == "object" && !Object.keys(value).length ) ){ 
		return true 
	}else{ 
		return false 
	} 
};
//객체 복사
function deepClone(obj) {
  if(obj === null || typeof obj !== 'object') {
    return obj;
  }
  
  const result = Array.isArray(obj) ? [] : {};
  
  for(var key in Object.keys(obj)) {
    result[key] = deepClone(obj[key]);
  }
  
  return result;
}
function cloneObject(obj) {
    var clone = {};
    for(var i in obj) {
        if(typeof(obj[i])=="object" && obj[i] != null)
            clone[i] = cloneObject(obj[i]);
        else
            clone[i] = obj[i];
    }
    return clone;
}
//객체 값 비교
function ObejctEquals(x, y) {
	if (x === y) return true; 
	
	if (!(x instanceof Object) || !(y instanceof Object)) return false; 
	
	if (x.constructor !== y.constructor) return false; 
	
	for (var p in x) { 
		if (!x.hasOwnProperty(p)) continue; 
		
		if (!y.hasOwnProperty(p)) return false; 
	
		if (x[p] === y[p]) continue; 
	
		if (typeof(x[p]) !== "object") return false; 
	
		if (!Object.equals(x[p], y[p])) return false; 
	}
	
	for (p in y) { 
		if (y.hasOwnProperty(p) && !x.hasOwnProperty(p)) return false; 
	}
 return true;
}
function ObjectClean(item){
	for ( var entry in item) {
				delete item[entry];
		}
}

	//window open 옵션
var Winoptions = {
	width: screen.availWidth-100,
	height: screen.availHeight-100,
	toolbar: 0,
	scrollbar:0,
	location: 0
};

var options =
	"width="+Winoptions.width+
	", height="+Winoptions.height+
	", left="+(screen.availWidth -Winoptions.width)/2+
	", top="+(screen.availHeight -Winoptions.height)/2+
	", location=no" +
	", toolbar=no" +
	", resizable=yes" +
	", scrollbars=yes" + 
	", status=yes" +
	", menubar=no";



//해시태그 생성 정규식
function converter (){
	var str = $('#beforeDesc').val();
	str = str.replace(/(<)/gi, '&lt;');
	str = str.replace(/(<)/gi, '&lg;');
	str = str.replace(/(?:\r\n|\n\r|\r|\n)/g, '<br />');
	str = str.replace(/#(.+?)(?=[\s.,:,]|$)/g, '<span>#$1</span>');
	str = str.replace(/@(.+?)(?=[\s.,:,]|$)/g, '<span>@$1</span>');
	str = str.replace(/((([A-Za-z]{3,9}:(?:\/\/)?)(?:[-;:&=\+\$,\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\+\$,\w]+@)[A-Za-z0-9.-]+)((?:\/[\+~%\/.\w-_]*)?\??(?:[-\+=&;%@.\w_]*)#?(?:[\w]*))?)/g, '<span>$1</span>');
	$('#afterDesc').html(str);
}

// 팝업 선택 번호
var liTabNum = 0;

$(function(){

    // nav select box
    let selectFlag;
    $('.custom-select').unbind("click").bind("click", function(){
		$(this).toggleClass('selected');
		$('.custom-select-arrow').toggleClass('turn')
		if($(this).hasClass('selected')) {
			$('.custom-select-list').slideDown();
		} else {
			$('.custom-select-list').slideUp();
		}
	});
	
	$('.custom-select').on('focusin', function() {
		$('.custom-select-list').slideDown();
	});
	
	$('.custom-select').on('focusout', function() {
		if(!selectFlag) {
			$('.custom-select-list').slideUp();
		}
		$(this).removeClass('selected');
	});
	
	$('.custom-select-option').on('mouseenter', function() {
		console.log("mouseenter");
		selectFlag = true;
	});
	
	$('.custom-select-option').on('mouseout', function() {
		console.log("mouseout");
		selectFlag = false;
	});
	
	$('.custom-select-list').on('click','li',function() {
		let value = $(this).attr('value');
		$('.custom-select-text').text($(this).text());
		$('.select-origin').val(value);
		$('.custom-select-list').slideUp();
	
		$('.select-origin').find('option').each(function(index, el) {
			if($(el).attr('value') == value) {
				$(el).attr('selected', 'selected');
			} else {
				$(el).removeAttr('selected');
			}
		});
		
		if(value == 'selectOption1'){
		 	$("#searchFldName").show();
			$("#searchClsName").hide();            
			$("#fldjstree").css('display','block');
			$("#MainClsTree").css('display','none');
		}else if(value == 'selectOption2'){
			$("#searchFldName").hide();
			$("#searchClsName").show();
			$("#fldjstree").css('display','none');
			$("#MainClsTree").css('display','block');
			
			
			$("#docRegistration").hide();
			$("#folderMenu").hide();
			
			$('li[id$=RegBtn]').hide();
			$('li[id$=DelBtn]').hide();
			$("#approvalBtn").hide();
			$("#returnBtn").hide();
			$("#DocDelBtn").show();
		}
		
	});
    
    //nav slide
    $('nav .nav_btn').unbind("click").bind("click", function(){
        $('nav').toggleClass('navSlide')
           if($('nav').hasClass('navSlide')){
               $('.nav_wrap').css('width','42px');
               $('.nav_btn').css('left','42px');
               $('.nav_btn img').attr('src','../resources/images/icon/menu_r_01.png');
               $('.nav_wrap').find('.foldingMn').css('display','none');
               $('.nav_wrap').find('.gnb').css('padding','inherit');
               $('.custom-select .custom-select-img').css('transform','translate(8px, 29px)')
               $('.nav_wrap').find('.sub').slideUp();
               $('#content').css('padding-left','80px');
            	    $("#searchFldName").hide();
			    	$("#searchClsName").hide();            
           } else {
               $('.nav_wrap').css('width','285px');
               $('.nav_btn').css('left','285px');
               $('.nav_btn img').attr('src','../resources/images/icon/menu_r_02.png');
               $('.nav_wrap').find('.foldingMn').fadeIn();
               $('.nav_wrap').find('.gnb').css('padding','18px');
               $('.custom-select .custom-select-img').css('transform','translate(27px, 7px)')
               $('#content').css('padding-left','285px');
              
               
               if($('.select-origin option:selected').val() == 'selectOption1'){
            	    $("#searchFldName").show();
			    	$("#searchClsName").hide();            
	            	$("#fldjstree").css('display','block');
					$("#MainClsTree").css('display','none');
	            }else{
	            	$("#searchFldName").hide();
			    	$("#searchClsName").show();
	            	$("#fldjstree").css('display','none');
					$("#MainClsTree").css('display','block');
	            }
               
            }
    });
    
    $('nav .nav_wrap li').unbind("click").bind("click", function(){
        if($('nav').width('42px')){
            $('.nav_wrap').css('width','285px');
            $('.nav_btn').css('left','285px');
            $('.nav_btn img').attr('src','../resources/images/icon/menu_r_02.png');
            $('.nav_wrap').find('.foldingMn').fadeIn();
            $('.nav_wrap').find('.gnb').css('padding','18px');
            $('.custom-select .custom-select-img').css('transform','translate(27px, 7px)')
            $('#content').css('padding-left','285px');
        }
    })

    //detail_wrap slide
    $('#detailContent .detail_btn').unbind("click").bind("click", function(){
        $('.detail_wrap').toggleClass('detailSlide')
        if($('.detail_wrap').hasClass('detailSlide')){
            $('.detail_wrap').animate({right:0});
            $(this).animate({right:210});
            $(this).find('img').attr('src','../resources/images/icon/menu_r_04.png');
            $('#content').css('padding-right','210px');
        } else {
            $('.detail_wrap').animate({right:-210});
            $(this).animate({right:0});
            $(this).find('img').attr('src','../resources/images/icon/menu_r_03.png');
            $('#content').css('padding-right','10px');
        }
    })

    //nav sub menu
    /*
    $('nav .nav_wrap .gnb .slideMenu a').not('#fldjstree #MainClsTree').unbind("click").bind("click", function(){
        $("nav .nav_wrap .gnb").toggleClass('slide');
        $("nav .nav_wrap .gnb").find('.openBtn').toggleClass('turn');
        if($("nav .nav_wrap .gnb").hasClass('slide')){
            $("nav .nav_wrap .gnb").find('.sub').slideDown();
            if($('.select-origin option:selected').val() == 'selectOption1'){
            	$("#fldjstree").css('display','block');
				$("#MainClsTree").css('display','none');
            }else{
            	$("#fldjstree").css('display','none');
				$("#MainClsTree").css('display','block');
            }
            
        } else {
            $("nav .nav_wrap .gnb").find('.sub').slideUp();
            if($('.select-origin option:selected').val() == 'selectOption1'){
            	$("#fldjstree").css('display','block');
				$("#MainClsTree").css('display','none');
            }else{
            	$("#fldjstree").css('display','none');
				$("#MainClsTree").css('display','block');
            }
        }
    });
    */
    //공유문서 슬라이드 이벤트
    $('nav .nav_wrap .gnb .shareDocMenu a').unbind("click").bind("click", function(){
    	console.log("shareDocMenu click");
        $("nav .nav_wrap .gnb .subMenu1").toggleClass('slide');
        $("nav .nav_wrap .gnb .shareDocMenu").find('.openBtn').toggleClass('turn');
        if($("nav .nav_wrap .gnb .subMenu1").hasClass('slide')){
            $("nav .nav_wrap .gnb .shareDocMenu").find('.sub').slideDown();
        } else {
            $("nav .nav_wrap .gnb .shareDocMenu").find('.sub').slideUp();
        }
        
        slideMenuInfo("shareDocList");
    });
    
    //승인문서 슬라이드 이벤트
    $('nav .nav_wrap .gnb .aprovMenu a').unbind("click").bind("click", function(){
        $("nav .nav_wrap .gnb .subMenu2").toggleClass('slide');
        $("nav .nav_wrap .gnb .aprovMenu").find('.openBtn').toggleClass('turn');
        if($("nav .nav_wrap .gnb .subMenu2").hasClass('slide')){
            $("nav .nav_wrap .gnb .aprovMenu").find('.sub').slideDown();
        } else {
            $("nav .nav_wrap .gnb .aprovMenu").find('.sub').slideUp();
        }
        slideMenuInfo("aprovList");
    });
    
    
    //최근항목 슬라이드 이벤트
    $('nav .nav_wrap .gnb .latelyMenu a').unbind("click").bind("click", function(){
        $("nav .nav_wrap .gnb .subMenu3").toggleClass('slide');
        $("nav .nav_wrap .gnb .latelyMenu").find('.openBtn').toggleClass('turn');
        if($("nav .nav_wrap .gnb .subMenu3").hasClass('slide')){
            $("nav .nav_wrap .gnb .latelyMenu").find('.sub').slideDown();
        } else {
            $("nav .nav_wrap .gnb .latelyMenu").find('.sub').slideUp();
        }
        slideMenuInfo("latelyList");
    });
    
    //즐겨찾기 슬라이드 이벤트
    $('nav .nav_wrap .gnb .fvtMenu a').unbind("click").bind("click", function(){
        $("nav .nav_wrap .gnb .subMenu4").toggleClass('slide');
        $("nav .nav_wrap .gnb .fvtMenu").find('.openBtn').toggleClass('turn');
        if($("nav .nav_wrap .gnb .subMenu4").hasClass('slide')){
            $("nav .nav_wrap .gnb .fvtMenu").find('.sub').slideDown();
        } else {
            $("nav .nav_wrap .gnb .fvtMenu").find('.sub').slideUp();
        }
        slideMenuInfo("fvtList");
    });
    

    //tooltip
    $('.tooltip_wrap button').unbind("click").bind("click",function(){
        $(this).parent().find('.ui_popup').slideDown();
        
        if($('#adSearchOK').is(':visible')){
			console.log("adSearchOK show");        
        
        	$("#adSearchTitle").val($("#searchTitle").val());
        }
        
        
    });

	$('.tooltip_wrap #upload').unbind("click").bind("click",function(){
       $('.bg').fadeIn();
       $('.popup').fadeIn();
    });

    $('.ui_popup ul li a').click(function(){
        $('.ui_popup').slideUp();
    })

    $(document).mouseup(function (e){

        var container = $('.ui_popup');
    
        if( container.has(e.target).length === 0){
    
          container.slideUp();
    
        }
    
      });


    //mobile menu

    $('.header_bar a').unbind("click").bind("click", function(){
        $('.header_bg').fadeIn();
        $('.myPage').animate({right:0});
    });

    $('.closebtn').unbind("click").bind("click", function(){
        $('.header_bg').fadeOut();
        $(this).parent().animate({right:-300})
        $('nav').width('42px')
    });

    //list:checked
    $('table tbody input[type="checkbox"]').unbind("click").bind("click", function(){
        if($(this).is(":checked")){
            console.log('checked')
            $(this).closest('tr').css('background-color','#fff6de');
        } else {
            $(this).closest('tr').css('background-color','inherit');
        }
        
    })

    // allcheck
    $('#allchk').unbind("click").bind("click", function(){
        if($('#allchk').prop("checked")){
            $('table input[type=checkbox]').prop('checked',true);
            $('table tbody input[type=checkbox]').closest('tr').css('background-color','#fff6de');
        } else {
            $('table input[type=checkbox]').prop('checked',false);
            $('table tbody input[type=checkbox]').closest('tr').css('background-color','inherit');
        }
    });

    var checkLgt = $('table input[type=checkbox]').length;
    $('table input[type=checkbox]').unbind("click").bind("click", function(){
        if($('.popup .tabCont #cont01 div .fileLists input[type=checkbox]:checked').length == checkLgt){
            $('#allchk').prop('checked',true);
        } else {
            $('#allchk').prop('checked',false);
        }
    });

    $('#allchk2').unbind("click").bind("click", function(){
        if($('#allchk2').prop("checked")){
            $('.popup .tabCont #cont01 div .fileList input[type=checkbox]').prop('checked',true);
            $('.popup .tabCont #cont01 div .fileList tbody input[type=checkbox]').closest('tr').css('background-color','#fff6de');
        } else {
            $('.popup .tabCont #cont01 div .fileList input[type=checkbox]').prop('checked',false);
            $('.popup .tabCont #cont01 div .fileList tbody input[type=checkbox]').closest('tr').css('background-color','inherit');
        }
    });

    var checkLgt2 = $('.popup .tabCont #cont01 div .fileList input[type=checkbox]').length;

    $('.popup .tabCont #cont01 div .fileList input[type=checkbox]').unbind("click").bind("click", function(){
        if($('.popup .tabCont #cont01 div .fileList input[type=checkbox]:checked').length == checkLgt2){
            $('#allchk2').prop('checked',true);
        } else {
            $('#allchk2').prop('checked',false);
        }
    });

    // mobile 제이쿼리
    var devWidth = $(window).width();
    if( devWidth < 1100 ){
        $('nav').addClass('navSlide')
            $('.nav_wrap').css('width','42px');
            $('.nav_btn').css('left','42px');
            $('.nav_btn img').attr('src','../resources/images/icon/menu_r_01.png');
            $('.nav_wrap').find('.foldingMn').css('display','none');
            $('.nav_wrap').find('.gnb').css('padding','inherit');
            $('.custom-select .custom-select-img').css('transform','translate(8px, 29px)');
            $('.nav_wrap').find('.sub').slideUp();
          	$("#searchFldName").hide(); 
          	$("#searchClsName").hide();
    }

    $(window).resize(function () {
        var mobWidth = $(window).width();

        if( mobWidth < 1100 ){
            $('nav').addClass('navSlide')
            $('.nav_wrap').css('width','42px');
            $('.nav_btn').css('left','42px');
            $('.nav_btn img').attr('src','../resources/images/icon/menu_r_01.png');
            $('.nav_wrap').find('.foldingMn').css('display','none');
            $('.nav_wrap').find('.gnb').css('padding','inherit');
            $('.custom-select .custom-select-img').css('transform','translate(8px, 29px)');
            $('.nav_wrap').find('.sub').slideUp();
            $("#searchFldName").hide();
            $("#searchClsName").hide();
        } else {
            $('.nav_wrap').css('width','285px');
            $('.nav_btn').css('left','285px');
            $('.nav_btn img').attr('src','../resources/images/icon/menu_r_02.png');
            $('.nav_wrap').find('.foldingMn').fadeIn();
            $('.nav_wrap').find('.gnb').css('padding','18px');
            $('.custom-select .custom-select-img').css('transform','translate(27px, 7px)')
            $('#content').css('padding-left','285px');
          
            
	        if($('.select-origin option:selected').val() == 'selectOption1'){
	          	$("#searchFldName").show(); 
	          	$("#searchClsName").hide();
	        	$("#fldjstree").css('display','block');
				$("#MainClsTree").css('display','none');
			}else{
				$("#searchFldName").hide(); 
	          	$("#searchClsName").show();
	        	$("#fldjstree").css('display','none');
				$("#MainClsTree").css('display','block');
			}
        };

    });

    //resizable
    $("#resizable").resizable({
        handles : 'e',
        maxWidth : 500,
        minWidth : 285,
        resize: function(e , ui){
			var navWidth = ui.size.width;
            $('nav .nav_wrap').width(navWidth);
            $('nav .nav_btn').css('left', navWidth);
            $('#content').css('padding-left', navWidth);

            if(navWidth > 42){
                $('.nav_wrap').find('.foldingMn').fadeIn();
                $('.nav_wrap').find('.gnb').css('padding','18px');
                $('.custom-select .custom-select-img').css('transform','translate(27px, 7px)');
            }
        }
      });

    //tabmenu
    $('.popup .tabmenu li').unbind("click").bind("click", function(){
        var tabNum = $(this).index();
		console.log("tabNum :" + tabNum);
        $(this).siblings().removeClass('on');
        $(this).addClass('on');
		
        $('.tabCont .contdiv').css('display','none');
        $('.tabCont .contdiv').eq(tabNum).css('display','block');

		liTabNum =  tabNum;
		console.log("liTabNum : " + liTabNum);
    });

	var subTabNum = 0;
	//tabmenu2
    $('.popup .tabmenu2 li').unbind("click").bind("click", function(){
        var tabNum = $(this).index();
        $(this).siblings().removeClass('on');
        $(this).addClass('on');
		
        $('.tabCont .contdiv2').css('display','none');
        $('.tabCont .contdiv2').eq(tabNum).css('display','block');

		subTabNum =  tabNum;
		console.log("subTabNum : " + subTabNum);
    });

	// 팝업 닫기
	$('#closeBtn').unbind("click").bind("click", function(){
		$('.bg').fadeOut();
        $('.popup').fadeOut();
    });
});





