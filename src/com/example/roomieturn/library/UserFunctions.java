package com.example.roomieturn.library;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.content.Context;

public class UserFunctions {

	// URL of the PHP API
	private JSONParser jsonParser;
	private static String loginURL = "http://muraltemp.com/";
	private static String registerURL = "http://muraltemp.com/";
	private static String forpassURL = "http://muraltemp.com/";
	private static String chgpassURL = "http://muraltemp.com/";
	private static String createhouseURL = "http://muraltemp.com/";
	private static String joinhouseURL = "http://muraltemp.com/";
	private static String removehouseURL = "http://muraltemp.com/";
	private static String addchoreURL = "http://muraltemp.com/";
	private static String rmchoreURL = "http://muraltemp.com/";
	private static String rmallchoreURL = "http://muraltemp.com/";

	private static String login_tag = "login";
	private static String register_tag = "register";
	private static String forpass_tag = "forpass";
	private static String chgpass_tag = "chgpass";
	private static String createHouse_tag = "createHouse";
	private static String joinHouse_tag = "joinHouse";
	private static String removeHouse_tag = "removeHouse";
	private static String addChore_tag = "addChore";
	private static String rmChore_tag = "rmChore";
	private static String rmAllChores_tag = "rmAllChores";

	// constructor
	public UserFunctions() {
		jsonParser = new JSONParser();
	}

	/**
	 * Function to Login
	 **/
	public JSONObject loginUser(String email, String password) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
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
		List<NameValuePair> params = new ArrayList<NameValuePair>();
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
		List<NameValuePair> params = new ArrayList<NameValuePair>();
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
		List<NameValuePair> params = new ArrayList<NameValuePair>();
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
	public JSONObject createHouse(String house, String password, String email) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", createHouse_tag));
		params.add(new BasicNameValuePair("house", house));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("email", email));
		JSONObject json = jsonParser.getJSONFromUrl(createhouseURL, params);
		return json;
	}

	/**
	 * Function to Join House
	 **/
	public JSONObject joinHouse(String houseCode, String housePass, String email) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", joinHouse_tag));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("house_code", houseCode));
		params.add(new BasicNameValuePair("housepassword", housePass));
		JSONObject json = jsonParser.getJSONFromUrl(joinhouseURL, params);
		return json;
	}

	/**
	 * Function to Remove House
	 **/
	public JSONObject removeHouse(String houseCode) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", removeHouse_tag));
		params.add(new BasicNameValuePair("house_code", houseCode));
		JSONObject json = jsonParser.getJSONFromUrl(removehouseURL, params);
		return json;
	}

	/**
	 * Function to Create Chore(s) on server
	 **/
	public JSONObject addChore(String chore_name, String house_code,
			String username, String uid, String date) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", addChore_tag));
		params.add(new BasicNameValuePair("chore_name", chore_name));
		params.add(new BasicNameValuePair("house_code", house_code));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("date", date));
		JSONObject json = jsonParser.getJSONFromUrl(addchoreURL, params);
		return json;
	}

	/** 
	 * Function to Remove Chore from server
	 **/
	public JSONObject rmChore(String choreid) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", rmChore_tag));
		params.add(new BasicNameValuePair("choreid", choreid));
		JSONObject json = jsonParser.getJSONFromUrl(rmchoreURL, params);
		return json;
	}

	/** 
	 * Function to Remove Chore from server
	 **/
	public JSONObject rmAllChore(String house_code) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", rmAllChores_tag));
		params.add(new BasicNameValuePair("house_code", house_code));
		JSONObject json = jsonParser.getJSONFromUrl(rmallchoreURL, params);
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
