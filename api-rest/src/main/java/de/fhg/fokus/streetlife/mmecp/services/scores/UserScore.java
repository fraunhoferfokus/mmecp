package de.fhg.fokus.streetlife.mmecp.services.scores;

/**
 * Created by csc on 25.01.2016.
 */
public class UserScore {

    private String userName;
    private Integer treesCount;
    private Integer currentSeasonScore;
    private Integer totalScore;

    public UserScore(String userName, Integer treesCount, Integer currentSeasonScore, Integer totalScore) {
        this.userName = userName;
        this.treesCount = treesCount;
        this.currentSeasonScore = currentSeasonScore;
        this.totalScore = totalScore;
    }

    public UserScore(String playerName, Integer trees, Integer score) {
        this(playerName, trees, score, score);
    }

    public void updateScore(UserScore newScore) {
        this.treesCount = newScore.getTreesCount();
        this.currentSeasonScore = newScore.getCurrentSeasonScore();
        this.totalScore += newScore.getCurrentSeasonScore();
    }

    public String getUserName() {return userName;}
    public Integer getTreesCount() {return treesCount;}
    public Integer getCurrentSeasonScore() {return currentSeasonScore;}
    public Integer getTotalScore() {return totalScore;}

    public void setUserName(String userName) {this.userName = userName;}
    public void setTreesCount(Integer treesCount) {this.treesCount = treesCount;}
    public void setCurrentSeasonScore(Integer currentSeasonScore) {this.currentSeasonScore = currentSeasonScore;}
    public void setTotalScore(Integer totalScore) {this.totalScore = totalScore;}


}
