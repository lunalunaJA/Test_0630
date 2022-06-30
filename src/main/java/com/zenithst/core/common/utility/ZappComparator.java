/**
 *
 */
package com.zenithst.core.common.utility;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

 /**
 * Comparator.java
 * @Description <pre>
 *              </pre>
 * @author ZenithST
 * @since 2013. 8. 26.
 * @version 1.0
 * @see
 *
 * @ Date             Updator     Note
 * @ -------------   ---------   -------------------------------
 * @ 2013. 8. 26.  	  Daniel      New
 *
 * @ Copyright (C) by ZENITHST All right reserved.
 */


public class ZappComparator {

    @SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj){
        if( obj instanceof String ) {
        	return obj==null || "".equals(obj.toString().trim());
        }
        else if( obj instanceof List ) return obj==null || ((List)obj).isEmpty();
        else if( obj instanceof Map ) return obj==null || ((Map)obj).isEmpty();
        else if( obj instanceof Object[] ) return obj==null || Array.getLength(obj)==0;
        else return obj==null;
    }

    public static boolean isNotEmpty(String s){
        return !isEmpty(s);
    }
}
