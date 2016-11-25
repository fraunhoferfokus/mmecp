package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.util.Color;
import org.jboss.resteasy.spi.NotImplementedYetException;

import java.util.Collections;
import java.util.List;

/**
 * Created by csc on 29.09.2015.
 */
public class Histogram {

    public static int[] computeHistogram( List<Long> valueList, int numBars ) {

        if( valueList == null || numBars < 1)
            return null;
        if( valueList.isEmpty() )
            return new int[0];

        Long min = Collections.min(valueList);
        Long max = Collections.max(valueList);

        int[] hist = new int[numBars];

        for( Long value : valueList ) {

            int index = (int)((long)numBars*(value - min)/(1+max-min));
            hist[index] += 1;
        }

        // System.out.println(Arrays.toString(hist));
        return hist;
    }


    public static ChartData makeChartDataWithFixedNumberOfBins(String key, List<Long> valueList, int numBars) {

        if( valueList == null || numBars < 1)
            return null;
        if( valueList.isEmpty() )
            return new ChartData(key);

        Long min = Collections.min(valueList);
        Long max = Collections.max(valueList);

        int[] hist = new int[numBars];

        for( Long value : valueList ) {
            int index = (int)((long)numBars*(value - min)/(1+max-min));
            hist[index] += 1;
        }

        // System.out.println(Arrays.toString(hist));

        ChartData cd = new ChartData(key);
        int i=0;
        for( int hValue : hist) {
            int low = (int) Math.ceil ((0.000000+i)*(1+max-min)/numBars);
            int top = (int) Math.floor((0.999999+i)*(1+max-min)/numBars);
            if (low<=top) {  // safety check, do not remove!
                String label = low + "-" + top;
                cd.addItem(new ChartDataItem(label, hValue, "#f09040"));
            }
            i++;
        }

        return cd;
    }


    public static ChartData makeChartDataWithFixedSizeOfBins(String key, List<Long> valueList, int binSize) {
        return makeChartDataWithFixedSizeOfBins(key, valueList, new Long(binSize));
    }

    public static ChartData makeChartDataWithFixedSizeOfBins(String key, List<Long> valueList, Long binSize) {
        return makeChartDataWithFixedSizeOfBins(key, valueList, binSize, false);
    }

    public static ChartData makeChartDataWithFixedSizeOfBins(String key, List<Long> valueList, Long binSize,
                                                             boolean percentages) {

        if( valueList == null || binSize < 1)
            return null;
        if( valueList.isEmpty() )
            return new ChartData(key);

        Long min = Collections.min(valueList);
        if (min < 0)
            throw new NotImplementedYetException("makeChartDataWithFixedSizeOfBins(...) does not support negative values (yet)!");
        min = 0L; // start first bin with 0 as the left border, even if 0 is not in valueList
        Long max = Collections.max(valueList);
        // round UP maximum to multiple of binSize
        int numBars = (int)((1+max-min)/binSize);
        if ((1+max-min) % binSize != 0) { numBars += 1; }
        max = numBars * binSize;

        int[] hist = new int[numBars];

        for( Long value : valueList ) {
            int index = (int)((long)(value-min)/binSize);
            hist[index] += 1;
        }

        // System.out.println(Arrays.toString(hist));

        ChartData cd = new ChartData(key);
        int i=0;
        for(int hValue : hist) {
            long low = i*binSize;
            long top = (i+1)*binSize-1;
            String label = low + "-" + top;
            if (percentages == false) {
                cd.addItem(new ChartDataItem(label, hValue, "#f09040"));
            } else {
                cd.addItem(new ChartDataItem(label, new Double(hValue)*100.0/valueList.size(), "#f09040"));
            }
            i++;
        }

        return cd;
    }


    public static ChartData makeSingleColumnChart(String name, Integer value) {
        if (value == null) return null;
        if (name == null)  name = "";

        ChartData cd = new ChartData(name);
        cd.addItem(new ChartDataItem(name, value, Color.BLUE.toString()));

        return cd;
    }


    public static ChartData makeDoubleColumnChart(String name1, String name2, Integer value1, Integer value2) {
        if (value1 == null || value2 == null) return null;
        if (name1 == null)  name1 = "";
        if (name2 == null)  name2 = "";

        ChartData cd = new ChartData(name1+"/"+name2);
        cd.addItem(new ChartDataItem(name1, value1, Color.BLUE.toString()));
        cd.addItem(new ChartDataItem(name2, value2, Color.BLUE.toString()));

        return cd;
    }


    public static ChartData makeChartForAccidentCounters(String key, List<Long> valueList, boolean severe) {

        if( valueList == null)
            return null;
        if( valueList.isEmpty() )
            return new ChartData(key);

        if (Collections.min(valueList) < 0)
            throw new NotImplementedYetException("makeChartForAccidentsCounters(...) does not support negative values!");
        Long max = Collections.max(valueList);

        int[] hist = new int[6];
        for( Long value : valueList ) {
            if (value == 0) continue; // do not count occurrence of zero values!
            int index = (int)((long)(value-1)/2);
            hist[Math.min(index,5)] += 1;
        }

        // System.out.println(Arrays.toString(hist));

        ChartData cd = new ChartData(key);
        for (int i=0; i<6; i++) {
            String label = (i<5) ? ((2*i+1) + "-" + (2*i+2)) : ">10";
            Color color = Color.LookupColor(2*i+1, (severe)?Color.FOKUS_COLOR_SCHEME_SEVERE:Color.FOKUS_COLOR_SCHEME);
            cd.addItem(new ChartDataItem(label, hist[i], color.toString()));
        }
/*      the above loop is equivalent to:
        cd.addItem( new ChartDataItem("1-2", hist[0], new VmzData.Color().colorize(1).toString()));
        cd.addItem( new ChartDataItem("3-4", hist[1], new VmzData.Color().colorize(3).toString()));
        cd.addItem( new ChartDataItem("5-6", hist[2], new VmzData.Color().colorize(4).toString()));
        cd.addItem( new ChartDataItem("7-8", hist[3], new VmzData.Color().colorize(7).toString()));
        cd.addItem( new ChartDataItem("9-10", hist[4],new VmzData.Color().colorize(9).toString()));
        cd.addItem( new ChartDataItem(">10", hist[5], new VmzData.Color().colorize(11).toString()));
*/
        return cd;
    }


}
