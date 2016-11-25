package de.fhg.fokus.streetlife.mmecp.services.scores;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csc on 08.01.2016.
 */
public class ScoresTable {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private List<UserScore> scores;

    public ScoresTable() {
        // JsonNode root = MAPPER.readTree(json);
        this.scores = new ArrayList<UserScore>();

//        scores.add(new UserScore("Carsten", 11, 40, 120));
//        scores.add(new UserScore("Manuel", 7, 52, 105));
//        scores.add(new UserScore("Ben", 5, 32, 95));
    }


    public void add(UserScore userScore) {
        scores.add(userScore);
    }


    public void update(UserScore newScore) {
        if (newScore == null || newScore.getUserName() == null || newScore.getUserName().isEmpty()) return;

        boolean found = false;
        for (UserScore entry : scores) {
            if (entry.getUserName().equals(newScore.getUserName())) {
                found = true;
                entry.updateScore(newScore);
                break; // assume that there exists only one entry with a matching user name
            }
        }
        if (found == false) {
            // add as new entry
            scores.add(newScore);
        }
    }

    public String toJsonString() {
        return Util.toJsonString(scores);
    }


}
