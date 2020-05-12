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
package plugin.xdoc;

import gui.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;

import javax.swing.*;


import action.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.layout.*;

import core.*;
import core.datasource.*;

public class DocumentsRecord extends AbstractRecordDataInput implements PropertyChangeListener {

	private JButton fileJB;
	private NativeFileChooser openFile;
	private byte[] docData;
	private Record rcd;

	public DocumentsRecord(Record r, boolean newr) {
		super(null, r);
		this.rcd = r;
		if (!newr) {
			docData = (byte[]) rcd.getFieldValue("xd_codata");
		}
		this.openFile = new NativeFileChooser(NativeFileChooser.OPEN_STYLE, this);
		openFile.addChoosableFileFilter("Microsoft Word", "docx");
		openFile.addChoosableFileFilter("OppeOffice Document", "odt");
		this.fileJB = new JButton(newr ? " " : (String) rcd.getFieldValue("xd_coname"));
		fileJB.setCursor(new Cursor(Cursor.HAND_CURSOR));
		fileJB.setToolTipText(TStringUtils.getBundleString("ttxd_coname"));
		fileJB.setFocusable(false);
		fileJB.addActionListener(this);
		addInputComponent("xd_codescription", TUIUtils.getJTextArea(rcd, "xd_codescription"), true, true);
		addInputComponent("xd_coproperties", TUIUtils.getTPropertyJTable(r, "xd_coproperties"), false, true);

		// firma digital
		String patt = "BEGIN:VCARD" +
		";VERSION:2.1" +
		";N:$name.variable" +  
		";ROLE:$job.variable" + 
		";ORG:Company name"+ 
		";ADR:" + 
		";TEL:(123)-456-78-90" + 
		";EMAIL:email@domain.com" + 
		";NOTE:Generated by Alesia xDoc plugin. Reference: $document.certificate" + 
		";END:VCARD";
		String[] vcard = patt.split(";");
		if (!newr) {
			vcard = rcd.getFieldValue("xd_coqrcode").toString().split(";");
		}
		JTextField jtf = TUIUtils.getJTextField("ttvcard_N", vcard[2].substring(2), 30);
		addInputComponent("vcard_N", jtf, true, true);
		
		jtf = TUIUtils.getJTextField("ttvcard_ROLE", vcard[3].substring(5), 30);
		addInputComponent("vcard_ROLE", jtf, false, true);
		
		jtf = TUIUtils.getJTextField("ttvcard_ORG", vcard[4].substring(4), 30);
		addInputComponent("vcard_ORG", jtf, false, true);
		
		jtf = TUIUtils.getJTextField("ttvcard_ADR", vcard[5].substring(4), 30);
		addInputComponent("vcard_ADR", jtf, false, true);
		
		jtf = TUIUtils.getJTextField("ttvcard_TEL", vcard[6].substring(4), 30);
		addInputComponent("vcard_TEL", jtf, false, true);
		
		jtf = TUIUtils.getJTextField("ttvcard_EMAIL", vcard[7].substring(6), 30);
		addInputComponent("vcard_EMAIL", jtf, false, true);

		addInputComponent("vcard_NOTE", TUIUtils.getJTextArea("ttvcard_NOTE", vcard[8].substring(5), 90, 2), false, true);

		JTabbedPane jtp = new JTabbedPane();
		JPanel jpa = getGeneralPanel();
		TUIUtils.setEmptyBorder(jpa);
		jtp.add(TStringUtils.getBundleString("xdoc.cfg.general"), jpa);
		jpa = getQRCodePanel();
		TUIUtils.setEmptyBorder(jpa);
		jtp.add(TStringUtils.getBundleString("xdoc.cfg.dsign"), jpa);

		setDefaultActionBar();
		add(jtp);
		preValidate(null);
	}

	/**
	 * create qrcode panel 
	 * 
	 * @return panel with input componentes
	 */

	private JPanel getQRCodePanel() {
		FormLayout lay = new FormLayout("left:pref, 150dlu", // 7dlu, left:pref, 3dlu, pref", // columns
				"p, p, 3dlu, p, p, 3dlu,p, p, 3dlu,p, p, 3dlu,p, p, 3dlu,p, p, 3dlu,p, p, 3dlu,p, p"); // rows
		CellConstraints cc = new CellConstraints();
		PanelBuilder build = new PanelBuilder(lay);

		build.add(getLabelFor("vcard_N"), cc.xy(1, 1));
		build.add(getInputComponent("vcard_N"), cc.xyw(1, 2, 2));
		build.add(getLabelFor("vcard_ROLE"), cc.xy(1, 4));
		build.add(getInputComponent("vcard_ROLE"), cc.xyw(1, 5, 2));
		build.add(getLabelFor("vcard_ORG"), cc.xy(1, 7));
		build.add(getInputComponent("vcard_ORG"), cc.xyw(1, 8, 2));
		build.add(getLabelFor("vcard_ADR"), cc.xy(1, 10));
		build.add(getInputComponent("vcard_ADR"), cc.xyw(1, 11, 2));
		build.add(getLabelFor("vcard_TEL"), cc.xy(1, 13));
		build.add(getInputComponent("vcard_TEL"), cc.xyw(1, 14, 2));
		build.add(getLabelFor("vcard_EMAIL"), cc.xy(1, 16));
		build.add(getInputComponent("vcard_EMAIL"), cc.xyw(1, 17, 2));
		build.add(getLabelFor("vcard_NOTE"), cc.xy(1, 19));
		build.add(getInputComponent("vcard_NOTE"), cc.xyw(1, 20, 2));

		return build.getPanel();
	}

	/**
	 * create general panel 
	 * 
	 * @return panel with input componentes
	 */
	private JPanel getGeneralPanel() {
		FormLayout lay = new FormLayout("left:pref, 3dlu, pref, 150dlu", // 7dlu, left:pref, 3dlu, pref", // columns
				"p, 3dlu, p, 3dlu, p, p, 3dlu, p, 150dlu"); // rows
		CellConstraints cc = new CellConstraints();
		PanelBuilder build = new PanelBuilder(lay);

		build.add(TUIUtils.getJLabel("xd_coname", true, true), cc.xy(1, 1));
		build.add(fileJB, cc.xyw(3, 1, 2));
		build.add(getLabelFor("xd_codescription"), cc.xy(1, 5));
		build.add(getInputComponent("xd_codescription"), cc.xyw(1, 6, 4));
		build.add(getLabelFor("xd_coproperties"), cc.xy(1, 8));
		build.add(getInputComponent("xd_coproperties"), cc.xyw(1, 9, 4));

		return build.getPanel();
	}

	@Override
	public void preValidate(Object src) {
		super.preValidate(src);
		if (!isShowingError()) {
			// setEnableDefaultButton(true);
			if (fileJB.getText().equals(" ")) {
				setEnableDefaultButton(false);
				return;
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(TConstants.FILE_SELECTED)) {
			try {
				File sf = (File) evt.getNewValue();
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sf));
				// verifica longitud
				int flen = 0;
				if (flen / 1024 < 16000) {
					docData = new byte[(int) sf.length()];
					bis.read(docData);
					bis.close();
					fileJB.setText(sf.getName());
				} else {
					showAplicationExceptionMsg("xdoc.msg24");
				}
				preValidate(fileJB);
			} catch (Exception e) {
				SystemLog.logException(e);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		super.actionPerformed(ae);
		if (ae.getSource() == fileJB) {
			Object[] options = { TStringUtils.getBundleString("xdoc.selectnew"), TStringUtils.getBundleString("xdoc.selectcancel") };
			int o = JOptionPane.showOptionDialog(null, TStringUtils.getBundleString("xdoc.msg06"),
					TStringUtils.getBundleString("xdoc.editd"), JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			if (o == 0) {
				openFile.actionPerformed(null);
			}
		}
	}

	@Override
	public Record getRecord() {
		Record rcd = super.getRecord();
		rcd.setFieldValue("xd_coname", fileJB.getText());
		rcd.setFieldValue("xd_codata", docData);
		Hashtable ht = getFields();
		// store ; character. removed before documento generation
		String bizc = 
			"BEGIN:VCARD" +
			";VERSION:2.1" +
			";N:" + ht.get("vcard_N") +  
			";ROLE:" + ht.get("vcard_ROLE") + 
			";ORG:" + ht.get("vcard_ORG") + 
			";ADR:" + ht.get("vcard_ADR") + 
			";TEL:" + ht.get("vcard_TEL") + 
			";EMAIL:" + ht.get("vcard_EMAIL") + 
			";NOTE:" + ht.get("vcard_NOTE") + 
			";END:VCARD";
		rcd.setFieldValue("xd_coqrcode", bizc);
		return rcd;
	}
}