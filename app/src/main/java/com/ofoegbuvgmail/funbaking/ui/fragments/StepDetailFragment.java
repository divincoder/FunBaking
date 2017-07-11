package com.ofoegbuvgmail.funbaking.ui.fragments;


import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ofoegbuvgmail.funbaking.R;
import com.ofoegbuvgmail.funbaking.model.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ofoegbuvgmail.funbaking.adapter.RecipeDetailAdapter.STEPS;
import static com.ofoegbuvgmail.funbaking.ui.activities.RecipeDetailActivity.PANES;
import static com.ofoegbuvgmail.funbaking.ui.activities.RecipeDetailActivity.POSITION;

/**
 * A simple {@link Fragment} subclass.
 */
public class StepDetailFragment extends Fragment implements View.OnClickListener, SimpleExoPlayer.EventListener {

    private static final String CURRENT_WINDOW_INDEX = "current_window_index";
    private static final String PLAYBACK_POSITION = "playback_position";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private boolean autoPlay = false;
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    private TrackSelector trackSelector;
    private SimpleExoPlayer exoPlayer;
    private int mPosition;
    private int mIndex;
    private boolean mTwoPane;
    private ArrayList<Step> mSteps;
    @BindView(R.id.step_video_player)
    SimpleExoPlayerView mVideoPlayerView;
    @BindView(R.id.next_step_button)
    Button nextStepButton;
    @BindView(R.id.previous_step_button)
    Button previousStepButton;
    @BindView(R.id.step_short_description)
    TextView shortDescription;
    @BindView(R.id.step_description_text)
    TextView stepDescrption;
    Unbinder unbinder;


    public StepDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mSteps = savedInstanceState.getParcelableArrayList(STEPS);
            mPosition = savedInstanceState.getInt(POSITION);
            mIndex = mPosition;
        }

        View rootView = inflater.inflate(R.layout.fragment_step_detail, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        nextStepButton.setOnClickListener(this);
        previousStepButton.setOnClickListener(this);

        mPosition = getArguments().getInt(POSITION);
        mIndex = mPosition;
        mTwoPane = getArguments().getBoolean(PANES);


        return rootView;
    }

    private void playStepVideo(int index) {
        mVideoPlayerView.setVisibility(View.VISIBLE);
        mVideoPlayerView.requestFocus();
        String videoUrl = mSteps.get(index).getVideoURL();
        String thumbNailUrl = mSteps.get(index).getThumbnailURL();
        if (!videoUrl.isEmpty()) {
            initializePlayer(Uri.parse(videoUrl));
        } else if (!thumbNailUrl.isEmpty()) {
            initializePlayer(Uri.parse(thumbNailUrl));
        } else {
            mVideoPlayerView.setVisibility(View.GONE);
        }
    }


    private void isLandscape() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            hideSystemUi();
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {

        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showStepDetails() {

        shortDescription.setVisibility(View.VISIBLE);
        mVideoPlayerView.setVisibility(View.VISIBLE);
        stepDescrption.setVisibility(View.VISIBLE);
        mSteps = getArguments().getParcelableArrayList(STEPS);
        assert mSteps != null;
        stepDescrption.setText(mSteps.get(mIndex).getDescription());
        shortDescription.setText(mSteps.get(mIndex).getShortDescription());

        playStepVideo(mIndex);
        isLandscape();
    }

    private void initializePlayer(Uri uri) {
        if (exoPlayer == null) {

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getActivity(),
                    null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

            trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

            // Set the ExoPlayer.EventListener to this activity.
            exoPlayer.addListener(this);

            mVideoPlayerView.setPlayer(exoPlayer);
            exoPlayer.setPlayWhenReady(playWhenReady);
            exoPlayer.seekTo(currentWindow, playbackPosition);
        }

        MediaSource mediaSource = buildMediaSource(uri);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
    }

    // Prepare the MediaSource.
    private MediaSource buildMediaSource(Uri uri) {
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        String userAgent = Util.getUserAgent(getActivity(), "FunBaking");
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);

        return new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            showStepDetails();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || exoPlayer == null)) {
            showStepDetails();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (exoPlayer != null) {
            outState.putLong(PLAYBACK_POSITION, playbackPosition);
            outState.putInt(CURRENT_WINDOW_INDEX, currentWindow);
        }

        outState.putParcelableArrayList(STEPS, mSteps);
        outState.putInt(POSITION, mPosition);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step_button:
                if (mIndex < mSteps.size() - 1) {
                    int index = ++mIndex;
                    shortDescription.setText(mSteps.get(index).getShortDescription());
                    stepDescrption.setText(mSteps.get(index).getDescription());
                    playStepVideo(index);
                } else {
                    Toast.makeText(getActivity(), R.string.end_of_steps, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.previous_step_button:
                if (mIndex > 0) {
                    int index = --mIndex;
                    shortDescription.setText(mSteps.get(index).getShortDescription());
                    stepDescrption.setText(mSteps.get(index).getDescription());
                    playStepVideo(index);
                } else {
                    Toast.makeText(getActivity(), R.string.start_of_steps, Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            Toast.makeText(getActivity(), "Playing", Toast.LENGTH_LONG).show();
        } else if ((playbackState == ExoPlayer.STATE_READY)) {

        }

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        String errorString = null;
        if (error.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = error.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            Toast.makeText(getActivity(), errorString, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }
}
