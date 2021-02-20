// Created by:
//              Liat Cohen 205595283
//              Adir Biran 308567239

package ModelPackage;
import java.util.Objects;

public class Term
{
    private String term;
    private int numOfDocuments;
    private int termFrequency;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Term)) return false;
        Term term1 = (Term) o;
        return getTerm().equals(term1.getTerm());
    }

    /**
     * Setter for the number of documents
     * @param numOfDocuments - the num of documents to set
     */
    public void setNumOfDocuments(int numOfDocuments) {
        this.numOfDocuments = numOfDocuments;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTerm());
    }


    public Term(String term) {
        this.term = term;
        this.numOfDocuments = 0;
        this.termFrequency = 0;
    }

    /**
     * Setter for term
     * @param term - the term to set
     */
    public void setTerm(String term) {
        this.term = term;
    }

    /**
     * Getter for the term
     * @return the term
     */
    public String getTerm() {
        return term;
    }

    /**
     * Getter for the number of documents
     * @return the number of documents
     */
    public int getNumOfDocuments() {
        return numOfDocuments;
    }

    /***
     * Adds one document to the counter of the term
     */
    public void addOneDocument(){
        numOfDocuments++;
    }

    /**
     * Adds frequency to the term
     * @param frequency - the number of frequencies to add to the term
     */
    public void addToTermFrequency(int frequency){
        this.termFrequency += frequency;
    }

    /**
     * Getter for the term frequency
     * @return the term frequency
     */
    public int getTermFrequency(){return termFrequency;}

    /**
     * Setter for the term frequency
     * @param termFrequency the frequency to set
     */
    public void setTermFrequency(int termFrequency) {
        this.termFrequency = termFrequency;
    }
}
