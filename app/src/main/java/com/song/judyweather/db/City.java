package com.song.judyweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Judy on 2016/12/22.
 */

public class City extends DataSupport {
    public int id;
    public String cityName;
    public int cityCode;
    public int provinceId;
}
