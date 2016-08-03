package cc.sinex.qiku.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

import java.util.List;

import cc.sinex.qiku.R;

public class MapActivity extends Activity implements AMap.OnMapLoadedListener,LocationSource,
        AMapLocationListener {

    List<LatLng> list ;//运动地图轨迹

    LatLng lastLatLng;//上一记录点
    LatLng newLatLng;//新记录点


    private AMap aMap;
    private MapView mapView;
    private OnLocationChangedListener mListener;//定位监听器
    private AMapLocationClient mlocationClient;//定位
    private AMapLocationClientOption mLocationOption;
    private UiSettings mUiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //惯例初始化
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);



        //获得mapView
        mapView = (MapView) findViewById(R.id.map);
        //初始化
        mapView.onCreate(savedInstanceState);
        init();
        //设置定位监听器
        aMap.setOnMapLoadedListener(this);


        //获得地图轨迹
        Intent intent = getIntent();
        list = (List<LatLng>) intent.getSerializableExtra("list");
        if (list != null && list.size() < 2){
            list = null;
        }
        if (list != null){
            lastLatLng = list.get(list.size()-1);
        }


        //普通视图与卫星视图切换按钮
        ToggleButton tb = (ToggleButton)findViewById(R.id.tb);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                } else {
                    aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                }
            }
        });


    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            //显示比例尺
            mUiSettings.setScaleControlsEnabled(true);

            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }

    }
    //生成最初的轨迹
    @Override
    public void onMapLoaded() {
        if (list != null){

            if (list != null){
                aMap.addPolyline(new PolylineOptions().color(Color.rgb(0, 153, 255)).addAll(list));
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(list.get(0)));
                aMap.addMarker(new MarkerOptions().position(list.get(0))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_start)));
            }else {
                aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(119.217926,26.036532)));
            }
            aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        }

    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {

                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点

                //新的定位点
                newLatLng = new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude());
                //绘制新的轨迹
                if (lastLatLng != null){
                    aMap.addPolyline(new PolylineOptions().color(Color.rgb(0, 153, 255))
                            .add(lastLatLng,newLatLng));
                }
                lastLatLng = newLatLng;//记录为旧记录点


            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);

            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}

