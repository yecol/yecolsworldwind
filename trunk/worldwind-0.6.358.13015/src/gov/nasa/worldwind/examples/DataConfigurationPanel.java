/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.examples;

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.cache.FileStore;
import gov.nasa.worldwind.exception.WWRuntimeException;
import gov.nasa.worldwind.globes.ElevationModel;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.terrain.CompoundElevationModel;
import gov.nasa.worldwind.util.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Arrays;

/**
 * @author dcollins
 * @version $Id: DataConfigurationPanel.java 13010 2010-01-15 19:27:13Z dcollins $
 */
public class DataConfigurationPanel extends JPanel
{
    protected static class ComponentConfiguration
    {
        protected DataConfiguration dataConfig;
        protected AVList params;

        public ComponentConfiguration(DataConfiguration dataConfig, AVList params)
        {
            this.dataConfig = dataConfig;
            this.params = params;
        }

        public DataConfiguration getDataConfiguration()
        {
            return this.dataConfig;
        }

        public AVList getParams()
        {
            return this.params;
        }
    }

    protected FileStore fileStore;
    protected WorldWindow worldWindow;
    protected JPanel layersPanel;
    protected JPanel modelsPanel;

    public DataConfigurationPanel(FileStore fileStore, WorldWindow worldWindow)
    {
        if (fileStore == null)
        {
            String msg = Logging.getMessage("nullValue.FileStoreIsNull");
            Logging.logger().severe(msg);
            throw new IllegalArgumentException(msg);
        }

        this.fileStore = fileStore;
        this.worldWindow = worldWindow;

        this.initComponents();
        this.layoutComponents();
        this.update();
    }

    public void update()
    {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                doUpdate();
            }
        });
        t.start();
    }

    protected void doUpdate()
    {
        final ComponentConfiguration[] configurations = this.listComponentConfigurations(fileStore);
        if (configurations == null)
            return;

        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                fill(Arrays.asList(configurations), worldWindow);
            }
        });
    }

    protected ComponentConfiguration[] listComponentConfigurations(FileStore fileStore)
    {
        String[] fileNames = fileStore.listTopFileNames(null, new DataConfigurationFilter());
        if (fileNames == null)
            return null;

        ComponentConfiguration[] configurations = new ComponentConfiguration[fileNames.length];

        for (int i = 0; i < fileNames.length; i++)
        {
            DataConfiguration configInfo = this.openDataConfig(fileStore, fileNames[i]);
            if (configInfo == null)
                continue;

            AVList params = new AVListImpl();
            DataConfigurationUtils.getFileStoreParams(fileStore, fileNames[i], params);
            configurations[i] = new ComponentConfiguration(configInfo, params);
        }

        return configurations;
    }

    protected DataConfiguration openDataConfig(FileStore fileStore, String fileName)
    {
        URL url = fileStore.findFile(fileName, false);
        if (url == null)
            return null;

        try
        {
            return DataConfigurationUtils.openDataConfigURL(url);
        }
        catch (WWRuntimeException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    protected void fill(Iterable<? extends ComponentConfiguration> configurations, WorldWindow worldWindow)
    {
        this.layersPanel.removeAll();
        this.modelsPanel.removeAll();

        for (ComponentConfiguration config : configurations)
        {
            ConfigAction action = new ConfigAction(config.getDataConfiguration(), config.getParams(), worldWindow);
            JCheckBox jcb = new JCheckBox(action);
            jcb.setSelected(false);

            if (action.getConfigType().equalsIgnoreCase("Layer"))
            {
                this.layersPanel.add(jcb);
            }
            else if (action.getConfigType().equalsIgnoreCase("ElevationModel"))
            {
                this.modelsPanel.add(jcb);
            }
        }

        this.revalidate();
    }

    protected void initComponents()
    {
        this.layersPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        this.modelsPanel = new JPanel(new GridLayout(0, 1, 0, 4));
    }

    protected void layoutComponents()
    {
        this.setLayout(new GridLayout(0, 1, 0, 0)); // nrows, ncols, hgap, vgap

        Component c = this.layoutPanel(this.layersPanel, "Layer Configurations");
        this.add(c);

        c = this.layoutPanel(this.modelsPanel, "Elevation Model Configurations");
        this.add(c);
    }

    protected Component layoutPanel(JPanel panel, String title)
    {
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Must put the grid in a container to prevent scroll panel from stretching its vertical spacing.
        JPanel dummyPanel = new JPanel(new BorderLayout());
        dummyPanel.add(panel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(dummyPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Add the scroll pane and data config panel to a titled panel that will resize with the main window.
        JPanel titlePanel = new JPanel(new GridLayout(0, 1, 0, 10));
        titlePanel.setBorder(
            new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder(title)));
        titlePanel.add(scrollPane);

        return titlePanel;
    }

    protected class ConfigAction extends AbstractAction
    {
        protected DataConfiguration dataConfig;
        protected AVList params;
        protected WorldWindow worldWindow;
        protected Object wwComponent;
        protected boolean componentCreated;

        public ConfigAction(DataConfiguration dataConfig, AVList params, WorldWindow worldWindow)
        {
            super(dataConfig.getString("DisplayName"));
            this.dataConfig = dataConfig;
            this.params = params.copy();
            this.worldWindow = worldWindow;
            this.componentCreated = false;
        }

        public void actionPerformed(ActionEvent actionEvent)
        {
            boolean enable = ((JCheckBox) actionEvent.getSource()).isSelected();
            this.updateComponent(enable);
        }

        public String getConfigType()
        {
            return this.dataConfig.getType();
        }

        public Object getConfigComponent()
        {
            if (!this.componentCreated)
            {
                this.wwComponent = this.createComponent(this.dataConfig, this.params);
                this.componentCreated = true;
            }

            return this.wwComponent;
        }

        protected Object createComponent(DataConfiguration dataConfig, AVList params)
        {
            if (dataConfig.getType().equalsIgnoreCase("Layer"))
            {
                return this.createComponentFromFactoryConfiguration(dataConfig, params, AVKey.LAYER_FACTORY);
            }
            else if (dataConfig.getType().equalsIgnoreCase("ElevationModel"))
            {
                return this.createComponentFromFactoryConfiguration(dataConfig, params, AVKey.ELEVATION_MODEL_FACTORY);
            }
            else
            {
                return null;
            }
        }

        protected Object createComponentFromFactoryConfiguration(DataConfiguration dataConfig, AVList params,
            String factoryKey)
        {
            try
            {
                Factory factory = (Factory) WorldWind.createConfigurationComponent(factoryKey);
                return factory.createFromDataConfig(dataConfig, params);
            }
            catch (Exception e)
            {
                String message = Logging.getMessage("generic.CreationFromConfigurationFileFailed",
                    dataConfig.getName());
                Logging.logger().log(java.util.logging.Level.SEVERE, message, e);
                return null;
            }
        }

        protected void updateComponent(boolean enable)
        {
            if (this.getConfigType().equalsIgnoreCase("Layer"))
            {
                Layer layer = (Layer) this.getConfigComponent();
                LayerList layers = this.worldWindow.getModel().getLayers();

                layer.setEnabled(enable);

                if (enable)
                {
                    if (!layers.contains(layer))
                        ApplicationTemplate.insertBeforePlacenames(this.worldWindow, layer);
                    DataConfigurationPanel.this.firePropertyChange("LayersPanelUpdated", null, layer);
                }
                else
                {
                    layers.remove(layer);
                    DataConfigurationPanel.this.firePropertyChange("LayersPanelUpdated", layer, null);
                }
            }
            else if (this.getConfigType().equalsIgnoreCase("ElevationModel"))
            {
                ElevationModel model = (ElevationModel) this.getConfigComponent();
                ElevationModel currentModel = this.worldWindow.getModel().getGlobe().getElevationModel();
                CompoundElevationModel compoundModel = (currentModel instanceof CompoundElevationModel) ?
                    (CompoundElevationModel) currentModel : null;

                if (enable)
                {
                    if (compoundModel == null)
                    {
                        compoundModel = new CompoundElevationModel();
                        compoundModel.addElevationModel(currentModel);
                        this.worldWindow.getModel().getGlobe().setElevationModel(compoundModel);
                    }

                    if (!compoundModel.getElevationModels().contains(model))
                        compoundModel.addElevationModel(model);
                }
                else
                {
                    if (compoundModel != null && compoundModel.getElevationModels().contains(model))
                        compoundModel.removeElevationModel(model);
                }
            }

            this.worldWindow.redraw();
        }
    }
}
