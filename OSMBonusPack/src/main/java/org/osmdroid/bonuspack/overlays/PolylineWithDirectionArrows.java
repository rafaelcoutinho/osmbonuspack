package org.osmdroid.bonuspack.overlays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

/**
 * Extends Polyline overlay to have direction and draw arrows on the lines
 * Created by rafael.coutinho on 04/05/16.
 */
public class PolylineWithDirectionArrows extends Polyline {
    public PolylineWithDirectionArrows(Context ctx) {
        super(ctx);
        init();
    }

    public PolylineWithDirectionArrows(final ResourceProxy resourceProxy) {
        super(resourceProxy);
        init();
    }

    @Override
    protected void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
        if (shadow) {
            return;
        }

        final int size = mPoints.size();
        if (size < 2) {
            // nothing to paint
            return;
        }
        final Projection pj = mapView.getProjection();
        Point projectedPoint0 = mPoints.get(0); // points from the points list

        Point screenPoint0 = pj.toPixelsFromProjected(projectedPoint0, mTempPoint1); // points on screen
        Point screenPoint1;

        for (int i = 1; i < size; i++) {

            Point projectedPoint1 = mPoints.get(i);
            screenPoint1 = pj.toPixelsFromProjected(projectedPoint1, this.mTempPoint2);
            if (Math.abs(screenPoint1.x - screenPoint0.x) + Math.abs(screenPoint1.y - screenPoint0.y) <= 25) {
                screenPoint0.x=screenPoint1.x;
                screenPoint0.y=screenPoint1.y;
                // skip this point, too close to previous point
                continue;
            }
            applyCustomStyles(screenPoint0,screenPoint1,canvas);

            screenPoint0.x=screenPoint1.x;
            screenPoint0.y=screenPoint1.y;

        }
    }



    private void init() {
        paintFill = new Paint();
        paintStroke = new Paint();

        paintFill.setStrokeWidth(1);
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setColor(Color.GREEN);

        paintStroke.setStrokeWidth(1);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.GRAY);

    }

    private Paint paintFill;
    private Paint paintStroke;
    protected void applyCustomStyles(Point p0, Point p1,  Canvas canvas) {
//        fillArrow(mPaint,canvas,p0.x,p0.y,p1.x,p1.y);
        // create and draw triangles
// use a Path object to store the 3 line segments
// use .offset to draw in many locations
// note: this triangle is not centered at 0,0



        Path path = new Path();

//        path.moveTo(0, -10);
        float x = (p0.x+p1.x)/2;
        float y = (p0.y+p1.y)/2;
        path.moveTo(x, y);
//        path.lineTo(5, 0);
        path.lineTo(x+5, y+10);
        path.lineTo(x-5, y+10);
//        path.lineTo(-5, 0);
        path.close();

        Matrix mMatrix = new Matrix();
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        float angle = (float)angleOf(p1,p0);

        mMatrix.postRotate(angle-90, x,y);//bounds.centerX(), bounds.centerY());
        path.transform(mMatrix);


        canvas.drawPath(path, paintFill);
        canvas.drawPath(path, paintStroke);

    }
    public static double angleOf(Point p1, Point p2) {
        // NOTE: Remember that most math has the Y axis as positive above the X.
        // However, for screens we have Y as positive below. For this reason,
        // the Y values are inverted to get the expected results.
        final double deltaY = (p2.y - p1.y);
        final double deltaX = (p2.x - p1.x);

        final double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360d + result) : result;
    }

    private void fillArrow(Paint paint, Canvas canvas, float x0, float y0, float x1, float y1) {

        paint.setStyle(Paint.Style.STROKE);

        int arrowHeadLenght = 30;
        int arrowHeadAngle = 45;
        float[] linePts = new float[] {x1 - arrowHeadLenght, y1, x1, y1};
        float[] linePts2 = new float[] {x1, y1, x1, y1 + arrowHeadLenght};
        Matrix rotateMat = new Matrix();

        //get the center of the line
        float centerX = x1;
        float centerY = y1;

        //set the angle
        double angle = Math.atan2(y1 - y0, x1 - x0) * 180 / Math.PI + arrowHeadAngle;

        //rotate the matrix around the center
        rotateMat.setRotate((float) angle, centerX, centerY);
        rotateMat.mapPoints(linePts);
        rotateMat.mapPoints(linePts2);

        canvas.drawLine(linePts [0], linePts [1], linePts [2], linePts [3], paint);
        canvas.drawLine(linePts2 [0], linePts2 [1], linePts2 [2], linePts2 [3], paint);
    }

    public Paint getPaintFill() {
        return paintFill;
    }

    public void setPaintFill(Paint paintFill) {
        this.paintFill = paintFill;
    }

    public Paint getPaintStroke() {
        return paintStroke;
    }

    public void setPaintStroke(Paint paintStroke) {
        this.paintStroke = paintStroke;
    }
}
