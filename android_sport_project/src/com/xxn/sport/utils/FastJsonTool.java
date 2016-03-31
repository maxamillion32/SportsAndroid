package com.xxn.sport.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class FastJsonTool {

	public FastJsonTool() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param object
	 * @return
	 */
	public static String createJsonString(Object object) {
		String jsonString = JSON.toJSONString(object);
		return jsonString;
	}
	
	 /**把数据源HashMap转换成json字符串
     * @param map  
     */  
    public static String hashMapToJsonString(HashMap map) {
        String string = "{";  
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {  
            Entry e = (Entry) it.next();  
            string += "'" + e.getKey() + "':";  
            string += "'" + e.getValue() + "',";  
        }  
        string = string.substring(0, string.lastIndexOf(","));  
        string += "}";  
        return string;  
    }
    /**把数据源HashMap转换成json格式
     * @param map  
     */  
    public static JSONObject hashMapToJson(HashMap map) {
        String string = "{";  
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {  
            Entry e = (Entry) it.next();  
            string += "'" + e.getKey() + "':";  
            string += "'" + e.getValue() + "',";  
        }  
        string = string.substring(0, string.lastIndexOf(","));  
        string += "}";
        JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(string);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonObject;
    }  

	/**
	 * ��ɶԵ���javaBean�Ľ���
	 * 
	 * @param jsonStringҪ
	 *            ������ַ�?
	 * @param cls
	 * @return
	 */
	public static <T> T getObject(String jsonString, Class<T> cls) {
		T t = null;
		try {
			t = JSON.parseObject(jsonString, cls);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return t;
	}

	/**
	 * ��ɶ�List�б�Ľ���?
	 * 
	 * @param jsonString
	 * @param cls
	 * @return
	 */
	public static <T> List<T> getObjectList(String jsonString, Class<T> cls) {
		List<T> list = new ArrayList<T>();
		try {
			list = JSON.parseArray(jsonString, cls);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return list;
	}

	/**
	 * ��ɶ�List�б���Map��ݵĽ���?
	 * 
	 * @param jsonString
	 * @return
	 */
	public static List<Map<String, Object>> getObjectMap(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			list = JSON.parseObject(jsonString,
					new TypeReference<List<Map<String, Object>>>() {
					});
		} catch (Exception e) {
			// TODO: handle exception
		}
		return list;

	}

}
