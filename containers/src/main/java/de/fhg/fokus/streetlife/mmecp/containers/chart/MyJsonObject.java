package de.fhg.fokus.streetlife.mmecp.containers.chart;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import java.io.IOException;

/**
 * Created by csc on 25.09.2015.
 */
public class MyJsonObject {

    public String toString() {
        return this.toJsonString();
    }

    public String toJsonString() {
        ObjectMapper mapper = new ObjectMapper();
//        mapper.enableDefaultTyping(); // defaults for defaults (see below); include as wrapper-array, non-concrete types
//        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT); // all non-final types
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        try {
            return ow.writeValueAsString(this);  // return JSON representation
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
