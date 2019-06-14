package editor.gui;

import javax.swing.JEditorPane;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentListener;

public class FFaplAPIEditorPane extends JEditorPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private DocumentListener listener;
	
	public FFaplAPIEditorPane(DocumentListener docListener, CaretListener caretListener) {
		listener = docListener;
		getDocument().addDocumentListener(docListener);
		addCaretListener(caretListener);
	}
	
	public void setText(String text) {
		getDocument().removeDocumentListener(listener);
		super.setText(text);
		getDocument().addDocumentListener(listener);
	}
}
