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
	//�̳���Ӧ�ó���ģ����
	public static class AppFrame extends ApplicationTemplate.AppFrame {
		
		//����AppFrame
		private final JPanel leftJPanel=new JPanel();
		private TerrainProfileLayer profile = new TerrainProfileLayer();//��������С���
		private PropertyChangeListener measureToolListener = new MeasureToolListener();

		public AppFrame() {

			super(true, false, false); //������������ֻ��Ҫ�������е�WorldWindow���
			
			profile.setEventSource(getWwd());					//�¼���Դ
			profile.setFollow(TerrainProfileLayer.FOLLOW_PATH);	//�صر����
			profile.setShowProfileLine(false);					
			insertBeforePlacenames(getWwd(), profile);			//���С���

			
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
		Configuration.setValue(AVKey.INITIAL_LATITUDE, 30.2387);		//�����е�γ��
		Configuration.setValue(AVKey.INITIAL_LONGITUDE, 120.1472);		//�����е㾭��
		Configuration.setValue(AVKey.INITIAL_ALTITUDE, 20e3);			//�ӽǸ߶�

		ApplicationTemplate.start("����NASA WorldWind��GIS�ں�ϵͳ�о�",
				YecolsApplication.AppFrame.class);
	}

}
