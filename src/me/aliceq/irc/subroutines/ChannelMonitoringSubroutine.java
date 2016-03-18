/*
 * The MIT License
 *
 * Copyright 2016 Alice Quiros <email@aliceq.me>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.aliceq.irc.subroutines;

import me.aliceq.irc.IRCChannel;
import me.aliceq.irc.IRCCode;
import me.aliceq.irc.IRCMessage;
import me.aliceq.irc.IRCMessageListener;
import me.aliceq.irc.IRCSubroutine;

/**
 * Subroutine used to monitor a channel and maintain information about its
 * getUsers and status
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class ChannelMonitoringSubroutine extends IRCSubroutine {

    @Override
    public void run() {
        // Listener returns any message that has a numeric mode (error, etc)
        // or that is a non-PRIVMSG directed at a channel
        //
        // Catching PRIVMSG now istead of after fetching results in slightly 
        //lower memory usage, as getting each message takes resources
        IRCMessageListener listener = new IRCMessageListener() {
            @Override
            public boolean check(IRCMessage message) {
                return message.numericType() || (message.getDestination().charAt(0) == '#' && !message.typeEquals("PRIVMSG"));
            }
        };

        // Run indefinitely (thread is a daemon)
        while (true) {
            // Get the next message
            IRCMessage message = getMessage(listener);

            // Check whether it's a numeric response or a command
            if (message.numericType()) {
                parseMode(message.getMode(), message);
            } else {
                parseCommand(message.getType(), message);
            }
        }
    }

    private void parseMode(int mode, IRCMessage message) {
        // Get index of colon
        String m = message.getMessage();
        int index = m.indexOf(':');

        // Extract current username
        server.getDetails().currentNick = message.getReceiver();

        // Parse
        if (mode == IRCCode.RPL_NAMREPLY) { // List of names, extract current users
            // Extract channel
            IRCChannel channel = server.getChannel(m.substring(2, index - 1));

            // Extract users and add
            for (String nick : m.substring(index + 1).split(" ")) { // Add each nick
                channel.addUser(nick);
            }

            if (server.isVerbose()) {
                System.out.println("Users in " + channel.getName() + ": " + channel.getUsers());
            }
        } else if (mode == IRCCode.RPL_TOPIC) {// Set the channel topic
            // Set the channel topic
            // Extract channel
            IRCChannel channel = server.getChannel(m.substring(0, index - 1));
            channel.setTopic(m.substring(index + 1));
            if (server.isVerbose()) {
                System.out.println("Topic for  " + channel.getName() + ": " + channel.getTopic());
            }
            
            // If we don't have an user list ask for one
            if (channel.getUsers().isEmpty()) {
                this.send("NAMES " + channel.getName());
            }
        }
    }

    private void parseCommand(String command, IRCMessage message) {
        String channel = message.getDestination();

        switch (message.getType().toUpperCase()) {
            case "JOIN":
                if (message.senderEquals(server.getDetails().currentNick)) {
                    break;
                }
                server.getChannel(channel).addUser(message.getSender());
                if (server.isVerbose()) {
                    System.out.println("Person joined " + channel);
                }
                break;
            case "PART":
            case "QUIT":
            case "KICK":
                if (message.senderEquals(server.getDetails().currentNick)) {
                    server.unregisterChannel(channel);
                    if (server.isVerbose()) {
                        System.out.println("Program left " + channel);
                    }
                } else {
                    server.getChannel(channel).removeUser(message.getSender());
                    if (server.isVerbose()) {
                        System.out.println(message.getSender() + " left " + channel);
                    }
                }
                break;
            case "MODE": {
                int index = message.getMessage().indexOf(' ');
                String user = message.getMessage().substring(index + 1);
                String mode = message.getMessage().substring(0, index);
                boolean toggle = true;   // true = add, false = remove
                for (int i = 0; i < mode.length(); i++) {
                    switch (mode.charAt(i)) {
                        case '+':
                            toggle = true;
                        case '-':
                            toggle = false;
                        case 'v':
                        case 'V':
                            if (toggle) {
                                server.getChannel(channel).replaceUser(user, "+" + user);
                            }
                        case 'o':
                        case 'O':
                            if (toggle) {
                                server.getChannel(channel).replaceUser(user, "@" + user);
                                break;
                            }
                    }
                }

                if (server.isVerbose()) {
                    System.out.println("Changed user mode for  " + channel + ":" + user + " to " + mode);
                }
            }
            break;
        }

    }
}
