package sunok.jang.camera_crop_example;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Transformation;

/**
 * Created by jang on 2017. 8. 6..
 */

public class CameraTouchEventView extends View {

    private Rect mRect;
    Bitmap bitmap;
    AlphaAnimation mFadeOut;
    Paint mCharacterPaint;
    Transformation mTransformation;

    public CameraTouchEventView(Context context) {
        super(context);
        init(context);
    }

    public CameraTouchEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }



    private void init(Context context) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icn_circle_camera);
    }


    public Rect drawTouchEvent(Rect rect){
        mRect = rect;
        mCharacterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTransformation = new Transformation();


        mFadeOut = new AlphaAnimation(1f, 0f);
        mFadeOut.setDuration(500);
        mFadeOut.start();
        mFadeOut.getTransformation(System.currentTimeMillis(), mTransformation);

        invalidate();
        return mRect;
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mRect!=null) {

            canvas.drawBitmap(bitmap, null, mRect, mCharacterPaint);
            if (mFadeOut.hasStarted() && !mFadeOut.hasEnded()) {
                mFadeOut.getTransformation(System.currentTimeMillis(), mTransformation);
                //Keep drawing until we are done
                mCharacterPaint.setAlpha((int)(255 * mTransformation.getAlpha()));
                invalidate();
            } else {
                //Reset the alpha if animation is canceled
                mCharacterPaint.setAlpha(255);
            }
        }
    }
}
