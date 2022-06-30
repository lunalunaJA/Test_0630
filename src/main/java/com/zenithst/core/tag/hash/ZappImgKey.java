package com.zenithst.core.tag.hash;

import java.util.List;
import java.util.UUID;

import com.zenithst.core.common.conts.ZappConts;
import com.zenithst.core.tag.vo.ZappImg;
import com.zenithst.core.tag.vo.ZappTag;
import com.zenithst.core.tag.vo.ZappTaskTag;
import com.zenithst.core.tag.vo.ZappTaskTagExtend;
import com.zenithst.framework.util.ZstFwDateUtils;
import com.zenithst.framework.util.ZstFwEncodeUtils;
import com.zenithst.framework.util.ZstFwValidatorUtils;

public class ZappImgKey {
	
	public static String getPk(Object pObj) {
		
		StringBuffer key = new StringBuffer();
		
		/* */
		if(pObj instanceof ZappImg) {
			ZappImg vo = (ZappImg) pObj;
			String uuid = UUID.randomUUID().toString().replace("-",  "");
			key.append(ZstFwDateUtils.getNow());		// 1. 시간
			key.append(uuid);							// 2. UUID
		}
		
		/* 태그 */
		if(pObj instanceof ZappTag) {
			ZappTag vo = (ZappTag) pObj;
			key.append(vo.getCode());			// 1. Code
		}
		
		/* 업무태그 */
		if(pObj instanceof ZappTaskTag) {
			ZappTaskTag vo = (ZappTaskTag) pObj;
			key.append(vo.getTaskid());			// 1. Task ID
			key.append(vo.getTagid());			// 2. Tag ID
			key.append(vo.getSeqno());			// 3. Serial number
		}		
		
		return ZstFwEncodeUtils.encodeString_SHA256(key.toString());
	}
	
	/**
	 * 태그 정보를 이용한 pk 자동 생성
	 * @param pObj
	 * @param pObjTag null 첫번째 객체만으로 pk 구성
	 * @return
	 */
	public static String getPk(Object pObj, Object pObjTag) {
		
		StringBuffer key = new StringBuffer();
		
		if(pObjTag == null) {
			
			/* [ImgDoc]
			 * 태그 정보가 없는 경우에 생성일시로 pk 를 생성한다.
			 */
			return getPk(pObj);
			
		}
		else {
			
			/* [태그 정보를 이용한 pk 생성]
			 * 
			 */
			if(pObj != null) {
				if(pObj instanceof ZappImg && pObjTag instanceof List) {
	
					ZappImg imgvo = (ZappImg) pObj;
	
					@SuppressWarnings("unchecked")
					List<ZappTaskTagExtend> tagvo = (List<ZappTaskTagExtend>) pObjTag;
					for(ZappTaskTagExtend vo : tagvo) {
						if(vo.getIncludepk().equals(ZappConts.USAGES.YES.use)) { 
							int _IDX_SEQ = ZstFwValidatorUtils.fixNullInt(vo.getSeqno(), ZappConts.NUMS.ZERO.num);
							switch(_IDX_SEQ) {
								case 1: key.append(imgvo.getIdx01()); break;
								case 2: key.append(imgvo.getIdx02()); break;
								case 3: key.append(imgvo.getIdx03()); break;
								case 4: key.append(imgvo.getIdx04()); break;
								case 5: key.append(imgvo.getIdx05()); break;
								case 6: key.append(imgvo.getIdx06()); break;
								case 7: key.append(imgvo.getIdx07()); break;
								case 8: key.append(imgvo.getIdx08()); break;
								case 9: key.append(imgvo.getIdx09()); break;
								case 10: key.append(imgvo.getIdx10()); break;
								case 11: key.append(imgvo.getIdx11()); break;
								case 12: key.append(imgvo.getIdx12()); break;
								case 13: key.append(imgvo.getIdx13()); break;
								case 14: key.append(imgvo.getIdx14()); break;
								case 15: key.append(imgvo.getIdx15()); break;
								case 16: key.append(imgvo.getIdx16()); break;
								case 17: key.append(imgvo.getIdx17()); break;
								case 18: key.append(imgvo.getIdx18()); break;
								case 19: key.append(imgvo.getIdx19()); break;
								case 20: key.append(imgvo.getIdx20()); break;
								default:
							}
						}
					}
				}
			}
			
		}
		
		return ZstFwEncodeUtils.encodeString_SHA256(key.toString());
		
	}	
	
}
