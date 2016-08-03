package cc.sinex.qiku.service;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cc.sinex.qiku.R;
import cc.sinex.qiku.activity.MainActivity;
import cc.sinex.qiku.db.RideDataServcie;
import cc.sinex.qiku.db.SportDataServcie;
import cc.sinex.qiku.entity.Locate;
import cc.sinex.qiku.entity.SportData;
import cc.sinex.qiku.event.MyEvent;

public class BackService extends Service{

    MyEvent myEvent = new MyEvent();
    private EventBus eventBus;
    @Subscribe
    public void onEvent(MyEvent eventData) {
        module = eventData.getModule();
    }

    PowerManager.WakeLock wakeLock = null;
    private final static String TAG = BackService.class.getSimpleName();
    private final static int FOREGROUND_ID = 1000;



    private Date startTime;
    private Date endTime;
    String module;//运动模式


    private boolean firstloc = true;//第一次定位成功
    private float speed = 0;//GPS返回的速度信息
    private float avspeed = 0;//平均速度
    private float distance = 0;//运动距离,米
    private float usetime = 0;//用时

    private float thisdistance = 0;//此次距离
    private LatLng latLng;//一次定位返回的经纬度
    List<LatLng> list = new ArrayList<LatLng>();//保存的经纬度list

    private Locate locate;//定位数据
    List<Locate> locates = new ArrayList<Locate>();//保存Locate
    private boolean quit;
    RideDataServcie rideDataServcie;
    SportDataServcie sportDataServcie;



    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock()
    {
        if (null == wakeLock)
        {

            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock)
            {
                wakeLock.acquire();
            }
        }
    }

    //释放设备电源锁
    private void releaseWakeLock()
    {
        if (null != wakeLock)
        {
            wakeLock.release();
            wakeLock = null;
        }
    }


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {

                    //定位成功回调信息，设置相关消息
                    speed = amapLocation.getSpeed();//获得速度speed，float
                    //本次经纬度信息
                    latLng = new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude());




                    if (firstloc) {//第一次定位成功
                        distance = 0;
                        firstloc = false;
                        list.add(latLng);

                        //启动一条线程，用来计时
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (!quit){
                                    usetime = (float) (usetime + 0.5);
                                }

                            }
                        }, 500, 500);


                    }else{
                        //计算与上次经纬度的距离，单位米
                        thisdistance = AMapUtils.calculateLineDistance(list.get(list.size() - 1), latLng);


                        distance = distance +thisdistance;//路程计算
                        if (usetime > 0){//平均速度计算
                            avspeed = distance/usetime;
                        }
                        //保存经纬度信息到list
                        list.add(latLng);



                    }


                    locate = new Locate(amapLocation.getLatitude(),amapLocation.getLongitude(),speed);
                    locates.add(locate);

                    myEvent.setModule(module);
                    myEvent.setAvspeed(avspeed);
                    myEvent.setList(list);
                    myEvent.setDistance(distance);
                    myEvent.setSpeed(speed);
                    eventBus.post(myEvent);



                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //把服务设置为前台服务
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "WhiteService->onStartCommand");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ico);
        builder.setContentTitle("骑酷");
        builder.setContentText("骑酷正在记录您的运动数据");
        //builder.setContentInfo("骑酷正在记录您的运动数据");
        builder.setWhen(System.currentTimeMillis());
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(FOREGROUND_ID, notification);
        return super.onStartCommand(intent, flags, startId);

    }



    //Service被断开时候调用该方法
    @Override
    public boolean onUnbind(Intent intent) {

        return true;
    }
    //Service被关闭时候调用该方法
    @Override
    public void onDestroy() {
        endTime = new Date();
        if(!firstloc){//定位成功过
            rideDataServcie.saveObject(locates);//保存轨迹数据
            SportData sportData = new SportData(startTime,endTime,usetime,avspeed,distance,module);//运动数据
            sportDataServcie.saveObject(sportData);//保存运动数据
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        }

        super.onDestroy();
        this.quit = true;
        mLocationClient.onDestroy();//销毁定位客户端。
        stopForeground(true);

        //反注册EventBus
        eventBus.unregister(this);
        releaseWakeLock();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakeLock();
        //注册EventBus
        eventBus = EventBus.getDefault();
        eventBus.register(this);


        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

        rideDataServcie = new RideDataServcie(this);
        sportDataServcie = new SportDataServcie(this);

        startTime = new Date();
    }
}

