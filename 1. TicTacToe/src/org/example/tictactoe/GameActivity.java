package org.example.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;


public class GameActivity extends Activity {
	public static final String KEY_RESTORE = "key_store";
	public static final String PREF_RESTORE = "pref_store";
	private GameFragment mGameFragment;
	private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
        // 在这里恢复游戏……
		mGameFragment = (GameFragment) getFragmentManager().findFragmentById(R.id.fragment_game);
		boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
		if (restore) {
			String gameData = getPreferences(MODE_PRIVATE).getString(PREF_RESTORE, null);
			if (gameData != null) {
				mGameFragment.putState(gameData);
			}
		}
		Log.d("UT3", "restore = " + restore);	
	}
	@Override
	protected void onResume() {
	    super.onResume();
	    mMediaPlayer = MediaPlayer.create(this, R.raw.frankum_loop001e);
	    mMediaPlayer.setLooping(true);
	    mMediaPlayer.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
	    mMediaPlayer.stop();
	    mMediaPlayer.reset();
	    mMediaPlayer.release();
		String gameData = mGameFragment.getState();
		getPreferences(MODE_PRIVATE).edit().putString(PREF_RESTORE, gameData).commit();
		Log.d("UT3", "state = " + gameData);
	}
	
	// 重启游戏
	public void restartGame() {
		mGameFragment.restartGame();
	}
	// 宣布获胜方
	public void reportWinner(final Tile.Owner winner) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
	        mMediaPlayer.stop();
	        mMediaPlayer.reset();
	        mMediaPlayer.release();
	    }
		builder.setMessage(getString(R.string.declare_winner, winner));
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				finish();
			}
		});
		final Dialog dialog = builder.create();
	    mHandler.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	            mMediaPlayer = MediaPlayer.create(GameActivity.this,
	                                              winner == Tile.Owner.X ? R.raw.oldedgar_winner
	                                              : winner == Tile.Owner.O ? R.raw.notr_loser
	                                              : R.raw.department64_draw
	                                             );
	            mMediaPlayer.start();
	            dialog.show();
	        }
	    }, 500);
	    // 将棋盘重置为初始状态
	    mGameFragment.initGame();
	}
	// 调用加载视图
	public void startThinking() {
        View thinkView = findViewById(R.id.thinking);
        thinkView.setVisibility(View.VISIBLE);
    }
	// 隐藏加载视图
    public void stopThinking() {
        View thinkView = findViewById(R.id.thinking);
        thinkView.setVisibility(View.GONE);
    }
}
