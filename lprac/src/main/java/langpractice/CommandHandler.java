package langpractice;

import java.util.ArrayList;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import langpractice.latin.LatinGame;
import reactor.core.publisher.Mono;

public final class CommandHandler {

	public static final ArrayList<User> openGameRequests = new ArrayList<>();

	private CommandHandler() {
		throw new UnsupportedOperationException();
	}

	public static Mono<Void> handle(Message prompt) {
		if (prompt.getContent().startsWith("lang "))
			return handle(prompt, prompt.getContent().substring(5).toLowerCase());
		else if (openGameRequests.contains(prompt.getAuthor().get()))
			return handleGameStarts(prompt);
		else if (Game.games.containsKey(prompt.getAuthor().get()))
			return handleGames(prompt);
		else
			return Mono.empty();
	}

	private static Mono<Void> handle(Message prompt, String command) {
		switch (command) {
		case "start":
			return startGameInitial(prompt);
		case "end":
		case "stop":
			return endGame(prompt);
		default:
			return noCommandFound(prompt);
		}
	}

	private static Mono<Void> handleGameStarts(Message prompt) {

		if (prompt.getContent().equalsIgnoreCase("sample") || prompt.getContent().equalsIgnoreCase("latin")) {

			openGameRequests.remove(prompt.getAuthor().get());
			Game.games.put(prompt.getAuthor().get(), makeGame(prompt.getContent().toLowerCase()));
			return composeDM(prompt.getAuthor().get(),
					"Game started!\n*" + Game.games.get(prompt.getAuthor().get()).getQuestion() + "*").then();

		} else if (prompt.getContent().equalsIgnoreCase("cancel")) {

			Game.games.remove(prompt.getAuthor().get());
			return composeDM(prompt.getAuthor().get(), "Canceled!").then();

		} else
			return Mono.empty();
	}

	private static Game makeGame(String name) {
		return switch (name) {
		case "sample" -> new SampleGame();
		case "latin" -> new LatinGame();
		default -> null;
		};
	}

	private static Mono<Void> handleGames(Message prompt) {

		Game game = Game.games.get(prompt.getAuthor().get());

		return switch (prompt.getContent()) {

		case "" -> Mono.empty();
		case "?" -> {
			game.fail();
			yield composeDM(prompt.getAuthor().get(), "*" + game.getQuestion() + "*").then();
		}
		default -> {
			if (game.answer(prompt.getContent()))
				yield composeDM(prompt.getAuthor().get(), "*" + game.getQuestion() + "*").then();
			else
				yield composeDM(prompt.getAuthor().get(),
						game.getStreakWrong() % 4 == 0 ? "Wrong!\n*" + game.getQuestion() + "*" : "Wrong!").then();

		}
		};
	}

	public static Mono<Void> startGameInitial(Message prompt) {
		if (prompt.getAuthor().map(openGameRequests::contains).get())
			return composeDM(prompt.getAuthor().get(), "I'm already waiting for a response!").then();
		else if (prompt.getAuthor().map(Game.games::containsKey).get())
			return composeDM(prompt.getAuthor().get(), "It seems you already have an active game!").then();
		else {
			openGameRequests.add(prompt.getAuthor().get());
			return composeDM(prompt.getAuthor().get(), "What game would you like to play?").then();
		}
	}

	public static Mono<Void> endGame(Message prompt) {
		if (prompt.getAuthor().map(Game.games::containsKey).get()) {
			Game.games.remove(prompt.getAuthor().get());
			return composeDM(prompt.getAuthor().get(), "Ended your game!").then();
		} else
			return composeDM(prompt.getAuthor().get(), "No active game to end!").then();
	}

	public static Mono<Void> noCommandFound(Message prompt) {
		return prompt.getChannel()
				.flatMap(channel -> channel
						.createMessage("This command doesn't exist! Type \"langbot help\" for a list of commands."))
				.then();
	}

	public static Mono<Message> composeDM(User user, String content) {
		return user.getPrivateChannel().flatMap(channel -> channel.createMessage(content));
	}

	public static Mono<Message> composeMessage(MessageChannel channel, String content) {
		return channel.createMessage(content);
	}
}
