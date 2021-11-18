package langpractice;

import java.util.Random;

public class SampleGame extends Game {

	private static final Random rand = new Random();

	private char question;

	@Override
	public final String getQuestion() {
		return Character.toString(question);
	}
	
	@Override
	public void generate() {
		question = (char) (rand.nextInt(26) + 'a');
	}

	@Override
	protected boolean isCorrect(String guess) {
		return guess.equals(getQuestion());
	}
}
