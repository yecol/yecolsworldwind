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
	private final WorldWindow wwd;
	private final MeasureTool measureTool;
	private BasicOrbitView bview;
	private WeightedGraph<LatLon, DefaultWeightedEdge> g;

	// ---------------------����������----------------------------------
	private PlacesDataLayerUtil placesDataLayerUtil;

	// ----------------------------------------------------------------
	private RenderableLayer annotationLayer;
	private boolean attachedAnnotationLayer=false;

	// ---------------------����ؼ�------------------------------------
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

	// ---------------------ȫ�ֱ���------------------------------------
	private boolean armed;
	private Image simulatorImage;
	private int placesSum;

	// --------------------����ģ�⴫�ݱ���----------------------------
	private LatLon findSinglePathBegin;
	private LatLon findSinglePathEnd;
	private Simulator simulator;
	private TimerTask simulatorsTimerTask;
	private Timer periodTimer;

	// ---------------------------------------------------
	private int taskScale = 3;
	private int taskBeginDelay = 1000;
	private int taskPeriod = 2000;
	private static final Angle anglePitch=Angle.fromDegrees(30);
	public BasicFlyView view;
	public Position viewPosition;

	public LeftControlPanel(WorldWindow wwdObject, MeasureTool measureToolObject) {
		super(new BorderLayout());
		this.wwd = wwdObject;
		this.measureTool = measureToolObject;
		annotationLayer = new RenderableLayer();
		try {
			simulatorImage = ImageIO.read(new File(
					"src/cn/yecols/images/simulator.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		placesDataLayerUtil = new PlacesDataLayerUtil();
		placesSum = placesDataLayerUtil.getSum();

		this.MakePanel(new Dimension(200, 300));

		// �������¼���������ѡ����㡣
		this.wwd.getInputHandler().addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent mouseEvent) {
				if (armed && mouseEvent.getButton() == MouseEvent.BUTTON1) {

					InfoE("���ұߵ�ͼ�е��ѡ����㡣");
					UpdatePositon();
					armed = false;
					startPosition.setEnabled(true);
					mouseEvent.consume();
					InfoE("�����������ѡ��Ŀ�ĵء�");

				}
			}
		});
		
		

		// �������¼�����������ʾ��ע��
		this.wwd.addSelectListener(new SelectListener() {
			public void selected(SelectEvent event) {
				if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)
						&& event.getTopObject() instanceof Place) {
					DisplayAnnotation(placesDataLayerUtil
							.MakePlaceAnnotation((Place) event.getTopObject()));

				}
				if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)
						&& event.getTopObject() instanceof Stop) {
					DisplayAnnotation(Emergency
							.MakeStopAnnotation((Stop) event.getTopObject()));
					System.out.println("stop");

				}
				if (event.getEventAction().equals(SelectEvent.LEFT_CLICK)
						&& event.getTopObject() instanceof Simulator) {
					System.out.println("OK");
				}
				if (attachedAnnotationLayer==true&&event.getEventAction().equals(SelectEvent.LEFT_CLICK)
						&& !(event.getTopObject() instanceof Simulator)&& !(event.getTopObject() instanceof Place)) {
					annotationLayer.removeAllRenderables();
					wwd.getModel().getLayers().remove(annotationLayer);
					attachedAnnotationLayer=false;
					System.out.println("yecol");
				}
				
					
			}

		});

	}

	private void UpdatePositon() {
		//ˢ�������Ϣ���
		Position curPos = this.wwd.getCurrentPosition();
		findSinglePathBegin = curPos.getLatLon();
		InfoA("�����ѡ���������Ϊ��\n" + findSinglePathBegin.toString());
	}

	private void DisplayAnnotation(Renderable r) {
		//�ڵ�ͼ����ʾ�����ע
		annotationLayer.removeAllRenderables();
		annotationLayer.addRenderable(r);
		wwd.getModel().getLayers().remove(annotationLayer);
		wwd.getModel().getLayers().add(annotationLayer);
		this.attachedAnnotationLayer=true;
	}

	private void MakePanel(Dimension dimension) {

		infomation = new JTextArea("���ȵ����ʼ����");
		infomation.setWrapStyleWord(true);
		JPanel dummyPanel = new JPanel(new BorderLayout());
		dummyPanel.add(this.infomation, BorderLayout.NORTH);

		// Put the name panel in a scroll bar.
		this.scrollPane = new JScrollPane(dummyPanel);
		this.scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		infoPanel = new JPanel(new GridLayout(0, 1, 0, 10));
		infoPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(
				9, 9, 9, 9), new TitledBorder("��Ϣ���")));
		infoPanel.setToolTipText("��Ϣ��ʾ");
		infoPanel.add(scrollPane);
		
		layerPanel=new LayerPanel(wwd);
		layerPanel.setMaximumSize(new Dimension(200, 200));
		layerPanel.setPreferredSize(new Dimension(200, 200));
		
		
		
		peopleInput=new JTextField();
		peopleInputButton=new JButton("��Ⱥ��Լ����");
		peopleInputButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				int peopleSum=Integer.parseInt(peopleInput.getText());
				for(Layer l:wwd.getModel().getLayers()){
					if(l.getName().equals("Emergency")){
						wwd.getModel().getLayers().remove(l);
					}
				}
				if(peopleSum<=Emergency.busOnlySum){
					wwd.getModel().getLayers().add(ManyDataLayerUtil.Emergency(peopleSum));
					InfoE("��ǰ������ͨ��������ɢ");
				}
				else if(peopleSum<=Emergency.allSum){
					wwd.getModel().getLayers().add(ManyDataLayerUtil.Emergency(peopleSum));
					InfoE("��ǰ�����ܶȹ�����Ҫ�����ʱ�Ϳ͵���ɢ");
				}
				else if(peopleSum>Emergency.alarmSum){
					wwd.getModel().getLayers().add(ManyDataLayerUtil.Emergency(peopleSum));
					InfoE("��ǰ�����ܶȳ���������ֵ����Ҫ��������ʱ�Ϳ͵�͹�����ʿ������ɢ");
				}
				layerPanel.update(wwd);
			}});
		

		JPanel initialPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		JPanel singlePanel = new JPanel(new GridLayout(1, 3, 5, 5));
		// TODO Auto-generated method stub
		loadStreetsData = new JButton("���ݳ�ʼ��");
		loadStreetsData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.out.println("loading streets data");
				StreetsDataReader streetsDataReader = new StreetsDataReader(wwd);
				try {
					g = streetsDataReader.ReadStreetsData();
					System.out.println(g.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					InfoE("Failed to Read xml or build a weighted graph.");
				}
				ManyDataLayerUtil.LoadStreetsDataLayer(g.edgeSet(), wwd);
				ManyDataLayerUtil.LoadAirSpaceLayer(wwd);
				InfoE("��ʼ�����");
				layerPanel.update(wwd);
			}

		});

		showPlaces = new JButton("��ʾ����");
		showPlaces.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// ��ע����
				try {
					placesDataLayerUtil.MakePlacesLayerWithMarker();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				placesDataLayerUtil.Visualize(wwd);

			

				LayerList layers = wwd.getModel().getLayers();
				for (Layer l : layers) {
					System.out.println(l.getName());
				}
				layerPanel.update(wwd);

			}
		});

		startPosition = new JButton("ѡ�����");
		startPosition.addActionListener(new ActionListener() {
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
			public void actionPerformed(ActionEvent event) {
				findSinglePathEnd = ((Place) endPosition.getSelectedItem())
						.getPlaceLocation();
			}
		});

		computeShortestPath = new JButton("·��");
		computeShortestPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (findSinglePathBegin != null && findSinglePathEnd != null) {
					simulator = new Simulator(findSinglePathBegin,
							findSinglePathEnd, g, simulatorImage);
					System.out.println(simulator.getCurrent().toString());
					ManyDataLayerUtil.LoadSimulatorPath(simulator, wwd);
					InfoA(String.format("��·������Լ%.2f��\n����Լ%.1f����", simulator
							.getMiles(), simulator.getMiles() / 90));
				} else
					InfoE("����ѡ������Ŀ�ĵ�");
				layerPanel.update(wwd);

			}
		});

		simulateTourist = new JButton("��·������");
		simulateTourist.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				final ArrayList<Simulator> simulators = new ArrayList<Simulator>();
				
					simulators.add(simulator);

				if(periodTimer!=null){
					periodTimer.cancel();
				   
				}
				 periodTimer = null;
				simulatorsTimerTask = null;
				try {
					periodTimer = new Timer();
					simulatorsTimerTask = (TimerTask) new SimulatorsTimerTask(
							wwd, simulators);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				periodTimer.scheduleAtFixedRate(simulatorsTimerTask,
						taskBeginDelay, taskPeriod);
				layerPanel.update(wwd);

			}
		});
		
		streetsTourist = new JButton("�־�");
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
				//moveToLocation(new LatLon(20,-140));
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

		simulateTourists = new JButton("ֹͣ����");
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

		JPanel outPanel = new JPanel(new GridLayout(3, 1, 5, 5));

		initialPanel.add(loadStreetsData);
		initialPanel.add(showPlaces);
		outPanel.add(initialPanel);
		outPanel.setToolTipText("�������");

		this.add(outPanel, BorderLayout.NORTH);
		singlePanel.add(startPosition);
		singlePanel.add(endPosition);
		singlePanel.add(computeShortestPath);

		outPanel.add(singlePanel);
        
		JPanel simuPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		simuPanel.add(simulateTourist);
		simuPanel.add(simulateTourists);
		outPanel.add(simuPanel);
		
		JPanel emerPanel = new JPanel(new GridLayout(1, 2, 5, 5));
		emerPanel.add(peopleInput);
		emerPanel.add(peopleInputButton);
		
		
		
		JPanel outerPanel = new JPanel(new BorderLayout());
		outerPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(
				9, 9, 9, 9), new TitledBorder("�������")));
		outerPanel.setToolTipText("������Ϣ");
		outerPanel.add(outPanel,BorderLayout.CENTER);
		outerPanel.add(scrollPane,BorderLayout.SOUTH);
		this.add(outerPanel,BorderLayout.NORTH);
		
		
		infoPanel.setMaximumSize(new Dimension(200, 200));
		infoPanel.setPreferredSize(new Dimension(200, 200));
		this.add(layerPanel,BorderLayout.CENTER);
		
		
		//-------------------------------monitor begin
		monitorPanel = new JPanel(new BorderLayout());
		monitorPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(
				9, 9, 9, 9), new TitledBorder("������")));
		monitorPanel.setToolTipText("�����ʾ");
		
		
		VideoMoniter video = new VideoMoniter();
		monitorPanel.add(video,BorderLayout.CENTER);
		//monitorPanel.set
		emerPanel.setBorder(BorderFactory.createEmptyBorder(
				3, 3, 3, 3));
		monitorPanel.add(emerPanel,BorderLayout.SOUTH);
		
		monitorPanel.setMaximumSize(new Dimension(200, 300));
		monitorPanel.setPreferredSize(new Dimension(200, 300));
		this.add(monitorPanel, BorderLayout.SOUTH);
		video.play();
		//------------------------------monitor end

	}

	private void InfoA(String info) {
		this.infomation.append("\n" + info);
		this.scrollPane.revalidate();
		this.infoPanel.repaint();
	}
	private void InfoE(String info) {
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
