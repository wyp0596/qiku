package cc.sinex.qiku.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.sinex.qiku.R;

/**
 * Created by linhonghong on 2015/8/11.
 */
public class UserFragment extends Fragment {

    public static UserFragment instance() {
        UserFragment view = new UserFragment();
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, null);
        return view;
    }
}