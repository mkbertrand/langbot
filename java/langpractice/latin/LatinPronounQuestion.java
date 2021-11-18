package langpractice.latin;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class LatinPronounQuestion extends LatinQuestion {

	private static final Random random = new Random();

	private static final PronounEntry[] pronouns;

	static {

		ArrayList<String> pronounslist = new ArrayList<>();
		try {
			File file = new File(LatinPronounQuestion.class.getClassLoader().getResource("pronouns.txt").toURI());
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine())
				pronounslist.add(scanner.nextLine());
			scanner.close();
		} catch (FileNotFoundException | URISyntaxException e) {
			e.printStackTrace();
		}

		String[] pronounstrings = pronounslist.toArray(new String[pronounslist.size()]);

		pronouns = new PronounEntry[pronounstrings.length];

		for (int i = 0; i < pronouns.length; i++)
			pronouns[i] = new PronounEntry(pronounstrings[i]);
	}

	private static String generateGrammar() {
		return "Pr" + (random.nextBoolean() ? "S" : "P") + "-" + switch (random.nextInt(5)) {
		case 0 -> "N";
		case 1 -> "G";
		case 2 -> "D";
		case 3 -> "A";
		case 4 -> "a";
		default -> throw new IllegalArgumentException("Unexpected value: " + random.nextInt(5));
		};
	}

	private static String latinFromGrammar(int entryIndex, String grammar) {
		return pronouns[entryIndex].latin((grammar.charAt(2) == 'S' ? 0 : 5) + switch (grammar.charAt(4)) {
		case 'N' -> 0;
		case 'G' -> 1;
		case 'D' -> 2;
		case 'A' -> 3;
		case 'a' -> 4;
		default -> throw new IllegalArgumentException("Unexpected value: " + grammar.charAt(4));
		});
	}

	private static String[] englishFromLatin(String latin) {
		ArrayList<String> english = new ArrayList<>();
		for (PronounEntry p : pronouns)
			for (int i = 0; i < 10; i++)
				if (latin.equals(p.latin(i)))
					english.add(p.english(i));

		HashSet<String> english2 = new HashSet<>();

		english.forEach(str -> {
			for (String s : str.split("/"))
				english2.add(s);
		});

		return english2.toArray(new String[english2.size()]);
	}

	static String englishFromGrammar(int entryIndex, String grammar) {
		return pronouns[entryIndex].english((grammar.charAt(2) == 'S' ? 0 : 5) + switch (grammar.charAt(4)) {
		case 'N' -> 0;
		case 'G' -> 1;
		case 'D' -> 2;
		case 'A' -> 3;
		case 'a' -> 4;
		default -> throw new IllegalArgumentException("Unexpected value: " + grammar.charAt(4));
		});
	}

	static int ordinalIndexOf(String str, char substr, int n) {
		int pos = str.indexOf(substr);
		while (--n > 0 && pos != -1)
			pos = str.indexOf(substr, pos + 1);
		return pos;
	}

	private static final class PronounEntry {

		private String meta;
		private String[] latin = new String[10];
		private String[] english = new String[10];

		PronounEntry(String entry) {
			meta = entry.substring(0, 4);
			entry = entry.replace('=', ',');
			int in = -1;
			for (int i = 0; i < 10; i++)
				latin[i] = entry.substring((in = entry.indexOf(',', in + 1)) + 1, entry.indexOf(',', in + 1));

			for (int i = 0; i < 10; i++)
				english[i] = entry.substring((in = entry.indexOf(',', in + 1)) + 1, (entry + ',').indexOf(',', in + 1));
		}

		String latin(int i) {
			return latin[i];
		}

		String english(int i) {
			return english[i];
		}
	}

	private final int entryIndex;
	private final String[] answers;

	public LatinPronounQuestion() {
		entryIndex = random.nextInt(pronouns.length);
		grammar = generateGrammar().replace('-',
				pronouns[entryIndex].meta.charAt(3) == 'A' ? 'M' : pronouns[entryIndex].meta.charAt(3));
		word = latinFromGrammar(entryIndex, grammar);
		answers = englishFromLatin(word);
	}

	@Override
	public boolean isRight(String guess) {
		guess = guess.replace("for ", "to ");
		for (String s : answers)
			if (s.equals(guess))
				return true;
		return false;
	}

}
