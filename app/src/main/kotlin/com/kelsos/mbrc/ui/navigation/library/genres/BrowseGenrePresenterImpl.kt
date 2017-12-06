package com.kelsos.mbrc.ui.navigation.library.genres

import com.kelsos.mbrc.content.library.genres.Genre
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.events.LibraryRefreshCompleteEvent
import com.kelsos.mbrc.events.bus.RxBus
import com.kelsos.mbrc.mvp.BasePresenter
import com.raizlabs.android.dbflow.list.FlowCursorList
import io.reactivex.Scheduler
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class BrowseGenrePresenterImpl
@Inject constructor(private val bus: RxBus,
                    private val repository: GenreRepository,
                    @Named("io") private val ioScheduler: Scheduler,
                    @Named("main") private val mainScheduler: Scheduler) :
    BasePresenter<BrowseGenreView>(),
    BrowseGenrePresenter {


  override fun attach(view: BrowseGenreView) {
    super.attach(view)
    bus.register(this, LibraryRefreshCompleteEvent::class.java, { load() })
  }

  override fun detach() {
    super.detach()
    bus.unregister(this)
  }

  override fun load() {
    view().showLoading()
    addDisposable(repository.getAllCursor().compose { schedule(it) }.subscribe({
      view().update(it)
      view().hideLoading()
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view().failure(it)
      view().hideLoading()
    }))
  }


  override fun reload() {
    view().showLoading()
    addDisposable(repository.getAndSaveRemote().compose { schedule(it) }.subscribe({
      view().update(it)
      view().hideLoading()
    }, {
      Timber.v(it, "Error while loading the data from the database")
      view().failure(it)
      view().hideLoading()
    }))
  }

  private fun schedule(it: Single<FlowCursorList<Genre>>) = it.observeOn(mainScheduler)
      .subscribeOn(ioScheduler)

}

