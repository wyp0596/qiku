package cc.sinex.qiku.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.lhh.apst.library.AdvancedPagerSlidingTabStrip;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cc.sinex.qiku.R;
import cc.sinex.qiku.event.RefreshEvent;
import cc.sinex.qiku.fragments.BbsFragment;
import cc.sinex.qiku.fragments.HistoryFragment;
import cc.sinex.qiku.fragments.SportFragment;
import cc.sinex.qiku.fragments.UserFragment;
import cc.sinex.qiku.utils.APSTSViewPager;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private EventBus eventBus;
    @Subscribe
    public void onEvent(RefreshEvent eventData) {

    }


    public AdvancedPagerSlidingTabStrip mAPSTS;
    public APSTSViewPager mVP;

    private static final int VIEW_FIRST 		= 0;
    private static final int VIEW_SECOND	    = 1;
    private static final int VIEW_THIRD       = 2;
    private static final int VIEW_FOURTH    = 3;

    private static final int VIEW_SIZE = 4;

    private SportFragment mSportFragment = null;
    private BbsFragment mBbsFragment = null;
    private HistoryFragment mHistoryFragment = null;
    private UserFragment mUserFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        findViews();
        init();


    }

    private void findViews(){
        mAPSTS = (AdvancedPagerSlidingTabStrip)findViewById(R.id.tabs);
        mVP = (APSTSViewPager)findViewById(R.id.vp_main);
    }

    private void init(){
        mVP.setOffscreenPageLimit(VIEW_SIZE);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());

        mVP.setAdapter(new FragmentAdapter(getSupportFragmentManager()));

        adapter.notifyDataSetChanged();
        mAPSTS.setViewPager(mVP);

        mAPSTS.setOnPageChangeListener(this);

        mVP.setCurrentItem(VIEW_FIRST);



        //mAPSTS.showDot(VIEW_FIRST,"99+");
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position){
            case 0://运动界面

                break;
            case 1://社区界面

                break;
            case 2://历史界面，更新历史数据
                eventBus.post(new RefreshEvent());
                break;
            case 3://用户界面

                break;
        }

    }



    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class FragmentAdapter extends FragmentStatePagerAdapter implements AdvancedPagerSlidingTabStrip.IconTabProvider{

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position >= 0 && position < VIEW_SIZE){
                switch (position){
                    case  VIEW_FIRST:
                        if(null == mSportFragment)
                            mSportFragment = SportFragment.instance();
                        return mSportFragment;

                    case VIEW_SECOND:
                        if(null == mBbsFragment)
                            mBbsFragment = BbsFragment.instance();
                        return mBbsFragment;

                    case VIEW_THIRD:
                        if(null == mHistoryFragment)
                            mHistoryFragment = HistoryFragment.instance();
                        return mHistoryFragment;

                    case VIEW_FOURTH:
                        if(null == mUserFragment)
                            mUserFragment = UserFragment.instance();
                        return mUserFragment;
                    default:
                        break;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return VIEW_SIZE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position >= 0 && position < VIEW_SIZE){
                switch (position){
                    case  VIEW_FIRST:
                        return  "运动";
                    case  VIEW_SECOND:
                        return  "社区";
                    case  VIEW_THIRD:
                        return  "历史";
                    case  VIEW_FOURTH:
                        return  "用户";
                    default:
                        break;
                }
            }
            return null;
        }

        @Override
        public Integer getPageIcon(int index) {
            if(index >= 0 && index < VIEW_SIZE){
                switch (index){
                    case  VIEW_FIRST:
                        return  R.mipmap.home_main_icon_n;
                    case VIEW_SECOND:
                        return  R.mipmap.home_categry_icon_n;
                    case VIEW_THIRD:
                        return  R.mipmap.home_live_icon_n;
                    case VIEW_FOURTH:
                        return  R.mipmap.home_mine_icon_n;
                    default:
                        break;
                }
            }
            return 0;
        }

        @Override
        public Integer getPageSelectIcon(int index) {
            if(index >= 0 && index < VIEW_SIZE){
                switch (index){
                    case  VIEW_FIRST:
                        return  R.mipmap.home_main_icon_f_n;
                    case VIEW_SECOND:
                        return  R.mipmap.home_categry_icon_f_n;
                    case VIEW_THIRD:
                        return  R.mipmap.home_live_icon_f_n;
                    case VIEW_FOURTH:
                        return  R.mipmap.home_mine_icon_f_n;
                    default:
                        break;
                }
            }
            return 0;
        }

        @Override
        public Rect getPageIconBounds(int position) {
            return null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if (keyCode==KeyEvent.KEYCODE_BACK) {
            //back key Constant Value: 4 (0x00000004)
            //创建退出对话框
            AlertDialog.Builder isExit=new AlertDialog.Builder(this);
            //设置对话框标题
            isExit.setTitle("退出可能使运动数据记录中断");
            //设置对话框消息
            isExit.setMessage("确定要退出吗?");
            // 添加选择按钮并注册监听
//			 isExit.setPositiveButton("确定",diaListener);
            //设置的另一种形式
            isExit.setPositiveButton("退出", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    MainActivity.this.finish();
                }
            });
            isExit.setNegativeButton("后台", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            });
//			 isExit.setNegativeButton("取消",diaListener);
            //对话框显示
            isExit.show();
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
            System.out.println("home");
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(this);
    }
}
