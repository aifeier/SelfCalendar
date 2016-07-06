package com.cwf.demo.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

/**
 * Created at 陈 on 2016/7/5.
 * 自定义日历
 *
 * @author cwf
 * @email 237142681@qq.com
 */
public class CalendarView extends View {
    private int mode = 1;

    private int screenWidth;
    private float cellHeight = 100;
    private int cellWidth = 100;

    private Paint weekTextPaint;
    private String[] weekText = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周七"};
    private TextPaint textPaint;
    private Paint bluePaint, whitePaint, blackPaint, grayPaint;

    /*本月第一天是周几*/
    private int firstDayOfMonthInWeek;

    /*本月有几天*/
    private int monthDay;

    /*今天*/
    private int today;

    /*是否是当前月*/
    private boolean isThisMonth = true;

    /*当前日期*/
    private int curYear;
    private int curMonth;
    private int curDay;

    private ItemClickListener itemClickListener;


    private Calendar calendar;

    private float textSize;

    private float density;


    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        calendar = Calendar.getInstance();
        today = calendar.get(Calendar.DAY_OF_MONTH);
//        calendar.add(Calendar.MONTH, -4);
        density = getResources().getDisplayMetrics().density;
        textSize = 14 * density;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        cellHeight = textSize + 30 * density;
        cellWidth = getResources().getDisplayMetrics().widthPixels / 7;
//        cellHeight = cellWidth;
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        marginLayoutParams.setMargins((screenWidth - 7 * cellWidth) / 2, 0, (screenWidth - 7 * cellWidth) / 2, 0);
        setLayoutParams(marginLayoutParams);

        weekTextPaint = new Paint();
        weekTextPaint.setTextSize(textSize);
        weekTextPaint.setColor(Color.MAGENTA);
        weekTextPaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        bluePaint = new Paint();
        bluePaint.setTextSize(textSize);
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStyle(Paint.Style.STROKE);
        bluePaint.setStrokeWidth(textSize / 15);
        bluePaint.setAntiAlias(true);

        whitePaint = new Paint();
        whitePaint.setTextSize(textSize);
        whitePaint.setColor(Color.WHITE);

        blackPaint = new Paint();
        blackPaint.setTextSize(textSize);
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStyle(Paint.Style.STROKE);
        blackPaint.setStrokeWidth(textSize / 15);
        blackPaint.setAntiAlias(true);

        grayPaint = new Paint();
        grayPaint.setTextSize(textSize);
        grayPaint.setColor(Color.GRAY);
        grayPaint.setStrokeCap(Paint.Cap.ROUND);
        initDate();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        mode = typedArray.getInt(R.styleable.CalendarView_mode, 1);
    }

    private void initDate() {
        curYear = calendar.get(Calendar.YEAR);
        curMonth = calendar.get(Calendar.MONTH) + 1;
        curDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (curMonth == Calendar.getInstance().get(Calendar.MONTH) + 1
                && curYear == Calendar.getInstance().get(Calendar.YEAR))
            isThisMonth = true;
        else
            isThisMonth = false;
        Calendar c = calendar;
        c.set(Calendar.DAY_OF_MONTH, 1); // 变为本月第一天
        firstDayOfMonthInWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (firstDayOfMonthInWeek == 0)
            firstDayOfMonthInWeek = 7;
        monthDay = getDayOfMonth();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(measureHeight(), View.MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * calculate the total height of the widget
     */
    private int measureHeight() {
        /**
         * the weekday of the first day of the month, Sunday's result is 1 and Monday 2 and Saturday 7, etc.
         */
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        /**
         * the number of days of current month
         */
        int daysOfMonth = getDayOfMonth();
        /**
         * calculate the total lines, which equals to 1 (head of the calendar) + 1 (the first line) + n/7 + (n%7==0?0:1)
         * and n means numberOfDaysExceptFirstLine
         */
        int numberOfDaysExceptFirstLine = -1;
        if (dayOfWeek >= 2 && dayOfWeek <= 7) {
            numberOfDaysExceptFirstLine = daysOfMonth - (8 - dayOfWeek + 1);
        } else if (dayOfWeek == 1) {
            numberOfDaysExceptFirstLine = daysOfMonth - 1;
        }
        int lines = 3 + numberOfDaysExceptFirstLine / 7 + (numberOfDaysExceptFirstLine % 7 == 0 ? 0 : 1);
        return (int) (cellHeight * lines - cellHeight / 2);
    }

    /**
     * render
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * render the head
         */

        float baseline = RenderUtil.getBaseline(0, cellHeight, weekTextPaint);
        canvas.drawText(curYear + "-" + curMonth, screenWidth / 2 - weekTextPaint.measureText(curYear + curMonth + ""), baseline, weekTextPaint);
        canvas.drawText("next", cellWidth * 6 - weekTextPaint.measureText("next"), baseline, weekTextPaint);
        canvas.drawText("previous", cellWidth * 2 - weekTextPaint.measureText("previous"), baseline, weekTextPaint);
        baseline += cellHeight / 2;
        for (int i = 0; i < 7; i++) {
            float weekTextX = RenderUtil.getStartX(cellWidth * i + cellWidth * 0.5f, weekTextPaint, weekText[i]);
            canvas.drawText(weekText[i], weekTextX, baseline, weekTextPaint);
        }

        for (int i = 1; i <= monthDay; i++) {
            float baseline1 = RenderUtil.getBaseline(0, cellHeight, textPaint);
            int start = (firstDayOfMonthInWeek + i - 2) * cellWidth;
            float startHeight = ((start + cellWidth) / screenWidth + 1) * (cellHeight)
                    + (cellHeight - baseline1) * 0.5f + cellHeight;
            float startWidth = start % (cellWidth * 7)
                    - textPaint.measureText(i + "") * 0.5f + cellWidth * 0.5f;
            if (isThisMonth && i == today) {
                canvas.drawText(i + ""
                        , startWidth
                        , startHeight, textPaint);
                canvas.drawCircle(startWidth, startHeight - cellWidth / 10, cellWidth / 4, bluePaint);
            } else {
                canvas.drawText(i + ""
                        , startWidth
                        , startHeight, textPaint);
            }
        }
    }


    private boolean next = false, previous = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            curDay = getCheckDay(event.getX(), event.getY());
            next = isNext(event.getX(), event.getY());
            previous = isPrevious(event.getX(), event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (curDay == getCheckDay(event.getX(), event.getY())
                    && curDay > 0 && curDay <= monthDay) {
                if (itemClickListener != null)
                    itemClickListener.onClick(curYear, curMonth, curDay);
            } else if (next && isNext(event.getX(), event.getY())) {
                gotoNext();
            } else if (previous && isPrevious(event.getX(), event.getY())) {
                gotoPrevious();
            }
        }
        Log.e("onTouch", curYear + ":" + curMonth + ":" + curDay + "");
        return true;
    }

    private boolean isNext(float x, float y) {
        if (x > cellWidth * 5 && y < cellHeight)
            return true;
        else
            return false;
    }

    private boolean isPrevious(float x, float y) {
        if (x < cellWidth * 2 && y < cellHeight)
            return true;
        else
            return false;
    }

    private void gotoNext() {
        calendar.add(Calendar.MONTH, 1);
        refresh();
    }

    private void gotoPrevious() {
        calendar.add(Calendar.MONTH, -1);
        refresh();
    }

    private void refresh() {
        initDate();
        requestLayout();
        invalidate();
    }


    /*根据点击的x,y坐标获取日期*/
    private int getCheckDay(float x, float y) {
        if (y < cellHeight * 1.5f) {
            return -1;
        }
        int line = (int) ((y - cellHeight * 1.53f) / cellHeight);
        int column = (int) (x / cellWidth);
        int day = line * 7 + column - firstDayOfMonthInWeek + 2;
        return day;
    }

    /*获取一个月有几天*/
    private int getDayOfMonth() {
        int day = calendar.getActualMaximum(Calendar.DATE);
        return day;
    }


    /*设置显示的月份*/
    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
        refresh();
    }

    /*设置日期点击事件*/
    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        public void onClick(int year, int month, int day);
    }

}
