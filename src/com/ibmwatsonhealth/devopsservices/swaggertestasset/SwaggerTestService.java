package com.ibmwatsonhealth.devopsservices.swaggertestasset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

/**
 * Servlet implementation class SwaggerTestService
 */
public class SwaggerTestService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int responsecode = -1;
		StringBuilder crunchifyBuilder = new StringBuilder();
		SwaggerExecutionEngine engine;
		String frameworkoutput = null;

		StringWriter stw;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
			String jsonString = crunchifyBuilder.toString();
			JSONObject jsonObject = new JSONObject(jsonString);
			String swaggerPath = jsonObject.getString("swaggerURL");
			engine = new SwaggerExecutionEngine();
			TestFrameworkOutput executionOutput = engine.executeTestFramework(swaggerPath);
			responsecode = executionOutput.getResponseCode();
			String testframeworkoutputPath = System.getProperty("user.dir") + "/SwaggerSuite/SwaggerTest.xml";
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			InputStream inputStream = new FileInputStream(new File(testframeworkoutputPath));
			org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
			stw = new StringWriter();
			Transformer serializer = TransformerFactory.newInstance().newTransformer();
			serializer.transform(new DOMSource(doc), new StreamResult(stw));
		} catch (JSONException | SAXException | ParserConfigurationException | TransformerFactoryConfigurationError
				| TransformerException e) {
			throw new ServletException(e);
		}
		
		frameworkoutput = stw.toString();
		
		response.addIntHeader("teststatus", responsecode);
		response.setContentType("application/xml");
		response.getWriter().println(frameworkoutput);		
	}
}
