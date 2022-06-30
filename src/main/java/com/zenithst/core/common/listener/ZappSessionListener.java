package com.zenithst.core.common.listener;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zenithst.framework.conts.ZstFwConst;
import com.zenithst.framework.util.ZstFwValidatorUtils;

@WebListener
public class ZappSessionListener implements HttpSessionListener {

	/* Log */
	protected final static Logger logger = LoggerFactory.getLogger(ZappSessionListener.class);
	
	/* 세션 저장 Map */
	private static final Map<String, HttpSession> sessions = new ConcurrentHashMap<String, HttpSession>();
	
	// 세션 저장
	public synchronized static void addSession(HttpSession pHttpSession) {
		sessions.put(pHttpSession.getId(), pHttpSession);
	}
	
	//중복로그인 지우기
    public synchronized static String removePrevSession(String idtype, String id, String iptype, String ip) {
        
    	logger.info("[중복 세션 체크 및 삭제 처리]");
    	
    	String sessId = ZstFwConst.SCHARS.BLANK.character;
    	
        for(String sesskey : sessions.keySet()){
            HttpSession httpSession = sessions.get(sesskey);
            if(httpSession != null) {
            	if(httpSession.getAttribute(idtype) != null && httpSession.getAttribute(idtype).equals(id)) {
            		if(httpSession.getAttribute(iptype) != null && !httpSession.getAttribute(iptype).equals(ip)) {
            			sessId = sesskey;
            			removeSessionForDoubleLogin(sesskey);
            		}
            	}
            }
        }
        
        return sessId;
    }
    
	//중복로그인 지우기
    public synchronized static String removePrevSession(Map<String, String> pSessCheck) {
        
    	logger.info("[중복 세션 체크 및 삭제 처리]");
    	
    	String sessId = ZstFwConst.SCHARS.BLANK.character;
    	
        for(String sesskey : sessions.keySet()){
            HttpSession httpSession = sessions.get(sesskey);
            
            if(httpSession != null) {
            	
            	boolean[] exist = new boolean[pSessCheck.size()]; Arrays.fill(exist, Boolean.FALSE);
            	int IDX = ZstFwConst.NUMS.ZERO.num;
            	for(String checkkey : pSessCheck.keySet()) {
            		if(httpSession.getAttribute(checkkey) != null && httpSession.getAttribute(checkkey).equals(pSessCheck.get(checkkey))) {
            			logger.info("[" + checkkey + "] : YES");
            			exist[IDX] = true;
            		} else {
            			logger.info("[" + checkkey + "] : NO");
            		}
            		IDX++;
            	}
            	
            	if(Arrays.asList(exist).contains(Boolean.FALSE) == true) {
            		sessId =  sesskey.toString();
            		break;
            	}
            }
        }
        
        // 세션 삭제
        if(ZstFwValidatorUtils.valid(sessId)) {
        	removeSessionForDoubleLogin(sessId);
        }
        
        return sessId;
    }
    
    /**
     * 세션제거 
     * @param userId
     */
    private static void removeSessionForDoubleLogin(String sessId){    	
    	logger.info("[removeSessionForDoubleLogin] sessId : " + sessId);
        if(ZstFwValidatorUtils.valid(sessId)){
            sessions.get(sessId).invalidate();
            sessions.remove(sessId); 
            logger.info("[removeSessionForDoubleLogin] sessId is removed.");
        }
    }
    
    /**
     * 세션이 생성 될 때
     */
	public void sessionCreated(HttpSessionEvent se) {
		logger.info("[sessionCreated] getId : " + se.getSession().getId());
		sessions.put(se.getSession().getId(), se.getSession());
	}

	/**
	 * 세션이 종료 될 때
	 */
	public void sessionDestroyed(HttpSessionEvent se) {
		if(sessions.get(se.getSession().getId()) != null){
			logger.info("[sessionDestroyed] getId : " + se.getSession().getId());
            sessions.get(se.getSession().getId()).invalidate();
            sessions.remove(se.getSession().getId());	
        }
	}

}
