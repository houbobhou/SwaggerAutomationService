package com.ibmwatsonhealth.devopsservices.swaggertestasset;

import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.testng.TestNG;
import org.testng.collections.Lists;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class SwaggerCommandLine {

	public static String testngxml = "testng.xml";

	public static void main(String[] args) {

		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();

		// Update testng.xml file with latest parameter path
		System.out.println("IBM Toolchains");
		System.out.println("args[0] is: " + args[0]);
		if (!modifyTestngxml(args[0])) {
			// Something went wrong with file parsing
			System.out.println("Something went wrong");
			System.exit(1);
		}
		File filedir = new File(System.getProperty("user.dir"));
		File filetestngxml = new File(filedir, testngxml);
		suites.add(filetestngxml.getPath());
		testng.setTestSuites(suites);
		testng.run();
		testng.getStatus();
		System.exit(testng.getStatus());

	}

	public static boolean modifyTestngxml(String sFilePathParameter) {

		try {

			// Access the original testng.xml file here

			File filedir = new File(System.getProperty("user.dir"));
			File filetestngxml = new File(filedir, testngxml);

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			System.out.println("Hey the file path is: " + filetestngxml.getPath());
			Document doc = docBuilder.parse(filetestngxml.getPath());

			// Get the parameter attribute that needs to be updated
			Node staff = doc.getElementsByTagName("parameter").item(0);

			// update staff attribute
			NamedNodeMap attr = staff.getAttributes();
			Node nodeAttr = attr.getNamedItem("value");
			nodeAttr.setTextContent(sFilePathParameter);
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);

			// Append results back to main testng.xml file
			filedir = new File(System.getProperty("user.dir"));
			filetestngxml = new File(filedir, testngxml);
			StreamResult result = new StreamResult(new File(filetestngxml.getPath()));
			transformer.transform(source, result);
		} catch (Exception e) {
			// Some error occured
			e.printStackTrace();
			return false;
		}

		return true;

	}

}
