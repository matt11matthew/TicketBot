package me.matthewe.ticket.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.List;

/**
 * Created by Matthew E on 12/10/2023 at 1:27 PM for the project TicketBot
 * This is class is gross
 *
 */
public class Config {
    @JsonProperty("database")
    public Database database;
    @JsonProperty("discord")
    public Discord discord;

    public static class Discord {
        @JsonProperty("auth")
        public Auth auth;
        @JsonProperty("channels")
        public Channels channels;
     @JsonProperty("departments")
        public List<String> departments;

        @JsonProperty("messages")
        public Messages messages;

        public static class Messages {
            @JsonProperty("ready")
            public EmbedValue ready;
            @JsonProperty("set_description")
            public EmbedValue set_description;

            @JsonProperty("posted")
            public EmbedValue posted;
            @JsonProperty("purged")
            public EmbedValue purged;
            @JsonProperty("created")
            public EmbedValue created;
            @JsonProperty("commission")
            public EmbedValue commission;
       @JsonProperty("transcript")
            public EmbedValue transcript;

            @JsonProperty("accepted")
            public EmbedValue accepted;
        }
        public static class Channels {
            @JsonProperty("info")
            public long info;

            @JsonProperty("ticketCategory")
            public long ticketCategory;

            @JsonProperty("commissions")
            public long commissions;
        }
        public static class Auth {

            public String token;

            @JsonProperty("guildId")public long guildId;
        }
    }

    public static class Database {
        public static class Host {
            @JsonProperty("address")
            public String address;

            @JsonProperty("port")
            public int port;
        }

        public static class Auth {
            @JsonProperty("username")
            public String username;

            @JsonProperty("password")
            public String password;
        }
        @JsonProperty("database")
        public String database;
        @JsonProperty("host")
        public Host host;

        @JsonProperty("auth")
        public Auth auth;
    }

}

