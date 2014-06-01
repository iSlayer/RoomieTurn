package com.example.roomieturn;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.roomieturn.library.UserFunctions;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PasswordReset extends Activity {

	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	EditText email;
	TextView alert;
	Button resetpass;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_reset);
		Button login = (Button) findViewById(R.id.bktolog);
		login.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(),
						LoginActivity.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myIntent, 0);
				finish();
			}
		});

		email = (EditText) findViewById(R.id.forpas);
		alert = (TextView) findViewById(R.id.alert);
		resetpass = (Button) findViewById(R.id.respass);

		resetpass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				NetAsync(view);
				//TODO: Add functionality to go back to login screen
			}
		});
	}

	private class NetCheck extends AsyncTask {
		private ProgressDialog nDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			nDialog = new ProgressDialog(PasswordReset.this);
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
		String forgotpassword;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			forgotpassword = email.getText().toString();
			pDialog = new ProgressDialog(PasswordReset.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected JSONObject doInBackground(String... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.forPass(forgotpassword);
			return json;
		}

		protected void onPostExecute(JSONObject json) {
			/**
			 * Checks if the Password Change Process is sucesss
			 **/
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					alert.setText("");
					String res = json.getString(KEY_SUCCESS);
					String red = json.getString(KEY_ERROR);
					if (Integer.parseInt(res) == 1) {
						pDialog.dismiss();
						alert.setText("A recovery email is sent to you, see it for more details.");
					} else if (Integer.parseInt(red) == 2) {
						pDialog.dismiss();
						alert.setText("Your email does not exist in our database.");
					} else {
						pDialog.dismiss();
						alert.setText("Error occured in changing Password");
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