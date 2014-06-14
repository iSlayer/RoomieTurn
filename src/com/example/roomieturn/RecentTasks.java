package com.example.roomieturn;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class RecentTasks extends Activity {

	// Initialize test button to change pass
	Button changePass;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_tasks);
		
		changePass = (Button) findViewById(R.id.btn_changePass);
		changePass.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(getApplicationContext(),
						ChangePassword.class);

				/**
				 * Close all views before launching Registered screen
				 **/
				myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(myIntent);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recent_tasks, menu);
		return true;
	}

}
