#!/bin/bash
[ -z $LOD_ANALYSIS_JAR ] && echo "LOD_ANALYSIS_JAR environment variable not set" && exit 1;

force='-force';
verbose='-verbose';

[ -z "$1" ] && echo "No dataset provided as argument" && exit 1;
[ -z "$2" ] && echo "No output directory provided to write results to" && exit 1;

java -jar $LOD_ANALYSIS_JAR $force $verbose -dataset $1 -metrics $2 "lodanalysis.streamer.StreamDatasets"
