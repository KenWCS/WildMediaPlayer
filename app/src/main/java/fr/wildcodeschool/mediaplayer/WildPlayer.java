package fr.wildcodeschool.mediaplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.StringRes;
import android.util.Log;

import java.io.IOException;

class WildPlayer implements WildAudioManagerListener {
  // Activity context
  private Context mContext;
  // Android media player
  private MediaPlayer mPlayer;
  // media player prepared state
  private boolean isPrepared = false;

  WildPlayer(Context ctx) {
    mContext = ctx;
    mPlayer  = new MediaPlayer();

    // Register to the audioManager events
    WildAudioManager.getInstance().setAudioManagerListener(this);
  }

  /**
   * Initialize the media to play
   * @param song URI of media to play
   * @param listener onPrepared event listener
   */
  void init(@StringRes int song, final WildOnPlayerListener listener) {

    try {
      // Set source and init the player engine
      mPlayer.setDataSource(mContext.getString(song));
      mPlayer.prepareAsync();
    } catch (IOException e) {
      Log.e(this.toString(), e.getMessage());
    }

    this.mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mp) {
        mp.seekTo(0);
        // Send the audio engine state to the listener
        if (null != listener) listener.onPrepared(mp);
        // Update state
        isPrepared = true;
      }
    });

    this.mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mp) {
        // Send the audio engine state to the listener
        if (null != listener) listener.onCompletion(mp);
      }
    });
  }

  /**
   * Check the validity of player and call play command
   * @return The validity of the call
   */
  boolean play() {
    if (null != mPlayer && isPrepared && !mPlayer.isPlaying()) {
      if (WildAudioManager.getInstance().requestAudioFocus()) {
        mPlayer.start();
        return true;
      }
    }
    return false;
  }

  /**
   * Check the validity of player and call pause command
   * @return The validity of the call
   */
  boolean pause() {
    if (null != mPlayer && isPrepared && mPlayer.isPlaying()) {
      mPlayer.pause();
      return true;
    }
    return false;
  }

  /**
   * Check the validity of player and call stop command
   * @return The validity of the call
   */
  boolean reset() {
    if (null != mPlayer && isPrepared) {
      mPlayer.seekTo(0);
      return true;
    }
    return false;
  }

  /**
   * Check the playing state of media
   * @return Media playing state
   */
  boolean isPlaying() {
    if (null != mPlayer && isPrepared) {
      return mPlayer.isPlaying();
    }
    return false;
  }

  /**
   * Check the validity of player and return media current position
   * @return Media current position
   */
  int getCurrentPosition() {
    if (null != mPlayer && isPrepared) {
      return mPlayer.getCurrentPosition();
    }
    return 0;
  }

  /**
   * Seek in the timeline
   * @param position Value in ms
   */
  void seekTo(int position) {
    if (null != mPlayer && isPrepared) {
      mPlayer.seekTo(position);
    }
  }

  /**
   * WildAudioManagerListener
   * @param isGain inform that application has audio focus or not
   */
  @Override
  public void audioFocusGain(boolean isGain) {
    if (!isGain) pause();
  }
}
