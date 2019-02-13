package com.umpay.payplugindemo;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

/**
 * Created by tianxiaoyang on 2017/5/9.
 */
public class SpannableUtil {
    public static SpannableString getSpannableString(String string) {
        SpannableString spanString = new SpannableString(string);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.RED);
        spanString.setSpan(span, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }
}
