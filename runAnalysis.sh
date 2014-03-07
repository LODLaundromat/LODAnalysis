#!/bin/bash

# Usage info
show_help() {
cat << EOF
Usage: ${0##*/} [-hv] [-f FILE] [-p ROOT_PATH] 
Run analysis pipeline on hadoop
    
    -h          display this help and exit
    -p PATH     read analysis files from hadoop path
    -f FILE     only analyze this hadoop ntriple file (no need for -p option here)
    -v          verbose mode. Can be used multiple times for increased
                verbosity.
EOF
}
function hadoopLs {
	hadoopListing=()
	cmd="hadoop fs -ls $1 | grep -e '\.nt\(\.gz\)*$'"
	#echo "hadoop fs -ls $1";
	if [ "$verbose" -eq 1 ]; then echo "fetching hadoop files: $cmd"; fi
	dirListing=`eval $cmd`
	for word in ${dirListing} ; do
 		if [[ $word =~ ^/ ]];then 
	    	hadoopListing+=(${word})
	    fi
	done
}

#do sanity check (we need to be in the proper directory to make sure the relative paths in for instance our pig scripts can be reached properly)
#just a naive check (check if there is an LODAnalysis dir in the current working dir)
if [ ! -d LODAnalysis ]; then
    echo "Wrong working directory. Cannot locate LODAnalysis path in current working directory";
    exit 1
fi
  
# Initialize our own variables:
rootPath=""
inputFiles=()
verbose=0

OPTIND=1 # Reset is necessary if getopts was used previously in the script.  It is a good idea to make this local in a function.
while getopts "hvp:f:" opt; do
    case "$opt" in
        h)
            show_help
            exit 0
            ;;
        v)  verbose=1
            ;;
        p)  rootPath=$OPTARG
            ;;
        f)  inputFiles=( $OPTARG )
		    ;;
        '?')
            show_help >&2
            exit 1
            ;;
    esac
done
shift "$((OPTIND-1))" # Shift off the options and optional --.


if [ ${#inputFiles[@]} -eq 0 ]; then
	if [ "$rootPath" ]; then
		echo "fetching ntriple directories from hadoop"
		hadoopLs
		inputFiles=$hadoopListing
		if [ ${#inputFiles[@]} -eq 0 ]; then
			echo "Could not find ntriple directories on hdfs. Root path: $rootPath";
			exit 1
		fi
	else
		echo "No root path -and- no input file defined in settings. Cannot analyze ntriples"
		show_help >&2
		exit 1
	fi
fi


for inputFile in "${inputFiles[@]}"
do :
  pig LODAnalysis/pig/extractNs.py $inputFile
done


