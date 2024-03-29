/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.*;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.util.*;

import java.util.*;

/**
 * The top-level interface common to all toolkit-specific World Wind windows.
 *
 * @author Tom Gaskins
 * @version $Id: WorldWindow.java 12524 2009-08-26 22:37:54Z tgaskins $
 * @comment 
 */
public interface WorldWindow extends AVList
{
    /**
     * Sets the model to display in this window. If <code>null</code> is specified for the model, the current model, if
     * any, is disassociated with the window.
     *
     * @param model the model to display. May be <code>null</code>.
     */
    void setModel(Model model);

    /**
     * Returns the window's current model.
     *
     * @return the window's current model.
     */
    Model getModel();

    /**
     * Sets the view to use when displaying this window's model. If <code>null</code> is specified for the view, the
     * current view, if any, is disassociated with the window.
     *
     * @param view the view to use to display this window's model. May be null.
     */
    void setView(View view);

    /**
     * Returns this window's current view.
     *
     * @return the window's current view.
     */
    View getView();

    /**
     * Sets the model to display in this window and the view used to display it. If <code>null</code> is specified for
     * the model, the current model, if any, is disassociated with the window. If <code>null</code> is specified for the
     * view, the current view, if any, is disassociated with the window.
     *
     * @param model the model to display. May be<code>null</code>.
     * @param view  the view to use to display this window's model. May be<code>null</code>.
     */
    void setModelAndView(Model model, View view);

    /**
     * Returns the scene controller assocciated with this instance.
     *
     * @return The scene controller associated with the instance, or <code>null</code> if no scene controller is
     *         associated.
     */
    SceneController getSceneController();

    /**
     * Returns the input handler associated with this instance.
     *
     * @return The input handler associated with this instance, or <code>null</code> if no input handler is associated.
     */
    InputHandler getInputHandler();

    /**
     * Sets the input handler to use for this instance.
     *
     * @param inputHandler The input handler to use for this world window. May by <code>null</code> if <code>null</code>
     *                     is specified, the current input handler, if any, is disassociated with the world window.
     */
    void setInputHandler(InputHandler inputHandler);

    /**
     * Adds a rendering listener to this world window. Rendering listeners are called at key point during World Wind
     * drawing and provide applications the ability to participate or monitor rendering.
     *
     * @param listener The rendering listener to add to those notified of rendering events by this world window.
     */
    void addRenderingListener(RenderingListener listener);

    /**
     * Removes a specified rendering listener associated with this world window.
     *
     * @param listener The rendering listener to remove.
     */
    void removeRenderingListener(RenderingListener listener);

    /**
     * Adds a select listener to this world window. Select listeners are called when a selection is made by the user in
     * the world window. A selection is any operation that idetifies a visible item.
     *
     * @param listener The select listener to add.
     */
    void addSelectListener(SelectListener listener);

    /**
     * Removes the specified select listener associated with this world window.
     *
     * @param listener The select listener to remove.
     */
    void removeSelectListener(SelectListener listener);

    /**
     * Adds a position listener to this world window. Position listeners are called when the cursor's position changes.
     * They identify the position of the cursor on the globe, or that the cursor is not on the globe.
     *
     * @param listener The position listener to add.
     */
    void addPositionListener(PositionListener listener);

    /**
     * Removes the specified position listener associated with this world window.
     *
     * @param listener The listener to remove.
     */
    void removePositionListener(PositionListener listener);

    /**
     * Causes a repaint event to be enqued with the window system for this world window. The repaint will occur at the
     * window system's discretion, within the window system toolkit's event loop, and on the thread of that loop. This
     * is the preferred method for requesting a repaint of the world window.
     */
    void redraw();

    /**
     * Immediately repaints the world window without waiting for a window system repaint event. This is not the
     * preferred way to cause a repaint, but is provided for the rare cases that require it.
     */
    void redrawNow();

    /**
     * Returns the current latitude, longitude and altitude of the current cursor position, or <code>null</code> if the
     * cursor is not on the globe.
     *
     * @return The current position of the cursor, or <code>null</code> if the cursor is not positioned on the globe.
     */
    Position getCurrentPosition();

    /**
     * Returns the World Wind ojbects at the current cursor position. The list of objects under the cursor is determined
     * each time the world window is repainted. This method returns the list of objects determined when the most recent
     * repaint was performed.
     *
     * @return The list of objects at the cursor position, or <code>null</code> if no objects are under the cursor.
     */
    PickedObjectList getObjectsAtCurrentPosition();

    /**
     * Returns the texture cache used by this World Window.
     *
     * @return The texture cache used by the World Window.
     */
    TextureCache getTextureCache();

    /**
     * Activates the per-frame performance statistic specified. Per-frame statistics measure values within a single
     * frame of rendering, such as number of tiles drawn to produce the frame.
     *
     * @param keys The statistics to activate.
     */
    void setPerFrameStatisticsKeys(Set<String> keys);

    /**
     * Returns the active per-frame performance statistics such as number of tiles drawn in the most recent frame.
     *
     * @return The keys and values of the active per-frame statistics.
     */
    Collection<PerformanceStatistic> getPerFrameStatistics(); // TODO: move the constants from AVKey to this interface.

    /**
     * Causes resources used by the World Window to be freed. The World Window cannot be used once this method is
     * called.
     */
    void shutdown();

    /**
     * Adds an exception listener to this world window. Exception listeners are called when an exception or other
     * critical event occurs during drawable initialization or during rendering.
     *
     * @param listener the The exception listener to add.
     */
    void addRenderingExceptionListener(RenderingExceptionListener listener);

    /**
     * Removes the specified rendering exception listener associated with this world window.
     *
     * @param listener The listener to remove.
     */
    void removeRenderingExceptionListener(RenderingExceptionListener listener);
}
