#!/usr/bin/env python

''' Lists of blocked and ready processes with currently executing process '''

# process lists
BLOCKED_PROCESSES = None
READY_PROCESSES = None
ACTIVE_PROCESS = None

# resource lists
RESOURCES = None

# simple counters for generating unique PIDs and RIDs
PID = 0
RID = 0
