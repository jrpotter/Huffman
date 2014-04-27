package huffman.tools;

import java.io.*;
import java.util.HashMap;
import java.util.PriorityQueue;

import huffman.Constants;
import huffman.lib.TreeNode;
import huffman.io.BitInputStream;
import huffman.io.BitOutputStream;

public class Compressor implements Tool {
	
	private String destination;
	private BitInputStream input;
	private BitOutputStream output;
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
	public Compressor(String fileName) {
		encoding = new HashMap<>();
		destination = fileName + ".hf";
		input = new BitInputStream(fileName);
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
		for(Integer i : encoding.keySet()) {
			output += i +  " : " + encoding.get(i) + "\n";
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
	// COMPRESSION
	// 
	// ///////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public String compress() throws IOException {
		
		TreeNode root = buildHuffmanTree();
		if(root != null) {
			
			// In this case, only one character is present in the file.
			// Given this, traversing left and right to build keys would
			// not work as expected. Thus, we simply set the one character 
			// to value '0'
			if(root.myLeft == null && root.myRight == null) {
				encoding.put(root.myValue, "0");
			} else {
				encode(root, "");
			}
			
			// Write out header
			output.writeBits(Constants.BITS_PER_INT, Constants.MAGIC_NUMBER);
			
			// Tells how many entries are in the Huffman Tree
			output.writeBits(Constants.BITS_PER_INT, encoding.size());
			
			// Begins writing out the values of each word
			for(Integer i : encoding.keySet()) {
				String seq = encoding.get(i);
				int iseq = Integer.parseInt(seq, 2);
				
				// The value
				output.writeBits(Constants.BITS_PER_WORD, i);
				
				// The length of the sequence
				output.writeBits(Constants.BITS_PER_WORD, seq.length());
				
				// The sequence
				output.writeBits(Constants.BITS_PER_INT, iseq);
			}
			
			// Write out contents of file encoded
			int inbits;
		    while ((inbits = input.readBits(Constants.BITS_PER_WORD)) != -1) {
		    	String seq = encoding.get(inbits);
		    	output.writeBits(seq.length(), Integer.parseInt(seq, 2));
		    }
		}
		
		input.close();
		output.close();
		
		return destination;
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private TreeNode buildHuffmanTree() throws IOException {
		
		// Keep track of how often words appear in the document
		// Using this table we can create the needed nodes for the
		// Huffman Tree
		HashMap<Integer, Integer> counts = new HashMap<>();
		for(int i = 0; i != -1; i = input.readBits(Constants.BITS_PER_WORD)) {
			if(counts.containsKey(i)) {
				int value = counts.get(i);
				counts.put(i, value + 1);
			} else {
				counts.put(i, 1);
			}
		}
		
		input.reset();
		
		// Next we collect the counts to extract in minimum-first order
		PriorityQueue<TreeNode> queue = new PriorityQueue<>();
		for(Integer i : counts.keySet()) {
			queue.add(new TreeNode(i, counts.get(i)));
		}
		
		// Begin building the tree, bottom up
		while(queue.size() > 1) {
			TreeNode fst = queue.poll();
			TreeNode snd = queue.poll();
			int weight = fst.myWeight + snd.myWeight;
			queue.add(new TreeNode(-1, weight, fst, snd));
		}
		
		return queue.poll();
	}
	
	/**
	 * 
	 * @param node
	 * @param value
	 */
	private void encode(TreeNode node, String value) {
		
		// Reached a leaf, and can store value in table
		if(node.myLeft == null && node.myRight == null) {
			encoding.put(node.myValue, value);
			return;
		}
		
		encode(node.myLeft, value + "0");
		encode(node.myRight, value + "1");
	}

}
