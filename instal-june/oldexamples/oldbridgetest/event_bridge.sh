#!/bin/bash
python ../../instalsolve.py $@ -i source.ial sink.ial -d domain.idc event_bridge_domain.idc -b event_bridge.ial -f bridge-init.iaf -q event_query.iaq
