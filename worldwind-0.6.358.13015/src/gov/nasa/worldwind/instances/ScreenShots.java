/*
Copyright (C) 2001, 2009 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

package gov.nasa.worldwind.instances;

import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.examples.util.ScreenShotAction;

import javax.swing.*;

/**
 * @author tag
 * @version $Id: ScreenShots.java 11809 2009-06-22 21:16:44Z tgaskins $
 */
public class ScreenShots extends JFrame
{
    static
    {
        // Ensure that menus and tooltips interact successfully with the WWJ window.
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
    }

    private WorldWindow wwd;

    public ScreenShots()
    {
        WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
        this.wwd = wwd;
        wwd.setPreferredSize(new java.awt.Dimension(1000, 800));
        this.getContentPane().add(wwd, java.awt.BorderLayout.CENTER);
        wwd.setModel(new BasicModel());
    }

    private JMenuBar createMenuBar()
    {
        JMenu menu = new JMenu("File");

        JMenuItem snapItem = new JMenuItem("Save Snapshot...");
        snapItem.addActionListener(new ScreenShotAction(this.wwd));
        menu.add(snapItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);

        return menuBar;
    }

    public static void main(String[] args)
    {
        // Swing components should always be instantiated on the event dispatch thread.
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                ScreenShots frame = new ScreenShots();

                frame.setJMenuBar(frame.createMenuBar()); // Create menu and associate with frame

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
}
