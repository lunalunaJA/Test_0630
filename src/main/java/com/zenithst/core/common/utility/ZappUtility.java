package com.zenithst.core.common.utility;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ZappUtility {

	/**
	 * 문자열을 Int로 파싱하고, null일 경우 0으로 초기 대입한다.
	 * @param parameterValue
	 * @return
	 */
	public static int parseInt( String parameterValue )
	{
		try
		{
			return ( parameterValue == null || parameterValue == "")? 0:Integer.parseInt ( parameterValue );
		}catch(Exception e){
			return 0;
		}
	}

	/**
	 * 문자열을 Int로 파싱하고, null일 경우 initValue로 초기 대입한다.
	 * @param parameterValue
	 * @param initVal
	 * @return
	 */
	public static int parseInt( String parameterValue, int initVal )
	{
		try
		{
			return ( parameterValue == null || parameterValue == "")? initVal:Integer.parseInt ( parameterValue );
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	/**
	 * 문자열을 파싱하고, null일 경우 ""으로 초기 대입한다.
	 * @param parameterValue
	 * @return
	 */
	public static String parseString( String parameterValue )
	{
		try
		{
			return ( parameterValue == null ||  parameterValue == "" )? "": parameterValue.trim();
		}
		catch(Exception e)
		{
			return "";
		}
	}

	/**
	 * 문자열을 파싱하고, null일 경우 ""으로 초기 대입한다.
	 * @param parameterValue
	 * @return
	 */
	public static String parseObject( Object object )
	{
		try
		{
			return ( object == null )? "" : object.toString();
		}
		catch(Exception e)
		{
			return "";
		}
	}
	
	/**
	 * 문자열을 replace/파싱하고, null일 경우 ""으로 초기 대입한다.
	 * @param parameterValue
	 * @return
	 */
	public static String replaceParseString( String parameterValue )
	{
		try
		{
			return ( parameterValue == null ||  parameterValue == "" )? "": parameterValue.replaceAll(" ", "");
		}
		catch(Exception e)
		{
			return "";
		}
	}

	/**
	 * 문자열을 replace/파싱하고, null일 경우 ""으로 초기 대입한다.
	 * @param parameterValue
	 * @return
	 */
	public static String replaceParseString( String parameterValue, String psStr )
	{
		try
		{
			return ( parameterValue == null ||  parameterValue == "" )? "": parameterValue.replaceAll(psStr, "");
		}
		catch(Exception e)
		{
			return "";
		}
	}

	/**
	 * 문자열을 replace/파싱하고, null일 경우 ""으로 초기 대입한다.
	 * @param parameterValue
	 * @return
	 */
	public static String replaceParseString( String parameterValue, String psStr1, String psStr2 )
	{
		try
		{
			return ( parameterValue == null ||  parameterValue == "" )? "": parameterValue.replaceAll(psStr1, psStr2);
		}
		catch(Exception e)
		{
			return "";
		}
	}

	/**
	 * 문자열을 파싱하고, null일 경우 initVale으로 초기 대입한다.
	 * @param parameterValue
	 * @param initVal
	 * @return
	 */
	public static String parseString( String parameterValue, String initVale )
	{
		try
		{
			return ( parameterValue == null || parameterValue == "")? initVale: parameterValue;
		}
		catch(Exception e)
		{
			return "";
		}
	}

	/**
	* 입력받은 스트링값을 8859형태로 읽어들여 KSC5601형태로 변형후 결과값반환
	*
	*	@param str	 변환대상 문자열
	*	@return ksc5601형태로 변경된 문자열
	*/
    public static String uniToKsc(String str)
    {
    	int	i = str.length();

        for(int j = 0; j < i; j++)
        {
            char c = str.charAt(j);

            if(c >= '\uAC00')
            {
                return str;
            }
        }

        return str != null ? convertCharSet(str, "8859_1", "KSC5601") : "";
    }

    public static String kscToUni(String str)
    {
//    	int	i = str.length();
//
//        for(int j = 0; j < i; j++)
//        {
//            char c = str.charAt(j);
//
//            if(c < '\uAC00')
//            {
//                return str;
//            }
//        }

        return str != null ? convertCharSet(str, "KSC5601", "8859_1") : "";
    }


	/**
	* 입력받은 스트링값을 ksc5601형태로 읽어들여 8859_1형태로 변형후 결과값반환
	*
	*	@param str	 변환대상 문자열
	*	@return 8859형태로 변경된 문자열
	*/
    public static String convertCharSet(String source, String decoder, String encoder)
    {
        try
        {
            return new String(source.getBytes(decoder), encoder);
        }
        catch(UnsupportedEncodingException ce)
        {
            return "";
        }
    }

    /**
     * 오류발생시 문자열로 변경 처리
     * @param poException
     * @return
     */
	public static String rtnErrMessage(Exception poException)
	{
		ByteArrayOutputStream 	oByteArrayOutputStream 	= new ByteArrayOutputStream();
		PrintStream 			oPrintStream 			= new PrintStream(oByteArrayOutputStream);

		String	message	= "";

		try
		{
			poException.printStackTrace(oPrintStream);
			message = oByteArrayOutputStream.toString();
		}
		catch(Exception e)
		{
			message="rtnErrMessage Exception : " + e.toString();
		}

		return message;
	}

	/**
	 * 문자열의 길이(byte)를 계산해 준다.( 영어, 숫자: 1byte/한글:[EUC-KR: 2byte, UTF-8:3byte, 기타 : 2byte])
	 * str.getBytes()로 계산 하여도 되지만 Char-set 따라 byte가 틀리므로 메소드 필요
	 * @param str
	 * @return
	 */
	public static int getByteSizeToComplex(String str, String encode)
	{
		int en = 0;
		int ko = 0;
		int etc = 0;

		char[] string = str.toCharArray();

		for (int j=0; j<string.length; j++)
		{
			if (string[j]>='A' && string[j]<='z')
			{
				en++;
			}
			else if (string[j]>='\uAC00' && string[j]<='\uD7A3')
			{
				if("euc-kr".equals(encode.toLowerCase()))
				{
					ko++;
					ko++;
				}
				else if("utf-8".equals(encode.toLowerCase()))
				{
					ko++;
					ko++;
					ko++;
				}
				else
				{
					ko++;
					ko++;
				}
			}
			else
			{
				etc++;
			}
		}

		return (en + ko + etc);
	}

    /**
     * request에 넘어온 파라메터를 jsp로 넘길 파라메터 형식으로 변경한다.
     *
     * @param  request
     * @return parmStr
     */
    public static String getParamSet(HttpServletRequest request)
    {
        String parmStr = "";
        String str = "";

        int cnt = 0;

        for(Enumeration<?> e = request.getParameterNames(); e.hasMoreElements(); )
        {
            str = (String)e.nextElement();

        	if( cnt == 0 )
        	{
//        		parmStr += str + "=" + Utility.uniToKsc(Utility.parseString(request.getParameter(str)));
        		parmStr += str + "=" + ZappUtility.parseString(request.getParameter(str));
        	}
        	else
        	{
//        		parmStr += "&" + str + "=" + ZappUtility.uniToKsc(ZappUtility.parseString(request.getParameter(str)));
        		parmStr += "&" + str + "=" + ZappUtility.parseString(request.getParameter(str));
        	}
        	cnt++;
        }

        return parmStr;
    }

    /**
     * request에 넘어온 파라메터를 jsp로 넘길 파라메터 형식으로 변경한다.
     *
     * @param  request
     * @return parmStr
     */
    public static String getParamSetUniToKsc(HttpServletRequest request)
    {
    	String parmStr = "";
    	String str = "";

    	int cnt = 0;

    	for(Enumeration<?> e = request.getParameterNames(); e.hasMoreElements(); )
    	{
    		str = (String)e.nextElement();

    		if( cnt == 0 )
    		{
        		parmStr += "?" + str + "=" + ZappUtility.uniToKsc(ZappUtility.parseString(request.getParameter(str)));
    		}
    		else
    		{
        		parmStr += "&" + str + "=" + ZappUtility.uniToKsc(ZappUtility.parseString(request.getParameter(str)));
    		}
    		cnt++;
    	}

    	return parmStr;
    }

    /**
     * 체크할 파라메터 리스트중 입력된 값이 한개 이상인지 체크한다.
     *
     * @param 	map
     * @param 	parmList - 체크할 파라메터 리스트
     * @return 	bChk - 파라메터 중에서 입력된 값이 있으면 treu 를 리턴한다.
     */
    public static boolean getParameterNullChk(Map<String, String> map, String[] parmList)
    {
    	boolean bChk = false;

    	for(String parm : parmList)
    	{
    		if(!"".equals(ZappUtility.parseString(map.get(parm))))
    		{
    			bChk = true;
    			break;
    		}
    	}

    	return bChk;
    }

    /**
     * request에 넘어온 파라메터 리스트를 문자열로 변경한다. - 로그 출력용
     *
     * @param  request
     * @return parmStr
     */
    public static String getParamStr(HttpServletRequest request)
    {
        String parmStr = "";
        String str = "";

        for(Enumeration<?> e = request.getParameterNames(); e.hasMoreElements(); )
        {
            str = (String)e.nextElement();

            parmStr += "[" + str + ">" + ZappUtility.uniToKsc(ZappUtility.parseString(request.getParameter(str))) + "]";
        }

        return parmStr;
    }

	/**
	*	int type의 숫자를 입력받아 일정한 format에 의하여 대상 문자열을 해당 format으로 변경시킨다.
	*
	*	@param	number	변경시킬 int형태의 수
	*	@param	format  해당 수를 변경시키기 위한 format
	*	@return	변경되어진 문자열 값
	*/
	public static String formatNumber(int number , String format)
	{
		DecimalFormat formatter = new DecimalFormat(format);
		return formatter.format(number);
	}

	/**
	*	long type의 숫자를 입력받아 일정한 format에 의하여 대상 문자열을 해당 format으로 변경시킨다.
	*
	*	@param	number	변경시킬 long 수
	*	@param	format  해당 수를 변경시키기 위한 format
	*	@return	변경되어진 문자열 값
	*/
	public static String formatNumber(long number , String format)
	{
		DecimalFormat formatter = new DecimalFormat(format);
		return formatter.format(number);
	}

	/**
	*	float type의 숫자를 입력받아 일정한 format에 의하여 대상 문자열을 해당 format으로 변경시킨다.
	*
	*	@param	number	변경시킬 float 수
	*	@param	format  해당 수를 변경시키기 위한 format
	*	@return	변경되어진 문자열 값
	*/
	public static String formatNumber(float number , String format)
	{
		DecimalFormat formatter = new DecimalFormat(format);
		return formatter.format(number);
	}

	/**
	*	double type의 숫자를 입력받아 일정한 format에 의하여 대상 문자열을 해당 format으로 변경시킨다.
	*
	*	@param	number	변경시킬 double형태의 수
	*	@param	format  해당 수를 변경시키기 위한 format
	*	@return	변경되어진 문자열 값
	*/
	public static String formatNumber(double number , String format)
	{
		DecimalFormat formatter = new DecimalFormat(format);
		return formatter.format(number);
	}

	/**
	*	BigDecimal type의 숫자를 입력받아 일정한 format에 의하여 대상 문자열을 해당 format으로 변경시킨다.
	*
	*	@param	number	변경시킬 double형태의 수
	*	@param	format  해당 수를 변경시키기 위한 format
	*	@return	변경되어진 문자열 값
	*/
	public static String formatNumber(BigDecimal number , String format)
	{
		DecimalFormat formatter = new DecimalFormat(format);
		return formatter.format(number);
	}

	public static String unFiltering(String value)
    {
		return ("".equals(ZappUtility.parseString(value)))? "" : value.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&#39;", "'").replaceAll("&quot;", "\"");
    }

	public static String clientIp(HttpServletRequest request) {

		String clientIp = "";
		try
		{
			clientIp = ZappUtility.parseString(request.getHeader("WL-Proxy-Client-IP"));

		    if("".equals(clientIp))
		    {
		    	clientIp = ZappUtility.parseString(request.getHeader("Proxy-Client-IP"));

		        if("".equals(clientIp))
	            {
		        	clientIp = ZappUtility.parseString(request.getHeader("X-Forwarded-For"));

	                if("".equals(clientIp))
	                {
	                	clientIp = ZappUtility.parseString(request.getRemoteAddr());
	                }
	            }
		    }
		}
		catch(Exception e)
		{
			clientIp = ZappUtility.parseString(request.getRemoteAddr());
		}

		return clientIp;
	}
	
}
