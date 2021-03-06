package com.kelsos.mbrc.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.webkit.WebView
import com.afollestad.materialdialogs.MaterialDialog

class WebViewDialog : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = MaterialDialog.Builder(activity)
    val webView = WebView(activity)
    webView.loadUrl(arguments.getString(ARG_URL))
    builder.customView(webView, false)
    builder.title(arguments.getInt(ARG_TITLE))
    builder.positiveText(android.R.string.ok)
    builder.onPositive { dialog, which -> dialog.dismiss() }
    return builder.build()
  }

  companion object {
    const val ARG_URL = "url"
    const val ARG_TITLE = "title"
  }
}
