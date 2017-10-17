package com.example.alit.bakingappnano;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.alit.bakingappnano.recipeProvider.StepsTable;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by AliT on 10/10/17.
 */

public class StepDetailFragment extends Fragment {

    @BindView(R.id.stepNumText) TextView stepNumText;
    @BindView(R.id.simpleExoPlayer) SimpleExoPlayerView simpleExoPlayerView;
    SimpleExoPlayer simpleExoPlayer;
    @BindView(R.id.longDescText) TextView longDescText;

    @BindView(R.id.backButton) CardView backButton;
    @BindView(R.id.nextButton) CardView nextButton;

    View rootView;

    StepDetailClickListener clickListener;

    Context context;

    int stepNum;
    boolean isLastStep;
    String shortDesc;
    String longDesc;
    String videoPath;
    String thumbnailPath;

    Unbinder unbinder;

    boolean hasVideo;

    boolean allowVideoLoading;

    DisplayMetrics displayMetrics;

    boolean isLand;

    boolean isTablet;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        this.displayMetrics = context.getResources().getDisplayMetrics();

        try {
            clickListener = (StepDetailClickListener) context;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement StepDetailClickListener");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        stepNum = bundle.getInt(RecipeDetailActivity.STEP_NUM);
        isLastStep = bundle.getBoolean(RecipeDetailActivity.IS_LAST_STEP);
        shortDesc = bundle.getString(StepsTable.SHORT_DESC);
        longDesc = bundle.getString(StepsTable.LONG_DESC);
        videoPath = bundle.getString(StepsTable.VIDEO_PATH);
        thumbnailPath = bundle.getString(StepsTable.THUMBNAIL_PATH);
        isTablet = bundle.getBoolean(RecipeDetailActivity.EXTRA_IS_TABLET);

        if (videoPath != null && !videoPath.isEmpty() || thumbnailPath != null && !thumbnailPath.isEmpty()) {
            hasVideo = true;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.step_detail_fragment_layout, container, false);

        int orientation = getScreenOrientation(context);

        if (isTablet) {
            unbinder = ButterKnife.bind(this, rootView);
        }
        else {
            switch (orientation) {
                case Surface.ROTATION_0:
                    isLand = false;
                    unbinder = ButterKnife.bind(this, rootView);
                    break;
                case Surface.ROTATION_90:
                    enableFullScreen();
                    isLand = true;
                    simpleExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.simpleExoPlayer);
                    break;
                case Surface.ROTATION_180:
                    isLand = false;
                    unbinder = ButterKnife.bind(this, rootView);
                    break;
                default:
            }
        }

        if (!hasVideo) simpleExoPlayerView.setVisibility(View.GONE);

        Timber.d("binding page: " + stepNum);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        stepNumText.setText("Step " + stepNum);

        if (!isLand) {

            stepNumText.setText(shortDesc);
            longDescText.setText(longDesc);

            if (isLastStep) {
                nextButton.setVisibility(View.INVISIBLE);
//            nextButton.setEnabled(false);
            }

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                simpleExoPlayerView.setVisibility(View.GONE);
                    clickListener.stepDetailClicked(true, stepNum - 1);
                }
            });

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                simpleExoPlayerView.setVisibility(View.GONE);
                    clickListener.stepDetailClicked(false, stepNum - 1);
                }
            });

        }

        Timber.d("onactivitycreated done: " + stepNum);
//        if (allowVideoLoading) initializeExoPlayer(false);
        if (hasVideo) initializeExoPlayer();

//        enableFullScreen(true);

    }

    public int getScreenOrientation(Context context){
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
    }

//    public void setAllowVideoLoading() {
//        allowVideoLoading = true;
//        if (simpleExoPlayerView != null) {
//            Timber.d("simplePlayerView was not null");
//            initializeExoPlayer(false);
//        }
//        else {
//            Timber.d("simplePlayerView was null " + stepNum);
//            simpleExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.simpleExoPlayer);
//            initializeExoPlayer();
//        }
//        if (simpleExoPlayerView != null) {
//            simpleExoPlayerView.setVisibility(View.VISIBLE);
//            initializeExoPlayer(true);
//        } else {
//            Timber.d("exoplayerview was null");
//            simpleExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.simpleExoPlayer);
//            if (simpleExoPlayerView == null) Timber.d("exoplayerview still null");
//            else {
//                simpleExoPlayerView.setVisibility(View.VISIBLE);
//                initializeExoPlayer(true);
//            }
//        }
//    }

    public void initializeExoPlayer() {

        Timber.d("initializing exoplayer for page: " + stepNum);

        if (simpleExoPlayer == null) {

            Timber.d("exoplayer was null");

            TrackSelector selector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, selector, loadControl);
            simpleExoPlayerView.setPlayer(simpleExoPlayer);
            String userAgent = Util.getUserAgent(context, "BakingAppNano");
            MediaSource mediaSource = null;
            if (videoPath != null && !videoPath.isEmpty()) {
                mediaSource = new ExtractorMediaSource(Uri.parse(videoPath), new DefaultDataSourceFactory(context, userAgent),
                        new DefaultExtractorsFactory(), null, null);
            }
            else if (thumbnailPath != null && !thumbnailPath.isEmpty()) {
                mediaSource = new ExtractorMediaSource(Uri.parse(thumbnailPath), new DefaultDataSourceFactory(context, userAgent),
                        new DefaultExtractorsFactory(), null, null);
            }
            else {
                Timber.d("there was no video path");
                return;
            }
//            simpleExoPlayer.setVideoListener(new SimpleExoPlayer.VideoListener() {
//                @Override
//                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//                }
//
//                @Override
//                public void onRenderedFirstFrame() {
//                }
//            });
            simpleExoPlayer.addVideoListener(new SimpleExoPlayer.VideoListener() {
                @Override
                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                    Timber.d("onVideoSizeChanged height: " + getDb(height) + " width: " + getDb(width) + " ratio: " + pixelWidthHeightRatio);
                    float heightToWidth = (float) height / width;
                    Timber.d("onvideo ratio: " + heightToWidth);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                    Timber.d("onvideo paramsHeight: " + simpleExoPlayerView.getHeight() + " paramsWidth: " + simpleExoPlayerView.getWidth());
                    params.height = (int) (simpleExoPlayerView.getWidth() * heightToWidth);
                    Timber.d("onvideo paramsHeightAfter: " + params.height);
                    simpleExoPlayerView.setLayoutParams(params);
                }

                @Override
                public void onRenderedFirstFrame() {
                    Timber.d("simpleExoPlayerHeight: " + getDb(simpleExoPlayerView.getPlayer().getVideoFormat().height));
                }
            });
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(false);

//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Timber.d("simpleExoPlayerHeight: " + getDb(simpleExoPlayerView.getPlayer().getVideoFormat().height));
//                }
//            }, 2000);

        }

        simpleExoPlayerView.post(new Runnable() {
            @Override
            public void run() {
                Timber.d("simpleExoPlayerViewHeight: " + getDb(simpleExoPlayerView.getHeight()));
            }
        });

//        simpleExoPlayerView.setVisibility(View.VISIBLE);

    }

    protected void enableFullScreen() {

        AppCompatActivity activity = (AppCompatActivity) context;

        if (Build.VERSION.SDK_INT < 16) {

            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else {

            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

        }

        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.hide();
    }

    public void releasePlayer() {

        simpleExoPlayer.stop();
        simpleExoPlayer.release();
        simpleExoPlayer = null;

//        simpleExoPlayerView.setVisibility(View.INVISIBLE);

    }

    public interface StepDetailClickListener {

        void stepDetailClicked(boolean moveUp, int position);

    }

    @Override
    public void onStop() {
        super.onStop();
//        if (simpleExoPlayer != null) releasePlayer();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
        if (simpleExoPlayer != null) releasePlayer();
        Timber.d("unbinding page: " + stepNum);
    }

    public int getDb(int px) {

        return (int) (px / displayMetrics.density);

    }

}
