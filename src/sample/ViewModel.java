// Created by:
//              Liat Cohen 205595283
//              Adir Biran 308567239

package sample;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ModelPackage.Document;
import ModelPackage.Term;

public class ViewModel {

    private Controller controller;
    private Model model;

    public ViewModel(Controller controller, Model model) {
        this.controller = controller;
        this.model = model;
    }

    /**
     * Setter for later use
     * @param controller - the controller to set
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Setter for the model
     * @param model - the model to set
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Starts the model
     * @param pathToCorpus - the path to the corpus directory
     * @param pathToPosting - the path to the posting directory
     * @param stemming - stemming enabled / disabled
     */
    public void start(String pathToCorpus, String pathToPosting, boolean stemming) {
        model.start(pathToCorpus, pathToPosting,stemming );
    }

    /**
     * Deletes the posting files
     */
    public void reset() {
        model.reset();
    }

    /**
     * Getter for the dictionary
     * @return the dictionary
     */
    public Map<String,Term> getDictionary(){
        return model.loadDictionary();
    }

    /**
     * Loads the dictionary from the posting files
     * @return the dictionary
     */
    public Map<String,Term> loadDictionary() {
        return getDictionary();
    }

    /**
     * This function shows the details of the indexing proccess
     * @param numOfDocuments - documents indexed
     * @param numOfUniqTerms - number of unique terms indexed
     * @param totalTimeInMillis - time passed in milliseconds
     */
    public void showIndexingData(int numOfDocuments, int numOfUniqTerms, long totalTimeInMillis) {
        controller.showIndexingData(numOfDocuments, numOfUniqTerms, totalTimeInMillis);
    }

    /**
     * Setter for the path to the posting directory
     * @param pathToPosting the path to set of the posting directory
     */
    public void setPathToPosting(String pathToPosting) {
        model.setPathToPosting(pathToPosting);
    }

    /**
     * This functiopn runs the queries
     * @param pathToQueriesFile - the path to the queries file
     * @param pathToPostings - the path to the posting file
     * @param isSemantic - boolean for semantics
     * @param isClickStream - boolean for click stream data
     */
    public void runQueries(String pathToQueriesFile, String pathToPostings, boolean isSemantic, boolean isClickStream)
    {
        model.runQueries(pathToQueriesFile, pathToPostings, isSemantic, isClickStream);
    }

    /**
     * This function runs the query search
     * @param query the query to search
     * @param pathToPostings - the path to the posting file
     * @param semantics - boolean for semantics
     * @param isClickStream - boolean for click stream data
     */
    public void runQuery(String query, String pathToPostings,  boolean semantics, boolean isClickStream)
    {
        model.runQuery(query, pathToPostings, semantics,isClickStream);
    }

    /**
     * This function shows the queries results
     * @param queries - the queries to show
     * @param documentsOfQuery - the relevant documents to the queries
     */
    public void ShowQueriesResults(HashMap<String, String> queries, ArrayList<HashMap<Document, Double>> documentsOfQuery) {
        controller.showQueriesList(queries, documentsOfQuery);
    }
}
