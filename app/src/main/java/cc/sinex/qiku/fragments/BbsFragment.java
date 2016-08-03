package cc.sinex.qiku.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import cc.sinex.qiku.R;


public class BbsFragment extends Fragment {






    public static BbsFragment instance() {
        BbsFragment view = new BbsFragment();
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lab_fragment, null);


        System.out.println("BbsFragment    onCreateView");



        return view;
    }



}