package com.ylw.ykeyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class Theme {
    private static Typeface _specialKeyFont = null;
    public final Paint keyBgPaint = new Paint();
    public final Paint keyDownBgPaint = new Paint();
    public final int lockedColor;
    public final int activatedColor;
    public final int labelColor;
    public final int subLabelColor;
    public final float keyBorderRadius;
    private final Paint _keyLabelPaint;
    private final Paint _specialKeyLabelPaint;
    private final Paint _keySubLabelPaint;
    private final Paint _specialKeySubLabelPaint;

    public Theme(Context context, AttributeSet attrs) {
        TypedArray s = context.getTheme().obtainStyledAttributes(attrs, R.styleable.keyboard, 0, 0);
        keyBgPaint.setColor(s.getColor(R.styleable.keyboard_colorKey, 0));
        keyDownBgPaint.setColor(s.getColor(R.styleable.keyboard_colorKeyActivated, 0));
        // colorKeyboard = s.getColor(R.styleable.keyboard_colorKeyboard, 0);
        labelColor = s.getColor(R.styleable.keyboard_colorLabel, 0);
        activatedColor = s.getColor(R.styleable.keyboard_colorLabelActivated, 0);
        lockedColor = s.getColor(R.styleable.keyboard_colorLabelLocked, 0);
        subLabelColor = s.getColor(R.styleable.keyboard_colorSubLabel, 0);
        keyBorderRadius = s.getDimension(R.styleable.keyboard_keyBorderRadius, 0);
        s.recycle();
        _keyLabelPaint = initLabelPaint(Paint.Align.CENTER, null);
        _keySubLabelPaint = initLabelPaint(Paint.Align.LEFT, null);
        Typeface specialKeyFont = getSpecialKeyFont(context);
        _specialKeyLabelPaint = initLabelPaint(Paint.Align.CENTER, specialKeyFont);
        _specialKeySubLabelPaint = initLabelPaint(Paint.Align.LEFT, specialKeyFont);
    }

    static public Typeface getSpecialKeyFont(Context context) {
        if (_specialKeyFont == null) {
            _specialKeyFont = Typeface.createFromAsset(context.getAssets(), "special_font.ttf");
        }
        return _specialKeyFont;
    }

    public Paint labelPaint(boolean special_font) {
        Paint p = special_font ? _specialKeyLabelPaint : _keyLabelPaint;
        return p;
    }

    public Paint subLabelPaint(boolean special_font, Paint.Align align) {
        Paint p = special_font ? _specialKeySubLabelPaint : _keySubLabelPaint;
        p.setTextAlign(align);
        return p;
    }

    private Paint initLabelPaint(Paint.Align align, Typeface font) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextAlign(align);
        if (font != null)
            paint.setTypeface(font);
        return (paint);
    }
}
