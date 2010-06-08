package cn.yecols.wwj;

import java.awt.Component;
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

import com.sun.media.MediaPlayer;


public class Vt {
     public static void main(String[] args){
    	 System.out.print("yes");
    	 VideoCapture video = new VideoCapture();
         video.play();
     }
}

class VideoCapture extends Frame implements ControllerListener {
    
	  
	private CaptureDeviceInfo infor ;
    private MediaLocator mediaLocator ;
    //private String url = "vfw:Microsoft WDM Image Capture (Win32):0";
    private static final String url = "vfw:Microsoft WDM Image Capture (Win32):0";
    
    private Component com;
    private Panel panel ;
    MediaPlayer player;


    public VideoCapture() {
	       infor = new CaptureDeviceInfo();
	       try{
	    	   System.out.println("yecolsD:"+CaptureDeviceManager.getDeviceList(null).toString());
           infor = CaptureDeviceManager.getDevice(url);
           
           System.out.println(infor.toString());
           mediaLocator = infor.getLocator();
	       }
	       catch(Exception e){
	    	   System.out.println(e.getStackTrace());
	       }
          
    }

    public void play() {
           try {
                  player = (MediaPlayer) Manager.createPlayer(mediaLocator);//����mediaLocator����Player
                  player.addControllerListener(this);
                  player.realize();
           } catch (NoPlayerException e) {
                  // TODO �Զ����� catch ��
                  e.printStackTrace();
           } catch (IOException e) {
                  // TODO �Զ����� catch ��
                  e.printStackTrace();
           }
           }

    public void createComponent() {
           setTitle("��Ƶ�ź�");
           //addWindowListener(new WinClose());
           setBounds(100,100,200,200);
           panel = new Panel();
          
           if((com = player.getVisualComponent()) != null) {
                  panel.add(com);
           }
           add(panel);
           setVisible(true);
    }

    public synchronized void controllerUpdate(ControllerEvent arg0) {
           // TODO �Զ����ɷ������
           if(arg0 instanceof RealizeCompleteEvent) {
                  System.out.println("realized");
                  createComponent();                 //����������ӵ�Panel��
                  player.prefetch();

           }
           if(arg0 instanceof PrefetchCompleteEvent) {
                  player.start();
                  System.out.println("prefetched");
           }
    }
}

