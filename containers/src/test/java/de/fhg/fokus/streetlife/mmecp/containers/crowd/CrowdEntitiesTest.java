package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.testng.annotations.Test;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * Created by csc on 07.10.2015.
 */
public class CrowdEntitiesTest {

    CrowdEntitiesAction crowdEntitiesAction;

    private com.fasterxml.jackson.databind.ObjectMapper om;

    //@Inject
    //@MqaServiceConfig(MqaServiceConfigKeys.CKAN_CHECK_URL)
    private final String serviceUrl = "http://private-9766f-streetlifeuserservicev2.apiary-mock.com/streetlife";


    private class Authenticator implements ClientRequestFilter {
        public void filter(ClientRequestContext clientRequestContext) throws IOException {
            MultivaluedMap<String, Object> headers = clientRequestContext.getHeaders();
            headers.add("content-type", "application/json");
            headers.add("authorization", "Basic <--ADD CIP PASSWORD HASH -->");
        }
    }

    @PostConstruct
    public void init() {
        ResteasyClient client = new ResteasyClientBuilder().build().register(new Authenticator());
        ResteasyWebTarget target = client.target(serviceUrl);
        crowdEntitiesAction = target.proxy(CrowdEntitiesAction.class);
        om = new com.fasterxml.jackson.databind.ObjectMapper();
    }

    @Test(enabled = true)
    public void testCrowdEntitiesToJson() throws IOException {

        CrowdEntities cent = new CrowdEntities();
        cent.add(new Entity());
        String json = Util.prettify(cent.toString());
        //System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, endsWith("}"));
        assertThat(json, containsString(("\"deviceId\" : \"\"")));
        assertThat(json, containsString(("\"streetlifeUserId\" : 0,")));
    }


    private final String TESTJSON = "{\n" +
            "  \"entities\": [\n" +
            "    {\n" +
            "      \"entitySettings\": {\n" +
            "        \"streetlifeUserId\": 43,\n" +
            "        \"deviceId\": \"e2e0c9ca08015a79\",\n" +
            "        \"model\": \"Nexus 4\",\n" +
            "        \"appVersion\": \"0.0.1\",\n" +
            "        \"googleIdHash\": \"314694c62571dd5c9c56723a27156c668288020382db056c607bbc3487d049475ac8af22292caa14e47d6f6847f715cb17858dd8c22d86e497ff14ce22949608\",\n" +
            "        \"creationTimeStamp\": \"2014-10-24T14:48:02\"\n" +
            "      },\n" +
            "      \"gamingData\": {\n" +
            "        \"playerName\": \"Horst\",\n" +
            "        \"greenLeavesScoreSum\": 1500,\n" +
            "        \"greenLeavesScorePeriod\": 500,\n" +
            "        \"ownPlantedTrees\": [\n" +
            "          345,\n" +
            "          567,\n" +
            "          856\n" +
            "        ],\n" +
            "        \"badges\": [],\n" +
            "        \"ownUnplantedTrees\": [1001]\n" +
            "      }\n" +
            "    }    \n" +
            "  ]\n" +
            "}    ";


    @Test(enabled = true)
    public void testCrowdEntitiesFromJson() throws IOException {

        org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();

        // JsonNode root = Mapper.readTree(TESTJSON).get("entities");
        // Iterator<JsonNode> iter = root.iterator();
        // while (iter.hasNext()) {
        //    Entity ent = Mapper.readValue(iter.next(), Entity.class);
        // }

        CrowdEntities cent = mapper.readValue(TESTJSON, CrowdEntities.class);

        String json = Util.prettify(cent.toString());
        //System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, endsWith("}"));
        assertThat(json, containsString(("\"deviceId\" : \"e2e0c9ca08015a79\",")));
        assertThat(json, containsString(("\"greenLeavesScorePeriod\" : 500,")));
    }


    public String doCrowdEntitiesWebServiceRequest() throws IOException {
        init();
        Response response = crowdEntitiesAction.getAllEntities();

        System.out.println("MMM status: " + response.getStatus());
        System.out.println("MMM headers: " + response.getHeaders());

        if (response.getStatusInfo() == Response.Status.OK) {
            String json = response.readEntity(String.class);
            System.out.println("MMM string out: " + json);
            return json;
        } else {
            return "";
        }
    }


    @Test(enabled = true)
    public void testCrowdEntitiesWebServiceRequest() throws IOException {
        String json = Util.prettify(doCrowdEntitiesWebServiceRequest());
        System.out.println("body:" + json);

        assertThat(json, startsWith("{"));
        assertThat(json, endsWith("}"));
        assertThat(json, containsString(("\"deviceId\" : \"e2e0c9ca08015a79\",")));
        assertThat(json, containsString(("\"greenLeavesScorePeriod\" : 500,")));
    }


    @Test(enabled = true)
    public void testCrowdEntitiesWebServiceResponse() throws IOException {

        String input = doCrowdEntitiesWebServiceRequest();
        ObjectMapper mapper = new ObjectMapper();
        CrowdEntities cent = mapper.readValue(input, CrowdEntities.class);
        String json = Util.prettify(cent.toString());
        System.out.println("parsed and regenerated Json: " + json);

        assertThat(json, startsWith("{"));
        assertThat(json, endsWith("}"));
        assertThat(json, containsString(("\"deviceId\" : \"e2e0c9ca08015a79\",")));
        assertThat(json, containsString(("\"greenLeavesScorePeriod\" : 500,")));
    }


}
