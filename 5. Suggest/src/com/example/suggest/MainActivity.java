package com.example.suggest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private EditText origText;
    private ListView suggList;
    private TextView ebandText;
    private Handler guiThread;
    private ExecutorService suggThread;
    private Runnable updateTask;
    private Future<?> suggPending;
    private List<String> items;
    private ArrayAdapter<String> adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initThreading();
        findViews();
        setListeners();
        setAdapters();
	}
	// 更新请求
	private void initThreading() {
	    guiThread = new Handler();
	    suggThread = Executors.newSingleThreadExecutor();
	    // 这个任务要求获取建议并更新屏幕
	    updateTask = new Runnable() {
	        public void run() {
	            // 获取搜索文本
	            String original = origText.getText().toString().trim();
	            // 撤销以前的建议（如果有的话）
	            if (suggPending != null)
	                suggPending.cancel(true);
	            // 确认输入了搜索文本
	            if (original.length() != 0) {
	                // 让用户知道程序正在执行操作
	                setText(R.string.working);
	                // 开始获取建议，但不等待结果
	                try {
	                    SuggestTask suggestTask = new SuggestTask(
	                        MainActivity.this, // 指向当前活动的引用
	                        original // 搜索文本
	                    );
	                    suggPending = suggThread.submit(suggestTask);
	                } catch (RejectedExecutionException e) {
	                    // 无法开始新任务
	                    setText(R.string.error);
	                }
	            }
	        }
	    };
	}
	// 分别指向布局文件定义的各个用户界面元素
	private void findViews() {
	    origText = (EditText) findViewById(R.id.original_text);
	    suggList = (ListView) findViewById(R.id.result_list);
	    ebandText = (TextView) findViewById(R.id.eband_text);
	}
	/** 设置列表视图的适配器 */
	private void setAdapters() {
	    items = new ArrayList<String>();
	    adapter = new ArrayAdapter<String>(this,
	                                       android.R.layout.simple_list_item_1, items);
	    suggList.setAdapter(adapter);
	}
	
	private void setListeners() {
	    // 定义文本变化监听器
	    TextWatcher textWatcher = new TextWatcher() {
	        public void beforeTextChanged(CharSequence s, int start,
	                                      int count, int after) {
	            /* 什么都不做 */
	        }
	        public void onTextChanged(CharSequence s, int start,
	                                  int before, int count) {
	            queueUpdate(1000 /* 毫秒数 */);
	        }
	        public void afterTextChanged(Editable s) {
	            /* 什么都不做 */
	        }
	    };
	    // 给搜索框指定监听器
	    origText.addTextChangedListener(textWatcher);
	    // 定义列表项点击监听器
	    OnItemClickListener clickListener = new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view,
	                                int position, long id) {
	            String query = (String) parent.getItemAtPosition(position);
	            doSearch(query);
	        }
	    };
	    // 给建议列表指定监听器
	    suggList.setOnItemClickListener(clickListener);
	    // 将网站链接设置成可点击的
	    ebandText.setMovementMethod(LinkMovementMethod.getInstance());
	}
	private void doSearch(String query) {
	    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
	    intent.putExtra(SearchManager.QUERY, query);
	    startActivity(intent);
	}

	/** 请求短暂延迟后进行更新 */
	private void queueUpdate(long delayMillis) {
		// 撤销以前的更新（如果它还没有开始）
		guiThread.removeCallbacks(updateTask);
		// 如果几毫秒后什么都没有发生，就开始更新
		guiThread.postDelayed(updateTask, delayMillis);
	}
	/** 修改屏幕上的建议列表（从另一个线程中调用） */
	public void setSuggestions(List<String> suggestions) {
		guiSetList(suggList, suggestions);
	}
	/** 对GUI的所有修改都必须在GUI线程中进行 */
	private void guiSetList(final ListView view,
	                        final List<String> list) {
	    guiThread.post(new Runnable() {
	        public void run() {
	            setList(list);
	        }
	    });
	}
	/** 显示一条消息 */
	private void setText(int id) {
	    adapter.clear();
	    adapter.add(getResources().getString(id));
	}
	/** 显示建议列表 */
	private void setList(List<String> list) {
	    adapter.clear();
	    adapter.addAll(list);
	}
}
