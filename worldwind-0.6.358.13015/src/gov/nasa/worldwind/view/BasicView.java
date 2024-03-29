/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.view;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.animation.Animator;
import gov.nasa.worldwind.awt.ViewInputHandler;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.GL;

/**
 * A base class from which {@link View} implementations can be dirived. Currently {@link
 * gov.nasa.worldwind.view.firstperson.BasicFlyView} and {@link gov.nasa.worldwind.view.orbit.BasicOrbitView} are both
 * derived from {@link BasicView} {@link BasicView} models the view in terms of a geographic position, and a pitch,
 * heading, and roll. It provides a mapping from that geocentric view model to a 3D graphics modelview matrix. BasicView
 * also manages the projection matrix via a {@link Frustum}.
 * <p/>
 * The view model is based on
 *
 * @author jym
 * @version $Id: BasicView.java 12748 2009-10-26 22:42:03Z dcollins $
 */
public class BasicView extends WWObjectImpl implements View
{

    /** The field of view in degrees. */
    protected Angle fieldOfView = Angle.fromDegrees(45);//高度
    // Provide reasonable default values for the near and far clip distances. By default, BasicView automatically
    // updates these values each frame based on the current eye position relative to the surface. These default values
    // are provided for two reasons:
    // * The view can provide a reasonable value to the application until the first frame.
    // * Subclass implementations which may override the automatic update of clipping plane distances have reasonable
    //   default values to fall back on.
    protected double nearClipDistance = MINIMUM_NEAR_DISTANCE;
    protected double farClipDistance = MINIMUM_FAR_DISTANCE;
    protected Matrix modelview = Matrix.IDENTITY;//初始化，4*4,对角线为1，其他为0。
    protected Matrix modelviewInv = Matrix.IDENTITY;
    protected Matrix projection = Matrix.IDENTITY;
    protected java.awt.Rectangle viewport = new java.awt.Rectangle();
    protected Frustum frustum = new Frustum();
    protected Frustum lastFrustumInModelCoords = null;
    protected ViewPropertyLimits viewLimits;

    protected DrawContext dc;
    protected boolean detectCollisions = true;
    protected boolean hadCollisions;
    protected ViewInputHandler viewInputHandler;
    protected Globe globe;//球体对象
    protected Position eyePosition = Position.ZERO;
    protected Angle roll = Angle.fromDegrees(0.0);
    protected Angle pitch = Angle.fromDegrees(0.0);//水平轴偏转角
    protected Angle heading = Angle.fromDegrees(0.0);//极轴偏转角
    protected Position lastEyePosition = null;
    protected Vec4 lastEyePoint = null;
    protected Vec4 lastUpVector = null;
    protected Vec4 lastForwardVector = null;

    // TODO: make configurable
    protected static final double MINIMUM_NEAR_DISTANCE = 2;
    protected static final double MINIMUM_FAR_DISTANCE = 100;
    protected static final double COLLISION_THRESHOLD = 10;
    protected static final int COLLISION_NUM_ITERATIONS = 4;

    /** Construct a BasicView */
    public BasicView()
    {
    }

    public Globe getGlobe()
    {
        return this.globe;
    }

    public DrawContext getDC()
    {
        return (this.dc);
    }

    public ViewInputHandler getViewInputHandler()
    {
        return viewInputHandler;
    }

    public void setViewInputHandler(ViewInputHandler viewInputHandler)
    {
        this.viewInputHandler = viewInputHandler;
    }

    public boolean isDetectCollisions()
    {
        return this.detectCollisions;
    }

    public void setDetectCollisions(boolean detectCollisions)
    {
        this.detectCollisions = detectCollisions;
    }

    public boolean hadCollisions()
    {
        boolean result = this.hadCollisions;
        this.hadCollisions = false;
        return result;
    }

    public void copyViewState(View view)
    {
    	//将传入View的globe赋值给当前view的globe。
        this.globe = view.getGlobe();
        Vec4 center = view.getCenterPoint();
        if (center == null)
        {
            Vec4 eyePoint = view.getCurrentEyePoint();
            center = eyePoint.add3(view.getForwardVector());
        }
        setOrientation(view.getCurrentEyePosition(), globe.computePositionFromPoint(center));
    }

    public void apply(DrawContext dc)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (dc.getGL() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGLIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }
        if (dc.getGlobe() == null)
        {
            String message = Logging.getMessage("layers.AbstractLayer.NoGlobeSpecifiedInDrawingContext");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }
        if (this.viewInputHandler != null)
        {
            this.viewInputHandler.apply();
        }
        doApply(dc);
    }

    protected void doApply(DrawContext dc)
    {
    }

    public void stopMovement()
    {
        firePropertyChange(VIEW_STOPPED, null, this);
    }

    public java.awt.Rectangle getViewport()
    {
        // java.awt.Rectangle is mutable, so we defensively copy the viewport.
        return new java.awt.Rectangle(this.viewport);
    }

    public Frustum getFrustum()
    {
        return this.frustum;
    }

    public Frustum getFrustumInModelCoordinates()
    {
        if (this.lastFrustumInModelCoords == null)
        {
            Matrix modelviewTranspose = this.modelview.getTranspose();
            if (modelviewTranspose != null)
                this.lastFrustumInModelCoords = this.frustum.transformBy(modelviewTranspose);
            else
                this.lastFrustumInModelCoords = this.frustum;
        }
        return this.lastFrustumInModelCoords;
    }

    public void setFieldOfView(Angle fieldOfView)
    {
        if (fieldOfView == null)
        {
            String message = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.fieldOfView = fieldOfView;
    }

    public double getNearClipDistance()
    {
        return this.nearClipDistance;
    }

    protected void setNearClipDistance(double clipDistance)
    {
        this.nearClipDistance = clipDistance;
    }

    public double getFarClipDistance()
    {
        return this.farClipDistance;
    }

    protected void setFarClipDistance(double clipDistance)
    {
        this.farClipDistance = clipDistance;
    }

    public Matrix getModelviewMatrix()
    {
        return this.modelview;
    }

    public Angle getFieldOfView()
    {
        return this.fieldOfView;
    }

    public Vec4 project(Vec4 modelPoint)
    {
        if (modelPoint == null)
        {
            String message = Logging.getMessage("nullValue.Vec4IsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return this.project(modelPoint, this.modelview, this.projection, this.viewport);
    }

    public Vec4 unProject(Vec4 windowPoint)
    {
        if (windowPoint == null)
        {
            String message = Logging.getMessage("nullValue.Vec4IsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        return unProject(windowPoint, this.modelview, this.projection, this.viewport);
    }

    public Vec4 getEyePoint()
    {
        if (this.lastEyePoint == null)
            this.lastEyePoint = Vec4.UNIT_W.transformBy4(this.modelviewInv);
        return this.lastEyePoint;
    }

    public Vec4 getCenterPoint()
    {
        Vec4 eyePoint = this.getEyePoint();
        Intersection[] intersection = this.globe.intersect(new Line(eyePoint, this.getForwardVector()), 0);
        if (intersection == null)
        {
            return null;
        }
        else
        {
            return (intersection[0].getIntersectionPoint());
        }
    }

    public Position getCenterPosition()
    {
        Vec4 eyePoint = this.getEyePoint();
        Intersection[] intersection = this.globe.intersect(new Line(eyePoint, this.getForwardVector()), 0);
        Position pos = this.globe.computePositionFromPoint(intersection[0].getIntersectionPoint());
        return (pos);
    }

    public Vec4 getCurrentEyePoint()
    {
        if (this.globe != null)
        {
            Matrix modelview = ViewUtil.computeTransformMatrix(this.globe, this.eyePosition,
                this.heading, this.pitch, this.roll);
            if (modelview != null)
            {
                Matrix modelviewInv = modelview.getInverse();
                if (modelviewInv != null)
                {
                    return Vec4.UNIT_W.transformBy4(modelviewInv);
                }
            }
        }

        return Vec4.ZERO;
    }

    public Position getCurrentEyePosition()
    {
        if (this.globe != null)
        {
            Matrix modelview = ViewUtil.computeTransformMatrix(this.globe, this.eyePosition,
                this.heading, this.pitch, this.roll);
            if (modelview != null)
            {
                Matrix modelviewInv = modelview.getInverse();
                if (modelviewInv != null)
                {
                    Vec4 eyePoint = Vec4.UNIT_W.transformBy4(modelviewInv);
                    return this.globe.computePositionFromPoint(eyePoint);
                }
            }
        }

        return Position.ZERO;
    }

    public Position getEyePosition()
    {
        if (this.lastEyePosition == null)
            this.lastEyePosition = computeEyePositionFromModelview();
        return this.lastEyePosition;
    }

    public void setEyePosition(Position eyePosition)
    {
        if (eyePosition == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.eyePosition = eyePosition;

        //resolveCollisionsWithCenterPosition();
    }

    public Angle getHeading()
    {
        return this.heading;
    }

    public void setHeading(Angle heading)
    {
        if (heading == null)
        {
            String message = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.heading = ViewUtil.normalizedHeading(heading);
        this.heading = heading;
        //resolveCollisionsWithPitch();
    }

    public Angle getPitch()
    {
        return this.pitch;
    }

    public void setPitch(Angle pitch)
    {
        if (pitch == null)
        {
            String message = Logging.getMessage("nullValue.AngleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        this.pitch = pitch;
        //resolveCollisionsWithPitch();
    }

    public Vec4 getUpVector()
    {
        if (this.lastUpVector == null)
            this.lastUpVector = Vec4.UNIT_Y.transformBy4(this.modelviewInv);
        return this.lastUpVector;
    }

    public Vec4 getForwardVector()
    {
        if (this.lastForwardVector == null)
            this.lastForwardVector = Vec4.UNIT_NEGATIVE_Z.transformBy4(this.modelviewInv);
        return this.lastForwardVector;
    }

    protected void setViewState(ViewUtil.ViewState modelCoords)
    {
        if (modelCoords != null)
        {
            if (modelCoords.getPosition() != null)
            {
                this.eyePosition = ViewUtil.normalizedEyePosition(modelCoords.getPosition());
            }
            if (modelCoords.getHeading() != null)
            {
                this.heading = ViewUtil.normalizedHeading(modelCoords.getHeading());
            }
            if (modelCoords.getPitch() != null)
            {
                this.pitch = ViewUtil.normalizedPitch(modelCoords.getPitch());
            }
            if (modelCoords.getRoll() != null)
            {
                this.roll = ViewUtil.normalizedRoll(modelCoords.getRoll());
            }
        }
    }

    public void setOrientation(Position eyePosition, Position centerPosition)
    {
        if (eyePosition == null || centerPosition == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (this.globe == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGlobeIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        Vec4 newEyePoint = this.globe.computePointFromPosition(eyePosition);
        Vec4 newCenterPoint = this.globe.computePointFromPosition(centerPosition);
        if (newEyePoint == null || newCenterPoint == null)
        {
            String message = Logging.getMessage("View.ErrorSettingOrientation", eyePosition, centerPosition);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // If eye lat/lon != center lat/lon, then the surface normal at the center point will be a good value
        // for the up direction.
        Vec4 up = this.globe.computeSurfaceNormalAtPoint(newCenterPoint);
        // Otherwise, estimate the up direction by using the *current* heading with the new center position.
        Vec4 forward = newCenterPoint.subtract3(newEyePoint).normalize3();
        if (forward.cross3(up).getLength3() < 0.001)
        {
            Matrix modelview = ViewUtil.computeTransformMatrix(
                this.globe, eyePosition, this.heading, Angle.ZERO, Angle.ZERO);
            if (modelview != null)
            {
                Matrix modelviewInv = modelview.getInverse();
                if (modelviewInv != null)
                {
                    up = Vec4.UNIT_Y.transformBy4(modelviewInv);
                }
            }
        }

        if (up == null)
        {
            String message = Logging.getMessage("View.ErrorSettingOrientation", eyePosition, centerPosition);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        ViewUtil.ViewState modelCoords = ViewUtil.computeViewState(
            this.globe, newEyePoint, newCenterPoint, up);

        setViewState(modelCoords);
    }

    public void stopAnimations()
    {
        viewInputHandler.stopAnimators();
    }

    public boolean isAnimating()
    {
        return viewInputHandler.isAnimating();
    }

    public void goTo(Position position, double distance)
    {
        viewInputHandler.goTo(position, distance);
    }

    public Line computeRayFromScreenPoint(double x, double y)
    {
        return ViewUtil.computeRayFromScreenPoint(this, x, y,
            this.modelview, this.projection, this.viewport);
    }

    public Position computePositionFromScreenPoint(double x, double y)
    {
        if (this.globe != null)
        {
            Line ray = computeRayFromScreenPoint(x, y);
            if (ray != null)
                return this.globe.getIntersectionPosition(ray);
        }

        return null;
    }

    public double computePixelSizeAtDistance(double distance)
    {
        return ViewUtil.computePixelSizeAtDistance(distance, this.fieldOfView, this.viewport);
    }

    protected Position computeEyePositionFromModelview()
    {
        if (this.globe != null)
        {
            Vec4 eyePoint = Vec4.UNIT_W.transformBy4(this.modelviewInv);
            return this.globe.computePositionFromPoint(eyePoint);
        }

        return Position.ZERO;
    }

    public double computeHorizonDistance()
    {
        double horizon = 0;
        Position eyePos = computeEyePositionFromModelview();
        if (eyePos != null)
        {
            horizon = computeHorizonDistance(eyePos);
        }

        return horizon;
    }

    protected double computeHorizonDistance(Position eyePosition)
    {
        if (this.globe != null && eyePosition != null)
        {
            double elevation = eyePosition.getElevation();
            double elevationAboveSurface = ViewUtil.computeElevationAboveSurface(this.dc, eyePosition);
            return ViewUtil.computeHorizonDistance(this.globe, Math.max(elevation, elevationAboveSurface));
        }

        return 0;
    }

    public ViewPropertyLimits getViewPropertyLimits()
    {
        return this.viewLimits;
    }

    protected double computeNearClipDistance()
    {
        return computeNearDistance(getCurrentEyePosition());
    }

    protected double computeFarClipDistance()
    {
        return computeFarDistance(getCurrentEyePosition());
    }

    protected double computeNearDistance(Position eyePosition)
    {
        double near = 0;
        if (eyePosition != null && this.dc != null)
        {
            double elevation = ViewUtil.computeElevationAboveSurface(this.dc, eyePosition);
            double tanHalfFov = this.fieldOfView.tanHalfAngle();
            near = elevation / (2 * Math.sqrt(2 * tanHalfFov * tanHalfFov + 1));
        }
        return near < MINIMUM_NEAR_DISTANCE ? MINIMUM_NEAR_DISTANCE : near;
    }

    protected double computeFarDistance(Position eyePosition)
    {
        double far = 0;
        if (eyePosition != null)
        {
            far = computeHorizonDistance(eyePosition);
        }

        return far < MINIMUM_FAR_DISTANCE ? MINIMUM_FAR_DISTANCE : far;
    }

    public Matrix getProjectionMatrix()
    {
        return this.projection;
    }

    public String getRestorableState()
    {
        RestorableSupport rs = RestorableSupport.newRestorableSupport();
        // Creating a new RestorableSupport failed. RestorableSupport logged the problem, so just return null.
        if (rs == null)
            return null;

        this.doGetRestorableState(rs, null);

        return rs.getStateAsXml();
    }

    public void restoreState(String stateInXml)
    {
        if (stateInXml == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        RestorableSupport rs;
        try
        {
            rs = RestorableSupport.parse(stateInXml);
        }
        catch (Exception e)
        {
            // Parsing the document specified by stateInXml failed.
            String message = Logging.getMessage("generic.ExceptionAttemptingToParseStateXml", stateInXml);
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message, e);
        }

        this.doRestoreState(rs, null);
    }

    //**************************************************************//
    //******************** Restorable State  ***********************//
    //**************************************************************//

    protected void doGetRestorableState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        this.getViewPropertyLimits().getRestorableState(rs, rs.addStateObject(context, "viewPropertyLimits"));

        rs.addStateValueAsBoolean(context, "detectCollisions", this.isDetectCollisions());

        if (this.getFieldOfView() != null)
            rs.addStateValueAsDouble(context, "fieldOfView", this.getFieldOfView().getDegrees());

        rs.addStateValueAsDouble(context, "nearClipDistance", this.getNearClipDistance());
        rs.addStateValueAsDouble(context, "farClipDistance", this.getFarClipDistance());

        if (this.getEyePosition() != null)
            rs.addStateValueAsPosition(context, "eyePosition", this.getEyePosition());

        if (this.getHeading() != null)
            rs.addStateValueAsDouble(context, "heading", this.getHeading().getDegrees());

        if (this.getPitch() != null)
            rs.addStateValueAsDouble(context, "pitch", this.getPitch().getDegrees());
    }

    protected void doRestoreState(RestorableSupport rs, RestorableSupport.StateObject context)
    {
        // Restore the property limits and collision detection flags before restoring the view's position and
        // orientation. This has the effect of ensuring that the view's position and orientation are consistent with the
        // current property limits and the current surface collision state.

        RestorableSupport.StateObject so = rs.getStateObject(context, "viewPropertyLimits");
        if (so != null)
            this.getViewPropertyLimits().restoreState(rs, so);

        Boolean b = rs.getStateValueAsBoolean(context, "detectCollisions");
        if (b != null)
            this.setDetectCollisions(b);
        
        Double d = rs.getStateValueAsDouble(context, "fieldOfView");
        if (d != null)
            this.setFieldOfView(Angle.fromDegrees(d));

        d = rs.getStateValueAsDouble(context, "nearClipDistance");
        if (d != null)
            this.setNearClipDistance(d);

        d = rs.getStateValueAsDouble(context, "farClipDistance");
        if (d != null)
            this.setFarClipDistance(d);

        Position p = rs.getStateValueAsPosition(context, "eyePosition");
        if (p != null)
            this.setEyePosition(p);

        d = rs.getStateValueAsDouble(context, "heading");
        if (d != null)
            this.setHeading(Angle.fromDegrees(d));

        d = rs.getStateValueAsDouble(context, "pitch");
        if (d != null)
            this.setPitch(Angle.fromDegrees(d));
    }

    public Matrix pushReferenceCenter(DrawContext dc, Vec4 referenceCenter)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (dc.getGL() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGLIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }
        if (referenceCenter == null)
        {
            String message = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Matrix modelview = getModelviewMatrix();

        // Compute a new model-view matrix with origin at referenceCenter.
        Matrix matrix = null;
        if (modelview != null)
            matrix = modelview.multiply(Matrix.fromTranslation(referenceCenter));

        GL gl = dc.getGL();

        // Store the current matrix-mode state.
        OGLStackHandler ogsh = new OGLStackHandler();

        try
        {
            ogsh.pushAttrib(gl, GL.GL_TRANSFORM_BIT);

            gl.glMatrixMode(GL.GL_MODELVIEW);

            // Push and load a new model-view matrix to the current OpenGL context held by 'dc'.
            gl.glPushMatrix();
            if (matrix != null)
            {
                double[] matrixArray = new double[16];
                matrix.toArray(matrixArray, 0, false);
                gl.glLoadMatrixd(matrixArray, 0);
            }
        }
        finally
        {
            ogsh.pop(gl);
        }

        return matrix;
    }

    /**
     * Removes the model-view matrix on top of the matrix stack, and restores the original matrix.
     *
     * @param dc the current World Wind drawing context on which the original matrix will be restored.
     *
     * @throws IllegalArgumentException if <code>dc</code> is null, or if the <code>Globe</code> or <code>GL</code>
     *                                  instances in <code>dc</code> are null.
     */
    public void popReferenceCenter(DrawContext dc)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (dc.getGL() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGLIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }

        GL gl = dc.getGL();

        // Store the current matrix-mode state.
        OGLStackHandler ogsh = new OGLStackHandler();

        try
        {
            ogsh.pushAttrib(gl, GL.GL_TRANSFORM_BIT);

            gl.glMatrixMode(GL.GL_MODELVIEW);

            // Pop the top model-view matrix.
            gl.glPopMatrix();
        }
        finally
        {
            ogsh.pop(gl);
        }
    }

    /**
     * Transforms the specified object coordinates into window coordinates using the given
     * modelview and projection matrices, and viewport.
     * @param point The object coordinate to transform
     * @param modelview The modelview matrix
     * @param projection The projection matrix
     * @param viewport The viewport
     * @return the transformed coordinates
     */
    public Vec4 project(Vec4 point, Matrix modelview, Matrix projection, java.awt.Rectangle viewport)
    {
        if (point == null)
        {
            String message = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (modelview == null || projection == null)
        {
            String message = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (viewport == null)
        {
            String message = Logging.getMessage("nullValue.RectangleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // GLU expects matrices as column-major arrays.
        double[] modelviewArray = new double[16];
        double[] projectionArray = new double[16];
        modelview.toArray(modelviewArray, 0, false);
        projection.toArray(projectionArray, 0, false);
        // GLU expects the viewport as a four-component array.
        int[] viewportArray = new int[] {viewport.x, viewport.y, viewport.width, viewport.height};

        double[] result = new double[3];
        if (!this.dc.getGLU().gluProject(
            point.x, point.y, point.z,
            modelviewArray, 0,
            projectionArray, 0,
            viewportArray, 0,
            result, 0))
        {
            return null;
        }

        return Vec4.fromArray3(result, 0);
    }

    /**
     * Maps the given window coordinates into model coordinates using the given matrices and viewport.
     * @param windowPoint the winodow point
     * @param modelview the modelview matrix
     * @param projection the projection matrix
     * @param viewport the window viewport
     * @return the unprojected point
     */
    public Vec4 unProject(Vec4 windowPoint, Matrix modelview, Matrix projection, java.awt.Rectangle viewport)
    {
        if (windowPoint == null)
        {
            String message = Logging.getMessage("nullValue.PointIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (modelview == null || projection == null)
        {
            String message = Logging.getMessage("nullValue.MatrixIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (viewport == null)
        {
            String message = Logging.getMessage("nullValue.RectangleIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        // GLU expects matrices as column-major arrays.
        double[] modelviewArray = new double[16];
        double[] projectionArray = new double[16];
        modelview.toArray(modelviewArray, 0, false);
        projection.toArray(projectionArray, 0, false);
        // GLU expects the viewport as a four-component array.
        int[] viewportArray = new int[] {viewport.x, viewport.y, viewport.width, viewport.height};

        double[] result = new double[3];
        if (!this.dc.getGLU().gluUnProject(
            windowPoint.x, windowPoint.y, windowPoint.z,
            modelviewArray, 0,
            projectionArray, 0,
            viewportArray, 0,
            result, 0))
        {
            return null;
        }

        return Vec4.fromArray3(result, 0);
    }

    /**
     * Sets the the opengl modelview and projection matrices to the given matrices.
     * @param dc the drawing context
     * @param modelview the modelview matrix
     * @param projection the projection matrix
     */
    public static void loadGLViewState(DrawContext dc, Matrix modelview, Matrix projection)
    {
        if (dc == null)
        {
            String message = Logging.getMessage("nullValue.DrawContextIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (dc.getGL() == null)
        {
            String message = Logging.getMessage("nullValue.DrawingContextGLIsNull");
            Logging.logger().severe(message);
            throw new IllegalStateException(message);
        }
        if (modelview == null)
        {
            Logging.logger().fine("nullValue.ModelViewIsNull");
        }
        if (projection == null)
        {
            Logging.logger().fine("nullValue.ProjectionIsNull");
        }

        double[] matrixArray = new double[16];

        GL gl = dc.getGL();
        // Store the current matrix-mode state.
        OGLStackHandler ogsh = new OGLStackHandler();

        try
        {
            ogsh.pushAttrib(gl, GL.GL_TRANSFORM_BIT);

            // Apply the model-view matrix to the current OpenGL context.
            gl.glMatrixMode(GL.GL_MODELVIEW);
            if (modelview != null)
            {
                modelview.toArray(matrixArray, 0, false);
                gl.glLoadMatrixd(matrixArray, 0);
            }
            else
            {
                gl.glLoadIdentity();
            }

            // Apply the projection matrix to the current OpenGL context.
            gl.glMatrixMode(GL.GL_PROJECTION);
            if (projection != null)
            {
                projection.toArray(matrixArray, 0, false);
                gl.glLoadMatrixd(matrixArray, 0);
            }
            else
            {
                gl.glLoadIdentity();
            }
        }
        finally
        {
            ogsh.pop(gl);
        }
    }

    /**
     * Add an animator to the this View.  The View does not start the animator.
     * @param animator the {@link gov.nasa.worldwind.animation.Animator} to be added
     */
    public void addAnimator(Animator animator)
    {
        viewInputHandler.addAnimator(animator);
    }
}
