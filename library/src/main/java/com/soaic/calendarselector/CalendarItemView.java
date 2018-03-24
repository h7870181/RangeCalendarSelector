package com.soaic.calendarselector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * 日历itemView
 * Created by DDY-XS on 2017/12/20.
 */
public class CalendarItemView extends View{
    private int width,height;
    private Rect rect;
    private Paint txtPaint, txtTodayPaint;
    private int txtColor = Color.parseColor("#3A3A3A");
    private int txtColorToday = Color.parseColor("#FF8000");
    private String text = "";
    private boolean isYearMonth = false;
    private boolean isToday = false;
    private String txtToday = "今天";
    private String txtIn = "入住";
    private String txtOut = "离开";
    private int selectType = 0;
    private String outDay = "";

    public CalendarItemView(Context context) {
        super(context);
        init();
    }

    public CalendarItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        rect = new Rect();
        txtPaint = new Paint();
        txtPaint.setColor(txtColor);
        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(sp2px(11));
        txtTodayPaint = new Paint();
        txtTodayPaint.setColor(txtColorToday);
        txtTodayPaint.setAntiAlias(true);
        txtTodayPaint.setTextSize(sp2px(11));

    }

    public void setText(String text){
        this.text = text;
        invalidate();
    }

    public void setTextColor(int txtColor){
        this.txtColor = txtColor;
        txtPaint.setColor(txtColor);
        invalidate();
    }

    public void setFlags(int flags){
        txtPaint.setFlags(flags);
        txtPaint.setAntiAlias(true);
    }

    public void setIsYearMonth(boolean bool){
        this.isYearMonth = bool;
    }

    public void setToday(boolean bool){
        this.isToday = bool;
    }

    public void setSelectType(int bool){
        this.selectType = bool;
    }

    public void setOutDay(String outDay){
        this.outDay = outDay;
    }

    public void setTextTag(String txtIn, String txtOut){
        this.txtIn = txtIn;
        this.txtOut = txtOut;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed){
            width = getWidth();
            height = getHeight();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        txtPaint.getTextBounds(text, 0, text.length(), rect);
        int viewX = (width - rect.width())/2;
        int viewY = (height + rect.height())/2;
        int txtInOrOutY = height - rect.height() + dp2px(5);
        txtTodayPaint.getTextBounds(txtToday, 0, txtToday.length(), rect);
        int todayX = (width - rect.width())/2;
        int todayY = rect.height() + dp2px(2);
        txtPaint.getTextBounds(outDay, 0, outDay.length(), rect);
        int outDayX = (width - rect.width())/2;
        int outDayY = rect.height() + dp2px(2);
        if(isToday){
            //今天
            canvas.drawText(txtToday, todayX, todayY, txtTodayPaint);
        }
        if(!TextUtils.isEmpty(outDay)){
            //离开共多少天
            canvas.drawText(outDay, outDayX, outDayY, txtPaint);
        }
        if(selectType>0){
            if(selectType == CalendarSelectorView.STATUS_SELECTED_START){
                //入住
                canvas.drawText(txtIn, todayX, txtInOrOutY, txtPaint);
            }else if(selectType == CalendarSelectorView.STATUS_SELECTED_END){
                //离开
                canvas.drawText(txtOut, todayX, txtInOrOutY, txtPaint);
            }
        }
        canvas.drawText(text,viewX,viewY,txtPaint);
        if(isYearMonth){
            canvas.drawLine(0,height,width,height,txtPaint);
        }
    }


    /**
     * sp转px
     */
    public int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }
    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
