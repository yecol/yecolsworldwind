/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.render;

import com.sun.opengl.util.texture.TextureCoords;
import gov.nasa.worldwind.Movable;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;
import java.awt.*;
import java.awt.geom.*;
import java.util.Arrays;

/**
 * Renders an icon image over the terrain surface.
 *
 * @author Patrick Murris
 * @version $Id: SurfaceIcon.java 12721 2009-10-14 19:57:40Z tgaskins $
 */
public class SurfaceIcon extends AbstractSurfaceRenderable implements Movable
{
    protected static final int DEFAULT_CACHE_CAPACITY = 16;

    private Object imageSource;
    private boolean useMipMaps = true;
    private LatLon location;
    private Vec4 locationOffset;                    // Pixels
    private double scale = 1d;
    private Angle heading = Angle.ZERO;             // CW from north
    private Color color = Color.WHITE;
    private boolean maintainSize = false;
    private double maxSize = Double.MAX_VALUE;      // Meter
    private double minSize = .1;                    // Meter
    protected BoundedHashMap<Object, CacheEntry<Iterable<? extends Sector>>> sectorCache;

    protected WWTexture texture;
    protected int imageWidth = 32;
    protected int imageHeight = 32;

    
    public SurfaceIcon(Object imageSource)
    {
        this(imageSource, null);
    }

    public SurfaceIcon(Object imageSource, LatLon location)
    {
        this.setImageSource(imageSource);
        if (location != null)
            this.setLocation(location);

        this.sectorCache = new BoundedHashMap<Object, CacheEntry<Iterable<? extends Sector>>>(DEFAULT_CACHE_CAPACITY,
            true);
    }

    /**
     * Get the icon reference location on the globe.
     *
     * @return the icon reference location on the globe.
     */
    public LatLon getLocation()
    {
        return this.location;
    }

    /**
     * Set the icon reference location on the globe.
     *
     * @param location the icon reference location on the globe.
     * @throws IllegalArgumentException if location is <code>null</code>.
     */
    public void setLocation(LatLon location)
    {
        if (location == null)
        {
            String message = Logging.getMessage("nullValue.LatLonIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.location = location;
        this.updateModifiedTime();
    }

    /**
     * Get the icon displacement in pixels relative to the reference location.  Can be <code>null</code>.
     * <p>
     * When <code>null</code> the icon will be drawn with it's image center on top of it's refence location -
     * see {@link #setLocation(LatLon)}. Otherwise the icon will be shifted of a distance equivalent to the number
     * of pixels specified as <code>x</code> and <code>y</code> offset values. Positive values will move the icon
     * to the right for <code>x</code> and up for <code>y</code>. Negative values will have the opposite effect.
     *
     * @return the icon displacement in pixels relative to the reference location.
     */
    public Vec4 getLocationOffset()
    {
        return this.locationOffset;
    }

    /**
     * Set the icon displacement in pixels relative to the reference location. Can be <code>null</code>.
     * <p>
     * When <code>null</code> the icon will be drawn with it's image center on top of it's refence location -
     * see {@link #setLocation(LatLon)}. Otherwise the icon will be shifted of a distance equivalent to the number
     * of pixels specified as <code>x</code> and <code>y</code> offset values. Positive values will move the icon
     * to the right for <code>x</code> and up for <code>y</code>. Negative values will have the opposite effect.
     *
     * @param locationOffset the icon displacement in pixels relative to the reference location.
     */
    public void setLocationOffset(Vec4 locationOffset)
    {
        this.locationOffset = locationOffset; // can be null
        this.updateModifiedTime();
    }

    /**
     * Get the source for the icon image. Can be a file path to a local image or
     * a {@link java.awt.image.BufferedImage} reference.
     *
     * @return the source for the icon image.
     */
    public Object getImageSource()
    {
        return this.imageSource;
    }

    /**
     * Set the source for the icon image. Can be a file path to a local image or
     * a {@link java.awt.image.BufferedImage} reference.
     *
     * @param imageSource the source for the icon image.
     * @throws IllegalArgumentException if imageSource is <code>null</code>.
     */
    public void setImageSource(Object imageSource)
    {
        if (imageSource == null)
        {
            String message = Logging.getMessage("nullValue.ImageSource");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.imageSource = imageSource;
        this.texture = null;
        this.updateModifiedTime();
    }

    /**
     * Returns whether the icon will apply mip-map filtering to it's source image. If <code>true</code> the icon image
     * is drawn using mip-maps. If <code>false</code> the icon is drawn without mip-maps, resulting in aliasing if the
     * icon image is drawn smaller than it's native size in pixels.
     *
     * @return <code>true</code> if the icon image is drawn with mip-map filtering; <code>false</code> otherwise.
     */
    public boolean isUseMipMaps()
    {
        return this.useMipMaps;
    }

    /**
     * Sets whether the icon will apply mip-map filtering to it's source image. If <code>true</code> the icon image
     * is drawn using mip-maps. If <code>false</code> the icon is drawn without mip-maps, resulting in aliasing if the
     * icon image is drawn smaller than it's native size in pixels.
     *
     * @param useMipMaps <code>true</code> if the icon image should be drawn with mip-map filtering; <code>false</code>
     *                   otherwise.
     */
    public void setUseMipMaps(boolean useMipMaps)
    {
        this.useMipMaps = useMipMaps;
        this.texture = null;
        this.updateModifiedTime();
    }

    /**
     * Get the current scaling factor applied to the source image.
     *
     * @return the current scaling factor applied to the source image.
     */
    public double getScale()
    {
        return this.scale;
    }

    /**
     * Set the scaling factor to apply to the source image. A value of <code>1</code> will produce no change,
     * a value greater then <code>1</code> will enlarge the image and a value smaller then <code>1</code> will
     * reduce it.
     *
     * @param scale the scaling factor to apply to the source image.
     * @throws IllegalArgumentException if scale is zero or negative.
     */
    public void setScale(double scale)
    {
        if (scale <= 0)
        {
            String message = Logging.getMessage("generic.ArgumentOutOfRange", "scale must be greater then zero");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        this.scale = scale;
        this.updateModifiedTime();
    }

    /**
     * Get the current heading {@link Angle}, clockwise from North or <code>null</code>.
     *
     * @return the current heading {@link Angle}, clockwise from North or <code>null</code>.
     */
    public Angle getHeading()
    {
        return this.heading;
    }

    /**
     * Set the heading {@link Angle}, clockwise from North. Setting this value to <code>null</code> will have
     * the icon follow the view heading so as to always face the eye. The icon will rotate around it's reference
     * location.
     *
     * @param heading the heading {@link Angle}, clockwise from North or <code>null</code>.
     */
    public void setHeading(Angle heading)
    {
        this.heading = heading;  // can be null
        this.updateModifiedTime();
    }

    /**
     * Determines whether the icon constantly maintains it's apparent size. If <code>true</code> the icon is
     * constantly redrawn at the proper size depending on it's distance from the eye. If <code>false</code> the
     * icon will be drawn only once per level of the underlying tile pyramid. Thus it's apparent size will vary up
     * to twice it's 'normal' dimension in between levels.
     *
     * @return <code>true</code> if the icon constantly maintains it's apparent size.
     */
    public boolean isMaintainSize()
    {
        return this.maintainSize;
    }

    /**
     * Sets whether the icon constantly maintains it's apparent size. If <code>true</code> the icon is
     * constantly redrawn at the proper size depending on it's distance from the eye. If <code>false</code> the
     * icon will be drawn only once per level of the underlying tile pyramid. Thus it's apparent size will vary up
     * to twice it's 'normal' dimension in between levels.
     *
     * @param state <code>true</code> if the icon should constantly maintains it's apparent size.
     */
    public void setMaintainSize(boolean state)
    {
        this.maintainSize = state;
    }

    /**
     * Get the minimum size in meter the icon image is allowed to be reduced to once applied to the terrain surface.
     * This limit applies to the source image largest dimension.
     * <p>
     * The icon will try to maintain it's apparent size depending on it's distance from the eye and will extend
     * over a rectangular area which largest dimension is bounded by the values provided with
     * {@link #setMinSize(double)} and {@link #setMaxSize(double)}.
     *
     * @return the minimum size of the icon in meter.
     */
    public double getMinSize()
    {
        return this.minSize;
    }

    /**
     * Set the minimum size in meter the icon image is allowed to be reduced to once applied to the terrain surface.
     * This limit applies to the source image largest dimension.
     * <p>
     * The icon will try to maintain it's apparent size depending on it's distance from the eye and will extend
     * over a rectangular area which largest dimension is bounded by the values provided with
     * {@link #setMinSize(double)} and {@link #setMaxSize(double)}.
     *
     * @param sizeInMeter the minimum size of the icon in meter.
     */
    public void setMinSize(double sizeInMeter)
    {
        this.minSize = sizeInMeter;
        this.updateModifiedTime();
    }

    /**
     * Get the maximum size in meter the icon image is allowed to be enlarged to once applied to the terrain surface.
     * This limit applies to the source image largest dimension.
     * <p>
     * The icon will try to maintain it's apparent size depending on it's distance from the eye and will extend
     * over a rectangular area which largest dimension is bounded by the values provided with
     * {@link #setMinSize(double)} and {@link #setMaxSize(double)}.
     *
     * @return the maximum size of the icon in meter.
     */
    public double getMaxSize()
    {
        return this.maxSize;
    }

    /**
     * Get the maximum size in meter the icon image is allowed to be enlarged to once applied to the terrain surface.
     * This limit applies to the source image largest dimension.
     * <p>
     * The icon will try to maintain it's apparent size depending on it's distance from the eye and will extend
     * over a rectangular area which largest dimension is bounded by the values provided with
     * {@link #setMinSize(double)} and {@link #setMaxSize(double)}.
     *
     * @param sizeInMeter the maximum size of the icon in meter.
     */
    public void setMaxSize(double sizeInMeter)
    {
        this.maxSize = sizeInMeter;
        this.updateModifiedTime();
    }

    /**
     * Get the {@link Color} the source image is combined with.
     *
     * @return the {@link Color} the source image is combined with.
     */
    public Color getColor()
    {
        return this.color;
    }

    /**
     * Set the {@link Color} the source image will be combined with - default to white.
     * <p>
     * A non white color will mostly affect the white portions from the original image. This is mostly useful
     * to alter the appearance of 'colorless' icons - which mainly contain black, white and shades of gray.
     *
     * @param color the {@link Color} the source image will be combined with.
     * @throws IllegalArgumentException if color is <code>null</code>.
     */
    public void setColor(Color color)
    {
        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        this.color = color;
        this.updateModifiedTime();
    }

    protected boolean isMaintainAppearance()
    {
        return this.getHeading() == null || this.isMaintainSize();  // always facing or constant size
    }

    // *** SurfaceObject interface ***

    public long getLastModifiedTime()
    {
        if (this.isMaintainAppearance())
            return System.currentTimeMillis();  // Refresh all the time if maintain appearance

        return super.getLastModifiedTime();
    }

    public Iterable<? extends Sector> getSectors(DrawContext dc, double texelSizeRadians)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (this.location == null)
            return null;

        if (this.isMaintainAppearance())
            return Arrays.asList(this.computeSector(dc, this.location));

        // If the icon does not redraw all the time, then cache it's sector, using texelSize and last modified time as
        // keys which uniquely identify the icon's sector..
        CacheEntry<Iterable<? extends Sector>> entry = this.sectorCache.get(texelSizeRadians);

        if (entry != null && entry.getValue() != null && entry.getLastModifiedTime() >= this.getLastModifiedTime())
            return entry.getValue();

        Iterable<? extends Sector> sectors = Arrays.asList(this.computeSector(dc, this.location));

        this.sectorCache.put(texelSizeRadians,
            new CacheEntry<Iterable<? extends Sector>>(sectors, this.getLastModifiedTime()));
        return sectors;
    }

    public void doRenderToRegion(DrawContext dc, Sector sector, int x, int y, int width, int height)
    {
        WWTexture texture = getTexture();
        if (texture == null)
            return;

        this.beginDraw(dc);
        try
        {
            if (texture.bind(dc))
            {
                // Update image width and height
                this.imageWidth = texture.getWidth(dc);
                this.imageHeight = texture.getHeight(dc);

                // Apply texture local transorm
                dc.getGL().glMatrixMode(GL.GL_TEXTURE);
                this.getTexture().applyInternalTransform(dc);
                
                // Apply draw color
                this.applyDrawColor(dc);

                //Draw
                this.drawIcon(dc, sector, x, y, width, height);
            }
        }
        catch (Exception e)
        {
            // TODO: log error
        }
        finally
        {
            // Restore gl state
            this.endDraw(dc);
        }
    }

    protected Sector computeSector(DrawContext dc, LatLon location)
    {
        Globe globe = dc.getGlobe();
        // Compute real world icon extent depending on distance from eye
        Rectangle2D.Double rect = computeDrawDimension(dc, location); // meter
        // If the icon does not redraw all the time, double it's dimension
        if (!this.isMaintainAppearance())
        {
            rect.setRect(rect.x,  rect.y,  rect.width * 2, rect.height * 2);
        }
        // Compute bounding sector and apply location offset to it
        double dLatRadians = rect.height / globe.getRadius();
        double dLonRadians = rect.width / globe.getRadius() / location.getLatitude().cos();
        double offsetLatRadians = locationOffset != null ? locationOffset.y * dLatRadians / this.imageHeight : 0;
        double offsetLonRadians = locationOffset != null ? locationOffset.x * dLonRadians / this.imageWidth : 0;
        Sector sector = new Sector(
            location.getLatitude().subtractRadians(dLatRadians / 2).addRadians(offsetLatRadians),
            location.getLatitude().addRadians(dLatRadians / 2).addRadians(offsetLatRadians),
            location.getLongitude().subtractRadians(dLonRadians / 2).addRadians(offsetLonRadians),
            location.getLongitude().addRadians(dLonRadians / 2).addRadians(offsetLonRadians)
        );
        // Rotate sector around location 
        return computeRotatedSectorBounds(sector, location, computeDrawHeading(dc));
    }

    protected Rectangle2D.Double computeDrawDimension(DrawContext dc, LatLon location)
    {
        // Compute icon extent at 1:1 depending on distance from eye
        double pixelSize = computePixelSizeAtLocation(dc, location);
        return computeDrawDimension(pixelSize);
    }

    protected Rectangle2D.Double computeDrawDimension(double pixelSize)
    {
        // Compute icon extent at 1:1 depending on target tile pixel size
        double height = this.imageHeight * this.scale * pixelSize;
        double width = this.imageWidth * this.scale * pixelSize;
        // Clamp to size range
        double size = height > width ? height : width;
        double scale = size > this.maxSize ? this.maxSize / size : size < this.minSize ? this.minSize / size : 1;

        return new Rectangle2D.Double(0, 0, width * scale, height * scale); // meter
    }

    protected Angle computeDrawHeading(DrawContext dc)
    {
        if (this.heading != null)
            return this.heading;

        return getViewHeading(dc);
    }

    protected void beginDraw(DrawContext dc)
    {
        GL gl = dc.getGL();

        int attributeMask = GL.GL_TRANSFORM_BIT // for modelview
                | GL.GL_CURRENT_BIT // for current color
                | GL.GL_COLOR_BUFFER_BIT // for alpha test func and ref, and blend
                | GL.GL_TEXTURE_BIT // for texture env
                | GL.GL_ENABLE_BIT; // for enable/disable changes
        gl.glPushAttrib(attributeMask);

        // Suppress any fully transparent image pixels
        gl.glEnable(GL.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0.001f);

        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glPushMatrix();

        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPushMatrix();

        if (dc.isPickingMode())
        {
            this.pickSupport.beginPicking(dc);

            // Set up to replace the non-transparent texture colors with the single pick color.
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_COMBINE);
            gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_SRC0_RGB, GL.GL_PREVIOUS);
            gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_COMBINE_RGB, GL.GL_REPLACE);
        }
        else
        {
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
        }
    }

    protected void endDraw(DrawContext dc)
    {
        if (dc.isPickingMode())
            this.pickSupport.endPicking(dc);

        GL gl = dc.getGL();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glPopMatrix();

        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glPopMatrix();

        gl.glPopAttrib();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void applyDrawTransform(DrawContext dc, Sector sector, int x, int y, int width, int height,
        LatLon location, double drawScale, Matrix geoTransform)
    {
        // Compute icon viewport point
        Vec4 point = new Vec4(location.getLongitude().degrees, location.getLatitude().degrees, 1);
        point = point.transformBy4(geoTransform);

        GL gl = dc.getGL();
        // Translate to location point
        gl.glTranslated(point.x(), point.y(), point.z());
        // Add x scaling transform to maintain icon width and aspect ratio at any latitude
        gl.glScaled(drawScale / location.getLatitude().cos(), drawScale, 1);
        // Add rotation to account for icon heading
        gl.glRotated(this.computeDrawHeading(dc).degrees, 0, 0, -1);
        // Translate to lower left corner
        gl.glTranslated(-this.imageWidth / 2, -this.imageHeight / 2, 0);
        // Apply location offset if any
        if (this.locationOffset != null)
            gl.glTranslated(this.locationOffset.x, this.locationOffset.y, 0);
    }


    protected double computeDrawScale(DrawContext dc, Sector sector, int width, int height, LatLon location)
    {
        // Compute scaling to maintain apparent size
        double drawPixelSize;
        double regionPixelSize = this.computeDrawPixelSize(dc, sector, width, height);
        if (this.isMaintainAppearance())
            // Compute precise size depending on eye distance
            drawPixelSize = this.computeDrawDimension(dc, location).width / this.imageWidth;
        else
            // Compute size according to draw tile resolution
            drawPixelSize = this.computeDrawDimension(regionPixelSize).width / this.imageWidth;
        return drawPixelSize / regionPixelSize;
    }

    protected void applyDrawColor(DrawContext dc)
    {
        if (!dc.isPickingMode())
            applyPremultipliedAlphaColor(dc.getGL(), this.color, getOpacity());
    }

    protected void drawIcon(DrawContext dc, Sector sector, int x, int y, int width, int height)
    {
        GL gl = dc.getGL();
        gl.glMatrixMode(GL.GL_MODELVIEW);
        double drawScale = this.computeDrawScale(dc, sector, width, height, this.location);
        Matrix geoTransform = Matrix.fromGeographicToViewport(sector, x, y, width, height);
        this.applyDrawTransform(dc, sector, x, y, width, height, this.location, drawScale, geoTransform);
        gl.glScaled(this.imageWidth, this.imageHeight, 1d);
        dc.drawUnitQuad(new TextureCoords(0, 0, 1, 1));
    }

    protected WWTexture getTexture()
    {
        if (this.texture == null)
            this.texture = new BasicWWTexture(this.imageSource, this.useMipMaps);

        return this.texture;
    }

    // *** Movable interface

    public Position getReferencePosition()
    {
        return new Position(this.location, 0);
    }

    public void move(Position delta)
    {
        if (delta == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.moveTo(this.getReferencePosition().add(delta));
    }

    public void moveTo(Position position)
    {
        if (position == null)
        {
            String msg = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.setLocation(position.getLatLon());
    }

    protected static class CacheEntry<T>
    {
        protected final T value;
        protected final long lastModifiedTime;

        public CacheEntry(T value, long lastModifiedTime)
        {
            this.value = value;
            this.lastModifiedTime = lastModifiedTime;
        }

        public T getValue()
        {
            return this.value;
        }

        public final long getLastModifiedTime()
        {
            return this.lastModifiedTime;
        }
    }
}
