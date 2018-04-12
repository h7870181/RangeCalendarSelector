package com.soaic.calendarselector.sticky;

import android.view.View;

/**
 * Created by chenpengfei88 on 2018/4/12.
 */

public class ExampleStickyView implements StickyView {

    @Override
    public boolean isStickyView(View view) {
        return (Boolean) view.getTag();
    }

    @Override
    public int getStickViewType() {
        return 11;
    }
}
