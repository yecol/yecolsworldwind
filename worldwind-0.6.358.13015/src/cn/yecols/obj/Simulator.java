package cn.yecols.obj;

import java.awt.Image;
import java.util.ArrayList;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.SurfaceIcon;

//ģ����࣬�̳��Ա���ͼ�꣬��ֱ������������Ⱦ��
public class Simulator extends SurfaceIcon{
	private LatLon begin;							//��ʼ�㣨����㣩
	private LatLon pathBegin;						//��ʼ�㣨��������������ͼ���㣩
	private LatLon temp;							//
	private LatLon current;							//��ǰ��
	private LatLon end;								//�յ�
	private Angle heading;							//��ǰ�Ƕȣ��������ĽǶȲ
	private DefaultWeightedEdge firstEdge;			//�ױ�
	private ArrayList<DefaultWeightedEdge> edges;	//���߱�����
	private int edgeIndex, factor;					//���߱����鵱ǰ�����͵�ǰ�н���λȨ��
	private double miles;							//·�����ȣ���λ��

	
	/*----------------------------------Constructors-------------------------------------------------------------*/
	public Simulator(LatLon begin,LatLon end,WeightedGraph<LatLon, DefaultWeightedEdge> g,Image imageSource){		
		super(imageSource,begin);
		pathBegin=FindNearest(begin,g);
		DijkstraShortestPath<LatLon,DefaultWeightedEdge> shortestPath = new DijkstraShortestPath(
				g, pathBegin, end);
		edges=(ArrayList<DefaultWeightedEdge>) shortestPath.getPathEdgeList();
		firstEdge=new DefaultWeightedEdge();
		firstEdge.setSource(begin);
		firstEdge.setTarget(pathBegin);
		edges.add(0, firstEdge);
		edgeIndex = 0;
		factor = 1;
		this.current=begin;
		this.end=end;
		miles=0;
	}
	
	/*----------------------------------Functions------------------------------------------------------------*/
	
	public LatLon FindNearest(LatLon begin,WeightedGraph<LatLon, DefaultWeightedEdge> g){
		//������ʼ���ҵ������ͼ�϶���
		LatLon nearest = null;
		double delta=1000.00;
		for(LatLon ll:g.vertexSet()){
			double tempDelta=Math.abs(begin.getLatitude().degrees-ll.getLatitude().degrees)+Math.abs(begin.getLongitude().degrees-ll.getLongitude().degrees);
			if(tempDelta<delta){
				nearest=ll;
				delta=tempDelta;
			}
		}
		if(nearest==null)
			System.out.println("Failed to find nearest LatLon");
		return nearest;
	}

	public void compute() {
		//���㡣ÿ�μ�ʱ���øú����õ���ǰһ���õ�����λ�á�
		if(edgeIndex < edges.size()) {
			DefaultWeightedEdge currentEdge = edges.get(edgeIndex);
			temp = (LatLon) currentEdge.getSource();
			end = (LatLon) currentEdge.getTarget();
			heading=LatLon.greatCircleAzimuth(temp, end);
			if (LatLon.equals4bits(current, end)) {
				factor = 1;
				edgeIndex += 1;
			}
			else{
				current = LatLon.interpolate(200 / currentEdge.getWeight() * factor,
						current, end);
				this.setLocation(current);
				factor += 1;	
			}
		}
	}
	
	/*---------------------------------Getter & Setter------------------------------------------------------------*/
	
	public Angle getHeading(){
		return this.heading;
	}
	
	public LatLon getBegin(){
		return this.begin;
	}
	
	public LatLon getEnd(){
		return this.end;
	}
	
	
	public LatLon getCurrent(){
		return this.current;
	}
	
	public DefaultWeightedEdge getCurrentEdge(){
		return edges.get(edgeIndex);
	}
	
	public DefaultWeightedEdge getLastEdge(){
		if(edgeIndex>0)
		    return edges.get(edgeIndex-1);
		else 
			return null;
	}
	
	public ArrayList<DefaultWeightedEdge> getEdges(){
		return edges;
	}
	
	public void computeMiles(){
		for(int i=1;i<edges.size();i++)
			miles+=edges.get(i).getWeight();
	}
	
	public Double getMiles(){
		computeMiles();
		return miles;
	}
	
	public String toString(){
		return " end: "+end.toString()+" cur: "+current.toString();
	}

}
