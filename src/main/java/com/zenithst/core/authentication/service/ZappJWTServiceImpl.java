package com.zenithst.core.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zenithst.archive.constant.Operators;
import com.zenithst.core.authentication.vo.ZappAuth;
import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.common.exception.ZappException;
import com.zenithst.core.common.exception.ZappFinalizing;
import com.zenithst.core.common.extend.ZappService;
import com.zenithst.core.common.message.ZappMessageMgtService;
import com.zenithst.core.common.utility.ZappJSONUtils;
import com.zenithst.core.organ.api.ZappOrganMgtService;
import com.zenithst.core.organ.vo.ZappCompany;
import com.zenithst.core.organ.vo.ZappDept;
import com.zenithst.core.organ.vo.ZappDeptUser;
import com.zenithst.core.organ.vo.ZappDeptUserExtend;
import com.zenithst.core.organ.vo.ZappGroupUser;
import com.zenithst.core.organ.vo.ZappGroupUserExtend;
import com.zenithst.core.organ.vo.ZappUser;
import com.zenithst.core.system.api.ZappSystemMgtService;
import com.zenithst.core.system.vo.ZappEnv;
import com.zenithst.framework.domain.ZstFwResult;
import com.zenithst.framework.util.ZstFwEncodeUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

@SuppressWarnings("restriction")
@Service("zappJWTService")
public class ZappJWTServiceImpl extends ZappService implements ZappJWTService {

	/* Authentication */
	@Resource(name="zappAuthenticationService")
	private ZappAuthenticationService zappAuthenticationService;
	
	// Organization
	@Autowired
	private ZappOrganMgtService organService;
	
	// System
	@Autowired
	private ZappSystemMgtService systemService;
	
	// Message
	@Autowired
	private ZappMessageMgtService messageService;
	
	
	/* Key */
	private static String secretKey = ZstFwEncodeUtils.encodeString_SHA512("ThisisforZenithstJwtSecurityKey.");

	
	public boolean checkJWT(ZappAuth pZappAuth, HttpServletRequest pRequest) {

		try {
			Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
	                .parseClaimsJws(pZappAuth.getObjJwt()).getBody(); // If successful, the token is a normal token.
	
	        logger.info("expireTime :" + claims.getExpiration());
	        logger.info("accessid :" + claims.get("accessid"));
	        logger.info("accesspoint :" + claims.get("accesspoint"));
	        
	        if(pRequest.getRemoteAddr().equals(claims.get("accesspoint"))) {
	        	return false;
	        }
	
	        return true;
		} catch (ExpiredJwtException exception) {
			logger.info("token expires");
            return false;
        } catch (JwtException exception) {
        	logger.info("token is changed");
            return false;
        }

	}
	
	@SuppressWarnings("unchecked")
	public ZappAuth checkJWT_Obj(ZappAuth pZappAuth) {
		
		ZappAuth rZappAuth = new ZappAuth();

		try {
			 Jwts.parser()
			 	.setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)) 
			 	.parseClaimsJws(pZappAuth.getObjJwt()); 
			 // If successful, the token is a normal token.
	
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(pZappAuth.getObjJwt());
	        
			// String Type
			rZappAuth.setObjCompanyid((String) claims.getBody().get(JWTConts.COMPANY_ID.id));
			rZappAuth.setObjDeptid((String) claims.getBody().get(JWTConts.DEPT_ID.id));
			rZappAuth.setObjlang((String) claims.getBody().get(JWTConts.LANG.id));
			
			// Object Type
			rZappAuth.setSessUser((ZappUser) ZappJSONUtils.cvrtJsonToObj(new ZappUser(), (String) claims.getBody().get(JWTConts.USER_OBJ.id)));
			rZappAuth.setSessDept((ZappDept) ZappJSONUtils.cvrtJsonToObj(new ZappDept(), (String) claims.getBody().get(JWTConts.DEPT_OBJ.id)));
			rZappAuth.setSessDeptUser((ZappDeptUserExtend) ZappJSONUtils.cvrtJsonToObj(new ZappDeptUserExtend(), (String) claims.getBody().get(JWTConts.DEPTUSER_OBJ.id)));
			rZappAuth.setSessOnlyDeptUser((ZappDeptUser) ZappJSONUtils.cvrtJsonToObj(new ZappDeptUser(), (String) claims.getBody().get(JWTConts.ONLY_DEPTUSER_OBJ.id)));
			rZappAuth.setSessDeptUsers((List<ZappDeptUserExtend>) ZappJSONUtils.cvrtJsonToObj(new ArrayList<ZappDeptUserExtend>(), (String) claims.getBody().get(JWTConts.DEPTUSER_OBJS.id)));
			
			// List Type
			rZappAuth.setSessOnlyDeptUsers((List<ZappDeptUser>) ZappJSONUtils.cvrtJsonToObj(new ArrayList<ZappDeptUser>(), (String) claims.getBody().get(JWTConts.ONLY_DEPTUSER_OBJS.id)));
			rZappAuth.setSessGroupUsers((List<ZappGroupUserExtend>) ZappJSONUtils.cvrtJsonToObj(new ArrayList<ZappGroupUserExtend>(), (String) claims.getBody().get(JWTConts.GROUPUSER_OBJS.id)));
			rZappAuth.setSessOnlyGroupUsers((List<ZappGroupUser>) ZappJSONUtils.cvrtJsonToObj(new ArrayList<ZappGroupUser>(), (String) claims.getBody().get(JWTConts.ONLY_GROUPUSER_OBJS.id)));
			List<String> lidentifier = new ArrayList<String>(Arrays.asList("LIST"));
			List<String> sessAllLowerDepts = (List<String>) ZappJSONUtils.cvrtJsonToObj(lidentifier, (String) claims.getBody().get(JWTConts.LOWER_ALL_DEPTS.id));
			rZappAuth.setSessAllLowerDepts(sessAllLowerDepts);			
			List<String> sessAclObjList = (List<String>) ZappJSONUtils.cvrtJsonToObj(lidentifier, (String) claims.getBody().get(JWTConts.ACL_OBJS.id));
			rZappAuth.setSessAclObjList(sessAclObjList);
	
			// Map type
			Map<String, String> midentifier = new HashMap<String, String>() {{  put("MAP", ""); }};
			Map<String, ZappEnv> sessEnv_Company = new HashMap<String, ZappEnv>();
			Map<String, String> sessEnvm = (Map<String, String>) ZappJSONUtils.cvrtJsonToObj(midentifier, (String) claims.getBody().get(JWTConts.COMPANY_ENV.id));
			for (Map.Entry<String, String> entry : sessEnvm.entrySet()) {
				sessEnv_Company.put(entry.getKey(), (ZappEnv) ZappJSONUtils.cvrtJsonToObj(new ZappEnv(), (String) entry.getValue()));
			}
			rZappAuth.setSessEnv(sessEnv_Company);
			Map<String, ZappEnv> sessEnv_User = new HashMap<String, ZappEnv>();
			sessEnvm = (Map<String, String>) ZappJSONUtils.cvrtJsonToObj(midentifier, (String) claims.getBody().get(JWTConts.USER_ENV.id));
			for (Map.Entry<String, String> entry : sessEnvm.entrySet()) {
				sessEnv_User.put(entry.getKey(), (ZappEnv) ZappJSONUtils.cvrtJsonToObj(new ZappEnv(), (String) entry.getValue()));
			}
			rZappAuth.setSessUserEnv(sessEnv_User);
			
			// Boolean
			rZappAuth.setObjValidLic((Boolean) claims.getBody().get(JWTConts.VALID_LIC.id));
			
	        return rZappAuth;
	        
		} catch (ExpiredJwtException exception) {
			logger.info("token expires");
            return null;
        } catch (JwtException exception) {
        	logger.info("token is changed");
            return null;
        } 
		
	}	

	@SuppressWarnings("unchecked")
	public ZappAuth checkJWT_Simple(ZappAuth pObjAuth) throws ZappException, SQLException {
		
		ZappAuth rZappAuth = new ZappAuth();
		ZstFwResult pObjRes = new ZstFwResult();

		try {
			 Jwts.parser()
			 	.setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)) 
			 	.parseClaimsJws(pObjAuth.getObjJwt()); 
			 // If successful, the token is a normal token.
	
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(pObjAuth.getObjJwt());
	        
			// String Type
			rZappAuth.setObjCompanyid((String) claims.getBody().get(JWTConts.COMPANY_ID.id));
			String _DEPTUSERID = ((String) claims.getBody().get(JWTConts.DEPTUSER_OBJ.id));
			if(ZstFwValidatorUtils.valid(_DEPTUSERID) == false) {
				return null;
			}
			
			// Boolean
			rZappAuth.setObjValidLic((Boolean) claims.getBody().get(JWTConts.VALID_LIC.id));

			/* */
			List<String> rSessAclObjList = new ArrayList<String>();
			
			
			/* Company */
			pObjRes = organService.selectObject(pObjAuth, new ZappCompany(rZappAuth.getObjCompanyid()), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return null;
			}
			if(pObjRes.getResObj() != null) {
				rZappAuth.setSessCompany((ZappCompany) pObjRes.getResObj());	
			}
			
			// DeptUser
			pObjRes = organService.selectObjectExtend(pObjAuth, new ZappDeptUser(_DEPTUSERID), pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return null;
			}
			if(pObjRes.getResObj() != null) {
				rZappAuth.setSessDeptUser((ZappDeptUserExtend) pObjRes.getResObj());
				rSessAclObjList.add(rZappAuth.getSessDeptUser().getDeptuserid());
				rSessAclObjList.add(rZappAuth.getSessDeptUser().getDeptid());
			}
			
			// User
			rZappAuth.setSessUser(rZappAuth.getSessDeptUser().getZappUser());
			
			// Dept
			rZappAuth.setSessDept(rZappAuth.getSessDeptUser().getZappDept());

			List<ZappDeptUserExtend> rZappDeptUserList = null;
			List<ZappDeptUser> rZappOnlyDeptUserList = new ArrayList<ZappDeptUser>();
			ZappDeptUser pZappDeptUser = new ZappDeptUser(null, rZappAuth.getSessDeptUser().getUserid());
			pZappDeptUser.setIsactive(YES);
			pObjRes = organService.selectObjectExtend(pObjAuth, pZappDeptUser, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return null;
			}
			rZappDeptUserList = (List<ZappDeptUserExtend>) pObjRes.getResObj();
			if(rZappDeptUserList == null) {
				return null;
			}
			for(ZappDeptUserExtend vo : rZappDeptUserList) {
				ZappDeptUser ivo = new ZappDeptUser();
				BeanUtils.copyProperties(vo, ivo);
				rZappOnlyDeptUserList.add(ivo);
			}
			rZappAuth.setSessDeptUsers(rZappDeptUserList);
			rZappAuth.setSessOnlyDeptUsers(rZappOnlyDeptUserList);

			
			// Group
			List<ZappGroupUser> rZappOnlyGroupUserList = new ArrayList<ZappGroupUser>();
			List<ZappGroupUserExtend> rZappGroupUserList = new ArrayList<ZappGroupUserExtend>();
			ZappGroupUser pZappGroupUser_Dept = new ZappGroupUser(null, rZappAuth.getSessDeptUser().getDeptid(), ZappConts.TYPES.OBJTYPE_DEPT.type);
			ZappGroupUser pZappGroupUser_User = new ZappGroupUser(null, pObjAuth.getSessDeptUser().getDeptuserid(), ZappConts.TYPES.OBJTYPE_USER.type);
			pObjRes = organService.selectAclObjectExtend(pObjAuth, null, pZappGroupUser_Dept, pZappGroupUser_User, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == true) {
				rZappGroupUserList = (List<ZappGroupUserExtend>) pObjRes.getResObj();
				rZappAuth.setSessGroupUsers(rZappGroupUserList);	
				for(ZappGroupUserExtend vo : rZappGroupUserList) {
					ZappGroupUser ivo = new ZappGroupUser();
					BeanUtils.copyProperties(vo, ivo);
					rZappOnlyGroupUserList.add(ivo);
					rSessAclObjList.add(vo.getGroupid());
				}
				rZappAuth.setSessOnlyGroupUsers(rZappOnlyGroupUserList);	
			}

			/* 권한 대상 정보 */
			HashSet<String> onlySessAclObjList = new HashSet<String>(rSessAclObjList);
			List<String> rSessAclObjList_ = new ArrayList<String>(onlySessAclObjList);
			rZappAuth.setSessAclObjList(rSessAclObjList_);
			
			/* [Inquiry all lower dept. Info]
			 * 
			 */
			if(rZappOnlyDeptUserList.size() > ZERO) {
				StringBuffer sbdeptid = new StringBuffer();
				ZappDept pZappDept_Filter = new ZappDept();
				pZappDept_Filter.setDeptid(Operators.IN.operator);
				ZappDept pZappDept_Value = new ZappDept();
				sbdeptid.setLength(ZERO);
				for(ZappDeptUser vo : rZappOnlyDeptUserList) {
					sbdeptid.append(vo.getDeptid() + DIVIDER);
				}
				pZappDept_Value.setDeptid(sbdeptid.toString());
				pObjRes = organService.selectObjectDown(pObjAuth, pZappDept_Filter, pZappDept_Value, pObjRes);
				if(ZappFinalizing.isSuccess(pObjRes) == true) {
					if(pObjRes.getResObj() != null) {
						List<ZappDept> tmpDeptList = (List<ZappDept>) pObjRes.getResObj();
						List<String> allLowerDeptList = new ArrayList<String>();
						for(ZappDept vo : tmpDeptList) {
							allLowerDeptList.add(vo.getDeptid());
						}
						rZappAuth.setSessAllLowerDepts(allLowerDeptList);
					} else {
						rZappAuth.setSessAllLowerDepts(new ArrayList<String>());
					}
				}
			}
			
			/* [Preferences] 
			 * Inquire preferences and store it in the authentication info.
			 */
			Map<String, ZappEnv> sessEnv = new HashMap<String, ZappEnv>();
			ZappEnv pZappEnv = new ZappEnv(rZappAuth.getObjCompanyid(), YES);
			pZappEnv.setUserid(rZappAuth.getObjCompanyid());  // Adding user id
			pObjRes = systemService.selectObject(pObjAuth, pZappEnv, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
			} 
			@SuppressWarnings("unchecked")
			List<ZappEnv> rZappEnvList = (List<ZappEnv>) pObjRes.getResObj();
			if(rZappEnvList == null) {
				return null;
			}
			if(rZappEnvList.size() == ZERO) {
				return null;
			}
			for(ZappEnv vo : rZappEnvList) {
				sessEnv.put(vo.getEnvkey(), vo);
			}
			rZappAuth.setSessEnv(sessEnv);

			/* [Personal Preferences]
			 * 
			 */
			sessEnv = new HashMap<String, ZappEnv>();
			pZappEnv = new ZappEnv(rZappAuth.getObjCompanyid(), YES);
			pZappEnv.setUserid(rZappAuth.getSessDeptUser().getUserid());  // Adding user id
			pObjRes = systemService.selectObject(pObjAuth, pZappEnv, pObjRes);
			if(ZappFinalizing.isSuccess(pObjRes) == false) {
				return null;
			} 
			@SuppressWarnings("unchecked")
			List<ZappEnv> rZappEnvList_ = (List<ZappEnv>) pObjRes.getResObj();
			for(ZappEnv vo : rZappEnvList_) {
				sessEnv.put(vo.getEnvkey(), vo);
			}
			rZappAuth.setSessUserEnv(sessEnv);
			
	        return rZappAuth;
	        
		} catch (ExpiredJwtException exception) {
			logger.info("token expires");
            return null;
        } catch (JwtException exception) {
        	logger.info("token is changed");
            return null;
        } 
		
	}	
	
	
	public String createJWT(ZappAuth pZappAuth, HttpServletRequest pRequest) {
		
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + 1000 * 60 * 10);
        @SuppressWarnings("restriction")
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        /* [JWT basic info.]       
         * Type, Algorithm 
         */
        Map<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("typ", ZappConts.AUTHENTICATION.JWT_TYPE.auth);
        headerMap.put("alg", ZappConts.AUTHENTICATION.JWT_ALGORITHM.auth);
        
        /* [Accessor info.]
         * 
         */
        ZappAuth rZappAuth = zappAuthenticationService.getAccessorInfo(pZappAuth);

        /* [세션 정보]
         * ZappAuth - company, gropu users, users
         */
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(ZappConts.AUTHENTICATION.ACCESSOR_INFO.auth, rZappAuth);
        map.put(ZappConts.AUTHENTICATION.ACCESSOR_POINT.auth, pRequest.getRemoteAddr());

        /** []
         * 
         */
        JwtBuilder builder = Jwts.builder().setHeader(headerMap)
                .setClaims(map)
                .setExpiration(expireTime)
                .signWith(signatureAlgorithm, signingKey);

        return builder.compact();

	}
	
	/**
	 * 
	 * @param pZappAuth
	 * @param pRequest
	 * @return
	 */
	public String createJWT_Obj(ZappAuth pZappAuth, HttpServletRequest pRequest) {
		
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + 1000 * 60 * 10);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        /* [JWT basic info.]       
         * Type, Algorithm 
         */
        Map<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("typ", ZappConts.AUTHENTICATION.JWT_TYPE.auth);
        headerMap.put("alg", ZappConts.AUTHENTICATION.JWT_ALGORITHM.auth);
        
        /* [Store info.]
         * Map
         */
        Map<String, String> sessEnvm = new HashMap<String, String>();
		for (Map.Entry<String, ZappEnv> entry : pZappAuth.getSessEnv().entrySet()) {
			sessEnvm.put(entry.getKey(), ZappJSONUtils.cvrtObjToJson((ZappEnv) entry.getValue()));
		}
        Map<String, String> sessEnvm_User = new HashMap<String, String>();
		for (Map.Entry<String, ZappEnv> entry : pZappAuth.getSessUserEnv().entrySet()) {
			sessEnvm_User.put(entry.getKey(), ZappJSONUtils.cvrtObjToJson((ZappEnv) entry.getValue()));
		}		
        
        /** []
         * 
         */
        
        String jwt = Jwts.builder()
                    //header
                    .setHeaderParam("typ", ZappConts.AUTHENTICATION.JWT_TYPE.auth) 
                    .setHeaderParam("alg", ZappConts.AUTHENTICATION.JWT_ALGORITHM.auth) 
                    .setSubject("ZenithECM4.0") 
                    
                    // payload
                    .claim(JWTConts.COMPANY_ID.id, pZappAuth.getObjCompanyid()) 	
                    .claim(JWTConts.DEPT_ID.id, pZappAuth.getObjDeptid()) 
                    .claim(JWTConts.COMPANY_OBJ.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessCompany())) 
                    .claim(JWTConts.DEPT_OBJ.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessDept())) 
                    .claim(JWTConts.USER_OBJ.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessUser())) 
                    .claim(JWTConts.DEPTUSER_OBJ.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessDeptUser())) 
                    .claim(JWTConts.ONLY_DEPTUSER_OBJ.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessOnlyDeptUser())) 
                    .claim(JWTConts.DEPTUSER_OBJS.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessDeptUsers())) 
                    .claim(JWTConts.ONLY_DEPTUSER_OBJS.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessOnlyDeptUsers())) 
                    .claim(JWTConts.GROUPUSER_OBJS.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessGroupUsers())) 
                    .claim(JWTConts.ONLY_GROUPUSER_OBJS.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessOnlyGroupUsers())) 
                    .claim(JWTConts.LOWER_ALL_DEPTS.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessAllLowerDepts()))
                    .claim(JWTConts.ACL_OBJS.id, ZappJSONUtils.cvrtObjToJson(pZappAuth.getSessAclObjList()))
                    .claim(JWTConts.COMPANY_ENV.id, ZappJSONUtils.cvrtObjToJson(sessEnvm))
                    .claim(JWTConts.USER_ENV.id, ZappJSONUtils.cvrtObjToJson(sessEnvm_User))
                    .claim(JWTConts.VALID_LIC.id, pZappAuth.getObjValidLic())
                    .claim(JWTConts.LANG.id, pZappAuth.getObjlang())
                    
                    .setIssuedAt(new Date(System.currentTimeMillis())) // token 생성날짜
                    .setExpiration(expireTime) // token 유효시간
                    
                    //signature
                    .signWith(signatureAlgorithm, signingKey)
                    .compact();

        return jwt;

	}

	public String createJWT_Simple(ZappAuth pZappAuth, HttpServletRequest pRequest) {
		
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date expireTime = new Date();
        expireTime.setTime(expireTime.getTime() + 1000 * 60 * 10);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        /* [JWT basic info.]       
         * Type, Algorithm 
         */
        Map<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("typ", ZappConts.AUTHENTICATION.JWT_TYPE.auth);
        headerMap.put("alg", ZappConts.AUTHENTICATION.JWT_ALGORITHM.auth);
        
        /** []
         * 
         */
        
        String jwt = Jwts.builder()
                    //header
                    .setHeaderParam("typ", ZappConts.AUTHENTICATION.JWT_TYPE.auth) 
                    .setHeaderParam("alg", ZappConts.AUTHENTICATION.JWT_ALGORITHM.auth) 
                    .setSubject("ZenithECM4.0") 
                    
                    // payload
                    .claim(JWTConts.COMPANY_OBJ.id, pZappAuth.getObjCompanyid()) 
                    .claim(JWTConts.DEPTUSER_OBJ.id, pZappAuth.getSessDeptUser().getDeptuserid()) 
                    .claim(JWTConts.VALID_LIC.id, pZappAuth.getObjValidLic())
                    
                    .setIssuedAt(new Date(System.currentTimeMillis())) // token 생성날짜
                    .setExpiration(expireTime) // token 유효시간
                    
                    //signature
                    .signWith(signatureAlgorithm, signingKey)
                    .compact();

        return jwt;

	}
	
	/**
	 * 
	 * @author Daniel
	 *
	 */
	private enum JWTConts {
		
		COMPANY_ID("objCompanyid", "Company ID"),
		DEPT_ID("objDeptid", "Dept. ID"),
		
		COMPANY_OBJ("sessCompany", "Company Object"),
		DEPT_OBJ("sessDept", "Dept. Object"),
		USER_OBJ("sessUser" ,"User Object"),
		DEPTUSER_OBJ("sessDeptUser", "Dept. User Object"),
		ONLY_DEPTUSER_OBJ("sessOnlyDeptUser", "Only Dept. User Object"),
		DEPTUSER_OBJS("sessDeptUsers", "Dept. User Objects"),
		ONLY_DEPTUSER_OBJS("sessOnlyDeptUsers", "Only Dept. User Objects"),
		GROUPUSER_OBJS("sessGroupUsers", "Group User Objects"),
		ONLY_GROUPUSER_OBJS("sessOnlyGroupUsers", "Only Group User Objects"),
		
		LOWER_ALL_DEPTS("sessAllLowerDepts", "lower all depts."),
		ACL_OBJS("sessAclObjList", "ACL Objects"),
		
		COMPANY_ENV("sessEnv", "Company Env."),
		USER_ENV("sessUserEnv", "User Env."),
		
		VALID_LIC("objValidLic", ""),
		
		LANG("objLang", "")
		;

		public final String id;
		public final String note;

		JWTConts(String id, String note) {
			this.id = id;
			this.note = note;
		}
	}

}
