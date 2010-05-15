package cn.yecols.wwj;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public class StreetsDataReader {
	private static File file;
	private static WorldWindow wwd;
	private static DocumentBuilder builder;
	private static WeightedGraph<LatLon, DefaultWeightedEdge> streetsGraph;
	private LatLon beginPosition, endPosition;
	private boolean tar;
	private LengthMeasurer lengthMeasurer;

	public StreetsDataReader(WorldWindow wwd) {
		file = new File("src/cn/yecols/geoHz.xml");
		this.wwd=wwd;
		streetsGraph = new SimpleWeightedGraph(DefaultWeightedEdge.class);
		lengthMeasurer = new LengthMeasurer();
		lengthMeasurer.setFollowTerrain(true);
	}

	public WeightedGraph ReadStreetsData() throws SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			Document xmldoc = builder.parse(file);
			Element root = xmldoc.getDocumentElement();
			// 获得根元素。对应<paths>。

			// System.out.println(root.hasChildNodes());
			NodeList paths = root.getChildNodes();
			// 路径列表。对应一个<path>列表。

			tar = false;

			for (int i = 0; i < paths.getLength(); i++) {
				Node path = paths.item(i);
				// 对应一个<path>
				MeasureTool measureTool = new MeasureTool(wwd);
				measureTool.setController(new MeasureToolController());
				measureTool.setFollowTerrain(true);
				measureTool.setMeasureShapeType(MeasureTool.SHAPE_PATH);
				measureTool.setShowControlPoints(false);
				measureTool.setArmed(true);

				if (path instanceof Element) {
					NodeList points = path.getChildNodes();
					// 对应一个<position>列表
					// System.out.println(points.toString());

					for (int j = 0; j < points.getLength(); j++) {
						if (points.item(j) instanceof Element) {
							if (tar == false) {
								beginPosition = new LatLon(Double
										.parseDouble(((Element) points.item(j))
												.getAttribute("lat")), Double
										.parseDouble(((Element) points.item(j))
												.getAttribute("lon")));
								streetsGraph.addVertex(beginPosition);
								tar = true;
								measureTool.addControlPoint(beginPosition);
							} else {
								endPosition = new LatLon(Double
										.parseDouble(((Element) points.item(j))
												.getAttribute("lat")), Double
										.parseDouble(((Element) points.item(j))
												.getAttribute("lon")));
								streetsGraph.addVertex(endPosition);
								measureTool.addControlPoint(endPosition);
								
								lengthMeasurer.clearPositons();
								//System.out.println(lengthMeasurer.getPositions().toString());
								
								lengthMeasurer.addLine(beginPosition, endPosition);
								//System.out.println(lengthMeasurer.getPositions().toString());
								
								
							
								System.out.println(lengthMeasurer.computeLength(wwd.getModel().getGlobe(), true));
								//streetsGraph
								//		.addEdge(beginPosition, endPosition,lengthMeasurer.computeLength(wwd.getModel().getGlobe(), true));
								//streetsGraph.addEdge(sourceVertex, targetVertex)
								try{
							    streetsGraph.setEdgeWeight(streetsGraph
										.addEdge(beginPosition, endPosition), lengthMeasurer.computeLength(wwd.getModel().getGlobe(), true));
								}
								catch(Exception e){
									System.out.println("Edge exists already.");
								}
								beginPosition = endPosition;
							}

							System.out.println(((Element) points.item(j))
									.getAttribute("lat"));
							System.out.println(((Element) points.item(j))
									.getAttribute("lon"));

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
