package org.apache.cxf.spring.boot.jaxws.soap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.cxf.spring.boot.jaxws.soap.type.SoapType;
import org.apache.cxf.spring.boot.jaxws.soap.type.SoapTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SoapUtils {
	
	private static Logger logger = LoggerFactory.getLogger(SoapUtils.class);

	private static Integer _connTimeout = Integer.valueOf(0);

	private static Integer _readTimeout = Integer.valueOf(0);

	private static Object getWholeObject(Map variables, String binding) throws Exception {
		Object obj = null;
		Pattern regex = Pattern.compile("(\\w*)\\..*");
		Matcher regexMatcher = regex.matcher(binding);
		if (regexMatcher.find()) {
			String varKey = regexMatcher.group(1);
			obj = variables.get(varKey);
		} else {
			obj = PropertyUtils.getProperty(variables, binding);
		}
		return obj;
	}

	private static void checkFault(SOAPMessage message) throws SOAPException, SoapUtils.InvokeException {
		SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
		SOAPBody body = envelope.getBody();
		SOAPFault fault = body.getFault();
		if ((fault != null) && (fault.getFaultCode() != null))
			throw new InvokeException(fault.getFaultCode(), fault.getFaultString());
	}

	private static String getAttribute(Node node, String name) {
		Node tmp = node.getAttributes().getNamedItem(name);
		return tmp != null ? tmp.getTextContent() : null;
	}

	private static SOAPMessage invoke(URL invokeURL, SOAPMessage request) throws Exception {
		SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
		SOAPConnection connection = null;
		try {
			URL endPoint = new URL(null, invokeURL.toString(), new URLStreamHandler() {
				protected URLConnection openConnection(URL u) throws IOException {
					URL clone_url = new URL(u.toString());
					HttpURLConnection clone_urlconnection = (HttpURLConnection) clone_url.openConnection();

					if (SoapUtils._connTimeout.intValue() == 0) {
						_connTimeout = Integer.parseInt(AppConfigUtil.get("webservice.connTimeout"));
						if (SoapUtils._connTimeout.intValue() == 0)
							_connTimeout = Integer.valueOf(3000);
					}
					clone_urlconnection.setConnectTimeout(SoapUtils._connTimeout.intValue());
					clone_urlconnection.setReadTimeout(SoapUtils._readTimeout.intValue());
					return clone_urlconnection;
				}
			});
			connection = soapConnFactory.createConnection();

			SOAPMessage reply = connection.call(request, endPoint);

			return reply;
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (connection != null)
				connection.close();
		}
	}

	public static SOAPMessage execute(Map variables, JSONObject jObject) throws Exception {
		JSONArray inputs = jObject.getJSONArray("inputs");
		JSONArray inputParams = null;
		if (jObject.containsKey("inputParams"))
			inputParams = jObject.getJSONArray("inputParams");
		String url = jObject.getString("url");
		String namespace = jObject.getString("namespace");
		String method = jObject.getString("method");
		Boolean needPrefix = Boolean.valueOf(false);
		if (jObject.containsKey("needPrefix")) {
			needPrefix = Boolean.valueOf(jObject.getBoolean("needPrefix"));
		}
		if ((!StringUtils.hasText(url)) || (!StringUtils.hasText(namespace)) || (!StringUtils.hasText(method))) {
			throw new Exception("没有获取到webservice的调用地址、名称空间或调用方法.");
		}
		SOAPMessage requestMessage = RequestBuilder.build(inputs, inputParams, namespace, method, variables,
				needPrefix);

		SOAPMessage responseMessage = invoke(new URL(url), requestMessage);

		checkFault(responseMessage);

		return responseMessage;
	}

	public static void invoke(Map variables, JSONArray jArray) throws Exception {
		if (jArray.size() == 0) {
			logger.warn("没有找到webservice的调用配置.", jArray);
			return;
		}
		try {
			Iterator i$;
			for (i$ = jArray.iterator(); i$.hasNext();) {
				Object obj = i$.next();
				JSONObject jObject = (JSONObject) obj;
				JSONArray inputs = jObject.getJSONArray("inputs");
				JSONArray outputs = jObject.getJSONArray("outputs");
				JSONArray inputParams = null;
				if (jObject.containsKey("inputParams")) {
					inputParams = jObject.getJSONArray("inputParams");
				}
				String url = jObject.getString("url");
				String namespace = jObject.getString("namespace");
				String method = jObject.getString("method");
				Boolean needPrefix = Boolean.valueOf(false);
				if (jObject.containsKey("needPrefix")) {
					needPrefix = Boolean.valueOf(jObject.getBoolean("needPrefix"));
				}
				if ((!StringUtils.hasText(url)) || (!StringUtils.hasText(namespace))
						|| (!StringUtils.hasText(method))) {
					logger.warn("没有获取到webservice的调用地址、名称空间或调用方法.", jObject);
				} else {
					SOAPMessage requestMessage = RequestBuilder.build(inputs, inputParams, namespace, method, variables,
							needPrefix);

					SOAPMessage responseMessage = invoke(new URL(url), requestMessage);

					ResponseBuilder.build(variables, outputs, responseMessage);
				}
			}
		} catch (Exception e) {

			logger.error("调用webservice出错.", e);
			throw e;
		}
	}

	private static class ResponseBuilder {
		public static void build(Map variables, JSONArray jarray, SOAPMessage message) throws Exception {
			SoapUtils.checkFault(message);

			NodeList nodeList = message.getSOAPBody().getChildNodes();

			if ((nodeList == null) || (nodeList.getLength() < 1)) {
				return;
			}

			SOAPElement[] elements = new SOAPElement[nodeList.getLength()];
			for (int i = 0; i < elements.length; i++) {
				elements[i] = ((SOAPElement) nodeList.item(i));
			}

			for (Iterator i$ = jarray.iterator(); i$.hasNext();) {
				Object obj = i$.next();
				JSONObject jobject = (JSONObject) obj;
				build(variables, elements, jobject);
			}
		}

		private static SOAPElement getElementByPath(SOAPElement[] elements, String fullpath) {
			if (!StringUtils.hasText(fullpath)) {
				return elements[0];
			}
			String[] names = fullpath.split("\\.");
			int size = names.length;

			SOAPElement root = null;
			Node node = elements[0].getFirstChild();
			if (node != null) {
				root = (SOAPElement) node;
			}

			if (root == null) {
				return null;
			}

			for (int i = 1; i < size; i++) {
				String name = names[i];
				root = getElement(root.getChildElements(), name);
			}
			return root;
		}

		private static SOAPElement getElement(Iterator<SOAPElement> it, String name) {
			while (it.hasNext()) {
				SOAPElement element = (SOAPElement) it.next();
				String tagName = element.getTagName();
				if (tagName.equals(name)) {
					return element;
				}
			}
			return null;
		}

		private static void build(Map variables, SOAPElement[] roots, JSONObject jobject) throws Exception {
			if (jobject == null)
				return;

			String binding = jobject.getString("bindingVal");
			String soapType = jobject.getString("soapType");
			String beanType = jobject.getString("javaType");
			Integer bindingType = Integer.valueOf(jobject.getIntValue("bindingType"));
			String fullpath = "";
			if (jobject.containsKey("fullpath")) {
				fullpath = jobject.getString("fullpath");
			}
			SOAPElement elements = getElementByPath(roots, fullpath);
			binding = StringUtil.jsonUnescape(binding);

			if (!StringUtils.hasText(binding))
				return;

			Object obj = null;
			SoapType converter;
			if (StringUtils.hasText(soapType)) {
				try {
					Class kclass;
					if (soapType.matches("List\\{\\w*\\}")) {
						kclass = List.class;
					} else {
						kclass = Class.forName(soapType);
					}
					converter = SoapTypes.getTypeBySoap(soapType);
					obj = converter.convertToBean(kclass, new SOAPElement[] { elements });
				} catch (Exception ex) {
					converter = SoapTypes.getTypeBySoap("string");
					obj = converter.convertToBean(new SOAPElement[] { elements });
				}

			} else if ((StringUtils.hasText(beanType)) && (bindingType.intValue() == 2)) {
				Class klass = Class.forName(beanType);
				converter = SoapTypes.getTypeByBean(klass);
				obj = converter.convertToBean(klass, new SOAPElement[] { elements });
			} else {
				obj = elements.getTextContent();
			}

			switch (bindingType.intValue()) {
			case 2:
				if (obj != null) {
					if ((!(obj instanceof List)) && (binding.indexOf("[i]") > -1)) {
						List list = new ArrayList();
						list.add(obj);
						obj = list;
					}

					PropertyUtils.setProperty(variables, binding, obj);
				}
				break;
			case 3:
				variables.put("returnObj", obj);
				SoapUtils.engine.executeObject(binding, variables);

				variables.remove("returnObj");
			}
		}
	}

	private static class RequestBuilder {
		public static SOAPMessage build(JSONArray jarray, JSONArray inputParams, String namespace, String method,
				Map variables, Boolean needPrefix) throws Exception {
			return buildRequest(createRequest(jarray, inputParams, namespace, method, variables, needPrefix),
					namespace);
		}

		private static SOAPMessage buildRequest(SOAPElement element, String namespace) throws SOAPException {
			MessageFactory messageFactory = MessageFactory.newInstance();

			SOAPMessage message = messageFactory.createMessage();

			if (StringUtils.hasText(namespace)) {
				MimeHeaders mHers = message.getMimeHeaders();
				mHers.setHeader("SOAPAction",
						element.getElementQName().getNamespaceURI() + element.getElementQName().getLocalPart());
			}

			SOAPPart soapPart = message.getSOAPPart();
			SOAPEnvelope envelope = soapPart.getEnvelope();

			SOAPBody body = envelope.getBody();

			body.addChildElement(element);

			message.saveChanges();
			return message;
		}

		private static void buildSoapElementValue(SOAPElement soapElement, JSONObject jobject, Map variables)
				throws Exception {
			if (jobject == null)
				return;

			String binding = jobject.getString("bindingVal");
			String soapType = jobject.getString("soapType");
			String javaType = jobject.getString("javaType");
			Integer bindingType = Integer.valueOf(jobject.getIntValue("bindingType"));
			binding = StringUtil.jsonUnescape(binding);

			String listObj = "";
			String elementStr = "";
			Pattern regex = Pattern.compile("^.*\\.(\\w+)\\[i\\](\\.\\w+)?$");
			Matcher regexMatcher = regex.matcher(binding);
			if (regexMatcher.find()) {
				listObj = regexMatcher.group(1);
				elementStr = regexMatcher.group(2);
			}
			try {
				Object obj = null;
				switch (bindingType.intValue()) {
				case 1:
					soapElement.setTextContent(binding);
					break;
				case 2:
					if (StringUtils.hasText(binding))
						obj = PropertyUtils.getProperty(variables, binding);
					break;
				case 3:
					GroovyScriptEngine scriptEngine = new GroovyScriptEngine();
					String scriptContent = binding;
					obj = scriptEngine.executeObject(scriptContent, variables);
				}

				if (obj != null) {
					SoapType converter = null;
					Class klass = null;

					if (soapType != null) {
						converter = SoapTypes.getTypeBySoap(soapType);
					} else if (javaType != null) {
						klass = Class.forName(javaType);
						converter = SoapTypes.getTypeByBean(klass);
					}

					if (StringUtils.hasText(listObj)) {
						if ((obj instanceof List)) {
							List list = (List) obj;
							String elementName = soapElement.getLocalName();
							SOAPElement parentElement = soapElement;

							if (StringUtils.hasText(elementStr)) {
								parentElement = soapElement.getParentElement();
							}
							if (list.size() == 0) {
								parentElement.detachNode();
								return;
							}

							SOAPElement grandpaElement = parentElement.getParentElement();
							listObj = parentElement.getTagName();
							NodeList fieldNodeList = grandpaElement.getElementsByTagName(listObj);
							if (fieldNodeList == null)
								return;
							int nodeCount = fieldNodeList.getLength();
							int listSize = list.size();
							int diffCount = listSize - nodeCount;

							for (int i = 0; i < diffCount; i++) {
								SOAPElement cloneElement = (SOAPElement) parentElement.cloneNode(true);
								grandpaElement.addChildElement(cloneElement);
							}
							fieldNodeList = grandpaElement.getElementsByTagName(listObj);
							for (int i = 0; i < listSize; i++) {
								Object item = list.get(i);
								SOAPElement listElement = (SOAPElement) fieldNodeList.item(i);
								SOAPElement itemElement = listElement;
								if (StringUtils.hasText(elementStr))
									itemElement = (SOAPElement) listElement.getElementsByTagName(elementName).item(0);
								if (item == null) {
									itemElement.detachNode();
								} else if (converter != null) {
									converter.setValue(itemElement, item, klass);
								} else {
									itemElement.setTextContent(item.toString());
								}
							}
						}

					} else if (converter != null) {
						converter.setValue(soapElement, obj, klass);
					} else {
						soapElement.setTextContent(obj.toString());
					}
				}

				String textContext = soapElement.getTextContent();
				boolean hasChild = soapElement.hasChildNodes();

				if ((!StringUtils.hasText(textContext)) && (!hasChild))
					soapElement.detachNode();
			} catch (Exception e) {
				SoapUtils.logger.error("动态设值出错.", e);
				throw e;
			}
		}

		private static void groupParams(JSONArray inputParams) {
			Collections.sort(inputParams, new Comparator<JSONObject>() {
				public int compare(JSONObject o1, JSONObject o2) {
					String name1 = o1.getString("name");
					String name2 = o2.getString("name");
					Pattern regex = Pattern.compile("^arg(\\d+)");
					Matcher matcher1 = regex.matcher(name1);
					Matcher matcher2 = regex.matcher(name2);
					if ((matcher1.matches()) && (matcher2.matches())) {
						String find1 = matcher1.group(1);
						String find2 = matcher2.group(1);
						return Integer.parseInt(find1) - Integer.parseInt(find2);
					}

					return 0;
				}
			});
		}

		private static SOAPElement createRequest(JSONArray jarray, JSONArray inputParams, String namespace,
				String method, Map variables, Boolean needPrefix) throws Exception {
			String prefix = "api";
			if (!StringUtils.hasText(namespace)) {
				prefix = "";
			}
			SOAPFactory factory = SOAPFactory.newInstance();
			SOAPElement bodyElement = factory.createElement(method, prefix, namespace);
			Map map;
			Iterator it;
			Iterator i$;
			if (!ObjectUtils.isEmpty(inputParams)) {
				groupParams(inputParams);
				map = new HashMap();
				for (i$ = inputParams.iterator(); i$.hasNext();) {
					Object obj = i$.next();
					JSONObject jobject = (JSONObject) obj;
					if (jobject != null) {
						String rootName = jobject.getString("name");
						SOAPElement rootElement = bodyElement.addChildElement(rootName);

						setRequestStruct(jobject, rootElement, 1);

						setBindingValue(jarray, rootElement, 1, rootName, variables, map);
					}
				}
				for (it = map.keySet().iterator(); it.hasNext();) {
					JSONObject bindingJobject = (JSONObject) it.next();
					SOAPElement soapElement = (SOAPElement) map.get(bindingJobject);
					buildSoapElementValue(soapElement, bindingJobject, variables);
				}
			} else {
				for (i$ = jarray.iterator(); i$.hasNext();) {
					Object obj = i$.next();
					JSONObject jobject = (JSONObject) obj;
					if (jobject != null) {
						String paramName = jobject.getString("name");
						SOAPElement element = bodyElement.addChildElement(paramName);

						buildSoapElementValue(element, jobject, variables);
					}
				}
			}
			return bodyElement;
		}

		private static void setRequestStruct(JSONObject jobject, SOAPElement soapElement, int level)
				throws SOAPException {
			String paramName = jobject.getString("name");
			String type = jobject.getString("type");
			SOAPElement element = null;

			if (level == 1) {
				element = soapElement;
			} else
				element = soapElement.addChildElement(paramName);
			Iterator i$;
			if (("bean".equals(type)) && (jobject.containsKey("children"))) {
				JSONArray children = jobject.getJSONArray("children");
				level++;
				for (i$ = children.iterator(); i$.hasNext();) {
					Object obj = i$.next();
					JSONObject childObject = (JSONObject) obj;
					if (childObject != null) {
						setRequestStruct(childObject, element, level);
					}
				}
			}
		}

		private static void setBindingValue(JSONArray jarray, SOAPElement soapElment, int level, String rootName,
				Map variables, Map<JSONObject, SOAPElement> map) throws Exception {
			String nodeName = soapElment.getNodeName();
			JSONObject bindingJobject = getBindingJObject(jarray, level, rootName, nodeName);
			Iterator it = soapElment.getChildElements();
			level++;
			if (bindingJobject == null) {
				if (!it.hasNext()) {
					soapElment.detachNode();
				} else {
					while (it.hasNext()) {
						SOAPElement child = (SOAPElement) it.next();
						setBindingValue(jarray, child, level, rootName + "." + child.getNodeName(), variables, map);
					}
				}
			} else
				map.put(bindingJobject, soapElment);
		}

		private static JSONObject getBindingJObject(JSONArray jarray, int level, String rootName, String nodeName) {
			JSONObject reJobject = null;
			for (Iterator i$ = jarray.iterator(); i$.hasNext();) {
				Object obj = i$.next();
				JSONObject jobject = (JSONObject) obj;
				if (jobject != null) {
					String paramName = jobject.getString("name");
					if (paramName.equals(nodeName)) {
						String fullpath = "";
						if (jobject.containsKey("fullpath")) {
							fullpath = jobject.getString("fullpath");
						}
						if (!StringUtils.hasText(fullpath)) {
							if (fullpath.equals(rootName))
								reJobject = jobject;
						} else {
							List pathAry = Arrays.asList(jobject.getString("bindingVal").split("\\."));

							if (level == pathAry.size())
								reJobject = jobject;
						}
					}
				}
			}
			return reJobject;
		}
	}

	public static class InvokeException extends Exception {
		private String code;
		private String msg;

		public InvokeException(String code, String msg) {
			this(code, msg, null);
		}

		public InvokeException(String code, String msg, Throwable e) {
			super(e);
			this.code = code;
			this.msg = msg;
		}

		public String getCode() {
			return this.code;
		}

		public String getMsg() {
			return this.msg;
		}
	}
}
