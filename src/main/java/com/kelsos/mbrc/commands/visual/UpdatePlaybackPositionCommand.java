package com.kelsos.mbrc.commands.visual;

import com.google.inject.Inject;
import com.kelsos.mbrc.events.ui.UpdatePosition;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.utilities.MainThreadBusWrapper;
import com.squareup.otto.Bus;
import org.codehaus.jackson.node.ObjectNode;

public class UpdatePlaybackPositionCommand implements ICommand
{
	@Inject MainThreadBusWrapper bus;

	public void execute(IEvent e)
	{
        ObjectNode oNode = (ObjectNode)e.getData();
        bus.post(new UpdatePosition(oNode.path("current").asInt(),oNode.path("total").asInt()));
	}
}
