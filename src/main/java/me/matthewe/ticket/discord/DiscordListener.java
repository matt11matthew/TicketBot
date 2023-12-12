package me.matthewe.ticket.discord;

import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.config.Config;
import me.matthewe.ticket.discord.ticket.Ticket;
import me.matthewe.ticket.discord.ticket.TicketStatus;
import me.matthewe.ticket.io.utilities.FileUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
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
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by Matthew E on 12/10/2023 at 2:51 PM for the project TicketBot
 */
public class DiscordListener  extends ListenerAdapter {
    private Map<UUID, Ticket> ticketMap = new HashMap<>();
    private DiscordHandler discordHandler;
    private TicketBot ticketBot;
    private Config config;
    private Category category;

//    private long currentId;


    public DiscordListener(DiscordHandler discordHandler, TicketBot ticketBot, Config config) {
        this.discordHandler = discordHandler;
        this.ticketBot = ticketBot;
        this.config = config;

//        String tokenPath = "data.txt";
//        if (TicketBot.DEBUG) {
//            tokenPath = "C:\\Users\\Matthew Eisenberg\\eclipse-workspace\\TicketBot\\src\\main\\resources\\data.txt";
//        }
//        this.currentId = Long.parseLong(FileUtils.readFileToString(new File(tokenPath)));
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
        category = event.getJDA().getCategoryById(this.config.discord.channels.ticketCategory);


    }

    public void onShutdown() {
//        String tokenPath = "data.txt";
//        if (TicketBot.DEBUG) {
//            tokenPath = "C:\\Users\\Matthew Eisenberg\\eclipse-workspace\\TicketBot\\src\\main\\resources\\data.txt";
//        }
//        FileUtils.writeStringToFile(new File(tokenPath), String.valueOf(currentId));

    }

    private void createTicketChannel(Guild guild, Member member) {
        ChannelAction<TextChannel> ticketChannel = guild.createTextChannel("ticket-" + member.getUser().getName(), guild.getCategoryById(this.config.discord.channels.ticketCategory));
        TextChannel complete = ticketChannel.clearPermissionOverrides().complete();

//        Ticket ticket = new Ticket(ticketMap.size() + 1, member.getIdLong(), complete.getIdLong(), System.currentTimeMillis(), TicketStatus.OPENED);
//        currentId++;
        Ticket ticket = new Ticket(UUID.randomUUID(), member.getIdLong(), complete.getIdLong(), System.currentTimeMillis(), TicketStatus.OPENED, null, complete, false, false, null, -1);
//        Ticket ticket = new Ticket(ticketMap.size() + 1, member.getIdLong(), complete.getIdLong(), System.currentTimeMillis(), TicketStatus.OPENED);


        createTicketDatabase(ticket);

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
            if (event.getUser().isBot()) continue;
            if (value.getChannelId() == event.getChannel().getIdLong()) {
                if (event.getComponentId().equalsIgnoreCase("menu:" + event.getChannel().getIdLong())) {
                    String reply = "";
                    for (SelectOption selectedOption : event.getSelectedOptions()) {
                        reply += selectedOption.getValue() + "\n";
                    }
                    event.getMessage().delete().queue();
                    event.reply("You've selected " + reply).queue(interactionHook -> {
                        event.getChannel().sendMessageEmbeds(this.config.discord.messages.set_description.toEmbedBuilder().build()).queue();

                    });

                    value.setDepartment(reply);

                    value.setWaitingDescription(true);
                    return;
                }

            }
        }


    }

    public Map<UUID, Ticket> getTicketMap() {
        return ticketMap;
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMember().getUser().isBot())return;
        Ticket ticketByChannelId = getTicketByChannelId(event.getChannel().getIdLong());
        if (ticketByChannelId != null && ticketByChannelId.isAwaitingDescription() && event.getMember().getUser().getIdLong() == ticketByChannelId.getClientId()) {

            String msg = event.getMessage().getContentStripped();

            ticketByChannelId.setWaitingDescription(false);
            ticketByChannelId.setDescription(msg);


            if (!ticketByChannelId.isPosted()) {

                event.getChannel().sendMessageEmbeds(config.discord.messages.posted.toEmbedBuilder(s -> new String(s).replaceAll("%description%", event.getMessage().getContentDisplay()).replaceAll("%department%", ticketByChannelId.getDepartment())).build()).queue();
                ticketByChannelId.setPosted(true);
                updateTicketInDatabase(ticketByChannelId);
                postTicket(ticketByChannelId);
            }

//            event.getChannel().getHistory().retrievePast(5).queue(messages -> messages.forEach(message -> message.delete().queue(unused -> {
//                if (!ticketByChannelId.isPosted()) {
//
//                    event.getChannel().sendMessageEmbeds(config.discord.messages.posted.toEmbedBuilder(s -> new String(s).replaceAll("%description%", event.getMessage().getContentDisplay()).replaceAll("%department%", ticketByChannelId.getDepartment())).build()).queue();
//                    ticketByChannelId.setPosted(true);
//                    updateTicketInDatabase(ticketByChannelId);
//                }
//
//            })));
            //TODO POST TICKET

        }
    }

    private void postTicket(Ticket ticketByChannelId) {

        TextChannel textChannel = discordHandler.getGuild().getTextChannelById(config.discord.channels.commissions);
        textChannelById.sendMessage(ticketByChannelId.toString()).queue();
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("ticket")) {
            createTicketChannel(event.getGuild(), event.getMember());
            event.replyEmbeds(config.discord.messages.created.toEmbedBuilder().build()).queue();
            return;

        }
        if (event.getName().equals("purge")) {
            for (TextChannel textChannel : event.getGuild().getTextChannels()) {
                if (textChannel.getName().startsWith("ticket-")) {
                    textChannel.delete().queue();
                }
            }
            ticketMap.clear();
            deleteAllTicketsFromDatabase();
            event.replyEmbeds(config.discord.messages.purged.toEmbedBuilder().build()).queue();
            return;

        }
        if (event.getName().equals("close")) {
            Ticket ticketByChannelId = getTicketByChannelId(event.getChannel().getIdLong());
            if (ticketByChannelId != null) {
                System.out.println(event.getChannel().getHistory());
                event.getChannel().delete().queue();

                ticketMap.remove(ticketByChannelId.getId());
                deleteTicketFromDatabase(ticketByChannelId);
            }


        }
    }

    public void deleteTicketFromDatabase(Ticket ticket) {
        this.ticketBot.getDatabaseHandler().deleteTicketFromDatabase(ticket);

    }

    public void updateTicketInDatabase(Ticket ticket) {
        this.ticketBot.getDatabaseHandler().updateTicketInDatabase(ticket);
    }

    public void deleteAllTicketsFromDatabase() {
        this.ticketBot.getDatabaseHandler().deleteAllTicketsFromDatabase();
    }

    public void downloadTicketsFromDatabase() {
        //TODO
        this.ticketMap = new HashMap<>();
        List<Ticket> tickets = this.ticketBot.getDatabaseHandler().downloadTicketsFromDatabase();
        for (Ticket ticket : tickets) {
            ticketBot.getLogger().info("[DiscordListener] [DatabaseHandler] Downloaded ticket " + ticket.getId());
            this.ticketMap.put(ticket.getId(), ticket);
        }
    }

    public void uploadAllTicketsToDatabase() {
        List<Ticket> tickets = new ArrayList<>();
        tickets.addAll(ticketMap.values());
        this.ticketBot.getDatabaseHandler().uploadAllTicketsToDatabase(tickets);
    }

    public void createTicketDatabase(Ticket ticket) {
        this.ticketBot.getDatabaseHandler().createTicketDatabase(ticket);
    }

    public void purgeDeadTickets() {
        List<TextChannel> toPurgeList = new ArrayList<>();
        for (Guild guild : discordHandler.getJda().getGuilds()) {
            if (guild.getIdLong()==config.discord.auth.guildId){
                for (TextChannel textChannel : guild.getTextChannels()) {
                    if (textChannel.getName().startsWith("ticket-")) {
                        if (getTicketByChannelId(textChannel.getIdLong()) == null) {
                            toPurgeList.add(textChannel);
                        }
                    }
                }
            }
        }
        ticketBot.getLogger().info("Purging dead tickets");
        for (TextChannel textChannel : toPurgeList) {
            textChannel.delete().queue();
        }
    }
}
