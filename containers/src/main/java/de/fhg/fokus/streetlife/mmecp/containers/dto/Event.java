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

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fhg.fokus.streetlife.mmecp.containers.chart.ChartRequest;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Event {

	private String useCaseID;
	private String optionID;
	private String live;
	private Context context;
	private List<String> filter;
	private String subType;
	private ChartRequest chartRequest;

	public Event() {
		this.useCaseID    = new String();
		this.optionID     = new String();
		this.live         = new String();
		this.context      = new Context();
		this.filter       = new ArrayList<>();
		this.subType	  = new String();
		this.chartRequest = new ChartRequest();
	}

	private static final Logger LOG = LoggerFactory.getLogger(Event.class);

	private static ObjectMapper Mapper = new ObjectMapper();
	/*
	 * Loads a json file from the test/resources/events folder into an event object
	 */
    public static synchronized Event loadFromFile(String fileName) {
		if (fileName == null || fileName.isEmpty()) return null;

    	InputStream is = Thread.currentThread().getContextClassLoader().
				getResourceAsStream("events" + File.separator + fileName + ".json");

        if(is == null){
            LOG.warn("Could not find file events/{}.json", fileName);
            return null;
        }
        LOG.info("Found file events/{}.json", fileName);

        try {
            return Mapper.readValue(is, Event.class);
        } catch (IOException e) {
            LOG.error("deserialize event ({}): {}", e.getMessage(), is);
        } finally {
			IOUtils.closeQuietly(is);
		}
		return null;
    }


	public boolean hasSelector(String selector) {
		if (selector == null) return false;

		return selector.equals(context.getSelect());
	}

	public boolean isChartRequest() {return !this.getChartRequest().getType().isEmpty();}

	synchronized public boolean hasId() {
		if (getContext().where == null) return false;

		Object id = this.context.where.get("id");
		return (id != null) && (id instanceof String) && !((String)id).isEmpty();
	}

	public synchronized Map<String, Object> fetchWhereWithoutID() {
		return this.getContext().fetchWhereWithoutID();
	}

	synchronized public String fetchReturnType() {
		// get context/select element for basic return type
		String basicReturnType = getContext().getSelect();
		// check if it should return a list or a single element
		if (hasId()) {
			return basicReturnType;
		} else {
			return "List<"+basicReturnType+">";
		}
	}


	public boolean hasKeyValue(String key, String value) {
		if (key == null || value == null) {return false;}

		for (String filter : this.getFilter()) {
			String[] splitted = filter.split("=", 2);
			if (splitted.length == 2 && value.equals(splitted[1]) && key.equals(splitted[0])){
				return true;
			}
		}
		return false;
	}

	@JsonIgnore
	public boolean isSevereType() { return this.getSubType().contains("_severe"); }
	@JsonIgnore
	public boolean isStreetType() {
		return this.getSubType().contains("_street");
	}

	public boolean isUseCase(String useCase) {
		return (useCase != null && useCase.equals(this.getUseCaseID()));
	}

	public String getUseCaseID() { return useCaseID; }

	public void setUseCaseID(String useCaseID) { this.useCaseID = useCaseID; }

	public String getLive() { return live; }

	public Context getContext() {
		return context;
	}

	public List<String> getFilter() {
		return filter;
	}

	public ChartRequest getChartRequest() {
		return chartRequest;
	}

	public void setChartRequest(ChartRequest chartRequest) {
		this.chartRequest = chartRequest;
	}

	public String getSubType() { return subType; }

	public void setSubType(String subType) { this.subType = subType; }

	public String getOptionID() { return optionID; }

	public void setOptionID(String optionID) { this.optionID = optionID; }


	public synchronized boolean hasParkingAreaContext() {
		return "ParkingArea".equals(getContext().getSelect());
	}


	public synchronized boolean hasWorkingDayFilter() {
		return getFilter().contains("working_day");
	}

	public synchronized boolean hasMorningTimeBandFilter() {
		return hasKeyValue("time_band", "morning");
	}

	public synchronized boolean hasAfternoonTimeBandFilter() {
		return hasKeyValue("time_band", "afternoon");
	}

	public synchronized boolean hasNightTimeBandFilter(){
		return hasKeyValue("time_band","night");
	}

	public synchronized boolean startLiveData() { return "start".equals(this.getLive()); }

	public synchronized boolean stopLiveData(){ return "stop".equals(this.getLive()); }


}
