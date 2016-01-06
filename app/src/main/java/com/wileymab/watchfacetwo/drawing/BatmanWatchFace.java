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
import android.util.TypedValue;

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
    private Resources.Theme mTheme;

    private boolean mIsAmbient;
    private boolean mIsLowBitAmbient;
    private Time mTime;

    private Paint mBackgroundPaint;
    private Paint mForegroundPaint;

    private Paint mHandsStrokePaint;
    private Paint mHandsFillPaint;

    private Path mLogoPath;

    private BatmanWatchFace(Resources resources) {
        mResources = resources;
        mTime = new Time();
        initPaints();
    }

    private void initPaints() {

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mResources.getColor(R.color.init_bg));

        mForegroundPaint = new Paint();
        mForegroundPaint.setColor(mResources.getColor(R.color.init_fg));
        mForegroundPaint.setStrokeWidth(mResources.getDimension(R.dimen.batman_logo_outline_stroke_width));

        mHandsStrokePaint = new Paint();
        mHandsStrokePaint.setStyle(Paint.Style.STROKE);
        mHandsStrokePaint.setColor(mResources.getColor(R.color.init_fg));
        mHandsStrokePaint.setStrokeWidth(mResources.getDimension(R.dimen.batman_logo_outline_stroke_width));
        mHandsStrokePaint.setStrokeCap(Paint.Cap.ROUND);

        mHandsFillPaint = new Paint();
        mHandsFillPaint.setStyle(Paint.Style.FILL);
        mHandsFillPaint.setColor(mResources.getColor(R.color.init_bg));

        attemptToThemeColors();

    }

    private void attemptToThemeColors() {

        if ( mIsAmbient ) {
            mBackgroundPaint.setColor(Color.BLACK);

            mForegroundPaint.setColor(Color.WHITE);
            mForegroundPaint.setStyle(Paint.Style.STROKE);

            mHandsFillPaint.setColor(Color.BLACK);
            mHandsStrokePaint.setColor(Color.WHITE);
        }
        else if ( mTheme != null ) {

            TypedValue attrValue = new TypedValue();

            mTheme.resolveAttribute(R.attr.bmwf_backgroundColor, attrValue, true);
            mBackgroundPaint.setColor(attrValue.data);
            mHandsFillPaint.setColor(attrValue.data);

            mTheme.resolveAttribute(R.attr.bmwf_foregroundColor, attrValue, true);
            mForegroundPaint.setColor(attrValue.data);
            mForegroundPaint.setStyle(Paint.Style.FILL);
            mHandsStrokePaint.setColor(attrValue.data);

        }

        mBackgroundPaint.setAntiAlias(!mIsAmbient);
        mForegroundPaint.setAntiAlias(!mIsAmbient);
        mHandsFillPaint.setAntiAlias(!mIsAmbient);
        mHandsStrokePaint.setAntiAlias(!mIsAmbient);

    }

    public void setTheme(Resources.Theme theme) {
        mTheme = theme;
        attemptToThemeColors();
    }

    public void setAmbientMode(boolean isAmbient, boolean isLowBitAmbient) {
        mIsAmbient = isAmbient;
        mIsLowBitAmbient = isLowBitAmbient;
        attemptToThemeColors();
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
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBackgroundPaint);
    }

    /*
        Draw the Batman logo.
     */
    private void drawForeground(Canvas canvas, Rect bounds) {

        if ( mLogoPath == null ) {

            mLogoPath = new Path();

            // Draw the left half
            mLogoPath.arcTo(0, 0, 280, 144, 120, 128, true);
            mLogoPath.rQuadTo(-35, 33, 22, 43);
            mLogoPath.rQuadTo(16, -3, 16, -51);
            mLogoPath.rLineTo(7, 14);
            mLogoPath.rQuadTo(0, 0, 9, -1);
            mLogoPath.rLineTo(0, 130);
            mLogoPath.rQuadTo(-25, -77, -42, -36);
            mLogoPath.rQuadTo(-33, -29, -44, -14);
            mLogoPath.rQuadTo(-15, 13, 15, 45);


            // Reflect the left half to create the right half
            Matrix reflectionMatrix = new Matrix();
            reflectionMatrix.preScale(-1f, 1f, 141, 0);
            Path transformedPath = new Path();
            mLogoPath.transform(reflectionMatrix, transformedPath);

            // Union the halves into one path
            mLogoPath.op(transformedPath, Path.Op.UNION);


            // Scale the logo to fit any screen similarly
            RectF logoBounds = new RectF();
            mLogoPath.computeBounds(logoBounds, true);

            float scale = 0.99f;
            int targetWidth = (int) (scale * bounds.width());

            float operationalScalar = (float) targetWidth / logoBounds.width();

            Matrix scalingMatrix = new Matrix();
            scalingMatrix.setScale(operationalScalar, operationalScalar);
            mLogoPath.transform(scalingMatrix);


            // Center the logo on the face
            mLogoPath.computeBounds(logoBounds, true);

            int logoCenterX = (int) logoBounds.centerX();
            int logoCenterY = (int) logoBounds.centerY();
            int faceCenterX = bounds.centerX();
            int faceCenterY = bounds.centerY();

            Matrix translationMatrix = new Matrix();
            translationMatrix.setTranslate((faceCenterX - logoCenterX), (faceCenterY - logoCenterY));
            mLogoPath.transform(translationMatrix);
        }

        Paint paint = mForegroundPaint;
        canvas.drawPath( mLogoPath, paint );

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


        int seconds = mTime.second;
        int minutes = mTime.minute;
        float secRot = seconds * 6f;
        float minRot = ((seconds / 60f) + minutes) * 6f;
        float hrRot = ((minutes / 60f) + mTime.hour) * 30f;

        float secLength = centerX - 20;
        float minLength = centerX - 40;
        float hrLength = centerX - 80;

        float handSize = 6f;
        float hubSize = handSize * 3f;

        Path hubPath = new Path();
        hubPath.addOval(-hubSize,-hubSize,hubSize,hubSize,Path.Direction.CW);

        Matrix rotateHand = new Matrix();

        Path minHandPath = new Path();
        minHandPath.addRoundRect(-handSize, -handSize, minLength, handSize, handSize, handSize, Path.Direction.CW);
        rotateHand.reset();
        rotateHand.setRotate(minRot, 0, 0);
        minHandPath.transform(rotateHand);

        Path hrHandPath = new Path();
        hrHandPath.addRoundRect(-handSize, -handSize, hrLength, handSize, handSize, handSize, Path.Direction.CW);
        rotateHand.reset();
        rotateHand.setRotate(hrRot, 0, 0);
        hrHandPath.transform(rotateHand);

        hrHandPath.op(minHandPath, Path.Op.UNION);

        if ( !mIsAmbient ) {
            Path secHandPath = new Path();
            secHandPath.addRoundRect(-handSize, -handSize, secLength, handSize, handSize, handSize, Path.Direction.CW);
            rotateHand.reset();
            rotateHand.setRotate(secRot, 0, 0);
            secHandPath.transform(rotateHand);

            hrHandPath.op(secHandPath, Path.Op.UNION);
        }

        hrHandPath.op(hubPath, Path.Op.UNION);

        Matrix resetToMidnight = new Matrix();
        resetToMidnight.setRotate(-90f,0,0);
        hrHandPath.transform(resetToMidnight);

        Matrix translateToCenter = new Matrix();
        translateToCenter.setTranslate(bounds.width() / 2, bounds.width() / 2);
        hrHandPath.transform(translateToCenter);

        canvas.drawPath(hrHandPath, mHandsFillPaint);
        canvas.drawPath(hrHandPath, mHandsStrokePaint);

    }

}
