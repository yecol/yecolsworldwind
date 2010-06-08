package cn.yecols.obj;

import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.SurfaceIcon;

//�����ࡣ�̳��Ա���ͼ�꣬��ֱ����Ⱦ����������Ϊ��ǵ㡣
public class Place extends SurfaceIcon{
	private String placeName;			//��������
	private LatLon placeLocation;		//��������
	private String placeDesc;			//��������

	
	/*-----------------------------Constructors---------------------------------*/
	public Place(String placeName, LatLon placeLocation,Image imageSource) {
		super(imageSource, placeLocation);
		this.placeName = placeName;
		this.placeLocation = placeLocation;
		this.setOpacity(1);
	}

	public Place(String placeName, LatLon placeLocation, String placeDesc,Image imageSource) {
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
