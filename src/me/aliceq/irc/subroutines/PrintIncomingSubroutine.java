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

import me.aliceq.irc.IRCSubroutine;

/**
 * Subroutine which simply prints all incoming messages
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class PrintIncomingSubroutine extends IRCSubroutine {

    private String format = null;

    /**
     * Default constructor which prints raw messages
     */
    public PrintIncomingSubroutine() {
    }

    /**
     * The format to use when printing new messages. See IRCMessage.toString for
     * specification.
     * <p>
     * %S : sender <br>
     * %H : sender host-name<br>
     * %F : full sender ID. This is equal to %S!%H<br>
     * %R : receiver<br>
     * %T : type<br>
     * %M : message<br>
     * %D : date/time in default format<br>
     *
     * @param format String specification of message format
     */
    public PrintIncomingSubroutine(String format) {
        this.format = format;
    }

    @Override
    public void run() {

        while (true) {
            if (format == null) {
                System.out.println(getMessage());
            } else {
                System.out.println(getMessage().toString(format));
            }
        }
    }

}
