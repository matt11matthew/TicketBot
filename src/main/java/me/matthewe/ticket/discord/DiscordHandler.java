package me.matthewe.ticket.discord;

import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.config.Config;
import me.matthewe.ticket.handler.Handler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Created by Matthew E on 12/10/2023 at 2:46 PM for the project TicketBot
 */
public class DiscordHandler extends Handler {

    private boolean ready;
    public DiscordHandler(TicketBot ticketBot, Config config) {
        super(ticketBot, config);
    }

    @Override
    public void onEnable() {
        System.out.println(this.config.discord.auth.token);
        JDA jda = JDABuilder.createDefault(this.config.discord.auth.token).build();
        jda.addEventListener(new DiscordListener(this, this.ticketBot, this.config));

    }

    @Override
    public void onDisable() {

    }


    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        if (!this.ready&&ready) {
            this.ticketBot.getLogger().info("[DiscordHandler] bot now ready!");
        }
        this.ready = ready;
    }
}
