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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import com.alee.extended.panel.*;
import com.alee.laf.button.*;

import action.*;
import core.*;
import gui.*;
import gui.docking.*;
import gui.prueckl.draw.*;

/**
 * present component and information about the game.
 * 
 * @author terry
 *
 */
public class ScreenRegions extends UIComponentPanel implements DockingComponent, ItemListener {

	// private JScrollPane scrollPane;
	private JPanel mainJPanel;
	private DrawingPanel drawingPanel;
	private WebToggleButton testButton, runButton, stopButton;
	private JEditorPane console;

	public ScreenRegions() {
		super(null, false);
		runButton = TUIUtils.getWebToggleButtonForToolBar("RunTrooper");
		runButton.addItemListener(this);
		testButton = TUIUtils.getWebToggleButtonForToolBar("TestTrooper");
		testButton.addItemListener(this);
		stopButton = TUIUtils.getWebToggleButtonForToolBar("StopTrooper");
		stopButton.addItemListener(this);

		TAbstractAction ta = new TAbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Hero.trooper.getSensorsArray().takeSample();
			}
		};

		LoadProperty load = new LoadProperty("DrawEditor") {
			public void actionPerformed2() {
				drawingPanel = (DrawingPanel) getValue();
				Hero.trooper.setEnviorement(drawingPanel);
				createPanel();
			};
		};
		load.setDefaultValues(LoadProperty.class.getSimpleName());

		setToolBar(load, ta);

		WebButtonGroup g = TUIUtils.getButtonGroup();
		g.add(runButton);
		g.add(stopButton);
		g.add(testButton);
		getToolBar().add(g);

		this.console = TUIUtils.getMessageConsole();
		JPanel jp = new JPanel(new BorderLayout(4, 4));
		JScrollPane jsp = new JScrollPane(console);
		jsp.setPreferredSize(new Dimension(100, 150));

		this.mainJPanel = new JPanel(new BorderLayout());
		jp.add(mainJPanel, BorderLayout.CENTER);
		jp.add(jsp, BorderLayout.SOUTH);
		addWithoutBorder(jp);
	}

	public DrawingPanel getDrawingPanel() {
		return drawingPanel;
	}

	/**
	 * create the panel whit my cards and comunity cards
	 * 
	 * @return
	 */
	private JPanel createCardPanel() {
		JPanel mycard = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		mycard.setBorder(new TitledBorder("My cards"));
		mycard.add(Hero.trooper.getSensorsArray().getScreenSensor("my_card1"));
		mycard.add(Hero.trooper.getSensorsArray().getScreenSensor("my_card2"));
		mycard.add(Hero.trooper.getSensorsArray().getScreenSensor("hero.button"));
		mycard.add(Hero.trooper.getSensorsArray().getScreenSensor("hero.call"));
		mycard.add(Hero.trooper.getSensorsArray().getScreenSensor("pot"));

		JPanel comcard = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		comcard.setBorder(new TitledBorder("Comunity cards"));
		comcard.add(Hero.trooper.getSensorsArray().getScreenSensor("flop1"));
		comcard.add(Hero.trooper.getSensorsArray().getScreenSensor("flop2"));
		comcard.add(Hero.trooper.getSensorsArray().getScreenSensor("flop3"));
		comcard.add(Hero.trooper.getSensorsArray().getScreenSensor("turn"));
		comcard.add(Hero.trooper.getSensorsArray().getScreenSensor("river"));

		JPanel pot = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		pot.setBorder(new TitledBorder("Pot"));

		// my card + community + pot
		JPanel mcpo = new JPanel(new GridLayout(0, 2, 4, 4));
		mcpo.add(mycard);
		mcpo.add(comcard);
		// mcpo.add(pot);
		return mcpo;

	}

	/**
	 * create a panel for all configured villans. v1|v1|v2|...
	 * 
	 * @return
	 */
	private JPanel createVillansPanel() {
		int tv = Hero.trooper.getSensorsArray().getVillans();
		JPanel villans = new JPanel(new GridLayout(1, tv, 4, 4));
		for (int i = 1; i <= tv; i++) {
			JPanel vinf_p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
			vinf_p.add(Hero.trooper.getSensorsArray().getScreenSensor("villan" + i + ".name"));
			vinf_p.add(Hero.trooper.getSensorsArray().getScreenSensor("villan" + i + ".call"));

			JPanel vcar_p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
			vcar_p.add(Hero.trooper.getSensorsArray().getScreenSensor("villan" + i + ".card1"));
			vcar_p.add(Hero.trooper.getSensorsArray().getScreenSensor("villan" + i + ".card2"));
			vcar_p.add(Hero.trooper.getSensorsArray().getScreenSensor("villan" + i + ".button"));

			JPanel jp = new JPanel(new GridLayout(2, 0, 4, 4));
			jp.add(vcar_p);
			jp.add(vinf_p);
			jp.setBorder(new TitledBorder("villan " + i));

			villans.add(jp);
		}
		// villans.setBorder(new TitledBorder("Villans"));
		return villans;
	}

	private JPanel createActionAreaPanel() {
		JPanel aapanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		aapanel.setBorder(new TitledBorder("Buttons & actions"));
		Vector<ScreenSensor> btns = Hero.trooper.getSensorsArray().getActionAreas();
		for (ScreenSensor btn : btns) {
			aapanel.add(btn);
		}
		return aapanel;
	}
	/**
	 * create the {@link ScreenSensor} array plus all UI components
	 * 
	 */
	private void createPanel() {

		mainJPanel.removeAll();
		// left panel: all sensors from the screen
		// JPanel jpleft = new JPanel(new GridLayout(3, 0, 4, 4));
		JPanel arrayjp = new JPanel();
		arrayjp.setLayout(new BoxLayout(arrayjp, BoxLayout.Y_AXIS));
		arrayjp.add(createCardPanel());
		arrayjp.add(createVillansPanel());
		arrayjp.add(createActionAreaPanel());

		// sensor array + pokerprothesis
		// JPanel jp = new JPanel(new GridLayout(0, 2, 4, 4));
		// jp.add(jpleft);

		JComponent jl = Hero.trooper.getPokerSimulator().getInfoJTextArea();
		// jl.setBorder(new TitledBorder("Simulation"));
		JTabbedPane jtp = new JTabbedPane();
		jtp.add(new JScrollPane(arrayjp), "Sensor Array");
		jtp.add(jl, "simulator data");

		mainJPanel.add(jtp, BorderLayout.CENTER);

		// jp.add(jl);

		// mainJPanel.add(jp, BorderLayout.CENTER);
		// scrollPane.setViewportView(jp);
	}

	@Override
	public void init() {

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// Object src = evt.getSource();
		// Object newv = evt.getNewValue();

	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object src = e.getSource();
		if (src.equals(testButton)) {
			Hero.trooper.setTestMode(true);
			Hero.trooper.start();
		}
		if (src.equals(runButton)) {
			console.setText("");
			Hero.trooper.setTestMode(false);
			Hero.trooper.start();
		}
		if (src.equals(stopButton)) {
			Hero.trooper.stop();
		}
	}
}
