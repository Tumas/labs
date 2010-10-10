#!/bin/bash

for i in {1..10}
do
  xterm -e mplayer -playlist m3u/root.m3u &
done
