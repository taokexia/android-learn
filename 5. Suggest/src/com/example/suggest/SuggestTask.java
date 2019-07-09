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
        // 获取针对搜索文本的建议
        List<String> suggestions = doSuggest(original);
        suggest.setSuggestions(suggestions);
    }
    /**
	    * 调用Google Suggest API，根据搜索文本创建一个建议列表
	    * 注意：这个API可能未得到支持。如果它不管用，请尝试使用Yahoo提供的API
    * http://ff.search.yahoo.com/gossip?output=xml&command=WORD 或
    * http://ff.search.yahoo.com/gossip?output=fxjson&command=WORD
    */
    private List<String> doSuggest(String original) {
        List<String> messages = new LinkedList<String>();
        String error = null;
        HttpURLConnection con = null;
        Log.d(TAG, "doSuggest(" + original + ")");
        try {
            // 检查任务是否被中断
            if (Thread.interrupted())
                throw new InterruptedException();
            // 创建针对Google API的RESTful查询
            String q = URLEncoder.encode(original, "UTF-8");
            URL url = new URL(
                "http://google.com/complete/search?output=toolbar&q="
                + q);
            con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000 /* 毫秒数 */);
            con.setConnectTimeout(15000 /* 毫秒数 */);
            con.setRequestMethod("GET");
            con.addRequestProperty("Referer",
                                   "http://www.pragprog.com/book/eband4");
            con.setDoInput(true);
            // 启动查询
            con.connect();
            // 检查任务是否被中断
            if (Thread.interrupted())
                throw new InterruptedException();
            // 提取查询结果
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
            // 检查任务是否被中断
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
        // 如果发生了错误，就返回错误本身
        if (error != null) {
            messages.clear();
            messages.add(error);
        }
        // 指出没有查询结果
        if (messages.size() == 0) {
            messages.add(suggest.getResources().getString(
                R.string.no_results));
        }
        // 处理完毕
        Log.d(TAG, " -> returned " + messages);
        return messages;
    }
}
