package com.app.VideoView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.app.Ui.FullScreenVideo;

/**
 * 自动全屏的VideoView
 */
public class FullScreenVideoView extends VideoView {

    //project yewu
    public int lookCurrentTotal;//试看次数
    public int videoTime;//视频时长
    public int lookAreaTotalSecond;//试看时长

    private int videoWidth;
    private int videoHeight;

    public FullScreenVideoView(Context context) {
        super(context);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullScreenVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int width = getDefaultSize(videoWidth, widthMeasureSpec);
//		int height = getDefaultSize(videoHeight, heightMeasureSpec);
//		if (videoWidth > 0 && videoHeight > 0) {
//			if (videoWidth * height > width * videoHeight) {
//				height = width * videoHeight / videoWidth;
//			} else if (videoWidth * height < width * videoHeight) {
//				width = height * videoWidth / videoHeight;
//			}
//		}
//		setMeasuredDimension(width, height);
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

//	public int getVideoWidth() {
//		return videoWidth;
//	}
//
//	public void setVideoWidth(int videoWidth) {
//		this.videoWidth = videoWidth;
//	}
//
//	public int getVideoHeight() {
//		return videoHeight;
//	}
//
//	public void setVideoHeight(int videoHeight) {
//		this.videoHeight = videoHeight;
//	}

    private PlayPauseListener mListener;

    public void setPlayPauseListener(PlayPauseListener listener) {
        mListener = listener;
    }

    @Override
    public void pause() {
        super.pause();
        if (mListener != null) {
            mListener.onPause();
        }
    }

    @Override
    public void start() {
        super.start();
        if (mListener != null) {
            mListener.onPlay();
        }
    }

    public interface PlayPauseListener {
        void onPlay();

        void onPause();
    }

}
