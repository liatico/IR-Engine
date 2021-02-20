// Created by:
//              Liat Cohen 205595283
//              Adir Biran 308567239

package ModelPackage;

import java.util.*;


public class Parse
{
    private HashSet<String> stopWords;// calculate contains in O(1)
    private String[] months = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec",
            "january", "february", "march","april","may", "june", "july", "august", "september", "october", "november", "december"};
    private Indexer indexer;
    private boolean stem;
    private Stemmer stemmer;


    /**
     * Constructor - parse is created only in Model
     * @param stopWords - a list provited outside the program
     * @param indexer - the indexer to index the parsed document
     */
    public Parse(HashSet<String> stopWords, Indexer indexer) {
        this.stopWords = stopWords;
        this.indexer = indexer;
        this.stemmer = new Stemmer();
        this.stem = true;

    }


    /**
     * Setter for the stemmer
     * @param stem the stemmer to set
     */
    public void setStemmer(boolean stem)
    {
        this.stem = stem;
    }

    /**
     * parse a query same way as the documents
     * @param query - query to parse
     */
    public HashMap<String, Integer> parseQuery(String query){
        if(query.contains("-")){
            query = query.replace("-", " ");
        }
        return parse(query);
    }

    public String parseAndGetText(String text)
    {
        String res = "";

        HashMap<String, Integer> map = parse(text);

        for (String s : map.keySet())
            res = res + s + " ";

        return res.trim();
    }

    /**
     * parse the text into words and put them in a 4 sized stack to find patterns in the text
     * @param text - text to parse
     * @return hashMap of the parsed text and term frequency for the text
     */
    private HashMap<String, Integer> parse(String text){
        HashMap<String, Integer> localDictionary = new HashMap<>();
        ArrayList<String> parseToWords = splitText(text);
        Stack<String> prevWords = new Stack<>();

        prevWords.push("");

        boolean hasDollarSign, hasUnits , added, checkPrev;
        boolean firstWord = true;
        for(String word : parseToWords){
            added = false;
            checkPrev = true;
            if(firstWord){
                firstWord = false;
                addToDictionary(localDictionary, word);
                addToPrevList(prevWords, word);
                checkPrev = false;
                continue;
            }

            if(word.length()==0 || word.matches("[^a-z^A-Z^0-9]*")) continue;//empty word (no letters or number)

            //changes words like million to M
            if(word.toLowerCase().contains("-million")){
                word = word.replace("-", " ");
                word = word.replace("million", "M");
                added = true;
                addToDictionary(localDictionary, word);
                checkPrev = false;

            }
            else if(word.toLowerCase().contains("-millions")){
                word = word.replace("-", " ");
                word = word.replace("millions", "M");
                added = true;
                addToDictionary(localDictionary, word);
                checkPrev = false;
            }
            else if(word.toLowerCase().contains("-billion")){
                word = word.replace("-", " ");
                word = word.replace("billion", "B");
                added = true;
                addToDictionary(localDictionary, word);
                checkPrev = false;
            }
            else if(word.toLowerCase().contains("-billions")){
                word = word.replace("-", " ");
                word = word.replace("billions", "B");
                added = true;
                addToDictionary(localDictionary, word);
                checkPrev = false;
            }
            else if(word.toLowerCase().contains("-percent")){
                word = word.replace("-", "");
                word = word.replace("percent", "%");
                added = true;
                addToDictionary(localDictionary, word);
                checkPrev = false;
            }
            else if(word.toLowerCase().contains("-percentage")){
                word = word.replace("-", "");
                word = word.replace("percentage", "%");
                added = true;
                addToDictionary(localDictionary, word);
                checkPrev = false;
            }

            switch (word.toLowerCase()) {
                case ("percent"): {
                    addToPrevList(prevWords, "%");
                    added = true;
                    break;
                }
                case ("percentage"): {
                    addToPrevList(prevWords, "%");
                    added = true;
                    break;
                }
                case ("thousand"): {
                    addToPrevList(prevWords, "K");
                    added = true;
                    break;
                }
                case ("thousands"): {
                    addToPrevList(prevWords, "K");
                    added = true;
                    break;
                }
                case ("million"): {
                    addToPrevList(prevWords, "M");
                    added = true;
                    break;
                }
                case ("millions"): {
                    addToPrevList(prevWords, "M");
                    added = true;
                    break;
                }
                case ("m"): {
                    addToPrevList(prevWords, "M");
                    added = true;
                    break;
                }
                case ("billion"): {
                    addToPrevList(prevWords, "B");
                    added = true;
                    break;
                }
                case ("bn"): {
                    addToPrevList(prevWords, "B");
                    added = true;
                    break;
                }
                case ("billions"): {
                    addToPrevList(prevWords, "B");
                    added = true;
                    break;
                }
                case ("dollars"): {
                    addToPrevList(prevWords, "$");
                    added = true;
                    break;
                }
                case ("dollar"): {
                    addToPrevList(prevWords, "$");
                    added = true;
                    break;
                }
                case ("$"): {
                    addToPrevList(prevWords, "$");
                    added = true;
                    break;
                }
                case("kilometer"):{
                    addToPrevList(prevWords, "km");
                    added = true;
                    break;
                }
                case("kilometers"):{
                    addToPrevList(prevWords, "km");
                    added = true;
                    break;
                }
                case("meter"):{
                    addToPrevList(prevWords, "m");
                    added = true;
                    break;
                }
                case("meters"):{
                    addToPrevList(prevWords, "m");
                    added = true;
                    break;
                }
                case("centimeter"):{
                    addToPrevList(prevWords, "cm");
                    added = true;
                    break;
                }
                case("centimeters"):{
                    addToPrevList(prevWords, "cm");
                    added = true;
                    break;
                }
                case("liter"):{
                    addToPrevList(prevWords, "L");
                    added = true;
                    break;
                }
                case("liters"):{
                    addToPrevList(prevWords, "L");
                    added = true;
                    break;
                }
                case("milliliter"):{
                    addToPrevList(prevWords, "ml");
                    added = true;
                    break;
                }
                case("mililiter"):{
                    addToPrevList(prevWords, "ml");
                    added = true;
                    break;
                }case("mililiters"):{
                    addToPrevList(prevWords, "ml");
                    added = true;
                    break;
                }
                case("milliliters"):{
                    addToPrevList(prevWords, "ml");
                    added = true;
                    break;
                }
                default: {
                    break;
                }
            }

            //"clean" the token - trim numbers and adds "Dollars" instead of $
            if(!added && isItANumber(word)){

                word = handleDouble(word, prevWords.peek());// cuts to 3 digits after the point and adds K/M/B if necessary with % and $

                int wordSize = word.length();
                hasDollarSign = word.charAt(wordSize-1)=='$';
                hasUnits =(word.length() > 2 && (word.charAt(wordSize-2)=='K'||word.charAt(wordSize-2)=='M'||word.charAt(wordSize-2)=='B'))||
                        (word.length() > 1 && (word.charAt(wordSize-1)=='K'||word.charAt(wordSize-1)=='M'||word.charAt(wordSize-1)=='B'));

                if(hasDollarSign && hasUnits ){
                    word = word.substring(0, word.length()-1);
                    String units = whichUnit(word);
                    word = word.substring(0, word.length()-1);
                    addToPrevList(prevWords, word);
                    addToPrevList(prevWords, ""+units);
                    addToPrevList(prevWords, "$");
                }

                else if(hasDollarSign){
                    word = word.substring(0, word.length()-1);
                    addToPrevList(prevWords, word);
                    addToPrevList(prevWords, "$");
                }

                else if(hasUnits){
                    String units = whichUnit(word);
                    word = word.substring(0, wordSize-1);
                    addToPrevList(prevWords, word);
                    addToPrevList(prevWords, ""+units);
                }

                else
                    addToPrevList(prevWords, word);
            }

            //to find patterns we add months and stop words to the prevslist
            else if(!added){
                addToPrevList(prevWords, word);

            }

            /***
             * add words to the dictionary here:
             */
            if(checkPrev)
                checkPrevWordsStack(prevWords, localDictionary);//adds terms to dictionary
        }
        return localDictionary;
    }

    /**
     * returns K/M/B when dealing with numbers and units
     * use it only if the word is a number with units
     * @param word - word that matches {"[0-9]*[KMB][$ ]}
     * @return K/M/B according to word
     */
    private String whichUnit(String word) {
        return ""+word.charAt(word.length()-1);
    }

    /**
     * go over the stack and finds patterns
     * dates - DD-MM || MM-YYYY || DD-MM-YYYY
     * entities - sequence of words that starts with capital letter
     * numbers with units and $/%
     * @param prevWords - stack of 4 prev words
     * @param dictionary - dictionary to add terms to
     */
    private void checkPrevWordsStack(Stack<String> prevWords, HashMap<String, Integer> dictionary){
        int size = prevWords.size();

        if(size > 2) {

            boolean added = false, poped4 = false;
            String word1, word2, word3, word4 = "";
            word1 = prevWords.pop();
            word2 = prevWords.pop();
            word3 = prevWords.pop();

            if (size == 4){
                poped4 = true;
                word4 = prevWords.pop();
            }

            /***
             * handle dates:
             */
            if(isWordAMonth(word3)&& isItANumber(word2)&& isItANumber(word1)||
                    isItANumber(word3)&&isWordAMonth(word2)&&isItANumber(word1))
            {
                addDateIncludingYear(dictionary, word3, word2, word1);
                added = true;

            }
            else if ((isItANumber(word1) && isWordAMonth(word2)) || (isItANumber(word2) && isWordAMonth(word1))) {
                addDateToDictionary(dictionary, word1, word2);
                added = true;
            }
            else if(isWordAMonth(word1)){
                added = true;//dont do anything
            }


            /***
             * handle units K\M\B\$\%
             */
            else if (isItANumber(word2)) {
                if (isItAFraction(word1) && !(word2.contains("/"))) {
                    concatAndAddToDictionary(" " + word1, dictionary, word2, true);
                    added = true;
                }
                switch (word1) {
                    case ("%"): {
                        concatAndAddToDictionary("%", dictionary, word2, true);//adds to dictionary
                        added = true;
                        break;
                    }
                    case ("K"): {
                        concatAndAddToDictionary(" K", dictionary, word2,true);
                        added = true;
                        break;
                    }
                    case ("M"): {
                        concatAndAddToDictionary(" M", dictionary, word2,true);
                        added = true;
                        break;
                    }
                    case ("B"): {
                        concatAndAddToDictionary(" B", dictionary, word2,true);
                        added = true;
                        break;
                    }
                    case ("$"): {
                        concatAndAddToDictionary(" Dollars", dictionary, word2,true);
                        added = true;
                        break;
                    }
                    case("km"):{
                        concatAndAddToDictionary(" km", dictionary, word2,true);
                        added = true;
                        break;
                    }
                    case("m"):{
                        concatAndAddToDictionary(" m", dictionary, word2,true);
                        added = true;
                        break;
                    }
                    case("cm"):{
                        concatAndAddToDictionary(" cm", dictionary, word2,true);
                        added = true;
                        break;
                    }
                    case("L"):{
                        concatAndAddToDictionary(" L", dictionary, word2,true);
                        added = true;
                        break;
                    }
                    case("ml"):{
                        concatAndAddToDictionary(" ml", dictionary, word2,true);
                        added = true;
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }

            else if (isItANumber(word3) && (word2.equals("$"))) {

                //switch on the units
                switch (word1) {
                    case ("K"): {
                        concatAndAddToDictionary(" Dollars", dictionary, word3 + " K", false);
                        removeFromDictionary(dictionary, word3 + " Dollars");
                        added = true;
                        break;
                    }

                    case ("M"): {
                        concatAndAddToDictionary(" Dollars", dictionary, word3 + " M", false);
                        removeFromDictionary(dictionary, word3 + " Dollars");
                        added = true;
                        break;
                    }
                    case ("B"): {
                        concatAndAddToDictionary(" Dollars", dictionary, word3 + " B", false);
                        removeFromDictionary(dictionary, word3 + " Dollars");
                        added = true;
                        break;
                    }
                    default:
                        break;
                }
            }

            else if (isItANumber(word3) && isItUnit(word2) && word1.equals("$")) {
                concatAndAddToDictionary(" Dollars", dictionary, word3 + " " + word2, true);
                added = true;
            }

            else if ((word1.equals("$")) &&
                    word2.toLowerCase().equals("us") &&
                    (isItANumber(word3) || (isItANumber(word4) && isItUnit(word3)))) {

                if (isItUnit(word3)) {
                    concatAndAddToDictionary(" Dollars", dictionary, word4 + " " + word3, true);
                    added = true;
                }
                else {
                    concatAndAddToDictionary(" Dollars", dictionary, word3, true);
                    added = true;
                }


            }

            else if(word4.toLowerCase().equals("between")&&
                    isItANumber(word3)&&
                    word2.toLowerCase().equals("and")&&
                    isItANumber(word1)){
                String token = word3 + "-" + word1;
                addToDictionary(dictionary, token);
                added = true;
            }
            /***
             * handle entities
             */

            else if (isWordStartsWithCapitalLetter(word1) && !isItUnit(word1) && word1.length()>1) {
                if (isWordStartsWithCapitalLetter(word2)&&!isAllCapital(word2)&& !isItUnit(word2)&& word2.length()>1 && !word2.contains("-")) {
                    concatAndAddToDictionary(" " + word1, dictionary, word2, true);
                    added = true;
                }
                else{
                    if(word1.contains("-")){
                        word1 = word1.replace("-", " ");
                    }
                    addToDictionary(dictionary, word1);
                    added = true;
                }


                }

            if (!added && !isItAStopWord(word1) && !isItUnit(word1)&& !word1.equals("$") && !word1.equals("%"))
                addToDictionary(dictionary, word1);

            if (poped4)
                prevWords.push(word4);
            else if(isWordStartsWithCapitalLetter(word2)){
                addToDictionary(dictionary, word2);
            }

            prevWords.push(word3);
            prevWords.push(word2);
            prevWords.push(word1);
        }

    }

    private boolean isAllCapital(String word) {
        return word.equals(word.toUpperCase());
    }

    /**
     * checks if a number is a fraction
     * @param word - number
     * @return true if the number is a fraction
     */
    private boolean isItAFraction(String word) {
        if(isItANumber(word)){
            int index = word.indexOf('/');
            return index > 0;
        }
        return false;
    }

    /**
     * checks if a word is unit - K/M/B
     * @param word - word from stack
     * @return true if it is
     */
    private boolean isItUnit(String word) {
        String units = "K,M,B,US$,m,km,cm,L,ml";
        String[] unisArray = units.split(",");
        for (int i = 0; i <unisArray.length ; i++) {
            if(word.equals(unisArray[i]))return true;
        }
        return false;
    }

    /**
     * removes the duplicate values in the dictionary
     * useful when trying to concat words or numbers into a term
     * @param dictionary - local dictionary of a specific document
     * @param word - word to remove its duplicates
     */
    private void removeFromDictionary(HashMap<String, Integer> dictionary, String word) {
        int value = 0;
        if(stem && !isWordStartsWithCapitalLetter(word)){
            word = stemmer.stem(word);
        }
        if(dictionary.containsKey(word)){
            value = dictionary.get(word);
        }
        else if (dictionary.containsKey(word.toUpperCase())) {
            word = word.toUpperCase();
            value = dictionary.get(word);
        }
        else if (dictionary.containsKey(word.toLowerCase())) {
            word = word.toLowerCase();
            value = dictionary.get(word);
        }
        if (value > 1) {
            value--;
            dictionary.replace(word, value);
        }
        else if (value == 1) {
            dictionary.remove(word);
        }
    }

    /**
     * public method to parse documents
     * @param documents - set of documents to parse
     * @param texts - set of the documents's text to parse
     *              make sure that the documents[i] match the text in texts[i]
     */
    public void parseDocuments(ArrayList<Document> documents, ArrayList<String> texts)
    {

        int len = documents.size();
        HashMap<String, Integer> dict;

        ArrayList<Document> documentsList = new ArrayList<>();
        ArrayList<Map> dictionaryList = new ArrayList<>();

        for (int i = 0; i < len; i++){
            Document curr = documents.get(i);
            dict = parseDocument(curr, texts.get(i));
            documentsList.add(curr);
            dictionaryList.add(dict);
        }

        indexer.indexFile(documentsList, dictionaryList);

    }

    /**
     * parse a single document
     * @param doc - document to parse
     * @param text - text to parse
     * @return the document's dictionary of terms and frequency
     */
    private HashMap<String, Integer> parseDocument(Document doc, String text){

        HashMap<String, Integer> localDictionary;
        //we keep a local dictionary for each document to count term frequency and merge all local dictionaries to one.

        localDictionary = parse(text);

        //find max tf
        int maxTF = findMaxTF(localDictionary);
        doc.setMaxTf(maxTF);
        doc.setNumOfUnique(localDictionary.size());


        return localDictionary;

    }

    /**
     * finds the max term frequency for a dictionary of a document     * @param localDictionary
     * @return the max term frequency for the document
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
     * converts a String month to its equivalent number
     * @param month - string month (jan, feb.. ect.)
     * @return - number month (1, 2, .. ect.)
     */
    private String convertMonthToNumber(String month){
        String numberMonth="";
        for (int i = 0; i <months.length ; i++) {
            if(month.toLowerCase().equals(months[i])){
                if(i > 11){
                    i = i-11;
                    numberMonth ="" + i;
                    break;
                }
                else{
                    i = i+1;
                    numberMonth ="" + i;
                    break;
                }
            }
        }
        return numberMonth;
    }

    /**
     * adds a date DD-MM / MM-YYYY to dictionary
     * @param localDictionary - dictionary to add to
     * @param day - day
     * @param month - month
     */
    private void addDateToDictionary(HashMap<String, Integer> localDictionary, String day, String month) {
        //MM-DD || MM-YYYY
        String date = "";
        if(day.contains(".") || month.contains("."))return;

        if(isWordAMonth(month)){
            if(day.length() > 2) return;
            month = convertMonthToNumber(month);
            if(month.length()==1) month = "0"+month;
            date = month +"-"+ day;
        }
        else if(isWordAMonth(day)){
            if(month.length() > 2) return;
            day = convertMonthToNumber(day);
            if(day.length()==1) day = "0"+day;
            date = day +"-"+ month;
        }
        addToDictionary(localDictionary, date);

    }

    /**
     * adds a date DD-MM-YYYY to dictionary
     * @param dict- dictionary to add to
     * @param day - day
     * @param month - month
     * @param year - year
     */
    private void addDateIncludingYear(HashMap<String, Integer> dict, String day, String month, String year){
        //MM-DD-yyyy
        if(day.contains(".") || month.contains(".") || year.contains(".") || year.length()!=4)return;
        String date = "";
        if(isWordAMonth(month)){
            if(day.length()>2)return;
            month = convertMonthToNumber(month);
            if(month.length()==1) month = "0"+month;
            date = month +"-"+ day +"-"+ year;
        }
        else if(isWordAMonth(day)){
            if(month.length()>2)return;
            day = convertMonthToNumber(day);
            if(day.length()==1) day = "0"+day;
            date = day +"-"+ month+"-"+ year;
        }
        addToDictionary(dict, date);
    }

    /**
     * adds a word to the dictionary - add as a new word if it doesn't there already
     * and increase frequency counter if it is
     * @param dictionary - dictionary to add to
     * @param word - word to add
     */
    private void addToDictionary(HashMap<String, Integer> dictionary, String word){
        int value;
        if(isItAStopWord(word)|| isWordAMonth(word) ||(!isWordContainsANumber(word) && !isWordContainsLetters(word)) )return;

        if(stem && !isWordStartsWithCapitalLetter(word)){
            word = stemmer.stem(word);
        }
        if(isWordContainsANumber(word)){
            if(dictionary.containsKey(word)){
                value = dictionary.get(word);
                value++;
                dictionary.replace(word, value);
            }
            else
                dictionary.put(word, 1);

        }

        else if(dictionary.containsKey(word.toUpperCase())){
            if(isWordStartsWithCapitalLetter(word)){//starts with capital letter so we save it as an entity.
                word = word.toUpperCase();
                value = dictionary.get(word);
                value++;
                dictionary.replace(word, value);
            }
            else{
                word = word.toLowerCase();
                value = dictionary.get(word.toUpperCase());
                dictionary.remove(word.toUpperCase());
                dictionary.put(word, value+1);
            }
        }

        else if(dictionary.containsKey(word.toLowerCase())){
            word = word.toLowerCase();
            value = dictionary.get(word);
            value++;
            dictionary.replace(word, value);
        }

        else{
            if(isWordStartsWithCapitalLetter(word)){//starts with capital letter
                word = word.toUpperCase();
            }
            else{
                word = word.toLowerCase();

            }
            dictionary.put(word, 1);
        }

    }

    /**
     * checks if a word contains letters
     * @param word
     * @return
     */
    private boolean isWordContainsLetters(String word) {
        for (int i = 0; i <word.length() ; i++) {
            if((word.charAt(i) >='a' && word.charAt(i) <='z' )|| (word.charAt(i) >='A' && word.charAt(i) <='Z'))
                return true;
        }
        return false;
    }

    /**
     * checks if a word starts with capital letter
     * @param word
     * @return true if the word starts with capital letter
     */
    private boolean isWordStartsWithCapitalLetter(String word){
        if(word.length()>0)
            return word.charAt(0)>='A' && word.charAt(0)<='Z';
        return false;
    }

    /**
     * concat @param addition to @param word and add it to the dictionary
     *
     * @param localDictionary - dictionary to add to
     */
    private void concatAndAddToDictionary(String addition, HashMap<String, Integer> localDictionary, String word, boolean remove) {

        if(remove){
            removeFromDictionary(localDictionary, word);
        }

        word = word + addition;
        addToDictionary(localDictionary, word);
    }

    /**
     * adds s to prev word stack.
     * this stack is in size 4 so if it is full the function removes the oldest value add adds the new one
     * @param prevWords - stack to add to
     * @param s - string to add
     */
    private void addToPrevList(Stack<String> prevWords, String s) {
        Stack<String> tempStack = new Stack<>();
        if(prevWords.size()==4){
            while(!prevWords.isEmpty()){
                tempStack.push(prevWords.pop());
            }
            tempStack.pop();
            while(!tempStack.isEmpty()){
                prevWords.push(tempStack.pop());
            }
            prevWords.push(s);
        }
        else{
            prevWords.push(s);
        }
    }

    /**
     * cuts big numbers into numbers with units K/M/B and cuts to 3 digits after the point
     * checks for $ and % and consider them
     * @param word - a number to handle
     * @param prevWord - the word that was before the number - to make sure it is not a year
     * @return the number after trimming and adding units tang signs
     */
    private String handleDouble(String word , String prevWord) {

        if(isNumberHasThreeZeroesAfterFloatingPoint(word)){
            int index = word.indexOf('.');
            word = word.substring(0, index);
        }

        boolean hasPercent = word.charAt(word.length()-1)=='%' ;
        boolean percentOnLeft = word.charAt(0)=='%';
        boolean hasDollarSign = word.charAt(word.length()-1)=='$';
        boolean dollarOnLeft = word.charAt(0)=='$';


        if(word.contains("/")) return word;

        if(hasDollarSign||hasPercent)
            word = word.substring(0,word.length()-1);

        if(dollarOnLeft || percentOnLeft)
            word = word.substring(1);
        double number = Double.parseDouble(word);

        if(number / 1000000000 > 1){
            word = trimNumber(number, "B");
        }
        else if(number / 1000000 > 1){
            word = trimNumber(number, "M");

        }
        else if(number / 1000 > 1 && notAYear(number, prevWord)){
            word = trimNumber(number, "K");
        }
        else{
            number = cutDoubleNumberEnds(number);//decide if and how to get this from outside?
            word = "" + number;
            word = handleInteger(word);
        }

        if(hasPercent||percentOnLeft)
            word = word+"%";
        if(hasDollarSign||dollarOnLeft)
            word = word +"$";

        return word;
    }

    /**
     * checks if a number has 3 zeroes after to floating point
     * @param word - word to check
     * @return true if the word has 3 zeroes after the point
     */
    private boolean isNumberHasThreeZeroesAfterFloatingPoint(String word) {
        if(word.length()< 5) return false;
        int index = word.indexOf('.');
        if(index > 0){
            if(word.length()>index+4)
                return word.charAt(index+1)=='0' && word.charAt(index+2)=='0'&&word.charAt(index+3)=='0';
        }
        return false;
    }

    /**
     * checks is a number is year
     * @param number - number to check
     * @param prevWord - check if word is a year based on the previous word
     * @return true if the word is not a year.
     */
    private boolean notAYear(double number, String prevWord) {//returns true if the number is not a year
        String stringNum = ""+number;
        if(number >= 1900 && number <=2020){
            return false;
        }
        stringNum = handleInteger(stringNum);

        if(stringNum.length() != 4 )
            return true;

        if(isWordAMonth(prevWord))
            return false;

        if(prevWord.toLowerCase().equals("in")|| prevWord.toLowerCase().equals("at"))
            return false;

        return true;
    }

    /**
     * checks if a word is a month
     * @param word - word to check
     * @return true if word is a month
     */
    private boolean isWordAMonth(String word) {
        for (String month: months) {
            if(month.equals(word.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    /**
     * cleans a number - uses inside "handleDouble"
     * cuts a number and adds K/M/B
     * @param number - number to cut
     * @param typeOfChang - K/M/B
     * @return the cleaned word
     */
    private String trimNumber(double number, String typeOfChang) {
        String word;
        switch (typeOfChang){
            case("B"):{
                number = number/1000000000;
                number = cutDoubleNumberEnds(number);
                word = "" + number;
                word = handleInteger(word);
                word = word + "B";
                return word;
            }
            case("M"):{
                number = number/1000000;
                number = cutDoubleNumberEnds(number);
                word = "" + number;
                word = handleInteger(word);
                word = word + "M";
                return word;
            }
            case("K"):{
                number = number/1000;
                number = cutDoubleNumberEnds(number);
                word = "" + number;
                word = handleInteger(word);
                word = word + "K";
                return word;
            }
        }
        return "";
    }

    /**
     * use to trim .0 of ta integer that was parsed as double
     * @param word - number to trim
     * @return - cleaned number
     */
    private String handleInteger(String word) {
        int index = word.indexOf(".");
        if(word.length()==index+2 && word.charAt(index+1)=='0'){
            word = word.substring(0,index);
            return word;
        }
        return word;
    }

    /**
     * cuts a double to 3 digits after to floating point
     * @param number - number to cut
     * @return the number after trimming its ends
     */
    private double cutDoubleNumberEnds(double number) {
        String stringNum = "" + number;
        int index = stringNum.indexOf(".");

        if(index > -1 ){
            int i = index + 4 ;

            if(i < stringNum.length())
                stringNum = stringNum.substring(0,i);
        }

        return Double.parseDouble(stringNum);
    }

    /**
     * checks if a word is a number
     * @param word word to check
     * @return true if the word is a number with or without units
     */
    private boolean isItANumber(String word) {

        if(word.length() == 0 || word == null){
            return false;
        }
        char c;
        boolean hasAPoint = false;
        if(!isWordContainsANumber(word))return false;

        for (int i = 0; i <word.length() ; i++) {
            c = word.charAt(i);
            if(hasAPoint && c=='.')return false;//if the number contains more then one point (like ip adders) handle like a word

            if(c=='.') hasAPoint=true;
            if(!((c >= '0' && c<= '9')|| (c=='%') || (c=='$')||(c=='.')|| (c=='/') || (c==' '))){
                return false;
            }
        }
        return true;
    }

    /**
     * checks is a word contains a number - alpha numeric expresion
     * @param word - word to check
     * @return true if the word contains numbers
     */
    private boolean isWordContainsANumber(String word){
        if(word.length()==0) return false;
        char c;
        for (int i = 0; i <word.length() ; i++) {
            c = word.charAt(i);
            if(c >= '0' && c<= '9'){
                return true;
            }
        }
        return false;
    }

    /**
     * checks if a word is a stop word
     * @param word - word to check
     * @return true if it is
     */
    private boolean isItAStopWord(String word) {
        word = word.toLowerCase();
        return stopWords.contains(word);
    }

    /**
     * split the text into a vector of words
     * @param text - text to split
     * @return a vector of all words
     */
    private ArrayList<String> splitText(String text){
        ArrayList<String> splitedText = new ArrayList<>();
        char c;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < text.length() ; i++) {
            c = text.charAt(i);
            if(c ==' '||c == '\n' || c==':' || c=='>' || c== ')' || c=='}'||c == '\r'){
                if(buffer.length()==0) continue;
                else {
                    splitedText.add(buffer.toString());
                    buffer = new StringBuilder();
                    continue;
                }
            }

            if(c == '[' && i+9 < text.length()){
                if(text.charAt(i+1)=='T' &&
                        text.charAt(i+2)=='e' &&
                        text.charAt(i+3)=='x'&&
                        text.charAt(i+4)=='t'&&
                        text.charAt(i+5)==']'){
                    splitedText = new ArrayList<>();
                    i += 6;
                    continue;
                }
                //[Excerpt]
                else if(text.charAt(i+1)=='E' &&
                        text.charAt(i+2)=='x'&&
                        text.charAt(i+3)=='c'&&
                        text.charAt(i+4)=='e'&&
                        text.charAt(i+5)=='r'&&
                        text.charAt(i+6)=='p'&&
                        text.charAt(i+7)=='t'&&
                        text.charAt(i+8)==']'){
                    splitedText = new ArrayList<>();
                    i += 9;
                    continue;
                }
            }


            if(c == '('||c == '<'||c == '{') continue;

            if(c == '$'||c =='%'){
                if(buffer.length()!=0){
                    buffer.append(c);
                    splitedText.add(buffer.toString());
                    buffer = new StringBuilder();
                }
                else{
                    buffer.append(c);

                }

                continue;
            }
            if(c==',' && isStringContainsLettersOnly(buffer.toString())){
                splitedText.add(buffer.toString());
                buffer = new StringBuilder();
                continue;
            }

            if(c == '.'){
                if(isStringContainsLettersOnly(buffer.toString())){
                    splitedText.add(buffer.toString());
                    buffer = new StringBuilder();
                    continue;
                }
                if(isItANumber(buffer.toString())){
                    if(i < text.length()-1){
                        if(isCharADigit(text.charAt(i+1)) && !buffer.toString().contains(".")){
                            buffer.append(c);
                            continue;
                        }
                    }
                    else continue;

                }
            }
            if(c == '-' ){
                if(i>0 && i<text.length()-1 && buffer.toString().length()>1 &&
                        buffer.toString().charAt(buffer.toString().length()-1)!='-'&&
                        text.charAt(i+1)!='-' && !isItANumber(buffer.toString()))
                    buffer.append(c);
                continue;
            }
            if(c == '/') {
                if (isStringContainsLettersOnly(buffer.toString())) {
                    splitedText.add(buffer.toString());
                    buffer = new StringBuilder();
                    continue;
                }
                else if(i>0 && i<text.length()-1 && text.charAt(i-1)!= '/' && (isCharADigit(text.charAt(i+1))))
                    {
                    buffer.append(c);
                    continue;
                }
            }

            if(isCharALetter(c)){
                if(i == text.length()-1){
                    buffer.append(c);
                    splitedText.add(buffer.toString());
                    continue;
                }
                if(buffer.length()==0){
                    buffer.append(c);
                    continue;
                }
                else {
                    if(buffer.toString().matches("[a-z-]*")&&(c >='A' && c <= 'Z')){
                        splitedText.add(buffer.toString());
                        buffer = new StringBuilder();
                        buffer.append(c);
                        continue;
                    }
                    else if(isStringContainsLettersOnly(buffer.toString())){
                        buffer.append(c);
                        continue;
                    }
                    else if(isItANumber(buffer.toString())){
                        splitedText.add(buffer.toString());
                        buffer = new StringBuilder();
                        buffer.append(c);
                    }
                    else if(buffer.toString().contains("-")){
                        buffer.append(c);
                        continue;
                    }
                }

            }
            if(isCharADigit(c)){
                if(buffer.length()==0){
                    buffer.append(c);
                    continue;
                }
                else {
                    if(isStringContainsLettersOnly(buffer.toString())){
                        splitedText.add(buffer.toString());
                        buffer = new StringBuilder();
                        buffer.append(c);
                    }
                    else if(isItANumber(buffer.toString()) || buffer.toString().contains("-")){
                        buffer.append(c);
                        continue;
                    }
                }
            }
        }
        return splitedText;
    }


    private boolean isCharADigit(char c){
        return c>='0' && c<='9';
    }

    /**
     * checks if a string contains only letters
     * @param word - word to check
     * @return - true if word contains only letters
     */
    private boolean isStringContainsLettersOnly(String word){
        if(word.length()==0)return false;
        char c;
        for (int i = 0; i < word.length(); i++) {
            c = word.charAt(i);
            if(!((c >= 'a' && c <= 'z')||(c >= 'A' && c <= 'Z') || c=='\'' || c=='-')){
                return false;
            }
        }
        return true;
    }

    private boolean isCharALetter(char c){
        return (c >= 'a' && c <= 'z') || (c >='A' && c <= 'Z');
    }


}
