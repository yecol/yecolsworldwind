package cn.yecols.wwj;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Annotation;
import gov.nasa.worldwind.render.AnnotationShadow;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.SurfaceIcon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import cn.yecols.obj.Place;
import cn.yecols.obj.Stop;

public class Emergency {
	// 紧急预案类
	private ArrayList<Stop> busStop; // 公交站牌点
	private ArrayList<Stop> tempStop; // 临时送客点
	private RenderableLayer stopsDataLayer; // 图层
	private static Image busImage; // 用于公交站点的图标
	private static Image tempImage; // 用于临时送客点的图标
	public static final int busOnlySum = 10000; // 只需公交送客的阈值
	public static final int allSum = 50000; // 启用所有送客点的阈值
	public static final int alarmSum = 50000; // 警报阈值

	public Emergency() {
		// 构造函数。在函数中完成初始化数组，读取图标等工作。
		busStop = new ArrayList<Stop>();
		tempStop = new ArrayList<Stop>();
		stopsDataLayer = new RenderableLayer();
		stopsDataLayer.setName("Emergency");
		stopsDataLayer.setMaxActiveAltitude(7000);
		try {
			busImage = ImageIO.read(new File("src/cn/yecols/images/bus.png"));
			tempImage = ImageIO.read(new File("src/cn/yecols/images/temp.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Initiation();
	}

	public void Initiation() {
		//初始化函数，在本函数中填充两个数组变量的内容。
		busStop.add(new Stop("黄龙洞", new LatLon(30.2652, 120.1312), "K193",
				busImage));
		busStop.add(new Stop("黄龙公交中心", new LatLon(30.2673, 120.1320), "K193",
				busImage));
		busStop.add(new Stop("求是路", new LatLon(30.2659, 120.1238), "K193,",
				busImage));
		busStop.add(new Stop("玉古路口", new LatLon(30.2686, 120.1235), "K193,",
				busImage));
		busStop.add(new Stop("天目山路学院路口", new LatLon(30.2730, 120.1235),
				"K193,", busImage));
		busStop.add(new Stop("西湖体育馆", new LatLon(30.2692, 120.1247), "K193,",
				busImage));
		busStop.add(new Stop("跑马场", new LatLon(30.2716, 120.1316), "K193,",
				busImage));
		busStop.add(new Stop("黄龙公交中心站", new LatLon(30.2717, 120.1329), "K193,",
				busImage));
		busStop.add(new Stop("庆丰村", new LatLon(30.2726, 120.1331), "K193,",
				busImage));
		busStop.add(new Stop("浙大附中路", new LatLon(30.2625, 120.1274), "K193,",
				busImage));

		tempStop.add(new Stop("临时送客点", new LatLon(30.2645, 120.1276), " ",
				tempImage));
		tempStop.add(new Stop("临时送客点", new LatLon(30.2651, 120.1261), " ",
				tempImage));
		tempStop.add(new Stop("临时送客点", new LatLon(30.2646, 120.1296), " ",
				tempImage));
		tempStop.add(new Stop("临时送客点", new LatLon(30.2684, 120.1319), " ",
				tempImage));
		tempStop.add(new Stop("临时送客点", new LatLon(30.2704, 120.1271), " ",
				tempImage));
		tempStop.add(new Stop("临时送客点", new LatLon(30.2705, 120.1243), " ",
				tempImage));
		tempStop.add(new Stop("临时送客点", new LatLon(30.2727, 120.1267), " ",
				tempImage));
		tempStop.add(new Stop("临时送客点", new LatLon(30.2706, 120.1317), " ",
				tempImage));
		tempStop.add(new Stop("临时送客点", new LatLon(30.2684, 120.1319), " ",
				tempImage));
	}

	public void Visualize(WorldWindow wwd) {		
		//将本累的图层变量显示到参数的WorldWindow中。
		wwd.getModel().getLayers().add(stopsDataLayer);
	}

	public RenderableLayer MakeLayerWithBusOnly() {
		//在图层中只添加公交图标类
		stopsDataLayer.removeAllRenderables();
		for (Stop s : busStop) {
			stopsDataLayer.addRenderable(s);
		}
		return stopsDataLayer;
	}

	public RenderableLayer MakeLayerWithAll() {
		//在图层中添加公交图标和临时送客点类
		stopsDataLayer.removeAllRenderables();
		for (Stop s : busStop) {
			stopsDataLayer.addRenderable(s);
		}
		for (Stop s : tempStop) {
			stopsDataLayer.addRenderable(s);
		}
		return stopsDataLayer;
	}

	public Renderable MakeBusMarker(Stop s) throws IOException {

		final SurfaceIcon surfaceIcon = new SurfaceIcon(busImage, s
				.getPlaceLocation());
		return surfaceIcon;
	}

	public Renderable MakeTempMarker(Stop s) throws IOException {

		final SurfaceIcon surfaceIcon = new SurfaceIcon(tempImage, s
				.getPlaceLocation());
		return surfaceIcon;
	}

	public RenderableLayer getPlacesDataLayer() {
		//返回该图层
		return stopsDataLayer;
	}

	public static GlobeAnnotation MakeStopAnnotation(Stop place) {
		//为该站点添加标注
		//没有调用
		GlobeAnnotation ga = new AnnotationShadow(
				"<p>\n<b><font color=\"#664400\">"
						+ place.getPlaceName()
						+ "</font></b></p>\n<p>\n"
						+ "<img src=\"http://yecols.cn/photos/buaagraduate/images/DSC00091-thumb.JPG\" />"
						+ place.getPlaceDesc() + "\n</p>", place
						.getPlacePosition());
		ga.getAttributes().setFont(Font.decode("SansSerif-PLAIN-14"));
		ga.getAttributes().setTextColor(Color.BLACK);
		ga.getAttributes().setTextAlign(AVKey.RIGHT);
		ga.getAttributes().setBackgroundColor(new Color(1f, 1f, 1f, .8f));
		ga.getAttributes().setBorderColor(Color.BLACK);
		ga.getAttributes().setSize(new Dimension(180, 0));
		ga.getAttributes().setImageSource("src/cn/yecols/images/hz.png");
		ga.getAttributes().setImageRepeat(Annotation.IMAGE_REPEAT_NONE);
		ga.getAttributes().setImageOpacity(1);
		ga.getAttributes().setImageScale(1);
		ga.getAttributes().setImageOffset(new Point(12, 12));

		ga.getAttributes().setInsets(new Insets(12, 20, 20, 12));
		return ga;
	}

}
