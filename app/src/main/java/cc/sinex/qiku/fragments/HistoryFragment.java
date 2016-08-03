package cc.sinex.qiku.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import cc.sinex.qiku.R;
import cc.sinex.qiku.activity.DataActivity;
import cc.sinex.qiku.db.SportDataServcie;
import cc.sinex.qiku.entity.SportData;
import cc.sinex.qiku.event.RefreshEvent;


public class HistoryFragment extends Fragment {

    private EventBus eventBus;
    private TextView textView;

    @Subscribe
    public void onEvent(RefreshEvent eventData) {
        showData();
    }

    FragmentActivity mainActivity;
    private ArrayList<SportData> sportDatas;
    private SportDataServcie sportDataServcie;


    public static HistoryFragment instance() {
        HistoryFragment view = new HistoryFragment();

        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, null);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        mainActivity = getActivity();

        Button showdata = (Button) view.findViewById(R.id.showdata);
        showdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Intent intent = new Intent(mainActivity, DataActivity.class);
                startActivity(intent);
            }
        });
        textView = (TextView) view.findViewById(R.id.sum_fragment);


        showData();


        return view;
    }

    public void showData(){
        sportDataServcie = new SportDataServcie(mainActivity);
        //取得所有历史运动数据
        sportDatas = sportDataServcie.getObject();
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

        textView.setText("总路程："+all_distance+" km  总耗时："+all_useTime);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }
}