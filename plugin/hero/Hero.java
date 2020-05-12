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

import gui.datasource.*;

import javax.swing.*;

import net.sourceforge.tess4j.*;
import core.*;

public class Hero extends PluginAdapter {

	protected static Tesseract iTesseract;
	protected static Trooper trooper;

	@Override
	public Object executePlugin(Object obj) {
		JMenu jm = new JMenu(myProperties.getProperty("plugin.caption"));
		// MenuActionFactory maf = new MenuActionFactory(ConnectionProfile.class);
		// maf.setDimentionFactor(-1);
		jm.add(new ConnectionProfileAction(true, true));
		jm.addSeparator();
		jm.add(new HeroAction());

		// init the enviorement
		iTesseract = new Tesseract(); // JNA Interface Mapping
		iTesseract.setDatapath("plugin/hero/tessdata"); // path to tessdata directory
		// iTesseract.setLanguage("pok");

		trooper = new Trooper();

		return jm;
	}

	public static void logInfo(String txt) {
//		log("info", txt);
	}
	public static void logGame(String txt) {
		log("game", txt);
	}
	public static void logDebug(String txt) {
		String mn = Thread.currentThread().getStackTrace()[2].getMethodName();
//		log("fine", mn + ": " + txt);
	}
	private static void log(String level, String txt) {
		System.out.println("["+level + "] " + txt);
	}

	public static void logPerformance(String txt, long t1) {
		// StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		// the 3th element contain the method´s name who call this method
		String mn = Thread.currentThread().getStackTrace()[2].getMethodName();
		String sp = TStringUtils.formatSpeed(System.currentTimeMillis() - t1);
//		log("fine", mn + ": " + txt + ": " + sp);
	}

	/**
	 * This metod is separated because maybe in the future we will need diferents robot for diferent graphics
	 * configurations
	 * 
	 * @return
	 */
	public static Robot getNewRobot() {
		Robot r = null;
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return r;
	}
}
