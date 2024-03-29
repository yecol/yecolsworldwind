/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.instances;

import gov.nasa.worldwind.formats.tiff.GeotiffImageReaderSpi;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;

import javax.imageio.spi.IIORegistry;
import java.awt.*;
import java.util.*;

/**
 * @author tag
 * @version $Id$
 */
public class SurfaceImages extends ApplicationTemplate
{
    static
    {
        IIORegistry reg = IIORegistry.getDefaultInstance();
        reg.registerServiceProvider(GeotiffImageReaderSpi.inst());
    }
    private static final String WWJ_SPLASH_PATH = "images/400x230-splash-nww.png";
    private static final String GEORSS_ICON_PATH = "images/georss.png";
    private static final String TEST_PATTERN = "testData/star-chart-bars144-600dpi.jpg";

    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            try
            {
                SurfaceImage si1 = new SurfaceImage(GEORSS_ICON_PATH, new ArrayList<LatLon>(Arrays.asList(
                    LatLon.fromDegrees(20d, -115d),
                    LatLon.fromDegrees(20d, -105d),
                    LatLon.fromDegrees(32d, -102d),
                    LatLon.fromDegrees(30d, -115d)
                    )));
                SurfaceImage si2 = new SurfaceImage(TEST_PATTERN, new ArrayList<LatLon>(Arrays.asList(
                    LatLon.fromDegrees(37.8677, -105.1668),
                    LatLon.fromDegrees(37.8677, -104.8332),
                    LatLon.fromDegrees(38.1321, -104.8326),
                    LatLon.fromDegrees(38.1321, -105.1674)
                    )));
                Polyline boundary = new Polyline(si1.getCorners(), 0);
                boundary.setFollowTerrain(true);
                boundary.setClosed(true);
                boundary.setPathType(Polyline.RHUMB_LINE);
                boundary.setColor(new Color(0, 255, 0));

                Polyline boundary2 = new Polyline(si2.getCorners(), 0);
                boundary2.setFollowTerrain(true);
                boundary2.setClosed(true);
                boundary2.setPathType(Polyline.RHUMB_LINE);
                boundary2.setColor(new Color(0, 255, 0));

                RenderableLayer layer = new RenderableLayer();
                layer.setName("Surface Images");
                layer.setPickEnabled(false);
                layer.addRenderable(si1);
                layer.addRenderable(si2);
                layer.addRenderable(boundary);
                layer.addRenderable(boundary2);

                insertBeforeCompass(this.getWwd(), layer);

                this.getLayerPanel().update(this.getWwd());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Surface Images", SurfaceImages.AppFrame.class);
    }
}
