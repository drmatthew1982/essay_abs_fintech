package net.matthew.app.controller;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.v5ent.entity.ReturnLatest;

@Controller
public class MainController {
	  public static String API_URL = "http://localhost:8080/getlatest";
	  private static DefaultHttpClient client = new DefaultHttpClient();
	  final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	  ModelAndView errorModelAndView = new ModelAndView();
	  
	  @RequestMapping("/")
      @ResponseBody
      ModelAndView home() {
    	HttpGet httpGet = new HttpGet(API_URL);
    	HttpResponse response;
		try {
			response = client.execute(httpGet);
			response.addHeader("content-type","application/json");
			HttpEntity entity = response.getEntity();
			String string = EntityUtils.toString(entity);
			ReturnLatest returnObject=gson.fromJson(string, ReturnLatest.class);
			ModelAndView modelAndView = new ModelAndView();
	        modelAndView.setViewName("/index");
	        modelAndView.addObject("returnObject", returnObject);
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
		}
		
      }
}