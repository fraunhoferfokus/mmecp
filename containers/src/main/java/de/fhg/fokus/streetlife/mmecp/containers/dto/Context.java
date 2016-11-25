package de.fhg.fokus.streetlife.mmecp.containers.dto;


// "event": {
//       "useCaseID" : "BER ParkRide",
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

import java.util.Map;

public class Context {

	private String select;
	public Map<String, Object> where;
	private String startDate;
	private String endDate;
	private String startWeekDay;

	public String getStartWeekDay() {
		return startWeekDay;
	}

	public void setStartWeekDay(String startWeekDay) {
		this.startWeekDay = startWeekDay;
	}

	public Context() {
		select = "";
		where  = null;// new HashMap(); <- using this will break EventProcessor::process (0-Ptr-Excp. at line #199)
		startDate = "";
		endDate = "";
		startWeekDay = "";
	}

	public String getSelect() {
		return select;
	}
	public String getStartDate() { return startDate; }
	public String getEndDate() { return endDate; }

	public Map<String, Object> fetchWhereWithoutID() {
		try {
			Map<String, Object> whereMapCopy = where.getClass().newInstance();
			whereMapCopy.putAll(where);
			whereMapCopy.remove("id");
			return whereMapCopy;

		} catch (Exception e) { // newInstance() throws exception upon failure
			return null;
		}
	}

}
