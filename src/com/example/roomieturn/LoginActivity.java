package com.example.roomieturn;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.roomieturn.library.DatabaseHandler;
import com.example.roomieturn.library.UserFunctions;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends Activity {

	/**
	 * Defining layout items.
	 **/
	private Button btnLogin;
	private EditText inputEmail;
	private EditText inputPassword;
	private TextView loginErrorMsg;
	private TextView btnRegister;
	private TextView passres;
	public String email;
	public String pass;

	/**
	 * Database keywords
	 */
	public static final String TAG = "Login";
	private static final String KEY_SUCCESS = "success";
	private static final String KEY_UID = "uid";
	private static final String KEY_USERNAME = "uname";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_HOUSENAME = "house_name";
	private static final String KEY_HOUSECODE = "house_code";
	private static final String KEY_CREATED_AT = "created_at";

	/**
	 * SharedPreferences setup
	 */
	public SharedPreferences sharePref;
	private static final String KEY_PASSWORD = "password";
	private static final String KEY_PREF = "RoomieTurn_app";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		sharePref = getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE);

		/**
		 * Defining all layout items
		 **/
		inputEmail = (EditText) findViewById(R.id.email);
		inputPassword = (EditText) findViewById(R.id.pword);
		btnRegister = (TextView) findViewById(R.id.registerbtn);
		btnLogin = (Button) findViewById(R.id.login);
		loginErrorMsg = (TextView) findViewById(R.id.loginErrorMsg);
		passres = (TextView) findViewById(R.id.passres);

		/**
		 * Login button click event A Toast is set to alert when the Email and
		 * Password field is empty
		 **/
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				email = inputEmail.getText().toString();
				pass = inputPassword.getText().toString();
				if ((!email.equals("")) && (!pass.equals(""))) {
					NetAsync(view);
				} else if ((!email.equals(""))) {
					showToast("Password field empty");
				} else if ((!pass.equals(""))) {
					showToast("Email field empty");
				} else {
					showToast("Email and Password fields empty");
				}
			}
		});

		/**
		 * Register button click event
		 */
		btnRegister.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), Register.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myIntent, 0);
				finish();
			}
		});

		/**
		 * Password reset text view click event
		 */
		passres.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(),
						PasswordReset.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myIntent, 0);
				finish();
			}
		});

		/**
		 * If SharedPreferences auto login
		 */
		loadSharedPreferences();

		/**
		 * TESTING TO SKIP TO OTHER ACTIVITIES
		 */
		Button btnTest = (Button) findViewById(R.id.btn_test);
		btnTest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), HouseMenu.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(myIntent);
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
			nDialog = new ProgressDialog(LoginActivity.this);
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
				loginErrorMsg.setText("Error in Network Connection");
			}
		}
	}

	/**
	 * ProcessLogin Task to get and send data to My SQL database through JSON
	 * response.
	 **/
	private class ProcessLogin extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Display dialog
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setTitle("Contacting Servers");
			pDialog.setMessage("Logging in ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected JSONObject doInBackground(String... args) {
			UserFunctions userFunction = new UserFunctions();
			JSONObject json = userFunction.loginUser(email, pass);
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
						JSONObject json_user = json.getJSONObject("user");

						/**
						 * Clear all previous data in SQlite database.
						 **/
						UserFunctions logout = new UserFunctions();
						logout.logoutUser(getApplicationContext());

						/**
						 * Store JSON data into SQLITE database
						 **/
						db.addUser(json_user.getString(KEY_EMAIL),
								json_user.getString(KEY_USERNAME),
								json_user.getString(KEY_UID),
								json_user.getString(KEY_HOUSENAME),
								json_user.getString(KEY_HOUSECODE),
								json_user.getString(KEY_CREATED_AT));

						/**
						 * Store User preferences for auto login
						 */
						saveSharedPreferences(json_user.getString(KEY_EMAIL),
								pass);

						/**
						 * Check if user has a house
						 **/
						Log.i(TAG, "user: " + json_user);
						Intent myIntent;
						if (json_user.getString(KEY_HOUSECODE).equals("null")) {
							myIntent = new Intent(getApplicationContext(),
									HouseMenu.class);
						} else {
							myIntent = new Intent(getApplicationContext(),
									RecentTasks.class);
						}
						myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						pDialog.dismiss();
						startActivity(myIntent);
						finish();
					} else {
						pDialog.dismiss();
						loginErrorMsg.setText("Incorrect username/password");
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
		if (sharePref.contains(KEY_EMAIL) && sharePref.contains(KEY_PASSWORD)) {
			Log.i(TAG, "load shared email and pass");
			inputEmail.setText(sharePref.getString(KEY_EMAIL, ""));
			inputPassword.setText(sharePref.getString(KEY_PASSWORD, ""));
			this.btnLogin.performClick();
		} else if (sharePref.contains(KEY_EMAIL)) {
			Log.i(TAG, "load shared email");
			inputEmail.setText(sharePref.getString(KEY_EMAIL, ""));
		}
	}

	private void saveSharedPreferences(String usr_email, String usr_pass) {
		Log.i(TAG, "saveSharedPreferences");
		Editor editor = sharePref.edit();
		editor.putString(KEY_EMAIL, usr_email);
		editor.putString(KEY_PASSWORD, usr_pass);
		editor.commit();
	}
}
