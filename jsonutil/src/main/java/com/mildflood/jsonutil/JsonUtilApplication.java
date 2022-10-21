package com.mildflood.jsonutil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class JsonUtilApplication {
	
	public static final String STRING_KEY = "String";
	public static final String JSONOBJECT_KEY = "JSONObject";
	public static final String JSONARRAY_KEY = "JSONArray";

	/**
	  * Modify json's key to uppercase or lowercase
	 * 
	 * @Description
	  * @Date 2019-3-7 3:07:32 PM
	 * @param jsonObject
	  * @param changeMode When the value is true, the description is to lowercase, false to uppercase
	 * @return
	 * @throws
	 */
	public static JSONObject transferJsonKey(JSONObject jsonObject, boolean transferMode) {
		JSONObject object = new JSONObject();
		Iterator iterator = jsonObject.keys();
		while (iterator.hasNext()) {
			String jsonKey = (String) iterator.next();
			Object valueObject = jsonObject.get(jsonKey);
			if (transferMode) {
				jsonKey = jsonKey.toLowerCase();
			} else {
				jsonKey = jsonKey.toUpperCase();
			}
			if (valueObject.getClass().toString().endsWith(STRING_KEY)) {
				object.accumulate(jsonKey, valueObject);
			} else if (valueObject.getClass().toString().endsWith(JSONOBJECT_KEY)) {
				JSONObject checkObject = JSONObject.fromObject(valueObject);
				// When the value is null, the valueObject is still a JSONObject object, the null is not true, to determine whether it is nullObject
				if (!checkObject.isNullObject()) {
					object.accumulate(jsonKey, transferJsonKey((JSONObject) valueObject, transferMode));
				} else {
					object.accumulate(jsonKey, null);
				}
			} else if (valueObject.getClass().toString().endsWith(JSONARRAY_KEY)) {
				object.accumulate(jsonKey, transferJsonArray(jsonObject.getJSONArray(jsonKey), transferMode));
			}
		}
		return object;
	}

	/**
	  * JSONArray key case conversion
	 * 
	 * @Description
	  * @Date 2019-3-7 3:28:30 PM
	 * @param jsonArray
	 * @param transferMode
	 * @return
	 * @throws
	 */
	public static JSONArray transferJsonArray(JSONArray jsonArray, boolean transferMode) {
		JSONArray array = new JSONArray();
		if (null != jsonArray && jsonArray.size() > 0) {
			for (Object object : jsonArray) {
				if (object.getClass().toString().endsWith(JSONOBJECT_KEY)) {
					array.add(transferJsonKey((JSONObject) object, transferMode));
				} else if (object.getClass().toString().endsWith(JSONARRAY_KEY)) {
					array.add(transferJsonArray((JSONArray) object, transferMode));
				}
			}
		}
		return array;
	}
	
	public static String readFile(String path, Charset encoding)
			  throws IOException
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
	}

	public static void main(String[] args) {
		String str = "";
		try {
			str = readFile("C:\\Users\\zhaoq\\Documents\\Dev\\Maxds2\\maxds_term_rule_all.json", StandardCharsets.UTF_8);
			System.out.println("File read: done");
		}catch (IOException e) {
			System.out.println("File reading error: " + e.getStackTrace());
		}
		str = str.replaceAll("},", "}#");
		
		List<String> items = Arrays.asList(str.split("\\s*#\\s*"));
		//System.out.println(str);
		String content = "";
		for (String item : items) {
			System.out.println("Processing: " + item);
			JSONObject object = JSONObject.fromObject(item);
			object = transferJsonKey(object, true);
			content = content + object.toString() + "," + "\n";
		}

		content = content.substring(0, content.lastIndexOf(','));
		String path = "C:\\Users\\zhaoq\\Documents\\Dev\\Maxds2\\maxds_term_rule_all_updated.json";
		try {
			Files.write( Paths.get(path), content.getBytes());
			System.out.println("File write: done");
		} catch (IOException e) {
			System.out.println("File writing error: " + e.getStackTrace());
		}
		
	}

}
