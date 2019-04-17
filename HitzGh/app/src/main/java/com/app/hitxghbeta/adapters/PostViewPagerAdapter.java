package com.app.hitxghbeta.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.app.hitxghbeta.PostFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anubhav on 21/02/18.
 */

public class PostViewPagerAdapter extends FragmentPagerAdapter {

    private List<Integer> postIds = new ArrayList<>();
    private Context context;

    public PostViewPagerAdapter(FragmentManager manager,Context context,List<Integer> list) {
        super(manager);
        this.context = context;
        this.postIds = list;
    }

    @Override
    public Fragment getItem(int position) {
        return new PostFragment().newInstance(postIds.get(position));
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return postIds.size();
    }
}
