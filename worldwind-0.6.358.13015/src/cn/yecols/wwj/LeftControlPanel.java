package cn.yecols.wwj;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;


import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import cn.yecols.obj.Place;
import cn.yecols.obj.Simulator;
import cn.yecols.obj.Stop;


import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ScreenImage;
import gov.nasa.worldwind.render.SurfaceIcon;
import gov.nasa.worldwind.render.markers.Marker;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;
import gov.nasa.worldwind.view.BasicView;
import gov.nasa.worldwind.view.firstperson.BasicFlyView;
import gov.nasa.worldwind.view.firstperson.FlyToFlyViewAnimator;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;

public class LeftControlPanel extends JPanel {
	private final WorldWindow wwd;								//地球窗口
	private final MeasureTool measureTool;						//测量工具类
	private WeightedGraph<LatLon, DefaultWeightedEdge> g;		//街道网

	// ---------------------辅助工具类----------------------------------
	private PlacesDataLayerUtil placesDataLayerUtil;			//地名图层工具类

	// ----------------------------------------------------------------
	private RenderableLayer annotationLayer;					//标注图层，一直显示在上方
	private boolean attachedAnnotationLayer=false;				//标示当前是否有标注图层

	// ---------------------界面控件------------------------------------
	private JButton loadStreetsData;							
	private JButton startPosition;
	private JComboBox endPosition;
	private JButton computeShortestPath;
	private JButton showPlaces;
	private JButton simulateTourists;
	private JButton simulateTourist;
	private JButton streetsTourist;
	private JTextArea infomation;
	private JPanel infoPanel,emergencePanel,monitorPanel;
	private LayerPanel layerPanel;
	private JScrollPane scrollPane;
	private JTextField peopleInput;
	private JButton peopleInputButton;

	// ---------------------全局变量------------------------------------
	private boolean armed;
	private Image simulatorImage;
	private int placesSum;

	// --------------------单线模拟传递变量----------------------------
	private LatLon findSinglePathBegin;
	private LatLon findSinglePathEnd;
	private Simulator simulator;
	private TimerTask simulatorsTimerTask;
	private Timer periodTimer;

    // --------------------其他----------------------------------------
	private int taskBeginDelay = 1000;			//模拟点时计划任务开始时延迟
	private int taskPeriod = 2000;				//模拟点时计划任务的间隔
	private static final Angle anglePitch=Angle.fromDegrees(30);
	public BasicFlyView view;
	public Position viewPosition;
	public AlarmIcons ai;						//警报图标

	public LeftControlPanel(WorldWindow wwdObject, MeasureTool measureToolObject) {
		super(new BorderLayout());
		this.wwd = wwdObject;
		this.measureTool = measureToolObject;
		annotationLayer = new RenderableLayer();
		//为模拟点载入图标。
		try {
			simulatorImage = ImageIO.read(new File(
					"src/cn/yecols/images/simulator.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		placesDataLayerUtil = new PlacesDataLayerUtil();
		placesSum = placesDataLayerUtil.getSum();

		//创建左面板，主要是控件的布局。
		this.MakePanel(new Dimension(200, 300));

		// 添加鼠标事件监听器，选择起点。
		this.wwd.getInputHandler().addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent mouseEvent) {
				if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {

					InfoE("在右边地图中点击选择起点。");
					UpdatePositon();
					armed = false;
					startPosition.setEnabled(true);
					mouseEvent.consume();
					InfoE("请从下拉框中选择目的地。");

				}
			}
		});
		
		

		// 添加鼠标事件监听器，显示标注框。
		this.wwd.addSelectListener(new SelectListener() {
			public void selected(SelectEvent event) {
				if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)
						&& event.getTopObject() instanceof Place) {
					DisplayAnnotation(placesDataLayerUtil
							.MakePlaceAnnotation((Place) event.getTopObject()));

				}
				if (attachedAnnotationLayer==true&&event.getEventAction().equals(SelectEvent.LEFT_CLICK)
						&& !(event.getTopObject() instanceof Simulator)&& !(event.getTopObject() instanceof Place)) {
					annotationLayer.removeAllRenderables();
					wwd.getModel().getLayers().remove(annotationLayer);
					attachedAnnotationLayer=false;
				}
				
					
			}

		});

	}

	private void UpdatePositon() {
		//更新地点信息
		Position curPos = this.wwd.getCurrentPosition();
		findSinglePathBegin = curPos.getLatLon();
		InfoA("起点已选择，起点坐标为：\n" + findSinglePathBegin.toString());
	}

	private void DisplayAnnotation(Renderable r) {
		//在地图上显示景点标注
		annotationLayer.removeAllRenderables();
		annotationLayer.addRenderable(r);
		wwd.getModel().getLayers().remove(annotationLayer);
		wwd.getModel().getLayers().add(annotationLayer);
		this.attachedAnnotationLayer=true;
	}

	private void MakePanel(Dimension dimension) {
		//创建左边面板控件
		
		//---------------------信息显示面板-----------------------
		infomation = new JTextArea("请先点击初始化。");
		infomation.setWrapStyleWord(true);
		JPanel dummyPanel = new JPanel(new BorderLayout());
		dummyPanel.add(this.infomation, BorderLayout.NORTH);
		this.scrollPane = new JScrollPane(dummyPanel);
		this.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        infoPanel = new JPanel(new GridLayout(0, 1, 0, 10));
		infoPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(
				9, 9, 9, 9), new TitledBorder("信息面板")));
		infoPanel.setToolTipText("信息显示");
		infoPanel.add(scrollPane);
		
		//---------------------图层控制面板-----------------------
		layerPanel=new LayerPanel(wwd);
		layerPanel.setMaximumSize(new Dimension(200, 200));
		layerPanel.setPreferredSize(new Dimension(200, 200));
		
		
		//---------------------人群数量模拟控件-------------------
		peopleInput=new JTextField();
		peopleInputButton=new JButton("人群大约数量");
		peopleInputButton.addActionListener(new ActionListener(){
			//为按钮加入响应事件
			public void actionPerformed(ActionEvent arg0) {
				int peopleSum=Integer.parseInt(peopleInput.getText());
				for(Layer l:wwd.getModel().getLayers()){
					if(l.getName().equals("Emergency")){
						wwd.getModel().getLayers().remove(l);
					}
				}
				if(peopleSum<=Emergency.busOnlySum){
					wwd.getModel().getLayers().add(ManyDataLayerUtil.Emergency(peopleSum));
					if(ai!=null){
						ai=null;
						for(Layer l:wwd.getModel().getLayers()){
							if(l.getName().equals("alarm icon"))
								wwd.getModel().getLayers().remove(l);
						}
					}
					InfoE("当前人流可通过公交疏散");
				}
				else if(peopleSum<=Emergency.allSum){
					wwd.getModel().getLayers().add(ManyDataLayerUtil.Emergency(peopleSum));
					if(ai!=null){
						ai=null;
						for(Layer l:wwd.getModel().getLayers()){
							if(l.getName().equals("alarm icon"))
								wwd.getModel().getLayers().remove(l);
						}
					}
					InfoE("当前人流密度过大，需要添加临时送客点疏散");
				}
				else if(peopleSum>Emergency.alarmSum){
					wwd.getModel().getLayers().add(ManyDataLayerUtil.Emergency(peopleSum));
					ai=new AlarmIcons(wwd);					
					InfoE("当前人流密度超过警戒阈值，需要采取预警措施");
				}
				layerPanel.update(wwd);
			}});
		
        //----------------------数据初始化面板-------------------------
		JPanel initialPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		JPanel singlePanel = new JPanel(new GridLayout(1, 3, 5, 5));
		loadStreetsData = new JButton("数据初始化");
		loadStreetsData.addActionListener(new ActionListener() {
			//点击初始化时，重建道路网
			public void actionPerformed(ActionEvent event) {
				//System.out.println("loading streets data");
				StreetsDataReader streetsDataReader = new StreetsDataReader(wwd);
				try {
					g = streetsDataReader.ReadStreetsData();
				} catch (Exception e) {
					e.printStackTrace();
					InfoE("Failed to Read xml or build a weighted graph.");
				}
				ManyDataLayerUtil.LoadStreetsDataLayer(g.edgeSet(), wwd);
				ManyDataLayerUtil.LoadAirSpaceLayer(wwd);
				InfoE("初始化完成");
				layerPanel.update(wwd);
			}
		});

		showPlaces = new JButton("显示景点");
		showPlaces.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// 通过辅助类标注景点
				try {
					placesDataLayerUtil.MakePlacesLayerWithMarker();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				placesDataLayerUtil.Visualize(wwd);
				layerPanel.update(wwd);
			}
		});

		//-------------------最短路径控件-----------------------------
		startPosition = new JButton("选择起点");
		startPosition.addActionListener(new ActionListener() {
			//从图中选择起点
			public void actionPerformed(ActionEvent event) {
				armed = true;
				startPosition.setEnabled(false);
			}
		});

		endPosition = new JComboBox();
		endPosition.setEditable(false);
		for (Place p : placesDataLayerUtil.getPlaces()) {
			endPosition.addItem(p);
		}
		endPosition.addActionListener(new ActionListener() {
			//从下拉框中获得终点
			public void actionPerformed(ActionEvent event) {
				findSinglePathEnd = ((Place) endPosition.getSelectedItem())
						.getPlaceLocation();
			}
		});

		computeShortestPath = new JButton("路径");
		computeShortestPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (findSinglePathBegin != null && findSinglePathEnd != null) {
					//计算最短路径
					simulator = new Simulator(findSinglePathBegin,
							findSinglePathEnd, g, simulatorImage);
					//加载路径到图上
					ManyDataLayerUtil.LoadSimulatorPath(simulator, wwd);
					//显示时间信息
					InfoA(String.format("该路径距离约%.2f米\n步行约%.1f分钟", simulator
							.getMiles(), simulator.getMiles() / 90));
				} else
					InfoE("请先选择起点和目的地");
				layerPanel.update(wwd);

			}
		});

		//----------------------------沿路径行走控件--------------------------------
		simulateTourist = new JButton("沿路径行走");
		simulateTourist.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				final ArrayList<Simulator> simulators = new ArrayList<Simulator>();
				//模拟对象
					simulators.add(simulator);
				//清空原有任务
				if(periodTimer!=null){
					periodTimer.cancel();
				   
				}
				periodTimer = null;
				simulatorsTimerTask = null;
				//进行模拟
				try {
					periodTimer = new Timer();
					simulatorsTimerTask = (TimerTask) new SimulatorsTimerTask(
							wwd, simulators);
				} catch (IOException e) {
					e.printStackTrace();
				}
				periodTimer.scheduleAtFixedRate(simulatorsTimerTask,
						taskBeginDelay, taskPeriod);
				layerPanel.update(wwd);
			}
		});
		
		streetsTourist = new JButton("街景");
		streetsTourist.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				
				ArrayList<LatLon> pathPoints=new ArrayList<LatLon>();
				for(DefaultWeightedEdge e:simulator.getEdges())
					pathPoints.add((LatLon) e.getSource());
				pathPoints.add(simulator.getEnd());
				
				viewPosition=wwd.getView().getEyePosition();
				view=new BasicFlyView();
				wwd.setView(view);
				
				wwd.getView().setEyePosition(new Position(simulator.getCurrent(),0));
				System.out.println(wwd.getView().getEyePosition().toString());
				for(LatLon ll:pathPoints){
					System.out.println(ll.toString());
				}
				
				for(LatLon ll:pathPoints){
					System.out.println(ll.toString());
				}
				
				for(int i=1;i<pathPoints.size();i++){
					moveToLocation(pathPoints.get(i-1),pathPoints.get(i));
					System.out.println("yecolsStep"+i+"    "+pathPoints.get(i).toString());
				}
			}
		});

		//--------------------------停止模拟--------------------------------
		simulateTourists = new JButton("停止行走");
		simulateTourists.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				
				if(periodTimer!=null){
					periodTimer.cancel();
				   
				}
				periodTimer = null;
				simulatorsTimerTask = null;
				wwd.getView().setHeading(Angle.fromDegrees(0));
				wwd.getView().setPitch(Angle.fromDegrees(0));
			    wwd.redraw();
			    layerPanel.update(wwd);
				}
				
		});

		//-----------------------第一面板 NORTH--------------------------------------
		JPanel outPanel = new JPanel(new GridLayout(3, 1, 5, 5));

		initialPanel.add(loadStreetsData);
		initialPanel.add(showPlaces);
		outPanel.add(initialPanel);
		outPanel.setToolTipText("控制面板");
		
		singlePanel.add(startPosition);
		singlePanel.add(endPosition);
		singlePanel.add(computeShortestPath);
		outPanel.add(singlePanel);
        
		JPanel simuPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		simuPanel.add(simulateTourist);
		simuPanel.add(simulateTourists);
		outPanel.add(simuPanel);
		
		JPanel outerPanel = new JPanel(new BorderLayout());
		outerPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(
				9, 9, 9, 9), new TitledBorder("控制面板")));
		outerPanel.setToolTipText("控制信息");
		outerPanel.add(outPanel,BorderLayout.CENTER);
		outerPanel.add(scrollPane,BorderLayout.SOUTH);
		this.add(outerPanel,BorderLayout.NORTH);
		
		
		infoPanel.setMaximumSize(new Dimension(200, 200));
		infoPanel.setPreferredSize(new Dimension(200, 200));
		
		
		//--------------------------第二面板 CENTER--------------------------------
		this.add(layerPanel,BorderLayout.CENTER);
		
		
		//------------------------第三面板SOUTH------------------------------------		
		JPanel emerPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		emerPanel.add(peopleInput);
		emerPanel.add(peopleInputButton);
		
		//-------------------------监控面板---------------------------------------
		monitorPanel = new JPanel(new BorderLayout());
		monitorPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(
				9, 9, 9, 9), new TitledBorder("监控面板")));
		monitorPanel.setToolTipText("监控显示");
		
		
		VideoMoniter video = new VideoMoniter();
		monitorPanel.add(video,BorderLayout.CENTER);
		emerPanel.setBorder(BorderFactory.createEmptyBorder(
				3, 3, 3, 3));
		monitorPanel.add(emerPanel,BorderLayout.SOUTH);
		
		monitorPanel.setMaximumSize(new Dimension(200, 300));
		monitorPanel.setPreferredSize(new Dimension(200, 300));
		this.add(monitorPanel, BorderLayout.SOUTH);
		video.play();

	}

	private void InfoA(String info) {
		//信息面板添加信息
		this.infomation.append("\n" + info);
		this.scrollPane.revalidate();
		this.infoPanel.repaint();
	}
	private void InfoE(String info) {
		//信息面板重写信息
		this.infomation.setText(info);
		this.scrollPane.revalidate();
		this.infoPanel.repaint();
	}
	void moveToLocation(LatLon ll1, LatLon ll2)
	 {
         if (ll1 == null)
         {
             return;
         }
         viewPosition=new Position(ll1,15000);
         double elevation = 9000;
         FlyToFlyViewAnimator animator =
              FlyToFlyViewAnimator.createFlyToFlyViewAnimator(view,
                  new Position(ll1, elevation),
                  new Position(ll2, elevation),
                  view.getHeading(), LatLon.greatCircleAzimuth(ll1, ll2),
                  anglePitch, anglePitch,
                  5000, 5000,
                         5000, true);
         view.addAnimator(animator);
         animator.start();
         view.firePropertyChange(AVKey.VIEW, null, view);
     }
    

}
