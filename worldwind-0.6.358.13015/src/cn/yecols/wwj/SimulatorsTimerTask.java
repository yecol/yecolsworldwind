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
	//模拟点的任务类 继承自TimerTask
	private ArrayList<Simulator> simulators;		//模拟点对象
	private WorldWindow wwd;						//wwd视图
	private RenderableLayer simulatorsLayer;		//模拟点图层
	private static final Angle anglePitch=Angle.fromDegrees(0); //俯角
	
	public SimulatorsTimerTask(WorldWindow wwd,ArrayList<Simulator> simulators) throws IOException{
		this.simulators=simulators;
		this.wwd=wwd;
		simulatorsLayer=new RenderableLayer();
		simulatorsLayer.setName("Simulators Layer");
	}
	
	@Override
	public void run() {
		
		//重新画出模拟点
		simulatorsLayer.removeAllRenderables();
		for(int i=0;i<simulators.size();i++){
			simulators.get(i).compute();	//计算位置		
			simulatorsLayer.addRenderable(simulators.get(i));
		}
		
		//添加图层
		LayerList layers = wwd.getModel().getLayers();
		for (Layer l : layers) {
			if (l.getName().equals("Simulators Layer"))
				layers.remove(l);
		}
		layers.add(simulatorsLayer);
		
		//调整角度、高度、和俯角
		wwd.getView().setEyePosition(new Position(simulators.get(0).getCurrent(),6000));
		wwd.getView().setHeading(simulators.get(0).getHeading());
		wwd.getView().setPitch(anglePitch);
		
	    wwd.redraw();
		
		System.out.println("Yecols.timerTask");
		
		
	}
	

}
