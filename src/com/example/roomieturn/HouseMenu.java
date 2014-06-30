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

public class HouseMenu extends Activity {

	/**
	 * Initialize GUI interface
	 */
	private Button btnCreate;
	private EditText houseName;
	private EditText newPass;
	private EditText confirmPass;
	private TextView join;
	private TextView loginErrorMsg;
	String email;
	String house_name;
	String house_pass;

	/**
	 * Called when the activity is first created.
	 */
	public static final String TAG = "HouseMenu";
	private static final String KEY_SUCCESS = "success";
	private static final String KEY_ERROR = "error";
	private static final String KEY_UID = "uid";
	private static final String KEY_HOUSENAME = "house_name";
	private static final String KEY_HOUSECODE = "house_code";
	private static final String KEY_HOUSEADMIN = "house_admin";

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
				house_name = houseName.getText().toString();
				house_pass = newPass.getText().toString();
				if (!house_name.equals("") && !house_pass.equals("")
						&& house_pass.equals(confirmPass.getText().toString())) {
					if (house_name.length() >= 3) {
						Log.i(TAG, "Creating House");
						NetAsync(view);
					} else {
						showToast("House name should be minimum 3 characters");
					}
				} else {
					if (house_name.equals("")) {
						showToast("House Name is Empty");
					} else if (house_pass.equals("")) {
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
				Log.i(TAG, "Join House");
				Intent myIntent = new Intent(view.getContext(), JoinHouse.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myIntent, 0);
				// finish();
			}
		});

	}

	/**
	 * showToast displays short messages to users
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
			nDialog = new ProgressDialog(HouseMenu.this);
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
					urlc.setConnectTimeout(3000);
					urlc.connect();
					if (urlc.getResponseCode() == 200) {
						return true;
					}
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean th) {
			if (th == true) {
				nDialog.dismiss();
				new ProcessCreateHouse().execute();
			} else {
				nDialog.dismiss();
				loginErrorMsg.setText("Error in Network Connection");
			}
		}
	}

	/**
	 * ProcessCreateHouse: Task to get and send data to My SQL database through
	 * JSON response.
	 **/
	private class ProcessCreateHouse extends
			AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			HashMap<String, String> user = new HashMap<String, String>();
			user = db.getUserDetails();
			email = user.get("email");

			// Display dialog
			pDialog = new ProgressDialog(HouseMenu.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Logging in ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			Log.i(TAG, "doInBackground");
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.createHouse(house_name, house_pass,
					email);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			Log.i(TAG, "onPostExecute");
			try {
				Log.i(TAG, "key_success: " + json.getString(KEY_SUCCESS));
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
						db.addHouse(json_user.getInt(KEY_UID),
								json_user.getString(KEY_HOUSENAME),
								json_user.getInt(KEY_HOUSECODE),
								json_user.getInt(KEY_HOUSEADMIN));

						// Go to Recent tasks activity
						Intent myIntent = new Intent(getApplicationContext(),
								RecentTasks.class);
						myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(myIntent);
						finish();

					} else if (Integer.parseInt(red) == 5) {
						pDialog.dismiss();
						loginErrorMsg.setText("SHITTT!"); // TODO: Proper response error
					} else {
						pDialog.dismiss();
						loginErrorMsg.setText("Must remove old house!");
					}
				}
			} catch (JSONException e) {
				Log.i(TAG, "JSONException e");
				e.printStackTrace();
			}
		}
	}

	public void NetAsync(View view) {
		new NetCheck().execute();
	}

}
