package com.line.clear.edtitext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * Created by chenliu on 2019-11-05.
 */
public class ClearEditText extends AppCompatEditText {

    private Bitmap mClearBitmap;
    private int mWidth;
    private int mHeight;
    private boolean showClose;
    private boolean enableClose;
    private float iconPadding;
    private float mClearIconDrawWidth;
    private float mClearIconDrawHeight;
    private RectF mDestRect;

    private View.OnFocusChangeListener innerFocusChangeListener;
    private View.OnFocusChangeListener outerFocusChangeListener;


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
                mClearIconDrawWidth = a.getDimension(R.styleable.ClearEditText_clear_edit_icon_width, 0f);
                mClearIconDrawHeight = a.getDimension(R.styleable.ClearEditText_clear_edit_icon_height, 0f);
                iconPadding = a.getDimension(R.styleable.ClearEditText_clear_edit_icon_padding, 0f);
                enableClose = a.getBoolean(R.styleable.ClearEditText_clear_edit_enable, true);
            } finally {
                a.recycle();
            }
        }
        mDestRect = new RectF();
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        if (clearIcon != 0) {
            mClearBitmap = BitmapFactory.decodeResource(getResources(), clearIcon, bfoOptions);
        }

        addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                showClose = !TextUtils.isEmpty(s);
                invalidate();
            }
        });


        innerFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showClose = hasFocus && !TextUtils.isEmpty(getText());
                invalidate();
                if (outerFocusChangeListener != null) {
                    outerFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        };
        super.setOnFocusChangeListener(innerFocusChangeListener);
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
        if (enableClose && event.getActionMasked() == MotionEvent.ACTION_UP) {
            if (showClose && event.getX() > (getWidth() - getHeight() + iconPadding)
                    && event.getX() < (getWidth() - iconPadding)
                    && event.getY() > iconPadding
                    && event.getY() < (getHeight() - iconPadding)) {
                setText("");
            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (enableClose && showClose) {
            mDestRect.set(mWidth - mClearIconDrawWidth - iconPadding, (mHeight - mClearIconDrawHeight) / 2, mWidth - iconPadding, (mHeight - mClearIconDrawHeight) / 2 + mClearIconDrawHeight);
            canvas.drawBitmap(mClearBitmap, null, mDestRect, null);
        }
    }

    private boolean hasScale;

    private void deal() {
        if (!hasScale) {
            int width = mClearBitmap.getWidth();
            int height = mClearBitmap.getHeight();
            // 设置想要的大小
            if (mClearIconDrawWidth == 0 || mClearIconDrawHeight == 0) {
                mClearIconDrawWidth = width;
                mClearIconDrawHeight = height;
            } else {
                if (mClearIconDrawWidth > mHeight) {
                    mClearIconDrawWidth = mHeight;
                }
                if (mClearIconDrawHeight > mHeight) {
                    mClearIconDrawHeight = mHeight;
                }
            }
            hasScale = true;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        deal();
    }

    public boolean isShowClose() {
        return showClose;
    }

    public void setShowClose(boolean showClose) {
        this.showClose = showClose;
    }

    public boolean isEnableClose() {
        return enableClose;
    }

    public void setEnableClose(boolean enableClose) {
        this.enableClose = enableClose;
    }

    public float getIconPadding() {
        return iconPadding;
    }

    public void setIconPadding(float iconPadding) {
        this.iconPadding = iconPadding;
    }
}