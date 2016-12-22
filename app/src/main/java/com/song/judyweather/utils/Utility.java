package com.song.judyweather.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.song.judyweather.db.City;
import com.song.judyweather.db.County;
import com.song.judyweather.db.Province;
import com.song.judyweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Judy on 2016/12/22.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response) {
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.provinceCode = provinceObject.getInt("id");
                    province.provinceName = provinceObject.getString("name");
                    province.save();
                }
                return  true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }

    public static boolean handleCityResponse(String response, int provinceId) {
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.cityCode = cityObject.getInt("id");
                    city.cityName = cityObject.getString("name");
                    city.provinceId = provinceId;
                    city.save();
                }
                return  true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }

    public static boolean handleCountyResponse(String response, int cityId) {
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.weatherId = countyObject.getString("weather_id");
                    county.countyName = countyObject.getString("name");
                    county.cityId = cityId;
                    county.save();
                }
                return  true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }

    public static Weather handleWeatherResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}