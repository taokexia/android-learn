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
	// ��������
	private void initThreading() {
	    guiThread = new Handler();
	    suggThread = Executors.newSingleThreadExecutor();
	    // �������Ҫ���ȡ���鲢������Ļ
	    updateTask = new Runnable() {
	        public void run() {
	            // ��ȡ�����ı�
	            String original = origText.getText().toString().trim();
	            // ������ǰ�Ľ��飨����еĻ���
	            if (suggPending != null)
	                suggPending.cancel(true);
	            // ȷ�������������ı�
	            if (original.length() != 0) {
	                // ���û�֪����������ִ�в���
	                setText(R.string.working);
	                // ��ʼ��ȡ���飬�����ȴ����
	                try {
	                    SuggestTask suggestTask = new SuggestTask(
	                        MainActivity.this, // ָ��ǰ�������
	                        original // �����ı�
	                    );
	                    suggPending = suggThread.submit(suggestTask);
	                } catch (RejectedExecutionException e) {
	                    // �޷���ʼ������
	                    setText(R.string.error);
	                }
	            }
	        }
	    };
	}
	// �ֱ�ָ�򲼾��ļ�����ĸ����û�����Ԫ��
	private void findViews() {
	    origText = (EditText) findViewById(R.id.original_text);
	    suggList = (ListView) findViewById(R.id.result_list);
	    ebandText = (TextView) findViewById(R.id.eband_text);
	}
	/** �����б���ͼ�������� */
	private void setAdapters() {
	    items = new ArrayList<String>();
	    adapter = new ArrayAdapter<String>(this,
	                                       android.R.layout.simple_list_item_1, items);
	    suggList.setAdapter(adapter);
	}
	
	private void setListeners() {
	    // �����ı��仯������
	    TextWatcher textWatcher = new TextWatcher() {
	        public void beforeTextChanged(CharSequence s, int start,
	                                      int count, int after) {
	            /* ʲô������ */
	        }
	        public void onTextChanged(CharSequence s, int start,
	                                  int before, int count) {
	            queueUpdate(1000 /* ������ */);
	        }
	        public void afterTextChanged(Editable s) {
	            /* ʲô������ */
	        }
	    };
	    // ��������ָ��������
	    origText.addTextChangedListener(textWatcher);
	    // �����б�����������
	    OnItemClickListener clickListener = new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view,
	                                int position, long id) {
	            String query = (String) parent.getItemAtPosition(position);
	            doSearch(query);
	        }
	    };
	    // �������б�ָ��������
	    suggList.setOnItemClickListener(clickListener);
	    // ����վ�������óɿɵ����
	    ebandText.setMovementMethod(LinkMovementMethod.getInstance());
	}
	private void doSearch(String query) {
	    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
	    intent.putExtra(SearchManager.QUERY, query);
	    startActivity(intent);
	}

	/** ��������ӳٺ���и��� */
	private void queueUpdate(long delayMillis) {
		// ������ǰ�ĸ��£��������û�п�ʼ��
		guiThread.removeCallbacks(updateTask);
		// ����������ʲô��û�з������Ϳ�ʼ����
		guiThread.postDelayed(updateTask, delayMillis);
	}
	/** �޸���Ļ�ϵĽ����б�����һ���߳��е��ã� */
	public void setSuggestions(List<String> suggestions) {
		guiSetList(suggList, suggestions);
	}
	/** ��GUI�������޸Ķ�������GUI�߳��н��� */
	private void guiSetList(final ListView view,
	                        final List<String> list) {
	    guiThread.post(new Runnable() {
	        public void run() {
	            setList(list);
	        }
	    });
	}
	/** ��ʾһ����Ϣ */
	private void setText(int id) {
	    adapter.clear();
	    adapter.add(getResources().getString(id));
	}
	/** ��ʾ�����б� */
	private void setList(List<String> list) {
	    adapter.clear();
	    adapter.addAll(list);
	}
}
