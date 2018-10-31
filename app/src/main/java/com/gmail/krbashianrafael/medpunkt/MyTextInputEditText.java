package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * это кастомный TextInputEditText у которого клавиатура не перекрывает текст
 * и, при сворачивании клавиатуры TextInputEditText выходит из фокуса
 * */

public class MyTextInputEditText extends android.support.design.widget.TextInputEditText {
    public MyTextInputEditText(Context context) {
        super(context);
    }

    public MyTextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus();
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
