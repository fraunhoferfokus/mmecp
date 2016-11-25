package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import de.fhg.fokus.streetlife.mmecp.containers.coord.GpsCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObject;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by csc on 23.10.2015.
 */
public class CrowdSourceBikeAccDataTest {


    private void testBaseAssertions(String json) {
        assertThat(json, startsWith("{"));
        assertThat(json, endsWith("}"));
        assertThat(json, containsString("\"type\" : \"mapobject\","));
        assertThat(json, containsString("\"objectType\" : \"dangerouspoints\","));
        assertThat(json, containsString("\"type\" : \"polygon\","));
        assertThat(json, containsString("\"coordinateType\" : \"GPS\","));
    }

    @Test(enabled = true)
    public void testCrowdSourceBikeAccData_1() throws Exception {

        MapObjectSet dataset = new MapObjectSet();
        dataset.add(new MapObject("objectID_1","subType_1"));
        dataset.add(CrowdSourceBikeAccData.makeExample().withObjectType("dangerouspoints"));

        String json = Util.prettify(dataset.toJsonString());
        this.testBaseAssertions(json);
        assertThat(json, containsString("52.522095"));
        assertThat(json, containsString("13.3491834"));
    }


    @Test(enabled = true)
    public void testCrowdSourceBikeAccData_2() throws Exception {

        List<Coordinate> list = new ArrayList<>(1);
        list.add(new GpsCoordinate(52.52, 13.13));

        MapObjectSet dataset = new MapObjectSet();
        dataset.add(new MapObject("objectID_2","subType_2"));
        dataset.add(new CrowdSourceBikeAccData("objID", "dangerouspoints", "a_street", 10, 45678).withStarsAt(list));

        String json = Util.prettify(dataset.toJsonString());
        this.testBaseAssertions(json);
        assertThat(json, containsString("52.52"));
        assertThat(json, containsString("13.12"));
    }


    @Test(enabled = true)
    public void testCrowdSourceBikeAccData_3() throws IOException {

        List<Coordinate> list = new ArrayList<>();
        list.add(new GpsCoordinate(52.521, 13.131));
        list.add(new GpsCoordinate(52.522, 13.132));
        list.add(new GpsCoordinate(52.523, 13.133));
        list.add(new GpsCoordinate(52.524, 13.134));
        list.add(new GpsCoordinate(52.525, 13.135));

        MapObjectSet dataset = new MapObjectSet();
        dataset.add(new CrowdSourceBikeAccData("objID", "dangerouspoints", "a_street", 10, 45678).withStarsAt(list));

        String json = Util.prettify(dataset.toJsonString());
        this.testBaseAssertions(json);
        assertThat(json, containsString("52.521"));
        assertThat(json, containsString("13.131"));
        assertThat(json, containsString("maparea\" : {"));
        assertThat(json, containsString("\"label\" : \"dangerouspoints\""));
        assertThat(json, containsString("\"color\" : {"));
        assertThat(json, containsString("\"objectType\" : \"dangerouspoints\""));
    }

}
