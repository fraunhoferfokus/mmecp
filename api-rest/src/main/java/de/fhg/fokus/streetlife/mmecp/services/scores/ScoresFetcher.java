package de.fhg.fokus.streetlife.mmecp.services.scores;

import de.fhg.fokus.streetlife.mmecp.containers.crowd.CipClient;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;

/**
 * Created by csc on 26.01.2016.
 */
public class ScoresFetcher  {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper mapper = new ObjectMapper();

    private GamesAction gamesAction;


    public ScoresFetcher() throws IOException {
        try {
            this.gamesAction = new CipClient().getTarget().proxy(GamesAction.class);
        } catch (GeneralSecurityException e) {
            throw new IOException("ScoresFetcher - cannot instantiate GamesAction CIP client due to problem: " + e.toString());
        }

    }


    public String getAllScores() throws IOException {

        Response response = this.getEntitiesAll();

        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new IOException(response.getStatusInfo().getReasonPhrase());
        }

        String json = response.readEntity(String.class);
        //LOG.info("Entities BODY is: " + Util.shorten(json, 555));

        ScoresTable scoresTable = new ScoresTable();

        JsonNode entities = mapper.readTree(json).get("entities");

        Iterator<JsonNode> iter = entities.iterator();
        while (iter.hasNext()) {
            JsonNode node = iter.next();

            JsonNode gamingData = node.get("gamingData");

            JsonNode playerNameNode = gamingData.get("playerName");
            if (playerNameNode == null || playerNameNode.isNull()) continue;
            String playerName = playerNameNode.asText();
            if (playerName == null || playerName.isEmpty()) continue;

            Integer scorePeriod = gamingData.get("greenLeavesScorePeriod").asInt(0);
            Integer scoreSum = gamingData.get("greenLeavesScoreSum").asInt(0);

            ArrayNode trees1 = (ArrayNode) gamingData.get("ownPlantedTrees");
            ArrayNode trees2 = (ArrayNode) gamingData.get("ownUnplantedTrees");
            Integer treesCount = trees1.size() + trees2.size();

            {
                JsonNode entitySettings = node.get("entitySettings");
                JsonNode creationNode = entitySettings.get("creationTimeStamp");
                if (creationNode == null || creationNode.isNull()) continue;
                String creationTimeStamp = creationNode.asText();
                String date = creationTimeStamp.split(" ")[0];

                // this functionality is only needed for the Berlin App pilot test starting on 15.02.2016
                if (date.compareTo(cutOffDate) < 0) continue;

                //LOG.info("Creation date for entity \"" + playerName + "\" is \"" + date +"\"");
            }

            if (treesCount > 0 || scorePeriod > 0 || scoreSum > 0) {
                scoresTable.add(new UserScore(playerName, treesCount, scorePeriod, scoreSum));
            }
        }

        json = scoresTable.toJsonString();
        //LOG.info("all scores Table result IS: " + json);
        return json;
    }

    private String cutOffDate = "1916-02-13"; // only consider players who registered on or after this date!

    public String setCutoffDate(String dateString) {

        // Attention: this next comparison is only here to "lock" the date during the Berlin test run
        // once it has been set to 2016-02-13, no new date can be set till a redeploy of teh api-rest module
        if (!this.cutOffDate.equals("2016-02-26")) {
            this.cutOffDate = dateString;
            return "Okay.";
        } else {
            return "Locked.";
        }
    }


    public Response getEntitiesAll() {
        Response response = null;
        try {
            response = gamesAction.getEntitiesAll();
            //LOG.info("status: " + response.getStatus());
            //LOG.info("headers: " + response.getHeaders());
            return response;
        } catch (Exception e) {
            //LOG.error("Error while getting entities: ", e);
            return Response.serverError().build();
        } finally {
            if (response != null )
                EntityUtils.consumeQuietly((HttpEntity)response.getEntity());
        }
    }


    public Response getGames() {
        Response response = null;
        try {
            response = gamesAction.getGames();
            LOG.info("status: " + response.getStatus());
            LOG.info("headers: " + response.getHeaders());
            return response;
        } catch (Exception e) {
            LOG.error("Error while getting games: ", e);
            return Response.serverError().build();
        } finally {
            if (response != null )
                EntityUtils.consumeQuietly((HttpEntity)response.getEntity());
        }
    }


    public Response getGameBoard(Integer gameID) {
        Response response = null;
        try {
            response = gamesAction.getGameBoard(gameID);
            LOG.info("status: " + response.getStatus());
            LOG.info("headers: " + response.getHeaders());
            return response;
        } catch (Exception e) {
            LOG.error("Error while getting game board: ", e);
            return Response.serverError().build();
        } finally {
            if (response != null )
                EntityUtils.consumeQuietly((HttpEntity)response.getEntity());
        }
    }


}
