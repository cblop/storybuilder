#!/usr/bin/python

# 20160422 JAP: Add None check for domain_files for compilation only step
# 20160413 JAP: removed -bd option and changed -d to allow multiple files
# 20160405 JAP: removed arb function and changed all type=arb to type=str
#               added g option for Gantt trace file
# 201603XX JAP: abstracted code from pyinstal, removed/added parameter,
#               revised checking logic and added code to use temporary files

from __future__ import print_function
import argparse
import sys
import os.path
from tempfile import mkstemp, mkdtemp

def buildargparser():
    argparser = argparse.ArgumentParser()
    argparser.add_argument(
        "-b", "--bridge-file", type=str,
        help="specify bridge instal file")
    # argparser.add_argument(
    #     "-bd", "--bridge-domain-file", type=str,
    #     help="specify domain file for bridge institution")
    argparser.add_argument(
        "-d", "--domain-files", type=str, nargs='+',
        help="specify one or more domain files (.idc)")
    argparser.add_argument(
        "-f", "--fact-file", type=str,
        help="specify initial fact file (.iaf)")
    argparser.add_argument(
        "-g", "--gantt-file", type=str,
        help="specify output file gantt visualization")
    argparser.add_argument(
        "-i", "--input-files", type=str, nargs='+', 
        help="specify one or more instal files (.ial)")
    argparser.add_argument(
        "-o", "--output-file", type=str,
        help="output file/directory for one/several inputs: uses /tmp if omitted")
    # hold this pending major rework of instaltrace
    # argparser.add_argument(
    #     "-s", "--states", type=str,
    #     help="specify which states to display (default all)")
    argparser.add_argument(
        "-t", "--trace-file", type=str,
        help="specify output file trace visualization")
    argparser.add_argument(
        "-v", "--verbose", action='count',
        help="turns on trace output, v for holdsat, vv for more")
    argparser.add_argument(
        "-q", "--query", type=str,
        help="specify query file (.iaq) - use \"-\" to take from stdin.")
    return argparser

def getargs():
    argparser = buildargparser()
    # got following line from http://stackoverflow.com/questions/1450393/how-do-you-read-from-stdin-in-python
    # which allows fileinput and argparse to co-exist, but might be better to use .REMAINDER
    args,unk = argparser.parse_known_args()
    # some self-check argument failure cases 
    # if  args.bridge_file and not args.bridge_domain_file:
    #     sys.stderr.write("ERROR: bridge domain file (-bd) is needed to process a bridge file \n")
    #     exit(-1)

    #unk checking
    if len(unk) > 1:
        sys.stderr.write("ERROR: More than one argument not connected to a flag.")
        exit(-1)

    #Query file checking
    if args.query is None:
        if unk == []:
            args.query = "-" #stdin
        else:
            args.query = unk[0]

    if not args.query == "-":
        name,ext = os.path.splitext(args.query)
        if not ext == ".iaq":
            sys.stderr.write("WARNING: Query file extension incorrect (should be .iaq)\n")

    #Fact file checking
    if args.fact_file:
        name,ext = os.path.splitext(args.fact_file)
        if not ext == ".iaf":
            sys.stderr.write("WARNING: Fact file extension incorrect (should be .iaf)\n")

    #Domain file checking
    if args.domain_files != None:
        for domain_file in args.domain_files:
            name,ext = os.path.splitext(domain_file)
            if not ext == ".idc":
                sys.stderr.write("WARNING: Domain file extension incorrect (should be .idc)\n")

    #bridge file checking
    if args.bridge_file and (not(args.input_files) or len(args.input_files)<2):
        sys.stderr.write("ERROR: bridge instal needs at least two instal files (-i) \n")
        exit(-1)

    #input file checking
    if len(args.input_files)==1 and not(args.output_file):
        (fd,ofile) = mkstemp()
        os.close(fd)
        args.output_file = ofile

    if len(args.input_files)>1 and not(args.output_file):
        # sys.stderr.write("ERROR: output directory (-o) needed with multiple input files\n")
        # exit(-1)
        args.output_file = mkdtemp()

    if len(args.input_files)>1 and not(os.path.isdir(args.output_file)):
        sys.stderr.write("ERROR: -o argument must be a directory for multiple input files\n")
        exit(-1)

    args.ial_files = []
    args.lp_files = []
    for input_file in args.input_files:
        name,ext = os.path.splitext(input_file)
        if ext == ".ial":
            args.ial_files.append(input_file)
        elif ext == ".lp":
            args.lp_files.append(input_file)
        else:
            sys.stderr.write("ERROR: Unrecognised input file extension (should be .ial or .lp)\n")
            exit(-1)

    # 20130331 JAP: commenting this case out to allow for composite mode
    # if len(args.input_files)>1 and not(args.bridge_file):
    #     sys.stderr.write("ERROR: a bridge file (-b) is needed with multiple input files\n")
    #     exit(-1)
    # or get on with the work

    return args,unk
