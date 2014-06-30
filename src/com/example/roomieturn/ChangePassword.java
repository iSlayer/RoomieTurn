//shorty's comment
package com.example.roomieturn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomieturn.library.DatabaseHandler;
import com.example.roomieturn.library.UserFunctions;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class ChangePassword extends Activity {

	/**
	 * Initialize GUI interface
	 */
	public static final String TAG = "ChangePass";
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private EditText newpassword;
	private EditText confirmpass;
	private Button changepass;
	public TextView alert;
	public String newpass;

	/**
	 * Database keywords
	 */
	private static final String KEY_EMAIL = "email";

	/**
	 * SharedPreferences setup
	 */
	public SharedPreferences sharePref;
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_PREF = "RoomieTurn_app";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		sharePref = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);

		/**
		 * Defining all layout items
		 **/
		newpassword = (EditText) findViewById(R.id.newpass);
		confirmpass = (EditText) findViewById(R.id.confirmpass);
		alert = (TextView) findViewById(R.id.alertpass);

		/**
		 * Change Password button
		 */
		changepass = (Button) findViewById(R.id.btchangepass);
		changepass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				newpass = newpassword.getText().toString();
				String confirmPass = confirmpass.getText().toString();
				if ((!newpass.equals(""))
						&& newpass.equals(confirmpass.getText().toString())) {
					NetAsync(view);
				} else if ((!newpass.equals(confirmPass))) {
					showToast("Inconsistent Passwords");
				} else {
					showToast("Password field empty");
				}
			}
		});
	}

	/**
	 * showToast displays short messages to users
	 */
	private void showToast(String msg) {
		Toast toast = Toast.makeText(getApplicationContext(), msg,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.show();
	}

	private class NetCheck extends AsyncTask<String, String, Boolean> {
		private ProgressDialog nDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			nDialog = new ProgressDialog(ChangePassword.this);
			nDialog.setMessage("Loading..");
			nDialog.setTitle("Checking Network");
			nDialog.setIndeterminate(false);
			nDialog.setCancelable(true);
			nDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... args) {
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
				new ProcessRegister().execute();
			} else {
				nDialog.dismiss();
				alert.setText("Error in Network Connection");
			}
		}
	}

	private class ProcessRegister extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;
		String email;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			HashMap<String, String> user = new HashMap<String, String>();
			user = db.getUserDetails();
			email = user.get(KEY_EMAIL);
			pDialog = new ProgressDialog(ChangePassword.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.chgPass(newpass, email);
			Log.d("Button", "Register");
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					alert.setText("");
					String res = json.getString(KEY_SUCCESS);
					String red = json.getString(KEY_ERROR);
					if (Integer.parseInt(res) == 1) {
						pDialog.dismiss();
						alert.setText("Your Password is successfully changed.");
						clearSharedPreferences();

						// Create intent, back to login
						Intent myIntent = new Intent(getApplicationContext(),
								RecentTasks.class);
						myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(myIntent);
						finish();
					} else if (Integer.parseInt(red) == 2) {
						pDialog.dismiss();
						alert.setText("Invalid old Password.");
					} else {
						pDialog.dismiss();
						alert.setText("Error occured in changing Password.");
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

	private void clearSharedPreferences() {
		Log.i(TAG, "clearSharedPreferences");
		Editor editor = sharePref.edit();
		editor.remove(KEY_PASSWORD);
		editor.commit();
	}
}
