/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.render;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Renders fast multiple polygons with or without holes in one pass. It relies on
 * a {@link gov.nasa.worldwind.util.CompoundVecBuffer}.
 * <p>
 * Whether a polygon ring is filled or is a hole in another polygon depends on the vertices winding order and the
 * winding rule used - see setWindingRule(String).
 *
 * @author Dave Collins
 * @author Patrick Murris
 * @version $Id: SurfacePolygons.java 12857 2009-12-04 15:02:51Z patrickmurris $
 */
public class SurfacePolygons extends SurfacePolylines
{
    protected Integer[] interiorDisplayLists;
    private int[] polygonRingGroups;
    private String windingRule = AVKey.CLOCKWISE;
    private boolean needsInteriorTessellation = true;
    private boolean tessellationFailed = false;
    protected static GLU glu;
    protected static GLUtessellator tess;
    protected WWTexture texture;

    public SurfacePolygons(CompoundVecBuffer buffer)
    {
        super(buffer);
    }

    public SurfacePolygons(Sector sector, CompoundVecBuffer buffer)
    {
        super(sector, buffer);
    }

    /**
     * Get a copy of the polygon ring groups array - can be null.
     * <p>
     * When not null the polygon ring groups array identifies the starting sub buffer index for each polygon.
     * In that case rings from a same group will be tesselated together as part of the same polygon.
     * <p>
     * When <code>null</code> polygon rings that follow the current winding rule are tessellated separatly
     * as different polygons. Rings that are reverse winded are considered holes to be applied to the last
     * straight winded ring polygon.
     *
     * @return a copy of the polygon ring groups array - can be null.
     */
    public int[] getPolygonRingGroups()
    {
        return this.polygonRingGroups.clone();
    }

    /**
     * Set the polygon ring groups array - can be null.
     * <p>
     * When not null the polygon ring groups array identifies the starting sub buffer index for each polygon.
     * In that case rings from a same group will be tesselated together as part of the same polygon.
     * <p>
     * When <code>null</code> polygon rings that follow the current winding rule are tessellated separatly
     * as different polygons. Rings that are reverse winded are considered holes to be applied to the last
     * straight winded ring polygon.
     *
     * @param ringGroups a copy of the polygon ring groups array - can be null.
     */
    public void setPolygonRingGroups(int[] ringGroups)
    {
        this.polygonRingGroups = ringGroups.clone();
        this.onGeometryChanged();
    }

    /**
     * Get the winding rule used when tessellating polygons. Can be one of {@link AVKey#CLOCKWISE} (default)
     * or {@link AVKey#COUNTER_CLOCKWISE}.
     * <p>
     * When set to {@link AVKey#CLOCKWISE} polygons which run clockwise will be filled and those which run
     * counter clockwise will produce 'holes'. The interpretation is reversed when the winding rule is set
     * to {@link AVKey#COUNTER_CLOCKWISE}.
     *
     * @return the winding rule used when tessellating polygons.
     */
    public String getWindingRule()
    {
        return this.windingRule;
    }

    /**
     * Set the winding rule used when tessellating polygons. Can be one of {@link AVKey#CLOCKWISE} (default)
     * or {@link AVKey#COUNTER_CLOCKWISE}.
     * <p>
     * When set to {@link AVKey#CLOCKWISE} polygons which run clockwise will be filled and those which run
     * counter clockwise will produce 'holes'. The interpretation is reversed when the winding rule is set
     * to {@link AVKey#COUNTER_CLOCKWISE}.
     *
     * @param windingRule the winding rule to use when tessellating polygons.
     */
    public void setWindingRule(String windingRule)
    {
        this.windingRule = windingRule;
        this.onGeometryChanged();
    }

    protected void onGeometryChanged()
    {
        this.needsInteriorTessellation = true;
        super.onGeometryChanged();
    }

    public void dispose()
    {
        super.dispose();

        GLContext glContext = GLContext.getCurrent();
        if (glContext == null)
            return;

        this.disposeOfInteriorDisplayLists(glContext.getGL());
    }

    protected void disposeOfInteriorDisplayLists(GL gl)
    {
        if (this.interiorDisplayLists != null && this.interiorDisplayLists.length > 0)
        {
            for (int list : this.interiorDisplayLists)
                gl.glDeleteLists(list, 1);

            this.interiorDisplayLists = null;
        }
    }

    protected void doRenderInteriorToRegion(DrawContext dc, Sector sector, int x, int y, int width, int height)
    {
        if (this.tessellationFailed)
            return;

        LatLon shapeCenter = Sector.boundingSector(this.getLocations(dc.getGlobe())).getCentroid();
        getSurfaceShapeSupport().applyInteriorState(dc, this.attributes, this.getTexture(), sector,
            new Rectangle(x, y, width, height), shapeCenter);
        this.drawInterior(dc, (int)Math.signum(sector.getCentroid().getLongitude().degrees));
    }


    protected void drawInterior(DrawContext dc, int hemisphereSign)
    {
        GL gl = dc.getGL();

        if (this.interiorDisplayLists == null || this.needsInteriorTessellation)
        {
            tessellateInterior(dc, new SurfaceConcaveShape.ImmediateModeCallback(dc));
            if (this.tessellationFailed)
                return;
        }

        this.drawLists(gl, this.interiorDisplayLists);
        if (this.crossesDateLine)
        {
            // Apply hemisphere offset and draw again
            gl.glTranslated(360 * hemisphereSign, 0, 0);
            this.drawLists(gl, this.interiorDisplayLists);
            gl.glTranslated(-360 * hemisphereSign, 0, 0);
        }

    }

    protected WWTexture getTexture()
    {
        if (this.attributes.getInteriorImageSource() == null)
            return null;

        if (this.texture == null && this.attributes.getInteriorImageSource() != null)
            this.texture = new BasicWWTexture(this.attributes.getInteriorImageSource());

        return this.texture;
    }

    //**************************************************************//
    //********************  Interior Tessellation  *****************//
    //**************************************************************//

    protected static GLU getGLU()
    {
        if (glu == null)
        {
            glu = new GLU();
        }

        return glu;
    }

    protected static GLUtessellator getGLUTessellator()
    {
        if (tess == null)
        {
            tess = getGLU().gluNewTess();
        }

        return tess;
    }

    protected void tessellateInterior(DrawContext dc, GLUtessellatorCallback callback)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        GLU glu = getGLU();
        GLUtessellator tess = getGLUTessellator();
        this.beginTessellation(dc, glu, tess, callback);

        try
        {
            this.doTessellate(dc, glu, tess);
        }
        finally
        {
            this.endTessellation(dc, glu, tess);
            glu.gluDeleteTess(tess);
            this.tessellationFailed = (this.interiorDisplayLists == null);
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void beginTessellation(DrawContext dc, GLU glu, GLUtessellator tess, GLUtessellatorCallback callback)
    {
        glu.gluTessNormal(tess, 0.0, 0.0, 1.0);
        glu.gluTessCallback(tess, GLU.GLU_TESS_BEGIN, callback);
        glu.gluTessCallback(tess, GLU.GLU_TESS_VERTEX, callback);
        glu.gluTessCallback(tess, GLU.GLU_TESS_END, callback);
        glu.gluTessCallback(tess, GLU.GLU_TESS_COMBINE, callback);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    protected void endTessellation(DrawContext dc, GLU glu, GLUtessellator tess)
    {
        glu.gluTessCallback(tess, GLU.GLU_TESS_BEGIN, null);
        glu.gluTessCallback(tess, GLU.GLU_TESS_VERTEX, null);
        glu.gluTessCallback(tess, GLU.GLU_TESS_END, null);
        glu.gluTessCallback(tess, GLU.GLU_TESS_COMBINE, null);
    }

    private boolean polygonStarted = false;
    protected void doTessellate(DrawContext dc, GLU glu, GLUtessellator tess)
    {
        GL gl = dc.getGL();
        this.disposeOfInteriorDisplayLists(gl);

        // Setup the winding order to correctly tessellate the outer and inner rings.
        int winding = this.windingRule.equals(AVKey.CLOCKWISE) ?
            GLU.GLU_TESS_WINDING_NEGATIVE : GLU.GLU_TESS_WINDING_POSITIVE;
        glu.gluTessProperty(tess, GLU.GLU_TESS_WINDING_RULE, winding);

        // Use one display list per polygon
        this.polygonStarted = false;
        ArrayList<Integer> displayLists = new ArrayList<Integer>();
        this.crossesDateLine = false;

        int numRings = this.buffer.getNumSubBuffers();
        if (this.polygonRingGroups == null)
        {
            // Polygon rings are drawn following the sub buffers order. If the winding rule is CW all clockwise
            // rings are considered an outer ring possibly followed by counter clock wise inner rings.
            for (int i = 0; i < numRings; i++)
            {
                VecBuffer vecBuffer = this.buffer.getSubBuffer(i);

                // Start a new polygon for each outer ring
                if (WWMath.computePolygonWindingOrder(vecBuffer.getLocations()).equals(this.getWindingRule()))
                    this.beginPolygon(gl, glu, tess, displayLists);

                if (tessellateRing(glu, tess, vecBuffer))
                    this.crossesDateLine = true;
            }
            if (this.polygonStarted)
                this.endPolygon(gl, glu, tess);
        }
        else
        {
            // Tessellate one polygon per ring group
            int numGroups = this.polygonRingGroups.length;
            for (int group = 0; group < numGroups; group++)
            {
                int groupStart = this.polygonRingGroups[group];
                int groupLength = (group == numGroups - 1) ? numRings - groupStart
                    : this.polygonRingGroups[group + 1] - groupStart;

                this.beginPolygon(gl, glu, tess, displayLists);
                for (int i = 0; i < groupLength; i++)
                {
                    if (tessellateRing(glu, tess, this.buffer.getSubBuffer(groupStart + i)))
                        this.crossesDateLine = true;
                }
                this.endPolygon(gl, glu, tess);
            }
        }

        this.interiorDisplayLists = new Integer[displayLists.size()];
        displayLists.toArray(this.interiorDisplayLists);
        this.needsInteriorTessellation = false;
    }

    protected boolean tessellateRing(GLU glu, GLUtessellator tess, VecBuffer vecBuffer)
    {
        // Check for pole wrapping shape
        List<double[]> dateLineCrossingPoints = this.computeDateLineCrossingPoints(vecBuffer);
        int pole = this.computePole(dateLineCrossingPoints);
        double[] poleWrappingPoint = this.computePoleWrappingPoint(pole, dateLineCrossingPoints);

        glu.gluTessBeginContour(tess);
        Iterable<double[]> iterable = vecBuffer.getCoords(3);
        boolean dateLineCrossed = false;
        int sign = 0;
        double[] previousPoint = null;
        for (double[] coords : iterable)
        {
            if (poleWrappingPoint != null && previousPoint != null
                && poleWrappingPoint[0] == previousPoint[0] && poleWrappingPoint[1] == previousPoint[1])
            {
                previousPoint = coords.clone();
                // Wrapping a pole
                double[] dateLinePoint1 = this.computeDateLineEntryPoint(poleWrappingPoint, coords);
                double[] polePoint1 = new double[] {180 * Math.signum(poleWrappingPoint[0]), 90d * pole, 0};
                double[] dateLinePoint2 = dateLinePoint1.clone();
                double[] polePoint2 = polePoint1.clone();
                dateLinePoint2[0] *= -1;
                polePoint2[0] *= -1;
                // Move to date line then to pole
                glu.gluTessVertex(tess, dateLinePoint1, 0, dateLinePoint1);
                glu.gluTessVertex(tess, polePoint1, 0, polePoint1);
                // Move to the other side of the date line
                glu.gluTessVertex(tess, polePoint2, 0, polePoint2);
                glu.gluTessVertex(tess, dateLinePoint2, 0, dateLinePoint2);
                // Finaly draw current point past the date line
                glu.gluTessVertex(tess, coords, 0, coords);

                dateLineCrossed = true;
            }
            else
            {
                if (previousPoint != null && Math.abs(previousPoint[0] - coords[0]) > 180)
                {
                    // Crossing date line, sum departure point longitude sign for hemisphere offset
                    sign += (int)Math.signum(previousPoint[0]);
                    dateLineCrossed = true;
                }
                previousPoint = coords.clone();
                coords[0] += sign * 360;   // apply hemisphere offset
                glu.gluTessVertex(tess, coords, 0, coords);
            }
        }
        glu.gluTessEndContour(tess);

        return dateLineCrossed;
    }

    private void beginPolygon(GL gl, GLU glu, GLUtessellator tess, java.util.List<Integer> displayLists)
    {
        if (this.polygonStarted)
            this.endPolygon(gl, glu, tess);

        int list = gl.glGenLists(1);
        gl.glNewList(list, GL.GL_COMPILE);
        displayLists.add(list);
        glu.gluTessBeginPolygon(tess, null);
        this.polygonStarted = true;
    }

    private void endPolygon(GL gl, GLU glu, GLUtessellator tess)
    {
        glu.gluTessEndPolygon(tess);
        gl.glEndList();
        this.polygonStarted = false;
    }

    // --- Pole wrapping shapes handling ---

    protected List<double[]> computeDateLineCrossingPoints(VecBuffer vecBuffer)
    {
        // Shapes that include a pole will yield an odd number of points
        List<double[]> list = new ArrayList<double[]>();
        Iterable<double[]> iterable = vecBuffer.getCoords(3);
        double[] previousPoint = null;
        for (double[] coords : iterable)
        {
            if (previousPoint != null && Math.abs(previousPoint[0] - coords[0]) > 180)
                list.add(previousPoint);
            previousPoint = coords;
        }
        
        return list;
    }

    protected int computePole(List<double[]> dateLineCrossingPoints)
    {
        int sign = 0;
        for (double[] point : dateLineCrossingPoints)
            sign += Math.signum(point[0]);

        if (sign == 0)
            return 0;
        
        // If we cross the date line going west (from a negative longitude) with a clockwise polygon,
        // then the north pole (positive) is included.
        return this.getWindingRule().equals(AVKey.CLOCKWISE) && sign < 0 ? 1 : -1;
    }

    protected double[] computePoleWrappingPoint(int pole, List<double[]> dateLineCrossingPoints)
    {
        if (pole == 0)
            return null;

        // Find point with latitude closest to pole
        int idx = -1;
        double max = pole < 0 ? 90 : -90;
        for (int i = 0; i < dateLineCrossingPoints.size(); i++)
        {
            double[] point = dateLineCrossingPoints.get(i);
            if (pole < 0 && point[1] < max) // increasing latitude toward north pole
            {
                idx = i;
                max = point[1];
            }
            if (pole > 0 && point[1] > max) // decreasing latitude toward south pole
            {
                idx = i;
                max = point[1];
            }
        }

        return dateLineCrossingPoints.get(idx);
    }

    protected double[] computeDateLineEntryPoint(double[] from, double[] to)
    {
        // Linear interpolation between from and to at the date line
        double dLat = to[1] - from[1];
        double dLon = 360 - Math.abs(to[0] - from[0]);
        double s = Math.abs(180 * Math.signum(from[0]) - from[0]) / dLon;
        double lat = from[1] + dLat * s;
        double lon = 180 * Math.signum(from[0]); // same side as from

        return new double[] {lon, lat, 0};
    }

}
