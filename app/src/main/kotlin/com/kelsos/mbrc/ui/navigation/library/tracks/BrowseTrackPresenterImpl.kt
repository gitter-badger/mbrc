package com.kelsos.mbrc.ui.navigation.library.tracks

import com.kelsos.mbrc.domain.Track
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.events.ui.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.mvp.BasePresenter
import com.kelsos.mbrc.repository.library.TrackRepository
import com.raizlabs.android.dbflow.list.FlowCursorList
import rx.Scheduler
import rx.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BrowseTrackPresenterImpl
@Inject constructor(private val bus: RxBus,
                    private val repository: TrackRepository,
                    @Named("io") private val ioScheduler: Scheduler,
                    @Named("main") private val mainScheduler: Scheduler) :
    BasePresenter<BrowseTrackView>(),
    BrowseTrackPresenter {

  override fun attach(view: BrowseTrackView) {
    super.attach(view)
    bus.register(this, LibraryRefreshCompleteEvent::class.java, { load() })
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    addSubcription(repository.getAllCursor().compose { schedule(it) }.subscribe({
      view?.update(it)
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view?.failure(it)
    }))
  }


  override fun reload() {
    addSubcription(repository.getAndSaveRemote().compose { schedule(it) }.subscribe({
      view?.update(it)
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view?.failure(it)
    }))
  }

  private fun schedule(it: Single<FlowCursorList<Track>>) = it.observeOn(mainScheduler)
      .subscribeOn(ioScheduler)

}
