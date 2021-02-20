package ModelPackage;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class ClickStreamData
{
    private Parse parser;
    private HashMap<String, String> clicksMap;

    /**
     * Constructor of ClickStreamData
     */
    public ClickStreamData()
    {
        this.parser = new Parse(StopWords.getStopWords(), null);
        this.clicksMap = new HashMap<>();

        parseClicksFile();
    }

    /**
     * This function evaluates the click stream data rank for the document and term received
     * @param doc - the document to calculate the rank
     * @param term - the term to calculate the rank
     * @return the caulculated rank of doc and term
     */
    public double evaluateClickStream(Document doc, Term term)
    {
        double evaluateRes = 0;

        String docID = doc.getIdString();

        String queries = clicksMap.get(docID);
        int termCounter;

        if (queries != null)
        {
            String[] queriesSplit = queries.split(",");

            for (String query : queriesSplit)
            {
                termCounter = 0;
                String[] terms = query.split(" ");

                for (String t : terms)
                    if (term.getTerm().equals(t))
                        termCounter++;

                evaluateRes = evaluateRes + ((double)termCounter/(double)terms.length);
            }
        }

        return evaluateRes;
    }

    /**
     * This function parses the CLickStreamData.txt file and saves the data into the class's attribute
     */
    private void parseClicksFile()
    {

        try
        {
            File clicksFile = new File("./resource/ClickStreamData.txt");
            ArrayList<String> clicksFileLines = new ArrayList<>(Files.readAllLines(clicksFile.toPath()));

            for (String line : clicksFileLines)
            {
                String[] lineCells = line.split(",");
                String queryAfterParse = parser.parseAndGetText(lineCells[3]);
                if (!queryAfterParse.equals(""))
                    this.clicksMap = addToHashDocs(this.clicksMap, lineCells[2], queryAfterParse);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * This function adds the docID and query to the hash map received
     * @param docsMap - the hashmap
     * @param docID - the id of the document to be inserted
     * @param query - the query to be inserted into the docsMap
     * @return the hashmap received with the paramertes after insertion
     */
    private HashMap<String, String> addToHashDocs(HashMap<String, String> docsMap, String docID, String query)
    {

        String queryInDoc = docsMap.get(docID);

        if (queryInDoc == null)
            docsMap.put(docID, query);
        else
            docsMap.put(docID, queryInDoc + "," + query);

        return docsMap;
    }
}
