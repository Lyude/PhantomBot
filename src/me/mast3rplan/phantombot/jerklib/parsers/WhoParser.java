/* 
 * Copyright (C) 2015 www.phantombot.net
 *
 * Credits: mast3rplan, gmt2001, PhantomIndex, GloriousEggroll
 * gloriouseggroll@gmail.com, phantomindex@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */



package me.mast3rplan.phantombot.jerklib.parsers;

import me.mast3rplan.phantombot.jerklib.events.IRCEvent;
import me.mast3rplan.phantombot.jerklib.events.WhoEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WhoParser implements CommandParser {
    public IRCEvent createEvent(IRCEvent event) {
        String data = event.getRawEventData();
        Pattern p = Pattern.compile("^:.+?\\s+352\\s+.+?\\s+(.+?)\\s+(.+?)\\s+(.+?)\\s+(.+?)\\s+(.+?)\\s+(.+?):(\\d+)\\s+(.+)$");
        Matcher m = p.matcher(data);
        if (m.matches()) {

            boolean away = m.group(6).charAt(0) == 'G';
            return new WhoEvent(m.group(1), // channel
                    Integer.parseInt(m.group(7)), // hop count
                    m.group(3), // hostname
                    away, // status indicator
                    m.group(5), // nick
                    data, // raw event data
                    m.group(8), // real name
                    m.group(4), // server name
                    event.getSession(), // session
                    m.group(2) // username
            );
        }
        return event;
    }
}
