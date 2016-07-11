package com.ibmwatsonhealth.devopsservices.swaggertestasset;

public class EndpointOperationType {
	private String endpoint;
	private String operation;

	public EndpointOperationType(String endpoint, String operation) {
		this.endpoint = endpoint;
		this.operation = operation;
	}

	public EndpointOperationType() {

	}

	public String getEndpoint() {
		return this.endpoint;
	}

	public String getOperation() {
		return this.operation;
	}

	public void setEndpoint(String endPoint) {
		this.endpoint = endPoint;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

}
