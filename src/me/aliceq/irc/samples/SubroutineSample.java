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

import me.aliceq.irc.IRCIdentity;
import me.aliceq.irc.IRCServer;
import me.aliceq.irc.IRCSubroutine;
import me.aliceq.irc.internal.IRCSocket;

/**
 * A sample program which connects to a server and runs a custom subroutine
 * implementation
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class SubroutineSample {

    public static void main(String[] args) {
        /*
         * See QuickStartSample for information on connecting to a server
         */

        // Create server and connect
        IRCServer server = new IRCServer("aperture.esper.net", IRCSocket.DEFAULT_PORT);
        IRCIdentity me = new IRCIdentity("Jabberwock");
        server.start();
        server.identify(me);

        // Create a new instance of the subroutine. See AutomaticResponseSubroutine within the same package to understand how it work
        IRCSubroutine subroutine = new AutomaticResponseSubroutine("I am a bot. This is an automated message. Beep boop.");

        try {
            // Check every half second if we successfully connected
            while (!server.getDetails().connected) {
                Thread.sleep(500);
            }
            System.out.println("Successfully connected!");
            server.join("#Wonderland");
            
            // Once we're connected, run our subroutine
            server.runSubroutine(subroutine);

            // Wait for 10 minutes (millisecond value)
            Thread.sleep(10 * 60 * 1000);
        } catch (Exception e) {

        }

        // and quit the program
        server.quit("Goodbyte world!");
    }
}
