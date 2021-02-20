// Created by:
//              Liat Cohen 205595283
//              Adir Biran 308567239

package ModelPackage;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Document implements Comparable
{
    private int id;
    private String idString;
    private int maxTf;
    private int numOfUnique;
    private String date;
    private String title;
    private HashMap<String, Integer> optionalEntities;
    private String[][] top5Entities;

    /**
     * Constructor - all Documents are created in ReadFile.
     * @param date - date of the document
     * @param title - document title
     * @param idString - (the retrieved text) document id
     */
    public Document(String date, String title, String idString) {
        this.date = date;
        this.title = title;
        this.idString = idString;
        this.top5Entities = new String [5][2];
        fillOutTop5();
        this.optionalEntities = new HashMap<>();

    }

    public Document(String date, String title, String idString, int maxTf, int numOfUnique) {
        this.date = date;
        this.title = title;
        this.idString = idString;
        this.maxTf = maxTf;
        this.numOfUnique = numOfUnique;
        this.top5Entities = new String [5][2];
        fillOutTop5();
        this.optionalEntities = new HashMap<>();

    }

    // Setter for document id
    public void setID(int id)
    {
        this.id = id;
    }

    /**
     * This function filss the top 5 identities of the document
     */
    private void fillOutTop5() {
        top5Entities[0][0] = "";
        top5Entities[0][1] = "0";
        top5Entities[1][0] = "";
        top5Entities[1][1] = "0";
        top5Entities[2][0] = "";
        top5Entities[2][1] = "0";
        top5Entities[3][0] = "";
        top5Entities[3][1] = "0";
        top5Entities[4][0] = "";
        top5Entities[4][1] = "0";
    }
    public String getStringEntities(){
        return top5Entities[0][0] + "|" + top5Entities[1][0] +"|" + top5Entities[2][0] +"|" + top5Entities[3][0] +"|" + top5Entities[4][0];
    }
    /**
     * this == o if they have the same docID
     * @param o
     * @return true if this == o, and false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
            Document document = (Document) o;
        return getId() == document.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    // Setter for maxTF
    public void setMaxTf(int maxTf) {
        this.maxTf = maxTf;
    }

    // Setter for unique words
    public void setNumOfUnique(int numOfUnique) {
        this.numOfUnique = numOfUnique;
    }

    // Getter for document's id
    public int getId() {
        return id;
    }

    // Getter for maxTF
    public int getMaxTf() {
        return maxTf;
    }

    // Getter for number of unique words in the document
    public int getNumOfUnique() {
        return numOfUnique;
    }

    // Getter for date as a string
    public String getDate() {
        return date;
    }

    // Getter for title (for later use)
    public String getTitle() {
        return title;
    }
    public void addEntity(String entity, int frequency){

        int tf0 = Integer.parseInt(top5Entities[0][1]),
                tf1= Integer.parseInt(top5Entities[1][1]),
                tf2= Integer.parseInt(top5Entities[2][1]),
                tf3= Integer.parseInt(top5Entities[3][1]),
                tf4= Integer.parseInt(top5Entities[4][1]);
        if(frequency>tf4){
            if(frequency>tf3){
                if(frequency>tf2){
                    if(frequency>tf1){
                        if(frequency>tf0){
                          insert0(entity, frequency);
                        }
                        else{
                            insert1(entity, frequency);
                        }
                    }
                    else{
                        insert2(entity, frequency);
                    }
                }
                else{
                    insert3(entity, frequency);
                }
            }
            else{
                top5Entities[4][0] = entity;
                top5Entities[4][1] = ""+frequency;
            }
        }

    }

    private void insert3(String entity, int frequency) {
        move3to4();
        top5Entities[3][0] = entity;
        top5Entities[3][1] = ""+frequency;
    }

    private void insert2(String entity, int frequency) {
        move3to4();
        move2to3();
        top5Entities[2][0] = entity;
        top5Entities[2][1] = ""+frequency;
    }

    private void insert1(String entity, int frequency) {
        move3to4();
        move2to3();
        move1to2();
        top5Entities[1][0] = entity;
        top5Entities[1][1] = ""+frequency;
    }

    private void insert0(String entity, int frequency)
    {
        move3to4();
        move2to3();
        move1to2();
        move0to1();
        top5Entities[0][0] = entity;
        top5Entities[0][1] = ""+frequency;
    }
    private void move3to4(){
        top5Entities[4][0] = top5Entities[3][0];
        top5Entities[4][1] = top5Entities[3][1];
    }
    private void move2to3(){
        top5Entities[3][0] = top5Entities[2][0];
        top5Entities[3][1] = top5Entities[2][1];
    }
    private void move1to2(){
        top5Entities[2][0] = top5Entities[1][0];
        top5Entities[2][1] = top5Entities[1][1];
    }

    private void move0to1(){
        top5Entities[1][0] = top5Entities[0][0];
        top5Entities[1][1] = top5Entities[0][1];
    }

    // Getter for top 5 entities
    public String[][] getTop5Entities(){return top5Entities;}

    // Getter for entities
    public HashMap<String, Integer> getOptionalEntities() {
        return optionalEntities;
    }

    // Getter for doc's id (doc's name)
    public String getIdString()
    {
        return this.idString;
    }


    @Override
    public int compareTo(Object o) {
        if(o instanceof Document){
            return Integer.compare(id, ((Document)o).getId());
        }
        return 0;
    }
}
