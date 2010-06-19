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
	// ���Ե���
	public static void main(String[] args) {
		System.out.print("yes");
		VideoMoniter video = new VideoMoniter();
		video.play();
	}
}

class VideoMoniter extends JPanel implements ControllerListener {

	private CaptureDeviceInfo infor;
	private MediaLocator mediaLocator; 												// ý��λ��
	private static final String url = "vfw:Microsoft WDM Image Capture (Win32):0"; 	// �豸λ��
	private Panel panel;
	private Component com;
	MediaPlayer player; // ������

	public VideoMoniter() {
		// ��ʼ������
		infor = new CaptureDeviceInfo();
		try {
			//���豸���ý��
			infor = CaptureDeviceManager.getDevice(url);
			mediaLocator = infor.getLocator();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}

	}

	public void play() {
		try {
			//����mediaLocator����Player
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
			createComponent(); // ����������ӵ�Panel��
			player.prefetch();

		}
		if (arg0 instanceof PrefetchCompleteEvent) {
			player.start();
		}
	}
}
