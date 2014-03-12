
youweimockproject
=================

YouweiMockProject

The project is setup using Maven for dependency managenet.  It has a simple script to deploy the project to Linux machine.

script/tick.py will read market tick in csv format, convert it into protobufer format, and insert it into cassandra
trade_pb.proto is protobuf file specification

ATickapi.java - some sensible query for tick data: query cassandra, filter data, convert to OHLC min/hour bar
TimeWithSequenceSerializer - Unique tick with Time and Sequence.  This call for fast query using time.

GrizzlyMain/JersyMain
two way to start web service: using gzilly or jersey to serve the time serie data

Entitystore:
Customization to astyanax data mapper entity, in particular, I was trying to embed a customized list inside Entity Manager framework.

UI: the ui is used to show the data in Swing GUI.

