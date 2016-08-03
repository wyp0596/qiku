package cc.sinex.qiku.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuItem;
import com.shehabic.droppy.DroppyMenuPopup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import cc.sinex.qiku.R;
import cc.sinex.qiku.activity.MapActivity;
import cc.sinex.qiku.event.MyEvent;
import cc.sinex.qiku.service.BackService;


public class SportFragment extends Fragment {

    FragmentActivity mainActivity;
    private EventBus eventBus;
    private MyEvent myEvent = new MyEvent();

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private String module = "跑步模式";

    private TextView title ;
    private static boolean sport_status = false;

    private Chronometer chronometer;//定时器
    private Button start_bn,map_bn;//开始按钮，地图按钮
    private TextView speed,licheng,junsu;//速度，里程，均速
    private boolean quit = true;//退出标记




    public static SportFragment instance() {
        SportFragment view = new SportFragment();
		return view;
	}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sport_fragment, null);

        //注册EventBus
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        //获得MainActivity
        mainActivity = getActivity();


        //


        sharedPreferences = mainActivity.getSharedPreferences("qiku", mainActivity.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        title = (TextView) view.findViewById(R.id.title);

        //创建启动Service的Internet
        final Intent intent = new Intent(mainActivity,BackService.class);

        //获得各种资源

        start_bn = (Button) view.findViewById(R.id.bt_start);//开始和结束按钮
        map_bn = (Button) view.findViewById(R.id.bt_map);//地图按钮
        chronometer = (Chronometer) view.findViewById(R.id.time);//定时器
        speed = (TextView) view.findViewById(R.id.speed);
        junsu = (TextView) view.findViewById(R.id.junsu);
        licheng = (TextView) view.findViewById(R.id.licheng);

        //绑定按钮监听器
        start_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sport_status = !sport_status;
                if (start_bn.getText().equals("开始运动")) {
                    start_bn.setText(R.string.stop);
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    mainActivity.startService(intent);

                    quit = false;
                    start_bn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_selected));



                } else {
                    start_bn.setText(R.string.start);
                    chronometer.stop();


                    mainActivity.stopService(intent);
                    quit = true;
                    start_bn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_sport));


                }
            }
        });


        //显示地图
        map_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, MapActivity.class);
                if (sport_status){//运动状态的话要携带描线数据
                    Bundle data = new Bundle();
                    data.putSerializable("list", (Serializable) myEvent.getList());
                    intent.putExtras(data);
                }

                startActivity(intent);
            }
        });


        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.what == 0x1122)
                {
                    speed.setText("速度\n"+String.format("%.2f",myEvent.getSpeed()*3.6));
                    if (chronometer.getDrawingTime() > 5000){
                        junsu.setText("均速\n"+String.format("%.2f",myEvent.getAvspeed()*3.6));
                    }

                    licheng.setText("里程\n" + String.format("%.2f", myEvent.getDistance() / 1000));
                    myEvent.setModule(module);
                    eventBus.post(myEvent);
                }
            }
        };

        //启动一条线程，动态地获取速度
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Message m = new Message();
                m.what = 0x1122;
                if (!quit) {//当启动
                    handler.sendMessage(m);
                }

            }
        }, 0, 1000);



        final ImageButton anchor = (ImageButton) view.findViewById(R.id.imgbt);
        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(mainActivity, anchor);


        // Add Item with icon
        droppyBuilder.addMenuItem(new DroppyMenuItem("骑行", R.drawable.riding)).addSeparator()
                .addMenuItem(new DroppyMenuItem("跑步", R.drawable.running)).addSeparator()
                .addMenuItem(new DroppyMenuItem("步行", R.drawable.walking));

        // Add custom views
//        DroppyMenuCustomView sBarItem = new DroppyMenuCustomView(R.layout.slider);
//        droppyBuilder.addMenuItem(sBarItem);

        // Set Callback handler
        droppyBuilder.setOnClick(new DroppyClickCallbackInterface() {
            @Override
            public void call(View v, int id) {
                Log.d("Clicked on ", String.valueOf(id));
                switch (id){
                    case 0://骑行模式
                        anchor.setImageResource(R.drawable.riding);
                        title.setText("骑行模式");
                        module = "骑行模式";
                        editor.putInt("module",id);
                        editor.commit();
                        break;
                    case 1://跑步模式
                        anchor.setImageResource(R.drawable.running);
                        title.setText("跑步模式");
                        module = "跑步模式";
                        editor.putInt("module",id);
                        editor.commit();
                        break;
                    case 2://步行模式
                        anchor.setImageResource(R.drawable.walking);
                        title.setText("步行模式");
                        module = "步行模式";
                        editor.putInt("module",id);
                        editor.commit();
                        break;
                }
                myEvent.setModule(module);
            }
        });

        DroppyMenuPopup droppyMenu = droppyBuilder.build();

        //读取上一次运动模式

        switch (sharedPreferences.getInt("module",0)){
            case 0:
                anchor.setImageResource(R.drawable.riding);
                title.setText("骑行模式");
                module = "骑行模式";
                break;
            case 1:
                anchor.setImageResource(R.drawable.running);
                title.setText("跑步模式");
                module = "跑步模式";
                break;
            case 2:
                anchor.setImageResource(R.drawable.walking);
                title.setText("步行模式");
                module = "步行模式";
                break;
        }



        //
        myEvent.setModule(module);



        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("SportFragment    onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("SportFragment    onStart");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.quit = true;

        //反注册EventBus
        eventBus.unregister(this);

    }

    @Subscribe
    public void onEvent(MyEvent eventData) {
        myEvent = eventData;
    }


}
