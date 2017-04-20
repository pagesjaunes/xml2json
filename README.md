xml2json
========

xml2json is a Java library allowing to transform any XML string to a JSON format. The datatype interpretation may be customized via a property file : thus, a known xpath may be forced to a Date type, respecting a given format, a numeric value, an array, a string, ... This project is based on the json.org library (https://github.com/douglascrockford/JSON-java)

# License

This project is released under [version 2.0 of the Apache License](https://www.apache.org/licenses/LICENSE-2.0.html)

# Usage

Include the jar and use the XmlToJsonService class to convert your flow. The properties file included in the "ConfigurationMapEnumTypes" constructor sets the mapping. It relies on the folowing format :

```
xpath=datatype

where : 
    - xpath is an xml xpath (dot as field separator)
    - datatype takes its value in 
        . number
        . date|yyyy-MM-dd (in this case, the date format is given before the pipe caracter)
        . boolean
        . array


Example : 

bloc_date.blocid.attr_date=date|yyyy-MM-dd HH:mm:ss
bloc_boolean2.blocid2.attr_bool=boolean
bloc.test_liste.test_liste=array
```

```java
package com.pagesjaunes.json;

import com.pagesjaunes.json.JSONException;
import com.pagesjaunes.json.config.ConfigurationMapEnumTypes;
import com.pagesjaunes.json.service.XmlToJsonService;

public class MyConvertClass {
    private ConfigurationMapEnumTypes conf;
    private XmlToJsonService stXmlToJson;

    public MyConvertClass() {
        this.conf = new ConfigurationMapEnumTypes("xml2json-datatype-mapping.properties");
        this.stXmlToJson = new XmlToJsonService(conf.getProperties());
      }

    public String doMyConvertion() throws JSONException {
        String xml = "<bloc>"
                + "<blocid>00413695C0001</blocid>"
                + "<test_liste>test liste 1</test_liste>"
                + "<test_liste>test liste 2</test_liste>"
                + "</bloc>";


        return stXmlToJson.toJSONObject(xml).toString());
    }
}
```

# Change History

1.0.1

* Prefixed all attributes with "@" to prevent collisions with elements
* Added XmlToJsonService.doExpandArrays() to support more consistent json output
* Made json ordering consistent with XML (at least at first serialization)
* Improved exception handling (released swalled exceptions)
* Fixed incosistent unit test results

1.0.0

* Original release
