/**
 *
 */
package com.pagesjaunes.json.config;

/**
 * @author pagesjaunes
 *
 */
public class JsonConf {

    private Types types;

    private String format;

    public Types getTypes() {
        return types;
    }

    public void setTypes(Types pTypes) {
        types = pTypes;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String pFormat) {
        format = pFormat;
    }

    @Override
    public String toString() {
        return "JsonConf [types=" + types + ", format=" + format + "]";
    }

}
