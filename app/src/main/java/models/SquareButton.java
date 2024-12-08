package models;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Makes AppCompatButton height = width
 */
public class SquareButton extends androidx.appcompat.widget.AppCompatButton {

    public SquareButton(Context context) {
        super(context);
        //init();
    }

    public SquareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
    }

    public SquareButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //square
        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Drawable mDrawable = getBackground();
        if (mDrawable != null) {
            int width = getWidth();
            int height = getHeight();

            // Calculate the desired size (70% of the button size)
            int drawableWidth = (int) (width * 0.5);
            int drawableHeight = (int) (height * 0.5);

            // Calculate the left and top to center the drawable
            int left = (width - drawableWidth) / 2;
            int top = (height - drawableHeight) / 2;

            // Set the bounds of the drawable
            mDrawable.setBounds(left, top, left + drawableWidth, top + drawableHeight);

            // Draw the drawable
            mDrawable.draw(canvas);
        }
    }
}
