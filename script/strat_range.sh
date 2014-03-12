#!/bin/sh

cygwin=false
        case "`uname`" in
        CYGWIN*) cygwin=true;;
esac

if $cygwin; then
  java -cp "..\lib\*;..\config;" com.mock.trading.rt.main.RangeMain
fi