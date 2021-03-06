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



package com.gmt2001;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author gmt2001
 */
public class HttpRequest
{

    private static final int timeout = 5 * 1000;

    public static enum RequestType
    {

        GET, POST, PUT, DELETE
    }

    private HttpRequest()
    {
        Thread.setDefaultUncaughtExceptionHandler(com.gmt2001.UncaughtExceptionHandler.instance());
    }

    public static HttpResponse getData(RequestType type, String url, String post, HashMap<String, String> headers)
    {
        HttpResponse r = new HttpResponse();
        boolean isHttps = url.startsWith("https");

        r.type = type;
        r.url = url;
        r.post = post;
        r.headers = headers;

        try
        {
            URL u = new URL(url);

            if (isHttps)
            {
                HttpsURLConnection h = ((HttpsURLConnection) u.openConnection());

                for (Entry<String, String> e : headers.entrySet())
                {
                    h.addRequestProperty(e.getKey(), e.getValue());
                }
                
                h.setRequestMethod(type.name());
                h.setUseCaches(false);
                h.setDefaultUseCaches(false);
                h.setConnectTimeout(timeout);

                if (!post.isEmpty())
                {
                    h.setDoOutput(true);
                }

                h.connect();

                if (!post.isEmpty())
                {
                    IOUtils.write(post, h.getOutputStream());
                }

                if (h.getResponseCode() == 200)
                {
                    r.content = IOUtils.toString(h.getInputStream(), h.getContentEncoding());
                    r.httpCode = h.getResponseCode();
                    r.success = true;
                } else
                {
                    r.content = IOUtils.toString(h.getErrorStream(), h.getContentEncoding());
                    r.httpCode = h.getResponseCode();
                    r.success = false;
                }
            } else
            {
                HttpURLConnection h = ((HttpURLConnection) u.openConnection());

                for (Entry<String, String> e : headers.entrySet())
                {
                    h.addRequestProperty(e.getKey(), e.getValue());
                }

                h.setRequestMethod(type.name());
                h.setUseCaches(false);
                h.setDefaultUseCaches(false);
                h.setConnectTimeout(timeout);

                if (!post.isEmpty())
                {
                    h.setDoOutput(true);
                }

                h.connect();

                if (!post.isEmpty())
                {
                    IOUtils.write(post, h.getOutputStream());
                }

                if (h.getResponseCode() == 200)
                {
                    r.content = IOUtils.toString(h.getInputStream(), h.getContentEncoding());
                    r.httpCode = h.getResponseCode();
                    r.success = true;
                } else
                {
                    r.content = IOUtils.toString(h.getErrorStream(), h.getContentEncoding());
                    r.httpCode = h.getResponseCode();
                    r.success = false;
                }
            }
        } catch (IOException ex)
        {
            r.success = false;
            r.httpCode = 0;
            r.exception = ex.getMessage();

            com.gmt2001.Console.err.printStackTrace(ex);
        }

        return r;
    }
}
