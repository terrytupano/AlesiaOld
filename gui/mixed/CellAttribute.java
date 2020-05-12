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
/*
 * (swing1.1beta3)
 * 
 */

package gui.mixed;

import java.awt.*;

/**
 * @version 1.0 11/22/98
 */

public interface CellAttribute {

  public void addColumn();

  public void addRow();

  public void insertRow(int row);

  public Dimension getSize();

  public void setSize(Dimension size);


}
