package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;

/**
 * Created by csc on 25.09.2015.
 */
public class ChartOptions {

    public static final String TYPE_BARCHART = "discreteBarChart";
    public static final String TYPE_PIECHART = "pieChart";

    private class ChartOption {

        private String type;
        private Integer height;
        private Integer width;
        private Margin margin;
        private Boolean showValues;
        private Boolean showLabels;
        private Integer transitionDuration;
        private Double labelThreshold;

        public ChartOption() {
            type = new String();
            height = 0;
            width = 0;
            margin = new Margin();
            showValues = true;
            showLabels = true;
            transitionDuration = 10;
            labelThreshold = 0.01;
        }

        private class Axis {
            public String axisLabel;
            public Integer axisLabelDistance;
            public Integer rotateLabels;

            public Axis() {
                axisLabel = new String();
                axisLabelDistance = 0;
                rotateLabels = 0;
            }

            public Axis(String axisLabel, Integer axisLabelDistance) {
                this(axisLabel, axisLabelDistance, 0);
            }

            public Axis(String axisLabel, Integer axisLabelDistance, Integer rotate) {
                this.axisLabel = axisLabel;
                this.axisLabelDistance = axisLabelDistance;
                this.rotateLabels = rotate;
            }
        }

        private Axis xAxis = new Axis();
        private Axis yAxis = new Axis();

        public void setxAxis(String label, Integer distance, Integer angle) {
            xAxis = new Axis(label, distance, angle);
        }

        public void setxAxis(String label, Integer distance) {
            setxAxis(label, distance, 0);
        }

        public void setyAxis(String label, Integer distance, Integer angle) {
            yAxis = new Axis(label, distance, angle);
        }

        public void setyAxis(String label, Integer distance) {
            setyAxis(label, distance, 0);
        }

        public ChartOption(String type, Integer height, Integer width, Boolean showValues, Boolean showLabels, Integer transitionDuration, Double labelThreshold) {
            this.type = type;
            this.height = height;
            this.width = width;
            this.showValues = showValues;
            this.showLabels = showLabels;
            this.transitionDuration = transitionDuration;
            this.labelThreshold = labelThreshold;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Margin getMargin() {
            return margin;
        }

        public void setMargin(Margin margin) {
            this.margin = margin;
        }

        public Boolean getShowValues() {
            return showValues;
        }

        public void setShowValues(Boolean showValues) {
            this.showValues = showValues;
        }

        public Boolean getShowLabels() {
            return showLabels;
        }

        public void setShowLabels(Boolean showLabels) {
            this.showLabels = showLabels;
        }

        public Integer getTransitionDuration() {
            return transitionDuration;
        }

        public void setTransitionDuration(Integer transitionDuration) {
            this.transitionDuration = transitionDuration;
        }

        public Double getLabelThreshold() {
            return labelThreshold;
        }

        public void setLabelThreshold(Double labelThreshold) {
            this.labelThreshold = labelThreshold;
        }

        public Axis getxAxis() {
            return xAxis;
        }

        public void setxAxis(Axis xAxis) {this.xAxis = xAxis;}

        public Axis getyAxis() {
            return yAxis;
        }

        public void setyAxis(Axis yAxis) {
            this.yAxis = yAxis;
        }
    }

    private ChartOption chart;

    public ChartOption getChart() {return chart;}

    public void setChart(ChartOption chart) {this.chart = chart;}

    public ChartOptions() {
        this.chart = new ChartOption();
    }

    public ChartOptions(String type, Integer height, Integer width, Boolean showValues, Boolean showLabels, Integer transitionDuration, Double labelThreshold) {
        this.chart = new ChartOption(type, height, width, showValues, showLabels, transitionDuration, labelThreshold);
    }

    public void setMargin(Margin margin) {chart.setMargin(margin);}

    public void setxAxis(String xLabel, int i) {chart.setxAxis(xLabel, i);}

    public void setxAxis(String xLabel, int i, int j) {chart.setxAxis(xLabel, i, j);}

    public void setyAxis(String yLabel, int i) {chart.setyAxis(yLabel, i);}

    public void setyAxis(String yLabel, int i, int j) {chart.setyAxis(yLabel, i, j);}

    public String toString() {return Util.toJsonString(this);}

}
