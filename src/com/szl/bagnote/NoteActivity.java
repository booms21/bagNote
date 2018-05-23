package com.szl.bagnote;

import com.example.kff.R;

import android.app.Activity;

import android.content.ContentValues;
import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

public class NoteActivity extends Activity {
	private SQLiteDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		final EditText title = (EditText) findViewById(R.id.title1);

		final EditText content = (EditText) findViewById(R.id.content1);

		Button bn = (Button) findViewById(R.id.save);
		Button bn2 = (Button) findViewById(R.id.delete);
		Intent intent = getIntent();
		final String id = intent.getStringExtra("id");

		final String title1 = intent.getStringExtra("title");
		final String content1 = intent.getStringExtra("content");

		title.setText(title1);
		content.setText(content1);
		//±£´æºÍÉ¾³ý°´Å¥µÄµã»÷¼àÌý
		bn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				updateData(id, title, content);
			}
		});
		bn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				deleteData(id);
			}
		});

	}

	private void deleteData(String id) {

		try {

			db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/data.db3", null);

			String[] id1 = new String[1];
			id1[0] = id;
			db.delete("note", "_id=?", id1);
		} catch (SQLiteException se) {
			Toast.makeText(this, "É¾³ýÊ§°Ü£¬´íÎó£º" + se, Toast.LENGTH_LONG).show();
		}
		Toast.makeText(this, "É¾³ý³É¹¦", Toast.LENGTH_LONG).show();
		HomeActivity.instance.finish();
		finish();

		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}

	private void updateData(String id, EditText title, EditText content) {
		// TODO Auto-generated method stub

		try {
			db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/data.db3", null);

			ContentValues values = new ContentValues();
			values.put("note_title", title.getText().toString());
			values.put("note_content", content.getText().toString());
			String[] id1 = new String[1];
			id1[0] = id;
			Log.d("d", title.getText().toString() + "++++++" + id1.toString());
			//Ö´ÐÐÐÞ¸Ä¼ÇÂ¼
			int result = db.update("note", values, "_id=?", id1);

			if (result <= 0) {
				Toast.makeText(this, "±£´æÊ§°Ü£¬²»´æÔÚ¸Ã±Ê¼Ç", Toast.LENGTH_LONG).show();

			}

		} catch (SQLiteException se) {

			Toast.makeText(this, "±£´æÊ§°Ü£¬´íÎó£º" + se, Toast.LENGTH_LONG).show();

		}
		Toast.makeText(this, "ÒÑ±£´æ", Toast.LENGTH_LONG).show();
		HomeActivity.instance.finish();
		Intent intent = new Intent(NoteActivity.this, HomeActivity.class);
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
