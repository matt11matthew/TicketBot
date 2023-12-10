package me.matthewe.ticket.io.console;

import me.matthewe.ticket.TicketBot;
import me.matthewe.ticket.handler.Handler;
import me.matthewe.ticket.io.console.command.ConsoleCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Matthew E on 12/10/2023 at 12:04 PM for the project TicketBot
 */
public class ConsoleCommandHandler extends Handler {

    private Map<String, ConsoleCommand> commandMap;
    public ConsoleCommandHandler(TicketBot ticketBot) {
        super(ticketBot);
        this.commandMap = new HashMap<>();

    }

    public void registerConsoleCommand(ConsoleCommand consoleCommand) {
        this.commandMap.put(consoleCommand.getName().toLowerCase(), consoleCommand);
        this.ticketBot.getLogger().info("[ConsoleHandler] Registered console command '" +consoleCommand.getName().toLowerCase()+"'");
    }
    @Override
    public void onEnable() {
        this.ticketBot.setOnMessage(this::handleCommandInput);
    }

    private void handleCommandInput(String text) {
        String command = null;
        List<String> argList = new ArrayList<>();

        if (text.contains(" ")){

            String[] split = text.split(" ");
            for (int i = 0; i < split.length; i++) {
                if (i==0){
                    command=split[i];
                } else {
                    argList.add(split[i]);
                }
            }
        } else {
            command = text;
        }
        String[] args = new String[argList.size()];
        for (int i = 0; i < argList.size(); i++) {
            args[i]=argList.get(i);
        }

        if (command!=null&&commandMap.containsKey(command.toLowerCase())){
            commandMap.get(command.toLowerCase()).onCommand(args);

        }
    }

    @Override
    public void onDisable() {

    }
}
