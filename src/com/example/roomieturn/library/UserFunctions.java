package com.example.roomieturn.library;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.content.Context;

public class UserFunctions {
	private JSONParser jsonParser;
	// URL of the PHP API
	private static String loginURL = "http://muraltemp.com/";
	private static String registerURL = "http://muraltemp.com/";
	private static String forpassURL = "http://muraltemp.com/";
	private static String chgpassURL = "http://muraltemp.com/";
	private static String createhouseURL = "http://muraltemp.com/";
	private static String joinhouseURL = "http://muraltemp.com/";
	private static String login_tag = "login";
	private static String register_tag = "register";
	private static String forpass_tag = "forpass";
	private static String chgpass_tag = "chgpass";
	private static String createHouse_tag = "createHouse";
	private static String joinHouse_tag = "joinHouse";

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	/**
	 * Function to Login
	 **/
	public JSONObject loginUser(String email, String password) {
		// Building Parameters
		List params = new ArrayList();
		params.add(new BasicNameValuePair("tag", login_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
		return json;
	}

	/**
	 * Function to change password
	 **/
	public JSONObject chgPass(String newpas, String email) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("tag", chgpass_tag));
		params.add(new BasicNameValuePair("newpas", newpas));
		params.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(chgpassURL, params);
		return json;
	}

	/**
	 * Function to reset the password
	 **/
	public JSONObject forPass(String forgotpassword) {
		List params = new ArrayList();
		params.add(new BasicNameValuePair("tag", forpass_tag));
		params.add(new BasicNameValuePair("forgotpassword", forgotpassword));
		JSONObject json = jsonParser.getJSONFromUrl(forpassURL, params);
		return json;
	}

	/**
	 * Function to Register
	 **/
	public JSONObject registerUser(String email, String uname, String password) {
		// Building Parameters
		List params = new ArrayList();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("uname", uname));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		return json;
	}

	/**
	 * Function to Create House
	 **/
	public JSONObject createHouse(String house, String password) {
		// Building Parameters
		List params = new ArrayList();
		params.add(new BasicNameValuePair("tag", createHouse_tag));
		params.add(new BasicNameValuePair("house", house));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = jsonParser.getJSONFromUrl(createhouseURL, params);
		return json;
	}

	/**
	 * Function to Join House
	 **/
	public JSONObject joinHouse(String houseCode, String housePass) {
		// Building Parameters
		List params = new ArrayList();
		params.add(new BasicNameValuePair("tag", joinHouse_tag));
		params.add(new BasicNameValuePair("house", houseCode));
		params.add(new BasicNameValuePair("password", housePass));
		JSONObject json = jsonParser.getJSONFromUrl(joinhouseURL, params);
		return json;
	}

	/**
	 * Function to logout user Resets the temporary data stored in SQLite
	 * Database
	 * */
	public boolean logoutUser(Context context) {
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}
}
