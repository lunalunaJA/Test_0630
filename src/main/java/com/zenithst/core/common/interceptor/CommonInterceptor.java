package com.zenithst.core.common.interceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.zenithst.core.common.exception.ZappException;
import com.zenithst.framework.domain.ZstFwResult;

public class CommonInterceptor extends HandlerInterceptorAdapter{
 
	/* License Path  */
//	@Value("#{archiveconfig['LIC_PATH']}")
//	protected String LIC_PATH;
	
	protected Log log = LogFactory.getLog(CommonInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
    	
    	String URI = request.getRequestURI().toString();

    	if (URI.lastIndexOf(".png") > 0 || URI.lastIndexOf(".gif") > 0 || URI.lastIndexOf(".js") > 0 || URI.lastIndexOf(".css") > 0) {
    		// Do Nothing
    	} 
    	else if (URI.lastIndexOf("checkMultiDept") > 0 
    			|| URI.lastIndexOf("auth/connect") > 0
    			|| URI.lastIndexOf("login.do") > 0) // for Central 
    	{
	    	log.debug("=================================================================================");
	    	log.debug("========= RequestURI: " + URI + " =========");
	    	log.debug("=================================================================================");
    	} 
    	else {
	    	log.debug("=================================================================================");
	    	log.debug("========= RequestURI: " + URI + " =========");
	    	log.debug("=================================================================================");
	    	
//	    	License pLicense = new License();
//	    	pLicense.setFilepath(LIC_PATH);
//	    	try {
//	    		ZenithLicense.checkLic(pLicense);
//	    	} catch(ZenithLicenseException e) {
//	    		
//	    		ZstFwResult result = new ZstFwResult();
//	    		result.setResCode("ERR_LICENSE");
//	    		result.setMessage(e.getMessage());
//	    		throw new ZappException(result.getMessage(), result);
//	    		
//	    	}
	    	
	    	HttpSession pHttpSession = request.getSession();
	    	if(pHttpSession == null) {
	    		return false;
	    	}
	    	
	    	if (URI.lastIndexOf("/main/") > 0 || URI.lastIndexOf("/doc/") > 0) { // Central
	    		// TODO: Token 처리?
	    		log.info("[Interceptor] Skip Central API");
	    	} else if (URI.lastIndexOf("/api/file/fileDown") > 0) { // 
		    		log.info("[Interceptor] Skip fileDown");
	    	} else {
				Enumeration<String> sessAttrNames = pHttpSession.getAttributeNames();                
				while(sessAttrNames.hasMoreElements()){
					String attrName = sessAttrNames.nextElement();
			        Object attrValue = request.getAttribute(attrName);
			        //log.debug("=== SessionAttribute, " + attrName + " : " + attrValue);
			    }
				
				if(pHttpSession.getAttribute("validLic") == null) {
					return false;
				}
				
				boolean VALID_LIC = (Boolean) pHttpSession.getAttribute("validLic");
	    		log.info("[Interceptor] VALID_LIC = " + VALID_LIC);
		    	if(VALID_LIC == true) {
		    		return true;
		    	} else {
		    		ZstFwResult result = new ZstFwResult();
		    		result.setResCode("ERR_LICENSE");
		    		result.setMessage("License is not valid.");
		    		throw new ZappException(result.getMessage(), result);
		    	}
	    	}
		    /*
		    Enumeration e = request.getParameterNames();
			while ( e.hasMoreElements() ){
				String name = (String) e.nextElement();
				String[] values = request.getParameterValues(name);		
				for (String value : values) {
					//log.debug("=== Parameter name=" + name + ",value=" + value);
				}   
			}
			
			Enumeration<String> attrNames = request.getAttributeNames();                
		      while(attrNames.hasMoreElements()){
		            String attrName = attrNames.nextElement();
		            Object attrValue = request.getAttribute(attrName);
		            //log.debug("=== Attribute " + attrName + " : " + attrValue);
		      }
	
			Enumeration<String> headerNames = request.getHeaderNames();               
		      while(headerNames.hasMoreElements()){
		            String hdrName = headerNames.nextElement();
		            Object hdrValue = request.getHeader(hdrName);
		            //log.debug("=== Header " + hdrName + " : " + hdrValue);
		      }
			//log.error("========= PathInfo: " + request.getPathInfo());
	    	//log.error("========= QueryString: " + request.getQueryString());
	    	 * 
	    	 */
    	}
        return super.preHandle(request, response, handler);
    }
 
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {

    	String URI = request.getRequestURI().toString();
    	if (URI.lastIndexOf(".png") > 0 || URI.lastIndexOf(".gif") > 0 || URI.lastIndexOf(".js") > 0 || URI.lastIndexOf(".css") > 0) {
    		
    	} else {
    		log.error("========= FINISH: " + URI + " ==========");
    		//log.error("======================================");
    	}
        super.postHandle(request, response, handler, modelAndView);
    }
     
}

