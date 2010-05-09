package cn.yecols.wwj;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.WeightedGraph;
import org.xml.sax.SAXException;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.util.measure.MeasureTool;



public class LeftControlPanel extends JPanel {
	private final WorldWindow wwd;
	private final MeasureTool measureTool;
	
	private WeightedGraph g;
	
	private JButton loadStreetsData;

	public LeftControlPanel(WorldWindow wwdObject, MeasureTool measureToolObject) {
		super(new BorderLayout());
		this.wwd = wwdObject;
		this.measureTool = measureToolObject;
		this.makePanel(new Dimension(200, 300));
	}

	private void makePanel(Dimension dimension) {
		// TODO Auto-generated method stub
		loadStreetsData=new JButton("装载杭州街道数据");
		loadStreetsData.addActionListener(new ActionListener(){
			 public void actionPerformed(ActionEvent event){
				System.out.println("loading streets data");
				StreetsDataReader streetsDataReader=new StreetsDataReader();
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
		add(loadStreetsData);
		
	}

}
