package langpractice;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.PrivateChannel;
import reactor.core.publisher.Mono;

public final class Transformations {

	private Transformations() {
		throw new UnsupportedOperationException();
	}
	
	public static Mono<PrivateChannel> privateChannel(Message message) {
		return message.getAuthor().map(User::getPrivateChannel).get();
	}
}
