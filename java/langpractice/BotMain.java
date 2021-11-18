package langpractice;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import langpractice.latin.LatinGame;
import langpractice.latin.LatinPronounQuestion;
import reactor.core.publisher.Mono;

public class BotMain {

	public static void main(String[] args) {

		new LatinPronounQuestion();

		DiscordClient client = DiscordClient.create("");

		ApplicationCommandRequest startGameRequest = ApplicationCommandRequest.builder().name("start")
				.description("Starts a new language game")
				.addOption(ApplicationCommandOptionData.builder().name("game").description("Game")
						.type(ApplicationCommandOption.Type.STRING.getValue()).required(true).build())
				.build();

		ApplicationCommandRequest endGameRequest = ApplicationCommandRequest.builder().name("end")
				.description("Ends your current game").build();

		Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {

			Mono<Long> applicationId = client.getApplicationId();

			// Login handling
			Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
				final User self = event.getSelf();
				System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
			})).then();

			Mono<Void> startGame = applicationId
					.flatMap(id -> client.getApplicationService().createGlobalApplicationCommand(id, startGameRequest))
					.then();

			Mono<Void> endGame = applicationId
					.flatMap(id -> client.getApplicationService().createGlobalApplicationCommand(id, endGameRequest))
					.then();

			Mono<Void> commands = gateway.on(ChatInputInteractionEvent.class, event -> {
				System.out.println(event.getCommandName());
				if (event.getCommandName().equals("start"))
					return event.deferReply().then(start(event));
				else if (event.getCommandName().equals("end"))
					return event.deferReply().then(end(event));
				else
					return Mono.empty();
			}).then();

			// All messages
			Mono<Void> messages = gateway.on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
					.filter(msg -> msg.getAuthor().map(Game.games::containsKey).get()).flatMap(message -> {

						User user = message.getAuthor().get();
						Game game = Game.games.get(user);

						return switch (message.getContent()) {

						case "?" -> {
							game.fail();
							yield user.getPrivateChannel()
									.flatMap(channel -> channel.createMessage("*" + game.getQuestion() + "*"));
						}
						default -> {
							if (game.answer(message.getContent()))
								yield user.getPrivateChannel()
										.flatMap(channel -> channel.createMessage("*" + game.getQuestion() + "*"));
							else
								yield user.getPrivateChannel()
										.flatMap(channel -> channel.createMessage(
												game.getStreakWrong() % 4 == 0 ? "Wrong!\n*" + game.getQuestion() + "*"
														: "Wrong!"));
						}
						};

					}).then();

			return printOnLogin.and(messages).and(startGame).and(endGame).and(commands);
		});

		login.block();
	}

	private static Mono<Void> start(ChatInputInteractionEvent event) {

		User user = event.getInteraction().getUser();

		if (Game.games.containsKey(user))
			return event.createFollowup("It seems you already have an active game!").then();

		Game game = switch (event.getOption("game").flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString).get()) {
		case "sample" -> new SampleGame();
		case "latin" -> new LatinGame();
		default -> null;
		};

		if (game == null)
			return event.createFollowup("This game does not exist! Type /help to get a list of games.").then();
		else {
			Game.games.put(user, game);
			return event.createFollowup("Game started!").and(
					user.getPrivateChannel().flatMap(channel -> channel.createMessage("*" + game.getQuestion() + "*")));
		}
	}

	private static Mono<Void> end(ChatInputInteractionEvent event) {
		return event
				.createFollowup(Game.games.remove(event.getInteraction().getUser()) == null ? "No active game to end!"
						: "Ended your game!")
				.then();
	}
}
