package GUI;

import DataStructure.Vocab;
import Trainer.VocabManager;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Controller {
    /**
     * TextField in which the current question gets written.
     */
    @FXML
    private TextField questionTextField;
    /**
     * TextField in which the user can enter his answer.
     */
    @FXML
    private TextField answerTextField;
    /**
     * TextFlow in which the from {@linkplain Trainer.VocabManager#getFormattedCorrection(String) } formatted Text objects will get displayed.
     */
    @FXML
    private TextFlow textflow;
    /**
     * TextField in which the user can enter the question when adding a vocab.
     */
    @FXML
    private TextField questionEnterTextField;
    /**
     * TextField in which the user can enter the answer when adding a vocab.
     */
    @FXML
    private TextField answerEnterTextField;
    /**
     * ProgressBar which displays the error rate of the current vocab. See {@linkplain Vocab#getWrongRate()}.
     */
    @FXML
    private ProgressBar rate;
    /**
     * Label which displays the error rate percentage of the current vocab. See {@linkplain Vocab#getWrongRate()}.
     */
    @FXML
    private Label rateLabel;
    /**
     * CheckBox to determine whether or not to switch question and answers. Sets {@linkplain Trainer.VocabManager#askInverted} through {@linkplain #saveSettings(Event)}.
     */
    @FXML
    private CheckBox askInvertedCheckBox;
    /**
     * CheckBox to determine whether or not to check case sensitive. Sets {@linkplain VocabManager#askInverted} through {@linkplain #saveSettings(Event)}.
     */
    @FXML
    private CheckBox caseSensitiveCheckBox;
    /**
     * Slider to select how many tries the user should have on one vocab until the next one is selected. Sets {@linkplain VocabManager#maxTries} through {@linkplain #saveSettings(Event)}.
     */
    @FXML
    private Slider maxTriesSlider;
    /**
     * Slider to select how many right answers in a row the user should have to provide to reset one vocabs failure rate. Sets {@linkplain VocabManager#rightAmountToReset} through {@linkplain #saveSettings(Event)}.
     */
    @FXML
    private Slider rightToReset;
    /**
     * Tab in which the questioning is done. Is disabled if no vocabs are available (See {@linkplain VocabManager#isReady()}).
     */
    @FXML
    private Tab questioningTab;
    /**
     * An instance of {@linkplain VocabManager}.
     */
    private VocabManager vocabManager;

    /**
     * FXML-Linked to Button Click (confirming answer in questioning).
     * @param e ActionEvent bc of which this method is called
     */
    @FXML
    private void fertigClick(Event e){
        vocabManager.onFinishClick();
    }
    /**
     * FXML-Linked to Button Click (adding vocab).
     * @param e ActionEvent bc of which this method is called
     */
    @FXML
    private void addVokabel(Event e){
      vocabManager.onAddClick(questionEnterTextField.getText(), answerEnterTextField.getText());
        questioningTab.setDisable(!vocabManager.isReady());
    }
    /**
     * FXML-Linked to Button Click (import vocab).
     * @param e ActionEvent bc of which this method is called
     */
    @FXML
    private void importVok(Event e){
        try {
            vocabManager.onImportClick();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        questioningTab.setDisable(!vocabManager.isReady());
    }
    /**
     * FXML-Linked to Button Click (export vocab).
     * @param e ActionEvent bc of which this method is called
     */
    @FXML
    private void exportVok(Event e){
        try {
            vocabManager.onExportClick();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    /**
     * FXML-Linked to Button Click (clear vocab).
     * @param e ActionEvent bc of which this method is called
     */
    @FXML
    private void clear(Event e){

        vocabManager.onClearClick();
        questioningTab.setDisable(true);
    }
    /**
     * FXML-Linked to Button Click (apply settings).
     * @param e ActionEvent bc of which this method is called
     */
    @FXML
    private void saveSettings(Event e){
        vocabManager.updateSettings((int) maxTriesSlider.getValue(),askInvertedCheckBox.isSelected(),caseSensitiveCheckBox.isSelected(),(int) rightToReset.getValue());
    }
    /**
     * FXML-Linked to Button Click (start button).
     * @param e ActionEvent bc of which this method is called
     */
    @FXML
    private void startTraining(Event e){
            questioningTab.setDisable(!vocabManager.isReady());
    }

    /**
     * Updates TextFlow with formatted Text[] (clearing it and adding all elements of the array.
     * @param corrected Text[] to display in TextFlow
     */
    public void updateCorrectionText(@NotNull Text[] corrected){
        textflow.getChildren().clear();
        textflow.getChildren().add(new Text("                     "));
        textflow.getChildren().clear();
        for (Text t : corrected) {
            t.setFont(Font.font(20));
            textflow.getChildren().add(t);
        }
        textflow.getChildren().add(new Text(" "));
    }

    /**
     * Get method to provide {@linkplain VocabManager} with the answer provided by the user.
     * @return Text of {@linkplain #answerTextField}
     */
    public String getAntwortText(){
        return answerTextField.getText();
    }

    /**
     * Initializing method which also adds a EventListener to register Enter presses in {@linkplain #answerTextField} as confirmation.
     * @param vocabManager VocabManager which sets {@linkplain #vocabManager}
     */
    public void init(VocabManager vocabManager) {
        this.vocabManager = vocabManager;
        answerTextField.onKeyPressedProperty().set(event -> {
            if(event.getCode() == KeyCode.ENTER){
                    fertigClick(event);
            }
        });


    }

    /**
     * Update visual features and clears {@linkplain #answerTextField}.
     * @param current current Vocab
     * @param askInverted whether or not to invert question and answer
     */
    public void updateVisuals(@NotNull Vocab current, boolean askInverted) {
        if(askInverted){
            questionTextField.setText(current.getAnswer());}else{ questionTextField.setText(current.getQuestion());}
        int wrongQuote = current.getWrongRate();
        rate.setProgress(wrongQuote/100.0);
        rateLabel.setText(wrongQuote+"%");
        answerTextField.setText("");
        answerTextField.requestFocus();
    }

    /**
     * Colorizes {@linkplain #answerTextField}
     * @param col Color to use
     */
    public void colorTextField(@NotNull Color col){
        answerTextField.setStyle("-fx-base: #" + Integer.toHexString(col.hashCode())+" ;");
    }
}
