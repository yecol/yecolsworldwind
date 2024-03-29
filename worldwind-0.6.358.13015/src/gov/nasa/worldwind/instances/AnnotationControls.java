/* Copyright (C) 2001, 2009 United States Government as represented by
the Administrator of the National Aeronautics and Space Administration.
All Rights Reserved.
*/
package gov.nasa.worldwind.instances;

import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.examples.util.*;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.pick.*;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwind.util.*;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * @author dcollins
 * @version $Id: AnnotationControls.java 11475 2009-06-06 01:39:21Z tgaskins $
 */
public class AnnotationControls extends ApplicationTemplate
{
    protected final static String AUDIO = "Audio";
    protected final static String IMAGES = "Images";

    protected final static String ICON_AUDIO = "images/audioicon-64.png";
    protected final static String ICON_IMAGES = "images/imageicon-64.png";

    protected final static String URL_AUDIO_WELCOME = "http://worldwind.arc.nasa.gov/java/demos/sounds/1-welcome.wav";
    protected final static String URL_AUDIO_MUSIC = "http://worldwind.arc.nasa.gov/java/demos/sounds/spacemusic.au";
    protected final static String URL_AUDIO_PERU = "http://worldwind.arc.nasa.gov/java/demos/sounds/destMouse_Peru.AIF";

    protected final static String URL_IMAGE_01 = "http://worldwind.arc.nasa.gov/java/demos/images/MountStHelens_01_800.jpg";
    protected final static String URL_IMAGE_02 = "http://worldwind.arc.nasa.gov/java/demos/images/the_nut.jpg";
    protected final static String URL_IMAGE_03 = "http://worldwind.arc.nasa.gov/java/demos/images/ireland.jpg";
    protected final static String URL_IMAGE_04 = "http://worldwind.arc.nasa.gov/java/demos/images/new_zealand.gif";
    protected final static String URL_IMAGE_05 = "http://worldwind.arc.nasa.gov/java/demos/images/pond.jpg";

    public static class AppFrame extends ApplicationTemplate.AppFrame implements SelectListener
    {
        protected IconLayer iconLayer;
        protected WWIcon highlit;
        protected RenderableLayer contentLayer;
        protected BasicDragger dragger;

        public AppFrame()
        {
            this.iconLayer = createIconLayer();
            this.contentLayer = new RenderableLayer();
            insertBeforePlacenames(this.getWwd(), this.iconLayer);
            insertBeforePlacenames(this.getWwd(), this.contentLayer);

            this.getWwd().addSelectListener(this);
            this.dragger = new BasicDragger(this.getWwd());
        }

        public IconLayer getIconLayer()
        {
            return this.iconLayer;
        }

        public RenderableLayer getContentLayer()
        {
            return this.contentLayer;
        }

        @SuppressWarnings({"StringEquality"})
        public void selected(SelectEvent e)
        {
            if (e == null)
                return;

            PickedObject topPickedObject = e.getTopPickedObject();

            if (e.getEventAction() == SelectEvent.LEFT_PRESS)
            {
                if (topPickedObject != null && topPickedObject.getObject() instanceof WWIcon)
                {
                    WWIcon selected = (WWIcon) topPickedObject.getObject();
                    this.highlight(selected);
                }
                else
                {
                    this.highlight(null);
                }
            }
            else if (e.getEventAction() == SelectEvent.LEFT_DOUBLE_CLICK)
            {
                if (topPickedObject != null && topPickedObject.getObject() instanceof WWIcon)
                {
                    WWIcon selected = (WWIcon) topPickedObject.getObject();
                    this.highlight(selected);
                    this.openResource(selected);
                }
            }
            else if (e.getEventAction() == SelectEvent.DRAG || e.getEventAction() == SelectEvent.DRAG_END)
            {
                this.dragger.selected(e);
            }
        }

        public void highlight(WWIcon icon)
        {
            if (this.highlit == icon)
                return;

            if (this.highlit != null)
            {
                this.highlit.setHighlighted(false);
                this.highlit = null;
            }

            if (icon != null)
            {
                this.highlit = icon;
                this.highlit.setHighlighted(true);
            }

            this.getWwd().redraw();            
        }

        protected void closeResource(ContentAnnotation content)
        {
            if (content == null)
                return;

            content.detach();
        }

        protected void openResource(WWIcon icon)
        {
            if (icon == null || !(icon instanceof AVList))
                return;

            ContentAnnotation content = this.createContent(icon.getPosition(), (AVList) icon);

            if (content != null)
            {
                content.attach();
            }
        }

        protected ContentAnnotation createContent(Position position, AVList params)
        {
            return createContentAnnotation(this, position, params);
        }
    }

    public static class ContentAnnotation implements ActionListener
    {
        protected AppFrame appFrame;
        protected DialogAnnotation annnotation;
        protected DialogAnnotationController controller;

        public ContentAnnotation(AppFrame appFrame, DialogAnnotation annnotation, DialogAnnotationController controller)
        {
            this.appFrame = appFrame;
            this.annnotation = annnotation;
            this.annnotation.addActionListener(this);
            this.controller = controller;
        }

        public AppFrame getAppFrame()
        {
            return this.appFrame;
        }

        public DialogAnnotation getAnnotation()
        {
            return this.annnotation;
        }

        public DialogAnnotationController getController()
        {
            return this.controller;
        }

        @SuppressWarnings({"StringEquality"})
        public void actionPerformed(ActionEvent e)
        {
            if (e == null)
                return;

            if (e.getActionCommand() == AVKey.CLOSE)
            {
                this.getAppFrame().closeResource(this);
            }
        }

        public void detach()
        {
            this.getController().setEnabled(false);

            RenderableLayer layer = this.getAppFrame().getContentLayer();
            layer.removeRenderable(this.getAnnotation());
        }

        public void attach()
        {
            this.getController().setEnabled(true);

            RenderableLayer layer = this.appFrame.getContentLayer();
            layer.removeRenderable(this.getAnnotation());
            layer.addRenderable(this.getAnnotation());
        }
    }

    public static class AudioContentAnnotation extends ContentAnnotation
    {
        protected Clip clip;
        protected Object source;
        protected Thread readThread;

        public AudioContentAnnotation(AppFrame appFrame, AudioPlayerAnnotation annnotation,
            AudioPlayerAnnotationController controller, Object source)
        {
            super(appFrame, annnotation, controller);
            this.source = source;
            this.retrieveAndSetClip(source);
        }

        public Object getSource()
        {
            return this.source;
        }

        public void detach()
        {
            super.detach();

            // Stop any threads or timers the controller may be actively running.
            AudioPlayerAnnotationController controller = (AudioPlayerAnnotationController) this.getController();
            if (controller != null)
            {
                this.stopController(controller);
            }

            // Stop any threads that may be reading the audio source.
            this.stopClipRetrieval();
        }

        @SuppressWarnings({"StringEquality"})
        protected void stopController(AudioPlayerAnnotationController controller)
        {
            String status = controller.getClipStatus();
            if (status == AVKey.PLAY)
            {
                controller.stopClip();
            }
        }

        protected void retrieveAndSetClip(Object source)
        {
            this.startClipRetrieval(source);
        }

        protected void doRetrieveAndSetClip(final Object source)
        {
            javax.swing.SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    getAnnotation().setBusy(true);
                    appFrame.getWwd().redraw();
                }
            });

            final Clip clip = this.readClip(source);

            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    AudioPlayerAnnotationController controller = (AudioPlayerAnnotationController) getController();
                    if (controller != null)
                    {
                        controller.setClip(clip);
                    }

                    AudioPlayerAnnotation annotation = (AudioPlayerAnnotation) getAnnotation();
                    if (annotation != null)
                    {
                        if (clip == null)
                        {
                            annotation.getTitleLabel().setText(createErrorTitle(source.toString()));
                        }
                    }

                    getAnnotation().setBusy(false);
                    appFrame.getWwd().redraw();
                }
            });
        }

        protected Clip readClip(Object source)
        {
            try
            {
                java.net.URL url = new java.net.URL(source.toString());
                return openAudioURL(url);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        protected void startClipRetrieval(final Object source)
        {
            this.readThread = new Thread(new Runnable()
            {
                public void run()
                {
                    doRetrieveAndSetClip(source);
                }
            });
            this.readThread.start();
        }

        protected void stopClipRetrieval()
        {
            if (this.readThread != null)
            {
                if (this.readThread.isAlive())
                {
                    this.readThread.interrupt();
                }
            }

            this.readThread = null;
        }
    }

    public static class ImageContentAnnotation extends ContentAnnotation
    {
        public ImageContentAnnotation(AppFrame appFrame, SlideShowAnnotation annnotation,
            SlideShowAnnotationController controller)
        {
            super(appFrame, annnotation, controller);
        }

        public void detach()
        {
            super.detach();

            // Stop any threads or timers the controller may be actively running.
            SlideShowAnnotationController controller = (SlideShowAnnotationController) this.getController();
            if (controller != null)
            {
                this.stopController(controller);
            }
        }

        @SuppressWarnings({"StringEquality"})
        protected void stopController(SlideShowAnnotationController controller)
        {
            String state = controller.getState();
            if (state == AVKey.PLAY)
            {
                controller.stopSlideShow();
            }

            controller.stopRetrievalTasks();
        }
    }

    public static IconLayer createIconLayer()
    {
        IconLayer layer = new IconLayer();
        layer.setPickEnabled(true);

        WWIcon icon = createIcon(AUDIO,
            Position.fromDegrees(37.304051, -121.872734, 0),
            "Welcome to Java Sound", URL_AUDIO_WELCOME);
        layer.addIcon(icon);

        icon = createIcon(AUDIO,
            Position.fromDegrees(28.533513, -81.375789, 0),
            "Music from the Java Sound demo", URL_AUDIO_MUSIC);
        layer.addIcon(icon);

        icon = createIcon(AUDIO,
            Position.fromDegrees(-10, -76, 0),
            "Peru<br/><i><font color=\"#808080\">\"Take a step back in time...\"</font></i>", URL_AUDIO_PERU);
        layer.addIcon(icon);

        Iterable sources = java.util.Arrays.asList(URL_IMAGE_01, URL_IMAGE_02, URL_IMAGE_03, URL_IMAGE_04);
        icon = createIcon(IMAGES,
            Position.fromDegrees(45.304051, -121.872734, 0),
            "", sources);
        layer.addIcon(icon);

        sources = java.util.Arrays.asList(URL_IMAGE_05);
        icon = createIcon(IMAGES,
            Position.fromDegrees(-12, -70, 0),
            "", sources);
        layer.addIcon(icon);

        return layer;
    }

    public static WWIcon createIcon(Object type, Position position, String title, Object data)
    {
        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (title == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (data == null)
        {
            String message = Logging.getMessage("nullValue.DataSetIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String iconPath = (type == AUDIO) ? ICON_AUDIO : ICON_IMAGES;

        UserFacingIcon icon = new UserFacingIcon(iconPath, position);
        icon.setSize(new java.awt.Dimension(64, 64));
        icon.setValue(AVKey.DATA_TYPE, type);
        icon.setValue(AVKey.TITLE, title);
        icon.setValue(AVKey.URL, data);
        return icon;
    }

    @SuppressWarnings({"StringEquality"})
    public static ContentAnnotation createContentAnnotation(AppFrame appFrame, Position position, AVList params)
    {
        if (appFrame == null)
        {
            String message = "AppFrameIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (params == null)
        {
            String message = Logging.getMessage("nullValue.ParamsIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        String type = params.getStringValue(AVKey.DATA_TYPE);
        String title = params.getStringValue(AVKey.TITLE);
        Object source = params.getValue(AVKey.URL);

        if (type == AUDIO)
        {
            return createAudioAnnotation(appFrame, position, title, source);
        }
        else if (type == IMAGES)
        {
            return createImageAnnotation(appFrame, position, title, (Iterable) source);
        }

        return null;
    }

    public static ContentAnnotation createAudioAnnotation(AppFrame appFrame, Position position, String title,
        Object source)
    {
        if (appFrame == null)
        {
            String message = "AppFrameIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (title == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (source == null)
        {
            String message = Logging.getMessage("nullValue.SourceIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        AudioPlayerAnnotation annotation = new AudioPlayerAnnotation(position);
        annotation.setAlwaysOnTop(true);
        annotation.getTitleLabel().setText(title);

        AudioPlayerAnnotationController controller = new AudioPlayerAnnotationController(appFrame.getWwd(), annotation);

        return new AudioContentAnnotation(appFrame, annotation, controller, source);
    }

    @SuppressWarnings({"TypeParameterExplicitlyExtendsObject"})
    public static ContentAnnotation createImageAnnotation(AppFrame appFrame, Position position, String title,
        Iterable sources)
    {
        if (appFrame == null)
        {
            String message = "AppFrameIsNull";
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (position == null)
        {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (title == null)
        {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        if (sources == null)
        {
            String message = Logging.getMessage("nullValue.IterableIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        SlideShowAnnotation annotation = new SlideShowAnnotation(position);
        annotation.setAlwaysOnTop(true);
        annotation.getTitleLabel().setText(title);

        java.util.List<Object> imageSources = new java.util.ArrayList<Object>();
        for (Object source : sources)
        {
            java.net.URL url = urlFromPath(source.toString());
            if (url != null)
            {
                imageSources.add(url);
            }
            else
            {
                imageSources.add(source);
            }
        }

        SlideShowAnnotationController controller = new SlideShowAnnotationController(appFrame.getWwd(), annotation, 
            imageSources);

        return new ImageContentAnnotation(appFrame, annotation, controller);
    }

    public static Clip openAudioURL(java.net.URL url) throws Exception
    {
        if (url == null)
        {
            String message = Logging.getMessage("nullValue.URLIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        Clip clip = null;

        AudioInputStream ais = null;
        try
        {
            ais = AudioSystem.getAudioInputStream(url);
            AudioFormat format = ais.getFormat();

            /**
             * Code taken from Java Sound demo at
             * http://java.sun.com/products/java-media/sound/samples/JavaSoundDemo
             *
             * we can't yet open the device for ALAW/ULAW playback,
             * convert ALAW/ULAW to PCM
             */
            if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
                (format.getEncoding() == AudioFormat.Encoding.ALAW))
            {
                AudioFormat tmp = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(),
                    format.getSampleSizeInBits() * 2,
                    format.getChannels(),
                    format.getFrameSize() * 2,
                    format.getFrameRate(),
                    true);
                ais = AudioSystem.getAudioInputStream(tmp, ais);
                format = tmp;
            }

            DataLine.Info info = new DataLine.Info(
                Clip.class, 
                ais.getFormat(),
                ((int) ais.getFrameLength() * format.getFrameSize()));

            clip = (Clip) AudioSystem.getLine(info);
            clip.open(ais);
        }
        finally
        {
            if (ais != null)
            {
                ais.close();
            }
        }

        return clip;
    }

    public static java.net.URL urlFromPath(String urlString)
    {
        try
        {
            return new java.net.URL(urlString);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static String createErrorTitle(String path)
    {
        if (path == null)
        {
            String message = Logging.getMessage("nullValue.PathIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Cannot open the resource at <i>").append(path).append("</i>");
        return sb.toString();
    }

    public static String createTitle(Iterable sources)
    {
        if (sources == null)
        {
            String message = Logging.getMessage("nullValue.IterableIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }

        StringBuilder sb = new StringBuilder();

        java.util.Iterator iter = sources.iterator();
        while (iter.hasNext())
        {
            Object o = iter.next();

            sb.append(o);

            if (iter.hasNext())
            {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    public static void main(String[] args)
    {
        ApplicationTemplate.start("World Wind Annotation Controls", AppFrame.class);
    }
}
