package cn.yecols.obj;

import java.awt.Image;
import java.util.ArrayList;

import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.render.SurfaceIcon;

//模拟点类，继承自表面图标，可直接在球面上渲染。
public class Simulator extends SurfaceIcon{
	private LatLon begin;							//起始点（随机点）
	private LatLon pathBegin;						//起始点（距离随机点最近的图顶点）
	private LatLon temp;							//
	private LatLon current;							//当前点
	private LatLon end;								//终点
	private Angle heading;							//当前角度（与正北的角度差）
	private DefaultWeightedEdge firstEdge;			//首边
	private ArrayList<DefaultWeightedEdge> edges;	//行走边数组
	private int edgeIndex, factor;					//行走边数组当前计数和当前行进段位权重
	private double miles;							//路径长度，单位米

	
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
		//根据起始点找到最近的图上顶点
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
		//计算。每次计时调用该函数得到当前一步该点所在位置。
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
