package com.example.roomieturn;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
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
	Button btnCreate;
	EditText house;
	TextView join;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_house_menu);

		/**
		 * Initialize GUI interface
		 */
		btnCreate = (Button) findViewById(R.id.button_create_house);
		house = (EditText) findViewById(R.id.editText_house_name);
		join = (TextView) findViewById(R.id.textView_join_house);

		/**
		 * Button click for create house
		 */
		btnCreate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Generate house and make usr the admin
				// taking him to the recent tasks screen
				if (!house.getText().toString().equals("")) {
					if (house.getText().toString().length() > 3) {
						// NetAsync(view);
					} else {
						showToast("Username should be minimum 4 characters");
					}
				} else {
					showToast("House Name is empty");
				}

			}
		});

		/**
		 * Button click for join house
		 */
		join.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Generate popup screen
				showSimplePopUp();

			}
		});

	}

	private void showToast(String msg) {
		Toast toast = Toast.makeText(getApplicationContext(), msg,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.show();
	}

	private void showSimplePopUp() {

		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
		helpBuilder.setTitle("Join House");
		final EditText input = new EditText(this);
		input.setSingleLine();
		input.setHint("House Admin E-mail");
		helpBuilder.setView(input);
		helpBuilder.setPositiveButton("Send",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO: Create intent to send email
						String adminEmail = input.getText().toString();
						if (!adminEmail.equals("")) {
							sendEmail(adminEmail);
						} else {
							showToast("Please enter an email address");
						}
					}
				});

		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();
	}

	private void sendEmail(String adminEmail) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{adminEmail});
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Join your house");
		emailIntent
				.putExtra(
						Intent.EXTRA_TEXT,
						"Testing: User wants to join your house."
								+ "Msg sent from android app roomieTurn. SHORTY, when you coming over?!?!");
		try {
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(HouseMenu.this,
					"There are no email clients installed.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.house_menu, menu);
		return true;
	}

}
