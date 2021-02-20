// Created by:
//              Liat Cohen 205595283
//              Adir Biran 308567239

package sample;

import ModelPackage.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Map;


public class Model {


    private ViewModel viewModel;
    private Indexer indexer;
    private Searcher searcher;


    public Model(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * starts the indexing process
     * @param pathToCorpus - user input path to corpus
     * @param pathToPosting - user input path to posting
     * @param stemming - user input use a stemmer or not
     */
    public void start(String pathToCorpus, String pathToPosting, boolean stemming)
    {

        long startTime = System.currentTimeMillis();

        ArrayList<File> allFiles = new ArrayList<>();
        allFiles = getAllFiles(fixPath(pathToCorpus), allFiles);

        this.indexer = new Indexer(fixPath(pathToPosting), allFiles.size(), false);

        initReadFiles(allFiles, stemming);

        //clears posting files - not suppose to here.
        int numOfDocuments = indexer.getNumOfDocuments();
        int numOfUniqTerms = indexer.getNumOfUniqTerms();
        long endTime = System.currentTimeMillis();

        long totalTimeInMillis = (endTime-startTime);
        
        viewModel.showIndexingData(numOfDocuments, numOfUniqTerms, totalTimeInMillis);

    }

    /**
     *
     * @param allFiles
     * @param stemming
     */
    private void initReadFiles(ArrayList<File> allFiles, boolean stemming)
    {
        int numOfThreads = 3;

        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);

        ReadFile rd;
        HashSet<String> stopWords = StopWords.getStopWords();

        int corpusSize = allFiles.size();
        File[] filesToRead;

        for (int i = 0; i < corpusSize; i++)
        {

            filesToRead = new File[5];


            for (int j = 0; j < 5 && i < corpusSize; j++, i++)
            {
                filesToRead[j] = allFiles.get(i);
            }
            Parse pr = new Parse(stopWords, indexer);

            i--;
            pr.setStemmer(stemming);

            rd = new ReadFile(pr, filesToRead);
            executor.execute(rd);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        indexer.removeNonEntities();
        indexer.setEntities();
        indexer.mergePostingFiles(numOfThreads);
        indexer.writeNumofTermsToDisk();

    }

    /**
     * gets all files from a directory path
     * @param path - path to files
     * @param files - files from directories
     * @return a list of files
     */
    private  ArrayList<File> getAllFiles(String path, ArrayList<File> files) {

        File directory = new File(path);
        File[] fList = directory.listFiles();

        if(fList != null)
            for (File file : fList) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    files = getAllFiles(file.getAbsolutePath(), files);
                }
            }

        return files;
    }



    /**
     * deletes all posting files
     */
    public void reset() {
        indexer.reset();
    }

    /**
     * replace \\ in / to fix the path given by user
     * @param path - corrupted path
     * @return - right path
     */
    private String fixPath(String path)
    {
        String fixedPath = "";
        char ch;

        for (int i = 0; i < path.length(); i++)
        {
            ch = path.charAt(i);
            if (ch == '\\')
                ch = '/';

            fixedPath = fixedPath + ch;
        }

        if (fixedPath.charAt(fixedPath.length()-1) != '/')
            fixedPath = fixedPath + '/';

        return fixedPath;
    }

    /**
     * loads the dictionary from disk to main memory
     * @return the dictionary
     */
    public Map<String, Term> loadDictionary(){
        return indexer.getDictionary();
    }

    /**
     * Setter for the path to posting
     * @param pathToPosting - the path to set of the posting directory
     */
    public void setPathToPosting(String pathToPosting) {
        this.indexer = new Indexer(fixPath(pathToPosting), 0, true);
    }

    /**
     * This functiopn runs the queries
     * @param pathToQueriesFile - the path to the queries file
     * @param pathToPostings - the path to the posting file
     * @param isSemantic - boolean for semantics
     * @param isClickStream - boolean for click stream data
     */
    public void runQueries(String pathToQueriesFile,String pathToPostings, boolean isSemantic, boolean isClickStream)
    {
        HashSet<String> stopWords = StopWords.getStopWords();
        searcher = new Searcher(fixPath(pathToPostings),new Parse(stopWords, null), isSemantic, isClickStream, this);
        searcher.searchQueries(pathToQueriesFile);

    }

    /**
     * This function runs the query search
     * @param query the query to search
     * @param pathToPostings - the path to the posting file
     * @param semantics - boolean for semantics
     * @param isClickStream - boolean for click stream data
     */
    public void runQuery(String query,String pathToPostings, boolean semantics, boolean isClickStream)
    {
        HashSet<String> stopWords = StopWords.getStopWords();
        searcher = new Searcher(fixPath(pathToPostings),new Parse(stopWords, null), semantics, isClickStream, this);
        searcher.searchQuery(query, false);
    }

    /**
     * This function shows the queries results
     * @param queries - the queries to show
     * @param documentsOfQuery - the relevant documents to the queries
     */
    public void ShowQueriesResults(HashMap<String, String> queries, ArrayList<HashMap<Document, Double>> documentsOfQuery) {
        viewModel.ShowQueriesResults(queries, documentsOfQuery);
    }

}
