package org.example.tictactoe;

import java.util.HashSet;
import java.util.Set;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class GameFragment extends Fragment {
	static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
			R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
			R.id.large9,};
	static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
			R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
			R.id.small9,};
	private Tile mEntireBoard = new Tile(this);
	private Tile mLargeTiles[] = new Tile[9];
	private Tile mSmallTiles[][] = new Tile[9][9];
	private Tile.Owner mPlayer = Tile.Owner.X;
	private Set<Tile> mAvailable = new HashSet<Tile>();
	private int mLastLarge;
	private int mLastSmall;
	// 获得 handler 实例
	private Handler mHandler = new Handler();
	
	private int mSoundX, mSoundO, mSoundMiss, mSoundRewind;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		// 设备配置发生变化时保留这个片段
		setRetainInstance(true);
		initGame();
		mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundX = mSoundPool.load(getActivity(), R.raw.sergenious_movex, 1);
        mSoundO = mSoundPool.load(getActivity(), R.raw.sergenious_moveo, 1);
        mSoundMiss = mSoundPool.load(getActivity(), R.raw.erkanozan_miss, 1);
        mSoundRewind = mSoundPool.load(getActivity(), R.raw.joanne_rewind, 1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		View rootView = inflater.inflate(R.layout.large_board, container, false);
		initViews(rootView);
		updateAllTiles();
		return rootView;
	}

	// 初始化游戏
	public void initGame() {
		Log.d("UT3", "init game");
		mEntireBoard = new Tile(this);
		// 创建所有的格子
	    for (int large = 0; large < 9; large++) {
	        mLargeTiles[large] = new Tile(this);
	        for (int small = 0; small < 9; small++) {
	            mSmallTiles[large][small] = new Tile(this);
	        }
	        mLargeTiles[large].setSubTiles(mSmallTiles[large]);
	    }
	    mEntireBoard.setSubTiles(mLargeTiles);
	    // 设置先下棋子的玩家可下的格子
	    mLastSmall = -1;
	    mLastLarge = -1;
	    setAvailableFromLastMove(mLastSmall);
	}
	// 初始化视图
	private void initViews(View rootView) {
		mEntireBoard.setView(rootView);
		for (int large = 0; large < 9; large++) {
	        View outer = rootView.findViewById(mLargeIds[large]);
	        mLargeTiles[large].setView(outer);
	        for (int small = 0; small < 9; small++) {
	            ImageButton inner = (ImageButton) outer.findViewById
	                (mSmallIds[small]);
	            final int fLarge = large;
	            final int fSmall = small;
	            final Tile smallTile = mSmallTiles[large][small];
	            smallTile.setView(inner);
	            inner.setOnClickListener(new View.OnClickListener() {
	                @Override
	                public void onClick(View view) {
	                	smallTile.animate();
	                    if (isAvailable(smallTile)) {
	                    	mSoundPool.play(mSoundX, mVolume, mVolume, 1, 0, 1f);
	                        makeMove(fLarge, fSmall);
	                        think();
	                    } else {
	                        mSoundPool.play(mSoundMiss, mVolume, mVolume, 1, 0, 1f);
	                    }
	                }
	            });
	        }
	    }
	}
	// 模拟思考
	private void think() {
		((GameActivity) getActivity()).startThinking();
		mHandler.postDelayed(new Runnable () {
			@Override
			public void run() {
				if (getActivity() == null) return;
				if (mEntireBoard.getOwner() == Tile.Owner.NEITHER) {
					int[] move = new int[2];
					pickMove(move);
					if (move[0] != -1 && move[1] != -1) {
	                    switchTurns();
	                    mSoundPool.play(mSoundO, mVolume, mVolume, 1, 0, 1f);
	                    makeMove(move[0], move[1]);
	                    switchTurns();
	                }
				}
				((GameActivity) getActivity()).stopThinking();
			}
		}, 1000);
	}
	// 下棋
	private void pickMove(int move[]) {
		Tile.Owner opponent = mPlayer == Tile.Owner.X ? Tile.Owner.O : Tile
		        .Owner.X;
		    int bestLarge = -1;
		    int bestSmall = -1;
		    int bestValue = Integer.MAX_VALUE;
		    for (int large = 0; large < 9; large++) {
		        for (int small = 0; small < 9; small++) {
		            Tile smallTile = mSmallTiles[large][small];
		            if (isAvailable(smallTile)) {
		                // 尝试下棋并评估得到的棋局的得分
		                Tile newBoard = mEntireBoard.deepCopy();
		                newBoard.getSubTiles()[large].getSubTiles()[small]
		                    .setOwner(opponent);
		                int value = newBoard.evaluate();
		                Log.d("UT3",
		                      "Moving to " + large + ", " + small + " gives value " +
		                      "" + value
		                     );
		                if (value < bestValue) {
		                    bestLarge = large;
		                    bestSmall = small;
		                    bestValue = value;
		                }
		            }
		        }
		    }
		    move[0] = bestLarge;
		    move[1] = bestSmall;
		    Log.d("UT3", "Best move is " + bestLarge + ", " + bestSmall);
	}
	// 将棋下到格子中
	private void makeMove(int large, int small) {
		mLastLarge = large;
	    mLastSmall = small;
	    Tile smallTile = mSmallTiles[large][small];
	    Tile largeTile = mLargeTiles[large];
	    smallTile.setOwner(mPlayer);
	    setAvailableFromLastMove(small);
	    Tile.Owner oldWinner = largeTile.getOwner();
	    Tile.Owner winner = largeTile.findWinner();
	    if (winner != oldWinner) {
	    	largeTile.animate();
	        largeTile.setOwner(winner);
	    }
	    winner = mEntireBoard.findWinner();
	    mEntireBoard.setOwner(winner);
	    updateAllTiles();
	    if (winner != Tile.Owner.NEITHER) {
	        ((GameActivity)getActivity()).reportWinner(winner);
	    }
	}
	// 让另一个玩家接着下
	private void switchTurns() {
		mPlayer = mPlayer == Tile.Owner.X ? Tile.Owner.O : Tile.Owner.X;
	}
	// 重新开始游戏
	public void restartGame() {
	    initGame();
	    initViews(getView());
	    updateAllTiles();
	}
	// 计算可下棋的格子
	private void clearAvailable() {
	    mAvailable.clear();
	}
	private void addAvailable(Tile tile) {
		tile.animate();
	    mAvailable.add(tile);
	}
	public boolean isAvailable(Tile tile) {
	    return mAvailable.contains(tile);
	}
	private void setAvailableFromLastMove(int small) {
	    clearAvailable();
	    // 让目标小棋盘中所有空格子都可下棋
	    if (small != -1) {
	        for (int dest = 0; dest < 9; dest++) {
	            Tile tile = mSmallTiles[small][dest];
	            if (tile.getOwner() == Tile.Owner.NEITHER)
	                addAvailable(tile);
	        }
	    }
	    // 如果目标小棋盘没有空格子，则令整个棋盘的所有空格子都可下棋
	    if (mAvailable.isEmpty()) {
	        setAllAvailable();
	    }
	}
	private void setAllAvailable() {
	    for (int large = 0; large < 9; large++) {
	        for (int small = 0; small < 9; small++) {
	            Tile tile = mSmallTiles[large][small];
	            if (tile.getOwner() == Tile.Owner.NEITHER)
	                addAvailable(tile);
	        }
	    }
	}
	// /** 创建包含游戏状态的字符串 */
	public String getState() {
	    StringBuilder builder = new StringBuilder();
	    builder.append(mLastLarge);
	    builder.append(',');
	    builder.append(mLastSmall);
	    builder.append(',');
	    for (int large = 0; large < 9; large++) {
	        for (int small = 0; small < 9; small++) {
	            builder.append(mSmallTiles[large][small].getOwner().name());
	            builder.append(',');
	        }
	    }
	    return builder.toString();
	}
	/** 根据给定的字符串恢复游戏状态 */
	public void putState(String gameData) {
	    String[] fields = gameData.split(",");
	    int index = 0;
	    mLastLarge = Integer.parseInt(fields[index++]);
	    mLastSmall = Integer.parseInt(fields[index++]);
	    for (int large = 0; large < 9; large++) {
	        for (int small = 0; small < 9; small++) {
	            Tile.Owner owner = Tile.Owner.valueOf(fields[index++]);
	            mSmallTiles[large][small].setOwner(owner);
	        }
	    }
	    setAvailableFromLastMove(mLastSmall);
	    updateAllTiles();
	}
	// 更新所有格子的drawable状态
	private void updateAllTiles() {
	    mEntireBoard.updateDrawableState();
	    for (int large = 0; large < 9; large++) {
	        mLargeTiles[large].updateDrawableState();
	        for (int small = 0; small < 9; small++) {
	        	mSmallTiles[large][small].updateDrawableState();
	        }
	    }
	}
}
