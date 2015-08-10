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

		// ΪSurfaceHolder��ӻص�
		sv.getHolder().addCallback(callback);
		
		// 4.0�汾֮����Ҫ���õ�����
		// ����Surface��ά���Լ��Ļ����������ǵȴ���Ļ����Ⱦ���潫�������͵�����
		// sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		// Ϊ��������ӽ��ȸ����¼�
		seekBar.setOnSeekBarChangeListener(change);
	}

	private Callback callback = new Callback() {
		// SurfaceHolder���޸ĵ�ʱ��ص�
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// ����SurfaceHolder��ʱ���¼��ǰ�Ĳ���λ�ò�ֹͣ����
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				currentPosition = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (currentPosition > 0) {
				// ����SurfaceHolder��ʱ����������ϴβ��ŵ�λ�ã������ϴβ���λ�ý��в���
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
			// ��������ֹͣ�޸ĵ�ʱ�򴥷�
			// ȡ�õ�ǰ�������Ŀ̶�
			int progress = seekBar.getProgress();
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				// ���õ�ǰ���ŵ�λ��
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
	 * ֹͣ����
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
	 * ��ʼ����
	 * 
	 * @param msec ���ų�ʼλ��    
	 */
	protected void play(final int msec) {
		// ��ȡ��Ƶ�ļ���ַ
		
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// ���ò��ŵ���ƵԴ
			//mediaPlayer.setDataSource("/sdcard/2.mp4");
			mediaPlayer.setDataSource("http://182.92.175.58:8080/Products/2/Medias/1/source.mp4");
			
			// ������ʾ��Ƶ��SurfaceHolder
			mediaPlayer.setDisplay(sv.getHolder());
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					mediaPlayer.start();
					// ���ճ�ʼλ�ò���
					mediaPlayer.seekTo(msec);
					// ���ý�������������Ϊ��Ƶ������󲥷�ʱ��
					seekBar.setMax(mediaPlayer.getDuration());
					// ��ʼ�̣߳����½������Ŀ̶�
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
					// �ڲ�����ϱ��ص�
					btn_play.setEnabled(true);
				}
			});

			mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// �����������²���
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
