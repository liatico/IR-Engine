package ModelPackage;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public class Ranker
{
    private int sumWordsInCorpus;
    private double a;
    private double b;
    private boolean isClickStream;
    private ClickStreamData clickStream;

    /**
     * Constructor of Ranker
     * @param pathToPosting - the path to the posting files
     * @param isClickStream - boolean, true if active, false otherwise
     */
    public Ranker(String pathToPosting, boolean isClickStream)
    {
        this.a = 2.0;
        this.b = 0.4;
        this.isClickStream = isClickStream;
        this.clickStream = new ClickStreamData();

        ArrayList<String> detailsFileLines = new ArrayList<>();
        try
        {
            File detailsFile = new File(pathToPosting + "details.txt");
            detailsFileLines = new ArrayList<>(Files.readAllLines(detailsFile.toPath()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.sumWordsInCorpus = Integer.parseInt(detailsFileLines.get(0));

    }

    /**
     * This function ranks a document with a term
     * @param term - term to rank
     * @param doc - doc to rank
     * @param freq - frequency of the term in the doc
     * @return the calculated rank
     */
    public double RankWordWithDocument(Term term, Document doc, int freq)
    {
        double idf = calculateIDF(term);

        double titleRank = calculateTitleRank(doc, term);

        double upper = (double)freq * (a + 1);
        double length = 0.6 * (double)(doc.getNumOfUnique()) / (double)(this.sumWordsInCorpus);
        double lower = freq + (a - b + ( b * length));

        if (isClickStream)
            return idf * (upper / lower) + titleRank + clickStream.evaluateClickStream(doc, term);
        else
            return idf * (upper / lower) + titleRank;
    }

    /**
     * This function calculates the title rank
     * @param doc - doc to rank
     * @param term - term to rank
     * @return the calculated rank of the title
     */
    private double calculateTitleRank(Document doc, Term term)
    {

        if (doc.getTitle().contains(term.getTerm()))
            return 1;

        return 0;
    }

    /**
     * This function calculates the idf
     * @param word - the term to calculate
     * @return the calculated rank
     */
    public double calculateIDF(Term word)
    {
        double N = this.sumWordsInCorpus;
        double n = word.getNumOfDocuments();

        double upper = N - n + 0.5;
        double lower = n + 0.5;

        return Math.log10((upper/lower));

    }

    /**
     * This function ranks the documents received
     * @param documentsMap - datastructure represnting the documents and terms
     * @return hashmap of documents and rankings
     */
    public HashMap<Document, Double> RankDocuments(HashMap<Document, HashMap<Term,Integer>> documentsMap)
    {
        HashMap<Document, Double> documentsRanking = new HashMap<>();

        double totalRank;

        for (Document doc : documentsMap.keySet())
        {
            totalRank = 0;

            for (Term term : documentsMap.get(doc).keySet())
            {
                int freq = documentsMap.get(doc).get(term);
                totalRank = totalRank + RankWordWithDocument(term, doc, freq);
            }

            documentsRanking.put(doc, totalRank);

        }
        TreeMap<Document, Double> sortedMap = new TreeMap<>(new Comparator<Document>() {
            @Override
            public int compare(Document o1, Document o2) {
                return Double.compare(documentsRanking.get(o2), documentsRanking.get(o1));
            }
        });
        for (Document doc: documentsRanking.keySet()){
            sortedMap.put(doc, documentsRanking.get(doc));
        }
        HashMap<Document, Double> docsToReturn = new HashMap<>();
        int i=0;
        for(Document doc : sortedMap.keySet()){
            if(i==50) break;
            docsToReturn.put(doc, sortedMap.get(doc));
            i++;
        }

        return docsToReturn;

    }

}
