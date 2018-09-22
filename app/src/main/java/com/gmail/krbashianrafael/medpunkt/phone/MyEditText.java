package com.gmail.krbashianrafael.medpunkt.phone;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * это кастомный EditText у которого клавиатура не перекрывает текст
 * и, при сворачивании клавиатуры EditText выходит из фокуса
 * */

public class MyEditText extends android.support.v7.widget.AppCompatEditText {

    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
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
