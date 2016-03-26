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

    private String raw;
    private String sender;
    private String type;
    private String receiver;
    private String message;
    private Date time;

    protected IRCMessage() {

    }

    /**
     * Returns the raw IRC message
     *
     * @return
     */
    public String getRaw() {
        return raw;
    }

    /**
     * Returns the full sender of format user!hostname
     *
     * @return
     */
    public String getSenderFull() {
        return sender;
    }

    /**
     * Returns the sender of the message minus the hostname
     *
     * @return
     */
    public String getSender() {
        int i = sender.indexOf('!');
        if (i < 0) {
            return sender;
        } else {
            return sender.substring(0, i);
        }
    }

    /**
     * Returns the sender hostname of the message. If there is none a blank
     * String is returned.
     *
     * @return
     */
    public String getHostMask() {
        int i = sender.indexOf('!');
        if (i < 0) {
            return "";
        } else {
            return sender.substring(i + 1, sender.length() - 1);
        }
    }

    /**
     * Returns the receiver
     *
     * @return
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Returns the type
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * If the message type is a number it returns the value of the type,
     * otherwise returns -1
     *
     * @return
     */
    public int getMode() {
        if (type.matches("[0-9]*")) {
            return Integer.parseInt(type);
        } else {
            return -1;
        }
    }

    /**
     * Returns true if the message type is numeric
     *
     * @return true if the message type is numeric
     */
    public boolean numericType() {
        return type.matches("[0-9]*");
    }

    /**
     * Returns the receiver username or channel
     *
     * @return
     */
    public String getDestination() {
        return receiver;
    }

    /**
     * Returns the internal message
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the internal message in lowercase. This is useful for parsing
     * commands.
     *
     * @return
     */
    public String getMessageLower() {
        return message.toLowerCase();
    }

    /**
     * Returns the date/time the message was received
     *
     * @return
     */
    public Date getDateTime() {
        return time;
    }

    /**
     * Returns true if the message type equals the value, ignoring case
     *
     * @param value value to check
     * @return
     */
    public boolean typeEquals(String value) {
        return this.type.equalsIgnoreCase(value);
    }

    /**
     * Returns true if the message sender equals the value, ignoring case
     *
     * @param value value to check
     * @return
     */
    public boolean senderEquals(String value) {
        return this.getSender().equalsIgnoreCase(value);
    }

    /**
     * Returns true if the message sender's full name equals the value, ignoring
     * case
     *
     * @param value value to check
     * @return
     */
    public boolean fullSenderEquals(String value) {
        return this.sender.equalsIgnoreCase(value);
    }

    /**
     * Returns true if the message hostname equals the value, ignoring case
     *
     * @param value value to check
     * @return
     */
    public boolean hostEquals(String value) {
        return this.getHostMask().equalsIgnoreCase(value);
    }

    /**
     * Returns true if the message receiver equals the value, ignoring case
     *
     * @param value value to check
     * @return
     */
    public boolean receiverEquals(String value) {
        return this.receiver.equalsIgnoreCase(value);
    }

    /**
     * Returns true if the message was sent to a channel. That is, the receiver
     * is of the format #*
     *
     * @return true if the message was sent to a channel
     */
    public boolean channelReceiver() {
        return this.receiver.charAt(0) == '#';
    }

    /**
     * Parses a raw string message into an IRCMessage container. The message is
     * parsed using the format :[sender] [type] [receiver] :[message]
     *
     * @param raw the raw message to parse
     * @return a new IRCMessage instance
     */
    public static final IRCMessage parseFrom(String raw) {
        // Tokenize into max 5 tokens
        String[] tokens = raw.split("\\s+:?", 4);

        // Create new instance
        IRCMessage instance = new IRCMessage();
        instance.raw = raw;
        instance.time = new Date();
        // Source
        if (tokens.length > 0) {
            instance.sender = tokens[0].substring(1).trim();
        } else {
            return instance;
        }

        // Type
        if (tokens.length > 1) {
            instance.type = tokens[1].trim();
        } else {
            return instance;
        }

        // Destination
        if (tokens.length > 2) {
            instance.receiver = tokens[2].trim();
        } else {
            return instance;
        }

        // Message
        if (tokens.length > 3) {
            instance.message = tokens[3].trim();
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
     * %S : sender <br>
     * %H : sender host-name<br>
     * %F : full sender ID<br>
     * %R : receiver<br>
     * %T : type<br>
     * %M : message<br>
     * %D : date/time in default format<br>
     * %W : raw message<br>
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
     * %S : sender <br>
     * %H : sender host-name<br>
     * %F : full sender ID<br>
     * %R : receiver<br>
     * %T : type<br>
     * %M : message<br>
     * %D : date/time in specified format<br>
     * %W : raw message<br>
     *
     * @param format the message format
     * @param dateformat the date-time format
     * @return a formatted message String
     */
    public String toString(String format, DateFormat dateformat) {
        String s = format;

        String user = sender == null ? "" : this.getSender();
        String host = sender == null ? "" : this.getHostMask();

        s = s.replace("%F", user + (host.equals("") ? "" : "!" + host));
        s = s.replace("%S", user);
        s = s.replace("%H", host);
        s = s.replace("%R", receiver == null ? "" : receiver);
        s = s.replace("%T", type == null ? "" : type);
        s = s.replace("%M", message == null ? "" : message);
        s = s.replace("%W", raw == null ? "" : raw);
        s = s.replace("%D", time == null ? "" : dateformat.format(time));

        return s;
    }
}
