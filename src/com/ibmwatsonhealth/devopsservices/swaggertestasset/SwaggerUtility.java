package com.ibmwatsonhealth.devopsservices.swaggertestasset;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import io.swagger.inflector.examples.ExampleBuilder;
import io.swagger.inflector.examples.XmlExampleSerializer;
import io.swagger.inflector.examples.models.Example;
import io.swagger.inflector.processors.JsonNodeExampleSerializer;
import io.swagger.inflector.utils.ResolverUtil;
import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.RefModel;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import io.swagger.models.parameters.BodyParameter;

public class SwaggerUtility {

	// get Map with swagger specification parsed and return in memory data
	// structure with all required information
	public static final String sampleJsonString = "{\"name\":\"Sample json string\"}";
	public static final String sampleTextString = "hello world";
	public static final String sampleXmlString = "<?xml version='1.1' encoding='UTF-8'?><dummy><id>0</id><user>string</user><child><childNames>string</childNames></child></dummy>";

	public static Map<Object, Object> getSwaggerData(String url) {

		// Variable declaration section
		Map<Object, Object> swaggerMap = new HashMap<Object, Object>();
		Map<Object, Object> dataMap = null;
		Map<String, Model> definitionMap;
		List<EndpointOperationType> endpointOperationList = new ArrayList<EndpointOperationType>();
		EndpointOperationType endpointoperationInstance;
		Map<String, Response> responseMap = null;
		List<Parameter> parameterList = null;
		String baseURI = null;
		String basePath;
		String xmlString = null;
		String jsonString = null;
		String textString = null;
		Map<String, Object> feedData = null;

		try {
			com.fasterxml.jackson.databind.module.SimpleModule simpleModule = new com.fasterxml.jackson.databind.module.SimpleModule();
			simpleModule.addSerializer(new JsonNodeExampleSerializer());
			Json.mapper().registerModule(simpleModule);
			Yaml.mapper().registerModule(simpleModule);

			// Extract path of swagger specification
			Swagger swagger = new SwaggerParser().read(url);

			// Extract basepath url for the endpoint
			File file1 = new File(url);
			// file1.getPath();
			// baseURI = file1.getParent();
			File file2 = new File(swagger.getBasePath());
			basePath = file2.getPath();
			// System.out.println(file2.getPath());

			URL urlobject = new URL(url);
			File file = new File(urlobject.getPath());
			String parentPath = file.getParent();
			// System.out.println(parentPath);
			URL parentUrl = new URL(urlobject.getProtocol(), urlobject.getHost(), urlobject.getPort(), parentPath);
			baseURI = parentUrl.toString();
			// System.out.println("Parent: " + baseURI);

			// get all definitions from the Swagger specification
			definitionMap = new HashMap<String, Model>();
			definitionMap = swagger.getDefinitions();
			// Path map of all end points and all operations
			Map<String, Path> pathMap = new HashMap<String, Path>();
			pathMap = swagger.getPaths();
			for (Map.Entry<String, Path> entry : pathMap.entrySet()) {

				String someString = entry.getKey();
				System.out.println(someString);
				Path checkPath = entry.getValue();
				// List all operations associated with the path
				Operation operationValidator = new Operation();
				operationValidator = checkPath.getGet();
				if (operationValidator == null) {
					// System.out.println("get was null");
				} else {

					// Get all responses associated with the operation
					responseMap = new HashMap<String, Response>();
					responseMap = operationValidator.getResponses();

					// Iterate through all the responses in the map
					for (Map.Entry<String, Response> entryResponse : responseMap.entrySet()) {
						// System.out.println(entryResponse.getKey());
						Response rep = entryResponse.getValue();
						// System.out.println(rep.getDescription());
					}

					// Get consumption list for Operation
					List<String> consumesList = operationValidator.getConsumes();
					dataMap = new HashMap<Object, Object>();
					// Initialize map to populate data
					feedData = new HashMap<String, Object>();

					// Get all Parameters associated with the operation
					parameterList = new ArrayList<Parameter>();
					parameterList = operationValidator.getParameters();

					for (Parameter param : parameterList) {

						if (param instanceof BodyParameter) {
							BodyParameter bp = (BodyParameter) param;
							Model model = bp.getSchema();
							Object o = model.getExample();
							if (o == null) {

							} else {

								System.out.println("Object was not null someting to work with");
							}
							System.out.println("Model is of type" + model.getClass());

							if (model instanceof RefModel) {
								System.out.println("Model was found of type RefModel");
								RefModel ref = (RefModel) model;
								String simpleRef = ref.getSimpleRef();
								System.out.println(simpleRef);
								Model concreteModel = swagger.getDefinitions().get(simpleRef);
								Object test = ExampleBuilder.fromProperty(
										new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

								if (test != null && consumesList != null) {
									if (consumesList.contains("application/json")) {
										jsonString = Json.pretty(test);
										System.out.println(jsonString);
										feedData.put("data", jsonString);
										feedData.put("schema", "application/json");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleTextString);
										feedData.put("schema", "application/json");
										dataMap.put("invaliddata", feedData);

									} else if (consumesList.contains("application/xml")) {
										xmlString = new XmlExampleSerializer().serialize((Example) test);
										System.out.println(xmlString);
										feedData.put("data", xmlString);
										feedData.put("schema", "application/xml");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleJsonString);
										feedData.put("schema", "application/xml");
										dataMap.put("invaliddata", feedData);

									} else if (consumesList.contains("text/plain")) {
										textString = ((Example) test).asString();
										System.out.println(textString);
										feedData.put("data", textString);
										feedData.put("schema", "text/plain");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleJsonString);
										feedData.put("schema", "application/json");
										dataMap.put("invaliddata", feedData);

									}
								} else {
									dataMap.put("validdata", null);
								}

							} else if (model instanceof ArrayModel) {
								ArrayModel arrayModel = (ArrayModel) model;
								Property prop = arrayModel.getItems();
								if (prop instanceof RefProperty) {
									Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop), definitionMap);

									if (test != null && consumesList != null) {

										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {

											xmlString = new XmlExampleSerializer().serialize((Example) test);
											System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);
										}

									} else {
										dataMap.put("validdata", null);
									}

								}
							}

						}
					}

					// Feed values into swagger map
					endpointoperationInstance = new EndpointOperationType(entry.getKey(), "get");

					dataMap.put("definition", definitionMap);
					dataMap.put("response", responseMap);
					dataMap.put("baseURI", baseURI);
					swaggerMap.put(endpointoperationInstance, dataMap);
				}

				operationValidator = checkPath.getDelete();
				if (operationValidator == null) {
					// System.out.println("delete was null");
				} else {

					// Get all responses associated with the operation
					responseMap = new HashMap<String, Response>();
					responseMap = operationValidator.getResponses();

					// Iterate through all the responses in the map
					for (Map.Entry<String, Response> entryResponse : responseMap.entrySet()) {
						// System.out.println(entryResponse.getKey());
						Response rep = entryResponse.getValue();
						// System.out.println(rep.getDescription());
					}

					// Get consumption list for Operation
					List<String> consumesList = operationValidator.getConsumes();
					dataMap = new HashMap<Object, Object>();
					// Initialize map to populate data
					feedData = new HashMap<String, Object>();

					// Get all Parameters associated with the operation
					parameterList = new ArrayList<Parameter>();
					parameterList = operationValidator.getParameters();

					for (Parameter param : parameterList) {
						// System.out.println(param.getClass());
						if (param instanceof BodyParameter) {
							BodyParameter bp = (BodyParameter) param;
							Model model = bp.getSchema();
							Object o = model.getExample();
							if (o == null) {

							} else {

								System.out.println("Object was not null someting to work with");
							}
							System.out.println("Model is of type" + model.getClass());

							if (model instanceof RefModel) {
								System.out.println("Model was found of type RefModel");
								RefModel ref = (RefModel) model;
								String simpleRef = ref.getSimpleRef();
								System.out.println(simpleRef);
								Model concreteModel = swagger.getDefinitions().get(simpleRef);
								Object test = ExampleBuilder.fromProperty(
										new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

								if (test != null && consumesList != null) {
									if (consumesList.contains("application/json")) {
										jsonString = Json.pretty(test);
										System.out.println(jsonString);
										feedData.put("data", jsonString);
										feedData.put("schema", "application/json");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleTextString);
										feedData.put("schema", "application/json");
										dataMap.put("invaliddata", feedData);

									} else if (consumesList.contains("application/xml")) {

										xmlString = new XmlExampleSerializer().serialize((Example) test);
										System.out.println(xmlString);
										feedData.put("data", xmlString);
										feedData.put("schema", "application/xml");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleJsonString);
										feedData.put("schema", "application/xml");
										dataMap.put("invaliddata", feedData);

									} else if (consumesList.contains("text/plain")) {
										textString = ((Example) test).asString();
										System.out.println(textString);
										feedData.put("data", textString);
										feedData.put("schema", "text/plain");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleJsonString);
										feedData.put("schema", "application/json");
										dataMap.put("invaliddata", feedData);

									}
								} else {
									dataMap.put("validdata", null);
								}

							} else if (model instanceof ArrayModel) {
								ArrayModel arrayModel = (ArrayModel) model;
								Property prop = arrayModel.getItems();
								if (prop instanceof RefProperty) {
									Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {

											xmlString = new XmlExampleSerializer().serialize((Example) test);
											System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								}
							}

						}
					}

					// Feed values into swagger map
					endpointoperationInstance = new EndpointOperationType(entry.getKey(), "delete");
					// dataMap = new HashMap<Object, Object>();
					dataMap.put("definition", definitionMap);
					dataMap.put("response", responseMap);
					dataMap.put("baseURI", baseURI);
					swaggerMap.put(endpointoperationInstance, dataMap);
				}

				operationValidator = checkPath.getPost();
				if (operationValidator == null) {
					// System.out.println("post was null");
				} else {
					// Get all responses associated with the operation
					responseMap = new HashMap<String, Response>();
					responseMap = operationValidator.getResponses();

					// Iterate through all the responses in the map
					for (Map.Entry<String, Response> entryResponse : responseMap.entrySet()) {
						// System.out.println(entryResponse.getKey());
						Response rep = entryResponse.getValue();
						// System.out.println(rep.getDescription());
					}

					// Get consumption list for Operation
					List<String> consumesList = operationValidator.getConsumes();
					dataMap = new HashMap<Object, Object>();
					// Initialize map to populate data
					feedData = new HashMap<String, Object>();

					// Get all Parameters associated with the operation
					parameterList = new ArrayList<Parameter>();
					parameterList = operationValidator.getParameters();

					for (Parameter param : parameterList) {
						// System.out.println(param.getClass());
						System.out.println("THIS IS A POST OPERATION");
						if (param instanceof BodyParameter) {
							BodyParameter bp = (BodyParameter) param;
							Model model = bp.getSchema();
							System.out.println("Model is of type" + model.getClass());

							if (model instanceof RefModel) {
								System.out.println("Model was found of type RefModel");
								RefModel ref = (RefModel) model;
								String simpleRef = ref.getSimpleRef();
								System.out.println(simpleRef);
								Model concreteModel = swagger.getDefinitions().get(simpleRef);
								Object test = ExampleBuilder.fromProperty(
										new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

								if (test != null && consumesList != null) {
									if (consumesList.contains("application/json")) {
										jsonString = Json.pretty(test);
										System.out.println(jsonString);
										feedData.put("data", jsonString);
										feedData.put("schema", "application/json");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleTextString);
										feedData.put("schema", "application/json");
										dataMap.put("invaliddata", feedData);

									} else if (consumesList.contains("application/xml")) {

										xmlString = new XmlExampleSerializer().serialize((Example) test);
										System.out.println(xmlString);
										feedData.put("data", xmlString);
										feedData.put("schema", "application/xml");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleJsonString);
										feedData.put("schema", "application/xml");
										dataMap.put("invaliddata", feedData);

									} else if (consumesList.contains("text/plain")) {
										textString = ((Example) test).asString();
										System.out.println(textString);
										feedData.put("data", textString);
										feedData.put("schema", "text/plain");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleJsonString);
										feedData.put("schema", "application/json");
										dataMap.put("invaliddata", feedData);

									}
								} else {
									dataMap.put("validdata", null);
								}

							} else if (model instanceof ArrayModel) {
								ArrayModel arrayModel = (ArrayModel) model;
								Property prop = arrayModel.getItems();
								if (prop instanceof RefProperty) {
									Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {

											xmlString = new XmlExampleSerializer().serialize((Example) test);
											System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								}
							}

						}
					}
					// Feed values into swagger map
					endpointoperationInstance = new EndpointOperationType(entry.getKey(), "post");
					// dataMap = new HashMap<Object, Object>();
					dataMap.put("definition", definitionMap);
					dataMap.put("response", responseMap);
					dataMap.put("baseURI", baseURI);
					swaggerMap.put(endpointoperationInstance, dataMap);
				}
				operationValidator = checkPath.getPut();
				if (operationValidator == null) {
					// System.out.println("put was null");
				} else {
					// Get all responses associated with the operation
					responseMap = new HashMap<String, Response>();
					responseMap = operationValidator.getResponses();
					// Iterate through all the responses in the map
					for (Map.Entry<String, Response> entryResponse : responseMap.entrySet()) {
						// System.out.println(entryResponse.getKey());
						Response rep = entryResponse.getValue();
						// System.out.println(rep.getDescription());
					}

					// Get consumption list for Operation
					List<String> consumesList = operationValidator.getConsumes();
					dataMap = new HashMap<Object, Object>();
					// Initialize map to populate data
					feedData = new HashMap<String, Object>();

					// Get all Parameters associated with the operation
					parameterList = new ArrayList<Parameter>();
					parameterList = operationValidator.getParameters();
					System.out.println("THIS IS A PUT OPERATION");

					for (Parameter param : parameterList) {
						System.out.println(param.getClass());
						if (param instanceof BodyParameter) {
							// System.out.println("BODY PARAMETER WAS FOUND for
							// a
							// put operation");
							BodyParameter bp = (BodyParameter) param;
							Model model = bp.getSchema();
							Object o = model.getExample();
							if (o == null) {

							} else {

								System.out.println("Object was not null someting to work with");
							}
							System.out.println("Model is of type" + model.getClass());
							if (model instanceof RefModel) {
								System.out.println("Model was found of type RefModel");
								RefModel ref = (RefModel) model;
								String simpleRef = ref.getSimpleRef();
								System.out.println(simpleRef);
								Model concreteModel = swagger.getDefinitions().get(simpleRef);
								Object test = ExampleBuilder.fromProperty(
										new io.swagger.models.properties.RefProperty(simpleRef), definitionMap);

								if (test != null && consumesList != null) {
									if (consumesList.contains("application/json")) {
										jsonString = Json.pretty(test);
										System.out.println(jsonString);
										feedData.put("data", jsonString);
										feedData.put("schema", "application/json");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleTextString);
										feedData.put("schema", "application/json");
										dataMap.put("invaliddata", feedData);

									} else if (consumesList.contains("application/xml")) {

										xmlString = new XmlExampleSerializer().serialize((Example) test);
										System.out.println(xmlString);
										feedData.put("data", xmlString);
										feedData.put("schema", "application/xml");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleJsonString);
										feedData.put("schema", "application/xml");
										dataMap.put("invaliddata", feedData);

									} else if (consumesList.contains("text/plain")) {
										textString = ((Example) test).asString();
										System.out.println(textString);
										feedData.put("data", textString);
										feedData.put("schema", "text/plain");
										dataMap.put("validdata", feedData);

										// Setup for invalid data
										feedData = new HashMap<String, Object>();
										feedData.put("data", sampleJsonString);
										feedData.put("schema", "application/json");
										dataMap.put("invaliddata", feedData);

									}
								} else {
									dataMap.put("validdata", null);
								}

							} else if (model instanceof ArrayModel) {
								ArrayModel arrayModel = (ArrayModel) model;
								Property prop = arrayModel.getItems();
								if (prop instanceof RefProperty) {
									Object test = ExampleBuilder.fromProperty(new ArrayProperty(prop), definitionMap);

									if (test != null && consumesList != null) {
										if (consumesList.contains("application/json")) {
											jsonString = Json.pretty(test);
											System.out.println(jsonString);
											feedData.put("data", jsonString);
											feedData.put("schema", "application/json");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleTextString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("application/xml")) {

											xmlString = new XmlExampleSerializer().serialize((Example) test);
											System.out.println(xmlString);
											feedData.put("data", xmlString);
											feedData.put("schema", "application/xml");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/xml");
											dataMap.put("invaliddata", feedData);

										} else if (consumesList.contains("text/plain")) {
											textString = ((Example) test).asString();
											System.out.println(textString);
											feedData.put("data", textString);
											feedData.put("schema", "text/plain");
											dataMap.put("validdata", feedData);

											// Setup for invalid data
											feedData = new HashMap<String, Object>();
											feedData.put("data", sampleJsonString);
											feedData.put("schema", "application/json");
											dataMap.put("invaliddata", feedData);

										}
									} else {
										dataMap.put("validdata", null);
									}

								}
							}

						}
					}

					// Feed values into swagger map
					endpointoperationInstance = new EndpointOperationType(entry.getKey(), "put");
					// dataMap = new HashMap<Object, Object>();
					dataMap.put("definition", definitionMap);
					dataMap.put("response", responseMap);
					dataMap.put("baseURI", baseURI);
					swaggerMap.put(endpointoperationInstance, dataMap);

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			swaggerMap = null;
		}

		return swaggerMap;
	}

	public static void main(String[] args) {

		Map<Object, Object> finalMap = getSwaggerData("http://petstore.swagger.io/v2/swagger.json");
		System.out.println("final map is generated debug and check once");

		// Iterate through Final map and see output
		for (Map.Entry<Object, Object> output : finalMap.entrySet()) {

			EndpointOperationType endpointtype = (EndpointOperationType) output.getKey();
			// endpointtype.getEndpoint() + endpointtype.getOperation();
			// System.out.println(endpointtype.getEndpoint() + "+" +
			// endpointtype.getOperation());
		}

	}

}
