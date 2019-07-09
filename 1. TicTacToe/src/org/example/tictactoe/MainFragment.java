package org.example.tictactoe;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment {
	private AlertDialog mDialog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
		// 启动方法，方法接受3个参数：一个 inflater 对象（可用于将XML转换为视图）、一个指向父容器的引用以及一些保存的状态。
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		// 在这里添加处理按钮的代码……
		View aboutButton = rootView.findViewById(R.id.about_button);
		aboutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.about_title);
				builder.setMessage(R.string.about_text);
				builder.setCancelable(false);
				builder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						// 这个方法为空
					}
				});
				mDialog = builder.show();
			}
		});
		// 新游戏按钮
		View newButton = rootView.findViewById(R.id.new_button);
		// 继续游戏按钮
		View continueButton = rootView.findViewById(R.id.continue_button);
		newButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View View) {
				Intent intent = new Intent(getActivity(), GameActivity.class);
				getActivity().startActivity(intent);
				
			}
		});
		continueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), GameActivity.class);
				intent.putExtra(GameActivity.KEY_RESTORE, true);
				getActivity().startActivity(intent);
			}
		});
		return rootView;
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    // 如果About对话框未关闭，就将其关闭
	    if (mDialog != null)
	        mDialog.dismiss();
	}
}