Longest-Concatenated-Word
=========================

This is a common coding puzzle when interviewing from Aspera, Inc.

Write a program that reads a file containing a sorted list of words (one

word per line, no spaces, all lower case), then identifies the longest

word in the file that can be constructed by concatenating copies of

shorter words also found in the file.

 

For example, if the file contained:

 

       cat

       cats

       catsdogcats

       catxdogcatsrat

       dog

       dogcatsdog

       hippopotamuses

       rat

       ratcatdogcat

 

The answer would be 'ratcatdogcat' - at 12 letters, it is the longest

word made up of other words in the list.  The program should then

go on to report how many of the words in the list can be constructed.

What is in it?
==============

Using Google concurrent tree api I have created a radix tree and also 
created a HashSet for set of all input word list. Then I used a HashMap
to store key value pairs of word to list of substrings and also subword
count.
