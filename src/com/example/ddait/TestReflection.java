package com.example.ddait;

import java.util.ArrayList;

import com.ddai.lib.reflectiondb.DBManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestReflection extends Activity {

	Button bt1, bt2, bt3;
	DBManager dbm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_reflect);
		dbm = DBManager.getInstance(getApplicationContext());
		bt1 = (Button) findViewById(R.id.button1);
		bt2 = (Button) findViewById(R.id.button2);
		bt3 = (Button) findViewById(R.id.button3);

		bt1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StoreMessage model = new StoreMessage();
				model.request = "AAA";
				model.response = "BBBbbb";
				dbm.saveSM(model);
			}
		});

		bt2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StoreMessage model = new StoreMessage();
				model.request = "EEE";
				model.response = "FFF";
				dbm.saveSM(model);
			}
		});

		bt3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<StoreMessage> r = dbm.getSMS();
				for (StoreMessage sm : r) {
					Log.d("DDAI", "sm: " + sm.request + ":" + sm.response);
				}
			}
		});
	}
}
