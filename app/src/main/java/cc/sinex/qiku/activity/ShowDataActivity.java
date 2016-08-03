package cc.sinex.qiku.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import cc.sinex.qiku.R;
import cc.sinex.qiku.db.RideDataServcie;
import cc.sinex.qiku.entity.Locate;


public class ShowDataActivity extends Activity implements AMap.OnMapLoadedListener {

    List<LatLng> list = new ArrayList<LatLng>();

    private LatLng latLng ;
    private AMap aMap;
    private MapView mapView;
    private UiSettings mUiSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //惯例初始化
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdata);

        //获得mapView
        mapView = (MapView) findViewById(R.id.map_show);
        //初始化
        mapView.onCreate(savedInstanceState);
        init();
        //设置定位监听器
        aMap.setOnMapLoadedListener(this);



        Intent intent = getIntent();
        int position = (int) intent.getSerializableExtra("position");//位置信息


        //获取运动轨迹
        List<Locate> locates = new RideDataServcie(this).getObject(position);


        for(Locate locate : locates){
            latLng = new LatLng(locate.getLatitude(),locate.getLongitude());
            list.add(latLng);
        }



    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            setUpMap();
        }

    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {




        //显示比例尺
        mUiSettings.setScaleControlsEnabled(true);




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

    }

    @Override
    public void onMapLoaded() {
        if (list != null){
            aMap.addPolyline(new PolylineOptions().color(Color.rgb(0, 153, 255)).addAll(list));
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(list.get(0)));
            aMap.addMarker(new MarkerOptions().position(list.get(0))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_start)));
            aMap.addMarker(new MarkerOptions().position(list.get(list.size()-1))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_stop)));

            aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        }

    }


}
