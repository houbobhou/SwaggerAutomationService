package com.ibmwatsonhealth.devopsservices.swaggertestasset;

import java.util.HashMap;
import java.util.Map;

public class DataLoadOperationType {
	private EndpointOperationType endpointOperationType = new EndpointOperationType();
	private Map<Object, Object> dataMap = new HashMap<Object,Object>();

	public DataLoadOperationType(EndpointOperationType endpointOperationType, Map<Object, Object> dataMap) {
		this.endpointOperationType = endpointOperationType;
		this.dataMap = dataMap;
	}

	public EndpointOperationType getEndpointOperationType() {
		return this.endpointOperationType;
	}

	public Map<Object, Object> getDataMap() {
		return this.dataMap;
	}

	public void setDataMap(EndpointOperationType endpointOperationType) {
		this.endpointOperationType = endpointOperationType;
	}

	public void setDataMap(Map<Object, Object> dataMap) {
		this.dataMap = dataMap;
	}

}
