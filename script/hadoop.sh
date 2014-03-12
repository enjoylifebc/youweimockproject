#!/bin/sh

cygwin=false
        case "`uname`" in
        CYGWIN*) cygwin=true;;
esac


if $cygwin; then
  java -cp "..\lib\*;c:\bin\cygwin\lib\mock\*;..\config;" com.mock.trading.hadoop.SymbolCount
else
  java -cp "../lib/*:/opt/lib/mock/*:../config" com.mock.trading.hadoop.SymbolCount
fi