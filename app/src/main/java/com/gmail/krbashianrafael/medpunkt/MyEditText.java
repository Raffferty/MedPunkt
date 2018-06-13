package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * этот класс создан для того, чтоб клавиатура не перекрывала EditText
 */

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
