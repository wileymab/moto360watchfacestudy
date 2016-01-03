package com.wileymab.watchfacetwo.drawing;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
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
        mOutlinePaint.setColor(mResources.getColor(R.color.batman_logo_outline_color));
        mOutlinePaint.setAntiAlias(!mIsAmbient);

    }

    public void setAmbientMode(boolean isAmbient) {
        mIsAmbient = isAmbient;
    }

    public void drawOnCanvasWithBounds(Canvas canvas, Rect bounds) {
        // TODO Check for ambient mode before drawing.

        drawTestShape(canvas,bounds);

        drawLogoOutline(canvas,bounds);
    }

    private void drawTestShape(Canvas canvas, Rect bounds) {

        Path logoPath = new Path();
        RectF arcBounds = new RectF();

        arcBounds.set(
                20,
                88,
                bounds.width() - 20,
                bounds.height() - 88
        );

        logoPath.arcTo(arcBounds,120,128,true);


        arcBounds.set(
                94,
                80,
                bounds.width() - 94,
                148
        );
        logoPath.arcTo(arcBounds,172,45,true);


        arcBounds.set(
                94,
                90,
                bounds.width() - 144,
                138
        );
        logoPath.arcTo(arcBounds,100,82,true);

        canvas.drawPath(logoPath,mTestPaint);
    }

    private void drawLogoOutline(Canvas canvas, Rect bounds) {

        // Path to hold all of the path components
        Path batmanLogoPath = new Path();

        // Add the outer wing arc paths
        batmanLogoPath = addOuterWingArcs(batmanLogoPath,bounds);

        // Draw the logo
        canvas.drawPath(batmanLogoPath,mOutlinePaint);
    }

    private Path addOuterWingArcs(Path masterPath,Rect bounds) {

        // Create the bounding rectangle.
        int     left = 20,
                top = 88,
                right = bounds.width() - left,
                bottom = bounds.height() - top;

        // Add the start and sweep for the left side.
        //masterPath.addArc(left,top,right,bottom,120,128);

        // Add the start and sweep for the right side.
        masterPath.addArc(left,top,right,bottom,292,128);

        return masterPath;
    }

}
