package com.wileymab.watchfacetwo.drawing;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.format.Time;

import com.wileymab.watchfacetwo.R;

/**
 * Created by matt on 1/2/16.
 */
public class BatmanWatchFace {
    /*
        SINGLETON ----------------------------------------------------------------------------------
     */
    private static BatmanWatchFace sSingleton;

    public static BatmanWatchFace getSingleton(Resources resources) {
        if ( sSingleton == null )
            sSingleton = new BatmanWatchFace(resources);
        return sSingleton;
    }
    /*
        END SINGLETON ------------------------------------------------------------------------------
     */

    private Resources mResources;

    private boolean mIsAmbient;
    private boolean mIsLowBitAmbient;
    private Time mTime;

    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;
    private Paint mHandsPaint;

    private Paint mAmbientForegroundPaint;
    private Paint mAmbientHandsPaint;

    private Paint mTestPaint;

    private BatmanWatchFace(Resources resources) {
        mResources = resources;
        mTime = new Time();
        initPaints();
    }

    private void initPaints() {

        // -- INTERACTIVE

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(mResources.getColor(R.color.background));
        mBackgroundPaint.setAntiAlias(!mIsAmbient);

        mForegroundPaint = new Paint();
        mForegroundPaint.setStyle(Paint.Style.FILL);
        mForegroundPaint.setColor(mResources.getColor(R.color.batman_1_background));
        mForegroundPaint.setAntiAlias(true);

        mHandsPaint = new Paint();
        mHandsPaint.setColor(mResources.getColor(R.color.analog_hands));
        mHandsPaint.setStrokeWidth(mResources.getDimension(R.dimen.analog_hand_stroke));
        mHandsPaint.setStrokeCap(Paint.Cap.ROUND);
        mHandsPaint.setAntiAlias(true);

        // --- AMBIENT

        mAmbientForegroundPaint = new Paint();
        mAmbientForegroundPaint.setStyle(Paint.Style.STROKE);
        mAmbientForegroundPaint.setColor(Color.WHITE);
        mAmbientForegroundPaint.setAntiAlias(!mIsLowBitAmbient);

        mAmbientHandsPaint = new Paint();
        mAmbientHandsPaint.setStyle(Paint.Style.STROKE);
        mAmbientHandsPaint.setStrokeWidth(mResources.getDimension(R.dimen.analog_hand_stroke));
        mAmbientHandsPaint.setStrokeCap(Paint.Cap.ROUND);
        mAmbientHandsPaint.setColor(Color.WHITE);
        mAmbientHandsPaint.setAntiAlias(!mIsLowBitAmbient);

        // --- OTHER CRAP

        mTestPaint = new Paint();
        mTestPaint.setStyle(Paint.Style.FILL);
        mTestPaint.setStrokeWidth(mResources.getDimension(R.dimen.batman_logo_outline_stroke_width));
        mTestPaint.setStrokeJoin(Paint.Join.BEVEL);
        mTestPaint.setColor(mResources.getColor(R.color.test_shape_color));
        mTestPaint.setAntiAlias(true);
    }

    public void setAmbientMode(boolean isAmbient, boolean isLowBitAmbient) {
        mIsAmbient = isAmbient;
        mIsLowBitAmbient = isLowBitAmbient;
    }

    public void clearTimeToTimeZone(String timezoneId) {
        mTime.clear(timezoneId);
        mTime.setToNow();
    }

    public void drawOnCanvasWithBounds(Canvas canvas, Rect bounds) {
        mTime.setToNow();
        drawBackground(canvas,bounds);
        drawForeground(canvas,bounds);
        drawHands(canvas,bounds);
    }

    private void drawBackground(Canvas canvas, Rect bounds) {
        if (mIsAmbient) {
            canvas.drawColor(Color.BLACK);
        }
        else {
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackgroundPaint);
        }
    }

    /*
        Draw the Batman logo.
     */
    private void drawForeground(Canvas canvas, Rect bounds) {

        Path logoPath = new Path();

        // Draw the left half
        logoPath.arcTo(0, 0, 280, 144,120,128,true);
        logoPath.rQuadTo(-35,33,22,43);
        logoPath.rQuadTo(16,-3,16,-51);
        logoPath.rLineTo(7,14);
        logoPath.rQuadTo(0,0,9,-1);
        logoPath.rLineTo(0,130);
        logoPath.rQuadTo(-25,-77,-42,-36);
        logoPath.rQuadTo(-33,-29,-44,-14);
        logoPath.rQuadTo(-15,13,15,45);


        // Reflect the left half to create the right half
        Matrix reflectionMatrix = new Matrix();
        reflectionMatrix.preScale(-1f,1f,141,0);
        Path transformedPath = new Path();
        logoPath.transform(reflectionMatrix,transformedPath);

        // Union the halves into one path
        logoPath.op(transformedPath, Path.Op.UNION);

        // Center the logo
        RectF logoBounds = new RectF();
        logoPath.computeBounds(logoBounds,true);

        int logoCenterX = (int)logoBounds.centerX();
        int logoCenterY = (int)logoBounds.centerY();
        int faceCenterX = bounds.centerX();
        int faceCenterY = bounds.centerY();

        Matrix translationMatrix = new Matrix();
        translationMatrix.setTranslate( (faceCenterX-logoCenterX), (faceCenterY-logoCenterY) );
        logoPath.transform(translationMatrix);

        Paint paint = (mIsAmbient) ? mAmbientForegroundPaint : mForegroundPaint;
        canvas.drawPath( logoPath, paint );

    }

    /*
        Draw the watch hands.
     */
    private void drawHands(Canvas canvas, Rect bounds) {

        // Find the center. Ignore the window insets so that, on round watches with a
        // "chin", the watch face is centered on the entire screen, not just the usable
        // portion.
        float spanLength = bounds.width();
        float centerX = spanLength / 2f, centerY = centerX;
        //float centerY = bounds.height() / 2f;

        float secRot = mTime.second / 30f * (float) Math.PI;
        int minutes = mTime.minute;
        float minRot = minutes / 30f * (float) Math.PI;
        float hrRot = ((mTime.hour + (minutes / 60f)) / 6f) * (float) Math.PI;

        float secLength = centerX - 20;
        float minLength = centerX - 40;
        float hrLength = centerX - 80;

        Paint paint = (mIsAmbient) ? mAmbientHandsPaint : mHandsPaint;

        if (!mIsAmbient) {
            float secX = (float) Math.sin(secRot) * secLength;
            float secY = (float) -Math.cos(secRot) * secLength;
            canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, paint);
        }

        float minX = (float) Math.sin(minRot) * minLength;
        float minY = (float) -Math.cos(minRot) * minLength;
        canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY, paint);

        float hrX = (float) Math.sin(hrRot) * hrLength;
        float hrY = (float) -Math.cos(hrRot) * hrLength;
        canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY, paint);

    }

}
