package com.szl.bagnote;

import com.example.kff.R;

import android.app.Activity;

import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

public class AddNoteActivity extends Activity {
	SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_addnote);
		Button bn = (Button) findViewById(R.id.add);
		bn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditText title = (EditText) findViewById(R.id.title);
				EditText content = (EditText) findViewById(R.id.content);

				insertData(title.getText().toString(), content.getText().toString());

			}
		});

	}

	private void insertData(String title, String content) {
		// TODO Auto-generated method stub

		try {
			db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/data.db3", null);
//在sqlite中添加记录
			db.execSQL("insert into note values(null,?,?)", new String[] { title, content });

		} catch (SQLiteException se) {

			Toast.makeText(this, "错误：" + se, Toast.LENGTH_LONG).show();

		}
		Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();
		HomeActivity.instance.finish();
		Intent intent = new Intent(AddNoteActivity.this, HomeActivity.class);
		startActivity(intent);
		finish();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db != null && db.isOpen()) {

			db.close();

		}

	}

}
