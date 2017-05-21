/* ***************************************************************************
                         Seyedehzahra Hosseini 
                         Hamid Bagheri

*******************************************************************************/


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;

public class QueryProcessor {
	public static Set<DocSim> s = new HashSet<DocSim>();
	public static double[] score;
	public static HashMap<String, Integer> Docnum = new HashMap<String, Integer>();

	public static boolean DESC = false;

	// return tfq:
	public static int tfq(String t, String[] q) {
		int count = 0;
		for (int i = 0; i < q.length; i++)
			if (q[i].equals(t))
				count++;
		return count;
	}

	public static double weight(String t, String[] q) {
		return Math.log(1 + (double) tfq(t, q)) / Math.log((double) 2);
	}

	// calculate vector size v(
	public static double vecsize(String[] q) {
		double sum = 0;
		for (int i = 0; i < q.length; i++) {
			sum += Math.pow(weight(q[i], q), 2);
		}
		return (double) Math.sqrt(sum);
	}

	public static double vecsized(String[] q, int num, String d) {
		double sum = 0;
		for (int i = 0; i < q.length; i++) {
			sum += Math.pow(weight(q[i], q), 2);
			// TODO
		}

		return (double) Math.sqrt(sum);
	}

	public static Object getKeyFromValue(HashMap hm, int value) {
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				return o;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		boolean Flag = true;
		while (Flag) {

			System.out
					.println("Please Enter the folder(Please Copy the tested file on the source current directory): ");
			String folder = input.nextLine();

			System.out.println("Enter the query: ");
			String query = input.nextLine();

			System.out.println("Enter K: ");
			int k = input.nextInt();
			mainTest(k, query, folder);

			System.out.println("Do you want to continue? (Y/N)");
			String k1 = input.nextLine();
			k1 = input.nextLine();

			if (k1.equals("N"))
			{
				break;				
			}
			
			

		}
	}

	public static void mainTest(int _topk, String _query, String _folder) {
		
		// TODO Auto-generated method stub
		int topk = _topk;
		String query = _query;
		String folder = _folder;

		WordIndex bw = new WordIndex();
		// TODO change folder
		bw.WordIndex(folder);
		bw.buildIndex();
		int size = bw.Docs.length;
		// System.out.println(size);
		score = new double[size];

		// Docnum saves the index for each document
		for (int k1 = 0; k1 < bw.Docs.length; k1++) {
			Docnum.put(bw.Docs[k1], k1);
		}

		// set score to zero
		for (int i = 0; i < score.length; i++) {
			score[i] = 0;
		}

		String[] query1 = query.split("[.,;': ]");
		// TODO remove The and size<2

		int termnum = bw.Docsize();
		double[] vdsize = new double[termnum];
		// calc vd
		for (int h = 0; h < vdsize.length; h++)
			vdsize[h] = 0;
		for (HashMap.Entry<String, Set<Post>> entry : bw.termDoc.entrySet()) {
			Set<Post> it =entry.getValue();
			for(Post p:it){
				String docx=p.getDoc();
				int intx=p.getTfd();
				double wtd = bw.weight(entry.getKey(), docx);
				vdsize[Docnum.get(docx)] += Math.pow(wtd, 2);
			}
//		for (int h = 0; h < vdsize.length; h++) {
//				
//				double wtd = bw.weight(entry.getKey(), getKeyFromValue(Docnum, h).toString());
//				vdsize[h] += Math.pow(wtd, 2);
//			}

		}

		for (int j = 0; j < query1.length; j++) {
			Set<Post> posting = bw.Mypostingslist(query1[j]);
			if (posting != null)
				for (Post item : posting) {

					// }
					//
					// Iterator it = posting.iterator();
					// while (it.hasNext()) {
					// Post item = (Post) it.next();
					String itemDoc = item.getDoc();
					double wtd = bw.weight(query1[j], itemDoc);
					score[Docnum.get(itemDoc)] += wtd * weight(query1[j], query1);

				}

		}

		for (int k1 = 0; k1 < bw.Docs.length; k1++) {
			if (vdsize[k1] == 0)
				score[k1] = 0;
			else
				score[k1] = (score[k1]) / (Math.sqrt(vdsize[k1]) * vecsize(query1));
		}

		// vecsize=||v(q)||

		// unsorted score
		Map<Integer, Double> unsortedRank = new HashMap<Integer, Double>();
		for (int i = 0; i < score.length; i++)
			unsortedRank.put(i, score[i]);

		// Sort
		Map<Integer, Double> sortedRank = sortByComparator(unsortedRank, DESC);

		// S should be <docID,0> for initialization
		int y = 0;
		Map<Integer, Integer> S = new HashMap<Integer, Integer>();
		for (HashMap.Entry<Integer, Double> entry : sortedRank.entrySet()) {
			S.put(entry.getKey(), 0);
			y++;
			if (y > 2 * topk)
				break;
		}

		ArrayList<String> B = new ArrayList<String>();

		String[] words;
		words = query.split("[.,;': ]");
		ArrayList<String> fwords1 = new ArrayList<String>();

		int k2 = 0;
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > 2 && !words[i].toLowerCase().equals("the")) {
				fwords1.add(words[i]);
				// System.out.println(fwords1.get(k2));
				k2++;
			}
		}

		for (int n = 0; n < fwords1.size() - 1; n++) {
			B.add(fwords1.get(n) + " " + fwords1.get(n + 1));
		}

		BiWordIndex bi = new BiWordIndex();
		// TODO call by folder
		bi.BiWordIndex(folder);
		bi.buildIndex();
		Set<Post> termset;

		for (int z = 0; z < B.size(); z++) {

			// Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			for (HashMap.Entry<String, Set<Post>> entry : bi.termDoc.entrySet()) {

				if (entry.getKey().equals(B.get(z))) {
					
					termset = entry.getValue();
					for(Post p: termset){
						int Sid = 0;
//					Iterator it1 = termset.iterator();
//					while (it1.hasNext()) {
						//Post p = (Post) it1.next();
						Sid = S.get(Docnum.get(p.getDoc()));
						S.put(Docnum.get(p.getDoc()), Sid + p.getTfd());
					}
				}
			}
		}
		// sort S again

		// Sort

		Map<Integer, Integer> sortedS = sortByComparatorS(S, DESC);
		HashMap<String, Double> x = new HashMap<String, Double>();

		String[] DocSorted = new String[sortedS.size()];
		Integer[] ScoreSorted = new Integer[sortedS.size()];
		int index = 0;
		for (HashMap.Entry<Integer, Integer> entry : sortedS.entrySet()) {
			DocSorted[index] = (String) getKeyFromValue(Docnum, entry.getKey());
			ScoreSorted[index] = entry.getValue();
			index++;
		}

		while (x.size() < topk) {
			for (index = 0; index < DocSorted.length - 1; index++) {
				if (ScoreSorted[index] > ScoreSorted[index + 1]) {
					x.put(DocSorted[index], sortedRank.get(Docnum.get(DocSorted[index])));
					// System.out.println(DocSorted[index]);

				}
				if (ScoreSorted[index] == ScoreSorted[index + 1]) {

					int sValue = ScoreSorted[index];
					// return keys for specific value
					// HashSet<Integer> keySet = new HashSet<Integer>();
					Set<Integer> keySet = new HashSet<Integer>();

					for (HashMap.Entry<Integer, Integer> en : sortedS.entrySet()) {
						if (en.getValue()==sValue)
							keySet.add(en.getKey());
					}
					// Now remove docs one by one with max JACCSIM and add
					// to x
					// Iterate over setkeys and check the max JacSim

					// System.out.println(keySet.size());
					while(!keySet.isEmpty()&&x.size()<topk){
						
					int maxDoc = 0;
					Double maxSim = Double.MIN_VALUE;

					for (Integer docid : keySet) {
						if( maxDoc == 0){
							maxDoc = docid;
							maxSim = sortedRank.get(docid);
						}
						if (maxSim < sortedRank.get(docid)) {
							maxDoc = docid;
							maxSim = sortedRank.get(docid);
						}
						
					}

					// Now whe have DocID that has the max similarity
					// put docID and Sim to x
					x.put((String) getKeyFromValue(Docnum, maxDoc), maxSim);
					// System.out.println((String)getKeyFromValue(Docnum,
					// maxDoc));
					// remove this DocID from the set
					keySet.remove(maxDoc);
					}
				}

			}

		}
		for (HashMap.Entry<String, Double> entry : x.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}

	}

	public static double ScoreRankBased(int docid, HashMap<Integer, DocSco> sortedRank) {
		double d = 0;
		for (HashMap.Entry<Integer, DocSco> entry : sortedRank.entrySet()) {
			DocSco item = entry.getValue();

			if (item.getDocId() == docid)
				return item.getScore();
		}
		return d;
	}

	private static Map<Integer, Double> sortByComparator(Map<Integer, Double> map, final boolean order) {
		List<Entry<Integer, Double>> list = new LinkedList<Entry<Integer, Double>>(map.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<Integer, Double>>() {
			public int compare(Entry<Integer, Double> o1, Entry<Integer, Double> o2) {
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());
				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
		for (Entry<Integer, Double> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	private static Map<Integer, Integer> sortByComparatorS(Map<Integer, Integer> map, final boolean order) {
		List<Entry<Integer, Integer>> list = new LinkedList<Entry<Integer, Integer>>(map.entrySet());

		// Sorting the list based on values
		Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());
				}
			}
		});

		// Maintaining insertion order with the help of LinkedList
		Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
		for (Entry<Integer, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

}
