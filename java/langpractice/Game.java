package langpractice;

import java.util.HashMap;

import discord4j.core.object.entity.User;

public abstract class Game {

	public static final HashMap<User, Game> games = new HashMap<>();

	private int score;

	private int streakright;
	private int streakwrong;

	public Game() {
		score = 0;
		streakright = 0;
		streakwrong = 0;
		generate();
	}

	public final int getScore() {
		return score;
	}

	public final int getStreakRight() {
		return streakright;
	}

	public final int getStreakWrong() {
		return streakwrong;
	}

	public abstract String getQuestion();

	public abstract void generate();

	protected abstract boolean isCorrect(String guess);

	public final boolean answer(String guess) {
		if (isCorrect(guess)) {
			generate();
			score++;
			streakright++;
			streakwrong = 0;
			return true;
		} else {
			streakright = 0;
			streakwrong++;
			return false;
		}
	}
	
	public final void fail() {
		generate();
		streakright = 0;
		streakwrong++;
	}
}
