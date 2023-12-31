package me.matthewe.ticket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.matthewe.ticket.config.Config;
import me.matthewe.ticket.config.EmbedValue;
import me.matthewe.ticket.database.DatabaseHandler;
import me.matthewe.ticket.discord.DiscordHandler;
import me.matthewe.ticket.handler.HandlerManager;
import me.matthewe.ticket.io.BasicLogger;
import me.matthewe.ticket.io.console.ConsoleCommandHandler;
import me.matthewe.ticket.io.console.OnMessage;
import me.matthewe.ticket.io.console.command.StopConsoleCommand;
import me.matthewe.ticket.io.utilities.FileUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.io.File;

import static me.matthewe.ticket.io.utilities.JsonUtils.loadJsonObjectFromFile;

/**
 * Created by Matthew E on 12/10/2023 at 11:58 AM for the project TicketBot
 */
public class TicketBot {
    private final BasicLogger logger;
    private boolean running;

    public static boolean DEBUG = true;

    private OnMessage onMessage;

    private Config config;
    private HandlerManager handlerManager;

    public TicketBot() {
        this.handlerManager = new HandlerManager(this);
        this.running = false;
        this.logger = new BasicLogger(this);
    }

    private void registerHandlers() {
        this.handlerManager.registerHandler(new DatabaseHandler(this, this.config));
        this.handlerManager.registerHandler(new ConsoleCommandHandler(this, this.config));
        this.handlerManager.registerHandler(new DiscordHandler(this, this.config));
    }

    public DatabaseHandler getDatabaseHandler() {
        return handlerManager.getHandler(DatabaseHandler.class);
    }

    public void start() {
        if (this.running) {
            logger.error("The bot is already running");
            return;
        }

        this.setupConfig();
        this.registerHandlers();
        this.registerConsoleCommands();
        this.handlerManager.enableHandlers();
        this.running = true;

    }

    private void registerConsoleCommands() {
        ConsoleCommandHandler commandHandler = this.handlerManager.getHandler(ConsoleCommandHandler.class);
        commandHandler.registerConsoleCommand(new StopConsoleCommand(this));
    }


    private void setupConfig() {
        String path = "config.json";
        if (DEBUG) {
            path = "C:\\Users\\Matthew Eisenberg\\eclipse-workspace\\TicketBot\\src\\main\\resources\\config.json";
        }

        String tokenPath = "token.txt";
        if (DEBUG) {
            tokenPath = "C:\\Users\\Matthew Eisenberg\\eclipse-workspace\\TicketBot\\src\\main\\resources\\token.txt";
        }
        Config config = loadJsonObjectFromFile(new File(path), Config.class);
        config.discord.auth.token = FileUtils.readFileToString(new File(tokenPath));
        this.config = config;
    }

    public BasicLogger getLogger() {
        return logger;
    }

    public void setOnMessage(OnMessage onMessage) {
        this.onMessage = onMessage;
    }

    public void onMessage(String text) {
        if (this.onMessage != null) {
            this.onMessage.onMessage(text);
        }
    }

    public void shutdown() {
        System.exit(0);
    }

    public void handleShutdown() {
        this.handlerManager.getHandler(DiscordHandler.class).handleShutdownFast();
        logger.info("Handling shutdown down.");
        this.handlerManager.disableHandlers();
    }

    public Config getConfig() {
        return config;
    }
}