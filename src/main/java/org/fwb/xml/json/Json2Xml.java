package org.fwb.xml.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class Json2Xml {
	/** xml root element name */
	public static final String TAG_ROOT = "json";
	
	public static String json2xml(String json) {
		return json2xml(json, TAG_ROOT);
	}
	public static String json2xml(String json, String rootTag) {
		return XML.toString(parseJson(json), rootTag);
	}
	public static String json2xmlNoRoot(String json) {
		return XML.toString(parseJson(json));
	}
	
	/**
	 * i'm amazed json.org doesn't have this in their api :(
	 * @return	a JSONObject, JSONArray, or the original String (including null)
	 */
	public static Object parseJson(String json) {
		if (json == null)
			return null;
		
		String st = json.trim();
		if (st.startsWith("{"))
			return new JSONObject(json);
		if (st.startsWith("["))
			return new JSONArray(json);
		
		return json;
	}
}
