/*
Copyright (C) 2001, 2006 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.instances;

import gov.nasa.worldwind.layers.Earth.MGRSGraticuleLayer;
import gov.nasa.worldwind.util.*;

import javax.swing.*;
import java.awt.*;

/** Using the UTM/MGRS Graticule layer
 * @author Patrick Murris
 * @version $Id: MGRSGraticule.java 10366 2009-04-20 23:37:04Z patrickmurris $
 */
public class MGRSGraticule extends ApplicationTemplate
{

    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        public AppFrame()
        {
            super(true, true, false);

            MGRSGraticuleLayer layer = new MGRSGraticuleLayer();

            // Add MGRS/UTM Graticule layer
            insertBeforePlacenames(this.getWwd(), layer);

            // Replace status bar with MGRS version
            this.getStatusBar().setEventSource(null);
            this.getWwjPanel().remove(this.getStatusBar());
            //StatusBar sb = new StatusBarUTM();
            //StatusBar sb = new StatusBarUPS();
            StatusBar sb = new StatusBarMGRS();
            sb.setEventSource(this.getWwd());
            this.getWwjPanel().add(sb, BorderLayout.SOUTH);

            // Update layer panel
            this.getLayerPanel().update(this.getWwd());

            // Add go to coordinate input panel
            this.getLayerPanel().add(new GoToCoordinatePanel(this.getWwd()),  BorderLayout.SOUTH);

            // Add MGRS graticule properties frame
            JDialog dialog = MGRSAttributesPanel.showDialog(this, "MGRS Graticule Properties", layer);
            Rectangle bounds = this.getBounds();
            dialog.setLocation(bounds.x + bounds.width, bounds.y);  
        }
    }


    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind UTM/MGRS Graticule", AppFrame.class);
    }
}