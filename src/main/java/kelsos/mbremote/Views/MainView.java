package kelsos.mbremote.Views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;
import com.squareup.otto.Bus;
import kelsos.mbremote.Events.UserActionEvent;
import kelsos.mbremote.R;
import kelsos.mbremote.controller.RunningActivityAccessor;
import kelsos.mbremote.enums.PlayState;
import kelsos.mbremote.enums.UserInputEventType;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.Timer;
import java.util.TimerTask;

import static android.os.Build.VERSION.SDK_INT;

@ContentView(R.layout.main)
public class MainView extends RoboSherlockActivity
{
    // Inject elements of the view
    @InjectView(R.id.artistLabel) TextView artistLabel;
    @InjectView(R.id.titleLabel) TextView titleLabel;
    @InjectView(R.id.albumLabel) TextView albumLabel;
    @InjectView(R.id.yearLabel) TextView yearLabel;
    @InjectView(R.id.trackProgressCurrent) TextView trackProgressCurrent;
    @InjectView(R.id.trackDuration) TextView trackDuration;
    @InjectView(R.id.playPauseButton) ImageButton playPauseButton;
    @InjectView(R.id.previousButton) ImageButton previousButton;
    @InjectView(R.id.nextButton) ImageButton nextButton;
    @InjectView(R.id.volumeSlider) SeekBar volumeSlider;
    @InjectView(R.id.trackProgressSlider) SeekBar trackProgressSlider;
    @InjectView(R.id.stopButton) ImageButton stopButton;
    @InjectView(R.id.muteButton) ImageButton muteButton;
    @InjectView(R.id.scrobbleButton) ImageButton scrobbleButton;
    @InjectView(R.id.shuffleButton) ImageButton shuffleButton;
    @InjectView(R.id.repeatButton) ImageButton repeatButton;
    @InjectView(R.id.connectivityIndicator) ImageButton connectivityIndicator;
    @InjectView(R.id.albumCover) ImageView albumCover;

    // Injects
    @Inject protected Bus bus;
	@Inject private RunningActivityAccessor accessor;

    private boolean userChangingVolume;
    private Timer progressUpdateTimer;
    private TimerTask progressUpdateTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//HACK: in 2.2 the onContentChanged(); method does not get called before the on create.
		if(SDK_INT >= 7 && SDK_INT <= 8)
		{
			super.onContentChanged();
		}
		accessor.register(this);
        userChangingVolume = false;
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		bus.post(new UserActionEvent(UserInputEventType.Refresh));
		RegisterListeners();
		SetTextViewTypeface();
    }

	@Override
	protected void onStart()
	{
		super.onStart();
		accessor.register(this);
		bus.post(new UserActionEvent(UserInputEventType.Refresh));
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		accessor.register(this);
		bus.post(new UserActionEvent(UserInputEventType.Refresh));
	}

	@Override
	protected void onPause()
	{
		accessor.unRegister(this);
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		accessor.unRegister(this);
		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		accessor.unRegister(this);
		super.onDestroy();
	}

    /**
     * Sets the typeface of the text views in the main activity to roboto.
     */
    private void SetTextViewTypeface() {
        /* Marquee Hack */
        artistLabel.setSelected(true);
        titleLabel.setSelected(true);
        albumLabel.setSelected(true);
        yearLabel.setSelected(true);

        Typeface robotoLight = Typeface.createFromAsset(getAssets(), "fonts/roboto_light.ttf");
        Typeface fresca = Typeface.createFromAsset(getAssets(), "fonts/fresca.ttf");
        artistLabel.setTypeface(robotoLight);
        titleLabel.setTypeface(robotoLight);
        albumLabel.setTypeface(robotoLight);
        yearLabel.setTypeface(robotoLight);
        trackProgressCurrent.setTypeface(fresca);
        trackDuration.setTypeface(fresca);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.main_menu_settings:
				startActivity(new Intent(this, AppPreferenceView.class));
				return true;
			case R.id.main_menu_lyrics:
				startActivity(new Intent(this, LyricsView.class));
				return true;
			case R.id.main_menu_playlist:
				startActivity(new Intent(this, PlaylistView.class));
				return true;
			default:
				return false;
		}
	}



	/**
     * Registers the listeners for the interface elements used for interaction.
     */
    private void RegisterListeners() {
        playPauseButton.setOnClickListener(playButtonListener);
        previousButton.setOnClickListener(previousButtonListener);
        nextButton.setOnClickListener(nextButtonListener);
        volumeSlider.setOnSeekBarChangeListener(volumeChangeListener);
        trackProgressSlider.setOnSeekBarChangeListener(durationSeekBarChangeListener);
        stopButton.setOnClickListener(stopButtonListener);
        stopButton.setEnabled(false);
        muteButton.setOnClickListener(muteButtonListener);
        scrobbleButton.setOnClickListener(scrobbleButtonListener);
        shuffleButton.setOnClickListener(shuffleButtonListener);
        repeatButton.setOnClickListener(repeatButtonListener);
        connectivityIndicator.setOnClickListener(connectivityIndicatorListener);

    }

    /**
     * Given a boolean state this function updates the Scrobbler button with the proper state.
     * Also it updates the internal MainActivityState object.
     *
     * @param state If true it means that the scrobbler is active, false is used for inactive.
     */
    public void updateScrobblerButtonState(boolean state) {
        if (state) {
            scrobbleButton.setImageResource(R.drawable.ic_media_scrobble_red);
        } else {
            scrobbleButton.setImageResource(R.drawable.ic_media_scrobble_off);
        }
    }

	public void resetAlbumCover()
	{
		albumCover.setImageResource(R.drawable.ic_image_no_cover);
	}

    public void updateAlbumCover(Bitmap cover) {
        albumCover.setImageBitmap(cover);
    }

    /**
     * Given a boolean state value this function updates the Shuffle button with the proper state.
     * Also it updates the internal MainActivityState object.
     *
     * @param state True is used to represent active shuffle, false is used for inactive.
     */
    public void updateShuffleButtonState(boolean state) {
        if (state) {
            shuffleButton.setImageResource(R.drawable.ic_media_shuffle);
        } else {
            shuffleButton.setImageResource(R.drawable.ic_media_shuffle_off);
        }
    }

    public void updateRepeatButtonState(boolean state) {
        if (state) {
            repeatButton.setImageResource(R.drawable.ic_media_repeat);
        } else {
            repeatButton.setImageResource(R.drawable.ic_media_repeat_off);
        }
    }

    public void updateMuteButtonState(boolean state) {
        if (state) {
            muteButton.setImageResource(R.drawable.ic_media_mute_active);
        } else {
            muteButton.setImageResource(R.drawable.ic_media_mute_full);
        }
    }

    public void updateVolumeData(int volume) {
        if (!userChangingVolume)
            volumeSlider.setProgress(volume);
    }

    public void updatePlayState(PlayState playState) {
        switch (playState) {
            case Playing:
                playPauseButton.setImageResource(R.drawable.ic_media_pause);
                stopButton.setImageResource(R.drawable.ic_media_stop);
                stopButton.setEnabled(true);

                /* Start the animation if the track is playing*/
                trackProgressAnimation();
                break;
            case Paused:
                playPauseButton.setImageResource(R.drawable.ic_media_play);
                stopButton.setEnabled(true);
                /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                break;
            case Stopped:
                /* Stop the animation if the track is paused*/
                stopTrackProgressAnimation();
                trackProgressSlider.setProgress(0);
                trackProgressCurrent.setText("00:00");
            case Undefined:
                playPauseButton.setImageResource(R.drawable.ic_media_play);
                stopButton.setImageResource(R.drawable.ic_media_stop_disabled);
                stopButton.setEnabled(false);
                break;
        }
    }

    public void updateArtistText(String artist) {
        artistLabel.setText(artist);
    }

    public void updateTitleText(String title) {
        titleLabel.setText(title);
    }

    public void updateAlbumText(String album) {
        albumLabel.setText(album);
    }

    public void updateYearText(String year) {
        yearLabel.setText(year);
    }

    public void updateConnectionIndicator(boolean connected) {
        if (connected) {
            connectivityIndicator.setImageResource(R.drawable.ic_icon_indicator_green);
        } else {
            connectivityIndicator.setImageResource(R.drawable.ic_icon_indicator_red);
        }
    }

    /**
     * Responsible for updating the displays and seekbar responsible for the display of the track duration and the
     * current progress of playback
     * @param current Integer represents the current playback position in milliseconds
     * @param total Integer represents the total track duration in milliseconds
     */
    public void updateDurationDisplay(int current, int total)
    {
        int currentSeconds = current/1000;
        int totalSeconds = total/1000;

        int currentMinutes = currentSeconds/60;
        int totalMinutes = totalSeconds/60;

        currentSeconds %= 60;
        totalSeconds %= 60;

        trackDuration.setText(String.format("%02d:%02d", totalMinutes, totalSeconds));
        trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));

        trackProgressSlider.setMax(total);
        trackProgressSlider.setProgress(current);

        trackProgressAnimation();
    }

    /**
     * Starts the progress animation when called. If It was previously running then it restarts it.
     */
    private void trackProgressAnimation() {
        /* If the scheduled tasks is not null then cancel it and clear it along with the timer to create them anew */
        final int timerPeriod = 100;
        stopTrackProgressAnimation();
        progressUpdateTimer = new Timer(true);
        progressUpdateTask = new TimerTask() {
            @Override
            public void run() {
                int currentProgress = trackProgressSlider.getProgress()/1000;
                final int currentMinutes = currentProgress/60;
                final int currentSeconds = currentProgress%60;
                runOnUiThread(new Runnable() {
                    public void run() {
                        trackProgressSlider.setProgress(trackProgressSlider.getProgress()+timerPeriod);
                        trackProgressCurrent.setText(String.format("%02d:%02d", currentMinutes, currentSeconds));
                    }
                });

            }
        };
        progressUpdateTimer.schedule(progressUpdateTask, 0, timerPeriod);
    }

    /**
     * If the track progress animation is running the the function stops it.
     */
    private void stopTrackProgressAnimation() {
        if(progressUpdateTask !=null)
        {
            progressUpdateTask.cancel();
            progressUpdateTask = null;
            progressUpdateTimer.cancel();
            progressUpdateTimer.purge();
            progressUpdateTimer = null;
        }
    }

    private OnClickListener playButtonListener = new OnClickListener() {

        public void onClick(View v) {
            bus.post(new UserActionEvent(UserInputEventType.PlayPause));
        }
    };

    private OnClickListener previousButtonListener = new OnClickListener() {

        public void onClick(View v) {
            bus.post(new UserActionEvent(UserInputEventType.Previous));
        }
    };

    private OnClickListener nextButtonListener = new OnClickListener() {

        public void onClick(View v) {
            bus.post(new UserActionEvent(UserInputEventType.Next));
        }
    };

    private OnClickListener stopButtonListener = new OnClickListener() {

        public void onClick(View v) {
            bus.post(new UserActionEvent(UserInputEventType.Stop));
        }
    };

    private OnClickListener muteButtonListener = new OnClickListener() {

        public void onClick(View v) {
            bus.post(new UserActionEvent(UserInputEventType.Mute));
        }
    };

    private OnClickListener scrobbleButtonListener = new OnClickListener() {

        public void onClick(View v) {
            bus.post(new UserActionEvent(UserInputEventType.Scrobble));
        }
    };

    private OnClickListener shuffleButtonListener = new OnClickListener() {

        public void onClick(View v) {
            bus.post(new UserActionEvent(UserInputEventType.Shuffle));
        }
    };

    private OnClickListener repeatButtonListener = new OnClickListener() {

        public void onClick(View v) {
            bus.post(new UserActionEvent(UserInputEventType.Repeat));
        }
    };
    private OnClickListener connectivityIndicatorListener = new OnClickListener() {

        public void onClick(View v) {
            bus.post(new UserActionEvent(UserInputEventType.Initialize));
        }
    };

    private OnSeekBarChangeListener volumeChangeListener = new OnSeekBarChangeListener() {

        public void onStopTrackingTouch(SeekBar seekBar) {
            userChangingVolume = false;

        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            userChangingVolume = true;

        }

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser)
                bus.post(new UserActionEvent(UserInputEventType.Volume, String.valueOf(seekBar.getProgress())));
        }
    };

    private OnSeekBarChangeListener durationSeekBarChangeListener = new OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser)
            {
                bus.post(new UserActionEvent(UserInputEventType.PlaybackPosition, String.valueOf(progress)));
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

}