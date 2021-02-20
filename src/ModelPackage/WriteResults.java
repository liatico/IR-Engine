package ModelPackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class WriteResults
{
    /**
     * This function writes the results of the queries search to the disk
     * @param queryID - the query id to write
     * @param docID - the doc id to write
     */
    public static void writeResultsToFile(String queryID, String docID)
    {
        try
        {
            File file = new File("./resource/results.txt");
            FileWriter fwriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fwriter);
            writer.write(queryID + " 0 " + docID + " 1 42.38 mt" + "\r\n");
            writer.close();
            fwriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
