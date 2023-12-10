package me.matthewe.ticket.discord;

import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.config.Config;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Created by Matthew E on 12/10/2023 at 2:51 PM for the project TicketBot
 */
public class DiscordListener  extends ListenerAdapter  {
    private DiscordHandler discordHandler;
    private TicketBot ticketBot;
    private Config config;


    public DiscordListener(DiscordHandler discordHandler, TicketBot ticketBot, Config config) {
        this.discordHandler = discordHandler;
        this.ticketBot = ticketBot;
        this.config = config;
    }

    @Override
    public void onReady(ReadyEvent event) {
        this.discordHandler.setReady(true);
    }
}
