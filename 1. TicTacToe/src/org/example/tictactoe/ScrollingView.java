package org.example.tictactoe;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
/**
* ����Զ�����ͼ�������һ�����Ϲ����ı���ͼ��
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
    // ��ʼ��
    private void init(AttributeSet attrs, int defStyle) {
    	// �����Զ�����ͼ�������б�
        final TypedArray a = getContext().obtainStyledAttributes(
            attrs, R.styleable.ScrollingView, defStyle, 0);
        // ��ȡ����ͼ��
        if (a.hasValue(R.styleable.ScrollingView_scrollingDrawable)) {
            mBackground = a.getDrawable(
                R.styleable.ScrollingView_scrollingDrawable);
            mBackground.setCallback(this);
        }
        // ������Ҫ�����б�
        a.recycle();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // ��ȡ��ͼ�ĳߴ磨�������ڱ߾ࣩ
        int contentWidth = getWidth();
        int contentHeight = getHeight();
        // ���Ʊ���
        if (mBackground != null) {
            // �ñ�����ʵ����Ҫ�ĸ���
            int max = Math.max(mBackground.getIntrinsicHeight(),
                               mBackground.getIntrinsicWidth());
            mBackground.setBounds(0, 0, contentWidth * 4, contentHeight * 4);
            // ����ͼ��Ļ���λ��
            mScrollPos += 2;
            if (mScrollPos >= max) mScrollPos -= max;
            setTranslationX(-mScrollPos);
            setTranslationY(-mScrollPos);
            // ����ͼ��ָ���´�ˢ��ʱҲӦ������
            mBackground.draw(canvas);
            this.invalidate();
        }
    }
}
