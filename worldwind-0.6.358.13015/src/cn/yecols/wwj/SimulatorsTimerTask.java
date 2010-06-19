package cn.yecols.wwj;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

import cn.yecols.obj.Simulator;

public class SimulatorsTimerTask extends TimerTask{
	//ģ���������� �̳���TimerTask
	private ArrayList<Simulator> simulators;		//ģ������
	private WorldWindow wwd;						//wwd��ͼ
	private RenderableLayer simulatorsLayer;		//ģ���ͼ��
	private static final Angle anglePitch=Angle.fromDegrees(0); //����
	
	public SimulatorsTimerTask(WorldWindow wwd,ArrayList<Simulator> simulators) throws IOException{
		this.simulators=simulators;
		this.wwd=wwd;
		simulatorsLayer=new RenderableLayer();
		simulatorsLayer.setName("Simulators Layer");
	}
	
	@Override
	public void run() {
		
		//���»���ģ���
		simulatorsLayer.removeAllRenderables();
		for(int i=0;i<simulators.size();i++){
			simulators.get(i).compute();	//����λ��		
			simulatorsLayer.addRenderable(simulators.get(i));
		}
		
		//���ͼ��
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers) {
			if (l.getName().equals("Simulators Layer"))
				layers.remove(l);
		}
		layers.add(simulatorsLayer);
		
		//�����Ƕȡ��߶ȡ��͸���
		wwd.getView().setEyePosition(new Position(simulators.get(0).getCurrent(),6000));
		wwd.getView().setHeading(simulators.get(0).getHeading());
		wwd.getView().setPitch(anglePitch);
		
	    wwd.redraw();
		
		System.out.println("Yecols.timerTask");
		
		
	}
	

}
