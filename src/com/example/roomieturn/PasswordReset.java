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

import com.example.roomieturn.library.UserFunctions;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PasswordReset extends Activity {

	/**
	 * Defining layout items.
	 **/
	public static final String TAG = "PassReset";
	private static String KEY_SUCCESS = "success";
	private static String KEY_ERROR = "error";
	private EditText email;
	public TextView alert;
	private Button resetpass;
	public String forgotpassword;

	/**
	 * SharedPreferences setup
	 */
	public SharedPreferences sharePref;
	private static final String KEY_EMAIL = "email";
	private static final String KEY_PREF = "RoomieTurn_app";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_reset);
		sharePref = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);

		/**
		 * Defining all layout items
		 **/
		email = (EditText) findViewById(R.id.forpas);
		alert = (TextView) findViewById(R.id.alert);
		resetpass = (Button) findViewById(R.id.respass);

		/**
		 * Reset Password button click
		 **/
		resetpass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				forgotpassword = email.getText().toString();
				if (!forgotpassword.equals("")) {
					NetAsync(view);
				} else {
					showToast("Email Field is Empty");
				}
			}
		});

		/**
		 * If SharedPreferences auto login
		 */
		loadSharedPreferences();
	}

	/**
	 * showToast will display user input errors
	 * 
	 * @param msg
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
			nDialog = new ProgressDialog(PasswordReset.this);
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
				new ProcessResetPass().execute();
			} else {
				nDialog.dismiss();
				alert.setText("Error in Network Connection");
			}
		}
	}

	private class ProcessResetPass extends
			AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Display dialog to user
			pDialog = new ProgressDialog(PasswordReset.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Getting Data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.forPass(forgotpassword);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			/**
			 * Checks if the Password Change Process is success
			 **/
			try {
				if (json.getString(KEY_SUCCESS) != null) {
					alert.setText("");
					String res = json.getString(KEY_SUCCESS);
					String red = json.getString(KEY_ERROR);
					if (Integer.parseInt(res) == 1) {
						pDialog.dismiss();
						alert.setText("A recovery email is sent to you, see it for more details.");
						saveSharedPreferences(forgotpassword);
						

						Intent myIntent = new Intent(getApplicationContext(),
									LoginActivity.class);
						myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(myIntent);
						finish();
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
	}

	public void NetAsync(View view) {
		new NetCheck().execute();
	}

	private void loadSharedPreferences() {
		Log.i(TAG, "loadSharedPreferences");
		if (sharePref.contains(KEY_EMAIL)) {
			Log.i(TAG, "load shared email and pass");
			email.setText(sharePref.getString(KEY_EMAIL, ""));
		}
	}

	private void saveSharedPreferences(String usr_email) {
		Log.i(TAG, "saveSharedPreferences");
		Editor editor = sharePref.edit();
		editor.putString(KEY_EMAIL, usr_email);
		editor.commit();
	}
}