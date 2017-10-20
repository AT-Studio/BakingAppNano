package com.example.alit.bakingappnano.recipeDetail;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.alit.bakingappnano.R;
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

/**
 * Created by AliT on 10/10/17.
 */

public class StepDetailFragment extends Fragment {

    @BindView(R.id.stepNumText)
    TextView stepNumText;
    @BindView(R.id.simpleExoPlayer)
    SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer simpleExoPlayer;
    @BindView(R.id.longDescText)
    TextView longDescText;

    @BindView(R.id.backButton)
    CardView backButton;
    @BindView(R.id.nextButton)
    CardView nextButton;

    private View rootView;

    private StepDetailClickListener clickListener;

    private Context context;

    private int stepNum;
    private boolean isLastStep;
    private String shortDesc;
    private String longDesc;
    private String videoPath;
    private String thumbnailPath;

    private Unbinder unbinder;

    private boolean hasVideo;

    private boolean isLand;

    private boolean isTablet;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        try {
            clickListener = (StepDetailClickListener) context;
        } catch (ClassCastException e) {
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
        } else {
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
                case Surface.ROTATION_270:
                    enableFullScreen();
                    isLand = true;
                    simpleExoPlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.simpleExoPlayer);
                default:
            }
        }

        if (!hasVideo) simpleExoPlayerView.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!isLand) {

            stepNumText.setText(shortDesc);
            longDescText.setText(longDesc);

            if (isLastStep) {
                nextButton.setVisibility(View.INVISIBLE);
            }

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.stepDetailClicked(true, stepNum - 1);
                }
            });

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.stepDetailClicked(false, stepNum - 1);
                }
            });

        }

        if (hasVideo) initializeExoPlayer();

    }

    public int getScreenOrientation(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
    }

    public void initializeExoPlayer() {

        if (simpleExoPlayer == null) {

            TrackSelector selector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, selector, loadControl);
            simpleExoPlayerView.setPlayer(simpleExoPlayer);
            String userAgent = Util.getUserAgent(context, getResources().getString(R.string.app_name));
            MediaSource mediaSource = null;
            if (videoPath != null && !videoPath.isEmpty()) {
                mediaSource = new ExtractorMediaSource(Uri.parse(videoPath), new DefaultDataSourceFactory(context, userAgent),
                        new DefaultExtractorsFactory(), null, null);
            } else if (thumbnailPath != null && !thumbnailPath.isEmpty()) {
                mediaSource = new ExtractorMediaSource(Uri.parse(thumbnailPath), new DefaultDataSourceFactory(context, userAgent),
                        new DefaultExtractorsFactory(), null, null);
            } else {
                return;
            }
            simpleExoPlayer.addVideoListener(new SimpleExoPlayer.VideoListener() {
                @Override
                public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                    float heightToWidth = (float) height / width;
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) simpleExoPlayerView.getLayoutParams();
                    params.height = (int) (simpleExoPlayerView.getWidth() * heightToWidth);
                    simpleExoPlayerView.setLayoutParams(params);
                }

                @Override
                public void onRenderedFirstFrame() {
                }
            });
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(false);

        }

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

    }

    public interface StepDetailClickListener {

        void stepDetailClicked(boolean moveUp, int position);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
        if (simpleExoPlayer != null) releasePlayer();
    }

}
