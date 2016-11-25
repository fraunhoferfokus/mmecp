package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.impl.ShapeFileReader;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.Area;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.VmzDtvwData;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManagerException;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by csc on 17.11.2015.
 */
public class BerCO2VmzLifeDataRunner extends BaseDataRunner {

    public BerCO2VmzLifeDataRunner(LiveResponseParseEngine parent, Event event) {
        super(parent, event);
    }

    // @Inject
    // CrowdEntitiesReader crowdEntitiesReader = new CrowdEntitiesReader();
    // CrowdUsersReader crowdUsersReader = new CrowdUsersReader();
    // @Inject
    // TripHistoryReader tripHistoryReader = new TripHistoryReader();

    public void run() {
        final int maxSamples = Integer.MAX_VALUE;
        final String RESOURCENAME = "Detail_Links_V03_DTVw_polyline.shp";

        String areaType;
        int style;

        if (this.event.hasSelector("Traffic rate")) {
    /*
            maxSamples = 2000;
            areaType =   Area.AREATYPE_POLYGON;
            style    =   Area.STYLE_HEXAGON;
    */
            areaType =   Area.AREATYPE_LINE;
            style    =   Area.DEFAULT_STYLE;;

        } else  { // if (event.hasSelector("Traffic heatmap")) {

            areaType = Area.AREATYPE_HEATMAP;
            style    = Area.STYLE_POINT;

        }

        try {
            URL url = this.getClass().getClassLoader().getResource("shapeFiles" + File.separator + RESOURCENAME);
            // the reader will invoke the this.callback(response) for incremental updates to the MMECP UI
            ShapeFileReader.readVmzDtvwDataFromFile(url, maxSamples, areaType, style, this);
        } catch (Exception e) {
            LOG.error("BER CO2 life runner TEST failure: " + e.getMessage());
        }
    }


    public void callback(MapObjectSet dataset) throws IOException, SessionManagerException {
        if (dataset != null) {
            dataset.setIDsFrom(this.event);
            if (dataset.size() > 0) {
                if (this.event.hasSelector("Traffic rate")) {
                    dataset.adjustColors(VmzDtvwData.TRAFFICRATE, Color.GREEN, Color.RED, /*min*/0L, /*max*/83500L);  // fix this hack!!! use real max (where to get from with live data???
                }
            }
            callback(dataset.toJsonString());
        }
    }


}
