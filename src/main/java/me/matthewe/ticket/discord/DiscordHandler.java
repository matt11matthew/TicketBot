package me.matthewe.ticket.discord;

import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.config.Config;
import me.matthewe.ticket.database.DatabaseHandler;
import me.matthewe.ticket.handler.Handler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * Created by Matthew E on 12/10/2023 at 2:46 PM for the project TicketBot
 */
public class DiscordHandler extends Handler {

    private boolean ready;
    private DiscordListener discordListener;
    private Guild guild;
    private JDA jda;
    public DiscordHandler(TicketBot ticketBot, Config config) {
        super(ticketBot, config);
        this.shutdownPriority = 10; //Ensures database starts first but ends last.
    }

    public Guild getGuild() {
        return guild;
    }

    public DatabaseHandler getDatabaseHandler() {
        return ticketBot.getDatabaseHandler();
    }
    @Override
    public void onEnable() {
        System.out.println(this.config.discord.auth.token);
        this.jda = JDABuilder.createDefault(this.config.discord.auth.token).enableIntents(GatewayIntent.MESSAGE_CONTENT).build();
        this.discordListener = new DiscordListener(this, this.ticketBot, this.config);

        this.jda.addEventListener(discordListener);
        this.discordListener.downloadTicketsFromDatabase();
    }

    public void onReady() {
        this.guild =  this.jda.getGuildById(this.config.discord.auth.guildId);
        this.guild.updateCommands().addCommands(
                Commands.slash("ticket", "Creates ticket"),
                Commands.slash("purge", "Purges tickets"),
                Commands.slash("close", "Closes ticket"))
                .queue();

        TextChannel channel = this.guild.getTextChannelById(this.config.discord.channels.info);
        channel.sendMessageEmbeds(this.config.discord.messages.ready.toEmbedBuilder().build()).queue();
        System.out.println(channel); //DEBUG MESSAGE
        this.discordListener.purgeDeadTickets(); //Deletes channels if ticket nolonger exists

    }
    public JDA getJda() {
        return jda;
    }

    @Override
    public void onDisable() {
        this.discordListener.uploadAllTicketsToDatabase();

    }

    public void setReady(boolean ready) {
        if (!this.ready && ready) {
            this.ticketBot.getLogger().info("[DiscordHandler] bot now ready!");
            onReady();
        }
        this.ready = ready;
    }

    public void handleShutdownFast() {
        this.discordListener.onShutdown();
    }
}
