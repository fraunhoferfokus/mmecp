package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * Created by csc on 07.10.2015.
 */
public class CrowdUsersTest {

    private final static Logger LOG = LoggerFactory.getLogger(CrowdUsersTest.class);

    CrowdUsersAction crowdUsersAction;

    @PostConstruct
    public void init() throws GeneralSecurityException {
        this.crowdUsersAction = new CipClient().getTarget().proxy(CrowdUsersAction.class);
    }

    @Test(enabled = true)
    public void testCrowdUsersToJson() throws IOException {

        CrowdUsers cusers = new CrowdUsers(TESTJSON);
        String json = Util.prettify(cusers.toString());
        LOG.info(json);
        LOG.info(cusers.getAllDeviceIDs().toString());
        LOG.info("number of device ID entries: " + cusers.getAllDeviceIDs().size());

        assertThat(json, startsWith("{"));
        assertThat(json, endsWith("}"));
        assertThat(json, containsString("\"deviceId\" : \"e2e0er4c9ca08015a795\""));
        assertThat(json, containsString("\"streetlifeUserId\" : 83"));
    }


    private final String TESTJSON =
            " {\n\"users\": [\n" +
            "    {\n" +
            "      \"streetlifeUserId\": 5,\n" +
            "      \"creationTimeStamp\": \"2014-10-24T14:48:02\",\n" +
            "      \"devices\": [\n" +
            "        {\n" +
            "          \"appVersion\": \"5.0.1\",\n" +
            "          \"deviceId\": \"e2e0er4c9ca08015a795\",\n" +
            "          \"model\": \"Lenova K3 Note\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"appVersion\": \"5.0.1\",\n" +
            "          \"deviceId\": \"e2e0er4c9ca08015a999\",\n" +
            "          \"model\": \"Lenova K3 Note\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"appVersion\": \"5.0.1\",\n" +
            "          \"deviceId\": \"e2e0er4c9ca08015a888\",\n" +
            "          \"model\": \"Lenova K3 Note\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"googleIdHash\": \"888314694c62571dd5c9c56723a27156c668288020382db056c607bbc3487d049475ac8af22292caa14e47d6f6847f715cb17858dd8c22d86e497ff14ce22949608\",\n" +
            "      \"streetlifeUserId\": 83,\n" +
            "      \"creationTimeStamp\": \"2014-10-24T14:48:02\",\n" +
            "      \"devices\": [\n" +
            "        {\n" +
            "          \"appVersion\": \"0.0.1\",\n" +
            "          \"deviceId\": \"888e2e0c9ca08015a79\",\n" +
            "          \"model\": \"Nexus 4\"\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"streetlifeUserId\": 86,\n" +
            "      \"creationTimeStamp\": \"2015-10-09T13:27:41\",\n" +
            "      \"devices\": [\n" +
            "        {\n" +
            "          \"appVersion\": \"0.0.5\",\n" +
            "          \"deviceId\": \"y2e0c9ca08015a79\",\n" +
            "          \"model\": \"Nexus 5\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }]}";


    public String doCrowdUsersWebServiceRequest() throws GeneralSecurityException {
        init();
        Response response = null;
        try {
            response = crowdUsersAction.getAllUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info("status: " + response.getStatus());
        LOG.info("headers: " + response.getHeaders());

        if (response.getStatusInfo() == Response.Status.OK) {
            String json = response.readEntity(String.class);
            LOG.info("body is: " + Util.shorten(json,222));
            return json;
        } else {
            return "";
        }
    }


    @Test(enabled = true)
    public void testCrowdUsersWebServiceRequest() throws GeneralSecurityException {
        String json = doCrowdUsersWebServiceRequest();

        assertThat(json, startsWith("{"));
        assertThat(json, endsWith("}"));
        assertThat(json, containsString("\"streetlifeUserId\":"));
//        assertThat(json, containsString("\"model\":\"Lenova K3 Note\""));
    }


    @Test(enabled = true)
    public void testCrowdUsersWebServiceResponse() throws GeneralSecurityException {
        try {
            String input = doCrowdUsersWebServiceRequest();
            CrowdUsers cusers = new CrowdUsers(input);
            String json = cusers.toString();
            LOG.info("parsed and regenerated Json: " + json);

            assertThat(json, startsWith("{"));
            assertThat(json, endsWith("}"));
            assertThat(json, containsString("\"streetlifeUserId\":"));
//            assertThat(json, containsString("\"model\":\"Lenova K3 Note\""));

            LOG.info(cusers.getAllDeviceIDs().toString());
            LOG.info("number of device ID entries: " + cusers.getAllDeviceIDs().size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
