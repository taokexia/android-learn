package org.example.tictactoe;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
/**
* 这个自定义视图负责绘制一幅不断滚动的背景图像
*/
public class ScrollingView extends View {
	private Drawable mBackground;
    private int mScrollPos;
    
    public ScrollingView(Context context) {
        super(context);
        init(null, 0);
    }
    public ScrollingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public ScrollingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    // 初始化
    private void init(AttributeSet attrs, int defStyle) {
    	// 加载自定义视图的属性列表
        final TypedArray a = getContext().obtainStyledAttributes(
            attrs, R.styleable.ScrollingView, defStyle, 0);
        // 获取背景图像
        if (a.hasValue(R.styleable.ScrollingView_scrollingDrawable)) {
            mBackground = a.getDrawable(
                R.styleable.ScrollingView_scrollingDrawable);
            mBackground.setCallback(this);
        }
        // 不再需要属性列表
        a.recycle();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取视图的尺寸（不包括内边距）
        int contentWidth = getWidth();
        int contentHeight = getHeight();
        // 绘制背景
        if (mBackground != null) {
            // 让背景比实际需要的更大
            int max = Math.max(mBackground.getIntrinsicHeight(),
                               mBackground.getIntrinsicWidth());
            mBackground.setBounds(0, 0, contentWidth * 4, contentHeight * 4);
            // 调整图像的绘制位置
            mScrollPos += 2;
            if (mScrollPos >= max) mScrollPos -= max;
            setTranslationX(-mScrollPos);
            setTranslationY(-mScrollPos);
            // 绘制图像并指出下次刷新时也应绘制它
            mBackground.draw(canvas);
            this.invalidate();
        }
    }
}
