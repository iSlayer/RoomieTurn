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

public class Register extends Activity {

	/**
	 * JSON Response keywords
	 **/
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_USERNAME = "uname";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";
	private static String KEY_ERROR = "error";
	public static final String TAG = "Register";

	/**
	 * Defining layout items.
	 **/
	private EditText inputUsername;
	private EditText inputEmail;
	private EditText inputPassword;
	private EditText confirmPassword;
	private Button btnRegister;
	private TextView registerErrorMsg;
	public String uname, email, password;

	/**
	 * SharedPreferences setup
	 */
	public SharedPreferences sharePref;
	private static final String KEY_PREF = "RoomieTurn_app";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		sharePref = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);
		Log.d(TAG, "onCreate");

		/**
		 * Defining all layout items
		 **/
		inputUsername = (EditText) findViewById(R.id.uname);
		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.pword);
		confirmPassword = (EditText) findViewById(R.id.confirm_pass);
		btnRegister = (Button) findViewById(R.id.register);
		registerErrorMsg = (TextView) findViewById(R.id.register_error);

		/**
		 * Register Button click event
		 **/
		btnRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "btnRegister Click");
				uname = inputUsername.getText().toString();
				email = inputEmail.getText().toString();
				password = inputPassword.getText().toString();
				String confirmpass = confirmPassword.getText().toString();
				if ((!uname.equals("")) && (!password.equals(""))
						&& (!email.equals(""))) {
					if (uname.length() >= 3 && uname.length() <= 10) {
						if (password.equals(confirmpass)) {
							NetAsync(view);
						} else {
							showToast("Passwords Do Not Match");
						}
					} else {
						showToast("Username should be between 3 and 10 characters");
					}
				} else {
					showToast("One or more fields are empty");
				}
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
	 * Network Check to check whether Internet connection is working
	 **/
	private class NetCheck extends AsyncTask<String, String, Boolean> {
		private ProgressDialog nDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.d(TAG, "onPreExecute");
			nDialog = new ProgressDialog(Register.this);
			nDialog.setMessage("Loading..");
			nDialog.setTitle("Checking Network");
			nDialog.setIndeterminate(false);
			nDialog.setCancelable(true);
			nDialog.show();
		}

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
				new ProcessRegister().execute();
			} else {
				nDialog.dismiss();
				registerErrorMsg.setText("Error in Network Connection");
			}
		}
	}

	/**
	 * ProcessRegister: Register user and update the SQLITE Database
	 * 
	 */
	private class ProcessRegister extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Insure server is registering
			pDialog = new ProgressDialog(Register.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Registering ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.registerUser(email, uname, password);
			return json;
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				if (json != null && json.getString(KEY_SUCCESS) != null) {
					registerErrorMsg.setText("");
					String res = json.getString(KEY_SUCCESS);
					String red = json.getString(KEY_ERROR);

					if (Integer.parseInt(res) == 1) {
						pDialog.setTitle("Getting Data");
						pDialog.setMessage("Loading Info");
						registerErrorMsg.setText("Successfully Registered");
						DatabaseHandler db = new DatabaseHandler(
								getApplicationContext());
						JSONObject json_user = json.getJSONObject("user");

						/**
						 * Removes all the previous data in the SQlite database
						 **/
						UserFunctions logout = new UserFunctions();
						logout.logoutUser(getApplicationContext());

						/**
						 * Store JSON data into SQLITE database
						 **/
						db.addUser(json_user.getString(KEY_EMAIL),
								json_user.getString(KEY_USERNAME),
								json_user.getString(KEY_UID), null, null, null,
								json_user.getString(KEY_CREATED_AT));

						/**
						 * Store User preferences for auto login
						 */
						saveSharedPreferences(json_user.getString(KEY_EMAIL));

						// Create intent, back to login
						Intent myIntent = new Intent(getApplicationContext(),
								LoginActivity.class);
						myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						pDialog.dismiss();
						startActivity(myIntent);
						finish();
					} else if (Integer.parseInt(red) == 2) {
						pDialog.dismiss();
						registerErrorMsg.setText("User already exists");
					} else if (Integer.parseInt(red) == 3) {
						pDialog.dismiss();
						registerErrorMsg.setText("Invalid Email id");
					}
				} else {
					pDialog.dismiss();
					registerErrorMsg.setText("Error occured in registration");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void NetAsync(View view) {
		new NetCheck().execute();
	}

	private void saveSharedPreferences(String usr_email) {
		Log.i(TAG, "saveSharedPreferences");
		Editor editor = sharePref.edit();
		editor.putString(KEY_EMAIL, usr_email);
		editor.commit();
	}
}
