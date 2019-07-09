package org.example.tictactoe; // �����˰�����Ϊ��������Լ��������Ƴ�ͻ����JavaԴ�ļ��ռ����˰��С�����������Ŀ¼��ƥ�䡣
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle; // ʹ�� Android ����ṩ�ı�׼����

public class MainActivity extends Activity {
	MediaPlayer mMediaPlayer;
    // ��ʼ�Ƿ��� onCreate() �����ǻ�������ڵ�һ���֣��ڻ����ʱ�����á�
    // @Override ָ���������������� Activity ���ж���ģ������ǽ������ṩ�µĶ��塣���¶����У����Ȼ���þɶ��塣
    @Override
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // ʹ��activity_main.xml�����XML������������ݣ�������ʾ���ʹ��ǰ��������XML
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.a_guy_1_epicbuilduploop);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }
}
