/* Copyright (C) 2001, 2008 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.instances;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.examples.util.ShapeUtils;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.AirspaceLayer;
import gov.nasa.worldwind.pick.PickedObjectList;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.airspaces.*;
import gov.nasa.worldwind.render.airspaces.Polygon;
import gov.nasa.worldwind.render.airspaces.editor.*;
import gov.nasa.worldwind.util.WWIO;
import gov.nasa.worldwind.view.orbit.*;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.zip.*;

/**
 * @author dcollins
 * @version $Id: AirspaceBuilder.java 12530 2009-08-29 17:55:54Z jterhorst $
 */
public class AirspaceBuilder extends ApplicationTemplate
{
    protected static final String AIRSPACE_LAYER_NAME = "Airspace Shapes";
    protected static final String CLEAR_SELECTION = "AirspaceBuilder.ClearSelection";
    protected static final String SIZE_NEW_SHAPES_TO_VIEWPORT = "AirspaceBuilder.SizeNewShapesToViewport";
    protected static final String ENABLE_EDIT = "AirspaceBuilder.EnableEdit";
    protected static final String OPEN = "AirspaceBuilder.Open";
    protected static final String OPEN_URL = "AirspaceBuilder.OpenUrl";
    protected static final String OPEN_DEMO_AIRSPACES = "AirspaceBuilder.OpenDemoAirspaces";
    protected static final String NEW_AIRSPACE = "AirspaceBuilder.NewAirspace";
    protected static final String REMOVE_SELECTED = "AirspaceBuilder.RemoveSelected";
    protected static final String SAVE = "AirspaceBuilder.Save";
    protected static final String SELECTION_CHANGED = "AirspaceBuilder.SelectionChanged";
    protected static final String DEMO_AIRSPACES_URL = "http://worldwind.arc.nasa.gov/java/demos/data/AirspaceBuilder-DemoShapes.zip";

    //**************************************************************//
    //********************  Airspace Builder Model  ****************//
    //**************************************************************//

    protected static class AirspaceEntry extends WWObjectImpl
    {
        private Airspace airspace;
        private AirspaceEditor editor;
        private AirspaceAttributes attributes;
        private boolean editing = false;
        private boolean selected = false;
        private boolean intersecting = false;

        public AirspaceEntry(Airspace airspace, AirspaceEditor editor)
        {
            this.airspace = airspace;
            this.editor = editor;
            this.attributes = this.airspace.getAttributes();
        }

        public boolean isEditing()
        {
            return this.editing;
        }

        public void setEditing(boolean editing)
        {
            this.editing = editing;
            this.updateAttributes();
        }

        public boolean isSelected()
        {
            return this.selected;
        }

        public void setSelected(boolean selected)
        {
            this.selected = selected;
            this.updateAttributes();
        }

        public boolean isIntersecting()
        {
            return this.intersecting;
        }

        public void setIntersecting(boolean intersecting)
        {
            this.intersecting = intersecting;
            this.updateAttributes();
        }

        public String getName()
        {
            return this.getStringValue(AVKey.DISPLAY_NAME);
        }

        public void setName(String name)
        {
            this.setValue(AVKey.DISPLAY_NAME, name);
        }

        public Airspace getAirspace()
        {
            return airspace;
        }

        public AirspaceEditor getEditor()
        {
            return editor;
        }

        public AirspaceAttributes getAttributes()
        {
            return this.attributes;
        }

        public String toString()
        {
            return this.getName();
        }

        public Object getValue(String key)
        {
            Object value = super.getValue(key);
            if (value == null)
            {
                value = this.airspace.getValue(key);
            }
            return value;
        }

        public Object setValue(String key, Object value)
        {
            //noinspection StringEquality
            if (key == AVKey.DISPLAY_NAME)
            {
                return this.airspace.setValue(key, value);
            }
            else
            {
                return super.setValue(key, value);
            }
        }

        protected void updateAttributes()
        {
            if (this.isSelected() && this.isIntersecting())
            {
                this.airspace.setAttributes(getSelectionAndIntersectionAttributes());
            }
            else if (this.isSelected())
            {
                this.airspace.setAttributes(getSelectionAttributes());
            }
            else if (this.isIntersecting())
            {
                this.airspace.setAttributes(getIntersectionAttributes());
            }
            else
            {
                this.airspace.setAttributes(this.getAttributes());
            }
        }
    }

    protected static class AirspaceBuilderModel extends AbstractTableModel
    {
        private static String[] columnName = {"Name"};
        private static Class[] columnClass = {String.class};
        private static String[] columnAttribute = {AVKey.DISPLAY_NAME};

        private ArrayList<AirspaceEntry> entryList = new ArrayList<AirspaceEntry>();

        public AirspaceBuilderModel()
        {
        }

        public String getColumnName(int columnIndex)
        {
            return columnName[columnIndex];
        }

        public Class<?> getColumnClass(int columnIndex)
        {
            return columnClass[columnIndex];
        }

        public int getRowCount()
        {
            return this.entryList.size();
        }

        public int getColumnCount()
        {
            return 1;
        }

        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return true;
        }

        public Object getValueAt(int rowIndex, int columnIndex)
        {
            AirspaceEntry entry = this.entryList.get(rowIndex);
            return entry.getValue(columnAttribute[columnIndex]);
        }

        public void setValueAt(Object aObject, int rowIndex, int columnIndex)
        {
            AirspaceEntry entry = this.entryList.get(rowIndex);
            String key = columnAttribute[columnIndex];
            entry.setValue(key, aObject);
        }

        public java.util.List<AirspaceEntry> getEntries()
        {
            return Collections.unmodifiableList(this.entryList);
        }

        public void setEntries(Iterable<? extends AirspaceEntry> entries)
        {
            this.entryList.clear();
            if (entries != null)
            {
                for (AirspaceEntry entry : entries)
                {
                    this.entryList.add(entry);
                }
            }

            this.fireTableDataChanged();
        }

        public void addEntry(AirspaceEntry entry)
        {
            this.entryList.add(entry);
            int index = this.entryList.size() - 1;
            this.fireTableRowsInserted(index, index);
        }

        public void removeEntry(AirspaceEntry entry)
        {
            int index = this.entryList.indexOf(entry);
            if (index != -1)
            {
                this.entryList.remove(entry);
                this.fireTableRowsDeleted(index, index);
            }
        }

        public void removeAllEntries()
        {
            this.entryList.clear();
            this.fireTableDataChanged();
        }

        public AirspaceEntry getEntry(int index)
        {
            return this.entryList.get(index);
        }

        public AirspaceEntry setEntry(int index, AirspaceEntry entry)
        {
            return this.entryList.set(index, entry);
        }

        public int getIndexForEntry(AirspaceEntry entry)
        {
            return this.entryList.indexOf(entry);
        }
    }

    protected static AirspaceFactory[] defaultAirspaceFactories = new AirspaceFactory[]
    {
        new PolygonAirspaceFactory(),
        new SphereAirspaceFactory()
    };

    protected static final double DEFAULT_SHAPE_SIZE_METERS = 200000.0; // 200 km

    protected interface AirspaceFactory
    {
        Airspace createAirspace(WorldWindow wwd, boolean fitShapeToViewport);

        AirspaceEditor createEditor(Airspace airspace);
    }

    protected static class PolygonAirspaceFactory implements AirspaceFactory
    {
        public PolygonAirspaceFactory()
        {
        }

        public Airspace createAirspace(WorldWindow wwd, boolean fitShapeToViewport)
        {
            Polygon poly = new Polygon();
            poly.setAttributes(getDefaultAttributes());
            poly.setValue(AVKey.DISPLAY_NAME, getNextName(toString()));
            poly.setAltitudes(0.0, 0.0);
            poly.setTerrainConforming(true, false);
            this.initializePolygon(wwd, poly, fitShapeToViewport);

            return poly;
        }

        public AirspaceEditor createEditor(Airspace airspace)
        {
            PolygonEditor editor = new PolygonEditor();
            editor.setPolygon((Polygon) airspace);
            setEditorAttributes(editor);
            return editor;
        }

        protected void initializePolygon(WorldWindow wwd, Polygon polygon, boolean fitShapeToViewport)
        {
            // Creates a rectangle in the center of the viewport. Attempts to guess at a reasonable size and height.

            Position position = ShapeUtils.getNewShapePosition(wwd);
            Angle heading = ShapeUtils.getNewShapeHeading(wwd, true);
            double sizeInMeters = fitShapeToViewport ?
                ShapeUtils.getViewportScaleFactor(wwd) : DEFAULT_SHAPE_SIZE_METERS;

            java.util.List<LatLon> locations = ShapeUtils.createSquareInViewport(wwd, position, heading, sizeInMeters);

            double maxElevation = -Double.MAX_VALUE;
            Globe globe = wwd.getModel().getGlobe();

            for (LatLon ll : locations)
            {
                double e = globe.getElevation(ll.getLatitude(), ll.getLongitude());
                if (e > maxElevation)
                    maxElevation = e;
            }

            polygon.setAltitudes(0.0, maxElevation + sizeInMeters);
            polygon.setTerrainConforming(true, false);
            polygon.setLocations(locations);
        }

        public String toString()
        {
            return "Polygon";
        }
    }

    protected static class SphereAirspaceFactory implements AirspaceFactory
    {
        public SphereAirspaceFactory()
        {
        }

        public Airspace createAirspace(WorldWindow wwd, boolean fitShapeToViewport)
        {
            SphereAirspace sphere = new SphereAirspace();
            sphere.setAttributes(getDefaultAttributes());
            sphere.setValue(AVKey.DISPLAY_NAME, getNextName(toString()));
            sphere.setAltitude(0.0);
            sphere.setTerrainConforming(true);
            this.initializeSphere(wwd, sphere, fitShapeToViewport);

            return sphere;
        }

        public AirspaceEditor createEditor(Airspace airspace)
        {
            SphereAirspaceEditor editor = new SphereAirspaceEditor();
            editor.setSphere((SphereAirspace) airspace);
            setEditorAttributes(editor);
            return editor;
        }

        protected void initializeSphere(WorldWindow wwd, SphereAirspace sphere, boolean fitShapeToViewport)
        {
            // Creates a sphere in the center of the viewport. Attempts to guess at a reasonable size and height.
            Position position = ShapeUtils.getNewShapePosition(wwd);
            double sizeInMeters = fitShapeToViewport ?
                ShapeUtils.getViewportScaleFactor(wwd) : DEFAULT_SHAPE_SIZE_METERS;

            sphere.setLocation(new LatLon(position));
            sphere.setRadius(sizeInMeters / 2.0);
        }

        public String toString()
        {
            return "Sphere";
        }
    }

    public static AirspaceAttributes getDefaultAttributes()
    {
        AirspaceAttributes attributes = new BasicAirspaceAttributes();
        attributes.setMaterial(new Material(Color.BLACK, Color.LIGHT_GRAY, Color.DARK_GRAY, Color.BLACK, 0.0f));
        attributes.setOutlineMaterial(Material.DARK_GRAY);
        attributes.setDrawOutline(true);
        attributes.setOpacity(0.95);
        attributes.setOutlineOpacity(.95);
        attributes.setOutlineWidth(2);
        return attributes;
    }

    public static AirspaceAttributes getSelectionAttributes()
    {
        AirspaceAttributes attributes = new BasicAirspaceAttributes();
        attributes.setMaterial(Material.WHITE);
        attributes.setOutlineMaterial(Material.BLACK);
        attributes.setDrawOutline(true);
        attributes.setOpacity(0.8);
        attributes.setOutlineOpacity(0.8);
        attributes.setOutlineWidth(2);
        return attributes;
    }

    public static AirspaceAttributes getIntersectionAttributes()
    {
        AirspaceAttributes attributes = new BasicAirspaceAttributes();
        attributes.setMaterial(Material.RED);
        attributes.setOpacity(0.95);
        return attributes;
    }

    public static AirspaceAttributes getSelectionAndIntersectionAttributes()
    {
        AirspaceAttributes attributes = new BasicAirspaceAttributes();
        attributes.setMaterial(Material.ORANGE);
        attributes.setOpacity(0.8);
        return attributes;
    }

    public static void setEditorAttributes(AirspaceEditor editor)
    {
        editor.setUseRubberBand(true);
        editor.setKeepControlPointsAboveTerrain(true);
    }

    public static String getNextName(String base)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(base);
        sb.append(nextEntryNumber++);
        return sb.toString();
    }

    private static AirspaceEditor getEditorFor(Airspace airspace)
    {
        if (airspace instanceof Polygon)
        {
            PolygonEditor editor = new PolygonEditor();
            editor.setPolygon((Polygon) airspace);
            setEditorAttributes(editor);
            return editor;
        }
        else if (airspace instanceof SphereAirspace)
        {
            SphereAirspaceEditor editor = new SphereAirspaceEditor();
            editor.setSphere((SphereAirspace) airspace);
            setEditorAttributes(editor);
            return editor;
        }

        return null;
    }

    private static long nextEntryNumber = 1;

    //**************************************************************//
    //********************  Airspace Builder Panel  ****************//
    //**************************************************************//

    protected static class AirspaceBuilderPanel extends JPanel
    {
        private JComboBox factoryComboBox;
        private JTable entryTable;
        private boolean ignoreSelectEvents = false;

        public AirspaceBuilderPanel(AirspaceBuilderModel model, AirspaceBuilderController controller)
        {
            this.initComponents(model, controller);
        }

        public int[] getSelectedIndices()
        {
            return this.entryTable.getSelectedRows();
        }

        public void setSelectedIndices(int[] indices)
        {
            this.ignoreSelectEvents = true;

            if (indices != null && indices.length != 0)
            {
                for (int index : indices)
                {
                    this.entryTable.setRowSelectionInterval(index, index);
                }
            }
            else
            {
                this.entryTable.clearSelection();
            }

            this.ignoreSelectEvents = false;
        }

        public AirspaceFactory getSelectedFactory()
        {
            return (AirspaceFactory) this.factoryComboBox.getSelectedItem();
        }

        public void setSelectedFactory(AirspaceFactory factory)
        {
            this.factoryComboBox.setSelectedItem(factory);
        }

        private void initComponents(AirspaceBuilderModel model, final AirspaceBuilderController controller)
        {
            final JCheckBox resizeNewShapesCheckBox;
            final JCheckBox enableEditCheckBox;

            JPanel newShapePanel = new JPanel();
            {
                JButton newShapeButton = new JButton("New shape");
                newShapeButton.setActionCommand(NEW_AIRSPACE);
                newShapeButton.addActionListener(controller);
                newShapeButton.setToolTipText("Create a new shape centered in the viewport");

                this.factoryComboBox = new JComboBox(defaultAirspaceFactories);
                this.factoryComboBox.setEditable(false);
                this.factoryComboBox.setToolTipText("Choose shape type to create");

                resizeNewShapesCheckBox = new JCheckBox("Fit new shapes to viewport");
                resizeNewShapesCheckBox.setActionCommand(SIZE_NEW_SHAPES_TO_VIEWPORT);
                resizeNewShapesCheckBox.addActionListener(controller);
                resizeNewShapesCheckBox.setSelected(controller.isResizeNewShapesToViewport());
                resizeNewShapesCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                resizeNewShapesCheckBox.setToolTipText("New shapes are sized to fit the geographic viewport");

                enableEditCheckBox = new JCheckBox("Enable shape editing");
                enableEditCheckBox.setActionCommand(ENABLE_EDIT);
                enableEditCheckBox.addActionListener(controller);
                enableEditCheckBox.setSelected(controller.isEnableEdit());
                enableEditCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                enableEditCheckBox.setToolTipText("Allow modifications to shapes");

                Box newShapeBox = Box.createHorizontalBox();
                newShapeBox.add(newShapeButton);
                newShapeBox.add(Box.createHorizontalStrut(5));
                newShapeBox.add(this.factoryComboBox);
                newShapeBox.setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel gridPanel = new JPanel(new GridLayout(0, 1, 0, 5)); // rows, cols, hgap, vgap
                gridPanel.add(newShapeBox);
                gridPanel.add(resizeNewShapesCheckBox);
                gridPanel.add(enableEditCheckBox);

                newShapePanel.setLayout(new BorderLayout());
                newShapePanel.add(gridPanel, BorderLayout.NORTH);
            }

            JPanel entryPanel = new JPanel();
            {
                this.entryTable = new JTable(model);
                this.entryTable.setColumnSelectionAllowed(false);
                this.entryTable.setRowSelectionAllowed(true);
                this.entryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                this.entryTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
                {
                    public void valueChanged(ListSelectionEvent e)
                    {
                        if (!ignoreSelectEvents)
                        {
                            controller.actionPerformed(new ActionEvent(e.getSource(), -1, SELECTION_CHANGED));
                        }
                    }
                });
                this.entryTable.setToolTipText("<html>Click to select<br>Double-Click to rename</html>");

                JScrollPane tablePane = new JScrollPane(this.entryTable);
                tablePane.setPreferredSize(new Dimension(200, 100));

                entryPanel.setLayout(new BorderLayout(0, 0)); // hgap, vgap
                entryPanel.add(tablePane, BorderLayout.CENTER);
            }

            JPanel selectionPanel = new JPanel();
            {
                JButton delselectButton = new JButton("Deselect");
                delselectButton.setActionCommand(CLEAR_SELECTION);
                delselectButton.addActionListener(controller);
                delselectButton.setToolTipText("Clear the selection");

                JButton deleteButton = new JButton("Delete Selected");
                deleteButton.setActionCommand(REMOVE_SELECTED);
                deleteButton.addActionListener(controller);
                deleteButton.setToolTipText("Delete selected shapes");

                JPanel gridPanel = new JPanel(new GridLayout(0, 1, 0, 5)); // rows, cols, hgap, vgap
                gridPanel.add(delselectButton);
                gridPanel.add(deleteButton);

                selectionPanel.setLayout(new BorderLayout());
                selectionPanel.add(gridPanel, BorderLayout.NORTH);
            }

            this.setLayout(new BorderLayout(30, 0)); // hgap, vgap
            this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // top, left, bottom, right
            this.add(newShapePanel, BorderLayout.WEST);
            this.add(entryPanel, BorderLayout.CENTER);
            this.add(selectionPanel, BorderLayout.EAST);

            controller.addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent e)
                {
                    //noinspection StringEquality
                    if (e.getPropertyName() == SIZE_NEW_SHAPES_TO_VIEWPORT)
                    {
                        resizeNewShapesCheckBox.setSelected(controller.isResizeNewShapesToViewport());
                    }
                    else //noinspection StringEquality
                        if (e.getPropertyName() == ENABLE_EDIT)
                    {
                        enableEditCheckBox.setSelected(controller.isEnableEdit());
                    }
                }
            });
        }
    }

    //**************************************************************//
    //********************  Airspace Builder Controller  ***********//
    //**************************************************************//

    protected static class AirspaceBuilderController extends WWObjectImpl implements ActionListener, MouseListener,
        AirspaceEditListener
    {
        private AppFrame app;
        private AirspaceBuilderModel model;
        private AirspaceBuilderPanel view;
        private AirspaceEntry selectedEntry;
        private AirspaceEditorController editorController;
        private boolean enabled = true;
        private boolean enableEdit = true;
        private boolean resizeNewShapes;
        // UI components.
        private JFileChooser fileChooser;

        public AirspaceBuilderController(AppFrame app)
        {
            this.app = app;
            this.editorController = new AirspaceEditorController();

            // The ordering is important here; we want first pass at mouse events.
            this.editorController.setWorldWindow(this.app.getWwd());
            this.app.getWwd().getInputHandler().addMouseListener(this);
        }

        public AppFrame getApp()
        {
            return this.app;
        }

        public AirspaceBuilderModel getModel()
        {
            return this.model;
        }

        public void setModel(AirspaceBuilderModel model)
        {
            this.model = model;
        }

        public AirspaceBuilderPanel getView()
        {
            return this.view;
        }

        public void setView(AirspaceBuilderPanel view)
        {
            this.view = view;
        }

        public boolean isEnabled()
        {
            return this.enabled;
        }

        public void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
            getView().setEnabled(enabled);
            getApp().setEnabled(enabled);
        }

        public boolean isEnableEdit()
        {
            return this.enableEdit;
        }

        public void setEnableEdit(boolean enable)
        {
            this.enableEdit = enable;
            this.handleEnableEdit(enable);
            this.firePropertyChange(ENABLE_EDIT, null, enable);
        }

        public boolean isResizeNewShapesToViewport()
        {
            return this.resizeNewShapes;
        }

        public void setResizeNewShapesToViewport(boolean resize)
        {
            this.resizeNewShapes = resize;
            this.firePropertyChange(SIZE_NEW_SHAPES_TO_VIEWPORT, null, resize);
        }

        public void actionPerformed(ActionEvent e)
        {
            if (!this.isEnabled())
            {
                return;
            }

            //noinspection StringEquality
            if (e.getActionCommand() == NEW_AIRSPACE)
            {
                this.createNewEntry(this.getView().getSelectedFactory());
            }
            else //noinspection StringEquality
                if (e.getActionCommand() == CLEAR_SELECTION)
            {
                this.selectEntry(null, true);
            }
            else //noinspection StringEquality
                if (e.getActionCommand() == SIZE_NEW_SHAPES_TO_VIEWPORT)
            {
                if (e.getSource() instanceof AbstractButton)
                {
                    boolean selected = ((AbstractButton) e.getSource()).isSelected();
                    this.setResizeNewShapesToViewport(selected);
                }
            }
            else //noinspection StringEquality
                if (e.getActionCommand() == ENABLE_EDIT)
            {
                if (e.getSource() instanceof AbstractButton)
                {
                    boolean selected = ((AbstractButton) e.getSource()).isSelected();
                    this.setEnableEdit(selected);
                }
            }
            else //noinspection StringEquality
                if (e.getActionCommand() == OPEN)
            {
                this.openFromFile();
            }
            else //noinspection StringEquality
                if (e.getActionCommand() == OPEN_URL)
            {
                this.openFromURL();
            }
            else //noinspection StringEquality
                if (e.getActionCommand() == OPEN_DEMO_AIRSPACES)
            {
                URL url = null;
                try
                {
                    url = new URL(DEMO_AIRSPACES_URL);
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }

                if (url != null)
                {
                    this.openFromURL(url);
                    this.zoomTo(LatLon.fromDegrees(47.6584074779224, -122.3059199579634),
                        Angle.fromDegrees(-152), Angle.fromDegrees(75), 750);
                }
            }
            else //noinspection StringEquality
                if (e.getActionCommand() == REMOVE_SELECTED)
            {
                this.removeEntries(Arrays.asList(this.getSelectedEntries()));
            }
            else //noinspection StringEquality
                if (e.getActionCommand() == SAVE)
            {
                this.saveToFile();
            }
            else //noinspection StringEquality
                if (e.getActionCommand() == SELECTION_CHANGED)
            {
                this.viewSelectionChanged();
            }
        }

        public void mouseClicked(MouseEvent e)
        {
        }

        public void mousePressed(MouseEvent e)
        {
            if (e == null || e.isConsumed())
            {
                return;
            }

            if (!this.isEnabled())
            {
                return;
            }

            //noinspection StringEquality
            if (e.getButton() == MouseEvent.BUTTON1)
            {
                this.handleSelect();
            }
        }

        public void mouseReleased(MouseEvent e)
        {
        }

        public void mouseEntered(MouseEvent e)
        {
        }

        public void mouseExited(MouseEvent e)
        {
        }

        public void airspaceMoved(AirspaceEditEvent e)
        {
            this.updateShapeIntersection();
        }

        public void airspaceResized(AirspaceEditEvent e)
        {
            this.updateShapeIntersection();
        }

        public void controlPointAdded(AirspaceEditEvent e)
        {
        }

        public void controlPointRemoved(AirspaceEditEvent e)
        {
        }

        public void controlPointChanged(AirspaceEditEvent e)
        {
        }

        protected void handleSelect()
        {
            // If the picked object is null or something other than an airspace, then ignore the mouse click. If we
            // deselect the current entry at this point, the user cannot easily navigate without loosing the selection.

            PickedObjectList pickedObjects = this.getApp().getWwd().getObjectsAtCurrentPosition();

            Object topObject = pickedObjects.getTopObject();
            if (!(topObject instanceof Airspace))
                return;

            AirspaceEntry pickedEntry = this.getEntryFor((Airspace) topObject);
            if (pickedEntry == null)
                return;

            if (this.getSelectedEntry() != pickedEntry)
            {
                this.selectEntry(pickedEntry, true);
            }
        }

        //protected void handleSelectHover(SelectEvent e)
        //{
        //    ToolTip toolTip = this.getApp().getWorldWindToolTip();
        //    if (toolTip != null)
        //    {
        //        this.getApp().setWorldWindToolTip(null);
        //        this.getApp().getWwd().redraw();
        //    }
        //
        //    // Don't show any tool tips if the editor controller is engaged in some action.
        //    if (this.getModel().getEditorController().isActive())
        //        return;
        //
        //    if (e.hasObjects())
        //    {
        //        toolTip = this.createToolTip(e.getTopObject(), e.getPickPoint());
        //        this.getApp().setWorldWindToolTip(toolTip);
        //        this.getApp().getWwd().redraw();
        //    }
        //}

        protected void handleEnableEdit(boolean enable)
        {
            if (this.getSelectedEntry() == null)
                return;

            if (this.isSelectionEditing() != enable)
                this.setSelectionEditing(enable);
        }

        protected void updateShapeIntersection()
        {
            AirspaceEntry selected = this.getSelectedEntry();

            if (selected != null)
            {
                boolean hasIntersection = false;
                for (AirspaceEntry entry : this.getModel().getEntries())
                {
                    if (entry != selected)
                    {
                        boolean intersecting = this.areShapesIntersecting(entry.getAirspace(), selected.getAirspace());
                        if (intersecting)
                            hasIntersection = true;

                        entry.setIntersecting(intersecting);
                    }
                }

                selected.setIntersecting(hasIntersection);
            }
            else
            {
                for (AirspaceEntry entry : this.getModel().getEntries())
                {
                    entry.setIntersecting(false);
                }
            }
        }

        protected boolean areShapesIntersecting(Airspace a1, Airspace a2)
        {
            if ((a1 instanceof SphereAirspace) && (a2 instanceof SphereAirspace))
            {
                SphereAirspace s1 = (SphereAirspace) a1;
                SphereAirspace s2 = (SphereAirspace) a2;

                LatLon location1 = s1.getLocation();
                LatLon location2 = s2.getLocation();
                double altitude1 = s1.getAltitudes()[0];
                double altitude2 = s2.getAltitudes()[0];
                boolean terrainConforming1 = s1.isTerrainConforming()[0];
                boolean terrainConforming2 = s2.isTerrainConforming()[0];

                // We have to compute the 3D coordinates of the sphere's center ourselves here.
                Vec4 p1 = terrainConforming1 ? this.getSurfacePoint(location1, altitude1) : this.getPoint(location1, altitude1);
                Vec4 p2 = terrainConforming2 ? this.getSurfacePoint(location2, altitude2) : this.getPoint(location2, altitude2);
                double r1 = s1.getRadius();
                double r2 = s2.getRadius();

                double d = p1.distanceTo3(p2);
                
                return d <= (r1 + r2);
            }

            return false;
        }

        protected Vec4 getSurfacePoint(LatLon latlon, double elevation)
        {
            Vec4 point = null;

            SceneController sc = this.getApp().getWwd().getSceneController();
            Globe globe = this.getApp().getWwd().getModel().getGlobe();

            if (sc.getTerrain() != null)
            {
                point = sc.getTerrain().getSurfacePoint(
                    latlon.getLatitude(), latlon.getLongitude(), elevation * sc.getVerticalExaggeration());
            }

            if (point == null)
            {
                double e = globe.getElevation(latlon.getLatitude(), latlon.getLongitude());
                point = globe.computePointFromPosition(
                    latlon.getLatitude(), latlon.getLongitude(), (e + elevation) * sc.getVerticalExaggeration());
            }

            return point;
        }

        protected Vec4 getPoint(LatLon latlon, double elevation)
        {
            SceneController sc = this.getApp().getWwd().getSceneController();            
            Globe globe = this.getApp().getWwd().getModel().getGlobe();
            double e = globe.getElevation(latlon.getLatitude(), latlon.getLongitude());
            return globe.computePointFromPosition(
                latlon.getLatitude(), latlon.getLongitude(), (e + elevation) * sc.getVerticalExaggeration());
        }

        public void createNewEntry(AirspaceFactory factory)
        {
            Airspace airspace = factory.createAirspace(this.getApp().getWwd(), this.isResizeNewShapesToViewport());
            AirspaceEditor editor = factory.createEditor(airspace);
            AirspaceEntry entry = new AirspaceEntry(airspace, editor);

            this.addEntry(entry);

            this.selectEntry(entry, true);
        }

        public void removeEntries(Iterable<? extends AirspaceEntry> entries)
        {
            if (entries != null)
            {
                for (AirspaceEntry entry : entries)
                {
                    this.removeEntry(entry);
                }
            }
        }

        public void addEntry(AirspaceEntry entry)
        {
            entry.getEditor().addEditListener(this);
            this.getModel().addEntry(entry);
            this.updateShapeIntersection();

            this.getApp().getAirspaceLayer().addAirspace(entry.getAirspace());
            this.getApp().getWwd().redraw();
        }

        public void removeEntry(AirspaceEntry entry)
        {
            entry.getEditor().removeEditListener(this);

            if (this.getSelectedEntry() == entry)
            {
                this.selectEntry(null, true);
            }

            this.getModel().removeEntry(entry);
            this.updateShapeIntersection();

            this.getApp().getAirspaceLayer().removeAirspace(entry.getAirspace());
            this.getApp().getWwd().redraw();
        }

        public AirspaceEntry getSelectedEntry()
        {
            return this.selectedEntry;
        }

        public void selectEntry(AirspaceEntry entry, boolean updateView)
        {
            this.setSelectedEntry(entry);

            if (updateView)
            {
                if (entry != null)
                {
                    int index = this.getModel().getIndexForEntry(entry);
                    this.getView().setSelectedIndices(new int[] {index});
                }
                else
                {
                    this.getView().setSelectedIndices(new int[0]);
                }
            }

            if (this.isEnableEdit())
            {
                if (this.getSelectedEntry() != null && !this.isSelectionEditing())
                {
                    this.setSelectionEditing(true);
                }
            }

            this.updateShapeIntersection();
            this.getApp().getWwd().redraw();
        }

        protected void setSelectedEntry(AirspaceEntry entry)
        {
            if (this.selectedEntry != null)
            {
                if (this.selectedEntry != entry && this.selectedEntry.isEditing())
                {
                    this.setSelectionEditing(false);
                }

                this.selectedEntry.setSelected(false);
            }

            this.selectedEntry = entry;

            if (this.selectedEntry != null)
            {
                this.selectedEntry.setSelected(true);
            }
        }

        protected boolean isSelectionEditing()
        {
            return this.selectedEntry != null && this.selectedEntry.isEditing();
        }

        protected void setSelectionEditing(boolean editing)
        {
            if (this.selectedEntry == null)
            {
                throw new IllegalStateException();
            }

            if (this.selectedEntry.isEditing() == editing)
            {
                throw new IllegalStateException();
            }

            this.selectedEntry.setEditing(editing);

            AirspaceEditor editor = this.selectedEntry.getEditor();
            editor.setArmed(editing);

            if (editing)
            {
                this.editorController.setEditor(editor);
                insertBeforePlacenames(this.getApp().getWwd(), editor);
            }
            else
            {
                this.editorController.setEditor(null);
                this.getApp().getWwd().getModel().getLayers().remove(editor);
            }

            int index = this.getModel().getIndexForEntry(this.selectedEntry);
            this.getModel().fireTableRowsUpdated(index, index);
        }

        protected void viewSelectionChanged()
        {
            int[] indices = this.getView().getSelectedIndices();
            if (indices != null)
            {
                for (AirspaceEntry entry : this.getEntriesFor(indices))
                {
                    this.selectEntry(entry, false);
                }
            }
            
            this.getApp().getWwd().redraw();
        }

        protected AirspaceEntry[] getSelectedEntries()
        {
            int[] indices = this.getView().getSelectedIndices();
            if (indices != null)
            {
                return this.getEntriesFor(indices);
            }

            return new AirspaceEntry[0];
        }

        protected AirspaceEntry[] getEntriesFor(int[] indices)
        {
            AirspaceEntry[] entries = new AirspaceEntry[indices.length];
            for (int i = 0; i < indices.length; i++)
            {
                entries[i] = this.getModel().getEntry(indices[i]);
            }
            return entries;
        }

        protected AirspaceEntry getEntryFor(Airspace airspace)
        {
            for (AirspaceEntry entry : this.getModel().getEntries())
            {
                if (entry.getAirspace() == airspace)
                {
                    return entry;
                }
            }
            return null;
        }

        //protected ToolTip createToolTip(Object object, Point pickPoint)
        //{
        //    AirspaceEntry pickedEntry = null;
        //
        //    if (object instanceof Airspace)
        //    {
        //        pickedEntry = this.getEntryFor((Airspace) object);
        //    }
        //    else if (object instanceof AirspaceControlPoint)
        //    {
        //        pickedEntry = this.getEntryFor(((AirspaceControlPoint) object).getAirspace());
        //    }
        //
        //    if (pickedEntry == null)
        //    {
        //        return null;
        //    }
        //
        //    StringBuilder sb = new StringBuilder();
        //    sb.append("<html>");
        //
        //    if (object instanceof Airspace)
        //    {
        //        if (pickedEntry.toString() != null)
        //        {
        //            sb.append(pickedEntry.toString());
        //        }
        //
        //        if (pickedEntry.isEditing())
        //        {
        //            if (sb.length() > 0)
        //            {
        //                sb.append("<br/>");
        //            }
        //
        //            sb.append("<b>Click & Drag to move</b>");
        //            sb.append("<br/>");
        //            sb.append("<b>Shift + Click & Drag to raise/lower</b>");
        //
        //            if (pickedEntry.getAirspace() instanceof Polygon)
        //            {
        //                sb.append("<br/>");
        //                sb.append("<b>Alt + Click to add points</b>");
        //            }
        //        }
        //    }
        //    else //noinspection ConstantConditions
        //        if (object instanceof AirspaceControlPoint)
        //    {
        //        sb.append("<b>Click & Drag to adjust</b>");
        //        sb.append("<br/>");
        //        sb.append("<b>Shift + Click & Drag to resize</b>");
        //        if (pickedEntry.getAirspace() instanceof Polygon)
        //        {
        //            sb.append("<br/>");
        //            sb.append("<b>Ctrl + Click to delete</b>");
        //        }
        //    }
        //
        //    sb.append("</html>");
        //
        //    int canvasHeight = this.getApp().getWwd().getHeight();
        //    Point wwPoint = new Point(pickPoint.x, canvasHeight - pickPoint.y - 1);
        //
        //    return new ToolTip(sb.toString(), wwPoint, 1.0);
        //}

        protected void zoomTo(LatLon latLon, Angle heading, Angle pitch, double zoom)
        {
            BasicOrbitView view = (BasicOrbitView) this.getApp().getWwd().getView();
            view.addPanToAnimator(
                new Position(latLon, 0), heading, pitch, zoom, true);

            view.stopMovement();
        }

        protected void openFromURL()
        {
            Object input = JOptionPane.showInputDialog(this.getApp(), "Enter a URL: ", "Open Shapes from URL",
                JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (input == null)
                return;

            URL url = null;
            try
            {
                url = new URL(input.toString());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (url != null)
            {
                this.openFromURL(url);
            }
        }

        protected void openFromURL(final URL url)
        {
            Thread t = new Thread(new Runnable()
            {
                public void run()
                {
                    final ArrayList<Airspace> airspaces = new ArrayList<Airspace>();
                    try
                    {
                        loadAirspacesFromURL(url, airspaces);
                    }
                    finally
                    {
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                setAirspaces(airspaces);
                                setEnabled(true);
                                getApp().setCursor(null);
                                getApp().getWwd().redraw();
                            }
                        });
                    }
                }
            });

            this.setEnabled(false);
            getApp().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            t.start();
        }

        protected void loadAirspacesFromURL(URL url, Collection<Airspace> airspaces)
        {
            File file = null;
            try
            {
                file = File.createTempFile("AirspaceBuilder-TempFile", null);

                InputStream is = url.openStream();
                OutputStream os = new FileOutputStream(file);

                byte[] buffer = new byte[4096];
                int length;
                while ((length = is.read(buffer)) > 0)
                {
                    os.write(buffer, 0, length);
                }

                is.close();
                os.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (file == null)
                return;

            try
            {
                ZipFile zipFile = new ZipFile(file);

                ZipEntry entry = null;
                for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements(); entry = e.nextElement())
                {
                    if (entry == null)
                        continue;

                    String name = entry.getName();
                    name = this.getFileName(name);

                    if (!(name.startsWith("gov.nasa.worldwind.render.airspaces") && name.endsWith(".xml")))
                        continue;

                    String[] tokens = name.split("-");

                    try
                    {
                        Class c = Class.forName(tokens[0]);
                        Airspace airspace = (Airspace) c.newInstance();
                        BufferedReader input = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));
                        String s = input.readLine();
                        airspace.restoreState(s);
                        airspaces.add(airspace);

                        if (tokens.length >= 2)
                        {
                            airspace.setValue(AVKey.DISPLAY_NAME, tokens[1]);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        protected void openFromFile()
        {
            if (this.fileChooser == null)
            {
                this.fileChooser = new JFileChooser();
                this.fileChooser.setCurrentDirectory(new File(Configuration.getUserHomeDirectory()));
            }

            this.fileChooser.setDialogTitle("Choose Airspace File Directory");
            this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            this.fileChooser.setMultiSelectionEnabled(false);
            int status = this.fileChooser.showOpenDialog(null);
            if (status != JFileChooser.APPROVE_OPTION)
                return;

            final File dir = this.fileChooser.getSelectedFile();
            if (dir == null)
                return;

            Thread t = new Thread(new Runnable()
            {
                public void run()
                {
                    final ArrayList<Airspace> airspaces = new ArrayList<Airspace>();
                    try
                    {
                        File[] files = dir.listFiles(new FilenameFilter()
                        {
                            public boolean accept(File dir, String name)
                            {
                                return name.startsWith("gov.nasa.worldwind.render.airspaces") && name.endsWith(".xml");
                            }
                        });

                        for (File file : files)
                        {
                            String[] name = file.getName().split("-");
                            try
                            {
                                Class c = Class.forName(name[0]);
                                Airspace airspace = (Airspace) c.newInstance();
                                BufferedReader input = new BufferedReader(new FileReader(file));
                                String s = input.readLine();
                                airspace.restoreState(s);
                                airspaces.add(airspace);

                                if (name.length >= 2)
                                {
                                    airspace.setValue(AVKey.DISPLAY_NAME, name[1]);
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    finally
                    {
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                setAirspaces(airspaces);
                                setEnabled(true);
                                getApp().setCursor(null);
                                getApp().getWwd().redraw();
                            }
                        });
                    }
                }
            });
            this.setEnabled(false);
            getApp().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            t.start();
        }

        protected void saveToFile()
        {
            if (this.fileChooser == null)
            {
                this.fileChooser = new JFileChooser();
                this.fileChooser.setCurrentDirectory(new File(Configuration.getUserHomeDirectory()));
            }

            this.fileChooser.setDialogTitle("Choose Directory to Place Airspaces");
            this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            this.fileChooser.setMultiSelectionEnabled(false);
            int status = this.fileChooser.showSaveDialog(null);
            if (status != JFileChooser.APPROVE_OPTION)
                return;

            final File dir = this.fileChooser.getSelectedFile();
            if (dir == null)
                return;

            if (dir.exists())
            {
                try
                {
                    WWIO.deleteDirectory(dir);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (!dir.exists())
            {
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }

            final Iterable<AirspaceEntry> entries = this.getModel().getEntries();

            Thread t = new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        java.text.DecimalFormat f = new java.text.DecimalFormat("####");
                        f.setMinimumIntegerDigits(4);
                        int counter = 0;

                        for (AirspaceEntry entry : entries)
                        {
                            Airspace a = entry.getAirspace();
                            AirspaceAttributes currentAttribs = a.getAttributes();
                            a.setAttributes(entry.getAttributes());
                            
                            String xmlString = a.getRestorableState();
                            if (xmlString != null)
                            {
                                try
                                {
                                    PrintWriter of = new PrintWriter(new File(dir,
                                        a.getClass().getName() + "-" + entry.getName() + "-" + f.format(counter++) + ".xml"));
                                    of.write(xmlString);
                                    of.flush();
                                    of.close();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }

                            a.setAttributes(currentAttribs);
                        }
                    }
                    finally
                    {
                        SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                setEnabled(true);
                                getApp().setCursor(null);
                                getApp().getWwd().redraw();
                            }
                        });
                    }
                }
            });
            this.setEnabled(false);
            getApp().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            t.start();
        }

        private void setAirspaces(Iterable<? extends Airspace> airspaces)
        {
            ArrayList<AirspaceEntry> entryList = new ArrayList<AirspaceEntry>(this.getModel().getEntries());
            this.removeEntries(entryList);

            for (Airspace airspace : airspaces)
            {
                airspace.setAttributes(getDefaultAttributes());
                AirspaceEntry entry = new AirspaceEntry(airspace, getEditorFor(airspace));
                this.addEntry(entry);
            }
        }

        private String getFileName(String s)
        {
            int index = s.lastIndexOf("/");
            if (index == -1)
                index = s.lastIndexOf("\\");
            if (index != -1 && index < s.length())
                return s.substring(index + 1, s.length());
            return s;
        }
    }

    //**************************************************************//
    //********************  Main  **********************************//
    //**************************************************************//

    //protected static class ToolTip implements Renderable
    //{
    //    private String text;
    //    private Point point;
    //    private ScreenAnnotation annotation;
    //
    //    private static final Font toolTipFont = UIManager.getFont("ToolTip.font");
    //    private static final Color toolTipFg = UIManager.getColor("ToolTip.foreground");
    //    private static final Color toolTipBg = UIManager.getColor("ToolTip.background");
    //    private static final Border toolTipBorder = UIManager.getBorder("ToolTip.border");
    //
    //    public ToolTip(String text, Point point, double opacity)
    //    {
    //        this.text = text;
    //        this.point = point;
    //        this.annotation = this.createAnnotation(text, point);
    //        this.annotation.getAttributes().setOpacity(opacity);
    //    }
    //
    //    public String getText()
    //    {
    //        return this.text;
    //    }
    //
    //    public Point getPoint()
    //    {
    //        return this.point;
    //    }
    //
    //    public void render(DrawContext dc)
    //    {
    //        this.annotation.render(dc);
    //    }
    //
    //    protected ScreenAnnotation createAnnotation(String text, Point point)
    //    {
    //        AnnotationAttributes attributes = new AnnotationAttributes();
    //        attributes.setFont(toolTipFont);
    //        attributes.setTextColor(toolTipFg);
    //        attributes.setBorderColor(toolTipFg);
    //        attributes.setBackgroundColor(toolTipBg);
    //        attributes.setBorderWidth(1.0);
    //        attributes.setCornerRadius(0);
    //        attributes.setInsets(toolTipBorder.getBorderInsets(null));
    //        attributes.setEffect(MultiLineTextRenderer.EFFECT_NONE);
    //        attributes.setAntiAliasHint(Annotation.ANTIALIAS_DONT_CARE);
    //        attributes.setAdjustWidthToText(Annotation.SIZE_FIT_TEXT);
    //        attributes.setSize(new Dimension(300, 0));
    //
    //        ScreenAnnotation sa = new ScreenAnnotation(text, point, attributes);
    //        sa.setAlwaysOnTop(true);
    //
    //        return sa;
    //    }
    //}

    protected static class AppFrame extends ApplicationTemplate.AppFrame
    {
        // Airspace layer and editor UI components.
        private AirspaceLayer airspaceLayer;
        private AirspaceBuilderModel builderModel;
        private AirspaceBuilderPanel builderView;
        private AirspaceBuilderController builderController;
        // Tool tip layer.
        //private ToolTip toolTip;
        //private RenderableLayer toolTipLayer;

        public AppFrame()
        {
            this.airspaceLayer = new AirspaceLayer();
            this.airspaceLayer.setName(AIRSPACE_LAYER_NAME);
            insertBeforePlacenames(this.getWwd(), this.airspaceLayer);
            this.getLayerPanel().update(this.getWwd());

            this.builderController = new AirspaceBuilderController(this);
            this.builderModel = new AirspaceBuilderModel();
            this.builderView = new AirspaceBuilderPanel(this.builderModel, this.builderController);
            this.getContentPane().add(this.builderView, BorderLayout.SOUTH);

            this.builderController.setModel(this.builderModel);
            this.builderController.setView(this.builderView);
            this.builderController.setResizeNewShapesToViewport(true);

            //this.toolTipLayer = new RenderableLayer();
            //this.toolTipLayer.setPickEnabled(false); // Don't want to pick the tool tip
            //insertBeforeCompass(this.getWwd(), this.toolTipLayer);

            makeMenuBar(this, this.builderController);
        }

        public AirspaceBuilderPanel getAirspaceBuilderPanel()
        {
            return this.builderView;
        }

        public AirspaceLayer getAirspaceLayer()
        {
            return this.airspaceLayer;
        }

        //public ToolTip getWorldWindToolTip()
        //{
        //    return this.toolTip;
        //}
        //
        //public void setWorldWindToolTip(ToolTip toolTip)
        //{
        //    this.toolTip = toolTip;
        //    this.toolTipLayer.removeAllRenderables();
        //    if (this.toolTip != null)
        //        this.toolTipLayer.addRenderable(this.toolTip);
        //}

        public static void makeMenuBar(JFrame frame, final AirspaceBuilderController controller)
        {
            JMenuBar menuBar = new JMenuBar();
            final JCheckBoxMenuItem resizeNewShapesItem;
            final JCheckBoxMenuItem enableEditItem;

            JMenu menu = new JMenu("File");
            {
                JMenuItem item = new JMenuItem("Open...");
                item.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                item.setActionCommand(OPEN);
                item.addActionListener(controller);
                menu.add(item);

                item = new JMenuItem("Open URL...");
                item.setActionCommand(OPEN_URL);
                item.addActionListener(controller);
                menu.add(item);

                item = new JMenuItem("Save...");
                item.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                item.setActionCommand(SAVE);
                item.addActionListener(controller);
                menu.add(item);

                menu.addSeparator();

                item = new JMenuItem("Load Demo Shapes");
                item.setActionCommand(OPEN_DEMO_AIRSPACES);
                item.addActionListener(controller);
                menu.add(item);
            }
            menuBar.add(menu);

            menu = new JMenu("Shape");
            {
                JMenu subMenu = new JMenu("New");
                for (final AirspaceFactory factory : defaultAirspaceFactories)
                {
                    JMenuItem item = new JMenuItem(factory.toString());
                    item.addActionListener(new ActionListener()
                    {
                        public void actionPerformed(ActionEvent e)
                        {
                            controller.createNewEntry(factory);
                        }
                    });
                    subMenu.add(item);
                }
                menu.add(subMenu);

                resizeNewShapesItem = new JCheckBoxMenuItem("Fit new shapes to viewport");
                resizeNewShapesItem.setActionCommand(SIZE_NEW_SHAPES_TO_VIEWPORT);
                resizeNewShapesItem.addActionListener(controller);
                resizeNewShapesItem.setState(controller.isResizeNewShapesToViewport());
                menu.add(resizeNewShapesItem);

                enableEditItem = new JCheckBoxMenuItem("Enable shape editing");
                enableEditItem.setActionCommand(ENABLE_EDIT);
                enableEditItem.addActionListener(controller);
                enableEditItem.setState(controller.isEnableEdit());
                menu.add(enableEditItem);
            }
            menuBar.add(menu);

            menu = new JMenu("Selection");
            {
                //JMenuItem item = new JMenuItem("Zoom To Selection");
                //item.setActionCommand(ZOOM_TO_SELECTED);
                //item.addActionListener(controller);
                //menu.add(item);

                JMenuItem item = new JMenuItem("Deselect");
                item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                item.setActionCommand(CLEAR_SELECTION);
                item.addActionListener(controller);
                menu.add(item);

                item = new JMenuItem("Delete");
                item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
                item.setActionCommand(REMOVE_SELECTED);
                item.addActionListener(controller);
                menu.add(item);
            }
            menuBar.add(menu);

            frame.setJMenuBar(menuBar);

            controller.addPropertyChangeListener(new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent e)
                {
                    //noinspection StringEquality
                    if (e.getPropertyName() == SIZE_NEW_SHAPES_TO_VIEWPORT)
                    {
                        resizeNewShapesItem.setSelected(controller.isResizeNewShapesToViewport());
                    }
                    else //noinspection StringEquality
                        if (e.getPropertyName() == ENABLE_EDIT)
                    {
                        enableEditItem.setSelected(controller.isEnableEdit());
                    }
                }
            });
        }
    }
    
    public static void main(String[] args)
    {
        ApplicationTemplate.start("Airspace Builder", AppFrame.class);
    }
}
