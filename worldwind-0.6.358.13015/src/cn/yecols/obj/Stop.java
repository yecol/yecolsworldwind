package cn.yecols.obj;

import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.SurfaceIcon;

//公交站点类。继承自表面图标，可直接渲染到球面上作为标记点。
public class Stop extends SurfaceIcon{
	private String placeName;			//公交线路名称
	private LatLon placeLocation;		//公交站点坐标
	private String placeDesc;			//公交线路描述

	
	/*-----------------------------Constructors---------------------------------*/
	public Stop(String placeName, LatLon placeLocation,Image imageSource) {
		super(imageSource, placeLocation);
		this.placeName = placeName;
		this.placeLocation = placeLocation;
		this.setOpacity(1);
	}

	public Stop(String placeName, LatLon placeLocation, String placeDesc,Image imageSource) {
		super(imageSource,placeLocation);
		this.placeName = placeName;
		this.placeLocation = placeLocation;
		this.placeDesc = placeDesc;
		this.setOpacity(1);
	}

	/*-----------------------------Getter & Setter------------------------------*/
	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public LatLon getPlaceLocation() {
		return placeLocation;
	}
	
	public Position getPlacePosition(){
		return new Position(placeLocation,0);
	}

	public void setPlaceLocation(LatLon placeLocation) {
		this.placeLocation = placeLocation;
	}

	public String getPlaceDesc() {
		return placeDesc;
	}

	public void setPlaceDesc(String placeDesc) {
		this.placeDesc = placeDesc;
	}
	
	/*-----------------------------Functions--------------------------------*/
	
	public String toString(){
		return this.placeName;
	}

}
