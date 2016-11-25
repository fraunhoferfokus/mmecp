package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.chart.*;
import de.fhg.fokus.streetlife.mmecp.containers.coord.Coordinate;
import de.fhg.fokus.streetlife.mmecp.containers.crowd.ModalSplitStats;
import de.fhg.fokus.streetlife.mmecp.containers.crowd.SegmentData;
import de.fhg.fokus.streetlife.mmecp.containers.crowd.TripHistory;
import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.CrowdSourceBikeAccData;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.Area;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.crowd.CrowdUsersReader;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.crowd.TripHistoryReader;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManagerException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by csc on 17.11.2015.
 */
class BerBikeCrowdSourceDataRunner extends BaseDataRunner {

    private int mode = 0;
    // @Inject
    // CrowdEntitiesReader crowdEntitiesReader = new CrowdEntitiesReader();
    CrowdUsersReader crowdUsersReader = new CrowdUsersReader();
    // @Inject
    TripHistoryReader tripHistoryReader = new TripHistoryReader();


    public BerBikeCrowdSourceDataRunner(LiveResponseParseEngine parent, Event event) {
        super(parent, event);
        if (event != null) {
            if (event.getOptionID().endsWith("selected")) {
                this.mode = TripHistory.MODE_GET_ONLY_SELECTED_TRIPS;
            }
            if (event.getOptionID().endsWith("detailed")) {
                this.mode = TripHistory.MODE_GET_DETAILED_TRIPS_WHERE_POSSIBLE;
            }
            if (event.getOptionID().endsWith("tracked")) {
                this.mode = TripHistory.MODE_GET_ONLY_TRACKED_DATA;
            }
            if (event.getOptionID().endsWith("dangerous")) {
                this.mode = TripHistory.MODE_GET_ONLY_DANGEROUS_POINTS;
            }
        }
    }


    // date format = 18-03-2016
    private final String DATE_FORMAT = "dd-MM-yyyy";
    final DateFormat DateFormatter = new SimpleDateFormat(DATE_FORMAT);

    private boolean isTodayOrYesterday(LocalDate date) {
        return (this.isToday(date) || this.isYesterday(date));
    }

    private boolean isToday(LocalDate date) {
        return date.isEqual(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    private boolean isYesterday(LocalDate date) {
        return date.plusDays(1).isEqual(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    private List<LocalDate> makeDateList(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> list = new ArrayList<>(40);
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            list.add(date);
        }
        //LOG.info("GOT DATES: " + list.toString());
        return list;
    }

    private LocalDate choseDate(String inputDate, String defaultDate) {
        String theDate = (inputDate != null && !inputDate.isEmpty()) ? inputDate : defaultDate;
        try {
            return this.DateFormatter.parse(theDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            return new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); // = today
        }
    }

    private LocalDate fetchStartDate() {
        return choseDate(this.event.getContext().getStartDate(), "01-04-2016");
    }

    private LocalDate fetchEndDate() {
        return choseDate(this.event.getContext().getEndDate(), null/*defaults to today if getEndDate delivers nothing*/);
    }


    public void run() {
        try {
            LOG.info("Running BerBikeCrowdSourceDataRunner in order to get live data...");
            LOG.info("Calling CIP API 1/2");

            //CrowdEntities entities = crowdEntitiesReader.getAllDeviceIDs();
            //List<String> deviceIDs = crowdUsersReader.getAllDeviceIDs();
            //LOG.info("found device IDs: {}", deviceIDs.toString());
            //LOG.info("number of device IDs gotten from CIP: {}", deviceIDs.size());

            Integer numDays = 0; Integer numBikeUse = 0;
            Integer numTrips = 0; Integer numTripsOld = 0;
            Integer numIcons = 0; Integer numIconsOld = 0;

            ModalSplitStats modalSplitStats = new ModalSplitStats();

            //LOG.info("start date json is: {}", this.event.getContext().getStartDate());
            //LOG.info("start date used is: {}", this.fetchStartDate().toString());
            //LOG.info("end date json is: {}", this.event.getContext().getEndDate());
            //LOG.info("end date used is: {}", this.fetchEndDate().toString());
            List<LocalDate> dateList = this.makeDateList(this.fetchStartDate(), this.fetchEndDate());

            for (LocalDate date : dateList) {
                if (isCanceled()) return; // terminate thread if running has been set to false (by parent)
                //if ("unknown".equals(deviceId)) continue;

                //int numDevices = deviceIDs.size();
                //LOG.info("Calling CIP API 2/2 (device " + (++numDevice) + "/" + numDevices + ") with date: " + date);

                numDays += 1;
                int pageCount;
                try {
                    LOG.info("Calling CIP API 1/2 for current game");
                    TripHistory thist = tripHistoryReader.getTripHistoryByDatePaged(date, 0);
                    pageCount = thist.getPageCount();
                    LOG.info("got to fetch data from " + pageCount + " pages for date " + date);
                } catch (IOException e) {
                    LOG.error("could not get number of pages for current game at day " + date);
                    return;
                }

                for (int page = 1; page <= pageCount; page++) {
                    TripHistory thist;
                    try {
                        String fileName = "tripHistoryData" + File.separator + "thist_" + date + "_" + page + ".gz";

                        if ( new File(fileName).exists() == true) {
                            // if trip data for current page exists as local file then read trip data from the file
                            thist = new TripHistory(Util.readFromGzipFile(fileName));
                        } else {
                            LOG.info("Calling CIP API 2/2 with date: " + date);
                            thist = tripHistoryReader.getTripHistoryByDatePaged(date, page);
                            LOG.info("Successfully called CIP API for trip history.");

                            // log all received trip data to a file, for later use, except for latest two days
                            if (!isTodayOrYesterday(date)) {
                                Util.writeToGzipFile(thist.toString(), fileName);
                            }
                        }
                        if (thist == null) continue;

                    } catch (IOException e) {
                        LOG.error("BER bike runner thread cannot parse reply: {}", e.toString());
                        continue;
                    }

                    LOG.info(thist.getSafeTripStatsMessage());

                    MapObjectSet dataset = new MapObjectSet();
                    if ((this.mode & TripHistory.MODE_GET_ONLY_DANGEROUS_POINTS) != 0) {
                        List<Coordinate> dangerousPoints = thist.getAllDangerousPoints();
                        if (dangerousPoints.size() >= 1) {
                            LOG.info("Extracted " + dangerousPoints.size() + " dangerous points : " + dangerousPoints.toString());
                            Integer a = numIcons + 1;
                            Integer b = numIcons + dangerousPoints.size();
                            CrowdSourceBikeAccData cdata = new CrowdSourceBikeAccData("csdp1_", "dangerousPoints", date.hashCode());
                            dataset.add(cdata.withAttribute("rated", "dangerous")
                                    .withAttribute("points no.", a + "-" + b)
                                    //.withAttribute("from device", numDevice.toString() + "/" + numDevices)
                                    .withIconsAt(dangerousPoints, Area.Icon.DANGER_ICON)
                                    .setGroupIdAll("group" + dangerousPoints.hashCode()));
                            numIcons += dangerousPoints.size();
                            LOG.info("So far, got " + numIcons + " dangerous points via data from " + numDays + " different Days");
                        }
                    }
                    try {
                        if (mode == TripHistory.MODE_GET_ONLY_TRACKED_DATA) {
                            List<List<SegmentData>> trips = thist.getAllTripsTracked(this.mode);
                            for (List<SegmentData> trip : trips) {
                                CrowdSourceBikeAccData cdata = new CrowdSourceBikeAccData("csdp2_", "segment", trip);
                                dataset.add(cdata
                                        //.withAttribute("device", numDevice.toString() + "/" + numDevices)
                                        /*.withAttribute("track", i.toString() + "/" + trips.size() + " on this day")*/);
                                numTrips += 1;
                            }
                        } else if (mode == TripHistory.MODE_GET_ONLY_SELECTED_TRIPS ||
                                mode == TripHistory.MODE_GET_DETAILED_TRIPS_WHERE_POSSIBLE) {
                            List<List<SegmentData>> trips = thist.getAllTripsSelected(this.mode);
                            for (List<SegmentData> trip : trips) {
                                CrowdSourceBikeAccData cdata = new CrowdSourceBikeAccData("csdp3_", "segment", trip);
                                if (cdata.hasBikeTransport) numBikeUse += 1;
                                dataset.add(cdata
                                        //.withAttribute("device", numDevice.toString() + "/" + numDevices)
                                        /*.withAttribute("trip", i.toString() + "/" + trips.size() + " for device")*/);
                                numTrips += 1;
                                modalSplitStats.count(trip);
                            }
                            LOG.info("Overall modal split stats (counted over trip segments): {}", modalSplitStats.toPercentagesString());
                        }
                    } catch (IOException e) {
                        LOG.error("BER bike acc runner thread fail at getAllTripsTracked or getAllTripsSelected: {}", e.toString());
                        continue;
                    }

                    LOG.info("So far, got " + numTrips + " trips via data from " + numDays + " different Days");
                    this.prepareAndSendDataset(dataset);
                }

                if (numTrips > numTripsOld || numIcons > numIconsOld) {
                    this.sendTripsAsBarGraph(mode, numTrips, numBikeUse, numIcons);
                    numTripsOld = numTrips;
                    numIconsOld = numIcons;
                }
            }

        } catch (IOException | SessionManagerException e) {
            LOG.info("Could not gather and send back results for dangerous points data:\n{}", e);
        }
        this.firstTime = false;
    }


    private void sendTripsAsBarGraph(int mode, Integer numTrips, Integer numBikeUse, Integer numIcons) throws IOException, SessionManagerException {

        ChartData cd;
        if (mode == TripHistory.MODE_GET_ONLY_TRACKED_DATA) {
            cd = Histogram.makeSingleColumnChart("tracked trips so far", numTrips);
        } else if (mode == TripHistory.MODE_GET_ONLY_SELECTED_TRIPS ||
                mode == TripHistory.MODE_GET_DETAILED_TRIPS_WHERE_POSSIBLE) {
            cd = Histogram.makeDoubleColumnChart("selected trips so far", "with bike", numTrips, numBikeUse);
        } else if ((this.mode & TripHistory.MODE_GET_ONLY_DANGEROUS_POINTS) != 0 ) {
            cd = Histogram.makeSingleColumnChart("dangerous points so far", numIcons);
        } else {
            return;
        }

        ChartOptions cho = new ChartOptions(ChartOptions.TYPE_BARCHART, 240, 400, true, true, 1, 0.1);
        //cho.setxAxis("test", 8 /*pixels distance*/);
        cho.setyAxis("count", 25 /*pixels distance*/);

        Chart chart = new Chart(this.event, "" /*graph title*/, cho, cd);
        //chart.setDescription("initial BER accidents bar chart description");

        String json = new Charts(chart).toString();
        //LOG.info("CHART IS: " + json);
        callback(json);
    }


    private void prepareAndSendDataset(MapObjectSet dataset) throws IOException, SessionManagerException {
        if (dataset != null && dataset.size() > 0) {
            dataset.setIDsFrom(event);
            // dataset.setLegend(Legend.tripsLegend);
            String response = dataset.toJsonString();
            LOG.info("for BER dangerous points data calling toPanelEndpoint callback with response:\n{}", Util.shorten(response, 99));
            callback(response);
        }
    }


}
