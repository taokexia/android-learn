package com.example.events;
import android.net.Uri;
import android.provider.BaseColumns;

public class Constants implements BaseColumns{
	public static final String TABLE_NAME = "events";
	// ���ݿ��е���
	public static final String TIME = "time";
	public static final String TITLE = "title";
	public static final String AUTHORITY = "com.example.events";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
}
