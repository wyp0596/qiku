package cc.sinex.qiku.entity;

import java.io.Serializable;

/**
 * Created by sinex on 16/4/14.
 * 用来存储经纬度、速度数据
 */
public class Locate implements Serializable{

    private static final long serialVersionUID = 1L;

    private double Latitude;
    private double Longitude;
    private float speed;
    public Locate(double Latitude,double Longitude,float speed){
        super();
        this.Latitude = Latitude;
        this.Longitude = Longitude;
        this.speed = speed;
    }



    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
