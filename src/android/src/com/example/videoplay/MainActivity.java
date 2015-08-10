package com.example.videoplay;
import java.io.File;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;


public class MainActivity extends Activity {
	private SurfaceView sv;
	private Button btn_play;
	private MediaPlayer mediaPlayer;
	private SeekBar seekBar;
	private int currentPosition = 0;
	private boolean isPlaying;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		seekBar = (SeekBar) findViewById(R.id.seekBar);
		sv = (SurfaceView) findViewById(R.id.sv);

		btn_play = (Button) findViewById(R.id.btn_play);

		btn_play.setOnClickListener(click);

		// 为SurfaceHolder添加回调
		sv.getHolder().addCallback(callback);
		
		// 4.0版本之下需要设置的属性
		// 设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到界面
		// sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		// 为进度条添加进度更改事件
		seekBar.setOnSeekBarChangeListener(change);
	}

	private Callback callback = new Callback() {
		// SurfaceHolder被修改的时候回调
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				currentPosition = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (currentPosition > 0) {
				// 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
				play(currentPosition);
				currentPosition = 0;
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

	};

	private OnSeekBarChangeListener change = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// 当进度条停止修改的时候触发
			// 取得当前进度条的刻度
			int progress = seekBar.getProgress();
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				// 设置当前播放的位置
				mediaPlayer.seekTo(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {

		}
	};

	private View.OnClickListener click = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btn_play:
				play(0);
				break;
			default:
				break;
			}
		}
	};


	/*
	 * 停止播放
	 */
	protected void stop() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			btn_play.setEnabled(true);
			isPlaying = false;
		}
	}

	/**
	 * 开始播放
	 * 
	 * @param msec 播放初始位置    
	 */
	protected void play(final int msec) {
		// 获取视频文件地址
		
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 设置播放的视频源
			//mediaPlayer.setDataSource("/sdcard/2.mp4");
			mediaPlayer.setDataSource("http://182.92.175.58:8080/Products/2/Medias/1/source.mp4");
			
			// 设置显示视频的SurfaceHolder
			mediaPlayer.setDisplay(sv.getHolder());
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
					// 按照初始位置播放
					mediaPlayer.seekTo(msec);
					// 设置进度条的最大进度为视频流的最大播放时长
					seekBar.setMax(mediaPlayer.getDuration());
					// 开始线程，更新进度条的刻度
					new Thread() {

						@Override
						public void run() {
							try {
								isPlaying = true;
								while (isPlaying) {
									int current = mediaPlayer
											.getCurrentPosition();
									seekBar.setProgress(current);
									
									sleep(500);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();

					btn_play.setEnabled(false);
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// 在播放完毕被回调
					btn_play.setEnabled(true);
				}
			});

			mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// 发生错误重新播放
					play(0);
					isPlaying = false;
					return false;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
