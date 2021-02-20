// Created by:
//              Liat Cohen 205595283
//              Adir Biran 308567239

package ModelPackage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class ReadFile implements Runnable
{


    private Parse pr;
    private File[] filesToRead;

    /**
     * Constructor - gets created in model and gets the parser and indexer
     * @param pr - parser to use to parse the documents
     * @param filesToRead - list of files to read
     */
    public ReadFile(Parse pr, File[] filesToRead)
    {
        this.pr = pr;
        this.filesToRead = filesToRead;

    }

    public static ArrayList<String>readQueriesFile(String path)
    {
        ArrayList<String> fileLines, queries = new ArrayList<>();
        File file = new File(path);
        if(file!=null){
            try {
                fileLines = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.ISO_8859_1));
                int queryID = 0;
                for (String line : fileLines)
                {
                    if(line.contains("<num>")){
                        queryID = Integer.parseInt(line.substring(line.indexOf(":")+2).trim());
                    }
                    if(line.contains("<title>")){
                        line = line.replace("<title>","");
                        if(queryID > 0 ){
                            line = queryID+ ":" + line;
                        }
                        queries.add(line);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return queries;
    }

    /**
     * run threads
     */
    @Override
    public void run()
    {
        readFiles(this.filesToRead);
    }

    /**
     * reads the files and creates a set of documents and a set to document's text
     * sends those sets to parser to parse
     * @param files
     */
    public void readFiles(File[] files)
    {
        ArrayList<Document> documents = new ArrayList<>();
        ArrayList<String> texts = new ArrayList<>();

        ArrayList<String> fileLines;
        String lines;

        for (File file: files)
        {

            if (file != null)
            {
                try {


                    fileLines = new ArrayList<>(Files.readAllLines(file.toPath(), StandardCharsets.ISO_8859_1));
                    lines = getStringFromFileLines(fileLines);

                    int openDOC = lines.indexOf("<DOC>") + 5;
                    int closeDOC = lines.indexOf("</DOC>");
                    Document dc;
                    String[] docDetails;

                    while (closeDOC != -1)
                    {

                        docDetails = getDocumentDetails(lines.substring(openDOC, closeDOC));
                        lines = lines.substring(closeDOC + 6);
                        openDOC = lines.indexOf("<DOC>") + 5;

                        dc = new Document(docDetails[1], docDetails[2], docDetails[0]);
                        documents.add(dc);
                        texts.add(docDetails[3]);

                        closeDOC = lines.indexOf("</DOC>");

                    }

                }
                catch (Exception e)
                {
                    System.out.println(file.toPath());

                    e.printStackTrace();
                }
            }

        }

        pr.parseDocuments(documents, texts);

    }

    /**
     * returns the doc's attributes:
     * docStringID, date, title, text
     * @param st - string to parse to get attributes
     * @return - an array of docs attributes
     */

    private String[] getDocumentDetails(String st)
    {
        String[] res = new String[4];

        int docNoStartInd = st.indexOf("<DOCNO>") + 7;
        int docNoEndInd = st.indexOf("</DOCNO>");


        int dateStartInd = st.indexOf("<DATE");
        int dateEndInd;

        if (st.charAt(dateStartInd+5) == '>')
        {
            dateStartInd = dateStartInd + 6;
            dateEndInd = st.indexOf("</DATE>");
        }
        else
        {
            dateStartInd = dateStartInd + 7;
            dateEndInd = st.indexOf("</DATE1>");
        }

        int titleStartInd = st.indexOf("<TI>") + 4;
        int titleEndInd = st.indexOf("</TI>");

        if (titleEndInd == -1)
        {
            titleStartInd = st.indexOf("<HEADLINE>") + 10;
            titleEndInd = st.indexOf("</HEADLINE>");
        }

        if (titleEndInd == -1)
            res[2] = "";
        else
            res[2] = st.substring(titleStartInd, titleEndInd).trim();

        int textStartInd = st.indexOf("<TEXT>") + 6;
        int textEndInd = st.indexOf("</TEXT>");

        if (textEndInd == -1)
            res[3] = "";
        else
            res[3] = st.substring(textStartInd, textEndInd).trim();

        res[0] = st.substring(docNoStartInd, docNoEndInd).trim();
        res[1] = st.substring(dateStartInd, dateEndInd).trim();

        return res;
    }

    /**
     * convert list of strings into one string
     * @param fileLines - list of strings
     * @return one string from the list
     */
    private String getStringFromFileLines(ArrayList<String> fileLines)
    {
        StringBuilder st = new StringBuilder();

        for (String line : fileLines)
            st.append(line + "\r\n");

        return st.toString();
    }


}
