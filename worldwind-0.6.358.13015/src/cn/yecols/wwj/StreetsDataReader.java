package cn.yecols.wwj;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.util.measure.MeasureTool;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public class StreetsDataReader {
	private static File file;
	private static DocumentBuilder builder;
	private static WeightedGraph<LatLon, DefaultWeightedEdge> streetsGraph;
	private LatLon beginPosition, endPosition;
	private boolean tar;

	public StreetsDataReader() {
		file = new File("src/cn/yecols/geoHz.xml");
		streetsGraph = new SimpleWeightedGraph(DefaultWeightedEdge.class);
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
				//MeasureTool measureTool = new MeasureTool();

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
							} else {
								endPosition = new LatLon(Double
										.parseDouble(((Element) points.item(j))
												.getAttribute("lat")), Double
										.parseDouble(((Element) points.item(j))
												.getAttribute("lon")));
								streetsGraph.addVertex(endPosition);
								streetsGraph
										.addEdge(beginPosition, endPosition);
								beginPosition = endPosition;
							}

							System.out.println(((Element) points.item(j))
									.getAttribute("lat"));
							System.out.println(((Element) points.item(j))
									.getAttribute("lon"));

							// streetsGraph.addEdge(sourceVertex, targetVertex,
							// e)
							// streetsGraph.

						}
					}
				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return streetsGraph;
	}

}
