package com.song.judyweather.gson;

/**
 * Created by Judy on 2016/12/22.
 */

public class AQI {
    public AQICity city;
    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
