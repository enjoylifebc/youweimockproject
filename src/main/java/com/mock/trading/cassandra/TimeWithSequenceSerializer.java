package com.mock.trading.cassandra;

import com.netflix.astyanax.annotations.Component;

public class TimeWithSequenceSerializer {
	 @Component(ordinal=0) Long   ts;
	 @Component(ordinal=1) Integer sequence;
	public TimeWithSequenceSerializer(){		 
	}
	
	public Long getTs() {
		return ts;
	}
	public void setTs(Long ts) {
		this.ts = ts;
	}

	public Integer getId() {
		return sequence;
	}

	public void setId(Integer id) {
		this.sequence = id;
	}

	
}
