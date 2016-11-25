package de.fhg.fokus.streetlife.mmecp.services.rovsim;

import de.fhg.fokus.streetlife.mmecp.containers.rovsim.ParkingLot;
import de.fhg.fokus.streetlife.mmecp.containers.rovsim.RovCalcResults;
import de.fhg.fokus.streetlife.mmecp.containers.rovsim.RovSimAction;
import de.fhg.fokus.streetlife.mmecp.containers.rovsim.RovSimResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by csc on 08.01.2016.
 */
public class RovSimServer implements RovSimAction {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public RovSimServer() {
    }


    @Override
    public String test() {
        return "Hello World!";
    }


    @Override
    public Response runSimulation(String body) {

        LOG.info("JSON IS: " + body);

        List<ParkingLot> parkingLots = ParkingLot.parseJsonList(body);
        if (parkingLots == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // else
        RovSimResults simResults = new RovSimRunner().runSimulation(parkingLots);
        LOG.info("SimResults IS: " + simResults.toJsonString());
        // return Response.status(Response.Status.OK).entity(simResults.toJsonString()).build();
        return Response.ok(simResults.toJsonString(), MediaType.APPLICATION_JSON).build();
    }

    @Override
    public Response calcParkingDemand(String rideServices, Integer capacity, Integer passingFlow, Integer regulation, Integer method) {
        RovCalcResults calcResults = new RovSimRunner().runCalculation(rideServices, capacity, passingFlow, regulation, method);
        if (calcResults == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("missing parameter or illegal parameter value(s).").
                    build();
        }
        // else
        LOG.info("CalcResults IS: " + calcResults.toJsonString());
        return Response.ok(calcResults.toJsonString(), MediaType.APPLICATION_JSON).build();
    }


}
