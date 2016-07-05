package com.cwf.demo.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

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

    private int curStartIndex;

    private int curEndIndex;

    private int todayIndex;

    private int[] date = new int[31];

    private Calendar calendar;


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
        calendar.add(Calendar.MONTH, -4);
        today = calendar.get(Calendar.DAY_OF_MONTH);
        isThisMonth = true;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        cellHeight = (14 + 30) * getResources().getDisplayMetrics().density;
        cellWidth = getResources().getDisplayMetrics().widthPixels / 7;

        weekTextPaint = new Paint();
        weekTextPaint.setTextSize(14 * getResources().getDisplayMetrics().density);
        weekTextPaint.setColor(Color.MAGENTA);
        weekTextPaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setTextSize(12 * getResources().getDisplayMetrics().density);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        bluePaint = new Paint();
        bluePaint.setTextSize(12 * getResources().getDisplayMetrics().density);
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStrokeCap(Paint.Cap.ROUND);
        bluePaint.setAntiAlias(true);

        whitePaint = new Paint();
        whitePaint.setTextSize(12 * getResources().getDisplayMetrics().density);
        whitePaint.setColor(Color.WHITE);

        blackPaint = new Paint();
        blackPaint.setTextSize(12 * getResources().getDisplayMetrics().density);
        blackPaint.setColor(Color.BLACK);
        blackPaint.setStrokeCap(Paint.Cap.ROUND);

        grayPaint = new Paint();
        grayPaint.setTextSize(12 * getResources().getDisplayMetrics().density);
        grayPaint.setColor(Color.GRAY);
        grayPaint.setStrokeCap(Paint.Cap.ROUND);
        initDate();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        mode = typedArray.getInt(R.styleable.CalendarView_mode, 1);
    }

    private void initDate() {
        Calendar c = calendar;
        c.set(Calendar.DAY_OF_MONTH, 1); // 变为本月第一天
        firstDayOfMonthInWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
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
        int lines = 2 + numberOfDaysExceptFirstLine / 7 + (numberOfDaysExceptFirstLine % 7 == 0 ? 0 : 1);
        return (int) (cellHeight * lines);
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
        for (int i = 0; i < 7; i++) {
            float weekTextX = RenderUtil.getStartX(cellWidth * i + cellWidth * 0.5f, weekTextPaint, weekText[i]);
            canvas.drawText(weekText[i], weekTextX, baseline, weekTextPaint);
        }

        for (int i = 1; i <= monthDay; i++) {
            float baseline1 = RenderUtil.getBaseline(0, cellHeight, textPaint);
            int start = (firstDayOfMonthInWeek + i - 1) * cellWidth;
            float startHeight = ((start + cellWidth) / screenWidth + 1) * (cellHeight)
                    + (cellHeight - baseline1) * 0.5f;
            float startWidth = start % (cellWidth * 7) -
                    textPaint.measureText(i + "") * 0.5f + cellWidth * 0.5f;
            if (isThisMonth && i == today)
                canvas.drawText(i + ""
                        , startWidth
                        , startHeight, bluePaint);
            else
                canvas.drawText(i + ""
                        , startWidth
                        , startHeight, textPaint);
        }
    }

    private void drawCircle(Canvas canvas, int i, Paint paint, float height) {

    }

    private void drawText(Canvas canvas, int i, Paint paint, String date) {

    }

    private int getDayOfMonth() {
//        Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
        int day = calendar.getActualMaximum(Calendar.DATE);
        return day;
    }

}
