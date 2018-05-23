package com.szl.bagnote;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.kff.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class HomeActivity extends Activity {
	public static HomeActivity instance = null;
	private SQLiteDatabase db;
	private SimpleAdapter listItemAdapter;
	private ArrayList<HashMap<String, Object>> listItem = null;
	private ListView listview;
	//防止按钮频繁点击
	public static class Utils {
	    private static long lastClickTime;
	    public synchronized static boolean isFastClick() {
	        long time = System.currentTimeMillis();   
	        if ( time - lastClickTime < 500) {   
	            return true;   
	        }   
	        lastClickTime = time;   
	        return false;   
	    }
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home);
		instance = this;
//打开（没有则创建）sqlite数据文件
		db = SQLiteDatabase.openOrCreateDatabase(this.getFilesDir().toString() + "/data.db3", null);

		try {

			Cursor cursor = db.rawQuery("select * from note order by _id desc", null);

			if (cursor.getCount() == 0) {//如果查询不到笔记
				Toast.makeText(getApplicationContext(), "笔记记录为空,请添加笔记", Toast.LENGTH_LONG).show();

			}
			inflateList(cursor);

		} catch (SQLiteException se) {  //第一次使用软件因没有创建表出错则自动创建
			db.execSQL("create table note(_id integer primary key autoincrement," + "note_title varchar(20),"
					+ "note_content varchar(255))");

			Cursor cursor = db.rawQuery("select * from note", null);
			inflateList(cursor);
		}

	}

	private void inflateList(Cursor cursor) {
		final Builder builder = new AlertDialog.Builder(this);
		listview = (ListView) findViewById(R.id.list);
		listItem = new ArrayList<HashMap<String, Object>>();

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(1);
			int id = cursor.getInt(0);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("viewspot", name);
			map.put("viewspot2", id);
			listItem.add(map);

			// do something useful with these
			cursor.moveToNext();
		}
		listItemAdapter = new SimpleAdapter(getApplicationContext(), listItem, 
				R.layout.item, new String[] { "viewspot", "viewspot2" }, new int[] { R.id.viewspot, R.id.viewspot2 });
		listview.setAdapter(listItemAdapter);
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
			//	final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) arg2;
				// TODO Auto-generated method stub
				//提示窗口
				builder.setTitle("提示:");
				builder.setMessage("确定要删除此条笔记吗?");
				builder.setNegativeButton("取消", null);
				builder.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub				
						String id = listItem.get(position).get("viewspot2").toString();//通过位置得到id
						try {
							String[] id1 = new String[1];
							id1[0] = id;
							db.delete("note", "_id=?", id1);
						} catch (SQLiteException se) {
							Toast.makeText(getApplicationContext(), "删除失败" + se, Toast.LENGTH_LONG).show();
						}
						Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
						listItem.remove(position);//移除列表中的项
						listItemAdapter.notifyDataSetChanged();
					}
				}).show();
				return true;
			}
		});
	//当listview列表点击某一项
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				 if (Utils.isFastClick()) {
				        return ;
				    }
				// TODO Auto-generated method stub
				String id = listItem.get(arg2).get("viewspot2").toString();
				try {

					// Toast.makeText(getApplicationContext(), "正在读取...",
					// Toast.LENGTH_LONG).show();
					//数据库中查询内容
					Cursor cursor = db.rawQuery("select * from note where _id=" + id, null);
					cursor.moveToFirst();

					String name = cursor.getString(1);
					String content = cursor.getString(2).toString();
					cursor.close();

					Bundle data = new Bundle();
					data.putString("id", id);
					data.putString("title", name);
					data.putString("content", content);

					Intent intent = new Intent(HomeActivity.this, NoteActivity.class);
					intent.putExtras(data);

					startActivity(intent);
				} catch (SQLiteException se) {
					Toast.makeText(getApplicationContext(), "读取失败，错误：" + se.toString(), Toast.LENGTH_LONG).show();
				}
			
			}

		}

		);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (db != null && db.isOpen()) {

			db.close();

		}

	}
     //菜单显示
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(Menu.NONE, Menu.FIRST, 1, "添加笔记");
		menu.add(Menu.NONE, Menu.FIRST + 1, 2, "关于我们");
		menu.add(Menu.NONE, Menu.FIRST + 2, 3, "退出");
		return true;

	}
   //当菜单项点击
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case Menu.FIRST:
			Intent intent = new Intent(HomeActivity.this, AddNoteActivity.class);
			startActivity(intent);
			break;

		case Menu.FIRST + 1:

			new AlertDialog.Builder(this)

					.setTitle("关于我们")

					.setMessage("兜兜笔记\nby SZL 2015")

					.setPositiveButton("确定", null)

					.show();

			//Toast.makeText(this, "11111", Toast.LENGTH_LONG).show();

			break;

		case Menu.FIRST + 2:

			finish();          

		}

		return false;

	}

}
