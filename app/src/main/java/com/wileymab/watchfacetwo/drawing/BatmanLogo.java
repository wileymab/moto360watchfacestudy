package com.wileymab.watchfacetwo.drawing;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

import com.wileymab.watchfacetwo.R;

/**
 * Created by matt on 1/2/16.
 */
public class BatmanLogo {
    /*
        SINGLETON ----------------------------------------------------------------------------------
     */
    private static BatmanLogo sSingleton;

    public static BatmanLogo getSingleton(Resources resources) {
        if ( sSingleton == null )
            sSingleton = new BatmanLogo(resources);
        return sSingleton;
    }
    /*
        END SINGLETON ------------------------------------------------------------------------------
     */

    private Resources mResources;

    private boolean mIsAmbient;

    private Paint mOutlinePaint;

    private Paint mTestPaint;

    private BatmanLogo(Resources resources) {
        mResources = resources;
        initPaints();
    }

    private void initPaints() {

        mTestPaint = new Paint();
        mTestPaint.setStyle(Paint.Style.STROKE);
        mTestPaint.setStrokeWidth(mResources.getDimension(R.dimen.batman_logo_outline_stroke_width));
        mTestPaint.setStrokeJoin(Paint.Join.BEVEL);
        mTestPaint.setColor(mResources.getColor(R.color.test_shape_color));
        mTestPaint.setAntiAlias(!mIsAmbient);

        mOutlinePaint = new Paint();
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setStrokeWidth(mResources.getDimension(R.dimen.batman_logo_outline_stroke_width));
        mTestPaint.setStrokeJoin(Paint.Join.BEVEL);
        mOutlinePaint.setColor(mResources.getColor(R.color.batman_logo_black));
        mOutlinePaint.setAntiAlias(!mIsAmbient);

    }

    public void setAmbientMode(boolean isAmbient) {
        mIsAmbient = isAmbient;
    }

    public void drawOnCanvasWithBounds(Canvas canvas, Rect bounds) {
        // TODO Check for ambient mode before drawing.
        drawLogo(canvas,bounds);
    }

    private void drawLogo(Canvas canvas, Rect bounds) {

        Path logoPath = new Path();
        RectF arcBounds = new RectF();

        // Outer wing arc
        arcBounds.set(
                20,
                88,
                bounds.width() - 20,
                bounds.width() - 88
        );

        logoPath.arcTo(arcBounds,120,128,true);

        // Upper wing ingress
        arcBounds.set(
                94,
                80,
                bounds.width() - 94,
                148
        );
        logoPath.arcTo(arcBounds,172,45,true);

        // Upper wing trough
        arcBounds.set(
                94,
                90,
                bounds.width() - 144,
                138
        );
        logoPath.arcTo(arcBounds,100,82,true);

        // Upper wing egress to ear
        arcBounds.set(
                110,
                50,
                146,
                137
        );
        logoPath.arcTo(arcBounds,352,103,true);

        // Ear to top of head
        logoPath.moveTo(146,87);
        logoPath.lineTo(152,101);

        // Half of top of head
        arcBounds.set(
                140,
                99,
                bounds.width() - 140,
                120
        );
        logoPath.arcTo(arcBounds,242,30,true);

        // From tail point to inside arc
        logoPath.moveTo(160,222);
        logoPath.lineTo(146,188);

        // Wing lower inside arc
        arcBounds.set(
                111,
                177,
                155,
                300
        );
        logoPath.arcTo(arcBounds,231,74,true);

        // Minor point
        logoPath.moveTo(120,191);
        logoPath.lineTo(97,174);

        // Wing lower outside arc
        arcBounds.set(
                71,
                172,
                106,
                207
        );
        logoPath.arcTo(arcBounds,147,160,true);

        // Terminus into outer wing arc
        logoPath.moveTo(73,198);
        logoPath.lineTo(89,222);

        Matrix reflectionMatrix = new Matrix();
        reflectionMatrix.preScale(-1f,1f,bounds.width()/2,bounds.height()/2);

        Path transformedPath = new Path();
        logoPath.transform(reflectionMatrix,transformedPath);

        logoPath.addPath(transformedPath);

        canvas.drawPath(logoPath,mOutlinePaint);
    }

}
