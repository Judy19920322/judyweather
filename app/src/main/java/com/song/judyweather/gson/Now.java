package com.song.judyweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Judy on 2016/12/22.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
