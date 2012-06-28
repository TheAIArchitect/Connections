package utils;

public class UnusedCode {
	
/*	
		public static HashMap<String,ArrayList<String>> docPathFinder() throws IOException{
			HashMap<String,ArrayList<String>> everything = new HashMap<String,ArrayList<String>>();
			JFileChooser open = new JFileChooser();
			open.setMultiSelectionEnabled(true);
			open.addChoosableFileFilter(new ExtFilter("Text files (*.txt)", "txt"));
			open.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int answer = open.showOpenDialog(null);
			int num = 0;
			String[] paths;
			if (answer == JFileChooser.APPROVE_OPTION) {
				File[] files = open.getSelectedFiles();
				authorNames = new String[files.length];
				int numFiles = files.length;
				for (File file: files) {
					if(file.isDirectory()){
						String[] theDocsInTheDir = file.list();
						int numDocs = theDocsInTheDir.length;
						authorNames[num] = file.getName();
						String pathFirstHalf = file.getAbsolutePath();
						paths = new String[numDocs];
						int innerNum =0;
						for (String otherFile: theDocsInTheDir){
							File newFile = new File(otherFile);	
							String path = pathFirstHalf+File.separator+otherFile;
							if(path.contains(".svn") || path.contains("imitation") || path.contains("verification") || path.contains("obfuscation") || path.contains("demographics"))
								continue;
							paths[innerNum] = path;
							innerNum++;
						}
						ArrayList<String> authorsSentenceTokens = getDocs(paths);
						everything.put(authorNames[num],authorsSentenceTokens);
						num++;
					}
				}
			}
			return everything;

		}
*/


/*
		public static ArrayList<String> getDocs(String[] paths) throws IOException{
			String fullDoc = "";
			for(String s:paths){
				if (s != null){
					FileReader fr = new FileReader(new File(s));
					BufferedReader buff = new BufferedReader(fr);
					String tempDoc = "";
					while((tempDoc = buff.readLine()) != null){
						fullDoc += tempDoc;
					}
				}
			}
			SentenceTools st = new SentenceTools();
			st.makeSentenceTokens(fullDoc);
			ArrayList<String> sentenceTokens = st.getSentenceTokens();
			return sentenceTokens;


		}
*/

}
