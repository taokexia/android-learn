package org.example.tictactoe;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageButton;

public class Tile {
	public enum Owner {
		O, X, NEITHER, BOTH
	}
	private static int LEVEL_X = 0;
	private static int LEVEL_O = 1;
	private static int LEVEL_BLANK = 2;
	private static int LEVEL_AVAILABLE = 3;
	private static int LEVEL_TIE = 3;
	private final GameFragment mGame;
	private Owner mOwner = Owner.NEITHER;
	private View mView;
	private Tile mSubTiles[];
	public Tile(GameFragment game) {
		this.mGame = game;
	}
	public Owner getOwner() {
		return mOwner;
	}
	public void setOwner(Owner owenr) {
		this.mOwner = owenr;
	}
	public View getView() {
		return mView;
	}
	public void setView(View view) {
		this.mView = view;
	}
	public Tile[] getSubTiles() {
		return mSubTiles;
	}
	public void setSubTiles(Tile[] subTiles) {
		this.mSubTiles = subTiles;
	}
	// 动画效果
	public void animate() {
	    Animator anim = AnimatorInflater.loadAnimator(mGame.getActivity(),
	                                                  R.animator.tictactoe);
	    if (getView() != null) {
	        anim.setTarget(getView());
	        anim.start();
	    }
	}
	// 管理drawable状态的代码以及判断谁是占据者。
	public void updateDrawableState() {
	    if (mView == null) return;
	    int level = getLevel();
	    if (mView.getBackground() != null) {
	        mView.getBackground().setLevel(level);
	    }
	    if (mView instanceof ImageButton) {
	        Drawable drawable = ((ImageButton) mView).getDrawable();
	        drawable.setLevel(level);
	    }
	}
	private int getLevel() {
	    int level = LEVEL_BLANK;
	    switch (mOwner) {
	        case X:
	            level = LEVEL_X;
	            break;
	        case O: // 字母O
	            level = LEVEL_O;
	            break;
	        case BOTH:
	            level = LEVEL_TIE;
	            break;
	        case NEITHER:
	            level = mGame.isAvailable(this) ? LEVEL_AVAILABLE : LEVEL_BLANK;
	            break;
	    }
	    return level;
	}
	// 确定占据者
	public Owner findWinner() {
		// 如果已确定占据者，就返回它
		if (getOwner() != Owner.NEITHER)
			return getOwner();
		int totalX[] = new int[4];
		int totalO[] = new int[4];
		countCaptures(totalX, totalO);
		if (totalX[3] > 0) return Owner.X;
	    if (totalO[3] > 0) return Owner.O;
	    // 检查是否打成了平局
	    int total = 0;
	    for (int row = 0; row < 3; row++) {
	        for (int col = 0; col < 3; col++) {
	            Owner owner = mSubTiles[3 * row + col].getOwner();
	            if (owner != Owner.NEITHER) total++;
	        }
	        if (total == 9) return Owner.BOTH;
	    }
	    // 未被任何玩家占据
	    return Owner.NEITHER;
	}
	private void countCaptures(int totalX[], int totalO[]) {
	    int capturedX, capturedO;
	    // 检查是否有同一个玩家的3个棋子排成了一行
	    for (int row = 0; row < 3; row++) {
	        capturedX = capturedO = 0;
	        for (int col = 0; col < 3; col++) {
	            Owner owner = mSubTiles[3 * row + col].getOwner();
	            if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
	            if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
	        }
	        totalX[capturedX]++;
	        totalO[capturedO]++;
	    }
	    // 检查是否有同一个玩家的3个棋子排成了一列
	    for (int col = 0; col < 3; col++) {
	        capturedX = capturedO = 0;
	        for (int row = 0; row < 3; row++) {
	            Owner owner = mSubTiles[3 * row + col].getOwner();
	            if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
	            if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
	        }
	        totalX[capturedX]++;
	        totalO[capturedO]++;
	    }
	    // 检查是否有同一个玩家的3个棋子排成对角线
	    capturedX = capturedO = 0;
	    for (int diag = 0; diag < 3; diag++) {
	        Owner owner = mSubTiles[3 * diag + diag].getOwner();
	        if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
	        if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
	    }
	    totalX[capturedX]++;
	    totalO[capturedO]++;
	    capturedX = capturedO = 0;
	    for (int diag = 0; diag < 3; diag++) {
	        Owner owner = mSubTiles[3 * diag + (2 - diag)].getOwner();
	        if (owner == Owner.X || owner == Owner.BOTH) capturedX++;
	        if (owner == Owner.O || owner == Owner.BOTH) capturedO++;
	    }
	    totalX[capturedX]++;
	    totalO[capturedO]++;
	}
	// 形式判断
	public int evaluate() {
		switch (getOwner()) {
			case X:
				return 100;
			case O:
				return -100;
			case NEITHER:
				int total = 0;
				if (getSubTiles() != null) {
					for(int tile = 0; tile < 9; tile++) {
						total += getSubTiles()[tile].evaluate();
					}
					int totalX[] = new int[4];
	                int totalO[] = new int[4];
	                countCaptures(totalX, totalO);
	                total = total * 100 + totalX[1] + 2 * totalX[2] + 8 *
	                        totalX[3] - totalO[1] - 2 * totalO[2] - 8 * totalO[3];
				}
				return total;
		}
		return 0;
	}
	// 复制整个棋盘
	public Tile deepCopy() {
	    Tile tile = new Tile(mGame);
	    tile.setOwner(getOwner());
	    if (getSubTiles() != null) {
	        Tile newTiles[] = new Tile[9];
	        Tile oldTiles[] = getSubTiles();
	        for (int child = 0; child < 9; child++) {
	            newTiles[child] = oldTiles[child].deepCopy();
	        }
	        tile.setSubTiles(newTiles);
	    }
	    return tile;
	}
}
