package cn.yecols.wwj;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import org.jgrapht.*;
import org.jgrapht.graph.*;

//从配置文件读取道路文件到程序
public class StreetsDataReader {
	private static File file;
	private static WorldWindow wwd;
	private static DocumentBuilder builder;
	private static WeightedGraph<LatLon, DefaultWeightedEdge> streetsGraph;		// 街道网
	private LatLon beginPosition, endPosition;									//街道的起始点和终止点
	private boolean tar;														//是否为线路起点的标示
	private LengthMeasurer lengthMeasurer;										//长度测量工具

	public StreetsDataReader(WorldWindow wwd) {
		//初始化函数
		file = new File("src/cn/yecols/geoHz.xml");//道路描述文件
		this.wwd = wwd;
		streetsGraph = new SimpleWeightedGraph(DefaultWeightedEdge.class);//创建一张新图
		lengthMeasurer = new LengthMeasurer();
		lengthMeasurer.setFollowTerrain(true);//设置测量路长时沿地表起伏
	}

	public WeightedGraph ReadStreetsData() throws SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			Document xmldoc = builder.parse(file);
			Element root = xmldoc.getDocumentElement();
			// 获得根元素。对应<paths>。

			NodeList paths = root.getChildNodes();
			// 路径列表。对应一个<path>列表。

			tar = false;
			for (int i = 0; i < paths.getLength(); i++) {
				Node path = paths.item(i);
				tar = false;
				// 对应一个<path>
				MeasureTool measureTool = new MeasureTool(wwd);
				//重新初始化一个测量工具，以测量路长作为路线的权重
				measureTool.setController(new MeasureToolController());
				measureTool.setFollowTerrain(true);
				measureTool.setMeasureShapeType(MeasureTool.SHAPE_PATH);
				measureTool.setShowControlPoints(false);
				measureTool.setArmed(true);

				if (path instanceof Element) {
					NodeList points = path.getChildNodes();

					for (int j = 0; j < points.getLength(); j++) {
						if (points.item(j) instanceof Element) {
							if (tar == false) {
								//当tar为假，说明是一条线路的起始点。
								beginPosition = new LatLon(Double
										.parseDouble(((Element) points.item(j))
												.getAttribute("lat")), Double
										.parseDouble(((Element) points.item(j))
												.getAttribute("lon")));
								streetsGraph.addVertex(beginPosition);
								tar = true;
								measureTool.addControlPoint(beginPosition);
								// measureTool.
							} else {
								//否则说明是一条线路的中间控制点。
								endPosition = new LatLon(Double
										.parseDouble(((Element) points.item(j))
												.getAttribute("lat")), Double
										.parseDouble(((Element) points.item(j))
												.getAttribute("lon")));
								streetsGraph.addVertex(endPosition);
								measureTool.addControlPoint(endPosition);

								lengthMeasurer.clearPositons();

								lengthMeasurer.addLine(beginPosition,
										endPosition);

								try {
								//将街道添加到图，并设置路长作为权重。
									streetsGraph.setEdgeWeight(
											streetsGraph.addEdge(beginPosition,
													endPosition),
											lengthMeasurer.computeLength(wwd
													.getModel().getGlobe(),
													true));
								} catch (Exception e) {
									System.out.println("Edge exists already.");
								}
								beginPosition = endPosition;

							}

						}
					}
				}
				measureTool.setArmed(false);
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return streetsGraph;
	}

}
