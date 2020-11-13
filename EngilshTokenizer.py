"""
Ling 570 Aut 2017

Q1 Breaks words in a file into tokens leaving intact common strings such as
emails, urls, and filepaths but separating other punctuation and contractions
from words.

#DEBUG lines provide functionality for debugging outside of command line

Rachel Lowy
"""

import sys
import re

def eng_tokenizer(abbrevs):

    abbrev_file = open(abbrevs, "r")
    #DEBUG file = open("ex1", "r")
    #DEBUGoutput = open("output.txt", "w")

    ## abbrev file ##
    first_line = True
    abbrev_regex = r'('
    for line in abbrev_file:
        this_abbrev = re.escape(line.rstrip())
        #DEBUG print(this_abbrev)
        if this_abbrev != "" and not first_line:
            this_abbrev = '|' + this_abbrev
        abbrev_regex += this_abbrev
        first_line = False
    abbrev_regex += ')'
    #DEBUG print(abbrev_regex)

    #DEBUG for line in file:
    for line in sys.stdin:
        # abbrev cases
        line = re.sub(abbrev_regex, r'\1XXX', line)
        line = re.sub(r'([A-Za-z]\.([A-Za-z]\.)+)', r'\1XXX', line)

        # path names - backslash
        line = re.sub(r'([^/\.]\b[a-z0-9]+)[/]', r'\1 / ', line)

        # quotations
        line = re.sub(r'("|\')([A-Za-z0-9]+)', r'\1 \2', line)
        line = re.sub(r'([A-Za-z0-9]+)("|\')', r'\1 \2 ', line)
        line = re.sub(r'([A-Za-z0-9]+)(,|\.)("|\')', r'\1 \2 \3 ', line)

        # remove punctuation
        line = re.sub('([\.?!,]\s)', r' \1', line)

        # preserve commas, separate negatives, separate dollar signs, preserve %
        line = re.sub(r'\$(\d)', r'$ \1', line)

        # handles dashes
        line = re.sub(r'([A-Za-z])?(-+)([^\d])', r'\1 \2 \3', line)

        # single quotes and contractions
        line = re.sub(r'([n]?\'[t]|\'[adlmrsv][le]?[l]?\b)', r' \1', line)  

        # Remove abbrev tags
        line = re.sub(r'\.XXX', r'.', line)

        #DEBUG output.write(line)
        print(line, end='')

if __name__ == '__main__':
    #DEBUG eng_tokenizer("abbrev_list.txt")
    eng_tokenizer(sys.argv[1])
