package com.mock.trading.cassandra;

import com.netflix.astyanax.annotations.Component;

public class BarSerializer {
	 @Component(ordinal=0) String   symbol;
	 @Component(ordinal=1) Long date;
	public BarSerializer(){		 
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	

	
}
