/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.layers;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.media.opengl.*;

/**
 * The <code>RenderableLayer</code> class manages a collection of {@link gov.nasa.worldwind.render.Renderable} objects
 * for rendering, picking, and disposal.
 *
 * @author tag
 * @version $Id: RenderableLayer.java 12821 2009-11-24 00:17:57Z tgaskins $
 * @see gov.nasa.worldwind.render.Renderable
 * @comments 可渲染图层类。该类维护一个可渲染对象列表。在render函数中，列表中各对象自行渲染。yecol.2010.4.18.
 */
public class RenderableLayer extends AbstractLayer
{
    private java.util.Collection<Renderable> renderables = new java.util.concurrent.ConcurrentLinkedQueue<Renderable>();
    private Iterable<Renderable> renderablesOverride;
    protected PickSupport pickSupport = new PickSupport();
    protected Layer delegateOwner;

    /** Creates a new <code>RenderableLayer</code> with a null <code>delegateOwner</code> */
    public RenderableLayer()
    {
        this.delegateOwner = null;
    }

    /**
     * Creates a new <code>RenderableLayer</code> with the specified <code>delegateOwner</code>.
     *
     * @param delegateOwner Layer that is this layer's delegate owner.
     */
    public RenderableLayer(Layer delegateOwner)
    {
        this.delegateOwner = delegateOwner;//委托者或授权者
    }

    /**
     * Adds the specified <code>renderable</code> to this layer's internal collection. If this layer's internal
     * collection has been overriden with a call to {@link #setRenderables}, this will throw an exception.
     *
     * @param renderable Renderable to add.
     *
     * @throws IllegalArgumentException If <code>renderable</code> is null.
     * @throws IllegalStateException    If a custom Iterable has been specified by a call to <code>setRenderables</code>.
     */
    public void addRenderable(Renderable renderable)
    {
        if (renderable == null)
        {
            String msg = Logging.getMessage("nullValue.RenderableIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (this.renderablesOverride != null)
        {
            String msg = Logging.getMessage("generic.LayerIsUsingCustomIterable");
            Logging.logger().severe(msg);
            throw new IllegalStateException(msg);
        }

        this.renderables.add(renderable);
    }

    /**
     * Adds the contents of the specified <code>renderables</code> to this layer's internal collection. If this layer's
     * internal collection has been overriden with a call to {@link #setRenderables}, this will throw an exception.
     *
     * @param renderables Renderables to add.
     *
     * @throws IllegalArgumentException If <code>renderables</code> is null.
     * @throws IllegalStateException    If a custom Iterable has been specified by a call to <code>setRenderables</code>.
     */
    public void addRenderables(Iterable<? extends Renderable> renderables)
    {
        if (renderables == null)
        {
            String msg = Logging.getMessage("nullValue.IterableIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (this.renderablesOverride != null)
        {
            String msg = Logging.getMessage("generic.LayerIsUsingCustomIterable");
            Logging.logger().severe(msg);
            throw new IllegalStateException(msg);
        }

        for (Renderable renderable : renderables)
        {
            // Internal list of renderables does not accept null values.
            if (renderable != null)
                this.renderables.add(renderable);
        }
    }

    /**
     * Removes the specified <code>renderable</code> from this layer's internal collection, if it exists. If this
     * layer's internal collection has been overriden with a call to {@link #setRenderables}, this will throw an
     * exception.
     *
     * @param renderable Renderable to remove.
     *
     * @throws IllegalArgumentException If <code>renderable</code> is null.
     * @throws IllegalStateException    If a custom Iterable has been specified by a call to <code>setRenderables</code>.
     */
    public void removeRenderable(Renderable renderable)
    {
        if (renderable == null)
        {
            String msg = Logging.getMessage("nullValue.RenderableIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        if (this.renderablesOverride != null)
        {
            String msg = Logging.getMessage("generic.LayerIsUsingCustomIterable");
            Logging.logger().severe(msg);
            throw new IllegalStateException(msg);
        }

        this.renderables.remove(renderable);
    }

    /**
     * Clears the contents of this layer's internal Renderable collection. If this layer's internal collection has been
     * overriden with a call to {@link #setRenderables}, this will throw an exception.
     *
     * @throws IllegalStateException If a custom Iterable has been specified by a call to <code>setRenderables</code>.
     */
    public void removeAllRenderables()
    {
        if (this.renderablesOverride != null)
        {
            String msg = Logging.getMessage("generic.LayerIsUsingCustomIterable");
            Logging.logger().severe(msg);
            throw new IllegalStateException(msg);
        }

        this.clearRenderables();
    }

    protected void clearRenderables()
    {
        if (this.renderables != null && this.renderables.size() > 0)
            this.renderables.clear();
    }

    public int getNumRenderables()
    {
        if (this.renderablesOverride != null)
        {
            int size = 0;
            //noinspection UnusedDeclaration
            for (Renderable r : this.renderablesOverride)
            {
                ++size;
            }

            return size;
        }
        else
        {
            return this.renderables.size();
        }
    }

    /**
     * Returns the Iterable of Renderables currently in use by this layer. If the caller has specified a custom Iterable
     * via {@link #setRenderables}, this will returns a reference to that Iterable. If the caller passed
     * <code>setRenderables</code> a null parameter, or if <code>setRenderables</code> has not been called, this returns
     * a view of this layer's internal collection of Renderables.
     *
     * @return Iterable of currently active Renderables.
     */
    public Iterable<Renderable> getRenderables()
    {
        return this.getActiveRenderables();
    }

    /**
     * Returns the Iterable of currently active Renderables. If the caller has specified a custom Iterable via {@link
     * #setRenderables}, this will returns a reference to that Iterable. If the caller passed
     * <code>setRenderables</code> a null parameter, or if <code>setRenderables</code> has not been called, this returns
     * a view of this layer's internal collection of Renderables.
     *
     * @return Iterable of currently active Renderables.
     */
    protected Iterable<Renderable> getActiveRenderables()
    {
        if (this.renderablesOverride != null)
        {
            return this.renderablesOverride;
        }
        else
        {
            // Return an unmodifiable reference to the internal list of renderables.
            // This prevents callers from changing this list and invalidating any invariants we have established.
            return java.util.Collections.unmodifiableCollection(this.renderables);
        }
    }

    /**
     * Overrides the collection of currently active Renderables with the specified <code>renderableIterable</code>. This
     * layer will maintain a reference to <code>renderableIterable</code> strictly for picking and rendering. This layer
     * will not modify the reference, or dispose of its contents. This will also clear and dispose of the internal
     * collection of Renderables, and will prevent any modification to its contents via <code>addRenderable,
     * addRenderables, removeRenderables, or dispose</code>. <p/> If the specified <code>renderableIterable</code> is
     * null, this layer will revert to maintaining its internal collection.
     *
     * @param renderableIterable Iterable to use instead of this layer's internal collection, or null to use this
     *                           layer's internal collection.
     */
    public void setRenderables(Iterable<Renderable> renderableIterable)
    {
        this.renderablesOverride = renderableIterable;
        // Dispose of the internal collection of Renderables.
        this.disposeRenderables();
        // Clear the internal collection of Renderables.
        this.clearRenderables();
    }

    /**
     * Returns this layer's delegate owner, or null if none has been specified.
     *
     * @return Layer that is this layer's delegate owner.
     */
    public Layer getDelegateOwner()
    {
        return this.delegateOwner;
    }

    /**
     * Opacity is not applied to layers of this type because each renderable typically has its own opacity control.
     *
     * @param opacity the current opacity value, which is ignored by this layer.
     */
    @Override
    public void setOpacity(double opacity)
    {
        super.setOpacity(opacity);
    }

    /**
     * Returns the layer's opacity value, which is ignored by this layer because each of its renderables typiically has
     * its own opacity control.
     *
     * @return The layer opacity, a value between 0 and 1.
     */
    @Override
    public double getOpacity()
    {
        return super.getOpacity();
    }

    /**
     * Disposes the contents of this layer's internal Renderable collection, but does not remove any elements from that
     * collection.
     *
     * @throws IllegalStateException If a custom Iterable has been specified by a call to <code>setRenderables</code>.
     */
    public void dispose()
    {
        if (this.renderablesOverride != null)
        {
            String msg = Logging.getMessage("generic.LayerIsUsingCustomIterable");
            Logging.logger().severe(msg);
            throw new IllegalStateException(msg);
        }

        this.disposeRenderables();
    }

    protected void disposeRenderables()
    {
        if (this.renderables != null && this.renderables.size() > 0)
        {
            for (Renderable renderable : this.renderables)
            {
                try
                {
                    if (renderable instanceof Disposable)
                        ((Disposable) renderable).dispose();
                }
                catch (Exception e)
                {
                    String msg = Logging.getMessage("generic.ExceptionAttemptingToDisposeRenderable");
                    Logging.logger().severe(msg);
                    // continue to next renderable
                }
            }
        }

        this.renderables.clear();
    }

    protected void doPreRender(DrawContext dc)
    {
        this.doPreRender(dc, this.getActiveRenderables());
    }

    protected void doPick(DrawContext dc, java.awt.Point pickPoint)
    {
        this.doPick(dc, this.getActiveRenderables(), pickPoint);
    }

    protected void doRender(DrawContext dc)
    {
        this.doRender(dc, this.getActiveRenderables());
    }

    protected void doPreRender(DrawContext dc, Iterable<? extends Renderable> renderables)
    {
        for (Renderable renderable : renderables)
        {
            try
            {
                // If the caller has specified their own Iterable,
                // then we cannot make any guarantees about its contents.
                if (renderable != null && renderable instanceof PreRenderable)
                    ((PreRenderable) renderable).preRender(dc);
            }
            catch (Exception e)
            {
                String msg = Logging.getMessage("generic.ExceptionWhilePrerenderingRenderable");
                Logging.logger().severe(msg);
                // continue to next renderable
            }
        }
    }

    protected void doPick(DrawContext dc, Iterable<? extends Renderable> renderables, java.awt.Point pickPoint)
    {
        this.pickSupport.clearPickList();
        this.pickSupport.beginPicking(dc);

        try
        {
            for (Renderable renderable : renderables)
            {
                // If the caller has specified their own Iterable,
                // then we cannot make any guarantees about its contents.
                if (renderable != null)
                {
                    float[] inColor = new float[4];
                    dc.getGL().glGetFloatv(GL.GL_CURRENT_COLOR, inColor, 0);
                    java.awt.Color color = dc.getUniquePickColor();
                    dc.getGL().glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());

                    try
                    {
                        renderable.render(dc);
                    }
                    catch (Exception e)
                    {
                        String msg = Logging.getMessage("generic.ExceptionWhilePickingRenderable");
                        Logging.logger().severe(msg);
                        continue; // go on to next renderable
                    }

                    dc.getGL().glColor4fv(inColor, 0);

                    if (renderable instanceof Locatable)
                    {
                        this.pickSupport.addPickableObject(color.getRGB(), renderable,
                            ((Locatable) renderable).getPosition(), false);
                    }
                    else
                    {
                        this.pickSupport.addPickableObject(color.getRGB(), renderable);
                    }
                }
            }

            this.pickSupport.resolvePick(dc, pickPoint, this.delegateOwner != null ? this.delegateOwner : this);
        }
        finally
        {
            this.pickSupport.endPicking(dc);
        }
    }

    protected void doRender(DrawContext dc, Iterable<? extends Renderable> renderables)
    {
    	//renderables中各自渲染。
        for (Renderable renderable : renderables)
        {
            try
            {
                // If the caller has specified their own Iterable,
                // then we cannot make any guarantees about its contents.
                if (renderable != null)
                    renderable.render(dc);
            }
            catch (Exception e)
            {
                String msg = Logging.getMessage("generic.ExceptionWhileRenderingRenderable");
                Logging.logger().severe(msg);
                // continue to next renderable
            }
        }
    }

    @Override
    public String toString()
    {
        return Logging.getMessage("layers.RenderableLayer.Name");
    }
}
