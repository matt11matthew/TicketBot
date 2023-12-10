package me.matthewe.ticket.io.console.command;

/**
 * Created by Matthew E on 12/10/2023 at 12:10 PM for the project TicketBot
 */
public abstract class ConsoleCommand {
    private String name;

    protected ConsoleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public  abstract void onCommand(String[] args);
}
