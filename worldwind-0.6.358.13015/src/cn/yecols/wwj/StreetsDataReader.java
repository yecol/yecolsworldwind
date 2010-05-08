package cn.yecols.wwj;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;

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
	private LatLon beginPosition,endPosition;
	

	public StreetsDataReader() {
		file = new File("src/cn/yecols/geoHz.xml");
		streetsGraph = new SimpleWeightedGraph(DefaultWeightedEdge.class);
	}

	void ReadStreetData() throws SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			Document xmldoc = builder.parse(file);
			Element root = xmldoc.getDocumentElement();
			// ��ø�Ԫ�ء���Ӧ<paths>��

			// System.out.println(root.hasChildNodes());
			NodeList paths = root.getChildNodes();
			// ·���б���Ӧһ��<path>�б�

			for (int i = 0; i < paths.getLength(); i++) {
				Node path = paths.item(i);
				// ��Ӧһ��<path>

				if (path instanceof Element) {
					NodeList points = path.getChildNodes();
					// ��Ӧһ��<position>�б�
					// System.out.println(points.toString());
					
                for (int j = 0; j < points.getLength(); j++) {
						if (points.item(j) instanceof Element) {
							System.out.println(((Element) points.item(j))
									.getAttribute("lat"));
						
							
							//streetsGraph.addEdge(sourceVertex, targetVertex, e)
							//streetsGraph.
							
						}
					}
				}
			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
