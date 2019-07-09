package org.example.tictactoe; // 定义了包名。为方便访问以及避免名称冲突，将Java源文件收集到了包中。包名必须与目录名匹配。
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle; // 使用 Android 框架提供的标准包。

public class MainActivity extends Activity {
	MediaPlayer mMediaPlayer;
    // 开始是方法 onCreate() ，它是活动生命周期的一部分，在活动创建时被调用。
    // @Override 指出这个方法最初是在 Activity 类中定义的，但我们将给它提供新的定义。在新定义中，首先会调用旧定义。
    @Override
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 使用activity_main.xml定义的XML布局填充活动的内容，用来演示如何使用前面声明的XML
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
