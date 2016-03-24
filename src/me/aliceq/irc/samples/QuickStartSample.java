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
import me.aliceq.irc.internal.IRCSocket;
import me.aliceq.irc.subroutines.PrintIncomingSubroutine;

/**
 * A sample program which simply shows how to set up a connection to a server,
 * authenticate and then read messages from it.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class QuickStartSample {

    public static void main(String[] args) {
        // Create a new server instance to connect to irc.esper.net on the default port
        IRCServer server = new IRCServer("aperture.esper.net", IRCSocket.DEFAULT_PORT);

        // Server the server verbosity. This tells it to print messages to System.out 
        // and is mostly for debugging, but we want to see what's happening.
        server.setVerbosity(IRCServer.VERBOSITY_MEDIUM);
        
        // Start the server connection
        server.start();

        // Create an identity for yourself using the username "Jabberwocky", no password
        IRCIdentity me = new IRCIdentity("Jabberwock");
        // And identify
        server.identify(me);

        // Sleep for a long time so that the program doesn't exit. The moment this thread exits the program will quit.
        try {
            // Check every half second if we successfully connected
            while (!server.getDetails().connected) {
                Thread.sleep(500);
            }

            System.out.println("Successfully connected!");

            // Join the "Wonderland" channel
            server.join("#Wonderland");

            // Send a hello message
            server.message("#Wonderland", "Hello world!");

            // Wait for 5 seconds
            Thread.sleep(5000);
            
            // And quit
            server.quit("Goodbyte world!");
        } catch (Exception e) {

        }
    }
}
