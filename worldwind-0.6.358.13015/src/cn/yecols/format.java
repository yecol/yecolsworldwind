package cn.yecols;

import java.io.File;

public class format {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	File file = new File("src/cn/yecols/geoHz.xml");
	try {
		builder = factory.newDocumentBuilder();
		Document xmldoc = builder.parse(file);
		Element root = xmldoc.getDocumentElement();
		// ��ø�Ԫ�ء���Ӧ<paths>��

		NodeList paths = root.getChildNodes();
		// ·���б���Ӧһ��<path>�б�

		for (int i = 0; i < paths.getLength(); i++) {
			Node path = paths.item(i);
			// ��Ӧһ��<path>

			if (path instanceof Element) {
				NodeList points = path.getChildNodes();

				for (int j = 0; j < points.getLength(); j++) {
					if (points.item(j) instanceof Element) {
						//�ֵ����ƽڵ�Ĵ���
					}
				}
			}
		}

	} catch (ParserConfigurationException e) {
		e.printStackTrace();
	}
}
