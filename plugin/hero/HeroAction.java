/*******************************************************************************
 * Copyright (C) 2017 terry.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     terry - initial API and implementation
 ******************************************************************************/
package plugin.hero;

import gui.docking.*;
import gui.prueckl.draw.*;

import java.awt.event.*;

import net.infonode.docking.*;
import action.*;

public class HeroAction extends TAbstractAction {

	public HeroAction() {
		super(NO_SCOPE);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// columns, data
		View[] views = new View[]{DockingContainer.createDynamicView(ScreenRegions.class.getName()),
				DockingContainer.createDynamicView(DrawingEditor.class.getName()),};
		TabWindow cent = new TabWindow(views);
		// SplitWindow sw = new SplitWindow(true, 0.3f,
		// DockingContainer.createDynamicView(SourceDBTables.class.getName()),
		// cent);

		// south tab
		// views = new View[]{DockingContainer.createDynamicView(ScriptList.class.getName()),
		// DockingContainer.createDynamicView(TweezerLog.class.getName())};
		// TabWindow inc = new TabWindow(views);
		// SplitWindow sw1 = new SplitWindow(false, 0.7f, sw, inc);
		DockingContainer.setWindow(cent, getClass().getName());
	}
}
