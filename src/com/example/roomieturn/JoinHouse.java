package com.example.roomieturn;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.roomieturn.library.DatabaseHandler;
import com.example.roomieturn.library.UserFunctions;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class JoinHouse extends Activity {

	/**
	 * Initialize GUI interface
	 */
	private EditText houseCode;
	private EditText housePass;
	private Button btnJoin;
	private TextView loginErrorMsg;
	String email;
	String uid;
	String housecode;
	String housepassword;

	/**
	 * Called when the activity is first created.
	 */
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private static final String TAG = "HouseMenu";
	private static final String KEY_UID = "uid";
	private static final String KEY_HOUSENAME = "house_name";
	private static final String KEY_HOUSECODE = "house_code";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_house);

		/**
		 * Initialize GUI interface
		 */
		houseCode = (EditText) findViewById(R.id.houseName);
		housePass = (EditText) findViewById(R.id.housePass);
		btnJoin = (Button) findViewById(R.id.join);
		loginErrorMsg = (TextView) findViewById(R.id.loginErrorMsg);

		/**
		 * Button click for join house
		 */
		btnJoin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				housecode = houseCode.getText().toString();
				housepassword = housePass.getText().toString();
				if (!housecode.equals("") && !housepassword.equals("")) {
					NetAsync(view);
				} else {
					showToast("One or More Fields Empty");
				}

			}
		});
	}

	/**
	 * showToast displays error messages to user
	 * 
	 * @param msg
	 */
	private void showToast(String msg) {
		Toast toast = Toast.makeText(getApplicationContext(), msg,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.show();
	}

	/**
	 * NetCheck Task to check whether Internet connection is working.
	 **/
	private class NetCheck extends AsyncTask<String, String, Boolean> {
		private ProgressDialog nDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			nDialog = new ProgressDialog(JoinHouse.this);
			nDialog.setTitle("Checking Network");
			nDialog.setMessage("Loading..");
			nDialog.setIndeterminate(false);
			nDialog.setCancelable(true);
			nDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... args) {

			/**
			 * Gets current device state and checks for working Internet
			 * connection by trying Google.
			 **/
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnected()) {
				try {
					URL url = new URL("http://www.google.com");
					HttpURLConnection urlc = (HttpURLConnection) url
							.openConnection();
					urlc.setConnectTimeout(3000); // timeout 5 mins
					urlc.connect();
					if (urlc.getResponseCode() == 200) {
						return true;
					}
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean th) {
			if (th == true) {
				nDialog.dismiss();
				new ProcessJoin().execute();
			} else {
				nDialog.dismiss();
				loginErrorMsg.setText("Error in Network Connection");
			}
		}
	}

	/**
	 * Async Task to get and send data to My SQL database through JSON response.
	 **/
	private class ProcessJoin extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			HashMap<String, String> user = new HashMap<String, String>();
			user = db.getUserDetails();
			email = user.get("email");
			uid = user.get("uid");

			// Display dialog
			pDialog = new ProgressDialog(JoinHouse.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Logging in ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.joinHouse(housecode, housepassword,
					email);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					String res = json.getString(KEY_SUCCESS);
					String red = json.getString(KEY_ERROR);
					if (Integer.parseInt(res) == 1) {
						pDialog.setMessage("Loading User Space");
						pDialog.setTitle("Getting Data");
						DatabaseHandler db = new DatabaseHandler(
								getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");

						/**
						 * Add house to SQlite database
						 **/
						Log.i(TAG, "user: " + json_user);
						db.addHouse(json_user.getString(KEY_UID),
								json_user.getString(KEY_HOUSENAME),
								json_user.getString(KEY_HOUSECODE));

						// Go to Recent tasks activity
						Intent myIntent = new Intent(getApplicationContext(),
								RecentTasks.class);
						myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(myIntent);
						finish();

					} else if (Integer.parseInt(red) == 1) {
						pDialog.dismiss();
						loginErrorMsg.setText("Incorrect house password.");

					} else if (Integer.parseInt(red) == 2) {
						pDialog.dismiss();
						loginErrorMsg.setText("House code does not exist.");

					} else {
						pDialog.dismiss();
						loginErrorMsg.setText("Must remove old house.");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void NetAsync(View view) {
		new NetCheck().execute();
	}

}
