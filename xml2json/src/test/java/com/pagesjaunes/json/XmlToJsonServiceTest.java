/**
 *
 */
package com.pagesjaunes.json;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.pagesjaunes.json.service.XmlToJsonService;


/**
 * @author Ryan Kenney
 */
public class XmlToJsonServiceTest {

	private static final String variousStructuresXml =
			"<root rootAttribute='rootAttributeValue'>\n"+
					"<singleRootEmpty/>\n"+
					"<singleRootWithAttribute key='singleRootWithAttributeValue'/>\n"+
					"<singleRootWithAttribute_WithClosingTag key='singleRootWithAttributeValue'></singleRootWithAttribute_WithClosingTag>\n"+
					"<singleRootWithContent>singleRootWithContentValue</singleRootWithContent>\n"+
					"<singleRootWithElement><childElement>singleRootWithElementValue</childElement></singleRootWithElement>\n"+
					"<collisionRootEmpty/>\n"+
					"<collisionRootEmpty/>\n"+
					"<collisionRootWithAttribute key='collisionRootWithAttributeValue1'/>\n"+
					"<collisionRootWithAttribute key='collisionRootWithAttributeValue2'/>\n"+
					"<collisionRootWithContent>collisionRootWithContentValue1</collisionRootWithContent>\n"+
					"<collisionRootWithContent>collisionRootWithContentValue2</collisionRootWithContent>\n"+
					"<collisionRootWithElement><childElement>collisionRootWithElementValue1</childElement></collisionRootWithElement>\n"+
					"<collisionRootWithElement><childElement>collisionRootWithElementValue2</childElement></collisionRootWithElement>\n"+
					"<singleRootWithChildCollision>"+
						"<childElement>singleRootWithChildCollisionValue1</childElement>\n"+
						"<childElement>singleRootWithChildCollisionValue2</childElement>\n"+
					"</singleRootWithChildCollision>\n"+
					"<singleRootWithAttibuteElementCollision collisionKey='attributeValue'><collisionKey>elementValue</collisionKey></singleRootWithAttibuteElementCollision>\n"+
					"<singleRootWithOneContentAndElements>\n"+
						"<childElement>element1</childElement>\n"+
						"<childElement>element2</childElement>\n"+
						"contentBlock\n"+
					"</singleRootWithOneContentAndElements>\n"+
					"<singleRootWithManyContentAndElements>\n"+
						"contentBlock1\n"+
						"<childElement>element1</childElement>\n"+
						"contentBlock2\n"+
						"<childElement>element2</childElement>\n"+
						"contentBlock3\n"+
					"</singleRootWithManyContentAndElements>\n"+
					"<singleRootWithContentAtMultipleLevels>\n"+
						"contentBlock1\n"+
						"<childElement>\n"+
							"nestedContentBlock1\n"+
							"<nestedChildElement>nestedChildElementValue</nestedChildElement>\n"+
							"nestedContentBlock2\n"+
						"</childElement>\n"+
						"contentBlock2\n"+
						"<childElement>\n"+
							"nestedContentBlock1\n"+
							"<nestedChildElement>nestedChildElementValue</nestedChildElement>\n"+
							"nestedContentBlock2\n"+
						"</childElement>\n"+
					"</singleRootWithContentAtMultipleLevels>\n"+
				"</root>";

	@Test
	public void testToJSONObject_DefaultConfig() throws Exception {
		XmlToJsonService xmlToJson = new XmlToJsonService();
		JSONObject json = xmlToJson.toJSONObject(variousStructuresXml);
		Object jsonIndex = Configuration.defaultConfiguration().
				jsonProvider().parse(json.toString());

		// Verify attribute rendered with "@" prefix
		assertEquals("rootAttributeValue", JsonPath.read(jsonIndex,
				"$.@rootAttribute"));
		// Verify empty elements not included in output
		try {
			JsonPath.read(jsonIndex, "$.singleRootEmpty");
			Assert.fail("Expected exception");
		} catch (PathNotFoundException e) {
			// Success
		}
		// Verify attribute rendered with "@" prefix
		assertEquals("singleRootWithAttributeValue", JsonPath.read(jsonIndex,
				"$.singleRootWithAttribute.@key"));
		// Verify both self-closed and closing-tag forms of an element are treated the same
		assertEquals("singleRootWithAttributeValue", JsonPath.read(jsonIndex,
				"$.singleRootWithAttribute_WithClosingTag.@key"));
		// Verify content of sole element is rendered as a simple string field (versus nested as a "context" field)
		assertEquals("singleRootWithContentValue", JsonPath.read(jsonIndex,
				"$.singleRootWithContent"));
		// Verify nested element is not wrapped with an array
		assertEquals("singleRootWithElementValue", JsonPath.read(jsonIndex,
				"$.singleRootWithElement.childElement"));
		// Verify empty elements not included in output, even when encountering multiple
		try {
			JsonPath.read(jsonIndex, "$.collisionRootEmpty");
			Assert.fail("Expected exception");
		} catch (PathNotFoundException e) {
			// Success
		}
		// Verify colliding elements, with same attribute are isolated in arrays at the element level
		assertEquals("collisionRootWithAttributeValue1", JsonPath.read(jsonIndex,
				"$.collisionRootWithAttribute[0].@key"));
		assertEquals("collisionRootWithAttributeValue2", JsonPath.read(jsonIndex,
				"$.collisionRootWithAttribute[1].@key"));
		// Verify colliding elements with content are wrapped in an array
		assertEquals("collisionRootWithContentValue1", JsonPath.read(jsonIndex,
				"$.collisionRootWithContent[0]"));
		assertEquals("collisionRootWithContentValue2", JsonPath.read(jsonIndex,
				"$.collisionRootWithContent[1]"));
		// Verify colliding elements with identical child elements wrapped in an array at the collision level
		assertEquals("collisionRootWithElementValue1", JsonPath.read(jsonIndex,
				"$.collisionRootWithElement[0].childElement"));
		assertEquals("collisionRootWithElementValue2", JsonPath.read(jsonIndex,
				"$.collisionRootWithElement[1].childElement"));
		// Verify colliding child elements are wrapped in an array at the collision level
		assertEquals("singleRootWithChildCollisionValue1", JsonPath.read(jsonIndex,
				"$.singleRootWithChildCollision.childElement[0]"));
		assertEquals("singleRootWithChildCollisionValue2", JsonPath.read(jsonIndex,
				"$.singleRootWithChildCollision.childElement[1]"));
		// Verify that colliding element and attribute values are islated by the "@" prefix on the resulting attribute field 
		assertEquals("attributeValue", JsonPath.read(jsonIndex,
				"$.singleRootWithAttibuteElementCollision.@collisionKey"));
		assertEquals("elementValue", JsonPath.read(jsonIndex,
				"$.singleRootWithAttibuteElementCollision.collisionKey"));
		// Verify that mixed content (elements and text content) is isolated by element names and a "$content" holder 
		// Verify that the "$content" holder is a simple string 
		assertEquals("element1", JsonPath.read(jsonIndex,
				"$.singleRootWithOneContentAndElements.childElement[0]"));
		assertEquals("element2", JsonPath.read(jsonIndex,
				"$.singleRootWithOneContentAndElements.childElement[1]"));
		assertEquals("contentBlock", JsonPath.read(jsonIndex,
				"$.singleRootWithOneContentAndElements.$content"));
		// Verify that mixed content (elements and text content) is isolated by element names and a "$content" holder 
		// Verify that the "$content" holder is an array when multiple content sections 
		assertEquals("contentBlock1", JsonPath.read(jsonIndex,
				"$.singleRootWithManyContentAndElements.$content[0]"));
		assertEquals("element1", JsonPath.read(jsonIndex,
				"$.singleRootWithManyContentAndElements.childElement[0]"));
		assertEquals("contentBlock2", JsonPath.read(jsonIndex,
				"$.singleRootWithManyContentAndElements.$content[1]"));
		assertEquals("element2", JsonPath.read(jsonIndex,
				"$.singleRootWithManyContentAndElements.childElement[1]"));
		assertEquals("contentBlock3", JsonPath.read(jsonIndex,
				"$.singleRootWithManyContentAndElements.$content[2]"));
	}

	/**
	 * Re-runs the same tests as {@link #testToJSONObject_DoExpandArrays()} with
	 * {@link XmlToJsonService@#doExpandArrays(boolean)} enabled and then
	 * verifies that all elements are expand to arrays.
	 */
	@Test
	public void testToJSONObject_DoExpandArrays() throws Exception {
		XmlToJsonService xmlToJson = new XmlToJsonService();
		xmlToJson.doExpandArrays(true);
		JSONObject json = xmlToJson.toJSONObject(variousStructuresXml);
		Object jsonIndex = Configuration.defaultConfiguration().
				jsonProvider().parse(json.toString());

		// Verify attribute rendered with "@" prefix
		assertEquals("rootAttributeValue", JsonPath.read(jsonIndex,
				"$.@rootAttribute"));
		// Verify empty elements not included in output
		try {
			JsonPath.read(jsonIndex, "$.singleRootEmpty");
			Assert.fail("Expected exception");
		} catch (PathNotFoundException e) {
			// Success
		}

		assertEquals("singleRootWithAttributeValue", JsonPath.read(jsonIndex,
				"$.singleRootWithAttribute[0].@key"));
		assertEquals("singleRootWithAttributeValue", JsonPath.read(jsonIndex,
				"$.singleRootWithAttribute_WithClosingTag[0].@key"));
		assertEquals("singleRootWithContentValue", JsonPath.read(jsonIndex,
				"$.singleRootWithContent[0].$content[0]"));
		assertEquals("singleRootWithElementValue", JsonPath.read(jsonIndex,
				"$.singleRootWithElement[0].childElement[0].$content[0]"));
		try {
			JsonPath.read(jsonIndex, "$.collisionRootEmpty");
			Assert.fail("Expected exception");
		} catch (PathNotFoundException e) {
			// Success
		}
		assertEquals("collisionRootWithAttributeValue1", JsonPath.read(jsonIndex,
				"$.collisionRootWithAttribute[0].@key"));
		assertEquals("collisionRootWithAttributeValue2", JsonPath.read(jsonIndex,
				"$.collisionRootWithAttribute[1].@key"));
		assertEquals("collisionRootWithContentValue1", JsonPath.read(jsonIndex,
				"$.collisionRootWithContent[0].$content[0]"));
		assertEquals("collisionRootWithContentValue2", JsonPath.read(jsonIndex,
				"$.collisionRootWithContent[1].$content[0]"));
		assertEquals("collisionRootWithElementValue1", JsonPath.read(jsonIndex,
				"$.collisionRootWithElement[0].childElement[0].$content[0]"));
		assertEquals("collisionRootWithElementValue2", JsonPath.read(jsonIndex,
				"$.collisionRootWithElement[1].childElement[0].$content[0]"));
		assertEquals("singleRootWithChildCollisionValue1", JsonPath.read(jsonIndex,
				"$.singleRootWithChildCollision[0].childElement[0].$content[0]"));
		assertEquals("singleRootWithChildCollisionValue2", JsonPath.read(jsonIndex,
				"$.singleRootWithChildCollision[0].childElement[1].$content[0]"));
		assertEquals("attributeValue", JsonPath.read(jsonIndex,
				"$.singleRootWithAttibuteElementCollision[0].@collisionKey"));
		assertEquals("elementValue", JsonPath.read(jsonIndex,
				"$.singleRootWithAttibuteElementCollision[0].collisionKey[0].$content[0]"));
		assertEquals("element1", JsonPath.read(jsonIndex,
				"$.singleRootWithOneContentAndElements[0].childElement[0].$content[0]"));
		assertEquals("element2", JsonPath.read(jsonIndex,
				"$.singleRootWithOneContentAndElements[0].childElement[1].$content[0]"));
		assertEquals("contentBlock", JsonPath.read(jsonIndex,
				"$.singleRootWithOneContentAndElements[0].$content[0]"));
		assertEquals("contentBlock1", JsonPath.read(jsonIndex,
				"$.singleRootWithManyContentAndElements[0].$content[0]"));
		assertEquals("element1", JsonPath.read(jsonIndex,
				"$.singleRootWithManyContentAndElements[0].childElement[0].$content[0]"));
		assertEquals("contentBlock2", JsonPath.read(jsonIndex,
				"$.singleRootWithManyContentAndElements[0].$content[1]"));
		assertEquals("element2", JsonPath.read(jsonIndex,
				"$.singleRootWithManyContentAndElements[0].childElement[1].$content[0]"));
		assertEquals("contentBlock3", JsonPath.read(jsonIndex,
				"$.singleRootWithManyContentAndElements[0].$content[2]"));
	}

	/**
	 * Verifies that XML namespace entries are preserved.
	 */
	@Test
	public void testToJSONObject_Xmlns() throws Exception {
		String xml =
				"<root "
				+ "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
				+ "xsi:noNamespaceSchemaLocation='http://foo/bar.xsd'"
				+ ">xxx</root>";
		
		XmlToJsonService xmlToJson = new XmlToJsonService();
		JSONObject json = xmlToJson.toJSONObject(xml);
		Object jsonIndex = Configuration.defaultConfiguration().
				jsonProvider().parse(json.toString());

		assertEquals("http://www.w3.org/2001/XMLSchema-instance", JsonPath.read(jsonIndex,
				"$.@xmlns:xsi"));
		assertEquals("http://foo/bar.xsd", JsonPath.read(jsonIndex,
				"$.@xsi:noNamespaceSchemaLocation"));
	}
}
