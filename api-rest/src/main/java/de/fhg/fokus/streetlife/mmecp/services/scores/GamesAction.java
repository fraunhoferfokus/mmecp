package de.fhg.fokus.streetlife.mmecp.services.scores;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Created by csc on 22.10.2015.
 */

public interface GamesAction {

    @GET
    @Path("/streetlife/gaming/getGames")
    @Produces("application/json")
    Response getGames();

    @GET
    @Path("/streetlife/gaming/gameBoard")
    @Produces("application/json")
    Response getGameBoard(@QueryParam("gameId") Integer gameId);

    @GET
    @Path("/streetlife/app/entities/all")
    @Produces("application/json")
    Response getEntitiesAll();

}
