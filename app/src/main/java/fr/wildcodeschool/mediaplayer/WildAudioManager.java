package fr.wildcodeschool.mediaplayer;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.widget.Toast;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static android.media.AudioManager.STREAM_MUSIC;

final public class WildAudioManager implements AudioManager.OnAudioFocusChangeListener {
  // Instance of the WildAudioManager class
  private static final WildAudioManager mInstance = new WildAudioManager();
  private AudioManager mAudioManager;
  private WildAudioManagerListener mListener;

  /**
   * Singleton accessor
   * @return the unique instance of the class
   */
  static WildAudioManager getInstance() {
    return mInstance;
  }

  /**
   * Constructor
   */
  private WildAudioManager() {
    mAudioManager = (AudioManager)MainActivity
      .getAppContext()
      .getSystemService(Context.AUDIO_SERVICE);
  }

  /**
   * Inform the audio manager that the application request the audio focus
   * @return if request is granted or not
   */
  boolean requestAudioFocus() {
    int state;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      AudioFocusRequest lRequest = new AudioFocusRequest
        .Builder(AUDIOFOCUS_GAIN)
        .setOnAudioFocusChangeListener(this)
        .build();

      state = mAudioManager.requestAudioFocus(lRequest);
    } else {
      state = mAudioManager.requestAudioFocus(this, STREAM_MUSIC, AUDIOFOCUS_GAIN);
    }
    return (AUDIOFOCUS_REQUEST_GRANTED == state);
  }

  /**
   * Inform the audio manager that the application release the audio focus
   * @return if request is granted or not
   */
  boolean releaseAudioFocus() {
    int state;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      AudioFocusRequest lRequest = new AudioFocusRequest
        .Builder(AUDIOFOCUS_LOSS)
        .setOnAudioFocusChangeListener(this)
        .build();

      state = mAudioManager.abandonAudioFocusRequest(lRequest);
    } else {
      state = mAudioManager.requestAudioFocus(this, STREAM_MUSIC, AUDIOFOCUS_LOSS);
    }
    return (AUDIOFOCUS_REQUEST_GRANTED == state);
  }

  @Override
  public void onAudioFocusChange(int focusChange) {
    Toast.makeText(
      MainActivity.getAppContext(),"AudioFocusChange: "+focusChange, Toast.LENGTH_SHORT)
      .show();

    switch (focusChange) {
      case AUDIOFOCUS_GAIN:
        if (null != mListener) mListener.audioFocusGain(true);
        break;
      case AUDIOFOCUS_LOSS:
      case AUDIOFOCUS_LOSS_TRANSIENT:
      case AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
        if (null != mListener) mListener.audioFocusGain(false);
        releaseAudioFocus();
        break;
      default:
        break;
    }
  }

  /**
   * Store the audioManager listener
   * @param listener new audioManager listener
   */
  void setAudioManagerListener(WildAudioManagerListener listener) {
    mListener = listener;
  }
}
