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

//�������ļ���ȡ��·�ļ�������
public class StreetsDataReader {
	private static File file;
	private static WorldWindow wwd;
	private static DocumentBuilder builder;
	private static WeightedGraph<LatLon, DefaultWeightedEdge> streetsGraph;		// �ֵ���
	private LatLon beginPosition, endPosition;									//�ֵ�����ʼ�����ֹ��
	private boolean tar;														//�Ƿ�Ϊ��·���ı�ʾ
	private LengthMeasurer lengthMeasurer;										//���Ȳ�������

	public StreetsDataReader(WorldWindow wwd) {
		//��ʼ������
		file = new File("src/cn/yecols/geoHz.xml");//��·�����ļ�
		this.wwd = wwd;
		streetsGraph = new SimpleWeightedGraph(DefaultWeightedEdge.class);//����һ����ͼ
		lengthMeasurer = new LengthMeasurer();
		lengthMeasurer.setFollowTerrain(true);//���ò���·��ʱ�صر����
	}

	public WeightedGraph ReadStreetsData() throws SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			Document xmldoc = builder.parse(file);
			Element root = xmldoc.getDocumentElement();
			// ��ø�Ԫ�ء���Ӧ<paths>��

			NodeList paths = root.getChildNodes();
			// ·���б���Ӧһ��<path>�б�

			tar = false;
			for (int i = 0; i < paths.getLength(); i++) {
				Node path = paths.item(i);
				tar = false;
				// ��Ӧһ��<path>
				MeasureTool measureTool = new MeasureTool(wwd);
				//���³�ʼ��һ���������ߣ��Բ���·����Ϊ·�ߵ�Ȩ��
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
								//��tarΪ�٣�˵����һ����·����ʼ�㡣
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
								//����˵����һ����·���м���Ƶ㡣
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
								//���ֵ���ӵ�ͼ��������·����ΪȨ�ء�
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
