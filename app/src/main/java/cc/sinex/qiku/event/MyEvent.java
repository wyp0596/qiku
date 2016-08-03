package cc.sinex.qiku.event;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sinex on 16/5/24.
 * 保存所有要通讯的数据
 */
public class MyEvent {

    String module;//运动模式
    private float speed = 0;//GPS返回的速度信息（实时）
    private float avspeed = 0;//平均速度
    private float distance = 0;//运动距离,米
    List<LatLng> list = new ArrayList<LatLng>();//保存的经纬度list

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getAvspeed() {
        return avspeed;
    }

    public void setAvspeed(float avspeed) {
        this.avspeed = avspeed;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public List<LatLng> getList() {
        return list;
    }

    public void setList(List<LatLng> list) {
        this.list = list;
    }
}

