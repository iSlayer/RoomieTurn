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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HouseMenu extends Activity {

	/**
	 * Initialize variables
	 */
	private Button btnCreate;
	private EditText houseName;
	private EditText newPass;
	private EditText confirmPass;
	private TextView join;
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
		setContentView(R.layout.activity_house_menu);

		/**
		 * Initialize GUI interface
		 */
		btnCreate = (Button) findViewById(R.id.button_create_house);
		houseName = (EditText) findViewById(R.id.editText_house_name);
		newPass = (EditText) findViewById(R.id.new_house_pass);
		confirmPass = (EditText) findViewById(R.id.confirm_house_pass);
		join = (TextView) findViewById(R.id.textView_join_house);
		loginErrorMsg = (TextView) findViewById(R.id.loginErrorMsg);

		/**
		 * Button click for create house
		 */
		btnCreate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Generate house and make usr the admin
				// taking him to the recent tasks screen
				if (!houseName.getText().toString().equals("")
						&& !newPass.getText().toString().equals("")
						&& newPass.getText().toString()
								.equals(confirmPass.getText().toString())) {
					if (houseName.getText().toString().length() >= 3) {
						 NetAsync(view);
					} else {
						showToast("House name should be minimum 3 characters");
					}
				} else {
					if (houseName.getText().toString().equals("")) {
						showToast("House Name is Empty");
					} else if (newPass.getText().toString().equals("")) {
						showToast("Password Field is Empty");
					} else {
						showToast("Passwords Do Not Match");
					}
				}

			}
		});

		/**
		 * Button click for join house
		 */
		join.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), JoinHouse.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myIntent, 0);
//				finish();
			}
		});

	}

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
			nDialog = new ProgressDialog(HouseMenu.this);
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

		@SuppressWarnings({ "unused", "unchecked" })
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
			house = houseName.getText().toString();
			password = newPass.getText().toString();
			
			// Display dialog
			pDialog = new ProgressDialog(HouseMenu.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Logging in ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@SuppressWarnings("unused")
		protected JSONObject doInBackground(String... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.createHouse(house, password);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.house_menu, menu);
		return true;
	}

}
