package com.boco.demo.http;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Hello world!
 *
 */
public class App {
	
	private static final RestTemplate restTempate = new RestTemplate();
	
	public static void main(String[] args) {
		String aliveUrl = "";
		String pro = "";
		
		if(args.length != 2) {
			System.out.println("参数不对（格式为：URL 省份代码），如：\"http://172.16.188.183:25590/ljdj/RESTForProv/isAlive\" 571");
			System.exit(-1);
		}
		
		aliveUrl = args[0];
		pro = args[1];
		
		System.out.println(String.format("开始调用%s with 参数：%s", aliveUrl, pro));
		System.out.println("方式一：RestTemplate...........................");
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
			params.add("province", pro);
			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
			ResponseEntity<String> response1 = restTempate.postForEntity(aliveUrl, entity, String.class);
			if (HttpStatus.OK == response1.getStatusCode()) {
				System.out.println(String.format("Return -> %s", response1.getBody()));
			} else {
				System.out.println(response1.getStatusCodeValue() + ":" + response1.getStatusCode().name());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("方式一：RestTemplate...........................end.");
		
		System.out.println("方式二：Jersey...........................");
		jersey(aliveUrl, pro);
		System.out.println("方式二：Jersey...........................end.");
		
	}
	
	private static void jersey(String url, String pro) {
		Client client = null;
		try {
			client = ClientBuilder.newClient();
			client.property("jersey.config.client.connectTimeout", Integer.valueOf(2000));
			client.property("jersey.config.client.readTimeout", Integer.valueOf(5000));
			WebTarget webTarget = client.target(url);
			Invocation.Builder invocationBuilder = webTarget.request();
			Form form = new Form();
			form.param("province", pro);
			Response response = invocationBuilder.post(Entity.form(form));
			if (Response.Status.OK.getStatusCode() == response.getStatus()) {
				String returnInfo = (String) response.readEntity(String.class);
				System.out.println(String.format("Return -> %s", returnInfo));
				if (client != null) {
					client.close();
				}
			} else {
				System.out.println(response.getStatus() + ":" + response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}
}
