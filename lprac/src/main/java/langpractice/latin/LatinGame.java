package langpractice.latin;

import langpractice.Game;

public class LatinGame extends Game {

	private LatinQuestion question;
	
	public final String getQuestion() {
		return question.getWord();
	}
	
	@Override
	public void generate() {
		question = LatinQuestion.getQuestion();
	}

	@Override
	protected boolean isCorrect(String guess) {
		return question.isRight(guess);
	}
}
