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
package me.aliceq.irc;

import java.text.DateFormat;
import java.util.Date;

/**
 * IRC message container
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class IRCMessage {

    public String raw;
    public String source;
    public String type;
    public String destination;
    public String message;
    public Date time;

    /**
     * Returns true if the message source equals the specified source
     *
     * @param source source to check
     * @return true if the message source equals the specified source
     */
    public boolean isSource(String source) {
        return this.source.equals(source);
    }

    /**
     * Returns true if the message type equals the specified type
     *
     * @param type type to check
     * @return true if the message type equals the specified type
     */
    public boolean isType(String type) {
        return this.type.equals(type);
    }

    /**
     * Returns true if the message type is a number whose value equals the
     * specified value
     *
     * @param mode value to check
     * @return true if the message type is a number and matches the specified
     * mode value
     */
    public boolean isMode(int mode) {
        if (type.matches("[0-9]*")) {
            return Integer.parseInt(this.type) == mode;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the message destination equals the specified destination
     *
     * @param destination destination to check
     * @return true if the message destination equals the specified destination
     */
    public boolean isDestination(String destination) {
        return this.destination.equals(destination);
    }

    /**
     * Parses a raw string message into an IRCMessage container. The message is
     * parsed using the format :[source] [type] [destination] :[message]
     *
     * @param raw the raw message to parse
     * @return a new IRCMessage instance
     */
    public static final IRCMessage parseFrom(String raw) {
        // Tokenize into max 5 tokens
        String[] tokens = raw.split("\\s", 4);

        // Create new instance
        IRCMessage instance = new IRCMessage();
        instance.raw = raw;
        instance.time = new Date();
        // Source
        if (tokens.length > 0) {
            instance.source = tokens[0].substring(1);
        } else {
            return instance;
        }

        // Type
        if (tokens.length > 1) {
            instance.type = tokens[1];
        } else {
            return instance;
        }

        // Destination
        if (tokens.length > 2) {
            instance.destination = tokens[2];
        } else {
            return instance;
        }

        // Message
        if (tokens.length > 3) {
            instance.message = tokens[3].substring(1);
        }
        return instance;
    }

    @Override
    public String toString() {
        return raw;
    }

    /**
     * Converts a message into a string following the specified format.
     * <p>
     * %S : source<br>
     * %U : destination<br>
     * %T : type<br>
     * %M : message<br>
     * %D : date/time in full format<br>
     *
     * @param format the message format
     * @return a formatted message String
     */
    public String toString(String format) {
        return toString(format, DateFormat.getDateTimeInstance());
    }

    /**
     * Converts a message into a string following the specified format.
     * <p>
     * %S : source<br>
     * %U : destination<br>
     * %T : type<br>
     * %M : message<br>
     * %D : date/time in specified<br>
     *
     * @param format the message format
     * @param dateformat the date-time format
     * @return a formatted message String
     */
    public String toString(String format, DateFormat dateformat) {
        String s = format;

        s = s.replace("%S", source == null ? "" : source);
        s = s.replace("%U", destination == null ? "" : destination);
        s = s.replace("%T", type == null ? "" : type);
        s = s.replace("%M", message == null ? "" : message);
        s = s.replace("%D", time == null ? "" : dateformat.format(time));

        return s;
    }
}
