package cc.sinex.qiku.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import java.math.BigDecimal;

/**
 * Created by sinex on 16/5/10.
 */
public class Tools {

    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px2dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**
     *根据设备信息获取当前分辨率下指定单位对应的像素大小；
     * px,dip,sp -> px
     */
    public float getRawSize(Context c, int unit, float size) {
        Resources r;
        if (c == null){
            r = Resources.getSystem();
        }else{
            r = c.getResources();
        }
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

    public static String add(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return String.valueOf(b1.add(b2));
    }
}
