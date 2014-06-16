package com.example.roomieturn;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class RecentTasks extends Activity {

	// Initialize test button to change pass
	Button changePass;
	Button logout;

	public static final String TAG = "RecentTasks";
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
	}

	private void clearSharedPreferences() {
		Log.i(TAG, "clearSharedPreferences");
		Editor editor = sharePref.edit();
		editor.remove(KEY_PASSWORD);
//		editor.clear(); // clear all stored data
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recent_tasks, menu);
		return true;
	}

}
