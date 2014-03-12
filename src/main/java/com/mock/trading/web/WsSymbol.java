package com.mock.trading.web;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.mock.api.ATickApi;
import com.mock.trading.pb.TradePb.tick_pb;

@Path("/symbol")
public class WsSymbol {


	@GET
	@Produces("text/html")
	public String get(@Context UriInfo uriInfo) {
		List<tick_pb> ticks = ATickApi.getTickByDate("JPM", new Integer(20130802), false);
		return WebUtil.csvToHtmlTable("jpm:"+ticks.size());
	}

}