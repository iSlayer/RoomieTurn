package com.example.roomieturn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	EditText newpass;
	EditText confirmpass;
	TextView alert;
	Button changepass;
	Button cancel;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);

		/**
		 * Cancel button click event
		 */
		cancel = (Button) findViewById(R.id.btcancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent myIntent = new Intent(getApplicationContext(),
						RecentTasks.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(myIntent);
				finish();
			}
		});

		// Set of password strings and alert
		newpass = (EditText) findViewById(R.id.newpass);
		confirmpass = (EditText) findViewById(R.id.confirmpass);
		alert = (TextView) findViewById(R.id.alertpass);

		/**
		 * Change Password button
		 */
		changepass = (Button) findViewById(R.id.btchangepass);
		changepass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if ((!newpass.getText().toString().equals(""))
						&& newpass.getText().toString()
								.equals(confirmpass.getText().toString())) {
					NetAsync(view);
					// TODO: Add functionality to go back to RecentTasks screen
				} else if ((!newpass.getText().toString()
						.equals(confirmpass.getText().toString()))) {
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

	private class NetCheck extends AsyncTask {
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
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		}

		protected void onPostExecute(Boolean th) {
			if (th == true) {
				nDialog.dismiss();
				new ProcessRegister().execute();
			} else {
				nDialog.dismiss();
				alert.setText("Error in Network Connection");
			}
		}

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private class ProcessRegister extends AsyncTask {
		private ProgressDialog pDialog;
		String newpas, email;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DatabaseHandler db = new DatabaseHandler(getApplicationContext());
			HashMap user = new HashMap();
			user = db.getUserDetails();
			newpas = newpass.getText().toString();
			email = (String) user.get("email");
			pDialog = new ProgressDialog(ChangePassword.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected JSONObject doInBackground(String... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.chgPass(newpas, email);
			Log.d("Button", "Register");
			return json;
		}

		protected void onPostExecute(JSONObject json) {
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					alert.setText("");
					String res = json.getString(KEY_SUCCESS);
					String red = json.getString(KEY_ERROR);
					if (Integer.parseInt(res) == 1) {
						/**
						 * Dismiss the process dialog
						 **/
						pDialog.dismiss();
						alert.setText("Your Password is successfully changed.");
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

		@Override
		protected Object doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public void NetAsync(View view) {
		new NetCheck().execute();
	}
}
