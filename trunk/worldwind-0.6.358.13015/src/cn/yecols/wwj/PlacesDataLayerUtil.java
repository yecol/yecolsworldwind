package cn.yecols.wwj;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import cn.yecols.obj.Place;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Annotation;
import gov.nasa.worldwind.render.AnnotationShadow;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.ScreenImage;
import gov.nasa.worldwind.render.SurfaceIcon;

public class PlacesDataLayerUtil {
	//�����ʾͼ��
	private ArrayList<Place> places;
	private RenderableLayer placesDataLayer;
	private static Image placeImage;
	
	public PlacesDataLayerUtil(){
		places=new ArrayList<Place>();
		placesDataLayer=new RenderableLayer();
		placesDataLayer.setName("Places");
		try {
			placeImage=ImageIO.read(new File("src/cn/yecols/images/place.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Initiation();
	}
	
	//��ʼ����������
	public void Initiation(){
		places.add(new Place("��Ϫ��÷", new LatLon(30.2633, 120.0785), "��Ϫʪ�ع��ҹ�԰��λ���㽭ʡ��������<br>����������������5����Ǻ����ĳ���<br>����ʪ�ء�������̬��Դ�ḻ����Ȼ����<br>���ӡ��Ļ��������������������������<br>���ݡ�����������Ŀǰ���ڵ�һ��Ҳ��Ψ<br>һ�ļ�����ʪ�ء�ũ��ʪ�ء��Ļ�ʪ����<br>һ��Ĺ���ʪ�ع�԰��",placeImage));
		places.add(new Place("�յ̴���", new LatLon(30.2390, 120.1356), "����Ԫ�v����(1090)��ʫ������(����)��<br>����֪��ʱ���迣���������ÿ��ڵ���<br>�๹���������������������ݱ���γɵģ�<br>��������Ϊ�����ն������������Ĺ�����<br>��������Ϊ�յ̡�",placeImage));
		places.add(new Place("���Ų�ѩ", new LatLon(30.2606, 120.1480), "����λ�ں����������׵̵Ķ��ˣ�������<br>ʯɽ�������ݳǣ�������ͱ�����ķ�<br>ˮ�㡣�����ƽϸ���Ұ�������Ƕ������<br>����ѩ�������ȥ����",placeImage));
		places.add(new Place("�׷�Ϧ��", new LatLon(30.2367, 120.1457), "�׷����������(975)������Խ����Ǯ��<br>�mΪ��ף�������Ӷ�������������������<br>���ڵ�ʱ�������⣬���ֳ�Ϊ����ש����<br>ԭ�⽨ʮ���㣬����������ޣ�ֻ������㡣<br>�����ξ�ʱ���������֣�����������<br>�������ݻ�������������ġ�",placeImage));
		places.add(new Place("��������", new LatLon(30.2320, 120.1460), "����ɽ�Ǿ���ɽ�ķ�֧����ɽɽ�����㣬<br>��ʯ���磬��ں��£��������ϡ����<br>������֮�ϣ���ʯ�������ϣ���������ɽ��<br> ����ɽ�����Ӻ���������ϰ���ɽ�߲���<br>���ף�ɽ������ȴ����ǧ���ס�",placeImage));
		places.add(new Place("������", new LatLon(30.2118, 120.1487),   "��������ַ�������λʼҼ������ַ����<br>���ǹŴ��й���ũΪ����ũ���Ļ�����<br>Ӱ���ǹŴ��ۻ�ͨ����ʥ��ʽ���ũҵ��<br>���������ӵĳ�����",placeImage));
		places.add(new Place("������", new LatLon(30.2008, 120.1315),   "��������������Դ�ڷ�̵ġ����;�������<br>ʱ�����Ŀ����������ѹǮ�����Ľ�����<br>����59.89�ף��佨����ǳ����أ���<br>�ڲ�שʯ�ṹ���߲㣬�ⲿľ�ṹΪ8��13<br>�㡣",placeImage));
		places.add(new Place("������ݺ", new LatLon(30.2405, 120.1497), "����ʮ��֮��λ���������ϰ����岨�Ŵ�<br>�Ĵ��͹�԰�������ꡢ��ݺ���۾�����<br>԰�ĸ����������Գ�������骡�ѩ�ɡ�����<br>����÷������ľ������",placeImage));
		places.add(new Place("��ʯ��ϼ", new LatLon(30.2613, 120.1410), "���������ĸ��롢��ʯɽ�Գ�һ�壬��ɫ<br>���ء������ɽ�ҳ�����ɫ����������<br>������������ĺ�ɫ�����ʣ�ÿ������ӳ�գ�<br>��ɽ��ϼ�ͷף������ǳ���������ϼ��<br>����֮ʱ�����m���㣬����ϼ�����ҫ<br>Ŀ���·�������ı�ʯ���������ԡ����<br>����Ϊ����ʯ��ϼ����",placeImage));
		places.add(new Place("�����´�", new LatLon(30.2642, 120.1286), "�ں�����ϼ�뱱´�����·������������<br>��ַ����˳ɽ·������ï���������<br>�Ϳɿ��������ŵ��̶��츣�صĻ������ż���<br>��������������Ϊ���������������֮<br>һ������ʢ�����庼�ݶ�ʮ�ľ����С�<br>��ɽ���䡷һ����ָ�ˡ�",placeImage));
		places.add(new Place("�����Ļ��㳡", new LatLon(30.2750, 120.1576), "�����Ļ��㳡λ�����ֹ㳡�˺ӱ��࣬<br>�ش����������ģ���������2�����<br>���Ļ��㳡ռ��13.3���ꡣ�����Ļ��㳡��<br>�������35��ƽ���ף������Ļ��㳡��<br>��㳡Լ10��ƽ���ס������Ļ��㳡��<br>��¥��41��170�׸ߵ��㽭�������ġ�",placeImage));
		places.add(new Place("��ɽ��", new LatLon(30.2774, 120.1862), "λ���㽭ʡ���ݾɳǵ�����ƫ������Ԫ������<br>������Ǳ��ұ�����ȴΪ˿��ҵ����<br>֮�ء��Դ�Ϊ�������ݵĹ�ҵ�������󶼳���<br>�˵ء�",placeImage));
		places.add(new Place("��ʷ���", new LatLon(30.2291, 120.1545), "���ɽλ������֮�ϣ�����������Ǯ����֮<br>�䣬���ζ�����ʮ���ף����ʱ��Խ<br>��Ǯ��ӭ���ݣ����������������·������ڴ�<br>���ʼ�С�����ɽ��֮�ơ�",placeImage));
		places.add(new Place("������Ȫ", new LatLon(30.2201, 120.1456), "������Ȫλ������֮�ϣ����ɽ���������ڡ�<br>����֮��������Ȫ��������˵�ƴ�<br>��ɮ�Կ�ס�����������ˮԴ��ȱ��׼��Ǩ<br>�ߡ���һ�죬�������еõ����ָʾ��<br>������ɽ��ͯ��Ȫ����ǲ������������<br>����������ܴ�����Ѩ��ʯ��ӿ��Ȫˮ��<br>������Ȫ�ɴ˵�����",placeImage));
		places.add(new Place("��Ϫ����", new LatLon(30.2085, 120.1216), "��Ϫ����λ���㽭ʡ�����е�����������<br>��֮���ļ������£��䷢Դ�ж���һ��<br>����ʨ�ӷ壬һ���̼�ɽ����÷�룬;�л��<br>�����塢�귨����ͷ�����ҡ���ʯ����<br>�ܡ����ɡ��Ƽҡ�С������֮ˮ��һ·<br>�ϴ�Խ��ɽ��ȣ��ֻ㼯������ϸ������<br>����������������7������Ǯ<br>�������ֳƾ�Ϫʮ�˽���",placeImage));
		places.add(new Place("������", new LatLon(30.1967, 120.1107), "������λ������֮���ϣ�Ǯ����������<br>����ɽ����������ڵ����������⣬<br>����ɽ�ϵ�������ƣ����ɼ�������������<br>���ò�ɢ���ơ����ܡ���",placeImage));
		places.add(new Place("���ݴ��Ժ", new LatLon(30.2569, 120.2101), "���ݴ��Ժ������صĽ������͡�����<br>���ݳ����ܡ��Ƚ�����̨������ΪĿǰ<br>�й�����ִ����ı�־���Ļ���ʩ��",placeImage));
		places.add(new Place("���͹�԰", new LatLon(30.2838, 120.1722), "λ�ڳ���·�ڣ���������һ���ľ������ڡ�<br>",placeImage));
		places.add(new Place("������", new LatLon(30.2293, 120.0904), "�ڷ������뱱�߷�֮������ɽ´�У�����Ю<br>�ţ���ľ���㣬��ɽ���£�������״��<br>��һ�������ľ�����ɫ���˵�����ʤ�ء�",placeImage));
		
	}
	
	//��ʾ����ͼ��
	public void Visualize(WorldWindow wwd){
		wwd.getModel().getLayers().add(placesDataLayer);
		
	}
	
	public Place getRandomPlace(){
		int index=(int)(Math.random()*places.size());
		return places.get(index);
	}
	
	
	public RenderableLayer MakePlacesLayerWithAnnnotation(){
		placesDataLayer.removeAllRenderables();
		for(Place p:places){
			placesDataLayer.addRenderable(MakePlaceAnnotation(p));
		}
		return placesDataLayer;
	}
	
	//Ϊ���о�����ӱ�ע
	public RenderableLayer MakePlacesLayerWithMarker() throws IOException{
		placesDataLayer.removeAllRenderables();
		for(Place p:places){
			placesDataLayer.addRenderable(p);
		}
		System.out.println(places.size());
		return placesDataLayer;
	}
	
	
	//��ӱ�ע
	public Renderable MakePlaceMarker(Place p) throws IOException {
		// TODO Auto-generated method stub
		final SurfaceIcon surfaceIcon = new SurfaceIcon(placeImage,p.getPlaceLocation());
		return surfaceIcon;		
	}

	public RenderableLayer getPlacesDataLayer() {
		return placesDataLayer;
	}
	
	public ArrayList<Place> getPlaces(){
		return places;
	}


	//���Ա�ע����ʾ
	public GlobeAnnotation MakePlaceAnnotation(Place place) {
		GlobeAnnotation ga = new AnnotationShadow(
				"<p>\n<b><font color=\"#664400\">" + place.getPlaceName()
						+ "</font></b></p>\n<p>\n"+"<img src=\"http://yecols.cn/photos/buaagraduate/images/DSC00091-thumb.JPG\" />"
						+ place.getPlaceDesc() + "\n</p>", place.getPlacePosition());
		System.out.println(ga.getText());
		ga.getAttributes().setFont(Font.decode("SansSerif-PLAIN-14"));
		ga.getAttributes().setTextColor(Color.BLACK);
		ga.getAttributes().setTextAlign(AVKey.RIGHT);
		ga.getAttributes().setBackgroundColor(new Color(1f, 1f, 1f, .8f));
		ga.getAttributes().setBorderColor(Color.BLACK);
		ga.getAttributes().setSize(new Dimension(180, 0));
		ga.getAttributes().setImageSource("src/cn/yecols/images/hz.png");
		ga.getAttributes().setImageRepeat(Annotation.IMAGE_REPEAT_NONE);
		ga.getAttributes().setImageOpacity(1);
		ga.getAttributes().setImageScale(1);
		ga.getAttributes().setImageOffset(new Point(12, 12));
		
		
		ga.getAttributes().setInsets(new Insets(12, 20, 20, 12));
		//ga.getAttributes().
		return ga;
	}

	public int getSum() {
		// TODO Auto-generated method stub
		return places.size();
	}

}

