package com.ibmwatsonhealth.devopsservices.swaggertestasset;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public class SwaggerExecutionEngine {

	@SuppressWarnings("deprecation")
	public TestFrameworkOutput executeTestFramework(String someJsonString) {

		TestFrameworkOutput output = null;
		TestNG myTestNG = new TestNG();
		myTestNG.setUseDefaultListeners(true);
		myTestNG.setVerbose(3);
		XmlSuite mySuite = new XmlSuite();
		mySuite.setName("SwaggerSuite");
		XmlTest myTest = new XmlTest(mySuite);
		myTest.setName("SwaggerTest");
		myTest.addParameter("swaggerPath", someJsonString);
		List<XmlClass> myClasses = new ArrayList<XmlClass>();
		myClasses.add(new XmlClass("com.ibmwatsonhealth.devopsservices.swaggertestasset.TestNGTestSuite"));
		myTest.setXmlClasses(myClasses);
		List<XmlTest> myTests = new ArrayList<XmlTest>();
		myTests.add(myTest);
		mySuite.setTests(myTests);
		List<XmlSuite> mySuites = new ArrayList<XmlSuite>();
		mySuites.add(mySuite);
		myTestNG.setXmlSuites(mySuites);
		System.out.println(System.getProperty("user.dir"));
		myTestNG.setOutputDirectory(System.getProperty("user.dir"));
		myTestNG.run();
		//check if the framework has a skip
		//myTestNG.hasSkip();
		// if framework had a test skipped 

		try {
			System.out.println("Start with the swagger output file extraction");
			String testframeworkoutputPath = System.getProperty("user.dir") + "/SwaggerSuite/SwaggerTest.xml";
			File file = new File(testframeworkoutputPath);
			// Set output of test framework here
			output = new TestFrameworkOutput();
			if(myTestNG.hasSkip()){
				output.setResponseCode(1);
			}else{
				output.setResponseCode(myTestNG.getStatus());
			}
			// output.setJunitoutput(junitresult);

		} catch (Exception e) {
			// Something went wrong in the test framework
			System.out.println("Something went wrong within swagger execution engine");
			e.printStackTrace();
			output = null;
		}
		return output;

	}

}
