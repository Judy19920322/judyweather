package com.song.judyweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Judy on 2016/12/22.
 */

public class County extends DataSupport {
    public int id;
    public String countyName;
    public String weatherId;
    public int cityId;
}
