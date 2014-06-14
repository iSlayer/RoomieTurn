package com.example.roomieturn;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
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

	// Initialize buttons and text views
	Button btnLogin;
	EditText inputEmail;
	EditText inputPassword;
	private TextView loginErrorMsg;
	TextView btnRegister;
	TextView passres;
	String email;
	String pass;

	/**
	 * Called when the activity is first created.
	 */
	private static String KEY_SUCCESS = "success";
	private static String KEY_UID = "uid";
	private static String KEY_USERNAME = "uname";
	private static String KEY_EMAIL = "email";
	private static String KEY_CREATED_AT = "created_at";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

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
		 * Password reset text view click event
		 */
		passres.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(),
						PasswordReset.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivityForResult(myIntent, 0);
//				finish();
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
//				finish();
			}
		});

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
		 * TESTING TO SKIP TO OTHER ACTIVITIES
		 */
		Button btnTest = (Button) findViewById(R.id.btn_test);
		btnTest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(),
						HouseMenu.class);
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
	private class NetCheck extends AsyncTask<String,String,Boolean> {
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
					urlc.setConnectTimeout(3000); // time in seconds currently 5
													// mins
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
	 * Async Task to get and send data to My SQL database through JSON response.
	 **/
	private class ProcessLogin extends AsyncTask<String, String, JSONObject> {
		private ProgressDialog pDialog;
//		String email, password;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			// Get user input data
            inputEmail = (EditText) findViewById(R.id.email);
            inputPassword = (EditText) findViewById(R.id.pword);
			//email = inputEmail.getText().toString();
			//password = inputPassword.getText().toString();
			
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
			JSONObject json = userFunction.loginUser(email, pass); //password);
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
						db.addUser(json_user.getString(KEY_EMAIL),
								json_user.getString(KEY_USERNAME),
								json_user.getString(KEY_UID),
								json_user.getString(KEY_CREATED_AT));
						
						/**
						 * If JSON array details are stored in SQlite it
						 * launches the User Panel.
						 **/
						Intent upanel = new Intent(getApplicationContext(),
								HouseMenu.class);
						upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						upanel.putExtra("email", email);
						pDialog.dismiss();
						startActivity(upanel);
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
}
