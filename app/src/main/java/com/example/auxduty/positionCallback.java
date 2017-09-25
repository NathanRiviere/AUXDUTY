package com.example.auxduty;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;

/**
 * Created by briviere on 9/24/17.
 */

public class positionCallback {
    private Context mContext;
    ListView mList;
    HashMap mMap;
    positionCallback(Context context, ListView lv, HashMap map) {
        mContext = context;
        mList = lv;
        mMap = map;
    }

    public void call(Integer pos, View view, boolean inflated) {
        if (inflated) {
            mMap.put(pos, pos);
        } else {
            mMap.put(pos, mList.getPositionForView(view));
        }
    }
}
