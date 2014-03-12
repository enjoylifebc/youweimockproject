package com.mock.trading.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import com.mock.trading.pb.TradePb.tick_pb;
import com.mock.trading.pb.TradePb.trade_pb;
import com.mock.trading.util.T;

@SuppressWarnings("serial")
public class TickTableModel extends AbstractTableModel {
	private String[] columnNames = { "ts", "index", "symbol", "type", "bid",
			"bidsize", "ask", "asksize" };

	public List<tick_pb> ticks = new ArrayList<tick_pb>();

	static private Map<Long, Double> tradesMap = new HashMap<>();

	public TickTableModel() {
	}

	public static void setTrade(List<trade_pb> trades) {
		tradesMap.clear();
		for (trade_pb t : trades) {
			tradesMap.put(t.getTs(), t.getPrice());
		}
	}

	public void setTicks(List<tick_pb> ticks) {
		this.ticks.clear();
		this.ticks.addAll(ticks);
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return ticks.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public tick_pb getRow(int rowIndex) {
		return ticks.get(rowIndex);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		tick_pb tick = ticks.get(rowIndex);

		boolean bold = false;
		if (tradesMap.get(tick.getTs()) != null || tradesMap.get(tick.getTs()+1) != null) {			
			double p = tradesMap.get(tick.getTs());
			if (p == 0)
				p = tradesMap.get(tick.getTs()+1);				
			if (T.d2(tick.getBid()) == p){
				bold =true;
			}
		}
		
		switch (columnIndex) {
		case 0:
			if (bold)
				return "<html><b>"+tick.getTs()+"<b></html>";
			else
				return tick.getTs();
		case 1:
			return tick.getIndex();
		case 2:
			return tick.getSymbol();
		case 3:
			return tick.getType();
		case 4:
			return tick.getBid();
		case 5:
			return tick.getBidsize();
		case 6:
			return tick.getAsk();
		case 7:
			return tick.getAsksize();
		}
		return null;
	}

}
