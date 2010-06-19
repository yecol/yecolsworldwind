package cn.yecols.wwj;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceImage;

public class ImageDataLayerUtil {
	//一个加载表面图像的类，加载了杭州地区的详细地图。用于图像精度优化。
	private RenderableLayer layer;
	public ImageDataLayerUtil(){
		
		layer = new RenderableLayer();
		layer.setName("yecol's images date");
		layer.setPickEnabled(false);
		
    	SurfaceImage si1 = new SurfaceImage("cn/yecols/images/ywwj001.jpg", Sector.fromDegrees(30.2613,30.2874,120.0785,120.1346));
    	SurfaceImage si2 = new SurfaceImage("cn/yecols/images/ywwj002.jpg", Sector.fromDegrees(30.2613,30.2884,120.1304,120.1899));
    	
    	SurfaceImage si5 = new SurfaceImage("cn/yecols/images/ywwj005.jpg", Sector.fromDegrees(30.2347,30.2642,120.0672,120.1317));
    	SurfaceImage si6 = new SurfaceImage("cn/yecols/images/ywwj006.jpg", Sector.fromDegrees(30.2641,30.2888,120.1862,120.2393));
    	SurfaceImage si7 = new SurfaceImage("cn/yecols/images/ywwj007.jpg", Sector.fromDegrees(30.2389,30.2661,120.1274,120.1870));
    	SurfaceImage si8 = new SurfaceImage("cn/yecols/images/ywwj008.jpg", Sector.fromDegrees(30.2421,30.2678,120.1805,120.2407));
    	SurfaceImage si9 = new SurfaceImage("cn/yecols/images/ywwj009.jpg", Sector.fromDegrees(30.2154,30.2445,120.0709,120.1369));
    	SurfaceImage si10 = new SurfaceImage("cn/yecols/images/ywwj010.jpg", Sector.fromDegrees(30.2182,30.2414,120.1368,120.1883));    
    	SurfaceImage si11 = new SurfaceImage("cn/yecols/images/ywwj011.jpg", Sector.fromDegrees(30.1926,30.2214,120.1136,120.1768));    
    	
        layer.addRenderable(si11);
        layer.addRenderable(si10);
        layer.addRenderable(si9);
        layer.addRenderable(si8); 
        layer.addRenderable(si7);
        layer.addRenderable(si6);
        layer.addRenderable(si5);
        layer.addRenderable(si2);
        layer.addRenderable(si1);
        
	}
	
	public RenderableLayer getLayer(){
		return layer;
	}

	
	

}
