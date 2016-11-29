package com.rjmahler.json;

import java.util.Iterator;
import java.util.Map.Entry;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * @author rjmahler
 */
public class JsonUtils {
	private JsonUtils() {
	}
	
	/**
	 * Gets the full path for a specific value from a JSON tree.
	 * This only works for String values.
	 * 
	 * @param rootNode - JSON tree
	 * @param path - current JSON path
	 * @param value - requested search value
	 * @return the string containing the full JSON path or null if no such value exists
	 */
	public static String getFullPath(JsonNode rootNode, String path, String value) {
		if(rootNode == null || value == null) {
			return null;
		}

		if(path == null) {
			path = "";
		}

		JsonNodeType type = rootNode.getNodeType();
		if(JsonNodeType.OBJECT.equals(type)) {
			ObjectNode objectNode = (ObjectNode) rootNode;
			Iterator<Entry<String, JsonNode>> iterator = objectNode.fields();
			while(iterator.hasNext()) {
				Entry<String,JsonNode> map = iterator.next();
				String result = getFullPath(map.getValue(), path + "\\" + map.getKey(), value);
				if(result != null) {
					return result;
				}
			}
		}
		else if(JsonNodeType.ARRAY.equals(type)) {
			ArrayNode arrayNode = (ArrayNode) rootNode;
			for(int i = 0; i < arrayNode.size(); i++) {
				String result = getFullPath(arrayNode.get(i), path + "[" + i + "]", value);
				if(result != null) {
					return result;
				}
			}
		}
		else if(JsonNodeType.STRING.equals(type)) {
			TextNode st = (TextNode) rootNode;
			if(st.asText().equals(value)) {
				return path;
			}
		}

		return null;
	}
	
	/**
	 * Replaces JSON data for the specified element. This only replaces the first occurrence.
	 * 
	 * @param rootNode - JSON tree
	 * @param element - element to replace
	 * @param value - data replacing the element
	 * @return true, if the operation is successful
	 */
	public static boolean findAndReplace(JsonNode rootNode, String element, JsonNode value) {
		if(rootNode == null || element == null) {
			return false;
		}
		JsonNodeType type = rootNode.getNodeType();
		if(JsonNodeType.OBJECT.equals(type)) {
			ObjectNode objectNode = (ObjectNode) rootNode;
			if(objectNode.has(element)) {
				objectNode.remove(element);
				objectNode.setAll((ObjectNode)value);
				return true;
			}
			else {
				Iterator<Entry<String, JsonNode>> iterator = objectNode.fields();
				while(iterator.hasNext()) {
					Entry<String,JsonNode> map = iterator.next();
					if(findAndReplace(map.getValue(), element, value)) {
						return true;
					}
				}
			}
		}
		else if(JsonNodeType.ARRAY.equals(type)) {
			ArrayNode arrayNode = (ArrayNode) rootNode;
			for(int i = 0; i < arrayNode.size(); i++) {
				if(findAndReplace(arrayNode.get(i), element, value)) {
					return true;
				}
			}
		}
		
		return false;
	}
}
