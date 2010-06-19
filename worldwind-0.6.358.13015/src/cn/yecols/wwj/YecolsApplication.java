/*
Copyright (C) 2001, 2008 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
 */
package cn.yecols.wwj;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey; //import gov.nasa.worldwind.examples.MeasureToolPanel;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.TerrainProfileLayer;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;
import gov.nasa.worldwind.view.firstperson.BasicFlyView;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class YecolsApplication extends ApplicationTemplate {
	//继承自应用程序模板类
	public static class AppFrame extends ApplicationTemplate.AppFrame {
		
		//集成AppFrame
		private final JPanel leftJPanel=new JPanel();
		private TerrainProfileLayer profile = new TerrainProfileLayer();//地形资料小面板
		private PropertyChangeListener measureToolListener = new MeasureToolListener();

		public AppFrame() {

			super(true, false, false); //三个参数表明只需要引用其中的WorldWindow面板
			
			profile.setEventSource(getWwd());					//事件来源
			profile.setFollow(TerrainProfileLayer.FOLLOW_PATH);	//沿地表起伏
			profile.setShowProfileLine(false);					
			insertBeforePlacenames(getWwd(), profile);			//添加小面板

			
			MeasureTool measureTool = new MeasureTool(this.getWwd());
			measureTool.setController(new MeasureToolController());
			
			leftJPanel.add(new LeftControlPanel(this.getWwd(), measureTool));

			this.getContentPane().add(leftJPanel, BorderLayout.WEST);
			this.pack();
		}

		private class MeasureToolListener implements PropertyChangeListener {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getPropertyName().equals(
						MeasureTool.EVENT_POSITION_ADD)
						|| event.getPropertyName().equals(
								MeasureTool.EVENT_POSITION_REMOVE)
						|| event.getPropertyName().equals(
								MeasureTool.EVENT_POSITION_REPLACE)) {
					updateProfile(((MeasureTool) event.getSource()));
				}
			}
		}

		

		private void updateProfile(MeasureTool mt) {
			ArrayList<? extends LatLon> positions = mt.getPositions();
			if (positions != null && positions.size() > 1) {
				profile.setPathPositions(positions);
				profile.setEnabled(true);
			} else
				profile.setEnabled(false);

			getWwd().redraw();
		}
	}

	public static void main(String[] args) {
		Configuration.setValue(AVKey.INITIAL_LATITUDE, 30.2387);		//窗口中点纬度
		Configuration.setValue(AVKey.INITIAL_LONGITUDE, 120.1472);		//窗口中点经度
		Configuration.setValue(AVKey.INITIAL_ALTITUDE, 20e3);			//视角高度

		ApplicationTemplate.start("基于NASA WorldWind的GIS融合系统研究",
				YecolsApplication.AppFrame.class);
	}

}
