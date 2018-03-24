package com.soaic.calendarselector;

import java.util.Date;

/**
 * 日历实体bean
 * Created by DDY-XS on 2017/12/20.
 */
public class CalendarBean {
    private Date calendarDate;  //时间
    private int calendarStatus;  //状态
    private String displayTxt;   //显示文本
    private int viewType;   //显示类型
    private boolean isToday; //是否今天

    public Date getCalendarDate() {
        return calendarDate;
    }

    public void setCalendarDate(Date calendarDate) {
        this.calendarDate = calendarDate;
    }

    public String getDisplayTxt() {
        return displayTxt;
    }

    public void setDisplayTxt(String displayTxt) {
        this.displayTxt = displayTxt;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public int getCalendarStatus() {
        return calendarStatus;
    }

    public void setCalendarStatus(int calendarStatus) {
        this.calendarStatus = calendarStatus;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setToday(boolean today) {
        isToday = today;
    }
}
