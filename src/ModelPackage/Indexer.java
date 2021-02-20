// Created by:
//              Liat Cohen 205595283
//              Adir Biran 308567239

package ModelPackage;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Indexer {



    private Map<String, Integer> entities;// entity ->num of documents
    private Map<String,Term> termEntities;//entity -> term
    private String pathToPosting;
    private ArrayList<Document> docsList;
    private int docsListSize;
    private int counter;
    private Lock lock;
    private int idCounter;
    // 0-9 in 0-9: 10
    // a-z in 10-35: 26

    private File[] postingDirectories;
    private int numOfTerms;

    // Indexer is the unique object, so holds the documents' id in the system

    /**
     * Constructor - indexer is created only in Model - according to user input pathToPosting
     * @param pathToPosting - a path to the the directory in which the postings will be saved
     */
    public Indexer(String pathToPosting, int corpusSize, boolean creationForDictionary)
    {
        this.pathToPosting = pathToPosting;
        this.lock = new ReentrantLock();
        if(!creationForDictionary){
            this.postingDirectories = initPostings(corpusSize);
            initDictionariesToWrite();
            this.entities = new TreeMap<>();
            this.counter = 1;
            this.docsList = new ArrayList<>();
            this.termEntities = new HashMap<>();
            this.numOfTerms = 0;
            this.docsListSize = 0;
            this.idCounter = 1;
        }



    }

    /**
     * This function initiates the directories
     * @return ArrayList of size 36 of HashSet for each directory
     */
    private ArrayList<HashSet<String>> initDictionariesToWrite() {

        ArrayList<HashSet<String>> dictionariesToWrite = new ArrayList<>();

        for (int i = 0; i < 36; i++)
            dictionariesToWrite.add(new HashSet<String>());

        return dictionariesToWrite;
    }

    /**
     * This function adds a term to the correct HashSet of the posting dictionary
     * @param dictionariesToWrite - the dictionaries adding the term to
     * @param term added to the dictionary
     */
    private void addToDictionariesToWrite(ArrayList<HashSet<String>> dictionariesToWrite, String term)
    {
        int dictionaryNumber;

        char ch = term.toLowerCase().charAt(0);

        if (ch >= '0' && ch <= '9')
        {
            dictionaryNumber = ch - '0';
            dictionariesToWrite.get(dictionaryNumber).add(term);
        }
        else if (ch >= 'a' && ch <= 'z')
        {
            dictionaryNumber = ch - 87;
            dictionariesToWrite.get(dictionaryNumber).add(term);
        }

    }

    /**
     * This function writes each HashSet of the array list to the specified posting file
     * @param dictionariesToWrite - dictionaries to write to disk
     * @param filesIndex - the index of the speified posting file
     */
    private void writeDictionariesToDisk(ArrayList<HashSet<String>> dictionariesToWrite, int filesIndex)
    {
        HashSet<String> map;
        File file;

        for (int i = 0; i < 36; i++)
        {
            map = dictionariesToWrite.get(i);
            String firstTermString = getFirstTermStringFromHashSet(map);

            if (firstTermString.length() > 0)
            {

                try
                {
                    lock.lock();
                    file = openPostingFile(firstTermString, filesIndex);
                    writeHashSetToFile(map, file);
                    lock.unlock();
                }
                catch (Exception e)
                {
                    lock.unlock();
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * This function writes the strings in the HashSet to the file
     * @param map - contains the strings for writing to the disk
     * @param file - destination of the writing
     */
    private void writeHashSetToFile(HashSet<String> map, File file)
    {
        try
        {
            FileWriter fWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fWriter);

            for (String s : map)
                writer.write(s + "\r\n");

            writer.flush();
            writer.close();
            fWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * This function returns the first string (if exists) in the map
     * Used for indicating the first letter representing the HashSet
     * @param map - holds the strings
     * @return the first string from the map
     */
    private String getFirstTermStringFromHashSet(HashSet<String> map)
    {
        String res = "";

        for (String s : map)
            return s;

        return res;
    }

    /**
     * initiates all postings files
     */
    private File[] initPostings(int corpusSize)
    {
        File[] postingDirectories = new File[36];
        int index = 0;

        int postingIndex;
        int postingMax = (corpusSize / 5) + 1;

        if (corpusSize%5 != 0)
            postingMax = postingMax + 1;

        File postingFile;

        try
        {
            lock.lock();
            File file = new File(pathToPosting);
            file.mkdirs();
            File docsFile = new File(pathToPosting + "docs.txt");
            docsFile.createNewFile();
            lock.unlock();
        }
        catch (Exception e)
        {
            lock.unlock();
            e.printStackTrace();
        }

        File directory;
        for (int i = 0; i < 10; i ++)
        {
            directory = new File(pathToPosting + i + "/");
            postingDirectories[index] = directory;
            index++;
            directory.mkdir();
        }

        for (char ch = 'a'; ch <= 'z'; ch++)
        {
            directory = new File(pathToPosting + ch + "/");
            postingDirectories[index] = directory;
            index++;
            directory.mkdir();
        }

        try
        {

            for (int i = 0; i < 10; i ++)
            {
                for (postingIndex = 1; postingIndex < postingMax; postingIndex++)
                {
                    postingFile = new File(pathToPosting + i + "/" + i + postingIndex + ".txt");
                    postingFile.createNewFile();
                }
            }

            for (char ch = 'a'; ch <= 'z'; ch++)
            {
                for (postingIndex = 1; postingIndex < postingMax; postingIndex++)
                {
                    postingFile = new File(pathToPosting + ch + "/" + ch + postingIndex + ".txt");
                    postingFile.createNewFile();
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return postingDirectories;


    }


    /**
     * index a set of documents including writing to postings each term
     * @param documents - set of Documents to index
     * @param maps - parsed Dictionaries of those documents <String, Integer> Maps
     */
    public void indexFile(ArrayList<Document> documents, ArrayList<Map> maps)
    {
        lock.lock();
        int fileCounter = this.counter;
        this.counter++;
        lock.unlock();

        ArrayList<HashSet<String>> dictionariesToWrite = initDictionariesToWrite();

        int numOfDocuments = documents.size();

        Document doc;
        Map<String, Integer> docMap;
        TreeMap<String, Term> dict = new TreeMap<>();

        for (int i = 0; i < numOfDocuments; i++)
        {
            doc = documents.get(i);
            docMap = maps.get(i);

            lock.lock();
            doc.setID(this.idCounter);
            idCounter = idCounter + 1;
            docsList.add(doc);
            lock.unlock();

            boolean newEntity;

            for (Object entryObject : docMap.entrySet()) // for each term
            {

                Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>)entryObject;
                String termString = entry.getKey();
                int termFrequency = entry.getValue();


                newEntity = false;
                Term term = null;
                String termLowerCase = termString.toLowerCase();

                if (dict.containsKey(termLowerCase)) // exists lowercase in dictionary
                {
                    //if this term is in the local dictionary - get it and add its term freq, add to postings as new
                    //else, add it to the dictionary and update postings.
                    termString = termLowerCase;
                    lock.lock();
                    if (entities.containsKey(termString.toUpperCase())) {//was upper case and now its not
                        term = termEntities.get(termString.toUpperCase());
                        term.setTerm(termString);
                        entities.remove(termString.toUpperCase());
                        termEntities.remove(termString.toUpperCase());
                        lock.unlock();
                        if(dict.containsKey(termString)){
                            int frq = term.getTermFrequency();
                            term = dict.get(termString);
                            term.addToTermFrequency(frq);
                        }
                        else
                            dict.put(termString, term);

                    }
                    else{
                        lock.unlock();
                        if(dict.containsKey(termString))
                            term = dict.get(termString);
                        //else get to null and create it
                    }
                }
                else if(termString.charAt(0)>='A' && termString.charAt(0)<='Z'){
                    lock.lock();
                    if(entities.containsKey(termString)){
                        term = termEntities.get(termString);
                        //dict.put(termString, term);
                        int value = (entities.get(termString));
                        value++;
                        entities.replace(termString, value);
                        doc.addEntity(termString, termFrequency);
                        lock.unlock();
                    }
                    else{//there is a chance that this doc should be on this entity postings
                        newEntity = true;
                        term = new Term(termString);
                        termEntities.put(termString, term);
                        entities.put(termString, 1);
                        doc.getOptionalEntities().put(termString, termFrequency);
                        lock.unlock();
                    }

                }

                if (term == null) { // term doesn't exist in dictionary
                    term = new Term(termString);
                    dict.put(termString, term);

                }

                term.addToTermFrequency(termFrequency);
                term.addOneDocument();

                try
                {
                    if(!newEntity){
                        String strToFile = termString + ";" + doc.getId() + ":" + termFrequency + ",";
                        addToDictionariesToWrite(dictionariesToWrite, strToFile);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }

        buildDocumentsPosting(documents);

        writeDictionariesToDisk(dictionariesToWrite, fileCounter);
    }

    /**
     * finds top 5 Entities of a document, this function is invoked only at the end after
     * we indexed all corpus and we know which word is an entity and which word isn't
     * @param allDocEntities - a map of all the entities that appeared in a specific document
     * @return a list sized 5 with the top 5 entities
     */
    private ArrayList<String> findTop5Entities(HashMap<String, Integer> allDocEntities) {
        ArrayList<String> top5entitiesList = new ArrayList<>();
        String word;
        for (int i = 0; i < 5; i++) {
            word = findMaxTerm(allDocEntities);

            if(word.length()>0){
                allDocEntities.remove(word);
                top5entitiesList.add(word);
            }
        }
        return top5entitiesList;

    }



    /**
     * finds the term that has the max frequency in a given dictionary
     * @param localDictionary - dictionary to find the max tf in
     * @return String - most frequent term
     */
    private String findMaxTerm(HashMap<String, Integer> localDictionary){
        int max = findMaxTF(localDictionary);
        for (String word:localDictionary.keySet()) {
            if(localDictionary.get(word)== max)
                return word;
        }
        return "";
    }

    /**
     * finds the max frequent term in a given dictionary
     * @param localDictionary - dictionary to find the max tf in
     * @return - int-max term frequency
     */
    private int findMaxTF(HashMap<String, Integer> localDictionary) {
        int max=0, val;
        for (String key: localDictionary.keySet()) {
            val = localDictionary.get(key);
            if(val > max)
                max = val;
        }
        return max;
    }

    /**
     * deletes all postings files
     */
    public void reset()
    {
        File postingPathDirectory = new File(pathToPosting);
        for (File f : postingPathDirectory.listFiles())
            f.delete();
    }

    /**
     * removes all non entities from the entities list and then adds to postings
     * all entities that didn't got in when document was indexed
     */
    public void removeNonEntities(){
        ArrayList<String> toDelete = new ArrayList<>();
        for(String entity : entities.keySet()){
            if(entities.get(entity)==1)
                toDelete.add(entity);
        }
        for(String entity: toDelete){
            entities.remove(entity);
        }
        setAllDocumentsEntities();
    }

    /**
     * writes entity's posting
     */
    private void setAllDocumentsEntities(){

        ArrayList<HashSet<String>> dictionariesToWrite = initDictionariesToWrite();
        int len = docsList.size();
        int j = 0;

        for(int i = 0; i < len; i++, j++){
            Document doc = docsList.get(i);
            removeNonEntities(doc, dictionariesToWrite);//deletes non entities and write to postings
            if (j == 3000 || i==len-1)
            {
                writeDictionariesToDisk(dictionariesToWrite, 0);
                j = 0;
                dictionariesToWrite = initDictionariesToWrite();
            }

        }
        writeDictionariesToDisk(dictionariesToWrite, 0);
        entities.clear();
        termEntities.clear();

    }

    /**
     * This function removes the non entities from the document
     * @param doc - the doc containing the entities
     * @param dictionariesToWrite - the datastructure responsible for writing to the disk
     */
    private void removeNonEntities(Document doc, ArrayList<HashSet<String>> dictionariesToWrite) {
        HashMap<String, Integer> entitiesMap = doc.getOptionalEntities();

        String toWrite;
        for (String entity: entitiesMap.keySet()) {
            if(entities.containsKey(entity)){
                doc.addEntity(entity, entitiesMap.get(entity));
                toWrite = "" + entity +";"+doc.getId()+":"+entitiesMap.get(entity)+",";
                addToDictionariesToWrite(dictionariesToWrite, toWrite);
            }
        }

    }

    /**
     * Opens a posting file represented by the term and counter
     * @param termString - term for indicating the first letter
     * @param fileCounter - counter for indicating the correct posting file
     * @return the correct posting file
     */
    private File openPostingFile(String termString, int fileCounter)
    {

        char ch = termString.toLowerCase().charAt(0);

        File file = new File(pathToPosting + ch + "/" + ch + fileCounter + ".txt");

        return file;

    }


    /**
     * merges all partial postings into united postings
     */
    public void mergePostingFiles(int numOfThreads)
    {
        File[] postingFilesToMerge;
        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);

        for (File folder : postingDirectories)
        {
            postingFilesToMerge = folder.listFiles();
            File postingFileToWrite = new File(folder.getPath() + ".txt");

            mergeThread mergeThr = new mergeThread(postingFilesToMerge, postingFileToWrite, this);
            executor.execute(mergeThr);
        }

        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }

        deleteDirectories();
    }


    /**
     * deletes all directories
     */
    private void deleteDirectories()
    {

        File[] postingFiles;

        for (File folder : postingDirectories)
        {
            postingFiles = folder.listFiles();

            for (File file : postingFiles)
                file.delete();
        }

        for (File folder : postingDirectories)
            folder.delete();

    }

    /**
     * Getter for the number of documents from the occurrences' string
     * @param line - of the occurrences of the term in the posting file
     * @return the number of documents where the term appeared
     */
    private int getNumOfDocumentsFromOccurrences(String line)
    {
        if (line.length() < 1)
            return 0;

        line = line.substring(0, line.length()-1);

        String[] pairs = line.split(",");

        return pairs.length;
    }

    /**
     * Getter for the term's frequency from the occurrences' string
     * @param line - of the occurrences of the term
     * @return the term frequency
     */
    private int getTermFrequencyFromOccurrences(String line)
    {
        int occurrences = 0;

        if (line.length() < 1)
            return 0;

        line = line.substring(0, line.length()-1);

        String[] pairs = line.split(",");
        int num;

        for (String pair : pairs)
        {
            num = Integer.parseInt(pair.substring(pair.indexOf(':')+1));
            occurrences = occurrences + num;
        }

        return occurrences;
    }

    /**
     * Getter for the occurrences string from the term line in the posting file
     * @param line of the term in the posting file
     * @return the occurrences of the term
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

    /**
     * creates a dictionary from posting files
     * @return Map<String, Term> off all terms in the corpus
     */
    private TreeMap<String, Term> loadDictionary()
    {
        TreeMap<String, Term> dict = new TreeMap<>();

        File postingDirectory = new File(pathToPosting);

        File[] postingFiles = postingDirectory.listFiles();

        List<String> fileLines;
        String termString;
        String occurrencesString;
        int termFrequency;
        int numOfDocuments;
        Term term;

        try
        {
            for (File file : postingFiles)
            {

                if (file.isFile() && !file.getName().equals("docs.txt") && !file.getName().equals("details.txt"))
                {
                    fileLines = Files.readAllLines(file.toPath());

                    for (String line : fileLines)
                    {
                        if (line.indexOf(';') != -1)
                        {
                            termString = getTerm(line);
                            occurrencesString = getOccurrences(line);
                            termFrequency = getTermFrequencyFromOccurrences(occurrencesString);
                            numOfDocuments = getNumOfDocumentsFromOccurrences(occurrencesString);
                            term = new Term(termString);
                            term.setNumOfDocuments(numOfDocuments);
                            term.setTermFrequency(termFrequency);
                            dict.put(termString, term);
                        }

                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return dict;
    }

    /**
     * public method to get the dictionary from postings
     * @return Map<String, Term> off all terms in the corpus
     */
    public Map<String, Term> getDictionary()
    {
        return loadDictionary();
    }

    /**
     * Getter for the number of documents in the system
     * @return  the number of documents in the system
     */
    public int getNumOfDocuments(){
        return docsListSize;
    }

    /**
     * Getter for the number of unique terms in the system
     * @return the number of unique terms in the system
     */
    public int getNumOfUniqTerms(){
        return numOfTerms;
    }

	private String fixString(String st)
    {
        return st.replace("<P>","").replace("</P>","").replace("\n", "").replace("\r\n", "").replace("\r", "").trim();
    }
	
    /**
     * This function creates the posting file of the documents
     * Document's posting file structure:
     * `docID`:docIDString;maxTF;uniqueWords;date;ent1,ent2,ent3,ent4,ent5
     */
    public void buildDocumentsPosting(ArrayList<Document> documents)
    {

        try
        {


            String postingText = "";

            int docID;
            String docIDString;
            int maxTF;
            int uniqueWords;
            String date;
            String title;

            //int id, String date, String title, String idString, int maxTf, int numOfUnique
            for (Document doc : documents)
            {

                docID = doc.getId();
                docIDString = doc.getIdString();
                maxTF = doc.getMaxTf();
                uniqueWords = doc.getNumOfUnique();
                date = fixString(doc.getDate());
                title = fixString(doc.getTitle());

                //docID:docIDString;maxTF;uniqueWords;date;title
                postingText = postingText + docID + ":" + docIDString + "|" + maxTF + "|" + uniqueWords + "|" + date + "|" + title + "|\r\n";
            }
            lock.lock();
            File docFile = new File(pathToPosting + "docs.txt");
            FileWriter fWriter = new FileWriter(docFile, true);
            BufferedWriter writer = new BufferedWriter(fWriter);
            writer.write(postingText);
            writer.flush();
            writer.close();
            fWriter.close();
            lock.unlock();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    /**
     * This function increases the number of unique terms in the corpus
     * @param addVal - the value to add
     */
    public void increaseNumOfUniqueTerms(int addVal)
    {
        lock.lock();
        this.numOfTerms = this.numOfTerms + addVal;
        lock.unlock();
    }

    /**
     * This function sets the entities
     */
    public void setEntities()
    {
        File docsFile = new File(pathToPosting + "docs.txt");
        try
        {
            ArrayList<String> fileLines = new ArrayList<>(Files.readAllLines(docsFile.toPath()));
            docsFile.delete();
            FileWriter fileWriter = new FileWriter(docsFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String[] sortedArray =  sortArray(fileLines);
            Collections.sort(docsList);
            for(Document doc : docsList){
                int docId = doc.getId();
                String line = sortedArray[docId];
                line = line + doc.getStringEntities();
                bufferedWriter.write(line + "\r\n");
            }

            bufferedWriter.close();
            fileWriter.close();


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.docsListSize = docsList.size();
        docsList.clear();
    }

    /**
     * This function sorts the array received
     * @param fileLines - the lines to sort
     * @return - the sorted lines as array of strings
     */
    private String[] sortArray(ArrayList<String> fileLines)
    {
        //put in array
        String[] sortedArray = new String [fileLines.size()+1];
        for (String line : fileLines){
            if(line.indexOf(":") !=-1){
                int docID = Integer.parseInt(line.substring(0, line.indexOf(":")));
                sortedArray[docID] = line;
            }
        }
        return sortedArray;
    }

    /**
     * This function writes the number of unique terms to the disk
     */
    public void writeNumofTermsToDisk()
    {
        try
        {
            File file = new File(pathToPosting + "details.txt");
            FileWriter fwriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fwriter);
            String toWrite = "" + this.numOfTerms;
            writer.write(toWrite);
            writer.close();
            fwriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
