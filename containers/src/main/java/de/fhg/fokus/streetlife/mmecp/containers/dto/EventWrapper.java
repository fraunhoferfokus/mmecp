package de.fhg.fokus.streetlife.mmecp.containers.dto;


// "event": {
//       "useCaseID" : "BER ParkRide",
//		 "live": "start"
//       "context": {
//       	"select": "ParkingArea",
//       	"where": {
//       		"id": "Microzone_ABC",
//       		"type": ["free", "fee"]
//       	}
//       }
//       "filter": [
//           "working_day",
//           "time_band=morning"
//       ]
// }
import java.util.List;

public class EventWrapper {

	private String event;

	public EventWrapper() {
		this.event = null;
	}

	public EventWrapper(String ev) {
		this.event = ev;
	}

	public String getEvent() {
		return event;
	}

}
