package gui.prueckl.draw;

import gui.*;
import gui.docking.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;

import action.*;

import com.alee.extended.panel.*;
import com.alee.laf.button.*;

import core.*;

public class DrawingEditor extends UIComponentPanel implements DockingComponent {

	public DrawingPanel getDrawingPanel() {
		return currentDrawing;
	}

	private DrawingPanel currentDrawing;
	private JScrollPane scrollPane;
	private WebButton deleteButton;
	public DrawingEditor() {
		super(null, false);
		this.currentDrawing = new DrawingPanel();

		LoadProperty load = new LoadProperty("DrawEditor") {
			public void actionPerformed2() {
				remove(currentDrawing);
				currentDrawing = null;
				currentDrawing = (DrawingPanel) getValue();
				currentDrawing.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
				scrollPane.setViewportView(currentDrawing);
				deleteButton.setAction(currentDrawing.getDeleteAction());
			};
		};
		load.setDefaultValues(LoadProperty.class.getSimpleName());

		SaveProperty save = new SaveProperty("DrawEditor") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setValue(currentDrawing);
				super.actionPerformed(arg0);
			}
			public void actionPerformed2() {
			};
		};
		save.setDefaultValues(SaveProperty.class.getSimpleName());

		// app actions
		setToolBar(false, load, save);

		// edit actions
		WebButtonGroup group = TUIUtils.getButtonGroup();
		this.deleteButton = TUIUtils.getWebButtonForToolBar(currentDrawing.getDeleteAction());
		group.add(deleteButton);
		group.add(TUIUtils.getWebButtonForToolBar(new GetImageFromClipboard(this)));
		group.add(TUIUtils.getWebButtonForToolBar(new SetImageToClipboard(this)));
		group.add(TUIUtils.getWebButtonForToolBar(new FigureProperties(this)));
		getToolBar().add(group);

		// align buttons
		addToolBarAction(new AlignFigure(this, AlignFigure.TOP), new AlignFigure(this, AlignFigure.LEFT),
				new AlignFigure(this, AlignFigure.JUSTIFY_HORIZONTAL),
				new AlignFigure(this, AlignFigure.JUSTIFY_VERTICAL));

		group = TUIUtils.getButtonGroup();
		// figures and conn
		JToggleButton sel = TUIUtils.getWebToggleButtonForToolBar(null, group);
		sel.setIcon(TResourceUtils.getSmallIcon("selection"));
		sel.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				DrawingPanel.select = ((AbstractButton) e.getSource()).isSelected();
				Drawable.activeDrawableClass = null;
			}
		});

		TUIUtils.getWebToggleButtonForToolBar(Drawable.getAction(RectangleFigure.class), group);
		TUIUtils.getWebToggleButtonForToolBar(Drawable.getAction(EllipseFigure.class), group);
		TUIUtils.getWebToggleButtonForToolBar(Drawable.getAction(LineConn.class), group);
		TUIUtils.getWebToggleButtonForToolBar(Drawable.getAction(CurveConn.class), group);
		getToolBar().add(group);

		// set the preferred size to the size of the screem
		currentDrawing.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		scrollPane = new JScrollPane(currentDrawing);
		addWithoutBorder(scrollPane);

		// 191011: startting coding again in my new camp in Heidenauer. after almost 1 month of my transfer from
		// hamburger straﬂe

		// auto selection at star
		sel.doClick();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}

	@Override
	public void init() {

	}
}
