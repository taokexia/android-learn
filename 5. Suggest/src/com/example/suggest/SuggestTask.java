package com.example.suggest;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Log;
import android.util.Xml;
public class SuggestTask implements Runnable {
    private static final String TAG = "SuggestTask";
    private final MainActivity suggest;
    private final String original;
    SuggestTask(MainActivity context, String original) {
        this.suggest = context;
        this.original = original;
    }
    public void run() {
        // ��ȡ��������ı��Ľ���
        List<String> suggestions = doSuggest(original);
        suggest.setSuggestions(suggestions);
    }
    /**
	    * ����Google Suggest API�����������ı�����һ�������б�
	    * ע�⣺���API����δ�õ�֧�֡�����������ã��볢��ʹ��Yahoo�ṩ��API
    * http://ff.search.yahoo.com/gossip?output=xml&command=WORD ��
    * http://ff.search.yahoo.com/gossip?output=fxjson&command=WORD
    */
    private List<String> doSuggest(String original) {
        List<String> messages = new LinkedList<String>();
        String error = null;
        HttpURLConnection con = null;
        Log.d(TAG, "doSuggest(" + original + ")");
        try {
            // ��������Ƿ��ж�
            if (Thread.interrupted())
                throw new InterruptedException();
            // �������Google API��RESTful��ѯ
            String q = URLEncoder.encode(original, "UTF-8");
            URL url = new URL(
                "http://google.com/complete/search?output=toolbar&q="
                + q);
            con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000 /* ������ */);
            con.setConnectTimeout(15000 /* ������ */);
            con.setRequestMethod("GET");
            con.addRequestProperty("Referer",
                                   "http://www.pragprog.com/book/eband4");
            con.setDoInput(true);
            // ������ѯ
            con.connect();
            // ��������Ƿ��ж�
            if (Thread.interrupted())
                throw new InterruptedException();
            // ��ȡ��ѯ���
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(con.getInputStream(), null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                if (eventType == XmlPullParser.START_TAG
                    && name.equalsIgnoreCase("suggestion")) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        if (parser.getAttributeName(i).equalsIgnoreCase(
                            "data")) {
                            messages.add(parser.getAttributeValue(i));
                        }
                    }
                }
                eventType = parser.next();
            }
            // ��������Ƿ��ж�
            if (Thread.interrupted())
                throw new InterruptedException();
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            error = suggest.getResources().getString(R.string.error)
                + " " + e.toString();
        } catch (XmlPullParserException e) {
            Log.e(TAG, "XmlPullParserException", e);
            error = suggest.getResources().getString(R.string.error)
                + " " + e.toString();
        } catch (InterruptedException e) {
            Log.d(TAG, "InterruptedException", e);
            error = suggest.getResources().getString(
                R.string.interrupted);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        // ��������˴��󣬾ͷ��ش�����
        if (error != null) {
            messages.clear();
            messages.add(error);
        }
        // ָ��û�в�ѯ���
        if (messages.size() == 0) {
            messages.add(suggest.getResources().getString(
                R.string.no_results));
        }
        // �������
        Log.d(TAG, " -> returned " + messages);
        return messages;
    }
}
