package de.uulm.graphicalpasswords;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startPassGo(View view){
		Intent intent = new Intent(this, de.uulm.graphicalpasswords.passgo.PassGoActivity.class);
		startActivity(intent);
	}
	
//	public void startUYI(View view){
//		Intent intent = new Intent(this, de.uulm.graphicalpasswords.uyi.UYIMainActivity.class);
//		startActivity(intent);
//	}
//	
//	public void startTAPI(View view){
//		Intent intent = new Intent(this, de.uulm.graphicalpasswords.tapi.TAPIMainActivity.class);
//		startActivity(intent);
//	}
//	
//	public void startPIN(View view){
//		Intent intent = new Intent(this, de.uulm.graphicalpasswords.pin.PINMainActivity.class);
//		startActivity(intent);
//	}
}
