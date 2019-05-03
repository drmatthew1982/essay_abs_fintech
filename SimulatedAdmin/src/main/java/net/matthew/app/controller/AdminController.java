package net.matthew.app.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.v5ent.entity.Block;
import com.v5ent.entity.ReturnLatest;

@Controller
public class AdminController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	public static final String API_URL_GETLATEST = "http://localhost:8080/getlatest";
	public static final String API_URL_GETALL = "http://localhost:8080/getAll";
	public static final String API_URL_ADDBLOCK = "http://localhost:8080/addBlock";
	public static final String API_URL_GET_REMOTE_DEBT_AND_ROE = "http://localhost:8084/getDebtAndRoe";
	public static String DEBT = null;
	public static String ROE = null;

	private static DefaultHttpClient client = new DefaultHttpClient();

	final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	ModelAndView errorModelAndView = new ModelAndView();

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	ModelAndView home(HttpServletRequest httprequest, HttpServletResponse httpresponse) {
		ModelAndView modelAndView = returnIndexValue();
		if(DEBT!=null&&ROE!=null) {
			appendDebtandRoe(modelAndView,httprequest,httpresponse);
		}
		return modelAndView;

	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	ModelAndView saveValues(HttpServletRequest httprequest, HttpServletResponse httpresponse) {
		String targetdebt = httprequest.getParameter("debt");
		String targetroe = httprequest.getParameter("roe");
		DEBT = targetdebt;
		ROE = targetroe;
		ModelAndView modelAndView = returnIndexValue();
		appendDebtandRoe(modelAndView,httprequest,httpresponse);
		return modelAndView;
	}
	void appendDebtandRoe(ModelAndView modelAndView,HttpServletRequest httprequest, HttpServletResponse httpresponse) {
		modelAndView.addObject("debt",DEBT);
		modelAndView.addObject("ROE",ROE);
		String remoteDebtAndRoe=getRemoteDebtAndRoe(httprequest,httpresponse);
		String[] debtAndRoe=remoteDebtAndRoe.split(",");
		String debt = debtAndRoe[0];
		String roe = debtAndRoe[1];
		modelAndView.addObject("remoteDebt",debt);
		modelAndView.addObject("remoteRoe",roe);
	}
	

	String getRemoteDebtAndRoe(HttpServletRequest httprequest, HttpServletResponse httpresponse) {
		HttpGet httpGet = null;
		HttpResponse response;
		try {
			URIBuilder uriBuilder = new URIBuilder(API_URL_GET_REMOTE_DEBT_AND_ROE);
			httpGet = new HttpGet(uriBuilder.build());
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			return result;
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
			return "0,0";
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
	}

	ModelAndView returnIndexValue() {
		HttpGet httpGet = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(API_URL_GETLATEST);
			List<NameValuePair> list = new LinkedList<>();
			BasicNameValuePair param1 = new BasicNameValuePair("user", "admin");
			list.add(param1);
			uriBuilder.addParameters(list);
			httpGet = new HttpGet(uriBuilder.build());
			HttpResponse response = client.execute(httpGet);
			response.addHeader("content-type", "application/json");
			HttpEntity entity = response.getEntity();
			String string = EntityUtils.toString(entity);
			ReturnLatest returnObject = gson.fromJson(string, ReturnLatest.class);

			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("/index");
			modelAndView.addObject("returnObject", returnObject);
			modelAndView.addObject("debt", DEBT);
			modelAndView.addObject("roe", ROE);
			return modelAndView;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorModelAndView.setViewName("error");
			return errorModelAndView;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorModelAndView.setViewName("error");
			return errorModelAndView;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorModelAndView.setViewName("error");
			return errorModelAndView;
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
	}

	@RequestMapping("/adminview")
	@ResponseBody
	ModelAndView adminView(HttpServletRequest httprequest, HttpServletResponse httpresponse) {
		HttpGet httpGet = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(API_URL_GETALL);
			String username = httprequest.getParameter("user");
			String type = httprequest.getParameter("type");
			String productcode = httprequest.getParameter("product");
			List<NameValuePair> list = new LinkedList<>();
			BasicNameValuePair param1 = new BasicNameValuePair("user", username);
			BasicNameValuePair param2 = new BasicNameValuePair("type", type);
			BasicNameValuePair param3 = new BasicNameValuePair("product", productcode);
			list.add(param1);
			list.add(param2);
			list.add(param3);
			uriBuilder.addParameters(list);
			httpGet = new HttpGet(uriBuilder.build());
			HttpResponse response = client.execute(httpGet);
			response.addHeader("content-type", "application/json");
			HttpEntity entity = response.getEntity();
			String string = EntityUtils.toString(entity);
			List<LinkedTreeMap> returnObject = gson.fromJson(string, List.class);
			for (LinkedTreeMap map : returnObject) {
				String vac = (String) map.get("vac");
				if (vac.contains("illegalContent")) {
					map.put("comments", "IllegalContent is detected");

				}
			}
			for (LinkedTreeMap map : returnObject) {
				String vac = (String) map.get("vac");
				if (vac.startsWith("Out") && !vac.endsWith("10000")) {
					map.put("comments", "Wrong Transaction is detected.");
				}
			}
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("/adminview");
			modelAndView.addObject("returnObject", returnObject);
			modelAndView.addObject("type", type);
			modelAndView.addObject("productcode", productcode);
			return modelAndView;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return errorModelAndView;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return errorModelAndView;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return errorModelAndView;
		} finally {
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
	}

	@RequestMapping("/addblock")
	@ResponseBody
	ModelAndView addblock(HttpServletRequest httprequest, HttpServletResponse httpresponse) {
		HttpPost httpPost = null;
		String username = httprequest.getParameter("user");
		String type = httprequest.getParameter("type");
		String vac = httprequest.getParameter("vac");
		try {
			URIBuilder uriBuilder = new URIBuilder(API_URL_ADDBLOCK);
			List<NameValuePair> list = new LinkedList<>();
			BasicNameValuePair param1 = new BasicNameValuePair("user", username);
			BasicNameValuePair param2 = new BasicNameValuePair("type", type);
			BasicNameValuePair param3 = new BasicNameValuePair("vac", vac);
			list.add(param1);
			list.add(param2);
			list.add(param3);
			uriBuilder.addParameters(list);
			httpPost = new HttpPost(uriBuilder.build());
			HttpResponse response = client.execute(httpPost);
			response.addHeader("content-type", "application/json");
			HttpEntity entity = response.getEntity();
			String string = EntityUtils.toString(entity);
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.setViewName("redirect:/adminview");
			// modelAndView.addObject("returnObject", returnObject);
			modelAndView.addObject("type", type);
			modelAndView.addObject("user", username);
			return modelAndView;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return errorModelAndView;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return errorModelAndView;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return errorModelAndView;
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}

	}

	@RequestMapping("/setValues")
	@ResponseBody
	ModelAndView setValues(HttpServletRequest httprequest, HttpServletResponse httpresponse) {
		ModelAndView modelAndView = returnIndexValue();
		return modelAndView;

	}

}