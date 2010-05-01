package gov.nasa.worldwind.examples;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.render.Annotation;
import gov.nasa.worldwind.render.AnnotationShadow;
import gov.nasa.worldwind.render.GlobeAnnotation;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class YecolsPanel extends JPanel {
	private JPanel yecolsPanel;
	private JPanel annotationPanel;
	private JPanel westPanel;
	private JScrollPane scrollPane;
	protected ArrayList<YecolsAnnotationData> annotationList;

	public YecolsPanel(WorldWindow wwd) {
		// Make a panel at a default size.
		super(new BorderLayout());
		annotationList = new ArrayList<YecolsAnnotationData>();
		this.makePanel(wwd, new Dimension(400, 400));
	}

	public YecolsPanel(WorldWindow wwd, Dimension size) {
		// Make a panel at a specified size.
		super(new BorderLayout());
		annotationList = new ArrayList<YecolsAnnotationData>();
		this.makePanel(wwd, size);
	}
	
	public YecolsPanel(WorldWindow wwd, Dimension size,ArrayList<YecolsAnnotationData> list) {
		// Make a panel at a specified size and pass the list.
		super(new BorderLayout());
		this.makePanel(wwd, size);
		this.annotationList=list;
	}

	private void makePanel(WorldWindow wwd, Dimension size) {
		// Make and fill the panel holding the layer titles.
		this.yecolsPanel = new JPanel(new GridLayout(0, 1, 0, 4));
		this.yecolsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.annotationPanel = new JPanel(new BorderLayout());
		final JTextArea annotationTextArea = new JTextArea();
		AddAnnotationAction addAnnotationAction = new AddAnnotationAction(wwd,
				annotationTextArea,annotationList);
		JButton annotationTextButton = new JButton(addAnnotationAction);

		annotationTextButton.setText("Add");
		JPanel annotationTitlePanel = new JPanel(new FlowLayout());
		annotationTitlePanel.add(new JLabel("文本标记"));
		annotationTitlePanel.add(annotationTextButton);
		this.annotationPanel.add(annotationTitlePanel, BorderLayout.NORTH);
		this.annotationPanel.add(annotationTextArea, BorderLayout.CENTER);
		// this.annotationPanel

		this.yecolsPanel.add(annotationPanel);
		this.yecolsPanel.setPreferredSize(size);
		this.add(yecolsPanel);
		/*
		 * this.fill(wwd);
		 * 
		 * // Must put the layer grid in a container to prevent scroll panel
		 * from stretching their vertical spacing. JPanel dummyPanel = new
		 * JPanel(new BorderLayout()); dummyPanel.add(this.yecolsPanel,
		 * BorderLayout.NORTH);
		 * 
		 * // Put the name panel in a scroll bar. this.scrollPane = new
		 * JScrollPane(dummyPanel);
		 * this.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0,
		 * 0)); if (size != null) this.scrollPane.setPreferredSize(size);
		 * 
		 * // Add the scroll bar and name panel to a titled panel that will
		 * resize with the main window. westPanel = new JPanel(new GridLayout(0,
		 * 1, 0, 10)); westPanel.setBorder( new
		 * CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new
		 * TitledBorder("Layers"))); westPanel.setToolTipText("Layers to Show");
		 * westPanel.add(scrollPane); this.add(westPanel, BorderLayout.CENTER);
		 */

	}

	private Font defaultFont;
	private Font atMaxFont;

	private void updateStatus() {
		for (Component layerItem : this.yecolsPanel.getComponents()) {
			if (!(layerItem instanceof JCheckBox))
				continue;

			LayerAction action = (LayerAction) ((JCheckBox) layerItem)
					.getAction();
			if (!(action.layer.isMultiResolution()))
				continue;

			if ((action.layer).isAtMaxResolution())
				layerItem.setFont(this.atMaxFont);
			else
				layerItem.setFont(this.defaultFont);
		}
	}

	private void fill(WorldWindow wwd) {
		// Fill the layers panel with the titles of all layers in the world
		// window's current model.
		for (Layer layer : wwd.getModel().getLayers()) {
			LayerAction action = new LayerAction(layer, wwd, layer.isEnabled());
			JCheckBox jcb = new JCheckBox(action);
			jcb.setSelected(action.selected);
			this.yecolsPanel.add(jcb);

			if (defaultFont == null) {
				this.defaultFont = jcb.getFont();
				this.atMaxFont = this.defaultFont.deriveFont(Font.ITALIC);
			}

		}
	}

	public void update(WorldWindow wwd) {
		// Replace all the layer names in the layers panel with the names of the
		// current layers.
		this.yecolsPanel.removeAll();
		this.fill(wwd);
		this.westPanel.revalidate();
		this.westPanel.repaint();
	}

	@Override
	public void setToolTipText(String string) {
		this.scrollPane.setToolTipText(string);
	}

	private static class LayerAction extends AbstractAction {
		WorldWindow wwd;
		private Layer layer;
		private boolean selected;

		public LayerAction(Layer layer, WorldWindow wwd, boolean selected) {
			super(layer.getName());
			this.wwd = wwd;
			this.layer = layer;
			this.selected = selected;
			this.layer.setEnabled(this.selected);
		}

		public void actionPerformed(ActionEvent actionEvent) {
			// Simply enable or disable the layer based on its toggle button.
			if (((JCheckBox) actionEvent.getSource()).isSelected())
				this.layer.setEnabled(true);
			else
				this.layer.setEnabled(false);

			wwd.redraw();
		}
	}

	private static class AddAnnotationAction extends AbstractAction {
		WorldWindow wwd;
		JTextArea annotationContent;
		ArrayList<YecolsAnnotationData> annotationList;

		public AddAnnotationAction(WorldWindow wwd,
				JTextArea annotationContent,
				ArrayList<YecolsAnnotationData> list) {
			this.wwd = wwd;
			this.annotationContent = annotationContent;
			this.annotationList = list;
		}

		public void actionPerformed(ActionEvent actionEvent) {
			// test text.
			//System.out.println(annotationContent.getText());
			//System.out.println("yecols.cn");

			// 创建标记
			// GlobeAnnotation ga;
			String title = "this is test title";
			String content = annotationContent.getText();

			YecolsAnnotationData yd = new YecolsAnnotationData(wwd
					.getSceneController().getDrawContext()
					.getViewportCenterPosition(), title, content);
			
			this.annotationList.add(yd);

			RenderableLayer layer = new RenderableLayer();
			layer.setName("Surface Objects");
			
			for(YecolsAnnotationData yad:this.annotationList){
				System.out.println(yad.toString());
				layer.addRenderable(yad.MakeGlobeAnnotation());
				layer.addRenderable(yad.MakeSurfaceIcon());
			}

			int compassPosition = 0;
			// boolean hasSurfaceObjectsLayer=false;
			LayerList layers = wwd.getModel().getLayers();
			for (Layer l : layers) {
				//System.out.println(l.getName());// test
				if (l instanceof CompassLayer)
					compassPosition = layers.indexOf(l);
				if (l.getName().equals("Surface Objects")) {
					layers.remove(l);
					// hasSurfaceObjectsLayer=true;
				}
			}
			layers.add(compassPosition, layer);

			wwd.redraw();
		}
	}

}
