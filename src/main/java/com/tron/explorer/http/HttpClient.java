package com.tron.explorer.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.tron.explorer.model.PostParameter;
import com.tron.explorer.model.TronException;

public class HttpClient {

	private static int connMaxTotal = 100;
	private static int maxPerRoute = 20;
	private static int conTimeOutMs = 6000;
	private static int soTimeOutMs = 6000;
	private static PoolingHttpClientConnectionManager cm;
	private static HttpRequestRetryHandler httpRequestRetryHandler;
	private final static Object syncLock = new Object();
	private static CloseableHttpClient httpClient=null;

	static {
		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory
				.getSocketFactory();
		LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory
				.getSocketFactory();
		Registry<ConnectionSocketFactory> registry = RegistryBuilder
				.<ConnectionSocketFactory> create().register("http", plainsf)
				.register("https", sslsf).build();

		cm = new PoolingHttpClientConnectionManager(registry);
		cm.setMaxTotal(connMaxTotal);
		cm.setDefaultMaxPerRoute(maxPerRoute);

		// 请求重试处理
		httpRequestRetryHandler = new HttpRequestRetryHandler() {
			@Override
			public boolean retryRequest(IOException exception,
					int executionCount, HttpContext context) {
				if (executionCount >= 3) {// 如果已经重试了5次，就放弃
					return false;
				}
				if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
					return true;
				}
				if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
					return false;
				}
				if (exception instanceof InterruptedIOException) {// 超时
					return false;
				}
				if (exception instanceof UnknownHostException) {// 目标服务器不可达
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
					return false;
				}
				if (exception instanceof SSLException) {// ssl握手异常
					return false;
				}

				HttpClientContext clientContext = HttpClientContext
						.adapt(context);
				HttpRequest request = clientContext.getRequest();
				// 如果请求是幂等的，就再次尝试
				if (!(request instanceof HttpEntityEnclosingRequest)) {
					return true;
				}
				return false;
			}
		};
		        
        new IdleConnectionMonitorThread(cm).start();//启动线程，5秒钟清空一次失效连接  
	}

	public HttpClient() {
	}

	public HttpClient(int conTimeOutMs, int soTimeOutMs) {
		this.conTimeOutMs = conTimeOutMs;
		this.soTimeOutMs = soTimeOutMs;
	}

	private static void config(HttpRequestBase httpRequestBase) {
		httpRequestBase.setHeader("User-Agent", "Mozilla/5.0");

		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(conTimeOutMs)
				.setConnectTimeout(conTimeOutMs).setSocketTimeout(soTimeOutMs)
				.build();
		httpRequestBase.setConfig(requestConfig);
	}

	public static CloseableHttpClient getHttpClient() {
		if(httpClient == null){
            synchronized (syncLock){
                if(httpClient == null){
					httpClient = HttpClients.custom()
							.setConnectionManager(cm)
							.setRetryHandler(httpRequestRetryHandler).build();
                }
            }
		}
		return httpClient;
	}
	
	public String get(String url) throws TronException {
		return get(url, new PostParameter[0]);

	}
	
	public String get(String url,PostParameter[] params) throws TronException {
		if (null != params && params.length > 0) {
			String encodedParams = HttpClient.encodeParameters(params);
			if (-1 == url.indexOf("?")) {
				url += "?" + encodedParams;
			} else {
				url += "&" + encodedParams;
			}
		}
		HttpGet httpget = new HttpGet(url);
		config(httpget);
		CloseableHttpResponse response = null;
		String result = "";
		try {
			response = getHttpClient().execute(httpget,
					HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return result;
	}
	
	/*
	 * 对parameters进行encode处理
	 */
	public static String encodeParameters(PostParameter[] postParams) {
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < postParams.length; j++) {
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(postParams[j].getName(), "UTF-8"))
						.append("=")
						.append(URLEncoder.encode(postParams[j].getValue(),
								"UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
		}
		return buf.toString();
	}
	
	public String post(String url, Map<String, Object> params)
			throws TronException {
		String result = "";
		HttpPost httppost = new HttpPost(url);
		config(httppost);
		setPostParams(httppost, params);
		CloseableHttpResponse response = null;
		try {
			response = getHttpClient().execute(httppost,
					HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public String post(String url, JSONObject obj)
			throws TronException {
		String result = "";
		HttpPost httppost = new HttpPost(url);
		config(httppost);
		setPostJsonParams(httppost, obj);
		CloseableHttpResponse response = null;
		try {
			response = getHttpClient().execute(httppost,
					HttpClientContext.create());
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private void setPostJsonParams(HttpPost httppost, JSONObject obj) {
		StringEntity s;
		try {
			s = new StringEntity(obj.toString(),"utf-8");
	        s.setContentType("application/json");
	        httppost.setEntity(s);	
		} catch (Exception e) {			
			e.printStackTrace();
		}        
		
	}

	private static void setPostParams(HttpPost httpost,
			Map<String, Object> params) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key).toString()));
		}
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
