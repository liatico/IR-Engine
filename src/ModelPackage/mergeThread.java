package ModelPackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class mergeThread implements Runnable {

    private File[] filesToMerge;
    private File outputFile;
    private Indexer indexer;

    /**
     * Constructor of mergeThread
     * @param filesToMerge - the files to merge
     * @param outputFile - output file
     * @param indexer - an indexer object
     */
    public mergeThread(File[] filesToMerge, File outputFile, Indexer indexer)
    {
        this.filesToMerge = filesToMerge;
        this.outputFile = outputFile;
        this.indexer = indexer;
    }

    @Override
    public void run()
    {
        mergeFiles(filesToMerge, outputFile);
    }

    /**
     * This function merges the files in the array into the output file
     * @param files - the files to merge
     * @param outputFile - the output file
     */
    private void mergeFiles(File[] files, File outputFile)
    {

        ArrayList<String> mergeLines = new ArrayList<>();

        try
        {
            for (File f : files){
                mergeLines.addAll(Files.readAllLines(f.toPath()));
            }


            Collections.sort(mergeLines);
            HashMap<String, String> termsMap = uniteDuplicates(mergeLines);

            FileWriter fwriter = new FileWriter(outputFile, true);
            BufferedWriter writer = new BufferedWriter(fwriter);


            for (String term : termsMap.keySet())
            {
                writer.write(term + ";" + termsMap.get(term) + "\r\n");
            }

            indexer.increaseNumOfUniqueTerms(termsMap.size());


            writer.flush();
            writer.close();
            fwriter.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        for (File f : files)
            f.delete();

    }


    /**
     * unites duplicate terms
     * @param lines the strings to unite
     * @return the united arraylist of strings
     */
    private HashMap<String, String> uniteDuplicates(ArrayList<String> lines)
    {

        HashMap<String, String> termsMap = new HashMap<>();

        String term;
        String occurrences;
        String prevOcc;

        for (String line : lines)
        {
            term = getTerm(line);
            occurrences = getOccurrences(line);

            prevOcc = termsMap.get(term);
            if (prevOcc == null)
                termsMap.put(term, occurrences);
            else
                termsMap.put(term, prevOcc + occurrences);

        }

        return termsMap;
    }

    /**
     * This function gets the occurrences from the line received
     * @param line - the whole line in the posting file
     * @return the occurrences in the posting file
     */
    private String getOccurrences(String line)
    {
        if (line.length() < 1)
            return "";

        return line.substring(line.indexOf(';')+1);
    }

    /**
     * gets the term from postings line.
     * @param line
     * @return string of a term
     */
    private String getTerm(String line)
    {
        if (line.equals(""))
            return "";

        return line.substring(0, line.indexOf(';'));
    }


}
