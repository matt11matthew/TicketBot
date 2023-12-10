package me.matthewe.ticket;

import me.matthewe.ticket.handler.HandlerManager;
import me.matthewe.ticket.io.BasicLogger;
import me.matthewe.ticket.io.console.ConsoleCommandHandler;
import me.matthewe.ticket.io.console.OnMessage;
import me.matthewe.ticket.io.console.command.StopConsoleCommand;

import java.util.logging.Logger;

/**
 * Created by Matthew E on 12/10/2023 at 11:58 AM for the project TicketBot
 */
public class TicketBot {
    private final BasicLogger logger;
    private boolean running;

    private OnMessage onMessage;

    private HandlerManager handlerManager;
    public TicketBot() {
        this.handlerManager = new HandlerManager(this);
        this.running = false;
        this.logger = new BasicLogger(this);
    }

    private void registerHandlers() {
        this.handlerManager.registerHandler(new ConsoleCommandHandler(this));

    }
    public void start() {
        if (this.running) {
            logger.error("The bot is already running");
            return;
        }
        this.registerHandlers();
        this.registerConsoleCommands();
        this.handlerManager.enableHandlers();
        this.running = true;

    }

    private void registerConsoleCommands() {
        ConsoleCommandHandler commandHandler = this.handlerManager.getHandler(ConsoleCommandHandler.class);
        commandHandler.registerConsoleCommand(new StopConsoleCommand(this));
    }


    public BasicLogger getLogger() {
        return logger;
    }

    public void setOnMessage(OnMessage onMessage) {
        this.onMessage = onMessage;
    }

    public void onMessage(String text) {
        if (this.onMessage!=null){
            this.onMessage.onMessage(text);
        }
    }

    public void shutdown() {
        System.exit(0);
    }

    public void handleShutdown() {
        logger.info("Handling shutdown down.");


    }
}
