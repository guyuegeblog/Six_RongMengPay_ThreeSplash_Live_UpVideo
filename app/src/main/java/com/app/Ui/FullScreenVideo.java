package com.app.Ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FullScreenVideo extends VideoView implements OnBufferingUpdateListener {
	private int mVideoWidth;
	private int mVideoHeight;
	private PlayPauseListener mListener;

	public FullScreenVideo(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

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


	public FullScreenVideo(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FullScreenVideo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}


	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
		super.setOnPreparedListener(l);
	}

	public interface PlayPauseListener {
		void onPlay();
		void onPause();
	}
}
