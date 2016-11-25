package de.fhg.fokus.streetlife.mmecp.containers.crowd;

/**
 * Created by csc on 07.10.2015.
 */
public class Entity {

    private EntitySettings entitySettings    = new EntitySettings();
    private GamingData     gamingData        = new GamingData();

    public String getDeviceId() {
        return entitySettings.getDeviceId();
    }

    public EntitySettings getEntitySettings() {return entitySettings;}

    public void setEntitySettings(EntitySettings entitySettings) {this.entitySettings = entitySettings;}

    public GamingData getGamingData() {return gamingData;}

    public void setGamingData(GamingData gamingData) {this.gamingData = gamingData;}


}
