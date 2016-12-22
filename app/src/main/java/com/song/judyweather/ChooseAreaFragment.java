package com.song.judyweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.song.judyweather.db.City;
import com.song.judyweather.db.County;
import com.song.judyweather.db.Province;
import com.song.judyweather.utils.HttpUtil;
import com.song.judyweather.utils.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Judy on 2016/12/22.
 */

public class ChooseAreaFragment extends Fragment {
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;
    private int currentLevel;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> mAdapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList = new ArrayList<>();
    private List<City> cityList = new ArrayList<>();
    private List<County> countyList = new ArrayList<>();
    private Province selectProvince;
    private City selectCity;
    private ProgressDialog mProgressDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        mAdapter =  new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel == LEVEL_PROVINCE) {
                    selectProvince = provinceList.get(i);
                    queryCities();
                } else if(currentLevel == LEVEL_CITY) {
                    selectCity = cityList.get(i);
                    queryCounties();
                } else if(currentLevel == LEVEL_COUNTY) {
                    String weatherId = countyList.get(i).weatherId;
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if(currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.provinceName);
            }
            mAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    private void queryCities() {
        titleText.setText(selectProvince.provinceName);
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid=?",String.valueOf(selectProvince.id)).find(City.class);
        if(cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.cityName);
            }
            mAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectProvince.provinceCode;
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    private void queryCounties() {
        titleText.setText(selectCity.cityName);
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?",String.valueOf(selectCity.id)).find(County.class);
        if(countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.countyName);
            }
            mAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectProvince.provinceCode;
            int cityCode = selectCity.cityCode;
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectProvince.id);
                } else if("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectCity.id);
                }
                if(result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)) {
                                queryProvinces();
                            } else if("city".equals(type)) {
                                queryCities();
                            } else if("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showProgressDialog() {
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

}
