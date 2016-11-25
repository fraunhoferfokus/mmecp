package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.impl;

/**
 * Created by csc on 19.08.2015. Example code based on
 * http://docs.geotools.org/latest/userguide/tutorial/quickstart/maven.html
 * and possibly on
 * http://stackoverflow.com/questions/2044876/does-anyone-know-of-a-library-in-java-that-can-parse-esri-shapefiles
 */

import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import de.fhg.fokus.streetlife.mmecp.containers.coord.UtmCoordinate;
import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.UidGenerator;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.BerCO2VmzLifeDataRunner;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.Area;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.VmzDtvwData;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManagerException;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapeFileReader {

    protected static final Logger LOG = LoggerFactory.getLogger(ShapeFileReader.class);

    private static final int ID_THE_GEOM  = 0;
    private static final int ID_LINK_ID   = 1;
    private static final int ID_FROM_NODE = 2;
    private static final int ID_TO_NODE   = 3;
    private static final int ID_STR_NAME  = 4;
    private static final int ID_DTVW_R    = 5;
    private static final int ID_DTVW_L    = 6;

    // the default shape file to read the Berlin map from
    public static final String BER_SHAPE_FILE = "Detail_Links_V03_DTVw_polyline";
    // file to read the Berlin _detailed_ map from (not yet used, as reading does not work yet)
    public static final String BER_SHAPE_FILE_DN = "Detail_Links_V03_SL_oBAB_polyline";

    public static File getDefaultBerShapeFile() {return getResourceFile(BER_SHAPE_FILE, ".shp");}
    public static URL getDefaultBerShapeFileUrl() {return getResourceUrl(BER_SHAPE_FILE, ".shp");}
    public static URL getDefaultBerDnShapeFileUrl() {return getResourceUrl(BER_SHAPE_FILE_DN, ".shp");}


    public static URL getResourceUrl(String fileName, String ext) {
        if (ext == null || fileName == null || fileName.isEmpty()) {return null;}
        String resName = "shapeFiles" + File.separator + fileName + ext;
        LOG.info("trying to open: {}", resName);
        return ShapeFileReader.class.getClassLoader().getResource("shapeFiles" + File.separator + fileName + ext);
    }


    public static File getResourceFile(String fileName, String ext) {
        if (ext == null || fileName == null || fileName.isEmpty()) {return null;}
        URL url = ShapeFileReader.class.getClassLoader().getResource("shapeFiles/" + fileName + ext);
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        return file;
    }


    /**
     * @param file  the shape file to open and read from
     */
    public static synchronized MapObjectSet testShapeFileReading(File file) throws Exception {
        if (file == null) {
            LOG.info("SHP: no file specified");
            return null;
        }
        if (!file.exists()) {
            LOG.info("SHP: {} is not an existing file!", file.getName());
            return null;
        }

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        SimpleFeatureSource featureSource = store.getFeatureSource();

        LOG.info("SHP: could open file: {}", file.getName());
        LOG.info("SHP: features in file: {}", featureSource.getFeatures().size());

        // iterate over features
        SimpleFeatureIterator iter = featureSource.getFeatures().features();
        int i = 0;
        Integer uid = UidGenerator.getNewUid();
        MapObjectSet dataset = new MapObjectSet();
        while (iter.hasNext()) {
            SimpleFeature feature = iter.next();
            List attributes = feature.getAttributes();
//          System.out.println(feature.getAttributes().get(0).getClass().toString());
            if (feature.getAttributes().get(0) instanceof com.vividsolutions.jts.geom.MultiLineString) {
                if (i % 1000 == 0) {
                    System.out.println(feature);
                    System.out.println(feature.getAttributes().get(0).toString());
                    String crossingID = feature.getID().substring(feature.getID().indexOf('.') + 1);
                    String streetName = (String)attributes.get(ID_STR_NAME);
                    Long traffic = (Long)attributes.get(ID_DTVW_L) + (Long)attributes.get(ID_DTVW_R);
                    MultiLineString mls = (MultiLineString)attributes.get(ID_THE_GEOM);
                    Point point = mls.getCentroid();

                    dataset.add(new VmzDtvwData(feature.getID(), crossingID, streetName, traffic,
                                                new UtmCoordinate(point), uid));
                    System.out.println(feature.getProperties().iterator().next().toString());
                    System.out.println();
                }
                i += 1;
            }
        }
        dataset.adjustColors(VmzDtvwData.TRAFFICRATE, Color.GREEN, Color.RED);
        return dataset;
    }


    public static MapObjectSet readVmzDtvwDataFromFile(File file) throws Exception {
        return readVmzDtvwDataFromFile(file, Integer.MAX_VALUE);
    }

    public static MapObjectSet readVmzDtvwDataFromFile(File file, int maxSamples) throws Exception {
        if (file == null || !file.exists() || maxSamples < 1) {return null;}
        return readVmzDtvwDataFromFile(file.toURI().toURL(), maxSamples);
    }

    public static MapObjectSet readVmzDtvwDataFromFile(URL url, int maxSamples) throws IOException {
        return readVmzDtvwDataFromFile(url, maxSamples, Area.AREATYPE_POLYGON, Area.STYLE_HEXAGON);
    }

    public static MapObjectSet readVmzDtvwDataFromFile(URL url, int maxSamples, String areaType, int polygonStyle) throws IOException {
        return readVmzDtvwDataFromFile(url, maxSamples, areaType, polygonStyle, null);
    }

    public static synchronized MapObjectSet readVmzDtvwDataFromFile(URL url, int maxSamples, String areaType,
                                                                    int polygonStyle, BerCO2VmzLifeDataRunner runner) throws IOException {
        if (url == null || maxSamples < 1) {return null;}

        FileDataStore dataStore = null;
        SimpleFeatureIterator iter = null;
        try {
            dataStore = FileDataStoreFinder.getDataStore(url);
            SimpleFeatureSource featureSource = dataStore.getFeatureSource();

            int numFeatures = featureSource.getFeatures().size();
            int sampleEach = (maxSamples >= numFeatures) ? 1 : ((numFeatures / (maxSamples+1)) + 1);

            // iterate over features
            iter = featureSource.getFeatures().features();
            int i = 0;
            Integer uid = UidGenerator.getNewUid();
            MapObjectSet dataSet = new MapObjectSet();
            while (iter.hasNext()) {
                SimpleFeature feature = iter.next();
                List attributes = feature.getAttributes();
                if (attributes.get(ID_THE_GEOM) instanceof com.vividsolutions.jts.geom.MultiLineString) {
                    if (sampleEach == 1 || (i % sampleEach) == 0) {
                        String crossingID = feature.getID().substring(feature.getID().indexOf('.') + 1);
                        String streetName = (String)attributes.get(ID_STR_NAME);
                        Long traffic = (Long)attributes.get(ID_DTVW_L) + (Long)attributes.get(ID_DTVW_R);
                        MultiLineString mls = (MultiLineString)attributes.get(ID_THE_GEOM);

                        if (areaType == Area.AREATYPE_HEATMAP || areaType == Area.AREATYPE_POLYGON) {
                            // in this case add a heatmap point or a polygon on the basis of the center point
                            dataSet.add(new VmzDtvwData(feature.getID(), crossingID, streetName, traffic,
                                                   new UtmCoordinate(mls.getCentroid()), uid, areaType, polygonStyle));
                        } else {
                            // otherwise construct and a a set of lines, based on the full MultiLineString geometry (mls)
                            dataSet.add(new VmzDtvwData(feature.getID(), crossingID, streetName, traffic, mls, uid));
                        }

                        // support for incremental UI updates if runner is set (every n elements):
                        if (runner != null && dataSet.size() == 120) {
                            if (runner.isCanceled()) {
                                return null;
                            } else {
                                // dataSet.sort();
                                runner.callback(dataSet);
                                dataSet = new MapObjectSet();
                                Thread.sleep(180);
                            }
                        }
                    }
                    i+=1;
                }
            }
            // dataSet.sort();
            if (runner != null) {
                runner.callback(dataSet);
                dataSet = null;
            }
            // normal return of data, contains set of all VmzDtvwData read from shapefile
            return dataSet;

        } catch (IOException e) {
            throw e;
        } catch (SessionManagerException | InterruptedException e) {  // this exception may be thrown by runner.callback() or Thread.sleep
            // e.printStackTrace();
            // ignore this exception, for now. Instead, return empty data set
            return new MapObjectSet();
        } finally {
            if (iter != null) { iter.close(); }
            if (dataStore != null) { dataStore.dispose(); }
        }
    }


    public static class NamedMultiLineString {
        public MultiLineString mls;
        public String streetName;

        public NamedMultiLineString(MultiLineString mls, String streetName) {
            this.mls = mls;
            this.streetName = streetName;
        }

        public Point getCenterPoint() {
            return mls.getCentroid();
        }
    }


    public static Map<String,NamedMultiLineString> generateFromNodeIndexFromFile(URL url) throws Exception {
        return generateNodeIndexFromFile(url, ID_FROM_NODE);
    }

    public static Map<String,NamedMultiLineString> generateToNodeIndexFromFile(URL url) throws Exception {
        return generateNodeIndexFromFile(url, ID_TO_NODE);
    }

    public static Map<String,NamedMultiLineString> generateLinkNodeIndexFromFile(URL url) throws Exception {
        return generateNodeIndexFromFile(url, ID_LINK_ID);
    }


    public static synchronized Map<String,NamedMultiLineString> generateNodeIndexFromFile(URL url, int theIdIndex) throws Exception {
        if (url == null || theIdIndex < 0) {return null;}

        FileDataStore store = FileDataStoreFinder.getDataStore(url);
        SimpleFeatureSource featureSource = store.getFeatureSource();

        int numFeatures = featureSource.getFeatures().size();

        // iterate over features
        SimpleFeatureIterator iter = featureSource.getFeatures().features();
        Map map = new HashMap<String, NamedMultiLineString>(numFeatures*2);

        while (iter.hasNext()){
            SimpleFeature feature = iter.next();
            List attributes = feature.getAttributes();
            if (attributes.get(ID_THE_GEOM) instanceof com.vividsolutions.jts.geom.MultiLineString) {
                String nodeID = (String)attributes.get(theIdIndex);
                String streetName = (String)attributes.get(ID_STR_NAME);
                MultiLineString mls = (MultiLineString)attributes.get(ID_THE_GEOM);
                // Point p = mls.getCentroid();
                map.put(nodeID, new NamedMultiLineString(mls, streetName));
                // side note: this may overwrite entries in the map, in case that a nodeId is doubled in the source file
            }
        }
        if (iter != null) { iter.close(); }
        if (store != null) { store.dispose(); }
        return map;
    }


}
