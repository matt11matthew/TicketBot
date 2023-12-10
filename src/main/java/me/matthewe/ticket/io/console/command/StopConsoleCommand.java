package me.matthewe.ticket.io.console.command;

import me.matthewe.ticket.TicketBot;

/**
 * Created by Matthew E on 12/10/2023 at 12:57 PM for the project TicketBot
 */
public class StopConsoleCommand extends ConsoleCommand{

    public StopConsoleCommand(TicketBot ticketBot) {
        super("stop", ticketBot);
    }

    @Override
    public void onCommand(String[] args) {

        System.exit(0);


    }
}
