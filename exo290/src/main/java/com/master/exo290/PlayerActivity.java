package com.master.exo290;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerActivity extends AppCompatActivity {

    private static final String URL = "https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8";

//    private static final String URL = "http://h12.jiayongfeng.wang/live/258203_8f2201f817ddd8108ced.flv?auth_key=1540392851-0-0-19c741142bcda437e398f72617d37222";

    public static void start(Context context) {
        Intent starter = new Intent(context, PlayerActivity.class);
        context.startActivity(starter);
    }

    private PlayerView mPlayerView;
    private ImageView mPlayView;

    private SimpleExoPlayer mExoPlayer;

    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exo_activity_player);

        View closeView = findViewById(R.id.fab);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mPlayerView = findViewById(R.id.player_view);
        mPlayerView.requestFocus();

        mPlayView = findViewById(R.id.play_view);
        mPlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    mExoPlayer.setPlayWhenReady(false);
                    mPlayView.setImageResource(R.drawable.exo_ic_play_arrow_black_24dp);
                } else {
                    mExoPlayer.setPlayWhenReady(true);
                    mPlayView.setImageResource(R.drawable.exo_ic_pause_black_24dp);
                }
            }
        });

        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean("exo_auto_play");
            startWindow = savedInstanceState.getInt("exo_start_window");
            startPosition = savedInstanceState.getLong("exo_start_position");
        } else {
            clearStartPosition();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateStartPosition();
        outState.putBoolean("exo_auto_play", startAutoPlay);
        outState.putInt("exo_start_window", startWindow);
        outState.putLong("exo_start_position", startPosition);
    }

    private boolean isPlaying() {
        return mExoPlayer != null
                && mExoPlayer.getPlaybackState() != Player.STATE_ENDED
                && mExoPlayer.getPlaybackState() != Player.STATE_IDLE
                && mExoPlayer.getPlayWhenReady();
    }

    @Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        clearStartPosition();
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (VersionCodes.g23()) {
            initializePlayer();
            if (mPlayerView != null) {
                mPlayerView.onResume();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (VersionCodes.le23() || mExoPlayer == null) {
            initializePlayer();
            if (mPlayerView != null) {
                mPlayerView.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (VersionCodes.le23()) {
            if (mPlayerView != null) {
                mPlayerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (VersionCodes.g23()) {
            if (mPlayerView != null) {
                mPlayerView.onResume();
            }
            releasePlayer();
        }
    }

    private void initializePlayer() {
        if (mExoPlayer == null) {

            DefaultTrackSelector selector = new DefaultTrackSelector();

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, selector);
            mExoPlayer.addListener(new PlayerEventListener());
            mExoPlayer.setPlayWhenReady(startAutoPlay);

            //mPlayerView.setPlaybackPreparer(this);
            mPlayerView.setPlayer(mExoPlayer);
        }

        MediaSource mediaSource = MediaSources.buildMediaSource(this, Uri.parse(URL));

        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            mExoPlayer.seekTo(startWindow, startPosition);
        }
        mExoPlayer.prepare(mediaSource, !haveStartPosition, false);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            updateStartPosition();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private void updateStartPosition() {
        if (mExoPlayer != null) {
            startAutoPlay = mExoPlayer.getPlayWhenReady();
            startWindow = mExoPlayer.getCurrentWindowIndex();
            startPosition = Math.max(0, mExoPlayer.getContentPosition());
        }
    }

    private void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private class PlayerEventListener implements Player.EventListener {

        @Override
        public void onPositionDiscontinuity(int reason) {
            if (mExoPlayer.getPlaybackError() != null) {
                updateStartPosition();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            if (isBehindLiveWindow(error)) {
                clearStartPosition();
                initializePlayer();
            } else {
                updateStartPosition();
            }
        }
    }
}
