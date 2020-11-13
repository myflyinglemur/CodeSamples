import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Project4 {
		
	public static Map<String, List<String>> MATCHES = new HashMap <String, List<String>>();
	
	public static void main(String[] args_array) throws IOException {
		Iterator<String> args = Arrays.asList(args_array).iterator();
		List<String> targets = readTargets(new File(args.next()));

		//File [] chromFiles = new File("../chroms" ).listFiles();
		// List<File> chrom = Arrays.asList(new File("../chroms" ).listFiles());

		PrintStream ps = new PrintStream(new File("output"));
		
		//Iterator<File> chromIT = Arrays.asList(new File("../chroms").listFiles()).iterator();

		Trie targetGenes = new Trie();

 		for (String target: targets) {
			targetGenes.add(target.toUpperCase());
			MATCHES.put(target, new ArrayList<String>());
		}

		while (args.hasNext()) {
			File thisFile = new File(args.next());
			ps.println(thisFile);
			process(targetGenes, thisFile, ps);
		}
		
		ps.close();
		
		printMatches();
	}

	public static void process(Trie targets, File file, PrintStream ps) throws IOException {
		byte[] chromBases = readFile(file);
		targets.resetSearch();
		
		//records where current search begins
		for (int i0 = 0; i0 < chromBases.length; i0++) {
			if (chromBases[i0] != 'N') {	// skips non-nucleotides					

				// advances current search
				for (int i1 = i0; i1 < chromBases.length; i1++) {
					byte currBase = chromBases[i1];

					if (targets.hasNext(currBase)) {
						targets.advanceSearch(currBase);
						
						if (targets.matchFound()) {
							String thisGene = targets.getCurrNode().getPayload();
							String hex = String.format("%08x", i1).toUpperCase();
							
							ps.println("\t" + hex + "\t" + thisGene);
							recordMatch(thisGene, hex, file.getAbsolutePath());
							
							i0 = i1 + 1;	//no nested genes - restart outer count
						} 
					} else {

						targets.resetSearch();
						break;
					}
				}				
			}
		}
	}
	
	
	public static void recordMatch(String gene, String hex,  String filePath){
		String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
		
		MATCHES.get(gene).add("\t" + hex + "\t" + fileName);
	}
	
	public static void printMatches() throws FileNotFoundException{
		PrintStream ps = new PrintStream(new File("extra-credit"));
		
		for(String gene: MATCHES.keySet()){
			ps.println(gene);
			
			for(String file: MATCHES.get(gene)){
				ps.println(file);
			}
			
		}
		
		ps.close();
	}

	public static byte[] readFile(File file) throws IOException {
		final int file_size = (int) file.length();

		byte[] file_buf = new byte[file_size];

		FileInputStream input = new FileInputStream(file);
		
		int input_len = input.read(file_buf);

		if (input_len != file_size) {
			System.err
					.println("Didn't read all the bytes of the file: " + file_size + " size vs " + input_len + " read");
		}

		input.close();

		return file_buf;
	}

	public static List<String> readTargets(File file) throws IOException {
		List<String> targets = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new FileReader(file));

		String line;
		while ((line = reader.readLine()) != null) {
			targets.add(line);
		}

		reader.close();

		return targets;
	}
}
