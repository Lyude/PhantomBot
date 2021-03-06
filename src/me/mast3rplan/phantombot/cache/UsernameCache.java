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



package me.mast3rplan.phantombot.cache;

import com.gmt2001.TwitchAPIv3;
import com.google.common.collect.Maps;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.json.JSONObject;

public class UsernameCache
{

    private static final UsernameCache instance = new UsernameCache();

    public static UsernameCache instance()
    {
        return instance;
    }
    private Map<String, String> cache = Maps.newHashMap();
    private Date timeoutExpire = new Date();
    private Date lastFail = new Date();
    private int numfail = 0;
    
    private UsernameCache()
    {
        Thread.setDefaultUncaughtExceptionHandler(com.gmt2001.UncaughtExceptionHandler.instance());
    }

    public String resolve(String username)
    {
        String lusername = username.toLowerCase();

        if (username.equalsIgnoreCase("jtv") || username.equalsIgnoreCase("twitchnotify") || new Date().before(timeoutExpire))
        {
            return username;
        }

        if (cache.containsKey(lusername))
        {
            return cache.get(lusername);
        } else
        {
            try
            {
                JSONObject user = TwitchAPIv3.instance().GetUser(lusername);

                if (user.getBoolean("_success"))
                {
                    if (user.getInt("_http") == 200)
                    {
                        String displayName = user.getString("display_name");
                        cache.put(lusername, displayName);

                        return displayName;
                    } else
                    {
                        try
                        {
                            throw new Exception("[HTTPErrorException] HTTP " + user.getInt("status") + " " + user.getString("error") + ". req="
                                    + user.getString("_type") + " " + user.getString("_url") + " " + user.getString("_post") + "   message="
                                    + user.getString("message"));
                        } catch (Exception e)
                        {
                            com.gmt2001.Console.out.println("UsernameCache.updateCache>>Failed to get username: " + e.getMessage());

                            return username;
                        }
                    }
                } else
                {
                    if (user.getString("_exception").equalsIgnoreCase("SocketTimeoutException") || user.getString("_exception").equalsIgnoreCase("IOException"))
                    {
                        Calendar c = Calendar.getInstance();

                        if (lastFail.after(new Date()))
                        {
                            numfail++;
                        } else
                        {
                            numfail = 1;
                        }

                        c.add(Calendar.MINUTE, 1);

                        lastFail = c.getTime();

                        if (numfail >= 5)
                        {
                            timeoutExpire = c.getTime();
                        }
                    }

                    return username;
                }
            } catch (Exception e)
            {
                com.gmt2001.Console.err.printStackTrace(e);
                return username;
            }
        }
    }
}
