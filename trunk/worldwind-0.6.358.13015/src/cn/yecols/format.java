package cn.yecols;

import java.io.File;

public class format {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	File file = new File("src/cn/yecols/geoHz.xml");
	try {
		builder = factory.newDocumentBuilder();
		Document xmldoc = builder.parse(file);
		Element root = xmldoc.getDocumentElement();
		// 获得根元素。对应<paths>。

		NodeList paths = root.getChildNodes();
		// 路径列表。对应一个<path>列表。

		for (int i = 0; i < paths.getLength(); i++) {
			Node path = paths.item(i);
			// 对应一个<path>

			if (path instanceof Element) {
				NodeList points = path.getChildNodes();

				for (int j = 0; j < points.getLength(); j++) {
					if (points.item(j) instanceof Element) {
						//街道控制节点的处理
					}
				}
			}
		}

	} catch (ParserConfigurationException e) {
		e.printStackTrace();
	}
}
