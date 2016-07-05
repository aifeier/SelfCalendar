package com.cwf.demo.myapplication;

import android.graphics.Paint;

/**
 * Created at 陈 on 2016/7/5.
 *
 * @author cwf
 * @email 237142681@qq.com
 */
public class RenderUtil {

    /**
     * get the baseline to draw between top and bottom in the middle
     */
    public static float getBaseline(float top, float bottom, Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (top + bottom - fontMetrics.bottom - fontMetrics.top) / 2;
    }
    /**
     *	get the x position to draw around the middle
     */
    public static float getStartX(float middle, Paint paint, String text){
        return middle - paint.measureText(text) * 0.5f;
    }
}
