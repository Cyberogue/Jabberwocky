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

    final private IRCChannel channel;

    public ChannelMonitoringSubroutine(IRCChannel channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        System.out.println("Monitoring channel " + channel.getName());

        // Get the next message with a mode
        IRCMessage message = getMessage(new IRCMessageListener() {
            @Override
            public boolean check(IRCMessage message) {
                return message.getMode() > 0;
            }
        });

        // Update current nickname
        server.getDetails().currentNick = message.getReceiver();

        if (message.getMode() == 353) {
            // Success!
            channel.setStatus(353);

            // Extract nicks from the message
            String s = message.getMessage();
            s = s.substring(s.indexOf(':') + 1, s.length());

            // And add to list
            for (String nick : s.split(" ")) {
                channel.addUser(nick);
            }
        }

        IRCMessageListener listener = new IRCMessageListener() {
            @Override
            public boolean check(IRCMessage message) {
                return message.getDestination().equals(channel.getName());
            }
        };

        System.out.println(channel.hasError());
        while (!channel.hasError()) {
            message = this.getMessage(listener);

            System.out.println(message.toString("%S|%T|%M"));

            if (message.typeEquals("PRIVMSG")) {
                // This is the most common message so an early check reduces computations
                continue;
            }

            if (message.typeEquals("JOIN")) {
                channel.addUser(message.getSender());
            } else if (message.typeEquals("PART")) {
                channel.removeUser(message.getSender());
            } else if (message.typeEquals("KICK")) {
                channel.removeUser(message.getSender());
                if (message.getSender().equalsIgnoreCase(server.getDetails().currentNick)) {
                    // We were kicked, set the banned/kicked flag
                    channel.setStatus(353);
                }
            } else if (message.typeEquals("NICK")) {
            } else if (message.typeEquals("MODE")) {
                String s = message.getMessage();
                int split = s.indexOf(" ");
                String modes = s.substring(0, split);
                String user = s.substring(split + 1, s.length());

                // Parse flags
                boolean add = true; // false = remove
                for (int i = 0; i < modes.length(); i++) {
                    switch (modes.charAt(i)) {
                        case '+':
                            add = true;
                            break;
                        case '-':
                            add = false;
                            break;
                        case 'v':
                        case 'V':
                            channel.replaceUser(user, "+" + user);
                            break;
                        case 'o':
                        case 'O':
                            channel.replaceUser(user, "@" + user);
                            break;
                    }
                }
            }
            System.out.println(channel.getUsers());
        }
    }

}
