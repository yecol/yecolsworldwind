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

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Example usage of MeasureTool, MeasureToolController and MeasureToolPanel.
 * 
 * @author Patrick Murris
 * @version $Id: MeasureToolUsage.java 12530 2009-08-29 17:55:54Z jterhorst $
 * @see gov.nasa.worldwind.util.measure.MeasureTool
 * @see gov.nasa.worldwind.util.measure.MeasureToolController
 * @see MeasureToolPanel
 */
public class YecolsApplication extends ApplicationTemplate {
	public static class AppFrame extends ApplicationTemplate.AppFrame {
		//private int lastTabIndex = -1;
		//private final JTabbedPane tabbedPane = new JTabbedPane();
		private final JPanel leftJPanel=new JPanel();
		private TerrainProfileLayer profile = new TerrainProfileLayer();
		private PropertyChangeListener measureToolListener = new MeasureToolListener();

		//private PrintWriter printWriter;// yecols.util to produce xml.

		public AppFrame() {

			super(true, false, false); // no layer panel

			// Add terrain profile layer
			profile.setEventSource(getWwd());
			profile.setFollow(TerrainProfileLayer.FOLLOW_PATH);
			profile.setShowProfileLine(false);
			insertBeforePlacenames(getWwd(), profile);

			// Add + tab
			//tabbedPane.add(new JPanel());
			//tabbedPane.setTitleAt(0, "+");
			/*tabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent changeEvent) {
					if (tabbedPane.getSelectedIndex() == 0) {
						// Add new measure tool in a tab when '+' selected
						MeasureTool measureTool = new MeasureTool(getWwd());
						measureTool.setController(new MeasureToolController());
						tabbedPane.add(new MeasureToolPanel(getWwd(),
								measureTool, printWriter));
						tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1, ""
								+ (tabbedPane.getTabCount() - 1));
						tabbedPane
								.setSelectedIndex(tabbedPane.getTabCount() - 1);
						switchMeasureTool();
					} else {
						switchMeasureTool();
					}
				}
			});*/

			// Add measure tool control panel to tabbed pane
			MeasureTool measureTool = new MeasureTool(this.getWwd());
			measureTool.setController(new MeasureToolController());
			//leftJPanel.add(new MeasureToolPanel(this.getWwd(), measureTool,printWriter));
			leftJPanel.add(new LeftControlPanel(this.getWwd(), measureTool));
			//leftJPanel.setTitleAt(1, "1");
			//leftJPanel.setSelectedIndex(1);
			//switchMeasureTool();

			this.getContentPane().add(leftJPanel, BorderLayout.WEST);
			this.pack();
		}

		private class MeasureToolListener implements PropertyChangeListener {
			public void propertyChange(PropertyChangeEvent event) {
				// Measure shape position list changed - update terrain profile
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

		/*private void switchMeasureTool() {
			// Disarm last measure tool when changing tab and switching tool
			if (lastTabIndex != -1) {
				MeasureTool mt = ((MeasureToolPanel) tabbedPane
						.getComponentAt(lastTabIndex)).getMeasureTool();
				mt.setArmed(false);
				mt.removePropertyChangeListener(measureToolListener);
			}
			// Update terrain profile from current measure tool
			lastTabIndex = tabbedPane.getSelectedIndex();
			MeasureTool mt = ((MeasureToolPanel) tabbedPane
					.getComponentAt(lastTabIndex)).getMeasureTool();
			mt.addPropertyChangeListener(measureToolListener);
			updateProfile(mt);
		}*/

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
		// Configuration.setValue(AVKey.INITIAL_LATITUDE, 47.15);
		// Configuration.setValue(AVKey.INITIAL_LONGITUDE, -122.74);
		// Configuration.setValue(AVKey.INITIAL_ALTITUDE, 300e3);
		// Configuration.setValue(AVKey.INITIAL_PITCH, 60);
		// Configuration.setValue(AVKey.INITIAL_HEADING, 155);

		ApplicationTemplate.start("º¼ÖÝ¾°ÇøµØÍ¼",
				YecolsApplication.AppFrame.class);
		System.out.println("application begin.");
	}

}
