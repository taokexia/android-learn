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
		// ������������������3��������һ�� inflater ���󣨿����ڽ�XMLת��Ϊ��ͼ����һ��ָ�������������Լ�һЩ�����״̬��
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		// ��������Ӵ���ť�Ĵ��롭��
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
						// �������Ϊ��
					}
				});
				mDialog = builder.show();
			}
		});
		// ����Ϸ��ť
		View newButton = rootView.findViewById(R.id.new_button);
		// ������Ϸ��ť
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
	    // ���About�Ի���δ�رգ��ͽ���ر�
	    if (mDialog != null)
	        mDialog.dismiss();
	}
}