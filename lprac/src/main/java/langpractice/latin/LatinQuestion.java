package langpractice.latin;

public abstract class LatinQuestion {

	protected String word;
	protected String grammar;
	
	public static LatinQuestion getQuestion() {
		return new LatinPronounQuestion();
	}
	
	public final String getWord() {
		return word;
	}
	
	public abstract boolean isRight(String guess);
}
