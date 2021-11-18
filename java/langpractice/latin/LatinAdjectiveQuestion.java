package langpractice.latin;

import java.util.Random;

class LatinAdjectiveQuestion extends LatinQuestion {

	private static final Random random = new Random();

	private static String hic(String grammar) {
		switch (grammar) {
		case "AjSMN":
			return "hic";
		case "AjSFN":
			return "haec";
		case "AjSNN":
			return "hoc";

		case "AjSMG":
		case "AjSFG":
		case "AjSNG":
			return "huius";

		case "AjSMD":
		case "AjSFD":
		case "AjSND":
			return "huic";

		case "AjSMA":
			return "hunc";
		case "AjSFA":
			return "hanc";
		case "AjSNA":
			return "hoc";

		case "AjSMa":
			return "hoc";
		case "AjSFa":
			return "hac";
		case "AjSNa":
			return "hoc";

		case "AjPMN":
			return "hi";
		case "AjPFN":
			return "hae";
		case "AjPNN":
			return "haec";

		case "AjPMG":
			return "horum";
		case "AjPFG":
			return "harum";
		case "AjPNG":
			return "horum";

		case "AjPMD":
		case "AjPFD":
		case "AjPND":
			return "his";

		case "AjPMA":
			return "hos";
		case "AjPFA":
			return "has";
		case "AjPNA":
			return "haec";

		case "AjPMa":
		case "AjPFa":
		case "AjPNa":
			return "his";

		default:
			return null;
		}
	}

	private static String generateGrammar() {
		return "Aj" + (random.nextBoolean() ? "S" : "P") + switch (random.nextInt(3)) {
		case 0 -> "M";
		case 1 -> "F";
		case 2 -> "N";
		default -> null;
		} + switch (random.nextInt(5)) {
		case 0 -> "N";
		case 1 -> "G";
		case 2 -> "D";
		case 3 -> "A";
		case 4 -> "a";
		default -> null;
		};
	}

	LatinAdjectiveQuestion() {

		grammar = generateGrammar();

		switch (random.nextInt(1)) {
		case 0:
			word = hic(grammar);

			break;
		default:
			throw new Error();
		}
	}

	@Override
	public boolean isRight(String guess) {

		switch (grammar) {
		case "AjSMN":
		case "AjSFN":
		case "AjSNN":
			return guess.equals("this");

		case "AjSMG":
		case "AjSFG":
		case "AjSNG":
			return guess.equals("of this");

		case "AjSMD":
		case "AjSFD":
		case "AjSND":
			return guess.equals("by this") || guess.equals("for this");

		case "AjSMA":
		case "AjSFA":
		case "AjSNA":
			return guess.equals("this");

		case "AjSMa":
		case "AjSFa":
		case "AjSNa":
			return guess.equals("by this");

		case "AjPMN":
		case "AjPFN":
		case "AjPNN":
			return guess.equals("these");

		case "AjPMG":
		case "AjPFG":
		case "AjPNG":
			return guess.equals("of these");

		case "AjPMD":
		case "AjPFD":
		case "AjPND":
			return guess.equals("by these") || guess.equals("for these");

		case "AjPMA":
		case "AjPFA":
		case "AjPNA":
			return guess.equals("these");

		case "AjPMa":
		case "AjPFa":
		case "AjPNa":
			return guess.equals("by these");

		default:
			return false;

		}
	}
}