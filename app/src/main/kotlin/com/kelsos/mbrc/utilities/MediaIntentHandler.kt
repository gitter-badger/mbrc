package com.kelsos.mbrc.utilities

import android.content.Intent
import android.view.KeyEvent
import com.kelsos.mbrc.events.bus.RxBus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaIntentHandler
@Inject constructor(private val bus: RxBus) {
  private var previousClick: Long = 0

  init {
    previousClick = 0
  }

  fun handleMediaIntent(mediaIntent: Intent?): Boolean {
    var result = false

    //noinspection StatementWithEmptyBody
    if (mediaIntent?.action == android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
      // Handle somehow
    } else if (mediaIntent?.action == Intent.ACTION_MEDIA_BUTTON) {
      val extras = mediaIntent?.extras
      val keyEvent = extras?.get(Intent.EXTRA_KEY_EVENT) as KeyEvent?

      if (keyEvent?.action != KeyEvent.ACTION_DOWN) {
        return false
      }

      when (keyEvent?.keyCode) {
        KeyEvent.KEYCODE_HEADSETHOOK -> {
          val currentClick = System.currentTimeMillis()
          if (currentClick - previousClick < DOUBLE_CLICK_INTERVAL) {
            TODO("Play Next")
          }
          previousClick = currentClick
          result = true
          TODO("play.pause")
        }
        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> TODO("PlayerPlayPause")
        KeyEvent.KEYCODE_MEDIA_PLAY -> TODO("PlayerPlay")
        KeyEvent.KEYCODE_MEDIA_PAUSE -> TODO("PlayerPause")
        KeyEvent.KEYCODE_MEDIA_STOP -> TODO("PlayerStop")
        KeyEvent.KEYCODE_MEDIA_NEXT -> TODO("PlayerNext")
        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> TODO("PlayerPrevious")
        else -> {
        }
      }
    }
    return result
  }

  companion object {
    private val DOUBLE_CLICK_INTERVAL = 350
  }
}
