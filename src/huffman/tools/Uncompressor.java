package huffman.tools;

import java.util.HashMap;
import java.io.IOException;

import huffman.Constants;
import huffman.tools.Tool;
import huffman.lib.TreeNode;
import huffman.io.BitInputStream;

public class Uncompressor implements Tool {
	
	private BitInputStream input;
	private HashMap<Integer, String> encoding;
	
	
	// ///////////////////////////////////////////////////////////////////
	//
	// CONSTRUCTOR
	// 
	// ///////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param fileName
	 */
	public Uncompressor(String fileName) {
		encoding = new HashMap<>();
		input = new BitInputStream(fileName);
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
		for(Integer i : encoding.keySet()) {
			output += i + " : " + encoding.get(i) + "\n";
		}

		return output;
	}

	/**
	 * 
	 */
	@Override
	public String getMessage() {

		return null;
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
		for(Integer i : encoding.keySet()) {
			buildHuffmanTree(root, i, encoding.get(i));
		}
		
		
		
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
			
			encoding.put(word, bin_string);
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
			node.myRight = new TreeNode(-1, -1);
			buildHuffmanTree(node.myRight, value, path.substring(1));
		}
	}
	

}
