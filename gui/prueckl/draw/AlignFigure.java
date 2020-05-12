package gui.prueckl.draw;

import java.awt.*;
import java.awt.event.*;

import action.*;

public class AlignFigure extends TAbstractAction {

	public static String TOP = "Top";
	public static String LEFT = "Left";

	public static String JUSTIFY_HORIZONTAL = "JustifyHorizontal";
	public static String JUSTIFY_VERTICAL = "JustifyVertical";

	private String alignName;
	private DrawingEditor drawingEditor;

	public AlignFigure(DrawingEditor de, String alg) {
		super(TAbstractAction.NO_SCOPE);
		this.alignName = alg;
		this.drawingEditor = de;
		setDefaultValues("Align" + alignName);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Figure firsts = drawingEditor.getDrawingPanel().getFirstSelectedFigure();
		if (firsts == null) {
			return;
		}
		Rectangle figrec = firsts.getBounds();
		for (Object obj : drawingEditor.getDrawingPanel().getFigures()) {
			Figure f = (Figure) obj;
			if (f.isSelected() && f != firsts) {
				Rectangle fr = f.getBounds();
				if (alignName == TOP) {
					fr.y = figrec.y;
				}
				if (alignName == LEFT) {
					fr.x = figrec.x;
				}
				if (alignName == JUSTIFY_HORIZONTAL) {
					fr.width = figrec.width;
				}
				if (alignName == JUSTIFY_VERTICAL) {
					fr.height = figrec.height;
				}
				f.setBounds(fr);
			}
		}
		drawingEditor.getDrawingPanel().repaint();
	}
}
