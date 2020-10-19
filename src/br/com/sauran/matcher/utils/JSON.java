package br.com.sauran.matcher.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.sauran.matcher.Matcher;

public class JSON {

	public static String json(String url) {
		
		String json = null;
		
		try {
			URL oracle = new URL(url);
			URLConnection yc = oracle.openConnection();

			yc.setConnectTimeout(0);
			yc.setReadTimeout(0);

			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

			String jsonString = null;
			if ((jsonString = in.readLine()) != null) {
				json = jsonString;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}
	

	public static Object jsonToObject(boolean web, String path, Class<?> clazz) {
		Gson gson = new GsonBuilder().create();

		String json = "";

		if (web) {
			try {
				URL oracle = new URL(path);
				URLConnection yc = oracle.openConnection();

				yc.setConnectTimeout(0);
				yc.setReadTimeout(0);

				BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

				String jsonString = "";
				if ((jsonString = in.readLine()) != null) {
					json = jsonString;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {

			try {
				
				BufferedReader in = new BufferedReader(new InputStreamReader(Matcher.class.getResourceAsStream(path)));

				String jsonString = "";
				while ((jsonString = in.readLine()) != null) {
					json += jsonString;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return gson.fromJson(json, clazz);
	}

}
