package gov.nasa.worldwind.examples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Annotation;
import gov.nasa.worldwind.render.AnnotationShadow;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.SurfaceIcon;

public class YecolsAnnotationData {
	private Position dPosition;
	private String dTitle;
	private String dContent;

	public YecolsAnnotationData(Position position, String title, String content) {
		this.dPosition = position;
		this.dTitle = title;
		this.dContent = content;
	}

	public GlobeAnnotation MakeGlobeAnnotation() {
		GlobeAnnotation ga = new AnnotationShadow(
				"<p>\n<b><font color=\"#664400\">" + this.dTitle
						+ "</font></b><br />\n<i>Alt: 2549m</i>\n</p>\n<p>\n"
						+ this.dContent + "\n</p>", this.dPosition);
		ga.getAttributes().setFont(Font.decode("SansSerif-PLAIN-14"));
		ga.getAttributes().setTextColor(Color.BLACK);
		ga.getAttributes().setTextAlign(AVKey.RIGHT);
		ga.getAttributes().setBackgroundColor(new Color(1f, 1f, 1f, .7f));
		ga.getAttributes().setBorderColor(Color.BLACK);
		ga.getAttributes().setSize(new Dimension(180, 0));
		ga.getAttributes().setImageSource("images/32x32-icon-earth.png");
		ga.getAttributes().setImageRepeat(Annotation.IMAGE_REPEAT_NONE);
		ga.getAttributes().setImageOpacity(.6);
		ga.getAttributes().setImageScale(.7);
		ga.getAttributes().setImageOffset(new Point(12, 12));
		ga.getAttributes().setInsets(new Insets(12, 20, 20, 12));
		return ga;
	}

	public SurfaceIcon MakeSurfaceIcon() {
		SurfaceIcon icon;
		// icon = new SurfaceIcon("images/gps32px.png", LatLon.fromDegrees(46,
		// -121));
		icon = new SurfaceIcon("images/gps_10.png", this.dPosition);
		icon.setOpacity(1);
		icon.setScale(.5);
		icon.setMaxSize(50e5);
		return icon;
	}
	
	public String toString(){
		return "Position: "+this.dPosition.toString()+" ;";
	}

}
