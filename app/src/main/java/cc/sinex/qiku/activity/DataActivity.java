package cc.sinex.qiku.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import cc.sinex.qiku.R;
import cc.sinex.qiku.db.RideDataServcie;
import cc.sinex.qiku.db.SportDataServcie;
import cc.sinex.qiku.entity.SportData;
import cc.sinex.qiku.event.RefreshEvent;
import cc.sinex.qiku.utils.Tools;

public class DataActivity extends Activity {

    private EventBus eventBus;
    @Subscribe
    public void onEvent(RefreshEvent eventData) {

    }

    private ArrayList<SportData> sportDatas;
    private SportDataServcie sportDataServcie;
    private RideDataServcie rideDataServcie;

    private Map<Integer,Integer> dataMap = new HashMap<Integer,Integer>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        setTitle("历史运动数据");
        eventBus = EventBus.getDefault();
        eventBus.register(this);

        rideDataServcie = new RideDataServcie(this);
        sportDataServcie = new SportDataServcie(this);
        //取得所有历史运动数据
        sportDatas = sportDataServcie.getObject();


        List<Map<String,Object>> listItems =
                new ArrayList<Map<String,Object>>();

        for (int i = 0; i< sportDatas.size(); i++){
            dataMap.put(i,sportDatas.get(i).getNum());

            Map<String,Object> listItem = new HashMap<String,Object>();

            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ft.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            String start = ft.format(sportDatas.get(i).getStartTime());
            String end = ft.format(sportDatas.get(i).getEndTime());




            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            String useTime = formatter.format((long)(sportDatas.get(i).getUseTime()*1000));

            listItem.put("avSpeed", String.format("%.2f", sportDatas.get(i).getAvSpeed() * 3.6));
            listItem.put("distance", String.format("%.2f", sportDatas.get(i).getDistance() / 1000));
            listItem.put("module", sportDatas.get(i).getModule());
            listItem.put("startTime",start);
            listItem.put("endTime",end);
            listItem.put("useTime",useTime);

            listItems.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,listItems,R.layout.simple_item,
                new String[]{"avSpeed","distance","module","startTime","endTime","useTime"},
                new int[]{R.id.avSpeed,R.id.distance,R.id.module,R.id.startTime,R.id.endTime,R.id.useTime});



        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {


                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(Tools.dip2px(getApplicationContext(),90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);

        listView.setMenuCreator(creator);
        listView.setAdapter(simpleAdapter);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //delete action
                int num = dataMap.get(position);
                sportDataServcie.deletData(num);
                rideDataServcie.deletData(num);
                eventBus.post(new RefreshEvent());

                // false : close the menu; true : not close the menu
                //自己调到自己的activity
                Intent intent = new Intent(DataActivity.this, DataActivity.class);
                startActivity(intent);
                //close this activity
                finish();
                return false;
            }
        });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //这里跳转到地图显示页面。


                Intent intent = new Intent(DataActivity.this, ShowDataActivity.class);
                Bundle data = new Bundle();
                int num = dataMap.get(position);
                data.putSerializable("position", num);//传递数据位置
                intent.putExtras(data);
                startActivity(intent);
            }
        });



        //显示总的运动时间与距离
        float sum_time=0;
        float sum_distance=0;
        for (int i = 0; i< sportDatas.size(); i++){
            sum_distance += sportDatas.get(i).getDistance();
            sum_time += sportDatas.get(i).getUseTime();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        String all_useTime = formatter.format((long)(sum_time*1000));
        String all_distance = String.format("%.2f", sum_distance / 1000);
        TextView sum = (TextView) findViewById(R.id.sum);
        sum.setText("总路程："+all_distance+" km  总耗时："+all_useTime);




    }







    @Override
    protected void onDestroy() {

        super.onDestroy();
        eventBus.unregister(this);

    }
}
