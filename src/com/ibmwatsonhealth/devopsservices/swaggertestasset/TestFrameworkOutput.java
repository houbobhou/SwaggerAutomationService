package com.ibmwatsonhealth.devopsservices.swaggertestasset;

public class TestFrameworkOutput {

	private int responseCode;
	private Object junitoutput;

	public TestFrameworkOutput() {

	}

	public int getResponseCode() {
		return this.responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public Object getJunitoutput() {
		return junitoutput;
	}

	public void setJunitoutput(Object junitresult) {
		this.junitoutput = junitresult;
	}

}
