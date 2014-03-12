#!/bin/sh

cygwin=false
        case "`uname`" in
        CYGWIN*) cygwin=true;;
esac


if $cygwin; then
  java -cp "..\lib\*;c:\bin\cygwin\lib\mock\*;..\config;" com.mock.trading.analytic.AnalyticMain $@
else
  java -cp "../lib/*:/opt/lib/*:/opt/lib/mock/*:../config" com.mock.trading.analytic.AnalyticMain $@
fi
