package org.osmdroid.bonuspack.overlays;

import android.graphics.Canvas;
import android.util.Log;

import org.osmdroid.ResourceProxy;
import org.osmdroid.views.MapView;

/**
 * Created by coutinho on 18/05/16.
 */
public class MarkerOptimized extends Marker {
    public MarkerOptimized(MapView mapView) {
        super(mapView);
    }

    public MarkerOptimized(MapView mapView, ResourceProxy resourceProxy) {
        super(mapView, resourceProxy);
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        //There is no reason to draw it in canvas if it's outside the map view.
        if(mapView.getBoundingBox().contains(mPosition)) {
            super.draw(canvas, mapView, shadow);
        }
    }
}
