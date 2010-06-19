package cn.yecols.wwj;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.io.IOException;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.swing.JPanel;

import com.sun.media.MediaPlayer;

public class VideoPlayer {
	// 测试单例
	public static void main(String[] args) {
		System.out.print("yes");
		VideoMoniter video = new VideoMoniter();
		video.play();
	}
}

class VideoMoniter extends JPanel implements ControllerListener {

	private CaptureDeviceInfo infor;
	private MediaLocator mediaLocator; 												// 媒体位置
	private static final String url = "vfw:Microsoft WDM Image Capture (Win32):0"; 	// 设备位置
	private Panel panel;
	private Component com;
	MediaPlayer player; // 播放器

	public VideoMoniter() {
		// 初始化函数
		infor = new CaptureDeviceInfo();
		try {
			//从设备获得媒体
			infor = CaptureDeviceManager.getDevice(url);
			mediaLocator = infor.getLocator();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}

	}

	public void play() {
		try {
			//利用mediaLocator创建Player
			player = (MediaPlayer) Manager.createPlayer(mediaLocator);				
			player.addControllerListener(this);
			player.realize();
		} catch (NoPlayerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createComponent() {
		setMaximumSize(new Dimension(180, 200));
		setPreferredSize(new Dimension(180, 200));
		panel = new Panel();
		if ((com = player.getVisualComponent()) != null) {
			panel.add(com);
		}
		add(panel);
		setVisible(true);
	}

	public synchronized void controllerUpdate(ControllerEvent arg0) {
		if (arg0 instanceof RealizeCompleteEvent) {
			createComponent(); // 将播放器添加到Panel上
			player.prefetch();

		}
		if (arg0 instanceof PrefetchCompleteEvent) {
			player.start();
		}
	}
}
