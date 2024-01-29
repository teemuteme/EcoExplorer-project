package fi.tuni.application.classes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherQualityData {
        private int airQuality;
        private int humidity;

        private List<QualityForecastInfo> list;

        public WeatherQualityData(int airQuality, int humidity, List<QualityForecastInfo> list) {
                this.airQuality = airQuality;
                this.humidity = humidity;
                this.list = list;
        }

        public int getAirQuality() {
                return airQuality;
        }

        public int getHumidity() {
                return humidity;
        }

        public List<QualityForecastInfo> getList() { return list; };

        public void setAirQuality(int airQuality) {
                this.airQuality = airQuality;
        }

        public void setHumidity(int humidity) {
                this.humidity = humidity;
        }

        public void setList(List<QualityForecastInfo> list) {
                this.list = list;
        }

        // Class for forecast, but not used because free API doesn't support those
        public static class QualityForecastInfo {
                private long dt;
                private int quality;

                public QualityForecastInfo(long dt, int quality) {
                        this.dt = dt;
                        this.quality = quality;
                }

                public long getDt() {
                        return dt;
                }

                public void setDt(long dt) {
                        this.dt = dt;
                }

                public long getQuality() {
                        return quality;
                }

                public void setQuality(int quality) {
                        this.quality = quality;
                }
        }
}
