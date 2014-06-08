package com.example.roomieturn;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

	/**
	 * Called when the activity is first created.
	 */
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_USERNAME = "uname";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

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
				if (!houseCode.getText().toString().equals("")
						&& !housePass.getText().toString().equals("")) {
					 NetAsync(view);
				} else {
					showToast("One or More Fields Empty");
				}

			}
		});
	}

	/**
	 * showToast displays error messages to user
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
	@SuppressWarnings("rawtypes")
	private class NetCheck extends AsyncTask {
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

		@SuppressWarnings("unused")
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
					urlc.setConnectTimeout(300); // timeout 5 mins
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

		@SuppressWarnings({ "unchecked", "unused" })
		protected void onPostExecute(Boolean th) {
			if (th == true) {
				nDialog.dismiss();
				new ProcessJoin().execute();
			} else {
				nDialog.dismiss();
				loginErrorMsg.setText("Error in Network Connection");
			}
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	/**
	 * Async Task to get and send data to My SQL database through JSON response.
	 **/
	@SuppressWarnings("rawtypes")
	private class ProcessJoin extends AsyncTask {
		private ProgressDialog pDialog;
		String house, password;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			// Get user input data
			house = houseCode.getText().toString();
			password = housePass.getText().toString();
			
			// Display dialog
			pDialog = new ProgressDialog(JoinHouse.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Logging in ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressWarnings("unused")
		protected JSONObject doInBackground(String... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.joinHouse(house, password);
			return json;
		}

		@SuppressWarnings("unused")
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					String res = json.getString(KEY_SUCCESS);
					if (Integer.parseInt(res) == 1) {
						pDialog.setMessage("Loading User Space");
						pDialog.setTitle("Getting Data");
						DatabaseHandler db = new DatabaseHandler(
								getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");
						
						/**
						 * Clear all previous data in SQlite database.
						 **/
						UserFunctions logout = new UserFunctions();
						logout.logoutUser(getApplicationContext());
						db.addUser(json_user.getString(KEY_EMAIL),
								json_user.getString(KEY_USERNAME),
								json_user.getString(KEY_UID),
								json_user.getString(KEY_CREATED_AT));
						
						/**
						 * If JSON array details are stored in SQlite it
						 * launches the User Panel.
						 **/
						Intent upanel = new Intent(getApplicationContext(),
								RecentTasks.class);
						upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(upanel);

						// Close Login Screen
						finish();
					} else {
						pDialog.dismiss();
						loginErrorMsg.setText("Incorrect housecode/password");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public void NetAsync(View view) {
		new NetCheck().execute();
	}

}
