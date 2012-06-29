package projectDev;

	import java.awt.BorderLayout;
	import java.awt.Color;
	import java.awt.FlowLayout;
	import java.awt.GridLayout;
	import java.awt.LayoutManager;
	import java.awt.event.WindowAdapter;
	import java.awt.event.WindowEvent;
	import java.io.BufferedInputStream;
	import java.io.BufferedReader;
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileNotFoundException;
	import java.io.FileReader;
	import java.io.IOException;
	import java.io.InputStream;
	import java.io.InputStreamReader;
	import java.io.StringReader;
	import java.util.*;

	import javax.swing.JFileChooser;
	import javax.swing.JFrame;
	import javax.swing.JLabel;
	import javax.swing.JPanel;
	import javax.swing.JScrollPane;
	import javax.swing.SpringLayout;


	import edu.stanford.nlp.ling.HasWord;
	import edu.stanford.nlp.ling.Word;
	import edu.stanford.nlp.ling.Sentence;
	import edu.stanford.nlp.process.DocumentPreprocessor;
	import edu.stanford.nlp.process.Tokenizer;
	import edu.stanford.nlp.trees.*;
	import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
	/**
	 * @author Andrew W.E. McDonald
	 *
	 */
public class Parser {
		
		
		int numSentences;
		private static SentenceTools st = new SentenceTools();
		private static String GRAMMAR_DIR = "./grammar_data/";
		static String[] authorNames;// = new String[]{"aa","cc","p","q","r","x","y","z"};
		private static HashMap<String,ArrayList<String>> otherSampleStrings;
		private static HashMap<String,ArrayList<String>> authorSampleStrings;
		private static HashMap<String,ArrayList<String>> toModifyStrings;
		
		
		
		public HashMap<String,ArrayList<TreeData>> parseAllDocs() throws IOException{ 
			String grammar =  "./jsan_resources/englishPCFG.ser.gz";
			String[] options = { "-maxLength", "120", "-retainTmpSubcategories" };
//			LexicalizedParser lp = new LexicalizedParser(grammar, options);
			
			LexicalizedParser lp = new LexicalizedParser()
			TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			Iterable<List<? extends HasWord>> sentences;
			ArrayList<HashMap<String,ArrayList<String>>> everything = new ArrayList<HashMap<String,ArrayList<String>>>(3); 
			everything.add(0,otherSampleStrings);
			everything.add(1,authorSampleStrings);
			everything.add(2,toModifyStrings);
			Iterator<HashMap<String,ArrayList<String>>> everythingIter = everything.iterator();
			int docTypeNumber = -1; // 0 for otherSampleStrings, 1 for authorSampleStrings, 2 for toModifyStrings
			int numLoaded = 0;
			while(everythingIter.hasNext()){
				docTypeNumber++;
				HashMap<String,ArrayList<String>> currentSampleStrings = docPathFinder();
				Set<String> currentDocStrings = currentSampleStrings.keySet();
				Iterator<String> docStrIter = currentDocStrings.iterator();
				String docID;
				ArrayList<String> sentenceTokens;
				allTreeProcessors[docTypeNumber]  = new TreeProcessor();
				allTreeProcessors[docTypeNumber].clearLoadedTreeDataMaps();
				numLoaded=0;
				while(docStrIter.hasNext()){
					docID = docStrIter.next();
					sentenceTokens = currentSampleStrings.get(docID);
					if(sentenceTokens == null){
						allTreeProcessors[docTypeNumber].loadTreeDataMap(docID, GRAMMAR_DIR, false);
						numLoaded++;
						continue;
					}
					//System.out.println(sentenceTokens.size()+", strIter.hasNext? -> "+strIter.hasNext());

					numSentences = sentenceTokens.size();
					//initialize(numSentences);
					Iterator<String> sentIter = sentenceTokens.iterator();
					List<List<? extends HasWord>> tmp = new ArrayList<List<? extends HasWord>>();
					String tempSent;
					while(sentIter.hasNext()){
						tempSent = sentIter.next();
						Tokenizer<? extends HasWord> toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(tempSent));
						List<? extends HasWord> sentenceTokenized = toke.tokenize();
						tmp.add(sentenceTokenized);
					}
					
					sentences = tmp;
					//int numDone = 0;
					TreeProcessor.singleDocMap.clear();
					boolean willSaveResults = true;
					for (List<? extends HasWord> sentence : sentences) {
						Tree parse = lp.apply(sentence);
						//parse.pennPrint();
						//System.out.println(parse.treeSkeletonCopy().toString());
						//System.out.println(parse.taggedYield());
						//System.out.println();
						//printSubTrees(parse);
						//TreeContainer.recurseTree(parse,"breadth");
						allTreeProcessors[docTypeNumber].processTree(parse, 0, willSaveResults); 
						//System.out.println(tc.processedTrees.toString().replaceAll("\\]\\], \\(","\\]\\]\n\\("));
						//numDone++;
						//System.out.println("sent "+numDone+" of "+numSentences+" done ");
						//System.out.println(tc.processedTrees.toString());
						//in.nextLine();
						//TreeContainer.recurseTree(parse, "depth");
						//in.nextLine();
						//addTree(parse);
						//GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);//TODO: LOOK AT THIS
						//Collection tdl = gs.typedDependenciesCCprocessed(true);
						//System.out.println(tdl);
						//System.out.println();
					}
					if(willSaveResults == true)
						ObjectIO.writeObject(TreeProcessor.singleDocMap,docID, GRAMMAR_DIR);

					//System.out.println("After all sents: ");
					//System.out.println(tc.processedTrees.toString().replaceAll("\\]\\], \\(","\\]\\]\n\\("));
					//String sent3 = "This is one last test!";
					//Tree parse3 = lp.apply(sent3);
					//parse3.pennPrint();
					//System.out.println("After sorting and writing:");
					//System.out.println(tc.processedTrees.toString().replaceAll("\\]\\], \\(","\\]\\]\n\\("));
					//Scanner in = new Scanner(System.in);
					//System.out.println("First one done.");
					//in.nextLine();
					//viewTrees();
				}
				
				//TreeProcessor.writeTreeDataToCSV(sortedTD,docID);
				allTreeProcessors[docTypeNumber].unmergedMaps = new ArrayList<HashMap<String,TreeData>>(numLoaded+1);
				
			}	
			
			
			int i= 0;
			allParsedAndOrdered.clear();
			String[] docTypes = new String[]{"otherSample","authorSample","toModify"};
			for(i=0; i < 3; i++){
				allTreeProcessors[i].unmergedMaps.add(allTreeProcessors[i].processedTrees);
				allTreeProcessors[i].unmergedMaps.addAll(allTreeProcessors[i].loadedTreeDataMaps);
				allTreeProcessors[i].mergeTreeDataLists(allTreeProcessors[i].unmergedMaps);
				allParsedAndOrdered.put(docTypes[i],allTreeProcessors[i].sortTreeData(allTreeProcessors[i].mergedMap));
				
			}
			
			//ArrayList<TreeData> sortedTD = TreeContainer.sortTreeData(TreeContainer.allProcessedTrees);
			//TreeContainer.writeTreeDataToCSV(sortedTD,"ALL_AUTHORS");
			
			return allParsedAndOrdered;
		}


}
