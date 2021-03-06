/* ***************************************************************************
                         Seyedehzahra Hosseini 
                         Hamid Bagheri

*******************************************************************************/


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.print.Doc;

public class WordIndex {

	public static HashMap<String, Integer> termNumber = new HashMap<String, Integer>(); // ctrl+shif+O
	public static HashMap<String, Set<Post>> termDoc = new HashMap<String, Set<Post>>(); // ctrl+shif+O

	public static String pathDoc = "pa42";
	public static String[] Docs;
	public static int Docsize(){
		return Docs.length;
	}
	public static String[] allDocs() {
		String files;
		File folder = new File(pathDoc);
		File[] listOfFiles = folder.listFiles();

		// System.out.println(listOfFiles + " -" + pathDoc );

		Docs = new String[listOfFiles.length];

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i].getName();
				// Copy name of the file to the Docs array
				Docs[i] = files;
				InsertDocTerms(files);
			}
		}

		return Docs;
	}

	private static void InsertDocTerms(String FileName) {
		String line;
		String[] words;

		try {
			BufferedReader br = new BufferedReader(new FileReader(pathDoc + "\\" + FileName));

			while ((line = br.readLine()) != null) {
				words = line.split("[.,;': ]");
				for (int i = 0; i < words.length; i++) {
					if (words[i].length() > 2 && !words[i].toLowerCase().equals("the")) {
						// addWord2Hash(words[i]);
						// TO DO
						// System.out.println(words[i]);
						add2HashMap(words[i], FileName);
					}
				}
			}
			br.close();

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

	private static void add2HashMap(String term, String doc) {
		// TODO Auto-generated method stub

		if (termNumber.containsKey(term)) {
			
			int curNum = termNumber.get(term);
			
			// update current set
			Set<Post> termset = termDoc.get(term);
			Boolean exitsDoc = false;
			Iterator iterator = termset.iterator();
			while (iterator.hasNext()) {
				Post item = (Post) iterator.next();
				String itemDoc = item.getDoc();
				if (itemDoc.equals(doc)) {
					int itemNum = item.getTfd();
					termset.remove(item);
					termset.add(new Post(itemDoc, itemNum + 1));
					exitsDoc = true;
					break;
				}

			}
			if (!exitsDoc)

			{
				termset.add(new Post(doc, 1));
				termNumber.put(term, curNum + 1);
			}
			termDoc.put(term, termset);			
		} else {
			termNumber.put(term, 1);
			Set<Post> termset = new HashSet<Post>();// (new Post(doc,1));

			termset.add(new Post(doc, 1));
			termDoc.put(term, termset);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		allDocs();

		System.out.println(postingsList("zahra"));
		System.out.println(weight("zahra", "baseball0.txt"));
	}

	public void WordIndex(String folder) {
		pathDoc = folder;
	}

	public void buildIndex() {
		// call addAll doc
		allDocs();

		// add words to HashMap docx
	}
	public static Set<Post> Mypostingslist(String t){
		return termDoc.get(t);
	}
	public static ArrayList<Post> postingsList(String t) {
		Set<Post> termset= termDoc.get(t) ;
		int num= termNumber.get(t);
		ArrayList<Post> postArray = new ArrayList<Post>();
		for(Post item:termset ){
//		Iterator iterator = termset.iterator();
//		while(iterator.hasNext()){
//			Post item = (Post)iterator.next();
			String itemDoc = item.getDoc();
			int itemNum= item.getTfd();
			postArray.add(item);
		}
		return postArray;
	}

	public void printPostingList(String t) {
		// TODO 
	}

	public static double weight(String t, String d) {

		Set<Post> termset= termDoc.get(t) ;
		int num= termNumber.get(t);
		double returnVal=0;
		Iterator iterator = termset.iterator();
		while(iterator.hasNext()){
			Post item = (Post)iterator.next();
			String itemDoc = item.getDoc();
			int itemNum= item.getTfd();
			if (itemDoc.equals(d)){
				returnVal=((double)Math.log10(1+itemNum)/Math.log10((double)2))* Math.log10((double)Docs.length/num);
			}
		}
		return returnVal;

	}

}
