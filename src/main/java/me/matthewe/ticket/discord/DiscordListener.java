package me.matthewe.ticket.discord;

import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.config.Config;
import me.matthewe.ticket.discord.ticket.Ticket;
import me.matthewe.ticket.discord.ticket.TicketStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Matthew E on 12/10/2023 at 2:51 PM for the project TicketBot
 */
public class DiscordListener  extends ListenerAdapter  {
    private Map<Long, Ticket> ticketMap = new HashMap<>();
    private DiscordHandler discordHandler;
    private TicketBot ticketBot;
    private Config config;



    public DiscordListener(DiscordHandler discordHandler, TicketBot ticketBot, Config config) {
        this.discordHandler = discordHandler;
        this.ticketBot = ticketBot;
        this.config = config;
    }

    public Ticket getTicketByChannelId(long id){
        for (Ticket value : ticketMap.values()) {
            if (value.getChannelId()==id){
                return value;
            }
        }
        return null;
    }

    @Override
    public void onReady(ReadyEvent event) {
        this.discordHandler.setReady(true);



    }

    public void onShutdown(){
        for (Ticket value : ticketMap.values()) {
            TextChannel textChannel = value.getTextChannel();

            if (textChannel!=null){
                textChannel.delete().queue();
            }

        }

    }
    private void createTicketChannel(Guild guild, Member member) {
        ChannelAction<TextChannel> ticketChannel = guild.createTextChannel("ticket-" + member.getId(), guild.getCategoryById(this.config.discord.channels.ticketCategory));
        TextChannel complete = ticketChannel.clearPermissionOverrides().complete();

        Ticket ticket = new Ticket(ticketMap.size()+1,member.getIdLong(), complete.getIdLong(), TicketStatus.OPENED);

        ticketMap.put(ticket.getId(), ticket);

        // Send an initial message with a button to close the ticket
        Button closeTicketButton = Button.danger("closeTicketButton_" + complete.getId(), "Close Ticket");
        complete.sendMessage(member.getAsMention() + " Please describe your issue here.")
                .setActionRow(closeTicketButton)
                .queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
//        if (ticketChannels.containsKey(event.getChannel().getIdLong())){
//            this
//        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("ticket")) {
//         createTicketChannel(event.getGuild(), event.getMember());
/*
            EntitySelectMenu build = EntitySelectMenu.create("department", EntitySelectMenu.SelectTarget.USER).setPlaceholder("plugin").build();


            TextInput body = TextInput.create("body", "Information", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("description")
                    .setMaxLength(1000)
                    .build();


            Modal modal = Modal.create("test1", "Test")
                    .addComponents(ActionRow.of(body))
                    .build();
            event.replyModal(modal).queue();

 */
            SelectMenu menu = EntitySelectMenu.create("menu:class", EntitySelectMenu.SelectTarget.CHANNEL)
                    .setPlaceholder("Choose your class") // shows the placeholder indicating what this menu is for

                    .build();

            event.reply("Please pick your class below")
                    .setEphemeral(true)
                    .addActionRow(menu)
                    .queue();
        }

        }

}
