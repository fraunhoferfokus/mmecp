package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by csc on 29.10.2015.
 */
public class CrowdUsers {

    // private static ObjectWriter ow = Mapper.writer().withDefaultPrettyPrinter();
    private static ObjectMapper Mapper = new ObjectMapper();

    JsonNode root = null;

    public CrowdUsers(String json) throws IOException {
        this.parse(json);
    }

    private synchronized void parse(String json) throws IOException {
        root = Mapper.readTree(json);
    }

    public List<String> getAllDeviceIDs() {
        List<JsonNode> devices = root.findValues("deviceId");
        List list = new ArrayList();
        for (JsonNode device : devices) {
            if (device == null) {continue;}
            String deviceId = device.asText();
            if ("unknown".equals(deviceId)) {continue;}
            // else
            list.add(deviceId);
        }
        return list;
    }

    public String toJsonString() {
        // return ow.writeValueAsString(root);
        return root.toString();
    }

    public String toString() {
        return this.toJsonString();
    }

}
