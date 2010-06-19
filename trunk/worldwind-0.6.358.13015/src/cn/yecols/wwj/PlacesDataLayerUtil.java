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
	//景点标示图层
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
	
	//初始化景点数组
	public void Initiation(){
		places.add(new Place("西溪访梅", new LatLon(30.2633, 120.0785), "西溪湿地国家公园，位于浙江省杭州市区<br>西部，距西湖不到5公里，是罕见的城中<br>次生湿地。这里生态资源丰富、自然景观<br>质朴、文化积淀深厚，曾与西湖、西泠并称<br>杭州“三西”，是目前国内第一个也是唯<br>一的集城市湿地、农耕湿地、文化湿地于<br>一体的国家湿地公园。",placeImage));
		places.add(new Place("苏堤春晓", new LatLon(30.2390, 120.1356), "北宋元祐五年(1090)，诗人苏轼(东坡)任<br>杭州知州时，疏浚西湖，利用浚挖的淤<br>泥构筑并历经杭州西湖后世演变而形成的，<br>杭州人民为纪念苏东坡治理西湖的功绩，<br>把它命名为苏堤。",placeImage));
		places.add(new Place("断桥残雪", new LatLon(30.2606, 120.1480), "断桥位于杭州市西湖白堤的东端，背靠宝<br>石山，面向杭州城，是外湖和北里湖的分<br>水点。断桥势较高视野开阔，是冬天观赏<br>西湖雪景的最佳去处。",placeImage));
		places.add(new Place("雷峰夕照", new LatLon(30.2367, 120.1457), "雷峰塔建于五代(975)，是吴越国王钱弘<br>俶为庆祝黄妃得子而建，初名黄妃塔。因<br>建在当时的西关外，故又称为西关砖塔。<br>原拟建十三层，后因财力所限，只造了五层。<br>明代嘉靖时，倭寇入侵，疑心塔内有<br>伏兵，纵火焚塔，仅存塔心。",placeImage));
		places.add(new Place("南屏晚钟", new LatLon(30.2320, 120.1460), "南屏山是九曜山的分支，此山山峰耸秀，<br>怪石玲珑，棱壁横坡，宛若屏障。因地<br>处杭城之南，有石壁如屏障，故名南屏山。<br> 南屏山，绵延横陈于西湖南岸，山高不过<br>百米，山体延伸却长达千余米。",placeImage));
		places.add(new Place("八卦田", new LatLon(30.2118, 120.1487),   "八卦田遗址曾是南宋皇家籍田的遗址。籍<br>田是古代中国以农为本的农耕文化的缩<br>影，是古代帝皇通过神圣仪式活动对农业生<br>产予以重视的场所。",placeImage));
		places.add(new Place("六和塔", new LatLon(30.2008, 120.1315),   "六和塔的名字来源于佛教的“六和敬”，当<br>时建造的目的是用以镇压钱塘江的江潮。<br>塔高59.89米，其建造风格非常独特，塔<br>内部砖石结构分七层，外部木结构为8面13<br>层。",placeImage));
		places.add(new Place("柳浪闻莺", new LatLon(30.2405, 120.1497), "西湖十景之五位于西湖东南岸，清波门处<br>的大型公园。分友谊、闻莺、聚景、南<br>园四个景区。柳丛衬托着紫楠、雪松、广玉<br>兰、梅花等异木名花。",placeImage));
		places.add(new Place("宝石流霞", new LatLon(30.2613, 120.1410), "西湖北岸的葛岭、宝石山自成一体，景色<br>奇特。这里的山岩呈赭红色，岩体中有<br>许多闪闪发亮的红色矿物质，每当阳光映照，<br>满山流霞缤纷，尤其是朝阳或落日霞光<br>洒沐之时，保俶清秀，披着霞光分外耀<br>目，仿佛数不清的宝石在奕奕生辉。因而<br>被称为“宝石流霞”。",placeImage));
		places.add(new Place("黄龙吐翠", new LatLon(30.2642, 120.1286), "在杭州栖霞岭北麓的曙光路，护国仁王寺<br>遗址处，顺山路步行至茂林修竹深处，<br>就可看到隐藏着道教洞天福地的黄龙洞古迹。<br>南宋以来这里作为西湖上五大祀龙点之<br>一而享有盛名，清杭州二十四景中有《<br>黄山积翠》一景即指此。",placeImage));
		places.add(new Place("西湖文化广场", new LatLon(30.2750, 120.1576), "西湖文化广场位于武林广场运河北侧，<br>地处杭州市中心，距西湖仅2公里，西<br>湖文化广场占地13.3公顷。西湖文化广场总<br>建筑面积35万平方米，西湖文化广场室<br>外广场约10万平方米。西湖文化广场主<br>塔楼是41层170米高的浙江环球中心。",placeImage));
		places.add(new Place("艮山门", new LatLon(30.2774, 120.1862), "位于浙江省杭州旧城的正北偏东。宋元以来，<br>此门虽非兵家必争，却为丝绸业集中<br>之地。以此为雏，杭州的工业，后来大都出于<br>此地。",placeImage));
		places.add(new Place("玉皇飞云", new LatLon(30.2291, 120.1545), "玉皇山位于西湖之南，介于西湖与钱塘江之<br>间，海拔二百三十九米，五代时吴越<br>王钱氏迎明州（今宁波）阿育王寺佛舍利于此<br>供奉，始有“育王山”之称。",placeImage));
		places.add(new Place("虎跑梦泉", new LatLon(30.2201, 120.1456), "虎跑梦泉位于西湖之南，大慈山定慧禅寺内。<br>虎跑之名，因梦泉而来。传说唐代<br>高僧性空住在这里，后来因水源短缺，准备迁<br>走。有一天，他在梦中得到神的指示：<br>南岳衡山有童子泉，当遣二虎移来。日<br>间果见两虎跑翠岩做穴，石壁涌出泉水，<br>虎跑梦泉由此得名。",placeImage));
		places.add(new Place("九溪烟树", new LatLon(30.2085, 120.1216), "九溪烟树位于浙江省杭州市的著名景点西<br>湖之西的鸡冠垅下，其发源有二：一自<br>龙井狮子峰，一自翁家山的杨梅岭，途中汇合<br>了青湾、宏法、猪头、方家、佛石、云<br>栖、百丈、唐家、小康九坞之水，一路<br>上穿越青山翠谷，又汇集了无数细流，上<br>自龙井起蜿蜒曲折7公里入钱<br>塘江，又称九溪十八涧。",placeImage));
		places.add(new Place("云栖竹径", new LatLon(30.1967, 120.1107), "云栖竹径位于西湖之西南，钱塘江北岸，<br>五云山云栖坞里。由于地理环境的特殊，<br>五云山上的五彩祥云，常飞集坞中栖留，并<br>经久不散，称“云栖”。",placeImage));
		places.add(new Place("杭州大剧院", new LatLon(30.2569, 120.2101), "杭州大剧院以其独特的建筑造型、完善<br>的演出功能、先进的舞台技术成为目前<br>中国最具现代化的标志性文化设施。",placeImage));
		places.add(new Place("朝晖公园", new LatLon(30.2838, 120.1722), "位于朝晖路口，在闹市中一处幽静的所在。<br>",placeImage));
		places.add(new Place("灵隐寺", new LatLon(30.2293, 120.0904), "在飞来峰与北高峰之间灵隐山麓中，两峰挟<br>峙，林木耸秀，深山古寺，云烟万状，<br>是一处古朴幽静、景色宜人的游览胜地。",placeImage));
		
	}
	
	//显示在视图上
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
	
	//为所有景点添加标注
	public RenderableLayer MakePlacesLayerWithMarker() throws IOException{
		placesDataLayer.removeAllRenderables();
		for(Place p:places){
			placesDataLayer.addRenderable(p);
		}
		System.out.println(places.size());
		return placesDataLayer;
	}
	
	
	//添加标注
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


	//属性标注的显示
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

