package com.liskovsoft.smartyoutubetv.flavors.exoplayer.player.autoframerate;

import com.liskovsoft.exoplayeractivity.R;
import com.liskovsoft.sharedutils.helpers.Helpers;
import com.liskovsoft.smartyoutubetv.CommonApplication;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.player.ExoPlayerFragment;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.player.PlayerEventListener;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.player.autoframerate.DisplayHolder.Mode;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.player.autoframerate.DisplaySyncHelper.AutoFrameRateListener;

public class AutoFrameRateManager implements PlayerEventListener, AutoFrameRateListener {
    protected final ExoPlayerFragment mPlayerFragment;
    protected AutoFrameRateHelper mAutoFrameRateHelper;

    public AutoFrameRateManager(ExoPlayerFragment playerFragment) {
        mPlayerFragment = playerFragment;
    }

    @Override
    public void onAppInit() {
        if (mAutoFrameRateHelper == null) { // at this moment fragment has been attached to activity
            mAutoFrameRateHelper = new AutoFrameRateHelper(mPlayerFragment.getActivity(), new DisplaySyncHelper(mPlayerFragment.getActivity()));
        }

        mAutoFrameRateHelper.saveOriginalState();
        if (CommonApplication.getPreferences().getUgoos50HZFix()) {
            mAutoFrameRateHelper.applyModeChangeFix();
        }
        mAutoFrameRateHelper.setListener(this);
    }

    @Override
    public void onPlayerCreated() {
        mAutoFrameRateHelper.setPlayer(mPlayerFragment.getPlayer()); // new player is crated
    }

    @Override
    public void onPlayerClosed() {
        if (mAutoFrameRateHelper != null) {
            mAutoFrameRateHelper.setPlayer(null);
        }
    }

    @Override
    public void onTrackEnded() {
        
    }

    @Override
    public void onPlaybackReady() {
        if (mAutoFrameRateHelper != null) {
            mAutoFrameRateHelper.apply();
        }
    }

    public boolean isEnabled() {
        if (mAutoFrameRateHelper == null) {
            return false;
        }

        return mAutoFrameRateHelper.getEnabled();
    }

    public void setEnabled(boolean enabled) {
        if (mAutoFrameRateHelper != null) {
            mAutoFrameRateHelper.setEnabled(enabled);
        }
    }

    public boolean isDelayEnabled() {
        if (mAutoFrameRateHelper == null) {
            return false;
        }

        return mAutoFrameRateHelper.getDelayEnabled();
    }

    public void setDelayEnabled(boolean enabled) {
        if (mAutoFrameRateHelper != null) {
            mAutoFrameRateHelper.setDelayEnabled(enabled);
        }
    }

    public long getDelayTime() {
        if (mAutoFrameRateHelper == null) {
            return 0;
        }

        return mAutoFrameRateHelper.getDelayTime();
    }

    public void setDelayTime(long pauseMS) {
        if (mAutoFrameRateHelper != null) {
            mAutoFrameRateHelper.setDelayTime(pauseMS);
        }
    }

    @Override
    public void onModeStart(Mode newMode) {
        if (isDelayEnabled() && newMode != null) {
            mPlayerFragment.startPlaybackDelay(getDelayTime());

            String modeStr = String.format("%sx%s@%s", newMode.getPhysicalWidth(), newMode.getPhysicalHeight(), Helpers.formatFloat(newMode.getRefreshRate()));
            String msg = mPlayerFragment.getResources().getString(R.string.changing_video_frame_rate, modeStr);
            mPlayerFragment.showMessage(msg, getDelayTime());
        }
    }

    @Override
    public void onAppPause() {
        // save last state
    }

    @Override
    public void onAppResume() {
        // restore last state
    }
}
