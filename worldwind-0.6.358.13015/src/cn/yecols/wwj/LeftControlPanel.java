package cn.yecols.wwj;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.xml.sax.SAXException;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;



public class LeftControlPanel extends JPanel {
	private final WorldWindow wwd;
	private final MeasureTool measureTool;
	
	private WeightedGraph g;
	private LatLon inputPathBegin;
	private LatLon inputPathEnd;
	
	private JButton loadStreetsData;
	private JButton startPosition;
	private JButton computeShortestPath;

	public LeftControlPanel(WorldWindow wwdObject, MeasureTool measureToolObject) {
		super(new FlowLayout());
		this.wwd = wwdObject;
		this.measureTool = measureToolObject;
		this.makePanel(new Dimension(200, 300));
		
		//test shortest Path
		inputPathBegin=new LatLon(30.2691,120.1021);
		inputPathEnd=new LatLon(30.2412,120.1281);
	}

	private void makePanel(Dimension dimension) {
		// TODO Auto-generated method stub
		loadStreetsData=new JButton("装载杭州街道数据");
		loadStreetsData.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent event){
				System.out.println("loading streets data");
				StreetsDataReader streetsDataReader=new StreetsDataReader(wwd);
				try {
					g=streetsDataReader.ReadStreetsData();
					System.out.println(g.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Failed to Read xml or build a weighted graph.");
				}
			 }

		});
		
		computeShortestPath=new JButton("获得路径");
		computeShortestPath.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				if(inputPathBegin==null||inputPathEnd==null)
					System.out.println("Not input begin or end point yet.");
				else if(g==null)
					System.out.println("No graph builded yet.");
				else{
					DijkstraShortestPath shortestPath=new DijkstraShortestPath(g,inputPathBegin,inputPathEnd);
					System.out.println(shortestPath.getPathLength());
					System.out.println(shortestPath.getPathEdgeList().toString());
					MeasureTool measureTool = new MeasureTool(wwd);
					measureTool.setController(new MeasureToolController());
					measureTool.setFollowTerrain(true);
					measureTool.setMeasureShapeType(MeasureTool.SHAPE_PATH);
					measureTool.setShowControlPoints(false);
					measureTool.setArmed(true);
					for(DefaultEdge e : (List<DefaultEdge>)shortestPath.getPathEdgeList()){
						System.out.println(e.toString());
					
						
					}
				}
					
			}
		});
		
		
		add(loadStreetsData);
		add(computeShortestPath);
		
	}

}
