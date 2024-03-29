package cn.yecols.wwj;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.imageio.ImageIO;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.SurfaceIcon;
import gov.nasa.worldwind.render.airspaces.Airspace;
import gov.nasa.worldwind.render.airspaces.Orbit;
import gov.nasa.worldwind.render.airspaces.PartialCappedCylinder;
import gov.nasa.worldwind.render.airspaces.Route;
import gov.nasa.worldwind.render.airspaces.SphereAirspace;

import org.jgrapht.graph.DefaultWeightedEdge;

import cn.yecols.obj.Path;
import cn.yecols.obj.Road;
import cn.yecols.obj.Simulator;

public class ManyDataLayerUtil {
	//辅助图层类
	
	private static RenderableLayer roadsLayer;	//道路网容纳图层
	private static Image destinationImage;		//终点小旗图示
	
	static void LoadStreetsDataLayer(Set<DefaultWeightedEdge> edgeset,WorldWindow wwd){
		//将道路网模型中的“边”，通过Road对象，画到视图中。

		roadsLayer=new RenderableLayer();
		roadsLayer.setName("Roads Layer");
		roadsLayer.setPickEnabled(false);
	
		for(DefaultWeightedEdge e:edgeset){
			roadsLayer.addRenderable(new Road((LatLon)e.getSource(),(LatLon)e.getTarget()));
		}
		
		//插入该道路表现图层
		LayerList layers = wwd.getModel().getLayers();
		int compassPosition = 0;
		for (Layer l : layers) {
			if(l.getName().equals("Measure Tool")||l.getName().equals("Roads Layer"))
				layers.remove(l);
			if(l.getName().equals("Scale bar"))
				compassPosition = layers.indexOf(l);
		}

		layers.add(compassPosition, roadsLayer);
	}
	
	
	
	static void UpdateStreetsDataLayer(Set<DefaultWeightedEdge> edgeset,WorldWindow wwd){

		//道路更新
		roadsLayer=new RenderableLayer();
		roadsLayer.setName("Roads Layer");
		roadsLayer.setPickEnabled(false);
	
		for(DefaultWeightedEdge e:edgeset){
			roadsLayer.addRenderable(new Road((LatLon)e.getSource(),(LatLon)e.getTarget()));
		}
		
		LayerList layers = wwd.getModel().getLayers();
		int compassPosition = 0;
		for (Layer l : layers) {
			if(l.getName().equals("Measure Tool")||l.getName().equals("Roads Layer"))
				layers.remove(l);
			if(l.getName().equals("Scale bar"))
				compassPosition = layers.indexOf(l);
		}

		layers.add(compassPosition, roadsLayer);
	}
	
	
	static void LoadSimulatorPath(Simulator simulator,WorldWindow wwd){
		//通过path对象，载入最短路径的表现图层。并在终点标识小旗帜。
		roadsLayer=new RenderableLayer();
		roadsLayer.setName("Simulator Layer");
		roadsLayer.setPickEnabled(false);
		
		for(DefaultWeightedEdge e:simulator.getEdges()){
			
			roadsLayer.addRenderable(new Path((LatLon)e.getSource(),(LatLon)e.getTarget()));
		}
		
		try {
			destinationImage=ImageIO.read(new File("src/cn/yecols/images/destination.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//添加终点标示
		SurfaceIcon surfaceIcon = new SurfaceIcon(destinationImage,simulator.getEnd());
		surfaceIcon.setOpacity(1);
		RenderableLayer destinationLayer=new RenderableLayer();
		destinationLayer.setName("Destination Icon");
		destinationLayer.addRenderable(surfaceIcon);
		
		//添加图层
		LayerList layers=wwd.getModel().getLayers();
		for(Layer l:layers){
			if(l.getName().equals("Simulator Layer"))
			layers.remove(l);
		}
		wwd.getModel().getLayers().add(1,roadsLayer);
		wwd.getModel().getLayers().add(destinationLayer);
		
	}
	
	static void LoadAirSpaceLayer(WorldWindow wwd){
		//添加3D抽象模型
		
		AirspaceLayer airspaceLayer=new AirspaceLayer();
		airspaceLayer.setName("AirSpace");
		airspaceLayer.setPickEnabled(false);
		
		//黄龙体育场
		PartialCappedCylinder partCyl = new PartialCappedCylinder();
	    partCyl.setCenter(LatLon.fromDegrees(30.2672, 120.1287));
	    partCyl.setAltitudes(0, 60);
	    partCyl.setTerrainConforming(false, false);
	    partCyl.setRadii(75.0, 125.0);
	    partCyl.setAzimuths(Angle.fromDegrees(0.0), Angle.fromDegrees(360.0));
	    setupDefaultMaterial(partCyl, Color.DARK_GRAY);
	    airspaceLayer.addAirspace(partCyl);
	    
	    //西湖体育馆
	    Route route = new Route();
        route.setAltitudes(0.0, 45.0);
        route.setWidth(45.0);
        route.setLocations(Arrays.asList(
            LatLon.fromDegrees(30.2697, 120.1251),
            LatLon.fromDegrees(30.2694, 120.1256)));
        route.setTerrainConforming(false, false);
        setupDefaultMaterial(route, Color.WHITE);
        airspaceLayer.addAirspace(route);
        
      //西湖网球馆
	    Route route2 = new Route();
        route2.setAltitudes(0.0, 50.0);
        route2.setWidth(80.0);
        route2.setLocations(Arrays.asList(
            LatLon.fromDegrees(30.2711, 120.1293),
            LatLon.fromDegrees(30.2700, 120.1293)));
        route2.setTerrainConforming(false, false);
        setupDefaultMaterial(route2, Color.WHITE);
        airspaceLayer.addAirspace(route2);
        
        
        //西湖网球馆
        Orbit orbit = new Orbit();
        orbit.setLocations(LatLon.fromDegrees(30.2692,120.1296), LatLon.fromDegrees(30.2692, 120.1304));
        orbit.setAltitudes(0, 40);
        orbit.setWidth(100.0);
        orbit.setOrbitType(Orbit.OrbitType.CENTER);
        orbit.setTerrainConforming(false, false);
        setupDefaultMaterial(orbit, Color.LIGHT_GRAY);
        airspaceLayer.addAirspace(orbit);
        
        //圆馆
        SphereAirspace sphere = new SphereAirspace();
        sphere.setLocation(LatLon.fromDegrees(30.2681, 120.1262));
        sphere.setAltitude(0);
        sphere.setTerrainConforming(false);
        sphere.setRadius(50.0);
        setupDefaultMaterial(sphere, Color.LIGHT_GRAY);
        airspaceLayer.addAirspace(sphere);
				
		LayerList layers=wwd.getModel().getLayers();
		for(Layer l:layers){
			if(l.getName().equals("AirSpace"))
			layers.remove(l);
		}
		wwd.getModel().getLayers().add(airspaceLayer);
		
	}
	
	public static RenderableLayer Emergency(int peopleNum){
		//根据输入的模拟人群参数选择创建相应的疏散点标示图层
		Emergency e=new Emergency();
		if(peopleNum<=Emergency.busOnlySum){
			return e.MakeLayerWithBusOnly();
		}
		else{
			return e.MakeLayerWithAll();
		}
	}
	
	
	
	private static void setupDefaultMaterial(Airspace a, Color color)
    {
        Color outlineColor = makeBrighter(color);

        a.getAttributes().setDrawOutline(true);
        a.getAttributes().setMaterial(new Material(color));
        a.getAttributes().setOutlineMaterial(new Material(outlineColor));
        a.getAttributes().setOpacity(0.8);
        a.getAttributes().setOutlineOpacity(0.9);
        a.getAttributes().setOutlineWidth(3.0);
    }

    private static Color makeBrighter(Color color)
    {
        float[] hsbComponents = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbComponents);
        float hue = hsbComponents[0];
        float saturation = hsbComponents[1];
        float brightness = hsbComponents[2];

        saturation /= 3f;
        brightness *= 3f;

        if (saturation < 0f)
            saturation = 0f;

        if (brightness > 1f)
            brightness = 1f;

        int rgbInt = Color.HSBtoRGB(hue, saturation, brightness);
        
        return new Color(rgbInt);
    }


}
