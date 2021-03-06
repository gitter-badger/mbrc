package com.kelsos.mbrc.events.ui

import android.support.annotation.IntDef
import com.kelsos.mbrc.data.dao.ConnectionSettings

class DiscoveryStopped(@Status val reason: Long, var settings: ConnectionSettings? = null) {

  companion object {
    const val NO_WIFI = 1L
    const val NOT_FOUND = 2L
    const val SUCCESS = 3L
  }

  @IntDef(NO_WIFI, NOT_FOUND, SUCCESS)
  @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
  annotation class Status
}
