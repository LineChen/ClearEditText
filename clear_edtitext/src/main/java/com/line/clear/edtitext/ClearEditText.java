package com.line.clear.edtitext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Created by chenliu on 2019-12-13.
 */
public class ClearEditText extends AppCompatEditText {

    private View.OnFocusChangeListener innerFocusChangeListener;
    private View.OnFocusChangeListener outerFocusChangeListener;
    private boolean enableClose;
    private Drawable clearIconDrawable;

    public ClearEditText(Context context) {
        super(context);
        init(null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        int clearIcon = 0;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ClearEditText);
            try {
                clearIcon = a.getResourceId(R.styleable.ClearEditText_clear_edit_icon, 0);
                enableClose = a.getBoolean(R.styleable.ClearEditText_clear_edit_enable, true);
            } finally {
                a.recycle();
            }
        }
        if (clearIcon != 0) {
            final Drawable drawable = ContextCompat.getDrawable(getContext(), clearIcon);
            final Drawable wrappedDrawable = DrawableCompat.wrap(drawable); //Wrap the drawable so that it can be tinted pre Lollipop
            DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());
            clearIconDrawable = wrappedDrawable;
            clearIconDrawable.setBounds(0, 0, clearIconDrawable.getIntrinsicWidth(), clearIconDrawable.getIntrinsicHeight());
        }
        setClearIconVisible(false);

        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setClearIconVisible(enableClose && !TextUtils.isEmpty(s));
            }
        });


        innerFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setClearIconVisible(enableClose && hasFocus && !TextUtils.isEmpty(getText()));
                if (outerFocusChangeListener != null) {
                    outerFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        };
        super.setOnFocusChangeListener(innerFocusChangeListener);
    }

    private void setClearIconVisible(final boolean visible) {
        if (clearIconDrawable != null) {
            clearIconDrawable.setVisible(visible, false);
            final Drawable[] compoundDrawables = getCompoundDrawables();
            setCompoundDrawables(
                    compoundDrawables[0],
                    compoundDrawables[1],
                    visible ? clearIconDrawable : null,
                    compoundDrawables[3]);
        }
    }

    @Override
    public void setOnFocusChangeListener(View.OnFocusChangeListener listener) {
        if (innerFocusChangeListener == null) {
            super.setOnFocusChangeListener(listener);
        } else {
            outerFocusChangeListener = listener;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int x = (int) event.getX();
        if (enableClose && clearIconDrawable.isVisible() && x > getWidth() - getPaddingRight() - clearIconDrawable.getIntrinsicWidth()) {
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                setError(null);
                setText("");
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

}
