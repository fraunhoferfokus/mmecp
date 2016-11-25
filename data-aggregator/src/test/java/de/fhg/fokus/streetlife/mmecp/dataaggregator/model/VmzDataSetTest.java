package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.impl.ShapeFileReader;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObject;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.VmzDtvwData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by csc on 06.10.2015.
 */
public class VmzDataSetTest {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    /*
    {
      "mapobjects" : [ {
         "type" : "mapobject",
         "objectID" : "objectID",
    ...
     */

    @Test(enabled = true)
    public void testVmzDataSet() throws Exception {

        MapObjectSet dataset = new MapObjectSet();
        dataset.add(new MapObject("objectID","a_subtype"));
        String json = Util.prettify(dataset.toJsonString());
        assertThat(json, startsWith("{"));
        assertThat(json, endsWith("}"));
        assertThat(json, containsString("\"mapobjects\" :"));
        assertThat(json, containsString("\"type\" : \"mapobject\""));
        assertThat(json, containsString("\"objectID\" : \"objectID\""));
    }


    @Test(enabled = true)
    public void checkVmzDtvwData_JsonOutputGeneration() throws Exception {

        MapObjectSet s = new MapObjectSet();
        s.add(VmzDtvwData.makeExample());
        String json = Util.prettify(s.toJsonString());
        //LOG.info("VmzDtvwData example with ONE element is: {}", json);
        assertThat(json, containsString("\"objectSubtype\" : \"DTVw\""));
    }


    @Test(enabled = true)
    public void checkVmzDtvwDataList_JsonOutputGeneration() throws Exception {

        String json = Util.prettify(VmzDtvwData.makeExampleList(3).toJsonString());
        LOG.info("VmzDtvwData example LIST(3) is: {}", json);
        assertThat(json, containsString("\"objectSubtype\" : \"DTVw\""));
    }


    @Test(enabled = true)
    public void checkVmzDtvwDataList_JsonOutputGeneration_fromFile() {

        final String FILENAME = "Detail_Links_V03_DTVw_polyline.shp";

        URL url = this.getClass().getClassLoader().getResource("shapeFiles/" + FILENAME);
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }

        List vmzDataList = null;
        try {
            MapObjectSet dataset = ShapeFileReader.readVmzDtvwDataFromFile(file, 10 /*max elements to read*/);
            String json = Util.prettify(dataset.toJsonString());
            assertThat(json, containsString("\"objectID\" : \"Detail_Links_V03_DTVw_polyline.1\""));
            //LOG.info("SHP success: found objectID Detail_Links_V03_DTVw_polyline.1 in shape file!");
        } catch (Exception e) {
            // e.printStackTrace();
            //LOG.info("SHP failure: " + e.getMessage());
        }
    }


}
