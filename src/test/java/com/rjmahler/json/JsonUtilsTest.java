package com.rjmahler.json;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.TestCase;

/**
 * Unit test for JsonUtils
 */
public class JsonUtilsTest extends TestCase
{
	ObjectMapper objMapper;
	
	@Override
	public void setUp() {
		objMapper = new ObjectMapper();
	}
	
	public void testFullPath() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("jsontest1.txt"));
			JsonNode rootNode = objMapper.readValue(br,  JsonNode.class);
			assertEquals(JsonUtils.getFullPath(rootNode, "", "item2"), "\\itemList\\items[1]\\id");
			assertEquals(JsonUtils.getFullPath(rootNode, "", "SubItem 3"),"\\itemList\\items[5]\\subItems[2]\\label");
			assertEquals(JsonUtils.getFullPath(rootNode, "", "SubItem 2 item1"),"\\itemList\\items[8]\\subItems\\label");
			assertNull(JsonUtils.getFullPath(rootNode, "", "SubItem 3 item1"));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
    	public void testFindAndReplace() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("jsontest2.txt"));
			JsonNode rootNode = objMapper.readValue(br,  JsonNode.class);
			br.close();
			System.out.println("=========================================================");
			System.out.println("Original:" + rootNode);
			br = new BufferedReader(new FileReader("personalinfo-phone.txt"));
			JsonNode phoneNode = objMapper.readValue(br, JsonNode.class);
			br.close();
			assertTrue(JsonUtils.findAndReplace(rootNode, "serverInsert", phoneNode));
			assertNotNull(JsonUtils.getFullPath(rootNode, "", "subItem1Itemphone"));
			System.out.println("=========================================================");
			System.out.println("After phone insert:" + rootNode);
			br = new BufferedReader(new FileReader("personalinfo-ssn.txt"));
			JsonNode ssnNode = objMapper.readValue(br, JsonNode.class);
			br.close();
			assertTrue(JsonUtils.findAndReplace(rootNode, "serverInsert", ssnNode));
			assertNotNull(JsonUtils.getFullPath(rootNode, "", "subItem1Itemssn"));
			assertNull(JsonUtils.getFullPath(rootNode, "", "serverInsert"));
			System.out.println("=========================================================");
			System.out.println("After ssn insert:" + rootNode);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
}
