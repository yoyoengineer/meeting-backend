package com.meeting.binary.android.binarymeeting.contact;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.meeting.binary.android.binarymeeting.R;
import com.meeting.binary.android.binarymeeting.model.Event;
import com.meeting.binary.android.binarymeeting.overlayutil.PoiOverlay;
import com.meeting.binary.android.binarymeeting.utils.DateUtils;
import com.meeting.binary.android.binarymeeting.utils.UtilsClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by meldi on 3/24/2018.
 */

public class InfoEventFragment extends Fragment implements OnGetPoiSearchResultListener, OnGetGeoCoderResultListener, OnGetSuggestionResultListener, BaiduMap.OnMapLongClickListener, BaiduMap.OnMapClickListener{

    private Event mEvent;

    private static final String INFO_MESSAGE = "com.meeting.binary.android.binarymeeting.infos message";

    public static InfoEventFragment newInstance(Serializable event) {
        Bundle args = new Bundle();
        args.putSerializable(INFO_MESSAGE, event);
        InfoEventFragment fragment = new InfoEventFragment();
        fragment.setArguments(args);
        return fragment;
    }



    //    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    //    private BaiduMap mBaiduMap = null;
    private List<String> suggest;
    private CardView mapcard;
    /**
     * 搜索关键字输入窗口
     */
    private EditText editCity = null;
    //    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    private int loadIndex = 0;

    int searchType = 0;  // 搜索的类型，在显示时区分

    private TextView large_description;
    private TextView time_date;
    private TextView attendees;
    private TextView likes;

    private AutoCompleteTextView keyWorldsView;
    private Button mapSearchSubmitButton;
    public LocationClient mLocationClient;
    private MapView mapView;
    private PoiSearch mPoiSearch;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private String TAG = "MainActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        if (getArguments() != null){
            mEvent = (Event) getArguments().getSerializable(INFO_MESSAGE);
            mLocationClient = new LocationClient(getActivity().getApplicationContext());
            mLocationClient.registerLocationListener(new MyLocationListener());
            SDKInitializer.initialize(getActivity().getApplicationContext());
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_info_event, container, false);


        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(mEvent.getTopic());
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mapcard = view.findViewById(R.id.map_card);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        } else {
            requestLocation();
        }

        mapView = (MapView) view.findViewById(R.id.bmapView);
        large_description = (TextView) view.findViewById(R.id.large_description);
        time_date = (TextView) view.findViewById(R.id.time_event);
        attendees = (TextView) view.findViewById(R.id.attendees);
        likes = (TextView) view.findViewById(R.id.likes);


        large_description.setText(mEvent.getDescription());
        time_date.setText(DateUtils.getReadableModifyDate(mEvent.getTime()));
        attendees.setText(" " + mEvent.getAttendees()+ " ");
        likes.setText(" " + mEvent.getLikes() + " ");


//        mapSearchInput = (EditText) findViewById(R.id.mapSearchInput);
        mapSearchSubmitButton = (Button) view.findViewById(R.id.mapSearchSubmitButton);
        mapSearchSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchButtonProcess(view);
            }
        });

        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setOnMapClickListener(this);

        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        editCity = (EditText) view.findViewById(R.id.city);
        keyWorldsView = (AutoCompleteTextView) view.findViewById(R.id.searchkey);
        sugAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);

        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }else {
                    mapcard.setVisibility(View.VISIBLE);
                }

                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(editCity.getText().toString()));
            }
        });

        return view;
    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            editCity.setText(location.getCity());
//            Toast.makeText(this, "nav to " + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.
                Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        mLocationClient.stop();
        mapView.onDestroy();
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        baiduMap.setMyLocationEnabled(false);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }







    /**
     * 响应城市内搜索按钮点击事件
     *
     * @param v
     */
    private void searchButtonProcess(View v) {
        searchType = 1;
        String citystr = editCity.getText().toString();
        String keystr = keyWorldsView.getText().toString();
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(citystr).keyword(keystr).pageNum(loadIndex));
    }

    public void goToNextPage(View v) {
        loadIndex++;
        searchButtonProcess(null);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getActivity(), "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(getActivity(), "发生未知错误", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            default:
        }
    }


    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
            baiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(baiduMap);
            baiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(poiResult);
            overlay.addToMap();
            overlay.zoomToSpan();

//            switch (searchType) {
//                case 2:
//                    showNearbyArea(center, radius);
//                    break;
//                case 3:
//                    showBound(searchbound);
//                    break;
//                default:
//                    break;
//            }

            return;
        }
        if (poiResult.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : poiResult.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }


    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        if (poiDetailResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            keyWorldsView.setText(poiDetailResult.getAddress());
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }
        suggest = new ArrayList<String>();
        for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
            }
        }
        sugAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, suggest);
        keyWorldsView.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }


    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        latlngToAddress(latLng);
//        Log.d(TAG, "onMapClick: " + latLng.toString());
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
//        Log.d(TAG, "onMapPoiClick: ");
        return false;
    }



    // 百度地图通过坐标获取地址，（ 要签名打包才能得到地址）
    private void latlngToAddress(LatLng latlng) {
        GeoCoder geoCoder = GeoCoder.newInstance();
        // 设置反地理经纬度坐标,请求位置时,需要一个经纬度
        geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latlng));

        // 设置地址或经纬度反编译后的监听,这里有两个回调方法
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    Toast.makeText(getActivity(), "抱歉，找不到该地址!", Toast.LENGTH_SHORT);
                } else {
                    keyWorldsView.setText(result.getAddress());
                }
            }

            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                // 详细地址转换在经纬度
                keyWorldsView.setText(result.getAddress());

            }
        });

    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation location) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (location.getLocType() == BDLocation.TypeGpsLocation
                            || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                        navigateTo(location);
                    }
                }
            });
        }
    }

}
