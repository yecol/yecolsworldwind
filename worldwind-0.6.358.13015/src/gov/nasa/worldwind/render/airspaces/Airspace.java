/*
Copyright (C) 2001, 2008 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.render.airspaces;

import gov.nasa.worldwind.Restorable;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.render.*;

import java.util.Collection;

/**
 * @author dcollins
 * @version $Id: Airspace.java 12870 2009-12-09 05:24:44Z tgaskins $
 */
public interface Airspace extends Renderable, Restorable, AVList
{
    public static final String DRAW_STYLE_FILL = "Airspace.DrawStyleFill";
    public static final String DRAW_STYLE_OUTLINE = "Airspace.DrawStyleOutline";

    boolean isVisible();

    void setVisible(boolean visible);

    AirspaceAttributes getAttributes();

    void setAttributes(AirspaceAttributes attributes);

    /**
     * Returns the current airspace surface altitudes.
     *
     * @return a two-element array of <code>double</code> with element 0 containing the lower surface altitude, and
     *         element 1 containing the upper surface altitude.
     *
     * @see #setAltitudes(double, double), #setAltitudeDatum, #setGroundReference
     */
    double[] getAltitudes();

    /**
     * Sets the lower and upper airspace surface altitudes. The altitudes are interpreted according to the current
     * altitude datum of the respective surface. See {@link #setAltitudeDatum(String, String)} for a description of the
     * possible interpretations and the means to specify them.
     *
     * @param lowerAltitude the lower surface altitude, in meters.
     * @param upperAltitude the upper surface altitude, in meters.
     *
     * @see #setAltitudes(double, double), #setAltitudeDatum, #setGroundReference
     */
    void setAltitudes(double lowerAltitude, double upperAltitude);

    /**
     * Sets the lower and upper airspace surface altitudes to the same value. The lower and upper altitudes are
     * interpreted according to the current altitude datum of the respective surface. See {@link
     * #setAltitudeDatum(String, String)} for a description of the possible interpretations and the means to specify
     * them.
     *
     * @param altitude the lower surface altitude, in meters.
     *
     * @see #setAltitudes(double, double), #setAltitudeDatum, #setGroundReference
     */
    void setAltitude(double altitude);

    /**
     * Returns the old-style indicators of the airspace's lower and upper surface datums.
     *
     * @return
     *
     */
    boolean[] isTerrainConforming();

    /**
     * Sets the altitude datum, which indicates whether airspace altitudes are relative to mean sea level, ground level
     * or a single ground reference location. The datum is normally set via {@link #setAltitudeDatum(String, String)},
     * but this method is provided for backwards compatibility with the means of originally setting the datum. See the
     * argument descriptions below for the mapping of the boolean values of this method to the altitude-datum values.
     *
     * @param lowerTerrainConformant the lower altitude datum. A value of true indicates a lower altitude datum of
     *                               {@link AVKey#ABOVE_GROUND_LEVEL} (terrain conforming), a value of false indicates a
     *                               lower altitude datum of {link AVKey#ABOVE_MEAN_SEA_LEVEL} (not terrain conforming).
     *                               the terrain-conforming, a value of false indicates that it's not.
     * @param upperTerrainConformant the upper altitude datum. A value of true indicates an upper altitude datum of
     *                               {@link AVKey#ABOVE_GROUND_LEVEL} (terrain conforming), a value of false indicates
     *                               an upper altitude datum of {link AVKey#ABOVE_MEAN_SEA_LEVEL} (not terrain
     *                               conforming. the terrain-conforming, a value of false indicates that it's not.
     *
     * @see #setAltitudeDatum(String, String)
     */
    void setTerrainConforming(boolean lowerTerrainConformant, boolean upperTerrainConformant);

    /**
     * Sets the altitude datum for both the lower and upper airspace surface to the same specified value. The datum is
     * normally set via {@link #setAltitudeDatum(String, String)}, but this method is provided for backwards
     * compatibility with the means of originally setting the datum. See the argument descriptions for the mapping of
     * the boolean values of this method to the altitude-datum values.
     *
     * @param terrainConformant the altitude datum. See {@link #setTerrainConforming(boolean, boolean)} for a
     *                          description of the possible values.
     */
    void setTerrainConforming(boolean terrainConformant);

    boolean isEnableLevelOfDetail();

    void setEnableLevelOfDetail(boolean enableLevelOfDetail);

    Iterable<DetailLevel> getDetailLevels();

    void setDetailLevels(Collection<DetailLevel> detailLevels);

    boolean isAirspaceVisible(DrawContext dc);

    Extent getExtent(DrawContext dc);

    void renderGeometry(DrawContext dc, String drawStyle);

    void renderExtent(DrawContext dc);

    /**
     * Sets the altitude datum, which indicates whether airspace altitudes are relative to mean sea level, ground level
     * or a single ground reference location.
     * <p/>
     * A value of {@link AVKey#ABOVE_MEAN_SEA_LEVEL}, the default for both lower and upper datums, indicates a datum of
     * mean sea level. The respective lower or upper surface of the airspace is drawn at the constant altitude specified
     * by {@link #setAltitude(double)}.
     * <p/>
     * A datum of {@link AVKey#ABOVE_GROUND_LEVEL} indicates that each position of the respective airspace surface is
     * offset vertically from the altitude specified to {@link #setAltitude(double)} by an amount equal to the terrain
     * elevation at that position. For example, if the specified lower altitude is zero, the lower surface lies on and
     * conforms to the terrain. If non-zero, the surface undulates in tandem with the terrain but relative to the
     * specified altitude.
     * <p/>
     * A datum of {@link AVKey#ABOVE_GROUND_REFERENCE} combines both of the above datums. It indicates that the
     * respective surface is drawn at the altitude specified to {@link #setAltitude(double)} but offset vertically by an
     * amount equal to the elevation at a single reference location on the ground. This is useful for displaying
     * surfaces that are "flat" but are positioned relative to the ground. An example is the roof of a building, which
     * maintains a constant altitude even as the base of its building may conform to varying terrain. One method of
     * representing buildings is to specify a lower altitude of 0, a lower altitude datum of {@link
     * AVKey#ABOVE_GROUND_LEVEL}, an upper altitude that's the building's height, and an upper altitude datum of {@link
     * AVKey#ABOVE_GROUND_REFERENCE}, where the ground reference is a location at the building's base. The reference
     * position is specifed by {@link #setGroundReference(LatLon)}.
     *
     * @param lowerAltitudeDatum the lower altitude datum.
     * @param upperAltitudeDatum the upper altitude datum
     *
     * @throws IllegalArgumentException if either the lower or upper altitude datum is null.
     * @see #setGroundReference(gov.nasa.worldwind.geom.LatLon), #setAltitudes
     */
    void setAltitudeDatum(String lowerAltitudeDatum, String upperAltitudeDatum);

    /**
     * Returns the current altitude datum of the airspace's lower and upper surfaces.
     *
     * @return a two-element array containing at position 0 the lower altitude datum, and at position 1 the upper
     *         altitude datum.
     *
     * @see #setAltitudeDatum(String, String)
     */
    String[] getAltitudeDatum();

    /**
     * Sets the reference location used to determine the elevation offset for airspace surfaces whose altitude datum is
     * {@link AVKey#ABOVE_GROUND_REFERENCE}. The reference location is unused if the altitude datum is a value other
     * than this.
     *
     * @param groundReference the location at which to compute the terrain elevation used to offset an upper or lower
     *                        airspace surface. The location need not be within the airspace's bounds. If null, an
     *                        airspace-specific position is chosen from those defining the airspace. See the method
     *                        descriptions for the individual airspaces to determine the position used.
     *
     * @see #setAltitudeDatum(String, String)
     */
    void setGroundReference(LatLon groundReference);

    /**
     * Returns the current ground reference location.
     *
     * @return the current ground reference location.
     */
    LatLon getGroundReference();
}
