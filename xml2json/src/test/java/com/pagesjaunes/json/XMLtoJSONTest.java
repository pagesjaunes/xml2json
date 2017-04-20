/**
 *
 */
package com.pagesjaunes.json;

import org.junit.Assert;
import org.junit.Test;

import com.pagesjaunes.json.config.ConfigurationMapEnumTypes;
import com.pagesjaunes.json.service.XmlToJsonService;


/**
 * @author pagesjaunes
 *
 */
public class XMLtoJSONTest {
    private ConfigurationMapEnumTypes conf;
    private XmlToJsonService stXmlToJson;

    public XMLtoJSONTest() {
        this.conf = new ConfigurationMapEnumTypes("xml2json-datatype-mapping.properties");
        this.stXmlToJson = new XmlToJsonService(conf.getProperties());
    }
    
    @Test
    public void convertXMLtoJSONAucuneGestion() throws JSONException {
        String xml = "<bloc>"
                + "<blocid>00413695C0001</blocid>"
                + "<test_liste>test liste 1</test_liste>"
                + "<test_liste>test liste 2</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0001\","
                + "\"test_liste\":[\"test liste 1\",\"test liste 2\"]}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0002</blocid>"
                + "<test_liste>test liste 1</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0002\","
                + "\"test_liste\":\"test liste 1\"}",
                stXmlToJson.toJSONObject(xml).toString());
    }

    @Test
    public void convertXMLtoJSONInstructionXmlInclude() throws JSONException {
        String xml = "<bloc>"
                + "<blocid>00413695C0001</blocid>"
                + "<?xml-include test_liste?>"
                + "<test_liste>test liste 1</test_liste>"
                + "<test_liste>test liste 2</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0001\","
                + "\"test_liste\":[\"test liste 1\",\"test liste 2\"]}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0002</blocid>"
                + "<?xml-include test_liste?>"
                + "<test_liste>test liste 1</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0002\","
                + "\"test_liste\":\"test liste 1\"}",
                stXmlToJson.toJSONObject(xml).toString());
    }

    @Test
    public void convertXMLtoJSONAttributeClass() throws JSONException {
        String xml = "<bloc>"
                + "<blocid>00413695C0001</blocid>"
                + "<test_liste class=\"array\">"
                + "<obj>test liste 1</obj>"
                + "<obj>test liste 2</obj>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0001\","
                + "\"test_liste\":{\"@class\":\"array\",\"obj\":[\"test liste 1\",\"test liste 2\"]}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0002</blocid>"
                + "<test_liste class=\"array\">"
                + "<obj>test liste 1</obj>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0002\","
                + "\"test_liste\":{\"@class\":\"array\",\"obj\":\"test liste 1\"}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0003</blocid>"
                + "<test_liste class=\"array\">"
                + "<obj>test liste 1</obj>"
                + "<obj2>test liste 2</obj2>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0003\","
                + "\"test_liste\":{\"@class\":\"array\",\"obj\":\"test liste 1\",\"obj2\":\"test liste 2\"}}",
                stXmlToJson.toJSONObject(xml).toString());
    }

    @Test
    public void convertXMLtoJSONListConfiguration() throws JSONException {
        String xml = "<bloc>"
                + "<blocid>00413695C0001</blocid>"
                + "<test_liste>"
                + "<test_liste>test liste 1</test_liste>"
                + "<test_liste>test liste 2</test_liste>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0001\","
                + "\"test_liste\":{\"test_liste\":[\"test liste 1\",\"test liste 2\"]}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0002</blocid>"
                + "<test_liste>"
                + "<test_liste>test liste 1</test_liste>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0002\","
                + "\"test_liste\":{\"test_liste\":[\"test liste 1\"]}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0003</blocid>"
                + "<test_liste>"
                + "<test_liste>"
                + "<test_liste>test liste 1</test_liste>"
                + "<test_liste>test liste 2</test_liste>"
                + "</test_liste>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0003\","
                + "\"test_liste\":{\"test_liste\":[{\"test_liste\":[\"test liste 1\",\"test liste 2\"]}]}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0004</blocid>"
                + "<test_liste>"
                + "<test_liste>"
                + "<test_liste>test liste 1</test_liste>"
                + "</test_liste>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0004\","
                + "\"test_liste\":{\"test_liste\":[{\"test_liste\":[\"test liste 1\"]}]}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0005</blocid>"
                + "<test_liste>"
                + "<test_liste>test liste 1</test_liste>"
                + "<test_liste>"
                + "<test_liste>test liste 2</test_liste>"
                + "</test_liste>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0005\","
                + "\"test_liste\":{\"test_liste\":[\"test liste 1\",{\"test_liste\":[\"test liste 2\"]}]}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0006</blocid>"
                + "<test_liste>"
                + "<test_liste>test liste 1</test_liste>"
                + "<test_liste>"
                + "<test_liste>test liste 2</test_liste>"
                + "<test_liste>test liste 3</test_liste>"
                + "</test_liste>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0006\","
                + "\"test_liste\":{\"test_liste\":[\"test liste 1\",{\"test_liste\":[\"test liste 2\",\"test liste 3\"]}]}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0007</blocid>"
                + "<test_liste>"
                + "<test_liste>test liste 1</test_liste>"
                + "<test_liste>"
                + "<test_liste1>test liste 2</test_liste1>"
                + "</test_liste>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0007\","
                + "\"test_liste\":{\"test_liste\":[\"test liste 1\",{\"test_liste1\":\"test liste 2\"}]}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0008</blocid>"
                + "<test_liste>"
                + "<test_liste1>test liste 1</test_liste1>"
                + "<test_liste>"
                + "<test_liste>test liste 2</test_liste>"
                + "</test_liste>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0008\","
                + "\"test_liste\":{\"test_liste1\":\"test liste 1\",\"test_liste\":[{\"test_liste\":[\"test liste 2\"]}]}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0009</blocid>"
                + "<test_liste>"
                + "<test_liste><test_obj>test object</test_obj></test_liste>"
                + "<test_liste1>test liste 1</test_liste1>"
                + "<test_liste><test_obj2>test object2</test_obj2></test_liste>"
                + "<test_list1>test liste 1</test_list1>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0009\","
                + "\"test_liste\":{"
                + "\"test_liste\":[{\"test_obj\":\"test object\"},{\"test_obj2\":\"test object2\"}],"
                + "\"test_liste1\":\"test liste 1\","
                + "\"test_list1\":\"test liste 1\"}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0009</blocid>"
                + "<test_liste>"
                + "<test_liste><test_obj>test object</test_obj></test_liste>"
                + "<test_liste1>test liste 1</test_liste1>"
                + "<test_list1>test liste 1</test_list1>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0009\","
                + "\"test_liste\":{\"test_liste\":[{\"test_obj\":\"test object\"}],\"test_liste1\":\"test liste 1\",\"test_list1\":\"test liste 1\"}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0003</blocid>"
                + "<test_liste>"
                + "<test_liste type=\"1\">"
                + "<test_liste>test liste 1</test_liste>"
                + "<test_liste>test liste 2</test_liste>"
                + "</test_liste>"
                + "<test_liste type=\"2\">"
                + "<test_liste>test liste 3</test_liste>"
                + "<test_liste>test liste 4</test_liste>"
                + "</test_liste>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0003\","
                + "\"test_liste\":{\"test_liste\":["
                + "{\"@type\":\"1\",\"test_liste\":[\"test liste 1\",\"test liste 2\"]},"
                + "{\"@type\":\"2\",\"test_liste\":[\"test liste 3\",\"test liste 4\"]}]}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc>"
                + "<blocid>00413695C0003</blocid>"
                + "<test_liste>"
                + "<test_liste type=\"1\">"
                + "<test_liste>test liste 1</test_liste>"
                + "</test_liste>"
                + "<test_liste type=\"2\">"
                + "<test_liste>test liste 2</test_liste>"
                + "</test_liste>"
                + "</test_liste>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0003\","
                + "\"test_liste\":{\"test_liste\":["
                + "{\"@type\":\"1\",\"test_liste\":[\"test liste 1\"]},"
                + "{\"@type\":\"2\",\"test_liste\":[\"test liste 2\"]}]}}",
                stXmlToJson.toJSONObject(xml).toString());
    }

    @Test
    public void convertXMLtoJSONAttributs() throws JSONException {
        String xml = "<bloc>"
                + "<blocid>00413695C0001</blocid>"
                + "<test_attribute"
                + " attr1=\"attribute 1\""
                + " attr2=\"attribute 2\" />"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0001\","
                + "\"test_attribute\":{\"@attr1\":\"attribute 1\",\"@attr2\":\"attribute 2\"}}",
                stXmlToJson.toJSONObject(xml).toString());
    }

    @Test
    public void convertXMLtoJSONContenu() throws JSONException {
        String xml = "<bloc>"
                + "<blocid>00413695C0001</blocid>"
                + "<test_attribute"
                + " attr1=\"attribute 1\""
                + " attr2=\"attribute 2\">contenu text</test_attribute>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0001\","
                + "\"test_attribute\":{"
                + "\"@attr1\":\"attribute 1\","
                + "\"@attr2\":\"attribute 2\","
                + "\"$content\":\"contenu text\"}}",
                stXmlToJson.toJSONObject(xml).toString());
    }

    @Test
    public void convertXMLtoJSONContenuMixte() throws JSONException {
        String xml = "<bloc>"
                + "<blocid>00413695C0001</blocid>"
                + "<test_mixte>contenu texte"
                + "<mixte>test</mixte>"
                + "</test_mixte>"
                + "</bloc>";
        Assert.assertEquals("{"
                + "\"blocid\":\"00413695C0001\","
                + "\"test_mixte\":{\"$content\":\"contenu texte\",\"mixte\":\"test\"}}",
                stXmlToJson.toJSONObject(xml).toString());
    }

    @Test
    public void convertXMLtoJSONCData() throws JSONException {
        String xml = "<bloc_cdata>"
                + "<blocid><![CDATA[<xml_test><toto test=\"ok\">cdata</toto></xml_test>]]></blocid>"
                + "<blocid2><xml_test><toto test=\"ok\">cdata</toto></xml_test></blocid2>"
                + "</bloc_cdata>";
        Assert.assertEquals("{"
                + "\"blocid\":\"<xml_test><toto test=\\\"ok\\\">cdata<\\/toto><\\/xml_test>\","
                + "\"blocid2\":{\"xml_test\":{\"toto\":{\"@test\":\"ok\",\"$content\":\"cdata\"}}}}",
                stXmlToJson.toJSONObject(xml).toString());
    }

    @Test
    public void convertXMLtoJSONFormat() throws JSONException {
        String xml = "<bloc_boolean>"
                + "<blocid attr_bool=\"1\" attr2_bool=\"true\">true</blocid>"
                + "<blocid2 attr_bool=\"0\" attr2_bool=\"false\">false</blocid2>"
                + "<blocid3 attr_bool=\"2\" attr2_bool=\"toto\">toto</blocid3>"
                + "<blocid4 attr_bool=\"1\" attr2_bool=\"true\">true</blocid4>"
                + "</bloc_boolean>";
        Assert.assertEquals("{"
                + "\"blocid\":{\"@attr_bool\":true,\"@attr2_bool\":true,\"$content\":true},"
                + "\"blocid2\":{\"@attr_bool\":false,\"@attr2_bool\":false,\"$content\":false},"
                + "\"blocid3\":{\"@attr_bool\":false,\"@attr2_bool\":false,\"$content\":false},"
                + "\"blocid4\":{\"@attr_bool\":\"1\",\"@attr2_bool\":\"true\",\"$content\":\"true\"}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc_number>"
                + "<blocid attr_number=\"004136950001\" attr2_number=\"-004136950001\">004136950001</blocid>"
                + "<blocid2 attr_number=\"004136950001.1515454354654\" attr2_number=\"-004136950001.1515454354654\">004136950001.1515454354654</blocid2>"
                + "<blocid3 attr_number=\"004136950001\" attr2_number=\"-004136950001\">004136950001</blocid3>"
                + "<blocid4 attr_number=\"004136950001.1515454354654\" attr2_number=\"-004136950001.1515454354654\">004136950001.1515454354654</blocid4>"
                + "</bloc_number>";
        Assert.assertEquals("{"
                + "\"blocid\":{\"@attr_number\":4136950001,\"@attr2_number\":-4136950001,\"$content\":4136950001},"
                + "\"blocid2\":{\"@attr_number\":4136950001.1515454354654,\"@attr2_number\":-4136950001.1515454354654,\"$content\":4136950001.1515454354654},"
                + "\"blocid3\":{\"@attr_number\":\"004136950001\",\"@attr2_number\":\"-004136950001\",\"$content\":\"004136950001\"},"
                + "\"blocid4\":{\"@attr_number\":\"004136950001.1515454354654\",\"@attr2_number\":\"-004136950001.1515454354654\",\"$content\":\"004136950001.1515454354654\"}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc_date>"
                + "<blocid attr_date=\"2011-08-26 00:10:05\" attr2_date=\"2011-08-26 00:10:05.012\">2011-08-26</blocid>"
                + "<blocid2 attr_date=\"2011-08-26 00:10:05\" attr2_date=\"2011-08-26 00:10:05.012\">2011-08-26</blocid2>"
                + "</bloc_date>";
        Assert.assertEquals("{"
                + "\"blocid\":{\"@attr_date\":{\"$date\":\"2011-08-26T00:10:05.000Z\"},\"@attr2_date\":{\"$date\":\"2011-08-26T00:10:05.012Z\"},\"$content\":{\"$date\":\"2011-08-26T00:00:00.000Z\"}},"
                + "\"blocid2\":{\"@attr_date\":\"2011-08-26 00:10:05\",\"@attr2_date\":\"2011-08-26 00:10:05.012\",\"$content\":\"2011-08-26\"}}",
                stXmlToJson.toJSONObject(xml).toString());

        xml = "<bloc_boolean2>"
                + "<blocid attr=\"1\" attr2_bool=\"true\" />"
                + "<blocid2 attr_bool=\"1\" attr2_bool=\"false\">false</blocid2>"
                + "</bloc_boolean2>";
        Assert.assertEquals("{"
                + "\"blocid\":{\"@attr\":\"1\",\"@attr2_bool\":\"true\"},"
                + "\"blocid2\":{\"@attr_bool\":true,\"@attr2_bool\":false,\"$content\":false}}",
                stXmlToJson.toJSONObject(xml).toString());
    }

    @Test
    public void convertTest() throws JSONException {
        String xml = "<bloc>"
                + "<bloc_vide></bloc_vide>"
                + "<bloc_vide2 />"
                + "<bloc_test>bloc non vide</bloc_test>"
                + "</bloc>";
        Assert.assertEquals("{\"bloc_test\":\"bloc non vide\"}", stXmlToJson.toJSONObject(xml).toString());
    }

}
