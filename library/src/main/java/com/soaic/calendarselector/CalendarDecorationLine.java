package com.soaic.calendarselector;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 * Created by DDY-XS on 2018/2/2.
 */
public class CalendarDecorationLine extends RecyclerView.ItemDecoration  {
    private int lineColor = Color.parseColor("#CCCCCC");
    private Paint paint;
    public CalendarDecorationLine() {
        paint = new Paint();
        paint = new Paint();
        paint.setColor(lineColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView recyclerView = new RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = 10;
            //c.drawLine(child.getX(),child.getY()+child.getHeight(),child.getX()+child.getWidth(),child.getX()+child.getHeight(),paint);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, 10);
    }

}
