package ModelPackage;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WordToVec
{


    private int numOfResultInList;

    /**
     * Constructor of WordToVec
     */
    public WordToVec()
    {
        this.numOfResultInList = 10;
    }

    /**
     * This function gets the semantics terms of the specific term
     * @param term - the term to search
     * @return an arraylist of semantics terms
     */
    public ArrayList<String> getSemanticTerms(String term) {

        ArrayList<String> semanticTerms = new ArrayList<>();

        try
        {
            Word2VecModel model = Word2VecModel.fromTextFile(new File("./resource/modelOutput.txt"));
            com.medallia.word2vec.Searcher semanticSearcher = model.forSearch();

            List<Searcher.Match> matches = semanticSearcher.getMatches(term, this.numOfResultInList);

            for (Searcher.Match match : matches)
                semanticTerms.add(match.match());

            semanticTerms.remove(term);
        }

        catch (Searcher.UnknownWordException noTermsExp)
        {
            return semanticTerms;
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }

        return semanticTerms;
    }

}
