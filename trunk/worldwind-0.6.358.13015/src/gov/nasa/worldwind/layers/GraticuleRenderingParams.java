/*
Copyright (C) 2001, 2007 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.layers;

import gov.nasa.worldwind.avlist.AVListImpl;
import gov.nasa.worldwind.util.Logging;

import java.awt.*;

/**
 * @author dcollins
 * @version $Id: GraticuleRenderingParams.java 11758 2009-06-19 10:02:02Z patrickmurris $
 * @comments 地理网格线渲染参数读取与设置。yecol.2010.4.28.
 */
public class GraticuleRenderingParams extends AVListImpl
{
    public static final String KEY_DRAW_LINES = "DrawGraticule";//是否画线，1或者0。
    public static final String KEY_LINE_COLOR = "GraticuleLineColor";//地理网格线颜色
    public static final String KEY_LINE_WIDTH = "GraticuleLineWidth";//地理网格线宽度
    public static final String KEY_LINE_STYLE = "GraticuleLineStyle";//地理网格线类型
    public static final String KEY_LINE_CONFORMANCE = "GraticuleLineConformance";//
    public static final String KEY_DRAW_LABELS = "DrawLabels";//是否画标签
    public static final String KEY_LABEL_COLOR = "LabelColor";//标签颜色
    public static final String KEY_LABEL_FONT = "LabelFont";//标签字体
    public static final String VALUE_LINE_STYLE_SOLID = "LineStyleSolid";//线条类型
    public static final String VALUE_LINE_STYLE_DASHED = "LineStyleDashed";
    public static final String VALUE_LINE_STYLE_DOTTED = "LineStyleDotted";

    public GraticuleRenderingParams()
    {
    }

    public boolean isDrawLines()
    {
        Object value = getValue(KEY_DRAW_LINES);
        return value instanceof Boolean ? (Boolean) value : false;
    }

    public void setDrawLines(boolean drawLines)
    {
        setValue(KEY_DRAW_LINES, drawLines);
    }

    public Color getLineColor()
    {
        Object value = getValue(KEY_LINE_COLOR);
        return value instanceof Color ? (Color) value : null;
    }

    public void setLineColor(Color color)
    {
        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        setValue(KEY_LINE_COLOR, color);
    }

    public double getLineWidth()
    {

        Object value = getValue(KEY_LINE_WIDTH);
        return value instanceof Double ? (Double) value : 0;
    }

    public void setLineWidth(double lineWidth)
    {
        setValue(KEY_LINE_WIDTH, lineWidth);
    }

    public String getLineStyle()
    {
        Object value = getValue(KEY_LINE_STYLE);
        return value instanceof String ? (String) value : null;
    }

    public void setLineStyle(String lineStyle)
    {
        if (lineStyle == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        setValue(KEY_LINE_STYLE, lineStyle);
    }

    public boolean isDrawLabels()
    {
        Object value = getValue(KEY_DRAW_LABELS);
        return value instanceof Boolean ? (Boolean) value : false;
    }

    public void setDrawLabels(boolean drawLabels)
    {
        setValue(KEY_DRAW_LABELS, drawLabels);
    }

    public Color getLabelColor()
    {
        Object value = getValue(KEY_LABEL_COLOR);
        return value instanceof Color ? (Color) value : null;
    }

    public void setLabelColor(Color color)
    {
        if (color == null)
        {
            String message = Logging.getMessage("nullValue.ColorIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        setValue(KEY_LABEL_COLOR, color);
    }

    public Font getLabelFont()
    {
        Object value = getValue(KEY_LABEL_FONT);
        return value instanceof Font ? (Font) value : null;
    }

    public void setLabelFont(Font font)
    {
        if (font == null)
        {
            String message = Logging.getMessage("nullValue.FontIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        setValue(KEY_LABEL_FONT, font);
    }
}
