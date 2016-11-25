package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.crowd;

import de.fhg.fokus.streetlife.mmecp.containers.crowd.CrowdEntities;
import de.fhg.fokus.streetlife.mmecp.containers.crowd.CrowdEntitiesAction;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.io.IOException;


/**
 * Created by csc on 07.10.2015.
 */

// @Named
// @RequestScoped
//@SessionScoped
//@ApplicationScoped
public class CrowdEntitiesReader {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    org.codehaus.jackson.map.ObjectMapper mapper;

    CrowdEntitiesAction crowdEntitiesAction;

    public CrowdEntitiesReader() {
        this.init();
    }

    //@Inject
    //@MqaServiceConfig(MqaServiceConfigKeys.CKAN_CHECK_URL)
    private final String serviceUrl = "http://private-9766f-streetlifeuserservicev2.apiary-mock.com/streetlife";

    @PostConstruct
    public void init() {
        mapper = new org.codehaus.jackson.map.ObjectMapper();

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(serviceUrl);
        crowdEntitiesAction = target.proxy(CrowdEntitiesAction.class);
    }


    public CrowdEntities getAllEntities() throws IOException {
        Response response = crowdEntitiesAction.getAllEntities();

        LOG.info("CrowdEntitiesReader status: " + response.getStatus());
        // LOG.info("CrowdEntitiesReader headers: " + response.getHeaders());

        if (response.getStatusInfo() != Response.Status.OK) {
            throw new IOException("web service failure: " +
                    Integer.toString(response.getStatusInfo().getStatusCode()) + " " +
                    response.getStatusInfo().getReasonPhrase());
        }
        // else, all is okay:
        String json = response.readEntity(String.class);
        LOG.info("CrowdEntitiesReader WS response:\n" + Util.shorten(json,555));
        return mapper.readValue(json, CrowdEntities.class);
    }


}
