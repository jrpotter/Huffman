package huffman.tools;

import java.util.HashMap;
import java.io.IOException;

import huffman.Constants;
import huffman.tools.Tool;
import huffman.lib.TreeNode;
import huffman.io.BitInputStream;
import huffman.io.BitOutputStream;

public class Uncompressor implements Tool {
	
	private String destination;
	private BitInputStream input;
	private BitOutputStream output;
	private HashMap<String, Integer> encoding;
	
	
	// ///////////////////////////////////////////////////////////////////
	//
	// CONSTRUCTOR
	// 
	// ///////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param fileName
	 */
	public Uncompressor(String fileName) throws IOException {
		
		int e_pos = fileName.lastIndexOf(".hf");
		if(e_pos == -1) {
			throw new IOException("File is not of type '.hf'");
		}
		
		encoding = new HashMap<>();
		input = new BitInputStream(fileName);
		destination = fileName.substring(0, e_pos);
		output = new BitOutputStream(destination);
	}
	
	
	// ///////////////////////////////////////////////////////////////////
	//
	// GETTERS/SETTERS
	// 
	// ///////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	@Override
	public String getOutput() {
		
		String output = "";
		for(String s : encoding.keySet()) {
			output += s + " : " + encoding.get(s) + "\n";
		}

		return output;
	}

	/**
	 * 
	 */
	@Override
	public String getMessage() {

		return "Written to file: " + destination;
	}
	
	
	// ///////////////////////////////////////////////////////////////////
	//
	// UNCOMPRESSION
	// 
	// ///////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @throws IOException
	 */
	public void uncompress() throws IOException {
		
		// Rebuild decoding table and Huffman tree
		decode();
		TreeNode root = new TreeNode(-1, -1);
		for(String s : encoding.keySet()) {
			buildHuffmanTree(root, encoding.get(s), s);
		}
		
		root.print();
		
		// There is only a single value present
		if(root.myValue != -1) {
			for(int i = 0; i != -1; i = input.readBits(1)) {
				System.out.println((char) root.myValue);
			}
		} 
		
		// Read in each bit, traversing the tree as one goes
		else {
			TreeNode node = root;
			for(int i = input.readBits(1); i != -1; i = input.readBits(1)) {
				if(i == 0) node = node.myLeft;
				else node = node.myRight;
				
				if(node.myValue > 0) {
					output.write(node.myValue);
				}
				
				if(node.myValue != -1) {
					node = root;
				}
			}
		}
		
		input.close();
		output.close();
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	private void decode() throws IOException {
		
		// Read in header
		int magic = input.readBits(Constants.BITS_PER_INT);
		if(magic != Constants.MAGIC_NUMBER) {
			throw new IOException("Could not find magic number");
		}
		
		// Build up encoding table
		int encoding_size = input.readBits(Constants.BITS_PER_INT);
		for(int i = 0; i < encoding_size; i++) {
			int word = input.readBits(Constants.BITS_PER_WORD);
			int seq = input.readBits(Constants.BITS_PER_WORD);
			int iseq = input.readBits(Constants.BITS_PER_INT);
			
			// Build sequence
			String bin_string = Integer.toBinaryString(iseq);
			while(bin_string.length() < seq) {
				bin_string = "0" + bin_string;
			}
			
			encoding.put(bin_string, word);
		}
	}

	/**
	 * 
	 * @param node
	 * @param value
	 * @param path
	 */
	private void buildHuffmanTree(TreeNode node, int value, String path) {
		
		if(path.isEmpty()) {
			node.myValue = value;
			return;
		}
		
		// Continue building down
		if(path.charAt(0) == '0') {
			if(node.myLeft == null) node.myLeft = new TreeNode(-1, -1);
			buildHuffmanTree(node.myLeft, value, path.substring(1));
		} else {
			if(node.myRight == null) node.myRight = new TreeNode(-1, -1);
			buildHuffmanTree(node.myRight, value, path.substring(1));
		}
	}
	

}
