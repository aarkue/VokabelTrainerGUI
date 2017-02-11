package DataStructure;

import GUI.Main;

/**
 * Vocab Class.
 */
public class Vocab {
    /**
     * String where the question is saved.
     */
    private String question;
    /**
     * String where the correct answer is saved.
     */
    private String answer;
    /**
     * int counter to count the amount of times the Vocab was answered correctly
     */
    private int amountRight;
    /**
     * int counter to count the amount of times the Vocab was answered incorrectly
     */
    private int amountWrong;
    /**
     * int counter to count the amount of times the Vocab was answered correctly in a row
     */
    private int rightInaRow;

    /**
     * Default Constructor.
     *
     * @param question sets {@linkplain #question}
     * @param answer   sets {@linkplain #answer}
     */
    public Vocab(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    /**
     * Alternative Constructor which also sets other fields. Useful for importing.
     *
     * @param question    sets {@linkplain #question}
     * @param answer      sets {@linkplain #answer}
     * @param amountRight sets {@linkplain #amountRight}
     * @param amountWrong sets {@linkplain #amountWrong}
     */
    public Vocab(String question, String answer, int amountRight, int amountWrong) {
        this.question = question;
        this.answer = answer;
        this.amountRight = amountRight;
        this.amountWrong = amountWrong;
    }

    /**
     * Getter for correct answer.
     *
     * @return correct answer ({@linkplain #answer})
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @param answer sets {@linkplain #answer}
     * @deprecated Not used anywhere. There should be no option to change existing Vocab.
     */
    @SuppressWarnings("unused")
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    /**
     * Getter for question.
     *
     * @return question ({@linkplain #question})
     */
    public String getQuestion() {
        return question;
    }
    /**
     * @deprecated Not used anywhere. There should be no option to change existing Vocab.
     * @param question sets {@linkplain #question}
     */
    @SuppressWarnings("unused")
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Getter for amount.
     *
     * @return question ({@linkplain #question})
     */
    public int getAmountRight() {
        return amountRight;
    }

    public int getAmountWrong() {
        return amountWrong;
    }

    /**
     * returns the percentage of wrong answers relative to all answers
     *
     * @return % of wrong answers
     */
    public int getWrongRate() {
        if (amountWrong < 1 && amountRight < 1) {
            return 100;
        }
        return amountWrong * 100 / (amountRight + amountWrong);
    }

    /**
     * Saves whether or not the Vocab was answered correctly.
     * @param right whether or not it was answered correctly
     * @param rightAmountToReset amount of right answers in a row to reset wrong
     * @return right (for row statements)
     */
    public boolean setAnswered(boolean right, int rightAmountToReset) {
        if (right) {

            rightInaRow++;
            if (rightInaRow >= rightAmountToReset) {
                Main.showInfo("Fehlerquote zurückgesetzt", "Diese Vocab wurde " + rightAmountToReset + " mal richtig beantwortet!");
                amountWrong = 0;
                rightInaRow = 0;
            } else {
                amountRight++;
            }
        } else {
            amountWrong++;
            rightInaRow = 0;
        }
        return right;
    }

}