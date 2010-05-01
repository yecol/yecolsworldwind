package cn.yecols.wwj;

import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceImage;

public class ImageDataLayerUtil {
	private RenderableLayer layer;
	public ImageDataLayerUtil(){
		
		layer = new RenderableLayer();
		
    	SurfaceImage si1 = new SurfaceImage("cn/yecols/images/ywwj001.jpg", Sector.fromDegrees(30.2613,30.2874,120.0785,120.1346));
    	SurfaceImage si2 = new SurfaceImage("cn/yecols/images/ywwj002.jpg", Sector.fromDegrees(30.2608,30.2873,120.1352,120.1936));
    	SurfaceImage si3 = new SurfaceImage("cn/yecols/images/ywwj003.jpg", Sector.fromDegrees(30.2414,30.2591,120.0795,120.1186));
    	SurfaceImage si4 = new SurfaceImage("cn/yecols/images/ywwj004.jpg", Sector.fromDegrees(30.2426,30.2779,120.0579,120.1356));
    	SurfaceImage si5 = new SurfaceImage("cn/yecols/images/ywwj005.jpg", Sector.fromDegrees(30.2347,30.2642,120.0672,120.1317));
    	
        layer.addRenderable(si1);
        layer.addRenderable(si2);
        //layer.addRenderable(si3);
        //layer.addRenderable(si4);
        layer.addRenderable(si5);
	}
	
	public RenderableLayer getLayer(){
		return layer;
	}

	
	

}
