package editor.handler;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

import javax.swing.TransferHandler;

import editor.gui.FFaplAPIEditorJFrame;

public class FileTransferHandler extends TransferHandler {

	FFaplAPIEditorJFrame frame;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8726608157924269915L;
	
	public FileTransferHandler(FFaplAPIEditorJFrame jFrameMain) {
		frame = jFrameMain;
	}

	public boolean canImport(TransferHandler.TransferSupport support) {
        return true;
    }

	/*
	 * imports the loaded file
	 * @param support the TransferSupport object to be used for file transfer
	 * @return true if import succeeded, false otherwise
	 */
    @SuppressWarnings("unchecked")
	public boolean importData(TransferHandler.TransferSupport support) {
        Transferable transferable = support.getTransferable();

        try {
            java.util.List<File> files = (java.util.List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);

            if (!files.isEmpty()) {
            	File file = files.get(0);
            	frame.fileLoaded(file);
            }
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
