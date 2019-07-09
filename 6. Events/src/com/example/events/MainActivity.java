package com.example.events;

import static com.example.events.Constants.TIME;
import static com.example.events.Constants.TITLE;
import static com.example.events.Constants.TABLE_NAME;
import static android.provider.BaseColumns._ID;
import static com.example.events.Constants.CONTENT_URI;

import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	 private static String[] FROM = { _ID, TIME, TITLE };
	private static int[] TO = { R.id.rowid, R.id.time, R.id.title, };
	private static String ORDER_BY = TIME + " DESC";
	private EventsData events;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addEvent("Hello, Android!");
	    Cursor cursor = getEvents();
	    showEvents(cursor);
	}
	
	private Cursor getEvents() {
		// 执行托管查询。活动会处理关闭工作，
	    // 并在必要时重新查询游标
		return managedQuery(CONTENT_URI, FROM, null, null, ORDER_BY);
	}
	
	private void addEvent(String string) {
		// 在数据源Events中插入一条新记录
	    // 你将执行类似的操作来删除和更新记录
	    ContentValues values = new ContentValues();
	    values.put(TIME, System.currentTimeMillis());
	    values.put(TITLE, string);
	    getContentResolver().insert(CONTENT_URI, values);
	}
	
//	private void showEvents(Cursor cursor) {
//	    // 将要显示的内容都塞到一个字符串中
//	    StringBuilder builder = new StringBuilder(
//	        "Saved events:\n");
//	    while (cursor.moveToNext()) {
//	        // 也可以使用getColumnIndexOrThrow()来获取列索引
//	        long id = cursor.getLong(0);
//	        long time = cursor.getLong(1);
//	        String title = cursor.getString(2);
//	        builder.append(id).append(": ");
//	        builder.append(time).append(": ");
//	        builder.append(title).append("\n");
//	    }
//	    // 显示到屏幕上
//	    TextView text = (TextView) findViewById(R.id.text);
//	    text.setText(builder);
//	}
	
	private void showEvents(Cursor cursor) {
	    // 设置数据绑定
	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
	    	R.layout.item, cursor, FROM, TO);
	    setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
