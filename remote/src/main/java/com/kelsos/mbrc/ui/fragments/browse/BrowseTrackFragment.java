package com.kelsos.mbrc.ui.fragments.browse;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.inject.Inject;
import com.kelsos.mbrc.R;
import com.kelsos.mbrc.adapters.TrackAdapter;
import com.kelsos.mbrc.annotations.Queue;
import com.kelsos.mbrc.domain.Track;
import com.kelsos.mbrc.presenters.BrowseTrackPresenter;
import com.kelsos.mbrc.ui.dialogs.PlaylistDialogFragment;
import com.kelsos.mbrc.ui.views.BrowseTrackView;
import java.util.List;
import roboguice.fragment.RoboFragment;

public class BrowseTrackFragment extends RoboFragment
    implements PlaylistDialogFragment.OnPlaylistSelectedListener, BrowseTrackView,
    TrackAdapter.MenuItemSelectedListener {

  @Bind(R.id.library_recycler) RecyclerView recyclerView;
  @Inject private TrackAdapter mAdapter;
  @Inject private BrowseTrackPresenter presenter;

  @NonNull public static BrowseTrackFragment newInstance() {
    return new BrowseTrackFragment();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_library, container, false);
    ButterKnife.bind(this, view);
    presenter.bind(this);
    RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
    recyclerView.setLayoutManager(manager);
    mAdapter.setMenuItemSelectedListener(this);
    return view;
  }

  @Override public void onStart() {
    super.onStart();
    presenter.load();
  }

  @Override public void onPlaylistSelected(String hash) {

  }

  @Override public void onNewPlaylistSelected() {

  }

  @Override public void onMenuItemSelected(MenuItem item, Track track) {
    switch (item.getItemId()) {
      case R.id.popup_track_play:
        presenter.queue(track, Queue.NOW);
        break;
      case R.id.popup_track_playlist:
        break;
      case R.id.popup_track_queue_next:
        presenter.queue(track, Queue.NEXT);
        break;
      case R.id.popup_track_queue_last:
        presenter.queue(track, Queue.LAST);
        break;
      default:
        break;
    }
  }

  @Override public void onItemClicked(Track track) {

  }

  @Override public void update(List<Track> tracks) {
    mAdapter.updateData(tracks);
  }
}
