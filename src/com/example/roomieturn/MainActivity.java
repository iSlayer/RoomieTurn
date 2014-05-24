package com.example.roomieturn;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* This is where the code lies for the database.
		 * From here the user shall be allowed to:
		 * 1. Create a home
		 * 2. Login to a home
		 * 3. Just sign in
		 */
		
		// Create an intent to start Recent tasks menu
		Intent myIntent = new Intent(MainActivity.this, RecentTasks.class);
		MainActivity.this.startActivity(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
