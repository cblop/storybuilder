#!/bin/bash
python ../../instalsolve.py $@ -i source.ial sink.ial -d domain.idc fluent_bridge_domain.idc -b fluent_bridge.ial -f bridge-init.iaf -q fluent_query.iaq
