package huffman;

import huffman.tools.Tool;
import huffman.tools.Compressor;
import huffman.tools.Uncompressor;

import java.io.IOException;
import java.util.Observable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Model extends Observable implements ActionListener {
	
	private String fileName = "";
	
	
	// ///////////////////////////////////////////////////////////////////
	//
	// EVENTS
	// 
	// ///////////////////////////////////////////////////////////////////
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// If the file name is empty, there is nothing to act upon
		if(!fileName.isEmpty()) {
			
			try {
				
				Tool tool = null;
				switch(e.getActionCommand()) {
				
					case View.COMPRESS: {
						Compressor compressor = new Compressor(fileName);
						compressor.compress();
						tool = compressor;
						break;
					}
					
					case View.UNCOMPRESS: {
						Uncompressor uncompressor = new Uncompressor(fileName);
						uncompressor.uncompress();
						tool = uncompressor;
						break;
					}
					
				}
				
				// Change Display
				setChanged();
				notifyObservers(tool);
			} 
			
			// Display a dialog box with the corresponding error
			catch(IOException ex) {
				setChanged();
				notifyObservers(ex.getMessage());
			}
		}
	}
	
	
	// ///////////////////////////////////////////////////////////////////
	//
	// GETTERS/SETTERS
	// 
	// ///////////////////////////////////////////////////////////////////
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
