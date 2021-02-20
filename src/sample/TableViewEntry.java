package sample;

public class TableViewEntry {

    private String docID;
    private double docScore;

    /**
     * Constructor for table view entry
     * @param docID of the document
     * @param docScore of the ranking
     */
    public TableViewEntry(String docID, double docScore) {
        this.docID = docID;
        this.docScore = docScore;
    }

    /**
     * Getter for doc id
     * @return the docid
     */
    public String getDocID() {
        return docID;
    }

    /**
     * Getter for doc's score
     * @return the document's score
     */
    public double getDocScore() {
        return docScore;
    }
}
