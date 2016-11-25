package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.crowd;

import de.fhg.fokus.streetlife.mmecp.containers.crowd.CipClient;
import de.fhg.fokus.streetlife.mmecp.containers.crowd.CrowdUsers;
import de.fhg.fokus.streetlife.mmecp.containers.crowd.CrowdUsersAction;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;


/**
 * Created by csc on 07.10.2015.
 */

// @Named
// @RequestScoped
//@SessionScoped
//@ApplicationScoped
public class CrowdUsersReader {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    org.codehaus.jackson.map.ObjectMapper mapper;

    CrowdUsersAction crowdUsersAction;

    public CrowdUsersReader() {
        try {
            this.init();
        } catch (GeneralSecurityException e) {
            // TODO: fix exception handling in this class!!!
            e.printStackTrace();
        }
    }

    //@PostConstruct
    private void init() throws GeneralSecurityException {
        mapper = new org.codehaus.jackson.map.ObjectMapper();
        this.crowdUsersAction = new CipClient().getTarget().proxy(CrowdUsersAction.class);
    }


    public List<String> getAllDeviceIDs() throws IOException {
        Response response = crowdUsersAction.getAllUsers();

        LOG.info("CrowdUsersReader status: " + response.getStatus());
        // LOG.info("CrowdUsersReader headers: " + response.getHeaders());

        if (response.getStatusInfo() != Response.Status.OK) {
            throw new IOException("web service failure: " +
                    Integer.toString(response.getStatusInfo().getStatusCode()) + " " +
                    response.getStatusInfo().getReasonPhrase());
        }
        // else, all is okay:
        String json = response.readEntity(String.class);
        LOG.info("CrowdUsersReader WS response:\n" + Util.shorten(json,555));

        CrowdUsers crowdUsers = new CrowdUsers(json);
        List<String> deviceIDs = crowdUsers.getAllDeviceIDs();
        LOG.info("CrowdUsersReader found deviceIDs: " + deviceIDs.toString());
        return deviceIDs;
    }


}
