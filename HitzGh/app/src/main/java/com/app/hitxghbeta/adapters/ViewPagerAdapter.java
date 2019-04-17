package com.app.hitxghbeta.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.app.hitxghbeta.fragments.VerticalListFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<String> mFragmentTitleList = new ArrayList<>();
    private final List<Integer> mFragmentIds = new ArrayList<>();
    private Context context;

    public ViewPagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(mFragmentIds.get(position)==0)
            return  new VerticalListFragment().newInstance(null,null,1);
        else
            return  new VerticalListFragment().newInstance(mFragmentIds.get(position)+"",null,1);
        //return null;
    }

    @Override
    public int getCount() {
        return mFragmentTitleList.size();
    }

    public void addFragment(String title,int id) {
        mFragmentTitleList.add(title);
        mFragmentIds.add(id);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}