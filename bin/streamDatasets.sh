#!/bin/bash

[ -z $LOD_ANALYSIS_JAR ] && echo "LOD_ANALYSIS_JAR environment variable not set" && exit 1;




#force='-force';
#verbose='-verbose';
threads=20;
datasetDir="/scratch/lodlaundromat/11/"
if [ -z "$1" ]; then
    echo "Using dataset dir $datasetDir"
else
    datasetDir=$1
fi
metricDir="/scratch/lodlaundromat/metrics-11/"
if [ -z "$2" ]; then
    echo "Using metric dir $metricDir"
else
    metricDir=$2
fi






while true; do
    echo "Run cmd [Y/n]?"
    cmd="java -jar $LOD_ANALYSIS_JAR $force $verbose -threads $threads -datasets $1 -metrics $2 \"lodanalysis.streamer.StreamDatasets\""
    echo "$cmd"
    read -n 1 -r yn
    case $yn in
        [Yy]* ) eval "$cmd";;
        [Nn]* ) exit 1;;
        * ) echo "Please answer yes or no.";;
    esac
done

