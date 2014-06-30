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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RecentTasks extends Activity {

	// Initialize test button to change pass
	Button changePass;
	Button logout;
	Button btn_delHouse;

	/**
	 * Called when the activity is first created.
	 */
	public static final String TAG = "HouseMenu";
	private static final String KEY_SUCCESS = "success";
//	private static final String KEY_ERROR = "error";
	private static final String KEY_HOUSEADMIN = "house_admin";
	private static final String KEY_HOUSECODE = "house_code";
	private static final String KEY_UID = "uid";

	public SharedPreferences sharePref;
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_PREF = "RoomieTurn_app";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_tasks);
		sharePref = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);

		changePass = (Button) findViewById(R.id.btn_changePass);
		changePass.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(getApplicationContext(),
						ChangePassword.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(myIntent);

			}
		});

		logout = (Button) findViewById(R.id.button1);
		logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				clearSharedPreferences();
			}
		});

		btn_delHouse = (Button) findViewById(R.id.btn_removeHouse);
		btn_delHouse.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				NetAsync(view);
			}
		});
	}

	/**
	 * showToast displays short messages to users
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
			nDialog = new ProgressDialog(RecentTasks.this);
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
				new ProcessLogin().execute();
			} else {
				nDialog.dismiss();
				showToast("Error in Network Connection");
			}
		}
	}

	/**
	 * ProcessLogin Task to get and send data to My SQL database through JSON
	 * response.
	 **/
	private class ProcessLogin extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;
		private String houseCode, uid;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			HashMap<String, String> user = new HashMap<String, String>();
			user = db.getUserDetails();
			houseCode = user.get(KEY_HOUSECODE);
			uid = user.get(KEY_UID);

			pDialog = new ProgressDialog(RecentTasks.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Logging in ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.removeHouse(houseCode);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					String res = json.getString(KEY_SUCCESS);
					if (Integer.parseInt(res) == 1) {
						pDialog.setMessage("Loading User Space");
						pDialog.setTitle("Getting Data");
						DatabaseHandler db = new DatabaseHandler(
								getApplicationContext());

						/**
						 * Store JSON data into SQLITE database
						 **/
						db.removeHouse(Integer.parseInt(uid), null, null, null);
						Intent myIntent = new Intent(getApplicationContext(),
								HouseMenu.class);
						myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(myIntent);
						finish();
					} else {
						pDialog.dismiss();
						showToast("Error in Removing House");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void NetAsync(View view) {
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		HashMap<String, String> user = new HashMap<String, String>();
		user = db.getUserDetails();
		String admin = user.get(KEY_HOUSEADMIN);
		if (Integer.parseInt(admin) == 1) {
			new NetCheck().execute();
		} else {
			// TODO: Display the user who is admin?
			showToast("User who created house has permission to delete it.");
		}
	}

	private void clearSharedPreferences() {
		Log.i(TAG, "clearSharedPreferences");
		Editor editor = sharePref.edit();
		editor.remove(KEY_PASSWORD);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recent_tasks, menu);
		return true;
	}

}
