/* Copyright (C) 2001, 2009 United States Government as represented by 
the Administrator of the National Aeronautics and Space Administration. 
All Rights Reserved. 
*/
package gov.nasa.worldwind.examples.analytics;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.data.BufferWrapperRaster;
import gov.nasa.worldwind.formats.worldfile.WorldFile;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.instances.ApplicationTemplate;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.text.*;
import java.util.ArrayList;
import java.util.zip.*;

/**
 * @author dcollins
 * @version $Id: AnalyticSurfaceDemo.java 13015 2010-01-18 21:50:50Z tgaskins $
 */
public class AnalyticSurfaceDemo extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        protected static final double HUE_BLUE = 240d / 360d;
        protected static final double HUE_RED = 0d / 360d;
        protected RenderableLayer analyticSurfaceLayer;

        public AppFrame()
        {
            this.initAnalyticSurfaceLayer();
        }

        protected void initAnalyticSurfaceLayer()
        {
            this.analyticSurfaceLayer = new RenderableLayer();
            this.analyticSurfaceLayer.setPickEnabled(false);
            this.analyticSurfaceLayer.setName("Analytic Surfaces");
            insertBeforePlacenames(this.getWwd(), this.analyticSurfaceLayer);
            this.getLayerPanel().update(this.getWwd());

            createRandomAltitudeSurface(HUE_BLUE, HUE_RED, 50, 50, this.analyticSurfaceLayer);

            // Load the static precipitation data. Since it comes over the network, load it in a separate thread to
            // avoid blocking the example if the load is slow or fails.
            Thread t = new Thread(new Runnable()
            {
                public void run()
                {
                    createPrecipitationSurface(HUE_BLUE, HUE_RED, analyticSurfaceLayer);
                }
            });
            t.start();
        }
    }

    protected static Renderable createLegendRenderable(final AnalyticSurface surface, final double surfaceMinScreenSize,
        final AnalyticSurfaceLegend legend)
    {
        return new Renderable()
        {
            public void render(DrawContext dc)
            {
                Extent extent = surface.getExtent(dc);
                if (!extent.intersects(dc.getView().getFrustumInModelCoordinates()))
                    return;

                if (WWMath.computeSizeInWindowCoordinates(dc, extent) < surfaceMinScreenSize)
                    return;

                legend.render(dc);
            }
        };
    }

    //**************************************************************//
    //********************  Random Altitude Surface  ***************//
    //**************************************************************//

    protected static void createRandomAltitudeSurface(double minHue, double maxHue, int width, int height,
        RenderableLayer outLayer)
    {
        double minValue = -200e3;
        double maxValue = 200e3;

        AnalyticSurface surface = new AnalyticSurface();
        surface.setSector(Sector.fromDegrees(25, 35, -90, -80));
        surface.setAltitude(400e3);
        surface.setDimensions(width, height);
        surface.setClientLayer(outLayer);
        outLayer.addRenderable(surface);

        BufferWrapper firstBuffer = randomGridValues(width, height, minValue, maxValue);
        BufferWrapper secondBuffer = randomGridValues(width, height, minValue * 2d, maxValue / 2d);
        mixValuesOverTime(2000L, firstBuffer, secondBuffer, minValue, maxValue, minHue, maxHue, surface);

        AnalyticSurfaceAttributes attr = new AnalyticSurfaceAttributes();
        attr.setShadowOpacity(0.5);
        surface.setSurfaceAttributes(attr);

        final double altitude = surface.getAltitude();
        final double verticalScale = surface.getVerticalScale();
        Format legendLabelFormat = new DecimalFormat("# km")
        {
            public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition)
            {
                double altitudeMeters = altitude + verticalScale * number;
                double altitudeKm = altitudeMeters * WWMath.METERS_TO_KILOMETERS;
                return super.format(altitudeKm, result, fieldPosition);
            }
        };

        AnalyticSurfaceLegend legend = AnalyticSurfaceLegend.fromColorGradient(minValue, maxValue, minHue, maxHue,
            AnalyticSurfaceLegend.createDefaultColorGradientLabels(minValue, maxValue, legendLabelFormat),
            AnalyticSurfaceLegend.createDefaultTitle("Random Altitudes"));
        legend.setOpacity(0.8);
        legend.setScreenLocation(new Point(650, 300));
        outLayer.addRenderable(createLegendRenderable(surface, 300, legend));
    }

    protected static void mixValuesOverTime(
        final long timeToMix,
        final BufferWrapper firstBuffer, final BufferWrapper secondBuffer,
        final double minValue, final double maxValue, final double minHue, final double maxHue,
        final AnalyticSurface surface)
    {
        Timer timer = new Timer(20, new ActionListener()
        {
            private long startTime = -1;

            public void actionPerformed(ActionEvent e)
            {
                if (this.startTime < 0)
                    this.startTime = System.currentTimeMillis();

                double t = (double) (e.getWhen() - this.startTime) / (double) timeToMix;
                int ti = (int) Math.floor(t);

                double a = t - ti;
                if ((ti % 2) == 0)
                    a = 1d - a;

                surface.setValues(createMixedColorGradientGridValues(
                    a, firstBuffer, secondBuffer, minValue, maxValue, minHue, maxHue));

                if (surface.getClientLayer() != null)
                    surface.getClientLayer().firePropertyChange(AVKey.LAYER, null, surface.getClientLayer());
            }
        });
        timer.start();
    }

    public static Iterable<? extends AnalyticSurface.GridPointAttributes> createMixedColorGradientGridValues(double a,
        BufferWrapper firstBuffer, BufferWrapper secondBuffer, double minValue, double maxValue,
        double minHue, double maxHue)
    {
        ArrayList<AnalyticSurface.GridPointAttributes> attributesList
            = new ArrayList<AnalyticSurface.GridPointAttributes>();

        long length = Math.min(firstBuffer.length(), secondBuffer.length());
        for (int i = 0; i < length; i++)
        {
            double value = WWMath.mixSmooth(a, firstBuffer.getDouble(i), secondBuffer.getDouble(i));
            attributesList.add(
                AnalyticSurface.createColorGradientAttributes(value, minValue, maxValue, minHue, maxHue));
        }

        return attributesList;
    }

    //**************************************************************//
    //********************  Precipitation Surface  *****************//
    //**************************************************************//

    protected static void createPrecipitationSurface(double minHue, double maxHue, final RenderableLayer outLayer)
    {
        BufferWrapperRaster raster = loadZippedBILData(
            "http://worldwind.arc.nasa.gov/java/demos/data/wa-precip-24hmam.zip");
        if (raster == null)
            return;

        double[] extremes = WWBufferUtil.computeExtremeValues(raster.getBuffer(), raster.getTransparentValue());
        if (extremes == null)
            return;

        final AnalyticSurface surface = new AnalyticSurface();
        surface.setSector(raster.getSector());
        surface.setDimensions(raster.getWidth(), raster.getHeight());
        surface.setValues(AnalyticSurface.createColorGradientValues(
            raster.getBuffer(), raster.getTransparentValue(), extremes[0], extremes[1], minHue, maxHue));
        surface.setVerticalScale(5e3);

        AnalyticSurfaceAttributes attr = new AnalyticSurfaceAttributes();
        attr.setDrawOutline(false);
        attr.setDrawShadow(false);
        attr.setInteriorOpacity(0.6);
        surface.setSurfaceAttributes(attr);

        Format legendLabelFormat = new DecimalFormat("# ft")
        {
            public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition)
            {
                double valueInFeet = number * WWMath.METERS_TO_FEET;
                return super.format(valueInFeet, result, fieldPosition);
            }
        };

        final AnalyticSurfaceLegend legend = AnalyticSurfaceLegend.fromColorGradient(extremes[0], extremes[1], minHue, maxHue,
            AnalyticSurfaceLegend.createDefaultColorGradientLabels(extremes[0], extremes[1], legendLabelFormat),
            AnalyticSurfaceLegend.createDefaultTitle("Annual Precipitation"));
        legend.setOpacity(0.8);
        legend.setScreenLocation(new Point(100, 300));

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                surface.setClientLayer(outLayer);
                outLayer.addRenderable(surface);
                outLayer.addRenderable(createLegendRenderable(surface, 300, legend));
            }
        });
    }

    protected static BufferWrapperRaster loadZippedBILData(String uriString)
    {
        try
        {
            File zipFile = File.createTempFile("data", ".zip");
            File hdrFile = File.createTempFile("data", ".hdr");
            File blwFile = File.createTempFile("data", ".blw");
            zipFile.deleteOnExit();
            hdrFile.deleteOnExit();
            blwFile.deleteOnExit();

            ByteBuffer byteBuffer = WWIO.readURLContentToBuffer(new URI(uriString).toURL());
            WWIO.saveBuffer(byteBuffer, zipFile);

            ZipFile zip = new ZipFile(zipFile);
            ByteBuffer dataBuffer = unzipEntryToBuffer(zip, "data.bil");
            WWIO.saveBuffer(unzipEntryToBuffer(zip, "data.hdr"), hdrFile);
            WWIO.saveBuffer(unzipEntryToBuffer(zip, "data.blw"), blwFile);
            zip.close();

            AVList params = new AVListImpl();
            WorldFile.decodeWorldFiles(new File[] {hdrFile, blwFile}, params);
            params.setValue(AVKey.DATA_TYPE, params.getValue(AVKey.PIXEL_TYPE));

            Double missingDataSignal = (Double) params.getValue(AVKey.MISSING_DATA_REPLACEMENT);
            if (missingDataSignal == null)
                missingDataSignal = Double.NaN;

            Sector sector = (Sector) params.getValue(AVKey.SECTOR);
            int[] dimensions = (int[]) params.getValue(WorldFile.WORLD_FILE_IMAGE_SIZE);
            BufferWrapper buffer = BufferWrapper.wrap(dataBuffer, params);

            BufferWrapperRaster raster = new BufferWrapperRaster(dimensions[0], dimensions[1], sector, buffer);
            raster.setTransparentValue(missingDataSignal);
            return raster;
        }
        catch (Exception e)
        {
            String message = Logging.getMessage("generic.ExceptionAttemptingToReadFrom", uriString);
            Logging.logger().severe(message);
            return null;
        }
    }

    protected static ByteBuffer unzipEntryToBuffer(ZipFile zipFile, String entryName) throws IOException
    {
        ZipEntry entry = zipFile.getEntry(entryName);
        InputStream is = zipFile.getInputStream(entry);
        return WWIO.readStreamToBuffer(is);
    }

    //**************************************************************//
    //********************  Random Grid Construction  **************//
    //**************************************************************//

    protected static final int DEFAULT_RANDOM_ITERATIONS = 1000;
    protected static final double DEFAULT_RANDOM_SMOOTHING = 0.5d;

    public static BufferWrapper randomGridValues(int width, int height, double min, double max, int numIterations,
        double smoothness, BufferFactory factory)
    {
        int numValues = width * height;
        double[] values = new double[numValues];

        for (int i = 0; i < numIterations; i++)
        {
            double offset = 1d - (i / (double) numIterations);

            int x1 = (int) Math.round(Math.random() * (width - 1));
            int x2 = (int) Math.round(Math.random() * (width - 1));
            int y1 = (int) Math.round(Math.random() * (height - 1));
            int y2 = (int) Math.round(Math.random() * (height - 1));
            int dx1 = x2 - x1;
            int dy1 = y2 - y1;

            for (int y = 0; y < height; y++)
            {
                int dy2 = y - y1;
                for (int x = 0; x < width; x++)
                {
                    int dx2 = x - x1;

                    if ((dx2 * dy1 - dx1 * dy2) >= 0)
                        values[x + y * width] += offset;
                }
            }
        }

        smoothValues(width, height, values, smoothness);
        scaleValues(values, numValues, min, max);
        BufferWrapper buffer = factory.newBuffer(numValues);
        buffer.putDouble(0, values, 0, numValues);

        return buffer;
    }

    public static BufferWrapper randomGridValues(int width, int height, double min, double max)
    {
        return randomGridValues(width, height, min, max, DEFAULT_RANDOM_ITERATIONS, DEFAULT_RANDOM_SMOOTHING,
            new BufferFactory.DoubleBufferFactory());
    }

    protected static void scaleValues(double[] values, int count, double minValue, double maxValue)
    {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < count; i++)
        {
            if (min > values[i])
                min = values[i];
            if (max < values[i])
                max = values[i];
        }

        for (int i = 0; i < count; i++)
        {
            values[i] = (values[i] - min) / (max - min);
            values[i] = minValue + values[i] * (maxValue - minValue);
        }
    }

    protected static void smoothValues(int width, int height, double[] values, double smoothness)
    {
        // top to bottom
        for (int x = 0; x < width; x++)
        {
            smoothBand(values, x, width, height, smoothness);
        }

        // bottom to top
        int lastRowOffset = (height - 1) * width;
        for (int x = 0; x < width; x++)
        {
            smoothBand(values, x + lastRowOffset, -width, height, smoothness);
        }

        // left to right
        for (int y = 0; y < height; y++)
        {
            smoothBand(values, y * width, 1, width, smoothness);
        }

        // right to left
        int lastColOffset = width - 1;
        for (int y = 0; y < height; y++)
        {
            smoothBand(values, lastColOffset + y * width, -1, width, smoothness);
        }
    }

    protected static void smoothBand(double[] values, int start, int stride, int count, double smoothness)
    {
        double prevValue = values[start];
        int j = start + stride;

        for (int i = 0; i < count - 1; i++)
        {
            values[j] = smoothness * prevValue + (1 - smoothness) * values[j];
            prevValue = values[j];
            j += stride;
        }
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("Analytic Surface Demo", AppFrame.class);
    }
}
