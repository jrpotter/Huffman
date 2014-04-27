package huffman;

import huffman.View;
import huffman.Model;

public class Main {
	
	public static void main(String[] args) {
		View view = new View("Huffman Encoding", new Model());
		view.show(true);
	}

}
