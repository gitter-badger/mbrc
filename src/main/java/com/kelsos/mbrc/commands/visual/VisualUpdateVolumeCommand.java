package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.VolumeChange;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;

public class VisualUpdateVolumeCommand implements ICommand
{
	@Inject MainThreadBusWrapper bus;
	@Inject MainDataModel model;

	public void execute(IEvent e)
	{
        bus.post(new VolumeChange(model.getVolume()));
	}
}
