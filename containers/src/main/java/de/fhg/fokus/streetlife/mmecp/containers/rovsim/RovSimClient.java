package de.fhg.fokus.streetlife.mmecp.containers.rovsim;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by csc on 08.01.2016.
 */
//@Named
//@RequestScoped
//@SessionScoped
//@ApplicationScoped
public class RovSimClient {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    //private static final ObjectMapper MAPPER = new ObjectMapper();

    private RovSimAction rovAction;

    public RovSimClient() {
        try {
            this.init();
        } catch (GeneralSecurityException e) {
            // TODO: fix exception handling in this class!!!
            e.printStackTrace();
        }
    }

    @PostConstruct
    private void init() throws GeneralSecurityException {
        this.rovAction = new RovClient().getTarget().proxy(RovSimAction.class);
    }


    public RovSimResults runSimulation(List<ParkingLot> existingParking, ParkingLot newParking) throws IOException {

        List<ParkingLot> parking = new LinkedList<>();
        parking.addAll(existingParking);

        newParking.setOccupation(0);
        parking.add(newParking);

        try {
            // Response response = new RovSimServer().runSimulation(ParkingLot.toJsonString(parking));
            Response response = rovAction.runSimulation(ParkingLot.toJsonString(parking));

            LOG.info("RovSimRunner status: " + response.getStatus());
            // LOG.info("RovSimRunner headers: " + response.getHeaders());

            if (response.getStatusInfo() != Response.Status.OK) {
                throw new IOException("Service failure: " +
                        Integer.toString(response.getStatusInfo().getStatusCode()) + " " +
                        response.getStatusInfo().getReasonPhrase());
            }
            // else, all is okay:
            String json = response.readEntity(String.class);
            LOG.info("RovSimRunner service response:\n" + Util.shorten(json,555));

            return new RovSimResults(json);

        } catch (Exception e) {
            LOG.warn("Could NOT run simulation or parse Json results successfully: " + e.getMessage());
            return null;
        }
    }


}
