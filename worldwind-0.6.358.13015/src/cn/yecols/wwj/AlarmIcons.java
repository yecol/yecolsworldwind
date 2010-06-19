package cn.yecols.wwj;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.IconLayer;
import gov.nasa.worldwind.render.UserFacingIcon;
import gov.nasa.worldwind.render.PatternFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlarmIcons {
	private UserFacingIcon icon;
	private WorldWindow wwd;

	public AlarmIcons(WorldWindow wwd) {
		this.wwd = wwd;

		IconLayer layer = new IconLayer();
		layer.setName("alarm icon");
		layer.setMaxActiveAltitude(7000);							// 设置显示该图层的最高高度
		icon = new UserFacingIcon("src/cn/yecols/images/info.png",	// 警告图标
				new Position(LatLon.fromDegrees(30.2672, 120.1287), 0));
		icon.setSize(new Dimension(64, 64));
		layer.addIcon(icon);
		this.getWwd().getModel().getLayers().add(layer);

		// Create bitmaps
		BufferedImage circleRed = createBitmap(PatternFactory.PATTERN_CIRCLE,
				Color.RED);


		PulsingAlarmAction paa = new PulsingAlarmAction("Pulsing Red Circle",
				circleRed, 100);
		paa.actionPerformed();

	}

	private WorldWindow getWwd() {
		// TODO Auto-generated method stub
		return this.wwd;
	}

	// Create a blurred pattern bitmap
	private BufferedImage createBitmap(String pattern, Color color) {
		// Create bitmap with pattern
		BufferedImage image = PatternFactory.createPattern(pattern,
				new Dimension(128, 128), 0.7f, color, new Color(color.getRed(),
						color.getGreen(), color.getBlue(), 0));
		// Blur a lot to get a fuzzy edge
		image = PatternFactory.blur(image, 13);
		image = PatternFactory.blur(image, 13);
		image = PatternFactory.blur(image, 13);
		image = PatternFactory.blur(image, 13);
		return image;
	}

	private class PulsingAlarmAction {
		protected final Object bgIconPath;
		protected int frequency;
		protected int scaleIndex = 0;
		protected double[] scales = new double[] { 1.25, 1.5, 1.75, 2, 2.25,
				2.5, 2.75, 3, 3.25, 3.5, 3.25, 3, 2.75, 2.5, 2.25, 2, 1.75, 1.5 };
		protected Timer timer;

		private PulsingAlarmAction(String name, Object bgp, int frequency) {

			this.bgIconPath = bgp;
			this.frequency = frequency;
		}

		private PulsingAlarmAction(String name, Object bgp, int frequency,
				double[] scales) {
			this(name, bgp, frequency);
			this.scales = scales;
		}

		public void actionPerformed() {
			if (timer == null) {
				timer = new Timer(frequency, new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						icon.setBackgroundScale(scales[++scaleIndex
								% scales.length]);
						getWwd().redraw();
					}
				});

			}
			icon.setBackgroundImage(bgIconPath);
			scaleIndex = 0;
			timer.start();
		}
	}

	private class FlashingAlarmAction extends PulsingAlarmAction {
		private FlashingAlarmAction(String name, Object bgp, int frequency) {
			super(name, bgp, frequency, new double[] { 2, 0.5 });
		}
	}
}
