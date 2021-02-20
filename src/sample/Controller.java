// Created by:
//              Liat Cohen 205595283
//              Adir Biran 308567239

package sample;

import ModelPackage.Document;
import ModelPackage.Term;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.*;
import java.io.File;

public class Controller {


    @FXML public Label textField_pathToCorpus;

    @FXML public Label textField_pathToPosting;

    @FXML public Label textField_pathToQueries;

    @FXML public TextField textField_Query;

    @FXML public CheckBox checkBox_stemming;

    @FXML public CheckBox checkBox_semantics;

    @FXML public CheckBox checkBox_clickStream;

    @FXML public Button button_corpusBrowse;

    @FXML public Button button_postingBrowse;

    @FXML public Button button_queriesBrowse;

    @FXML public Button button_queriesRun;

    @FXML public Button button_runQuery;

    @FXML public Button button_start;

    @FXML public Button button_reset;

    @FXML public Button button_showDictionary;

    @FXML public Button button_loadDictionary;

    private ViewModel viewModel;


    /**
     * Setter for the viewModel
     * @param viewModel - the viewModel to set
     */
    public void setViewModel(ViewModel viewModel){
        this.viewModel = viewModel;
    }

    /**
     * open directory chooser to get the path to corpus and postings
     *
     * @param actionEvent - button pushed event
     */
    public void browseButtonPushed (ActionEvent actionEvent){
        DirectoryChooser dc = new DirectoryChooser();
        File directory = dc.showDialog(null);
        if(directory!=null){
            Button btn = (Button) actionEvent.getSource();
            if(btn.equals(button_corpusBrowse)){
                textField_pathToCorpus.setText(directory.getPath());
            }
            else if(btn.equals(button_postingBrowse)){
                textField_pathToPosting.setText(directory.getPath());
                button_runQuery.setDisable(false);
            }

            if(!(textField_pathToQueries.getText().equals("Path to Queries")||textField_pathToPosting.getText().equals("Path to Posting")))
                button_queriesRun.setDisable(false);

            if(!textField_pathToPosting.getText().equals("Path to Posting"))
                button_loadDictionary.setDisable(false);

            if(!(textField_pathToCorpus.getText().equals("Path to Corpus")||textField_pathToPosting.getText().equals("Path to Posting")))
                button_start.setDisable(false);
        }
    }


    /**
     * browse the queries file
     * @param actionEvent - button pushed event
     */
    public void browseQueriesButtonPushed (ActionEvent actionEvent)
    {
        FileChooser fileChooser = new FileChooser();
        File queries = fileChooser.showOpenDialog(null);
        if(queries!=null){
            button_queriesRun.setDisable(false);
            textField_pathToQueries.setText(queries.getPath());
            if(textField_pathToPosting.getText().equals("Path to Posting")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("to run queries please select path to posting files");
                alert.setHeaderText("INFORMATION");
                alert.show();
            }
        }

    }

    /**
     * Run the queries file
     * @param actionEvent - button pushed event
     */
    public void runQueriesButtonPushed (ActionEvent actionEvent)
    {
        button_queriesRun.setDisable(true);

       viewModel.runQueries(textField_pathToQueries.getText(), textField_pathToPosting.getText(),checkBox_semantics.isSelected(), false);

    }

    /**
     * Run the query search
     * @param actionEvent - button pushed event
     */
    public void runQueryButtonPushed (ActionEvent actionEvent)
    {
        button_queriesRun.setDisable(true);
        if(textField_Query.getText().length()>1)
            viewModel.runQuery(textField_Query.getText(), textField_pathToPosting.getText(),checkBox_semantics.isSelected(), checkBox_clickStream.isSelected());
        else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Please insert a query");
            alert.setHeaderText("INFORMATION");
            alert.show();
        }
    }

    /**
     * starts the indexing process
     * @param actionEvent - button pushed event
     */
    public void startButtonPushed (ActionEvent actionEvent){

        button_start.setDisable(true);
        button_corpusBrowse.setDisable(true);
        button_postingBrowse.setDisable(true);

        viewModel.start(textField_pathToCorpus.getText(), textField_pathToPosting.getText()+"/", checkBox_stemming.isSelected());

        //indexer finished its work
        button_reset.setDisable(false);
        button_showDictionary.setDisable(false);
        button_loadDictionary.setDisable(false);

    }

    /**
     * deletes all postings files
     * @param actionEvent - button pushed event
     */
    public void resetButtonPushed (ActionEvent actionEvent){
        button_start.setDisable(true);
        button_showDictionary.setDisable(true);
        button_loadDictionary.setDisable(true);

        viewModel.reset();

        button_corpusBrowse.setDisable(false);
        button_postingBrowse.setDisable(false);
        textField_pathToPosting.setText("Path to Posting");
        textField_pathToCorpus.setText("Path to Corpus");
        button_reset.setDisable(true);
    }

    /**
     * shows the dictionary on the screen
     * @param actionEvent - button pushed event
     */
    public void showDictionaryButtonPushed (ActionEvent actionEvent){
        Map<String , Term> dictionary = viewModel.getDictionary();

        TreeMap<String,Term> sortedDictionary = new TreeMap<>();
        for(String term : dictionary.keySet()){
            sortedDictionary.put(term, dictionary.get(term));
        }

        Stage dictStage = new Stage();
        dictStage.setTitle("DICTIONARY");

        ObservableList<Term> data = FXCollections.observableArrayList();
        data.addAll(sortedDictionary.values());

        TableColumn firstCol = new TableColumn();
        firstCol.setText("Term");
        firstCol.setCellValueFactory(new PropertyValueFactory("term"));
        TableColumn secondCol = new TableColumn();
        secondCol.setText("Frequency");
        secondCol.setCellValueFactory(new PropertyValueFactory("termFrequency"));

        TableView tableView = new TableView<>();

        tableView.setItems(data);

        tableView.getColumns().addAll(firstCol, secondCol);



        Scene scene = new Scene(tableView);
        dictStage.setScene(scene);
        dictStage.show();

    }

    /**
     * loads the dictionary from disc to RAM
     * @param actionEvent
     */
    public void loadDictionaryButtonPushed (ActionEvent actionEvent){
        viewModel.setPathToPosting(textField_pathToPosting.getText());
        viewModel.loadDictionary();
        button_showDictionary.setDisable(false);
        button_reset.setDisable(false);
        button_loadDictionary.setDisable(true);
    }


    /**
     * This function shows the details of the indexing proccess
     * @param numOfDocuments - documents indexed
     * @param numOfUniqTerms - number of unique terms indexed
     * @param totalTimeInMillis - time passed in milliseconds
     */
    public void showIndexingData(int numOfDocuments, int numOfUniqTerms, long totalTimeInMillis) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String massage = "Number Of Documents Indexed: "+ numOfDocuments+"\nNumber of uniq terms: "+numOfUniqTerms+"\nTotalTime : "+ totalTimeInMillis/1000+" sec";

        alert.setHeaderText("Index Information");
        alert.setContentText(massage);
        alert.show();
    }

    /**
     * Shows the queries list
     * @param queries received
     * @param documentsOfQuery documents relevant to queries
     */
    //                                  query , List.toString()
    public void showQueriesList(HashMap<String, String> queries, ArrayList<HashMap<Document, Double>> documentsOfQuery){
        Stage stage = new Stage();
        final VBox vbox = new VBox();
        ChoiceBox choiceBox = new ChoiceBox();
        List<String> list = new ArrayList<>();
        list.addAll(queries.keySet());
        ObservableList obList = FXCollections.observableList(list);
        choiceBox.getItems().clear();
        choiceBox.setItems(obList);
        choiceBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String query = (String)choiceBox.getValue();
                String docId = queries.get(query);
                HashMap<Document, Double> docMap = findMap(docId, documentsOfQuery);
                if(docMap!=null){
                    showTop50(docMap, query);
                }
            }
        });
        final Label label = new Label("Select a query");

        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getChildren().addAll(label, choiceBox);
        Scene scene = new Scene(vbox);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This function finds the relevant map
     * @param listToString string to compare to
     * @param documentsOfQuery the documents relevant to queries
     * @return the relevant map. null if not found
     */
    private HashMap<Document, Double> findMap(String listToString, ArrayList<HashMap<Document, Double>> documentsOfQuery)
    {
        for(HashMap<Document, Double> map : documentsOfQuery){
            if(map.toString().equals(listToString))
                return map;
        }
        return null;
    }

    /**
     * This function shows the top 50 relevant documents
     * @param docsRankingMap - the documents and the rankings
     * @param query - the query searched
     */
    private void showTop50(HashMap<Document, Double> docsRankingMap, String query)
    {
        Stage dictStage = new Stage();
        dictStage.setTitle("QueryResult");


        ObservableList<TableViewEntry> data = FXCollections.observableArrayList();
        ArrayList<TableViewEntry> tableEntries = new ArrayList<>();
        int i=0;
        for(Document doc : docsRankingMap.keySet()){
            if(i==50) break;
            tableEntries.add(new TableViewEntry(doc.getIdString(), docsRankingMap.get(doc)));
            i++;
        }
        data.addAll(tableEntries);
        TableColumn firstCol = new TableColumn();
        firstCol.setText("Document");
        firstCol.setCellValueFactory(new PropertyValueFactory("docID"));
        TableColumn secondCol = new TableColumn();
        secondCol.setText("Score");
        secondCol.setCellValueFactory(new PropertyValueFactory("docScore"));
        secondCol.setComparator(secondCol.getComparator().reversed());

        TableView tableView = new TableView<>();

        tableView.setItems(data);

        tableView.getColumns().addAll(firstCol, secondCol);

        tableView.getSortOrder().addAll(secondCol);

        final Label label = new Label(query + "\nNumber of documents: "+ docsRankingMap.size());
        label.setFont(new Font("Tahoma", 16));
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, tableView);

        Scene scene = new Scene(vbox);
        dictStage.setScene(scene);
        dictStage.show();
    }
}
