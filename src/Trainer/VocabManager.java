package Trainer;

import DataStructure.PriorityQueue;
import DataStructure.Vocab;
import GUI.Controller;
import GUI.Main;
import javafx.event.Event;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * Created by aarim on 17.11.2016.
 */
@SuppressWarnings("DefaultFileTemplate")
public class VocabManager {
    /**
     * An instance of the {@linkplain GUI.Controller JavaFX Controller}.
     */
    private final Controller controller;
    /**
     * An instance of the main Stage (needed for File Dialogs).
     */
    private final Stage main;
    /**
     * An instance of an PriorityQueue containing Vocab. The Priority is determined by the failure-rate of the vocab.
     */
    private final PriorityQueue<Vocab> vokabelliste = new PriorityQueue<>();
    /**
     * An instance of the currently selected vocab. Should not be used in methods as it may be null. Use the getter {@linkplain #getCurrent() getCurrent()} instead as it does not return null.
     */
    @Nullable
    private Vocab current;
    /**
     * Determines whether or not to check case sensitive when comparing the input answer.
     * Controlled by the user through a {@linkplain GUI.Controller#caseSensitiveCheckBox CheckBox} and set by {@linkplain GUI.Controller#saveSettings(Event) this method}.
     */
    private boolean caseSense = false;
    /**
     * Determines the amount of correct answers are needed to reset a vocabs failure-rate.
     * Controlled by the user through a {@linkplain GUI.Controller#rightToReset Slider} and set by {@linkplain GUI.Controller#saveSettings(Event) this method}.
     */
    private int rightAmountToReset = 3;
    /**
     * Determines if answer and question of vocab should be switched.
     * Controlled by the user through a {@linkplain GUI.Controller#askInvertedCheckBox Slider} and set by {@linkplain GUI.Controller#saveSettings(Event) this method}.
     */
    private boolean askInverted = false;
    /**
     * Determines after how many wrong answers the next vocab should get selected.
     * Controlled by the user through a {@linkplain GUI.Controller#maxTriesSlider Slider} and set by {@linkplain GUI.Controller#saveSettings(Event) this method}.
     */
    private int maxTries = 3;
    /**
     * An int-counter which saves the amount of wrong answers on the current selected vocab.
     */
    private int triesOnVoc = 0;

    /**
     * Constructor of VocabManager. Called by the start {@linkplain Main#start(Stage) method} in Main.
     * @param controller The JavaFX Controller. Sets {@linkplain #controller this field}
     * @param main The main JavaFX Stage. Sets {@linkplain #main this field}
     */
    public VocabManager(Controller controller, Stage main) {
        this.controller = controller;
        this.main = main;
        if (vokabelliste.isEmpty()) {
            Main.showInfo("Keine Vokabeln!", "Vokabelliste leer!");
        }
    }

    /**
     * Gets called by {@linkplain GUI.Controller#fertigClick(Event) this Method}. Updates visual features in the {@linkplain GUI.Controller JavaFX Controller} and logs the answer on the current vocab.
     */
    public void onFinishClick() {
        controller.updateCorrectionText(getFormattedCorrection(controller.getAntwortText()));

        if (getCurrent().setAnswered(isRight(), rightAmountToReset)) {
            controller.colorTextField(Color.rgb(142, 186, 67, 1));
            Main.showInfo("Richtig!", "Sehr gut!");
            nextVocab();
        } else {
            controller.colorTextField(Color.ORANGERED);
            Main.showInfo("Falsch!", "Leider falsch!");
            if (triesOnVoc < maxTries) {
                triesOnVoc++;
                controller.updateVisuals(getCurrent(), askInverted);
            } else {
                nextVocab();
                triesOnVoc = 0;
            }
        }

    }

    /**
     * Selects the next vocab. Always takes the front of the {@linkplain #vokabelliste PriorityQueue} unless the front is the current vocab. In that case it takes the second vocab from the front.
     */
    private void nextVocab() {

        if (vokabelliste.front() == getCurrent()) {
            Vocab temp = vokabelliste.front();
            vokabelliste.del();
            current = vokabelliste.front();

            vokabelliste.add(temp, temp != null ? temp.getWrongRate() : 0);
        } else {
            current = vokabelliste.front();
        }
        controller.colorTextField(Color.STEELBLUE);
        controller.updateVisuals(getCurrent(), askInverted);
    }

    /**
     * Gets called by {@linkplain GUI.Controller#addVokabel(Event) this Method }. Adds a vocab to the {@linkplain #vokabelliste PriorityQueue}.
     * @param question The question of the vocab. Will be set to {@linkplain DataStructure.Vocab#question this field} in the new vocab.
     * @param answer The answer of the vocab. Will be set to {@linkplain DataStructure.Vocab#answer this field} in the new vocab..
     */
    public void onAddClick(String question, String answer) {
        vokabelliste.add(new Vocab(question, answer), 100);
    }

    /**
     * Gets called by {@linkplain GUI.Controller#importVok(Event) this Method}. Opens a File Opening Dialog and calls {@linkplain #importVocab(File) this method} with the chosen file.
     * @throws IOException Exception while reading selected file. Is thrown when file is null.
     */
    public void onImportClick() throws IOException {
        importVocab(new FileChooser().showOpenDialog(main));
    }

    /**
     * Gets called by {@linkplain GUI.Controller#exportVok(Event) this Method}. Opens a File Saving Dialog and calls {@linkplain #exportVocab(File) this method} with the chosen file.
     * @throws IOException Exception while saving file. Is thrown when file is null.
     */
    public void onExportClick() throws IOException {
        exportVocab(new FileChooser().showSaveDialog(main));
    }

    /**
     * Gets called by {@linkplain GUI.Controller#clear(Event) this Method}. Deletes every element of the {@linkplain #vokabelliste PriorityQueue }.
     */
    public void onClearClick() {
        while (!vokabelliste.isEmpty()) {
            vokabelliste.del();
        }
    }

    /**
     * Parses String read from file and adds parsed vocabs to the {@linkplain #vokabelliste PriorityQueue }.
     * @param saveFile File that gets read to String. Should contain only one line with the following format:
     *                 #$question;$answer;$amountAnsweredRight$amountAnsweredWrong;$priority;#         replace $... with the fields from {@linkplain DataStructure.Vocab Vocab}
     * @throws IOException Exception while reading file. Is thrown when file is null
     */
    private void importVocab(@NotNull File saveFile) throws IOException {
        BufferedReader buf = new BufferedReader(new FileReader(saveFile));
        char[] allInAll = buf.readLine().toCharArray();
        int index = 0;

        while (allInAll.length > index) {
            String[] data = new String[5];
            for (int i = 0; i < data.length; i++) {
                data[i] = "";
            }
            int dataIndex = 0;
            while (allInAll[index] != '#') {


                while (allInAll[index] != ';' && data.length > dataIndex) {
                    data[dataIndex] = data[dataIndex] + allInAll[index++];
                }
                dataIndex++;
                index++;
            }
            index++;
            vokabelliste.add(new Vocab(data[0], data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3])), Integer.parseInt(data[4]));


        }
    }

    /**
     * Formats all vocabs in the {@linkplain #vokabelliste PriorityQueue } to a String.
     * Parses to the following format:
     *        #$question;$answer;$amountAnsweredRight$amountAnsweredWrong;$priority;#
     *        replace $... with the fields from {@linkplain DataStructure.Vocab Vocab}
     * @return the formatted String containing data from all Vocabs
     */
    @NotNull
    private String getAllVocabAsString() {
        String allTogether = "";
        while (!vokabelliste.isEmpty()) {
            Vocab i = vokabelliste.front();
            int prio = vokabelliste.getFrontPrio();
            if (i != null) {
                allTogether = allTogether + i.getQuestion() + ";" + i.getAnswer() + ";" + i.getAmountRight() + ";" + i.getAmountWrong() + ";" + prio + ";" + "#";
            }
            vokabelliste.del();
        }
        return allTogether;

    }

    /**
     * Appends a file with the String of all vocab returned by {@linkplain #getAllVocabAsString() this method}.
     * @param savefile The file to append
     * @throws IOException Exception while writing to the file. Is thrown when the file is null
     */
    private void exportVocab(@NotNull File savefile) throws IOException {
        PrintWriter out = new PrintWriter(savefile);
        out.append(getAllVocabAsString());
        out.close();
    }

    /**
     * Checks if the current vocab is rightfully answered. Compensates for {@linkplain #caseSense case sensivity}.
     * @return if the vocab should be considered rightfully answered
     */
    private boolean isRight() {
        String correctAnswer = getCurrent().getAnswer();
        if (askInverted) {
            correctAnswer = getCurrent().getQuestion();
        }
        if (caseSense) {
            return correctAnswer.equals(controller.getAntwortText());
        } else {
            return correctAnswer.equalsIgnoreCase(controller.getAntwortText());
        }
    }

    /**
     * Getter method which provides a vocab instance which is never null (unlike the {@linkplain #current current field}).
     * @return instance of vocab: Normally {@linkplain #current current} if it is not null
     */
    @NotNull
    private Vocab getCurrent() {
        if (current != null) {
            return current;
        } else {
            if (vokabelliste.isEmpty()) {
                return new Vocab("Keine Vokabeln in der Liste", "Keine Vokabeln in der Liste");
            } else {
                //noinspection ConstantConditions
                return current = vokabelliste.front();
            }

        }
    }

    /**
     * Getter method which generates a formatted Text[] which contains the provided answer as char[] colored red or green dependent whether or not the char matches with the correct answer.
     * @param answerProvided the input which should be compared to the correct answer
     * @return Text[] with Text all containing only one red/green colored char
     */
    @NotNull
    private Text[] getFormattedCorrection(@NotNull String answerProvided) {


        String realAnswer;
        if (askInverted) {
            realAnswer = this.getCurrent().getQuestion();
        } else {
            realAnswer = this.getCurrent().getAnswer();
        }
        if (!caseSense) {
            realAnswer = realAnswer.toLowerCase();
            answerProvided = answerProvided.toLowerCase();
        }
        char[] answerProvidedChars = answerProvided.toCharArray();
        char[] answerChar = realAnswer.toCharArray();
        Text[] markedText = new Text[answerProvidedChars.length];
        for (int i = 0; i < answerChar.length && i < answerProvidedChars.length; i++) {
            Paint textColor;
            if (answerChar[i] == answerProvidedChars[i]) {
                textColor = Color.GREEN;

            } else {
                textColor = Color.RED;
            }
            Text text = new Text(answerProvidedChars[i] + "");
            text.setFill(textColor);

            markedText[i] = text;
        }
        if (answerChar.length < answerProvidedChars.length) {
            for (int i = answerChar.length; i < answerProvidedChars.length; i++) {
                Text text = new Text(answerProvidedChars[i] + "");
                text.setFill(Color.RED);
                markedText[i] = text;
            }
        }
        return markedText;
    }

    /**
     * Gets called by {@linkplain GUI.Controller#saveSettings(Event) this method} and applies settings by changing fields. Also invokes {@linkplain #nextVocab() nextVocab()} to compensate for changes.
     * @param maxTries new Value of {@linkplain #maxTries}
     * @param askInverted new Value of {@linkplain #askInverted}
     * @param caseSense new Value of {@linkplain #caseSense}
     * @param rightAmountToReset new Value of {@linkplain #rightAmountToReset}
     */
    public void updateSettings(int maxTries, boolean askInverted, boolean caseSense, int rightAmountToReset) {
        this.maxTries = maxTries;
        this.askInverted = askInverted;
        this.caseSense = caseSense;
        this.rightAmountToReset = rightAmountToReset;
        nextVocab();
    }

    /**
     * Is called to make sure a vocab is available and is ready.
     * @return whether or not questioning is ready to start
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isReady() {
        if ((vokabelliste.isEmpty() || vokabelliste.front() == null)) {
            Main.showInfo("Keine Vokabeln!", "Vokabelliste leer!");
            return false;
        } else {
            nextVocab();
            controller.updateVisuals(getCurrent(), askInverted);
            return true;
        }
    }
}

