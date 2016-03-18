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

import me.aliceq.irc.IRCMessage;
import me.aliceq.irc.IRCMessageListener;
import me.aliceq.irc.IRCSubroutine;

/**
 * Subroutine called by a server upon connecting to validate the connection
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public final class ConnectionSubroutine extends IRCSubroutine {

    @Override
    public void run() {
        // Search for either a 001 connection success or 433 nick taken error
        IRCMessage msg = getMessage(new IRCMessageListener() {
            @Override
            public boolean check(IRCMessage message) {
                return message.getType().equals("001") || message.getType().equals("433");
            }
        });

        // Verify connection or not
        switch (msg.getType()) {
            case "001": // RPL_WELCOME
                server.getDetails().connected = true;
                break;
            case "433": // NICK TAKEN
                server.getDetails().nickIsTaken = true;
                return;
        }

        // Nickserv registration message
        if (getMessage("NickServ").getMessage().contains("This nickname is registered")) {
            server.getDetails().registered = true;

            // Nickserv identification message
            if (getMessage("NickServ").getMessage().contains("You are now identified for")) {
                server.getDetails().identified = true;
            }
        }
    }

}
