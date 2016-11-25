package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by csc on 07.10.2015.
 */
public class CrowdEntities {

    private List<Entity> entities = new ArrayList<>();

    public void add(Entity ent) {
        entities.add(ent);
    }

    public List<String> getAllDeviceIDs() {
        List<String> list = new ArrayList<>();
        for (Entity ent : entities) {
            list.add(ent.getDeviceId());
        }
        return list;
    }

    public List<Entity> getEntities() {return entities;}
    public void setEntities(List<Entity> entities) {this.entities = entities;}

    public String toString() {return Util.toJsonString(this);}

}
