package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Created by csc on 22.10.2015.
 */

public interface TripHistoryAction {

    /* Apiary interface, deprecated
    @GET
    @Path("entities/{id}/tripHistory")
    @Produces("application/json")
    Response getTripHistory(@PathParam("id") String userID);
    */

    // CIP style interface, current
    // get all trip history per "user", i.e. per deviceId
    @GET
    @Path("/streetlife/tripHistory/entities/all")
    @Produces("application/json")
    Response getTripHistory(@QueryParam("deviceId") String deviceId);


    // CIP style interface, current
    // get trip history per "user", i.e. per deviceId, by latest game
    @GET
    @Path("/streetlife/tripHistory/entities")
    @Produces("application/json")
    Response getTripHistoryLatestGame(@QueryParam("deviceId") String deviceId);


    // CIP style interface, current
    // get trip history per "user", i.e. per deviceId, and by game
    @GET
    @Path("/streetlife/tripHistory/entities")
    @Produces("application/json")
    Response getTripHistoryByGame(@QueryParam("deviceId") String deviceId, @QueryParam("gameId") int gameId);


    // CIP style interface, current
    // get trip history per "user", i.e. per deviceId, and by game
    @GET
    @Path("/streetlife/tripHistory/game")
    @Produces("application/json")
    Response getTripHistoryCurrentGamePaged(@QueryParam("page") int page);


    // CIP style interface, current
    // get trip history per date
    // e.g. GET https://www.cityintelligenceplatform.siemens.com/streetlife/tripHistory/perDate?date=25-02-2016
    @GET
    @Path("/streetlife/tripHistory/perDate")
    @Produces("application/json")
    Response getTripHistoryByDate(@QueryParam("date") String date);


    // CIP style interface, current
    // get trip history per date
    // e.g. GET https://www.cityintelligenceplatform.siemens.com/streetlife/tripHistory/perDate?date=25-02-2016
    @GET
    @Path("/streetlife/tripHistory/perDate")
    @Produces("application/json")
    Response getTripHistoryByDatePaged(@QueryParam("date") String date, @QueryParam("page") int page);


}
