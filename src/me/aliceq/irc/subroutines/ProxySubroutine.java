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
 * An advanced-use subroutine which works as a proxy for messages. Any messages
 * received containing with the provided key whose sender matches the master
 * will be echoed raw.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class ProxySubroutine extends IRCSubroutine {

    private final String master;
    private final String key;

    /**
     * Constructor. By default the key is "$" (no quotes)
     *
     * @param master the nickname[s] this program will listen to. This is a
     * regex string so multiple nicks may be contained.
     */
    public ProxySubroutine(String master) {
        this("$", master);
    }

    /**
     * Constructor
     *
     * @param key the key is a character sequence that must be included at the
     * beginning of every message to echo
     * @param master the nickname this program will listen to. This is a regex
     * string so multiple nicks may be contained.
     */
    public ProxySubroutine(String key, String master) {
        this.master = master;
        this.key = key;
    }

    @Override
    public void run() {
        IRCMessageListener filter = new IRCMessageListener() {

            @Override
            public boolean check(IRCMessage message) {
                int index = message.getMessage().indexOf(key);
                return index == 0 && message.getSender().matches(master);
            }
        };

        while (true) {
            IRCMessage message = getMessage(filter);
            String raw = message.getMessage().substring(key.length());
            server.send(raw);
        }
    }
}
