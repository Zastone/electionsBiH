import re
import sys

def findstrings(fn):
    with open(fn) as f:
        data=f.read()
    return re.findall('translate\("(.*?)"\)',data)

if __name__=="__main__":
    fn = sys.argv[1]
    for s in findstrings(fn):
        print "#: %s"%fn
        print 'msgid "%s"'%s
        print 'msgstr ""'
        print ""

