import sys
sys.path.append(".")
import pycassa
from datetime import datetime
import time
from trade_pb_pb2  import tick_pb 
import logging
FORMAT='%(asctime)-15s %(message)s'
logging.basicConfig(filename='log/tick.log',level=logging.DEBUG,format=FORMAT)

class TradeImporter():
    
    pool = None
    cf = None
        
    KEY_SPACE = 'trade'
    
    def __init__(self):
        self.pool = pycassa.ConnectionPool(TradeImporter.KEY_SPACE, server_list=['localhost:9160'])
        self.cf = pycassa.ColumnFamily(self.pool, 'tick')
    
    def trade_import(self):
    logging.debug('starting tick import')
        i=0
        with self.cf.batch() as batch:
            for line in open(sys.argv[1],'r'):
                
                i += 1

        #symbol, [index], PriceTime, ticktype, bid, Ask, BidSize, AskSize
                line = line.strip('\n')
                line = line.strip('\r')
                splits = line.split(',')
        if (len(splits)>3):
                    time_orderref = (long(splits[2]), int(splits[1]))
        else:
            continue

                symbol= splits[0]       
        
                trade = tick_pb()
            trade.symbol=splits[0]
        trade.index=int(splits[1])
        trade.ts=long(splits[2])
        trade.type=splits[3]
            if splits[4] != '0.0':
            trade.bid=float(splits[4])
            if splits[5] != '0.0':
            trade.ask=float(splits[5])
            if splits[6] != '0':
            trade.bidsize=int(splits[6])
            if splits[7] != '0':
            trade.asksize=int(splits[7])
        
                batch.insert(symbol, {time_orderref: trade.SerializeToString()})
                if i % 50000 == 0:
                    logging.debug("inserted " + str(i) + ' ' + symbol)


if __name__ == '__main__':
    cas = TradeImporter()
    cas.trade_import()
