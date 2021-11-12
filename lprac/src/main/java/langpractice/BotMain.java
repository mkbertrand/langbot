package langpractice;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import langpractice.latin.LatinPronounQuestion;
import reactor.core.publisher.Mono;

public class BotMain {

	public static void main(String[] args) {

		new LatinPronounQuestion();

		DiscordClient client = DiscordClient.create("");

		Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {

			// Login handling
			Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event -> Mono.fromRunnable(() -> {
				final User self = event.getSelf();
				System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
			})).then();

			// All messages
			Mono<Void> messages = gateway.on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
					.flatMap(CommandHandler::handle).then();

			return printOnLogin.and(messages);
		});

		login.block();
	}
}
