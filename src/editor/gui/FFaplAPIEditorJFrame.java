package editor.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Utilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.UndoManager;

import editor.converter.Converter;
import editor.mapper.FileMapper;
import editor.handler.FileTransferHandler;
import editor.handler.BinaryFileHandler;
import editor.handler.TextFileHandler;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import java.io.File;
import java.util.Date;

import javax.swing.JSplitPane;
import javax.swing.JTree;
import java.awt.Dimension;

import sunset.gui.api.APITreeCellRenderer;
import sunset.gui.api.spec.*;
import sunset.gui.api.util.ApiUtil;

import javax.swing.JScrollPane;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;

import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.InputEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import javax.swing.JSeparator;

public class FFaplAPIEditorJFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9101983091918451518L;
	
	{
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private final String APP_NAME = "FFapl API Editor";
	private static FFaplAPIEditorJFrame frame;
	private File loadedFile = null;
	private FFaplAPIEditorPane editorPane;
	private JTree jTreeAPI;
	private JScrollPane jScrollPaneTree;
	private JTextField tfStatus;
	private JButton jButtonSave;
	private JButton jButtonSaveAsText;
	private JButton jButtonConvertToBinary;
	private JMenuItem mntmSave;
	private JMenuItem mntmCut;
	private JMenuItem mntmCopy;
	private JMenuItem mntmPaste;
	private JTextField tfLineNumber;
	private JComboBox<String> jComboBoxTextFormat;
	private JButton jButtonWarnings;
	private UndoManager undoManager;
	private JMenuItem mntmUndo;
	private JMenuItem mntmRedo;
	private JSeparator separator_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new FFaplAPIEditorJFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FFaplAPIEditorJFrame() {
		setTitle(APP_NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1224, 768);
		
		JMenuBar jMenuBarMain = new JMenuBar();
		setJMenuBar(jMenuBarMain);
		
		undoManager = new UndoManager();
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (jButtonSave.isEnabled()) {
					int result = JOptionPane.showConfirmDialog(frame, "File not saved! Do you want to exit anyway?", "Warning", JOptionPane.YES_NO_OPTION);
					
					if (result == JOptionPane.YES_OPTION)
						System.exit(1);
					else
						frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				}
			}
		});
		
		JMenu mnFile = new JMenu("File");
		jMenuBarMain.add(mnFile);
		
		mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		mntmSave.setEnabled(false);
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		
		ActionListener actionListenerOpen = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				String description = "." + FileMapper.BIN_EXT + ", ." + FileMapper.JSON_EXT + ", ." + FileMapper.YAML_EXT;
		    	FileNameExtensionFilter filter = new FileNameExtensionFilter(description, FileMapper.BIN_EXT, FileMapper.JSON_EXT, FileMapper.YAML_EXT);
		    	fileChooser.setFileFilter(filter);
				
				int returnVal = fileChooser.showOpenDialog(frame);
				
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = fileChooser.getSelectedFile();
					fileLoaded(file);
				}
			}
		};
		
		mntmOpen.addActionListener(actionListenerOpen);
		mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mntmOpen.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/open16.png")));
		mnFile.add(mntmOpen);
		mntmSave.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/save.png")));
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mntmSave);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});
		
		separator_1 = new JSeparator();
		mnFile.add(separator_1);
		mnFile.add(mntmExit);
		
		JMenu mnEdit = new JMenu("Edit");
		mnEdit.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				boolean fileLoaded = (loadedFile != null);
				mntmCut.setEnabled(fileLoaded);
		    	mntmCopy.setEnabled(fileLoaded);
		    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    	boolean textInClipboard = clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
		    	mntmPaste.setEnabled(textInClipboard && fileLoaded);
		    	mntmUndo.setEnabled(undoManager.canUndo() && fileLoaded);
		    	mntmRedo.setEnabled(undoManager.canRedo() && fileLoaded);
			}
		});
		jMenuBarMain.add(mnEdit);
		
		mntmCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
		mntmCopy.setEnabled(false);
		mntmCopy.setText("Copy");
		mntmCopy.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/copy16.png")));
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntmCopy);
		
		mntmCut = new JMenuItem(new DefaultEditorKit.CutAction());
		mntmCut.setEnabled(false);
		mntmCut.setText("Cut");
		mntmCut.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/cut16.png")));
		mntmCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntmCut);
		
		mntmPaste = new JMenuItem(new DefaultEditorKit.PasteAction());
		mntmPaste.setEnabled(false);
		mntmPaste.setText("Paste");
		mntmPaste.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/paste16.png")));
		mntmPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntmPaste);
		
		JSeparator separator = new JSeparator();
		mnEdit.add(separator);
		
		mntmUndo = new JMenuItem("Undo");
		mntmUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (undoManager.canUndo())
					undoManager.undo();
			}
		});
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		mntmUndo.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/undo.png")));
		mnEdit.add(mntmUndo);
		
		mntmRedo = new JMenuItem("Redo");
		mntmRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (undoManager.canRedo())
					undoManager.redo();
			}
		});
		mntmRedo.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/redo.png")));
		mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		mnEdit.add(mntmRedo);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JSplitPane jSplitPaneMain = new JSplitPane();
		panel.add(jSplitPaneMain);
		
		JScrollPane jScrollPaneEditor = new JScrollPane();
		jSplitPaneMain.setLeftComponent(jScrollPaneEditor);
		
		DocumentListener docListener = new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				fileStatusChanged(true);
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				fileStatusChanged(true);
			}
		};
		
		CaretListener caretListener = new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				int pos = editorPane.getCaretPosition();
				int row = (pos==0) ? 1 : 0;
				
		        try {
		            while(pos > 0) {
		            	pos = Utilities.getRowStart(editorPane, pos) -1;
		            	row++;
		            }
		        } catch (BadLocationException e) {
		        }

				tfLineNumber.setText("Line number: " + row);
			}
		};
		
		editorPane = new FFaplAPIEditorPane(docListener, caretListener);
		editorPane.setEnabled(false);
		editorPane.setPreferredSize(new Dimension(2, 2));
		editorPane.getDocument().addUndoableEditListener(undoManager);
		jScrollPaneEditor.setViewportView(editorPane);
		
		jComboBoxTextFormat = new JComboBox<String>();
		jComboBoxTextFormat.setEnabled(false);
		jScrollPaneEditor.setColumnHeaderView(jComboBoxTextFormat);
		jComboBoxTextFormat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textFormatSelectionChanged();
			}
		});
		jComboBoxTextFormat.setMinimumSize(new Dimension(2, 2));
		jComboBoxTextFormat.setPreferredSize(new Dimension(2, 24));
		jComboBoxTextFormat.setModel(new DefaultComboBoxModel<String>(new String[] {FileMapper.JSON_EXT, FileMapper.YAML_EXT}));
		jComboBoxTextFormat.setSelectedIndex(0);
		
		jSplitPaneMain.setResizeWeight(0.5);
		
		jScrollPaneTree = new JScrollPane();
		jSplitPaneMain.setRightComponent(jScrollPaneTree);
		
		JPanel jPanelStatus = new JPanel();
		contentPane.add(jPanelStatus, BorderLayout.SOUTH);
		jPanelStatus.setLayout(new BorderLayout(0, 0));
		
		tfLineNumber = new JTextField();
		tfLineNumber.setPreferredSize(new Dimension(2, 22));
		tfLineNumber.setMaximumSize(new Dimension(100, 2147483647));
		tfLineNumber.setFont(new Font("Tahoma", Font.PLAIN, 10));
		tfLineNumber.setText("Line number");
		tfLineNumber.setEditable(false);
		jPanelStatus.add(tfLineNumber, BorderLayout.WEST);
		tfLineNumber.setColumns(10);
		
		tfStatus = new JTextField();
		tfStatus.setText("Status");
		tfStatus.setEditable(false);
		jPanelStatus.add(tfStatus);
		tfStatus.setColumns(10);
		
		JToolBar jToolBarTools = new JToolBar();
		contentPane.add(jToolBarTools, BorderLayout.NORTH);
		
		jButtonSave = new JButton("");
		jButtonSave.setToolTipText("Save (Ctrl+S)");
		jButtonSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		
		JButton jButtonOpen = new JButton("");
		jButtonOpen.setToolTipText("Open (Ctrl+O)");
		jButtonOpen.addActionListener(actionListenerOpen);
		jButtonOpen.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/open.png")));
		jToolBarTools.add(jButtonOpen);
		jButtonSave.setEnabled(false);
		jButtonSave.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/save24.png")));
		jToolBarTools.add(jButtonSave);
		
		jButtonWarnings = new JButton("");
		jButtonWarnings.setToolTipText("Warnings");
		jButtonWarnings.setEnabled(false);
		jButtonWarnings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showWarnings();
			}
		});
		
		jButtonSaveAsText = new JButton("");
		jButtonSaveAsText.setEnabled(false);
		jButtonSaveAsText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsText();
			}
		});
		jButtonSaveAsText.setToolTipText("Save as text");
		jButtonSaveAsText.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/txtFile.png")));
		jToolBarTools.add(jButtonSaveAsText);
		
		jButtonConvertToBinary = new JButton("");
		jButtonConvertToBinary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				convertToBinary();
			}
		});
		jButtonConvertToBinary.setToolTipText("Convert to binary");
		jButtonConvertToBinary.setEnabled(false);
		jButtonConvertToBinary.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/binFile.png")));
		jToolBarTools.add(jButtonConvertToBinary);
		jButtonWarnings.setIcon(new ImageIcon(FFaplAPIEditorJFrame.class.getResource("/editor/images/warning.png")));
		jToolBarTools.add(jButtonWarnings);
		
		this.setTransferHandler(new FileTransferHandler(this));
		this.setLocationRelativeTo(null);
	}
	
	/*
	 * Loads a specified file into the editor
	 * @param file the file to be loaded
	 */
	public void fileLoaded(File file) {
		if (FileMapper.getInstance().isFileSupported(file)) {
			IApiObject apiObject = null;
			String apiText = null;
			
			if (FileMapper.getInstance().isBinaryFile(file)) {
				apiObject = BinaryFileHandler.getInstance().deserializeBinaryFileToApiObject(file);
				
				if (apiObject != null) {
					String type = jComboBoxTextFormat.getSelectedItem().toString();
				    apiText = Converter.getInstance().objectToText(apiObject, type);
				}
			} else {
				apiText = TextFileHandler.getInstance().readApiTextFromFile(file);
				
				if (apiText != null) {
					String type = FileMapper.getInstance().getType(file);
					Class<?> clazz = FileMapper.getInstance().getClass(file);
					apiObject = Converter.getInstance().textToObject(apiText, type, clazz);
					jComboBoxTextFormat.setSelectedItem(type);
				}			
			}
			
			if (apiObject != null && apiText != null) {
				editorPane.setText(apiText);
				jButtonWarnings.setEnabled(!apiObject.isApiConsistent());
			    updateAPITree(apiObject);
			    Date dateNow = new Date(System.currentTimeMillis());
			    tfStatus.setText("File " + file.getPath() + " loaded at " + dateNow.toString());
			    loadedFile = file;
			    fileStatusChanged(false);
			}
		}
		else {
			JOptionPane.showMessageDialog(frame, "Supported files:\n\n" + FileMapper.getInstance().getSupportedFiles(), "File not supported!", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	/*
	 * Updates the API tree representation of the according API object
	 * @param apiObject the API object which should be shown in the tree
	 */
    private void updateAPITree(IApiObject apiObject) {
    	MutableTreeNode root, rootSpec, rootExample, node;
		DefaultTreeModel model;
		DefaultTreeCellRenderer renderer = new APITreeCellRenderer();
		
		jTreeAPI = new JTree();
		jScrollPaneTree.setViewportView(jTreeAPI);

		jTreeAPI.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		model = (DefaultTreeModel) jTreeAPI.getModel();
		jTreeAPI.setCellRenderer(renderer);
		root = new DefaultMutableTreeNode("FFapl API");
		
		if (apiObject instanceof ApiSpecification) {
			rootSpec = new DefaultMutableTreeNode("Specification");
			root.insert(rootSpec, root.getChildCount());
			
			node = new DefaultMutableTreeNode("Types");
			rootSpec.insert(node, rootSpec.getChildCount());
			ApiUtil.addToTreeNode(node, ((ApiSpecification)apiObject).getTypeList(), 0);
			
			node = new DefaultMutableTreeNode("Functions");
			rootSpec.insert(node, rootSpec.getChildCount());
			ApiUtil.addToTreeNode(node, ((ApiSpecification)apiObject).getFunctionList(), 0);
			
			node = new DefaultMutableTreeNode("Procedures");
			rootSpec.insert(node, rootSpec.getChildCount());
			ApiUtil.addToTreeNode(node, ((ApiSpecification)apiObject).getProcedureList(), 0);
			
		} else if (apiObject instanceof SampleCode) {
			rootExample = new DefaultMutableTreeNode("Samples");
			root.insert(rootExample, root.getChildCount());

			node = new DefaultMutableTreeNode("Functions");
			rootExample.insert(node, rootExample.getChildCount());
			ApiUtil.addToTreeNode(node, ((SampleCode)apiObject).getFunctionList(), 0);

			node = new DefaultMutableTreeNode("Procedures");
			rootExample.insert(node, rootExample.getChildCount());
			ApiUtil.addToTreeNode(node, ((SampleCode)apiObject).getProcedureList(), 0);
		}

		model.setRoot(root);
		expandTreeNode(jTreeAPI, (DefaultMutableTreeNode)root);
		jTreeAPI.setRootVisible(true);
	}
    
    /*
     * Recursively expands all tree nodes
     * @param tree the JTree which should be expanded
     * @param node the TreeNode which should be expanded
     */
    private void expandTreeNode(JTree tree, DefaultMutableTreeNode node) {
    	for (int i = 0; i < node.getChildCount(); i++) {
    		expandTreeNode(tree, (DefaultMutableTreeNode)node.getChildAt(i));
    	}
    	
        TreePath path = new TreePath(node.getPath());
        tree.expandPath(path);
    }
    
    /*
     * Shows the API specification warnings
     */
    private void showWarnings() {
    	IApiObject apiObject = null;
		
		if (FileMapper.getInstance().isBinaryFile(loadedFile)) {
			apiObject = BinaryFileHandler.getInstance().deserializeBinaryFileToApiObject(loadedFile);
		} else {
			String apiText = TextFileHandler.getInstance().readApiTextFromFile(loadedFile);
			String type = FileMapper.getInstance().getType(loadedFile);
			Class<?> clazz = FileMapper.getInstance().getClass(loadedFile);
			apiObject = Converter.getInstance().textToObject(apiText, type, clazz);
		}
		
		if (apiObject != null) {
			String result = apiObject.getInconsistencies();
			JOptionPane.showMessageDialog(frame, result, "Inconsistencies of FFapl API Specification", JOptionPane.WARNING_MESSAGE);
		}
    }
    
    /*
     * Saves the current opened and modified file
     */
    private void saveFile() {
		String apiText = editorPane.getText();
		String type = jComboBoxTextFormat.getSelectedItem().toString();
		Class<?> clazz = FileMapper.getInstance().getClass(loadedFile);
		IApiObject apiObject = Converter.getInstance().textToObject(apiText, type, clazz);

		if (apiObject != null) {
			boolean saveSuccessful;
			
			if (FileMapper.getInstance().isBinaryFile(loadedFile)) {
				saveSuccessful = BinaryFileHandler.getInstance().serializeApiObjectToBinaryFile(apiObject, loadedFile);
			} else {
				saveSuccessful = TextFileHandler.getInstance().saveApiTextToFile(apiText, loadedFile);
			}
			
			if (saveSuccessful) {
				jButtonWarnings.setEnabled(!apiObject.isApiConsistent());
				updateAPITree(apiObject);
				Date dateNow = new Date(System.currentTimeMillis());
			    tfStatus.setText("File " + loadedFile.getPath() + " saved at " + dateNow.toString());
				fileStatusChanged(false);
			}
		}
    }
    
    /*
     * Converts the opened binary file into a text file (i.e. saves the text representation of the binary file)
     */
    private void saveAsText() {
    	String ext = jComboBoxTextFormat.getSelectedItem().toString();
		
    	FileNameExtensionFilter filter = new FileNameExtensionFilter("." + ext, ext);		
		
		File selectedFile = handleSaveDialog(filter);

		if (selectedFile != null) {
			String apiText = editorPane.getText();
			boolean saveSuccessful = TextFileHandler.getInstance().saveApiTextToFile(apiText, selectedFile);
			
			if (saveSuccessful) {
				Date dateNow = new Date(System.currentTimeMillis());
			    tfStatus.setText("File " + selectedFile.getPath() + " saved at " + dateNow.toString());
			}
		}
    }
    
    /*
     * Converts the opened text file into a binary file
     */
    private void convertToBinary() {
    	FileNameExtensionFilter filter = new FileNameExtensionFilter("." + FileMapper.BIN_EXT, FileMapper.BIN_EXT);
    	
    	File selectedFile = handleSaveDialog(filter);
    	
		if (selectedFile != null) {
			String apiText = editorPane.getText();
			String type = FileMapper.getInstance().getType(loadedFile);
			Class<?> clazz = FileMapper.getInstance().getClass(loadedFile);

			IApiObject apiObject = Converter.getInstance().textToObject(apiText, type, clazz);
				
			if (apiObject != null) {
				boolean saveSuccessful = BinaryFileHandler.getInstance().serializeApiObjectToBinaryFile(apiObject, selectedFile);
				
				if (saveSuccessful) {
					Date dateNow = new Date(System.currentTimeMillis());
				    tfStatus.setText("File " + selectedFile.getPath() + " saved at " + dateNow.toString());
				}
			}
		}
    }
    
    /*
     * Shows an save dialog according to the give extension filter and returns the specified file
     * @param filter The extension filter to be used in the save dialog
     * @return the file specified by the user
     */
    private File handleSaveDialog(FileNameExtensionFilter filter) {
    	JFileChooser fileChooser = new JFileChooser();
		
    	fileChooser.setFileFilter(filter);
    	fileChooser.setSelectedFile(new File(loadedFile.getName().substring(0, loadedFile.getName().lastIndexOf("."))));
		
		int saveDialogResult = fileChooser.showSaveDialog(frame);
		
		if (saveDialogResult == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			String extension = filter.getExtensions()[0];
			
			if (!selectedFile.getName().endsWith(extension))
				selectedFile = new File(selectedFile.getPath() + "." + extension);
			
			if (selectedFile.exists()) {
				int result = JOptionPane.showConfirmDialog(frame, "File already exists! Do you want to overwrite the existing file?", "Warning", JOptionPane.YES_NO_OPTION);
				
				if (result == JOptionPane.NO_OPTION)
					return null;
			}
			
			return selectedFile;
		}
		
		return null;
    }
    
    /*
     * deserializes the loaded file and converts it to text afterwards
     * is called when the user changes the text format in the combobox
     * this can only be done if a binary file was loaded
     */
    private void textFormatSelectionChanged() {
    	if (loadedFile != null && FileMapper.getInstance().isBinaryFile(loadedFile)) {
    		IApiObject apiObject = BinaryFileHandler.getInstance().deserializeBinaryFileToApiObject(loadedFile);
    		String type = jComboBoxTextFormat.getSelectedItem().toString();
    		String text = Converter.getInstance().objectToText(apiObject, type);
    		editorPane.setText(text);
    	}
    }
    
    /*
     * enables or disables controls depending on the file status (modified or not)
     * @param bFileModified indicates if file has been modified previously
     */
    private void fileStatusChanged(boolean fileModified) {
    	boolean isBinaryFile = FileMapper.getInstance().isBinaryFile(loadedFile);
    	editorPane.setEnabled(true);
    	mntmSave.setEnabled(fileModified);
    	jButtonSave.setEnabled(fileModified);
	    jButtonConvertToBinary.setEnabled(!fileModified && !isBinaryFile);
	    jButtonSaveAsText.setEnabled(!fileModified && isBinaryFile);
    	jComboBoxTextFormat.setEnabled(!fileModified && isBinaryFile);
    	setTitle(APP_NAME + " - " + loadedFile.getName() + (fileModified?"*":""));
    }
}
