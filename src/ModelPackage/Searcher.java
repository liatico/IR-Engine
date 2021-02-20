package ModelPackage;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import sample.Model;

public class Searcher {

    private Map<String,Term> dictionary;
    private Parse parser;
    private String pathToPostingFiles;
    private int totalNumberOfDocuments;
    private boolean isSemantic;
    private boolean isClickStream;
    private Model model;


    /**
     * Constructor of Searcher
     * @param pathToPosting - path to posting file
     * @param parser - parser object
     * @param isSemantic - true if semantics is active, false otherwise
     * @param isClickStream - true if click-stream data is active, false otherwise
     * @param model - the model
     */
    public Searcher(String pathToPosting, Parse parser, boolean isSemantic, boolean isClickStream, Model model){
        this.parser = parser;
        pathToPostingFiles = pathToPosting;
        buildDictionary(pathToPosting);
        totalNumberOfDocuments = 0;
        this.isSemantic = isSemantic;
        this.isClickStream = isClickStream;
        this. model = model;
    }

    /**
     * This function builds the dictionary
     * @param pathToPosting - the path to the posting file
     */
    private void buildDictionary(String pathToPosting)
    {
        Indexer indexer = new Indexer(pathToPosting, 0, true);
        dictionary = indexer.getDictionary();

    }
    //could return 0
    public int getTotalNumberOfDocuments() {

        return totalNumberOfDocuments;
    }


    /**
     * This function searches the queries in the queries file
     * @param pathToQueriesFile - the path to the queries file
     */
    public void searchQueries(String pathToQueriesFile)
    {

        ArrayList<String> queries = ReadFile.readQueriesFile(pathToQueriesFile);
        HashMap<String, String> queriesAndTheirDocuments = new HashMap<>();
        ArrayList<HashMap<Document, Double>> results = new ArrayList<>();
        for(String query : queries){
            HashMap<Document, Double> result = searchQuery(query, true);
            results.add(result);
            queriesAndTheirDocuments.put(query, result.toString());
        }
        //create a list of queries from file
        //parse each query
        //find documents for each query
        model.ShowQueriesResults(queriesAndTheirDocuments, results);

    }

    /**
     * This function searches a single query
     * @param query - the query to search
     * @param isFromFile - boolean representing if the query if from file or not
     * @return hashmap of documents and rankings
     */
    public HashMap<Document, Double> searchQuery(String query, boolean isFromFile)
    {
        int queryID = 0;
        if(isFromFile){
            queryID = Integer.parseInt(query.substring(0, query.indexOf(":")));
            query = query.substring(query.indexOf(":")+1);
        }
        HashMap<String, Integer> map = parser.parseQuery(query);
        if(queryID>0){
            query = queryID + ":" + query;
        }
        if(isSemantic){
            loopThroughWords(map);

        }
        //HashMap<term:String, postingEntry:String>
        HashMap<String, String> postingEntries = findPostingEntries(map);
        //HashMap<docID:String,HashMap<term:Term,TFInDoc:int>>
        HashMap<String, HashMap<Term,Integer>> optionalDocuments = findRelevantDocuments(postingEntries);
        //HashMap<doc:Document,HashMap<term:Term,TFInDoc:int>>>
        HashMap<Document, HashMap<Term,Integer>> dataStructure = CreateDocumentDataStructure(optionalDocuments);

        Ranker ranker = new Ranker(pathToPostingFiles, isClickStream);
        HashMap<Document, Double> docsRankingMap = ranker.RankDocuments(dataStructure);
		
		for (Document doc : docsRankingMap.keySet())
            WriteResults.writeResultsToFile("" + queryID, doc.getIdString());

		if(!isFromFile){
            HashMap<String, String> queries = new HashMap<>();
            queries.put(query, docsRankingMap.toString());
            ArrayList<HashMap<Document, Double>> result = new ArrayList<>();
            result.add(docsRankingMap);
            model.ShowQueriesResults(queries, result);
        }
        return docsRankingMap;
    }

    /**
     * This function loops through words and gets the semantics words
     * @param map of the semantic words
     */
    private void loopThroughWords(HashMap<String, Integer> map) {
        WordToVec model = new WordToVec();
        HashMap<String, Integer> tempMap = new HashMap<>();
        for(String term : map.keySet()){
            int freq = map.get(term);
            ArrayList<String> semanticTerms = model.getSemanticTerms(term);
            for(String semanticTerm : semanticTerms){
                tempMap.put(semanticTerm, freq);
            }
        }

        for (String term : tempMap.keySet())
            map.put(term, tempMap.get(term));

    }

    /**
     * This function creates the documents data structure from the posting file
     * @param optionalDocuments - the potential documents received
     * @return hashmap of documents and terms inside with frequencies
     */
    private HashMap<Document, HashMap<Term,Integer>> CreateDocumentDataStructure(HashMap<String, HashMap<Term,Integer>> optionalDocuments){
        HashMap<Document, HashMap<Term,Integer>> dataStructure = new HashMap<>();
        File file = new File(pathToPostingFiles + "docs.txt");
        ArrayList<String> fileLines ;
        try {
            fileLines = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.ISO_8859_1));
            if(totalNumberOfDocuments == 0){
                totalNumberOfDocuments = fileLines.size();
            }
            for (String line : fileLines)
            {
                //doc posting (line) = docID:docIDString;maxTF;uniqueWords;date;title;entity1;entity2;entity3;entity4,entity5
                //doc constructor = int id, String date, String title, String idString, int maxTf, int numOfUnique
                String docId = line.substring(0,line.indexOf(":"));
                line = line.substring(line.indexOf(":")+1);
                if(optionalDocuments.containsKey(docId)){
                    String[] data = line.split("\\|");
                    Document document = new Document(data[3],data[4],data[0],Integer.parseInt(data[1]),Integer.parseInt(data[2]));
                    document.setID(Integer.parseInt(docId));
                    for (int i = 5; i <data.length ; i++) {
                        if(data[i].length()>0)
                            document.addEntity(data[i], 10-i);
                    }

                    dataStructure.put(document, optionalDocuments.get(docId));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataStructure;
    }

    /**
     * This function finds the relevant documents in the posting file
     * @param postingEntries - the entries to search
     * @return hashmap of the potential documents retrieved from the file
     */
    private HashMap<String, HashMap<Term,Integer>> findRelevantDocuments(HashMap<String, String> postingEntries)
    {
        HashMap<String, HashMap<Term,Integer>> optionalDocuments = new HashMap<>();
        for(String term : postingEntries.keySet()){
            Term termObj = dictionary.get(term);
            if(termObj!=null){
                String[]docs = postingEntries.get(term).split(",");
                termObj.setNumOfDocuments(docs.length);
                for(String doc : docs){
                    String docId = doc.substring(0, doc.indexOf(":"));
                    String tf = doc.substring(doc.indexOf(":")+1);
                    if(optionalDocuments.containsKey(docId)){
                        optionalDocuments.get(docId).put(termObj, Integer.parseInt(tf));
                    }
                    else{
                        HashMap<Term,Integer> docsMap = new HashMap<>();
                        docsMap.put(termObj, Integer.parseInt(tf));
                        optionalDocuments.put(docId, docsMap);
                    }
                }
            }
        }
        return optionalDocuments;
    }

    /**
     * This function finds the posting entries in the documents' posting file
     * @param map - the hashmap received of the entries in the posting file
     * @return the map after text manipulations
     */
    private HashMap<String, String> findPostingEntries(HashMap<String, Integer> map)
    {
        HashMap<String, String> postingEntries = new HashMap<>();
        String postingPath, term;
        File posting;
        ArrayList<String>fileLines;

        for(String keyWord : map.keySet()) {
            postingPath = pathToPostingFiles + keyWord.toLowerCase().charAt(0) + ".txt";
            posting = new File(postingPath);
            try {
                fileLines = new ArrayList<>(Files.readAllLines(posting.toPath(), StandardCharsets.ISO_8859_1));
                for (String line : fileLines) {
                    term = line.substring(0, line.indexOf(";"));
                    line = line.substring(line.indexOf(";")+1);
                    if(term.equals(keyWord)|| term.equals(keyWord.toLowerCase()) || term.equals(keyWord.toUpperCase())){
                        postingEntries.put(term,line);
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return postingEntries;
    }
}
