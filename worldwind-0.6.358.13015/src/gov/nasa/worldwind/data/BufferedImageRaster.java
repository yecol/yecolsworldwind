/* Copyright (C) 2001, 2008 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.data;

import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.cache.Cacheable;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.util.*;

/**
 * @author dcollins
 * @version $Id: BufferedImageRaster.java 12432 2009-08-10 16:44:42Z tgaskins $
 */
public class BufferedImageRaster implements DataRaster, Cacheable, Disposable
{
    private Sector sector;
    private java.awt.image.BufferedImage bufferedImage;
    private java.awt.Graphics2D g2d;

    public BufferedImageRaster(Sector sector, java.awt.image.BufferedImage bufferedImage)
    {
        if (sector == null)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (bufferedImage == null)
        {
            String message = Logging.getMessage("nullValue.ImageIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.sector = sector;
        this.bufferedImage = bufferedImage;
    }

    public BufferedImageRaster(int width, int height, int transparency, Sector sector)
    {
        if (width < 1)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "width < 1");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (height < 1)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "height < 1");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (sector == null)
        {
            String message = Logging.getMessage("nullValue.SectorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.sector = sector;
        this.bufferedImage = ImageUtil.createCompatibleImage(width, height, transparency);
    }
//
//    public static java.awt.image.BufferedImage toCompatibleImage(java.awt.image.BufferedImage image)
//    {
//        if (image == null)
//        {
//            String message = Logging.getMessage("nullValue.ImageIsNull");
//            Logging.logger().severe(message);
//            throw new IllegalArgumentException(message);
//        }
//
//        if(java.awt.GraphicsEnvironment.isHeadless())
//            return image;
//
//        // If the image is not already compatible, and is within the restrictions on dimension, then convert it
//        // to a compatible image type.
//        if (!isCompatibleImage(image)
//            && (image.getWidth() <= MAX_IMAGE_SIZE_TO_CONVERT)
//            && (image.getHeight() <= MAX_IMAGE_SIZE_TO_CONVERT))
//        {
//            java.awt.image.BufferedImage compatibleImage =
//                createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
//            java.awt.Graphics2D g2d = compatibleImage.createGraphics();
//            g2d.drawImage(image, 0, 0, null);
//            g2d.dispose();
//            return compatibleImage;
//        }
//        // Otherwise return the original image.
//        else
//        {
//            return image;
//        }
//    }
//
//    public static java.awt.image.BufferedImage createCompatibleImage(int width, int height, int transparency)
//    {
//        if (width < 1)
//        {
//            String message = Logging.getMessage("generic.ArgumentOutOfRange", "width < 1");
//            Logging.logger().severe(message);
//            throw new IllegalArgumentException(message);
//        }
//        if (height < 1)
//        {
//            String message = Logging.getMessage("generic.ArgumentOutOfRange", "height < 1");
//            Logging.logger().severe(message);
//            throw new IllegalArgumentException(message);
//        }
//
//        if(java.awt.GraphicsEnvironment.isHeadless())
//        {
//            return new BufferedImage( width, height,
//                ( transparency == Transparency.OPAQUE ) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB
//            );
//        }
//
//        java.awt.GraphicsConfiguration gc = getDefaultGraphicsConfiguration();
//        return gc.createCompatibleImage(width, height, transparency);
//    }
//
//    protected static boolean isCompatibleImage(java.awt.image.BufferedImage image)
//    {
//        if(java.awt.GraphicsEnvironment.isHeadless())
//            return false;
//
//        java.awt.GraphicsConfiguration gc = getDefaultGraphicsConfiguration();
//        java.awt.image.ColorModel gcColorModel = gc.getColorModel(image.getTransparency());
//        return image.getColorModel().equals(gcColorModel);
//    }
//
//    protected static java.awt.GraphicsConfiguration getDefaultGraphicsConfiguration()
//    {
//        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
//        java.awt.GraphicsDevice gd = ge.getDefaultScreenDevice();
//        return gd.getDefaultConfiguration();
//    }

    public int getWidth()
    {
        return this.bufferedImage.getWidth();
    }

    public int getHeight()
    {
        return this.bufferedImage.getHeight();
    }

    public Sector getSector()
    {
        return this.sector;
    }

    public java.awt.image.BufferedImage getBufferedImage()
    {
        return this.bufferedImage;
    }

    public java.awt.Graphics2D getGraphics()
    {
        if (this.g2d == null)
        {
            this.g2d = this.bufferedImage.createGraphics();
            // Enable bilinear interpolation.
            this.g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        return g2d;
    }

    public void drawOnCanvas(DataRaster canvas, Sector clipSector)
    {
        if (canvas == null)
        {
            String message = Logging.getMessage("nullValue.DestinationIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (!(canvas instanceof BufferedImageRaster))
        {
            String message = Logging.getMessage("DataRaster.IncompatibleRaster", canvas);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.doDrawOnCanvas((BufferedImageRaster) canvas, clipSector);
    }

    public void drawOnCanvas(DataRaster canvas)
    {
        if (canvas == null)
        {
            String message = Logging.getMessage("nullValue.DestinationIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.drawOnCanvas(canvas, null);
    }

    public void fill(java.awt.Color color)
    {
        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        java.awt.Graphics2D g2d = this.getGraphics();

        // Keep track of the previous color.
        java.awt.Color prevColor = g2d.getColor();
        try
        {
            // Fill the raster with the specified color.
            g2d.setColor(color);
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        finally
        {
            // Restore the previous color.
            g2d.setColor(prevColor);
        }
    }

    public long getSizeInBytes()
    {
        long size = 0L;
        java.awt.image.Raster raster = this.bufferedImage.getRaster();
        if (raster != null)
        {
            java.awt.image.DataBuffer db = raster.getDataBuffer();
            if (db != null)
                size = sizeOfDataBuffer(db);
        }
        return size;
    }

    public void dispose()
    {
        if (this.g2d != null)
        {
            this.g2d.dispose();
            this.g2d = null;
        }
    }

    protected void doDrawOnCanvas(BufferedImageRaster canvas, Sector clipSector)
    {
        if (!this.getSector().intersects(canvas.getSector()))
            return;

        java.awt.Graphics2D g2d = canvas.getGraphics();

        // Keep track of the previous clip, composite, and transform.
        java.awt.Shape prevClip = g2d.getClip();
        java.awt.Composite prevComposite = g2d.getComposite();
        java.awt.geom.AffineTransform prevTransform = g2d.getTransform();
        try
        {
            // Compute the region of the destination raster to be be clipped by the specified clipping sector. If no
            // clipping sector is specified, then perform no clipping. We compute the clip region for the destination 
            // raster because this region is used by AWT to limit which pixels are rasterized to the destination.
            if (clipSector != null)
            {
                java.awt.Rectangle clipRect = this.computeClipRect(clipSector, canvas);
                g2d.clipRect(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
            }

            // Set the alpha composite for appropriate alpha blending.
            g2d.setComposite(java.awt.AlphaComposite.SrcOver);

            // Apply the transform that correctly maps the image onto the canvas.
            java.awt.geom.AffineTransform transform = this.computeSourceToDestTransform(
                this.getWidth(), this.getHeight(), this.getSector(),
                canvas.getWidth(), canvas.getHeight(), canvas.getSector());
            g2d.setTransform(transform);

            // Render the image onto the canvas.
            g2d.drawImage(this.getBufferedImage(), 0, 0, null);
        }
        finally
        {
            // Restore the previous clip, composite, and transform.
            g2d.setClip(prevClip);
            g2d.setComposite(prevComposite);
            g2d.setTransform(prevTransform);
        }
    }

    protected java.awt.geom.AffineTransform computeSourceToDestTransform(
        int sourceWidth, int sourceHeight, Sector sourceSector,
        int destWidth, int destHeight, Sector destSector)
    {
        // Compute the the transform from source to destination coordinates. In this computation a pixel is assumed
        // to cover a finite area.

        double ty = destHeight * -(sourceSector.getMaxLatitude().degrees - destSector.getMaxLatitude().degrees)
            / destSector.getDeltaLatDegrees();
        double tx = destWidth * (sourceSector.getMinLongitude().degrees - destSector.getMinLongitude().degrees)
            / destSector.getDeltaLonDegrees();

        double sy = ((double) destHeight / (double) sourceHeight)
            * (sourceSector.getDeltaLatDegrees() / destSector.getDeltaLatDegrees());
        double sx = ((double) destWidth / (double) sourceWidth)
            * (sourceSector.getDeltaLonDegrees() / destSector.getDeltaLonDegrees());

        java.awt.geom.AffineTransform transform = new java.awt.geom.AffineTransform();
        transform.translate(tx, ty);
        transform.scale(sx, sy);
        return transform;
    }

    protected java.awt.geom.AffineTransform computeGeographicToRasterTransform(int width, int height, Sector sector)
    {
        // Compute the the transform from geographic to raster coordinates. In this computation a pixel is assumed
        // to cover a finite area.

        double ty = -sector.getMaxLatitude().degrees;
        double tx = -sector.getMinLongitude().degrees;

        double sy = -(height / sector.getDeltaLatDegrees());
        double sx = (width / sector.getDeltaLonDegrees());

        java.awt.geom.AffineTransform transform = new java.awt.geom.AffineTransform();
        transform.scale(sx, sy);
        transform.translate(tx, ty);
        return transform;
    }

    protected java.awt.Rectangle computeClipRect(Sector clipSector, DataRaster clippedRaster)
    {
        java.awt.geom.AffineTransform geographicToRaster = this.computeGeographicToRasterTransform(
            clippedRaster.getWidth(), clippedRaster.getHeight(), clippedRaster.getSector());

        java.awt.geom.Point2D geoPoint = new java.awt.geom.Point2D.Double();
        java.awt.geom.Point2D ul = new java.awt.geom.Point2D.Double();
        java.awt.geom.Point2D lr = new java.awt.geom.Point2D.Double();

        geoPoint.setLocation(clipSector.getMinLongitude().degrees, clipSector.getMaxLatitude().degrees);
        geographicToRaster.transform(geoPoint, ul);

        geoPoint.setLocation(clipSector.getMaxLongitude().degrees, clipSector.getMinLatitude().degrees);
        geographicToRaster.transform(geoPoint, lr);

        int x = (int) Math.floor(ul.getX());
        int y = (int) Math.floor(ul.getY());
        int width = (int) Math.ceil(lr.getX() - ul.getX());
        int height = (int) Math.ceil(lr.getY() - ul.getY());

        return new java.awt.Rectangle(x, y, width, height);
    }
    
    private static long sizeOfDataBuffer(java.awt.image.DataBuffer dataBuffer)
    {
        return sizeOfElement(dataBuffer.getDataType()) * dataBuffer.getSize();
    }

    private static long sizeOfElement(int dataType)
    {
        switch (dataType)
        {
            case java.awt.image.DataBuffer.TYPE_BYTE:
                return (Byte.SIZE / 8);
            case java.awt.image.DataBuffer.TYPE_DOUBLE:
                return (Double.SIZE / 8);
            case java.awt.image.DataBuffer.TYPE_FLOAT:
                return (Float.SIZE / 8);
            case java.awt.image.DataBuffer.TYPE_INT:
                return (Integer.SIZE / 8);
            case java.awt.image.DataBuffer.TYPE_SHORT:
            case java.awt.image.DataBuffer.TYPE_USHORT:
                return (Short.SIZE / 8);
            case java.awt.image.DataBuffer.TYPE_UNDEFINED:
                break;
        }
        return 0L;
    }
}
