package gui.prueckl.draw;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;

import action.*;

public class GetImageFromClipboard extends TAbstractAction {

	private DrawingEditor editor;
	public GetImageFromClipboard(DrawingEditor de) {
		this.editor = de;
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			try {
				BufferedImage img = (BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor);
				editor.getDrawingPanel().setBackgroundImage(img);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
