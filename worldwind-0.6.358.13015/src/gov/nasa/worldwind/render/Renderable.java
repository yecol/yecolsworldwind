/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.render;

/**
 * @author Tom Gaskins
 * @version $Id: Renderable.java 2471 2007-07-31 21:50:57Z tgaskins $
 * @comments 可渲染类接口。可渲染意味着这个对象可以通过自身的DrawContext对象渲染。
 * 该对象中包含了一个高度模型、一个OG实例、一个球体等。yecol.2010.4.18.
 */
public interface Renderable
{
    /**
     * Causes this <code>Renderable</code> to render itself using the <code>DrawContext</code> provided. The
     * <code>DrawContext</code> provides the elevation model, openGl instance, globe and other information required for
     * drawing. It is recommended that the <code>DrawContext</code> is non-null as most implementations do not support
     * null <code>DrawContext</code>s.
     *
     * @param dc the <code>DrawContext</code> to be used
     * @see DrawContext
     */
    public void render(DrawContext dc);
}
