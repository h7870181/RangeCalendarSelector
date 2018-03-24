package com.soaic.calendarselector;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 日历选择器
 * Created by DDY-XS on 2017/12/20.
 */
public class CalendarSelectorView extends RecyclerView {
    private List<CalendarBean> dateList = new ArrayList<>();
    private List<Integer> tempStatusList = new ArrayList<>();
    private List<Date> disDateList = new ArrayList<>();
    private Context mContext;
    private CalendarAdapter calendarAdapter;
    private Calendar calendar;
    private Date curDate;
    private Date minDate,maxDate;
    private int selectMode = 1;  //默认范围选择
    private OnItemClickListener onItemClickListener;
    private int selectStartIndex = -1;
    private int selectEndIndex = -1;
    private int textColorSelect = Color.parseColor("#FFFFFF");
    private int textColorNormal = Color.parseColor("#3A3A3A");
    private int textColorDisable = Color.parseColor("#C3C3C3");
    private int bgColorNormal = Color.parseColor("#FFFFFF");

    public static final int STATUS_EMPTY = -2; //空占位
    public static final int STATUS_DISABLED = -1;//禁用
    public static final int STATUS_NORMAL = 0; //基本
    public static final int STATUS_SELECTED_START = 1; //选择开始
    public static final int STATUS_SELECTED_CENTER = 2; //选择中间
    public static final int STATUS_SELECTED_END = 3; //选择结束
    public static final int SELECT_MODE_RANGE = 1;  //范围选择
    public static final int SELECT_MODE_DISABLE = 2; //禁用选择
    public static final int VIEW_TYPE_TITLE = 1;  //title 年月
    public static final int VIEW_TYPE_CONTENT = 0;  //content 日

    private boolean isSelectDay = false; //是否选择天数
    public void enableSelectDay(){
        this.isSelectDay = true;
    }

    public CalendarSelectorView(Context context) {
        super(context);
        init(context);
    }

    public CalendarSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarSelectorView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        this.mContext = context;
        setBackgroundColor(bgColorNormal);
        calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        curDate = new Date();
        minDate = curDate;
        Calendar temp = Calendar.getInstance();
        temp.setTime(curDate);
        temp.add(Calendar.MONTH,1);
        maxDate = temp.getTime();
        initCalendarDate();
        calendarAdapter = new CalendarAdapter(context,dateList);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
        setLayoutManager(layoutManager);
        setAdapter(calendarAdapter);
        //addItemDecoration(new CalendarDecoration(mContext,dateList,45));
        addItemDecoration(new CalendarDecorationLine());
        closeDefaultAnimator();
    }

    private void initCalendarDate() {
        dateList.clear();
        tempStatusList.clear();
        CalendarBean dateBean;
        calendar.setTime(minDate);
        while (calendar.getTime().before(maxDate)) {
            int curDayMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int maxDayMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            //添加年月
            dateBean = new CalendarBean();
            dateBean.setCalendarDate(calendar.getTime());
            dateBean.setCalendarStatus(STATUS_EMPTY);
            dateBean.setViewType(VIEW_TYPE_TITLE);
            dateBean.setDisplayTxt(getYearMonth(calendar.getTime()));
            dateList.add(dateBean);
            tempStatusList.add(dateBean.getCalendarStatus());

            //添加本周过去日期
            int curDayWeek = calendar.get(Calendar.DAY_OF_WEEK);
            calendar.set(Calendar.DAY_OF_WEEK, 1);
            //星期从索引从一开始，从星期日开始
            for (int j = 1; j < curDayWeek; j++) {
                if(!calendar.getTime().before(maxDate)){
                    break;
                }
                dateBean = new CalendarBean();
                dateBean.setCalendarDate(calendar.getTime());
                dateBean.setCalendarStatus(STATUS_EMPTY);
                dateBean.setDisplayTxt("");
                dateBean.setViewType(VIEW_TYPE_CONTENT);
                dateList.add(dateBean);
                tempStatusList.add(dateBean.getCalendarStatus());
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            //添加本月剩余时间
            for (int j = curDayMonth; j <= maxDayMonth; j++) {
                if(!calendar.getTime().before(maxDate)){
                    break;
                }
                dateBean = new CalendarBean();
                dateBean.setCalendarDate(calendar.getTime());
                dateBean.setCalendarStatus(STATUS_NORMAL);
                dateBean.setDisplayTxt(calendar.get(Calendar.DAY_OF_MONTH) + "");
                dateBean.setViewType(VIEW_TYPE_CONTENT);
                if(isSameDay(calendar.getTime(),curDate)){
                    dateBean.setToday(true);
                }
                dateList.add(dateBean);
                tempStatusList.add(dateBean.getCalendarStatus());
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
    }

    /**
     * 获取选择范围
     * @return list index 1:开始日期 index 2: 结束日期
     */
    public List<Date> getSelectRangeDate(){
        if(selectStartIndex!=-1&&selectEndIndex!=-1){
            List<Date> dates = new ArrayList<>();
            dates.add(dateList.get(selectStartIndex).getCalendarDate());
            dates.add(dateList.get(selectEndIndex).getCalendarDate());
            return dates;
        }
        return null;
    }

    /**
     * 获取选择了多少天
     * @return
     */
    public int getSelectDateNum(){
        List<Date> selectDate = getSelectRangeDate();
        if(selectDate.size()==2) {
            return getDateBetween(selectDate.get(0),selectDate.get(1));
        }else{
            return 0;
        }
    }

    public static int getDateBetween(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 获取选择范围 可以选择一天
     * @return
     */
    public List<Date> getSelectRangeDateV2(){
        List<Date> dates = new ArrayList<>();
        if(selectStartIndex!=-1) {
            dates.add(dateList.get(selectStartIndex).getCalendarDate());
            if(selectEndIndex!=-1){
                dates.add(dateList.get(selectEndIndex).getCalendarDate());
            }
        }
        return dates;
    }

    /**
     * 获取禁用日期集合
     * @return
     */
    public List<Date> getDisDateList(){
        return disDateList;
    }

    /**
     * 获得当前显示的年月
     * @return str 如：2017-12
     */
    public String getYearMonth(Date date) {
        Calendar temp = Calendar.getInstance();
        temp.setTime(date);
        int year = temp.get(Calendar.YEAR);
        int month = temp.get(Calendar.MONTH)+1;
        return String.format(Locale.getDefault(),"%d-%02d",year,month);
    }

    /**
     * 设置选择模式
     * @param selectMode 选择模式 SELECT_MODE_RANGE|SELECT_MODE_DISABLE
     */
    public void setSelectMode(int selectMode){
        this.selectMode = selectMode;
    }

    /**
     * 设置禁用日期
     * @param disDateList 禁用日期集合
     */
    public void setDisableDataArray(List<Date> disDateList){
        this.disDateList = disDateList;
        for (Date date:disDateList) {
            for (int i = 0; i < dateList.size(); i++) {
                CalendarBean bean = dateList.get(i);
                if (isSameDay(date, bean.getCalendarDate())&&STATUS_EMPTY != bean.getCalendarStatus()) {
                    bean.setCalendarStatus(STATUS_DISABLED);
                    dateList.set(i,bean);
                    tempStatusList.set(i,STATUS_DISABLED);
                    break;
                }
            }
        }
        calendarAdapter.notifyDataSetChanged();
    }

    /**
     * 设置选择范围日期
     * @param start
     * @param end
     */
    public void setSelectRangeDate(Date start, Date end){
        for (int i = 0; i < dateList.size(); i++) {
            CalendarBean bean = dateList.get(i);
            if(selectStartIndex >= 0 && selectEndIndex >= 0){
                break;
            }
            if (selectStartIndex<0 && isSameDay(start, bean.getCalendarDate()) && STATUS_EMPTY != bean.getCalendarStatus()) {
                selectStartIndex = i;
            }
            if (selectEndIndex<0 && isSameDay(end, bean.getCalendarDate()) && STATUS_EMPTY != bean.getCalendarStatus()) {
                selectEndIndex = i;
            }
        }
        for(int i = selectStartIndex;i<=selectEndIndex;i++){
            CalendarBean bean = dateList.get(i);
            if(STATUS_EMPTY != tempStatusList.get(i)) {
                if(i == selectStartIndex){
                    bean.setCalendarStatus(STATUS_SELECTED_START);
                }else if(i == selectEndIndex){
                    bean.setCalendarStatus(STATUS_SELECTED_END);
                }else {
                    bean.setCalendarStatus(STATUS_SELECTED_CENTER);
                }
                dateList.set(i, bean);
            }
        }
        calendarAdapter.notifyDataSetChanged();
    }

    /**
     * 设置最大最小范围日期
     * @param date1 最小日期
     * @param date2 最大日期
     */
    public void setMinMaxDate(Date date1, Date date2){
        this.minDate = date1;
        this.maxDate = date2;
        initCalendarDate();
        calendarAdapter.notifyDataSetChanged();
    }

    private class CalendarAdapter extends Adapter<CalendarHolder>{
        private List<CalendarBean> list = new ArrayList<>();
        private Context context;
        private CalendarAdapter(Context context, List<CalendarBean> dateList){
            this.context = context;
            this.list = dateList;
        }

        @Override
        public int getItemViewType(int position) {
            return list.get(position).getViewType();
        }

        @Override
        public CalendarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CalendarHolder(new CalendarItemView(context));
        }

        @Override
        public void onBindViewHolder(CalendarHolder holder, int position) {
            holder.bind(list.get(position),position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class CalendarHolder extends ViewHolder{
        private CalendarItemView view;
        private CalendarHolder(View itemView) {
            super(itemView);
            view = (CalendarItemView) itemView;

        }
        public void bind(final CalendarBean bean, final int position){
            view.setText(bean.getDisplayTxt());
            int viewWidth = getScreenWidth(mContext)/7;
            if(VIEW_TYPE_CONTENT == bean.getViewType()){
                StaggeredGridLayoutManager.LayoutParams layoutParams1 = new StaggeredGridLayoutManager.LayoutParams(viewWidth, dp2px(45));
                //view.setPadding(0, dp2px(2), 0, dp2px(2));
                view.setLayoutParams(layoutParams1);
            }else if(VIEW_TYPE_TITLE == bean.getViewType()){
                StaggeredGridLayoutManager.LayoutParams layoutParams2 = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (viewWidth - 10));
                layoutParams2.setFullSpan(true);
                view.setLayoutParams(layoutParams2);
                view.setIsYearMonth(true);
            }

            view.setFlags(0);
            view.setOutDay("");
            if(STATUS_NORMAL == bean.getCalendarStatus()){
                view.setTextColor(textColorNormal);
                view.setBackgroundColor(bgColorNormal);
            }else if(STATUS_EMPTY == bean.getCalendarStatus()){
                view.setTextColor(textColorNormal);
                view.setBackgroundColor(bgColorNormal);
            }else if(STATUS_DISABLED == bean.getCalendarStatus()){
                view.setTextColor(textColorDisable);
                view.setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                view.setBackgroundColor(bgColorNormal);
            } else if (STATUS_SELECTED_START == bean.getCalendarStatus()) {
                view.setTextColor(textColorSelect);
                if (selectEndIndex == -1) {
                    view.setBackgroundResource(R.drawable.calendar_range_select_left);
                } else {
                    view.setBackgroundResource(R.drawable.calendar_range_select_left);
                }
            } else if (STATUS_SELECTED_CENTER == bean.getCalendarStatus()) {
                view.setTextColor(textColorSelect);
                view.setBackgroundResource(R.drawable.calendar_range_select);
            } else if (STATUS_SELECTED_END == bean.getCalendarStatus()) {
                view.setTextColor(textColorSelect);
                view.setBackgroundResource(R.drawable.calendar_range_select_right);
                if(isSelectDay){
                    view.setOutDay("共"+(getSelectDateNum()+1)+"天");
                }else{
                    view.setOutDay("共"+getSelectDateNum()+"晚");
                }
            }

            if(isSelectDay){
                view.setTextTag("开始","结束");
            }

            //是否是今天
            view.setToday(bean.isToday());
            //选择类型
            view.setSelectType(bean.getCalendarStatus());

            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(STATUS_EMPTY != bean.getCalendarStatus()) {
                        if(selectMode == SELECT_MODE_RANGE && STATUS_DISABLED != bean.getCalendarStatus()){
                            dateItemRangeClick(position);
                        }else if(selectMode == SELECT_MODE_DISABLE){
                            dateItemDisableClick(position);
                        }
                    }
                }
            });

        }
    }

    private void dateItemDisableClick(int position) {
        CalendarBean bean = dateList.get(position);
        if(bean.getCalendarStatus() == STATUS_DISABLED)
            bean.setCalendarStatus(STATUS_NORMAL);
        else
            bean.setCalendarStatus(STATUS_DISABLED);
        dateList.set(position,bean);
        Date tempDate = dateContains(disDateList,bean.getCalendarDate());
        if( tempDate != null)
            disDateList.remove(tempDate);
        else
            disDateList.add(bean.getCalendarDate());
        if(onItemClickListener!=null)
            onItemClickListener.onItemClick(bean.getCalendarDate());
        calendarAdapter.notifyItemChanged(position);
    }

    private void dateItemRangeClick(int position) {
        if(selectStartIndex == -1){
            //第一下点选
            selectStartIndex = position;
            CalendarBean bean = dateList.get(selectStartIndex);
            bean.setCalendarStatus(STATUS_SELECTED_START);
            dateList.set(position, bean);
            calendarAdapter.notifyItemChanged(position);
        }else if(position<selectStartIndex&&selectEndIndex==-1){
            //第二下点选，并且选择的日期在已选开始日期之前
            CalendarBean bean2 = dateList.get(selectStartIndex);
            bean2.setCalendarStatus(STATUS_NORMAL);
            dateList.set(selectStartIndex, bean2);
            calendarAdapter.notifyItemChanged(selectStartIndex);
            selectStartIndex = position;
            CalendarBean bean = dateList.get(selectStartIndex);
            bean.setCalendarStatus(STATUS_SELECTED_START);
            dateList.set(position, bean);
            calendarAdapter.notifyItemChanged(position);
        }else if(selectEndIndex == -1){
            //第二下点选
            if(position == selectStartIndex){
                //选择和已选开始日期一样
                return;
            }
            //选择已选日期之后的一个日期
            selectEndIndex = position;
            for(int i = selectStartIndex+1;i<=selectEndIndex;i++){
                CalendarBean bean = dateList.get(i);
                if(STATUS_EMPTY != tempStatusList.get(i)) {
                    if(i == selectEndIndex){
                        bean.setCalendarStatus(STATUS_SELECTED_END);
                    }else {
                        bean.setCalendarStatus(STATUS_SELECTED_CENTER);
                    }
                    dateList.set(i, bean);
                }
            }
            calendarAdapter.notifyDataSetChanged();
        }else{
            //第三下点选
            if(tempStatusList.get(position) == STATUS_DISABLED){
                //如果之前数据不能点击
                return;
            }
            for(int i = selectStartIndex;i<=selectEndIndex;i++){
                //恢复
                CalendarBean cb = dateList.get(i);
                cb.setCalendarStatus(tempStatusList.get(i));
                dateList.set(i, cb);
            }
            selectStartIndex = position;
            selectEndIndex = -1;

            CalendarBean bean = dateList.get(selectStartIndex);
            bean.setCalendarStatus(STATUS_SELECTED_START);
            dateList.set(selectStartIndex, bean);
            calendarAdapter.notifyDataSetChanged();
        }

        handlerRangeDisableDate();

    }

    /**
     * 处理范围选择不可选日期
     */
    private void handlerRangeDisableDate() {
        Date date1,date2;
        if(selectEndIndex==-1){
            date1 = getMinData(dateList.get(selectStartIndex).getCalendarDate());
            date2 = getMaxData(dateList.get(selectStartIndex).getCalendarDate());
        }else{
            date1 = null;
            date2 = null;
        }

        for(int i = 0;i< dateList.size();i++){
            CalendarBean bean = dateList.get(i);
            if(bean.getCalendarStatus()!=STATUS_SELECTED_START&&bean.getCalendarStatus()!=STATUS_SELECTED_CENTER&&
                    bean.getCalendarStatus()!=STATUS_SELECTED_END&&bean.getCalendarStatus()!=STATUS_EMPTY) {
                if (date1 != null && date2 != null) {
                    if (bean.getCalendarDate().compareTo(date1)>0 && bean.getCalendarDate().compareTo(date2)<0) {
                        bean.setCalendarStatus(STATUS_NORMAL);
                    } else {
                        bean.setCalendarStatus(STATUS_DISABLED);
                    }
                } else {
                    bean.setCalendarStatus(tempStatusList.get(i));
                }
                dateList.set(i, bean);
            }
        }

        calendarAdapter.notifyDataSetChanged();
    }

    public boolean isSameDay(Date date1, Date date2) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(date1);
        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(date2);
        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB
                .get(Calendar.DAY_OF_MONTH);
    }

    private Date dateContains(List<Date> listDate, Date date){
        if(listDate == null){
            return null;
        }
        for(Date d: listDate){
            if(isSameDay(d,date)){
                return d;
            }
        }
        return null;
    }

    private int getScreenWidth(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        if(windowManager!=null){
            windowManager.getDefaultDisplay().getSize(size);
        }
        return size.x;
    }

    public void closeDefaultAnimator() {
        this.getItemAnimator().setAddDuration(0);
        this.getItemAnimator().setChangeDuration(0);
        this.getItemAnimator().setMoveDuration(0);
        this.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) this.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private Date getMinData(Date date) {
        Collections.sort(disDateList, new Comparator<Date>() {
            @Override
            public int compare(Date d1, Date d2) {
                //从大到小排序
                return d2.compareTo(d1);
            }
        });
        Date tempDate = minDate;
        for (Date d : disDateList) {
            if (d.after(minDate) && date.after(d)) {
                tempDate =  d;
                break;
            }
        }
        Calendar temp = Calendar.getInstance();
        temp.setTime(tempDate);
        temp.add(Calendar.DAY_OF_YEAR,-1);
        return temp.getTime();
    }

    private Date getMaxData(Date date) {
        Collections.sort(disDateList, new Comparator<Date>() {
            @Override
            public int compare(Date s, Date t1) {
                //从小到大排序
                return s.compareTo(t1);
            }
        });
        Date tempDate = maxDate;
        for (Date d : disDateList) {
            if (d.before(maxDate) && date.before(d)) {
                tempDate =  d;
                break;
            }
        }
        Calendar temp = Calendar.getInstance();
        temp.setTime(tempDate);
        temp.add(Calendar.DAY_OF_YEAR,1);
        return temp.getTime();
    }

    public interface OnItemClickListener{
        void onItemClick(Date date);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * dp转px
     */
    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
