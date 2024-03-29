/*
Copyright (C) 2001, 2008 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.instances;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.layers.MarkerLayer;
import gov.nasa.worldwind.render.markers.*;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.event.SelectEvent;

import java.util.ArrayList;

/**
 * @author tag
 * @version $Id: Markers.java 7671 2008-11-17 00:18:14Z tgaskins $
 */
public class Markers extends ApplicationTemplate
{
    private static class AppFrame extends ApplicationTemplate.AppFrame
    {
        private static final MarkerAttributes[] attrs = new BasicMarkerAttributes[]
            {
                new BasicMarkerAttributes(Material.BLACK, BasicMarkerShape.SPHERE, 1d, 10, 5),
                new BasicMarkerAttributes(Material.DARK_GRAY, BasicMarkerShape.CONE, 1d, 10, 5),
                new BasicMarkerAttributes(Material.LIGHT_GRAY, BasicMarkerShape.CYLINDER, 1d, 10, 5),
                new BasicMarkerAttributes(Material.GRAY, BasicMarkerShape.HEADING_ARROW, 1d, 10, 5),
                new BasicMarkerAttributes(Material.WHITE, BasicMarkerShape.HEADING_LINE, 1d, 10, 5),
                new BasicMarkerAttributes(Material.RED, BasicMarkerShape.ORIENTED_CONE_LINE, 0.7),
                new BasicMarkerAttributes(Material.YELLOW, BasicMarkerShape.ORIENTED_CYLINDER_LINE, 0.9),
                new BasicMarkerAttributes(Material.CYAN, BasicMarkerShape.ORIENTED_SPHERE_LINE, 0.7),
                new BasicMarkerAttributes(Material.GREEN, BasicMarkerShape.ORIENTED_CONE, 1d),
                new BasicMarkerAttributes(Material.PINK, BasicMarkerShape.ORIENTED_SPHERE, 0.8),
                new BasicMarkerAttributes(Material.BLUE, BasicMarkerShape.ORIENTED_CYLINDER, 0.6),
            };

        static
        {
            for (MarkerAttributes attr : attrs)
            {
                String shapeType = attr.getShapeType();
                //noinspection StringEquality
                if (shapeType == BasicMarkerShape.ORIENTED_SPHERE)
                    attr.setHeadingMaterial(Material.YELLOW);
                //noinspection StringEquality
                if (shapeType == BasicMarkerShape.ORIENTED_CONE)
                    attr.setHeadingMaterial(Material.PINK);
            }
        }

        private Marker lastHighlit;
        private BasicMarkerAttributes lastAttrs;

        public AppFrame()
        {
            super(true, true, false);

            double minLat = 20, maxLat = 60, latDelta = 2;
            double minLon = -140, maxLon = -60, lonDelta = 2;

            ArrayList<Marker> markers = new ArrayList<Marker>();
            for (double lat = minLat; lat <= maxLat; lat += latDelta)
            {
                for (double lon = minLon; lon <= maxLon; lon += lonDelta)
                {
                    Marker marker = new BasicMarker(Position.fromDegrees(lat, lon, 0),
                        attrs[(int) (Math.abs(lat) + Math.abs(lon)) % attrs.length]);
                    marker.setPosition(Position.fromDegrees(lat, lon, 0));
                    marker.setHeading(Angle.fromDegrees(lat * 5));
                    markers.add(marker);
                }
            }

            final MarkerLayer layer = new MarkerLayer();
            layer.setOverrideMarkerElevation(true);
            layer.setKeepSeparated(false);
            layer.setElevation(1000d);
            layer.setMarkers(markers);
            insertBeforePlacenames(this.getWwd(), layer);
            this.getLayerPanel().update(this.getWwd());

            this.getWwd().addSelectListener(new SelectListener()
            {
                public void selected(SelectEvent event)
                {
                    if (lastHighlit != null
                        && (event.getTopObject() == null || !event.getTopObject().equals(lastHighlit)))
                    {
                        lastHighlit.setAttributes(lastAttrs);
                        lastHighlit = null;
                    }

                    if (!event.getEventAction().equals(SelectEvent.ROLLOVER))
                        return;

                    if (event.getTopObject() == null || event.getTopPickedObject().getParentLayer() == null)
                        return;

                    if (event.getTopPickedObject().getParentLayer() != layer)
                        return;

                    if (lastHighlit == null && event.getTopObject() instanceof Marker)
                    {
                        lastHighlit = (Marker) event.getTopObject();
                        lastAttrs = (BasicMarkerAttributes) lastHighlit.getAttributes();
                        MarkerAttributes highliteAttrs = new BasicMarkerAttributes(lastAttrs);
                        highliteAttrs.setMaterial(Material.WHITE);
                        highliteAttrs.setOpacity(1d);
                        highliteAttrs.setMarkerPixels(lastAttrs.getMarkerPixels() * 1.4);
                        highliteAttrs.setMinMarkerSize(lastAttrs.getMinMarkerSize() * 1.4);
                        lastHighlit.setAttributes(highliteAttrs);
                    }
                }
            });
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Markers", AppFrame.class);
    }
}
