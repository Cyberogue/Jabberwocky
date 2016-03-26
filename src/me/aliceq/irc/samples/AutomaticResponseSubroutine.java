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
package me.aliceq.irc.samples;

import me.aliceq.irc.IRCMessage;
import me.aliceq.irc.IRCMessageListener;
import me.aliceq.irc.IRCSubroutine;

/**
 * Custom subroutine which simply responds to a preset message to any incoming
 * messages, either from users or from channels
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class AutomaticResponseSubroutine extends IRCSubroutine {

    private final String response;

    /**
     * Constructor
     *
     * @param response message to send back
     */
    public AutomaticResponseSubroutine(String response) {
        this.response = response;
    }

    /**
     * Run method. This is the entry point to the subroutine and where all
     * custom code should go
     */
    @Override
    public void run() {
        // We need a custom filter to retrieve messages. In partiucular, 
        // we want any private message - which have the type "PRIVMSG"

        // You can write a listener as its own class or as an anonymous class
        IRCMessageListener filter = new IRCMessageListener() {

            @Override
            public boolean check(IRCMessage message) {
                // Return true if the message matches our criteria
                return message.typeEquals("PRIVMSG");   // We want all messages of type PRIVMSG
            }
        };

        // This program runs separate to main() so you can run forever
        while (true) {
            // Get the next message using the filter. This will wait until a suitable message arrives.
            IRCMessage next = getMessage(filter);

            // Print the message we received to console
            // The format %S@%R: $M translates to [sender]@[receiver]: [message]
            System.out.println(next.toString("%S@%R: %M"));

            // This subroutine operates differently depending on whether or not the message was from a channel
            if (next.channelReceiver()) {
                // Channel message, only return if our current nick was mentioned
                if (next.getMessage().contains(server.getDetails().currentNick)) {
                    server.message(next.getSender(), response);
                }
            } else {
                // Direct message, send back to sender
                server.message(next.getSender(), response);
            }
        }
    }
}
