package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;

/**
 * Created by csc on 07.10.2015.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GamingData {

    private String playerName = "";
    private Integer greenLeavesScoreSum = 0;
    private Integer greenLeavesScorePeriod = 0;
    private List<Integer> ownTrees          = null;
    private List<Integer> ownPlantedTrees   = null;
    private List<Integer> ownUnplantedTrees = null;
    private List<Integer> badges            = null;

    /*
        "playerName": "Wenko",
        "greenLeavesScoreSum": 1500,
        "greenLeavesScorePeriod": 500,
        "ownPlantedTrees": [
          345,
          567,
          856
        ],
     */

    public String getPlayerName() {return playerName;}
    public void setPlayerName(String playerName) {this.playerName = playerName;}

    public Integer getGreenLeavesScoreSum() {return greenLeavesScoreSum;}
    public void setGreenLeavesScoreSum(Integer greenLeavesScoreSum) {this.greenLeavesScoreSum = greenLeavesScoreSum;}

    public Integer getGreenLeavesScorePeriod() {return greenLeavesScorePeriod;}
    public void setGreenLeavesScorePeriod(Integer greenLeavesScorePeriod) {this.greenLeavesScorePeriod = greenLeavesScorePeriod;}

    public List<Integer> getOwnPlantedTrees() {return ownPlantedTrees;}
    public void setOwnPlantedTrees(List<Integer> ownPlantedTrees) {this.ownPlantedTrees = ownPlantedTrees;}

    public List<Integer> getOwnUnplantedTrees() {return ownUnplantedTrees;}
    public void setOwnUnplantedTrees(List<Integer> ownUnplantedTrees) {this.ownUnplantedTrees = ownUnplantedTrees;}

    public List<Integer> getBadges() {return badges;}
    public void setBadges(List<Integer> badges) {this.badges = badges;}

    public List<Integer> getOwnTrees() {return ownTrees;}
    public void setOwnTrees(List<Integer> ownTrees) {this.ownTrees = ownTrees;}


}
