package cn.yecols.wwj;

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

public class LayerPanel extends JPanel
{
    private JPanel layersPanel;
    private JPanel westPanel;
    private JScrollPane scrollPane;

    public LayerPanel(WorldWindow wwd)
    {
        super(new BorderLayout());
        this.makePanel(wwd, new Dimension(200, 400));
    }

    public LayerPanel(WorldWindow wwd, Dimension size)
    {
        super(new BorderLayout());
        this.makePanel(wwd, size);
    }

    private void makePanel(WorldWindow wwd, Dimension size)
    {
        //����ͼ�����ؼ�
        this.layersPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        this.layersPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.fill(wwd);

        JPanel dummyPanel = new JPanel(new BorderLayout());
        dummyPanel.add(this.layersPanel, BorderLayout.NORTH);

        // ���������
        this.scrollPane = new JScrollPane(dummyPanel);
        this.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        if (size != null)
            this.scrollPane.setPreferredSize(size);

        
        westPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        westPanel.setBorder(
            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("ͼ�����")));
        westPanel.setToolTipText("����ͼ����ʾ���");
        westPanel.add(scrollPane);
        this.add(westPanel, BorderLayout.CENTER);


    }

    private Font defaultFont;
    private Font atMaxFont;

    private void updateStatus()
    {
        for (Component layerItem : this.layersPanel.getComponents())
        {
            if (!(layerItem instanceof JCheckBox))
                continue;

            LayerAction action = (LayerAction) ((JCheckBox) layerItem).getAction();
            if (!(action.layer.isMultiResolution()))
                continue;

            if ((action.layer).isAtMaxResolution())
                layerItem.setFont(this.atMaxFont);
            else
                layerItem.setFont(this.defaultFont);
        }
    }

    private void fill(WorldWindow wwd)
    {
        //ͨ����ǰwwd��Model�Ķ�ȡ�������ͼ�������
        for (Layer layer : wwd.getModel().getLayers())
        {
            LayerAction action = new LayerAction(layer, wwd, layer.isEnabled());
            JCheckBox jcb = new JCheckBox(action);
            jcb.setSelected(action.selected);
            this.layersPanel.add(jcb);

            if (defaultFont == null)
            {
                this.defaultFont = jcb.getFont();
                this.atMaxFont = this.defaultFont.deriveFont(Font.ITALIC);
            }

        }
    }

    public void update(WorldWindow wwd)
    {
        //ˢ��ͼ������
        this.layersPanel.removeAll();
        this.fill(wwd);
        this.westPanel.revalidate();
        this.westPanel.repaint();
    }


    private static class LayerAction extends AbstractAction
    {
        WorldWindow wwd;
        private Layer layer;
        private boolean selected;

        public LayerAction(Layer layer, WorldWindow wwd, boolean selected)
        {
            super(layer.getName());
            this.wwd = wwd;
            this.layer = layer;
            this.selected = selected;
            this.layer.setEnabled(this.selected);
        }

        public void actionPerformed(ActionEvent actionEvent)
        {
            //ʹѡ��ť��wwd�е�ͼ����ʾ��������
            if (((JCheckBox) actionEvent.getSource()).isSelected())
                this.layer.setEnabled(true);
            else
                this.layer.setEnabled(false);

            wwd.redraw();
        }
    }
}
