package com.mock.trading.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtil
{
	static Logger logger = LoggerFactory.getLogger(WebUtil.class.getName());	
	@SuppressWarnings("unchecked")
	public static String writeJson(UriInfo uriInfo, String text)
	{
		JSONObject obj = new JSONObject();
		String callback = uriInfo.getQueryParameters().getFirst("callback");
		obj.put("data", text);
		return callback + "(" + obj.toJSONString() + ")";
	}

	public static String csvToHtmlTable(String data)
	{
		String[] lines = data.split("\n");
		StringBuilder builder = new StringBuilder();
		builder.append("<table>");
		for (String line : lines)
		{
			builder.append("<tr>");
			String[] row = line.split(",");
			for (String td : row)
			{
				builder.append("<td>" + td + "</td>");
			}
			builder.append("</tr>");
		}
		builder.append("</table>");
		return builder.toString();
	}

	@SuppressWarnings("unused")
	public static String fromUrl(String urlStr)
	{
		logger.info("reading data from " + urlStr);
		StringBuilder b = new StringBuilder();
		try

		{
			URL url = new URL("http://" + urlStr);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				b.append(b + "\n");
			in.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return b.toString();
	}

}