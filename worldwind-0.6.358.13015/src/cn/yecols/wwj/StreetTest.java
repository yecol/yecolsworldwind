package cn.yecols.wwj;

import java.io.IOException;

import org.xml.sax.SAXException;

public class StreetTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StreetsDataReader streetsDataReader=new StreetsDataReader();
		try {
			streetsDataReader.ReadStreetData();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
