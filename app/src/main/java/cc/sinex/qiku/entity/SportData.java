package cc.sinex.qiku.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by sinex on 16/5/7.
 */
public class SportData implements Serializable {
    private static final long serialVersionUID = 13L;

    private Date startTime;//开始时间
    private Date endTime;//结束时间
    private float useTime;//用时
    private float avSpeed;//平均速度
    private float distance;//运动距离,米
    private String module;//运动模式 

    private int num;//序号

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public SportData(Date startTime, Date endTime, float useTime, float avSpeed, float distance,String module) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.useTime = useTime;
        this.avSpeed = avSpeed;
        this.distance = distance;
        this.module = module;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public float getUseTime() {
        return useTime;
    }

    public void setUseTime(float useTime) {
        this.useTime = useTime;
    }

    public float getAvSpeed() {
        return avSpeed;
    }

    public void setAvSpeed(float avSpeed) {
        this.avSpeed = avSpeed;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
