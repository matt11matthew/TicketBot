package me.matthewe.ticket.discord;

import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.config.Config;
import me.matthewe.ticket.discord.ticket.Ticket;
import me.matthewe.ticket.discord.ticket.TicketStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew E on 12/10/2023 at 2:51 PM for the project TicketBot
 */
public class DiscordListener  extends ListenerAdapter {
    private Map<Long, Ticket> ticketMap = new HashMap<>();
    private DiscordHandler discordHandler;
    private TicketBot ticketBot;
    private Config config;


    public DiscordListener(DiscordHandler discordHandler, TicketBot ticketBot, Config config) {
        this.discordHandler = discordHandler;
        this.ticketBot = ticketBot;
        this.config = config;
    }

    public Ticket getTicketByChannelId(long id) {
        for (Ticket value : ticketMap.values()) {
            if (value.getChannelId() == id) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void onReady(ReadyEvent event) {
        this.discordHandler.setReady(true);


    }

    public void onShutdown() {
        for (Ticket value : ticketMap.values()) {
            TextChannel textChannel = value.getTextChannel();

            if (textChannel != null) {
                textChannel.delete().queue();
            }

        }

    }

    private void createTicketChannel(Guild guild, Member member) {
        ChannelAction<TextChannel> ticketChannel = guild.createTextChannel("ticket-" + member.getUser().getName(), guild.getCategoryById(this.config.discord.channels.ticketCategory));
        TextChannel complete = ticketChannel.clearPermissionOverrides().complete();

        Ticket ticket = new Ticket(ticketMap.size() + 1, member.getIdLong(), complete.getIdLong(), System.currentTimeMillis(), TicketStatus.OPENED);

        ticketMap.put(ticket.getId(), ticket);


        StringSelectMenu.Builder builder = StringSelectMenu.create("menu:" + complete.getIdLong())
                .setPlaceholder("Choose Department");
        for (String department : config.discord.departments) {
            builder.addOption(department, department);
        }
        SelectMenu menu = builder
                .build();

        complete.sendMessageEmbeds(new EmbedBuilder().setDescription("What would you like done?").build())
                .addActionRow(menu)
                .queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        for (Ticket value : ticketMap.values()) {
            if (event.getUser().isBot())continue;
            if (value.getChannelId() == event.getChannel().getIdLong()) {
                if (event.getComponentId().equalsIgnoreCase("menu:" + event.getChannel().getIdLong())) {
                    String reply = "";
                    for (SelectOption selectedOption : event.getSelectedOptions()) {
                        reply += selectedOption.getValue() + "\n";
                    }
                    event.getMessage().delete().queue();
                    event.reply("You've selected " + reply).queue();

                    value.setDepartment(reply);
                    event.getChannel().sendMessageEmbeds(this.config.discord.messages.set_description.toEmbedBuilder().build()).queue();

                    value.setWaitingDescription(true);
                    return;
                }

            }
        }


    }

    public Map<Long, Ticket> getTicketMap() {
        return ticketMap;
    }

    public void setTicketMap(Map<Long, Ticket> ticketMap) {
        this.ticketMap = ticketMap;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Ticket ticketByChannelId = getTicketByChannelId(event.getChannel().getIdLong());
        if (ticketByChannelId != null && ticketByChannelId.isAwaitingDescription() && event.getMember().getUser().getIdLong() == ticketByChannelId.getClientId()) {

            String msg =event.getMessage().getContentRaw();
            ticketByChannelId.setWaitingDescription(false);
            ticketByChannelId.setDescription(msg);
            event.getChannel().getHistory().retrievePast(5).queue(messages -> messages.forEach(message -> message.delete().queue(unused -> {
               if (!ticketByChannelId.isPosted()){

                   event.getChannel().sendMessageEmbeds(config.discord.messages.posted.toEmbedBuilder(s -> new String(s).replaceAll("%description%", event.getMessage().getContentDisplay()).replaceAll("%department%", ticketByChannelId.getDepartment())).build()).queue();
                    ticketByChannelId.setPosted(true);
               }

            })));
            //TODO POST TICKET

        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("ticket")) {
            createTicketChannel(event.getGuild(), event.getMember());
            return;

        }
        if (event.getName().equals("purge")) {
            for (TextChannel textChannel : event.getGuild().getTextChannels()) {
                if (textChannel.getName().startsWith("ticket-")){
                    textChannel.delete().queue();
                }
            }
            ticketMap.clear();
            deleteAllTicketsFromDatabase();
            return;

        }
        if (event.getName().equals("close")) {
            Ticket ticketByChannelId = getTicketByChannelId(event.getChannel().getIdLong());
            if (ticketByChannelId!=null){
                System.out.println(event.getChannel().getHistory());
                event.getChannel().delete().queue();

                ticketMap.remove(ticketByChannelId.getId());
            }


        }
    }

    private void deleteAllTicketsFromDatabase() {
        //TODO
    }
}
