package huffman;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Observer;
import java.util.Observable;

import huffman.tools.Tool;

/**
 * 
 * @author jrpotter
 *
 */
public class View implements ActionListener, Observer {
	
	// GUI elements
	private JFrame frame;
	private JTextArea output;
	private JTextField message;
	private JTextField fileNameDisplay;
	
	// MVC
	private Model model;
	
	// Events
	public static final String OPEN_COUNT = "open_count";
	public static final String COMPRESS = "compress";
	public static final String UNCOMPRESS = "uncompress";
	public static final String FORCE = "force";
	public static final String QUIT = "quit";
	public static final String SELECT = "select";
	
	
	// ///////////////////////////////////////////////////////////////////
	//
	// INITIALIZATION
	// 
	// ///////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param title
	 * @param proc
	 */
	public View(String title, Model model) {
	
		// Initialize Window
		this.frame = new JFrame(title);
		this.model = model;
		this.model.addObserver(this);
		
		// Initialize Content
		initMenuBar();
		initViewport();
		
		// Finish
		frame.pack();
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * 
	 */
	private void initMenuBar() {

		JMenuBar menuBar = new JMenuBar();
		
		// File
		JMenuItem open_count = new JMenuItem("Open/Count");
		open_count.setActionCommand(OPEN_COUNT);
		open_count.addActionListener(model);
		
		JMenuItem compress = new JMenuItem("Compress");
		compress.setActionCommand(COMPRESS);
		compress.addActionListener(model);
		
		JMenuItem uncompress = new JMenuItem("Uncompress");
		uncompress.setActionCommand(UNCOMPRESS);
		uncompress.addActionListener(model);
		
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(this);
		quit.setActionCommand(QUIT);
		
		JMenu file = new JMenu("File");
		file.add(open_count);
		file.add(compress);
		file.add(uncompress);
		file.add(quit);
		
		// Options		
		JMenuItem force = new JMenuItem("Force Compression");
		force.setActionCommand(FORCE);
		force.addActionListener(model);
		
		JMenu options = new JMenu("Options");
		options.add(force);
		
		// Finish
		menuBar.add(file);
		menuBar.add(options);
		frame.setJMenuBar(menuBar);
	}
	
	/**
	 * 
	 */
	private void initViewport() {
		
		frame.setContentPane(new JPanel());
		GridBagConstraints c = new GridBagConstraints();
		frame.getContentPane().setLayout(new GridBagLayout());
		
		// File Name Display
		c.gridx = 0; c.gridy = 0;
		c.weightx = 9; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		
		fileNameDisplay = new JTextField("No file selected");
		fileNameDisplay.setEditable(false);
		
		frame.getContentPane().add(fileNameDisplay, c);
		
		// File Name Choice
		c.gridx = 1; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		
		JButton fileChoose = new JButton("Open");
		fileChoose.addActionListener(this);
		fileChoose.setActionCommand(SELECT);
		
		frame.getContentPane().add(fileChoose, c);
		
		// Output
		c.gridwidth = 2;
		c.gridx = 0; c.gridy = 1;
		c.weightx = 10; c.weighty = 19;
		c.fill = GridBagConstraints.BOTH;
		
		output = new JTextArea();
		JScrollPane scrollPane = new JScrollPane();
		
		output.setEditable(false);
		scrollPane.getViewport().add(output);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));
		
		frame.getContentPane().add(scrollPane, c);
		
		// Messages
		c.gridwidth = 2;
		c.gridx = 0; c.gridy = 2;
		c.weightx = 10; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		
		message = new JTextField();
		message.setEditable(false);
		message.setBorder(BorderFactory.createTitledBorder("Message"));
		
		frame.getContentPane().add(message, c);
	}
	
	
	// ///////////////////////////////////////////////////////////////////
	//
	// EVENTS
	// 
	// ///////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param visible
	 */
	public void show(boolean visible) {
		frame.setVisible(visible);
	}

	/**
	 * 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		switch(e.getActionCommand()) {
			case QUIT: {
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
				break;
			}
			case SELECT: {
				JFileChooser chooser = new JFileChooser();
			    int returnVal = chooser.showOpenDialog(frame);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	String fileName = chooser.getSelectedFile().getAbsolutePath();
			    	fileNameDisplay.setText(fileName);
			    	model.setFileName(fileName);
			    }
			    
			    break;
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void update(Observable o, Object arg) {
		if(arg instanceof String) {
			String e = (String) arg;
			JOptionPane.showMessageDialog(frame, e, "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Tool tool = (Tool) arg;
		output.setText(tool.getOutput());
		message.setText(tool.getMessage());
	}

}
