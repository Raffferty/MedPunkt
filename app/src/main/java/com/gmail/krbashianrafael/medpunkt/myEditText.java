package com.gmail.krbashianrafael.medpunkt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by raf on 12.03.2018.
 * это кастомный класс для EditText
 * используется для создания объетов с реализацией OnTouchListener
 * без предоупреждения о том, что не реализованно performClick()
 */


public class myEditText extends android.support.v7.widget.AppCompatEditText implements View.OnTouchListener {


    public myEditText(Context context) {
        super(context);
    }

    public myEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public myEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        // Calls the super implementation, which generates an AccessibilityEvent
        // and calls the onClick() listener on the view, if any
        super.performClick();

        // Handle the action for the custom click here

        return true;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
