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
package gui.docking;

import gui.*;

import java.beans.*;
import java.util.*;

import javax.swing.*;

import action.*;
import core.*;
import core.datasource.*;
import core.reporting.*;

/**
 * Basic support for display records from T_SYSTEM_LOG file. This implementation
 * <ul>
 * <li>show list form database only for the user in session and the flag variable. (t_slflag)
 * <li>basic suport for {@link DisplayRecord} and clear log based on {@link #whereClause} variable
 * </ul>
 * 
 * in general scenario, subclass only are interested on chage the {@link #flag} variable.
 * 
 * @author terry
 * 
 */
public class BasicLogList extends UIListPanel implements DockingComponent {

	protected ServiceRequest request;
	protected String whereClause;
	protected DeleteRecord2 deleteRecord2;
	protected String flag;

	public BasicLogList() {
		super(null);
		this.request = new ServiceRequest(ServiceRequest.DB_QUERY, "t_system_log", null);
		// clear log
		this.deleteRecord2 = new DeleteRecord2(this) {
			@Override
			public void actionPerformed2() {
				Vector<Record> rl = getTableModel().getRecords();
				Record[] rcds = new Record[rl.size()];
				rl.copyInto(rcds);
				for (Record rcd : rcds) {
					ConnectionManager.getAccessTo(rcd.getTableName()).delete(rcd);
				}
				freshen();
			}
		};
		deleteRecord2.setScope(TAbstractAction.TABLE_SCOPE);
		deleteRecord2.setEnabled(true);
		deleteRecord2.setDefaultValues("ClearLog");
		deleteRecord2.setMessagePrefix("action.ClearLog.");

		setToolBar(new DisplayRecord(this), deleteRecord2, new ExportToFileAction(this, ""));
		setColumns("t_sldatetime;t_slmessage");
		setIconParameters("0;;t_sltype");
	}
	@Override
	public UIComponentPanel getUIFor(AbstractAction aa) {
		return null;
	}

	@Override
	public void init() {
		this.whereClause = "T_SLUSERID = '" + Session.getUserName() + "' AND T_SLFLAG = '" + flag + "'";
		request.setData(whereClause);
		setServiceRequest(request);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}
}
