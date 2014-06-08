package com.example.roomieturn;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HouseMenu extends Activity {

	/**
	 * Initialize variables
	 */
	Button btnCreate;
	EditText houseName;
	EditText newPass;
	EditText confirmPass;
	TextView join;

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

		/**
		 * Button click for create house
		 */
		btnCreate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Generate house and make usr the admin
				// taking him to the recent tasks screen
				if (!houseName.getText().toString().equals("")
						&& !newPass.getText().toString().equals("")
						&& newPass.getText().toString()
								.equals(confirmPass.getText().toString())) {
					if (houseName.getText().toString().length() >= 3) {
						// NetAsync(view);
					} else {
						showToast("House name should be minimum 3 characters");
					}
				} else {
					if (houseName.getText().toString().equals("")) {
						showToast("House Name is Empty");
					} else if (newPass.getText().toString().equals("")) {
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
			public void onClick(View v) {
				// TODO Generate pop-up screen
				showDialog();
			}
		});

	}

	private void showToast(String msg) {
		Toast toast = Toast.makeText(getApplicationContext(), msg,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.show();
	}

	private void showDialog() {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout = inflater.inflate(R.layout.dialog_join_house,
				(ViewGroup) findViewById(R.id.root));
		final EditText houseCode = (EditText) layout
				.findViewById(R.id.houseName);
		final EditText housePass = (EditText) layout
				.findViewById(R.id.housePass);
		final TextView error = (TextView) layout
				.findViewById(R.id.TextView_PwdProblem);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Join House");
		builder.setView(layout);

		builder.setPositiveButton("Send",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						if (!houseCode.getText().toString().equals("")
								&& !housePass.getText().toString().equals("")) {
							// TODO: Authenticate house code and pass
							// get house code pass
							Boolean correctPass = false; // returnHouseCodePass();
							if (correctPass) {
								// TODO: Connect user to household & create
								// authentication tokens
							} else {
								error.setText("House Password Incorrect");
							}
						} else {
							if(houseCode.getText().toString().equals("")){
								error.setText("Please Enter House Code");
							}else{
								error.setText("Please Enter House Password");
							}
						}
					}
				});

		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = builder.create();
		helpDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.house_menu, menu);
		return true;
	}

}
