package br.com.joaoduartemariucio.cordova.plugins.streamingnative;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.media.VideoView;

import java.util.ArrayList;
import java.util.List;

import br.com.fulltime.fullarm.mobile.R;

public class PlayerRTSPActivity extends Activity implements View.OnClickListener, View.OnLayoutChangeListener {

    public TextView mTitle = null;
    private boolean carregamentoFinalizado = false;
    private int countClick = 0;
    private ProgressBar mProgressBar = null;
    private VideoView mVideoView = null;
    private LibVLC vlcInstance = null;
    private MediaPlayer player = null;
    private View mControllerView = null;
    private Bundle bundle = null;
    private ImageView mBotaoPlay = null;
    private boolean isPaused = false;
    private List<String> options = new ArrayList<>();
    private Runnable checkIfPlaying = new Runnable() {
        @Override
        public void run() {
            try {
                if ((player.isPlaying() && player != null) || isPaused) {
                    mVideoView.postDelayed(checkIfPlaying, 4000);
                } else {
                    recreate();
                }
            } catch (Exception ex) {
                Log.e("erro", "checkIfPlayin");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_rtsp);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        bundle = getIntent().getExtras();
        mBotaoPlay = findViewById(R.id.rtsp_botao_pause);
        mTitle = findViewById(R.id.rtsp_title_view);
        mVideoView = findViewById(R.id.rtsp_video_view);
        mProgressBar = findViewById(R.id.rtsp_progress_bar);
        mControllerView = findViewById(R.id.controler_view);
        mostrarController();
        mTitle.setText(bundle.getString("mediaTitle", ""));

        options.add("-vvv");
        vlcInstance = new LibVLC(this, (ArrayList<String>) options);
        this.options.add(":fullscreen");
        play();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (player.isPlaying()) {
                player.pause();
                isPaused = true;
            }
        } catch (Exception message) {
            Log.e("Erro", "Player is null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPaused) {
            recreate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void play() {
        if (player == null || player.isReleased()) {
            setMedia(new Media(vlcInstance, Uri.parse(bundle.getString("mediaUrl"))));
        } else {
            stop();
            player.play();
        }
    }

    public void updatePlayPause(View view) {
        if (mVideoView == null || player == null) return;

        mostrarController();
        if (player.isPlaying()) {
            isPaused = true;
            player.pause();
            mBotaoPlay.setImageResource(R.drawable.ic_item_play);
        } else {
            player.play();
            isPaused = false;
            mBotaoPlay.setImageResource(R.drawable.ic_item_pause);
        }
    }

    public void stop() {
        try {
            if (player != null && player.isPlaying()) {
                player.stop();
                player.release();
            }
        } catch (Exception ex) {
            Log.e("Erro", ex.getMessage());
        }
    }

    private void setMedia(Media media) {
        if (options != null) {
            for (String s : options) {
                media.addOption(s);
            }
        }
        media.setHWDecoderEnabled(true, false);
        player = new MediaPlayer(vlcInstance);
        player.setMedia(media);
        player.setEventListener(new MediaPlayer.EventListener() {
            @Override
            public void onEvent(MediaPlayer.Event event) {
                switch (event.type) {
                    case MediaPlayer.Event.Buffering:
                        if (event.getBuffering() == 100) {
                            finalizarCarregamento();
                        }
                        break;
                    case MediaPlayer.Event.TimeChanged:
                        Log.i("event: TimeChanged     ", "(" + event.getTimeChanged() + ")");
                        break;
                    case MediaPlayer.Event.EncounteredError:
                        mProgressBar.setVisibility(View.GONE);
                        wrapItUp(RESULT_CANCELED, "Erro");
                        Log.i("event: EncounteredError", " (erro)");
                        break;
                }
            }
        });

        IVLCVout vlcVout = player.getVLCVout();
        if (!vlcVout.areViewsAttached()) {
            vlcVout.setVideoView(mVideoView);
            vlcVout.attachViews();
        } else {
            throw new RuntimeException("You cant set a null render object");
        }
        mVideoView.addOnLayoutChangeListener(this);
        player.setVideoTrackEnabled(true);
        player.play();
    }

    private void finalizarCarregamento() {
        if (!carregamentoFinalizado) {
            mProgressBar.setVisibility(View.GONE);
            mostrarController();
            if (!bundle.getString("mediaUrl").contains("https")) {
                mBotaoPlay.setVisibility(View.VISIBLE);
            }
            mVideoView.postDelayed(checkIfPlaying, 2000);
            carregamentoFinalizado = true;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mostrarController();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void wrapItUp(int resultCode, String message) {
        Intent intent = new Intent();
        intent.putExtra("message", message);
        setResult(resultCode, intent);
        finish();
    }

    public void sair(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        // If we're leaving, let's finish the activity
        stop();
        wrapItUp(RESULT_OK, null);
    }

    @Override
    public void onDestroy() {
        stop();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (mControllerView.getVisibility() == View.INVISIBLE) {
            mostrarController();
        } else {
            mControllerView.setVisibility(View.INVISIBLE);
            countClick++;
        }
    }

    private void mostrarController() {
        countClick++;
        mControllerView.setVisibility(View.VISIBLE);
        final int click = countClick;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (click == countClick) {
                    mControllerView.setVisibility(View.INVISIBLE);
                }
            }
        }, 3000);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                               int oldTop, int oldRight, int oldBottom) {
        IVLCVout vlcVout = player.getVLCVout();
        vlcVout.setWindowSize(right, bottom);
    }
}