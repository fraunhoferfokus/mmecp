package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.runners.RetrieveTreLiveDataRunner;

/**
 * Created by bdi on 21/12/14.
 */
public class TamParkingStationEngine extends BaseLiveEngine {

    private static final String USECASENAME = "ParkAndRide@Tampere";
    public String getUseCaseID() {return USECASENAME;}

    @Override
    public String parseResponse(String response) {
        return null;
    }

    // note:
    // setEndpointCallback() and setSessionId() must have been called before the first
    // ever call to handleEvent !!!  see also EventProcessor::process(Event)

    public String handleEvent(Event event) {
        if (event == null) {return null;}
        if(event.startLiveData()) {
            this.start(event, new RetrieveTreLiveDataRunner(this, event), /*schedule every*/10/*seconds*/);
        } else if(event.stopLiveData()) {
            this.stop(event);
        }
        return "";
    }


}
