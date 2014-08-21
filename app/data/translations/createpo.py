# -*- coding: utf-8 -*-
import re
import sys
import json

def findstrings(fn):
    with open(fn) as f:
        data=f.read()
    return re.findall('translate\("(.*?)"\)',data)

if __name__=="__main__":
    fn = sys.argv[1]
    pd={}
    if len(sys.argv)>2:
        tf=sys.argv[2]
        with open(tf) as f:
            d=json.load(f)
        pd=d["messages"]
    
    for k in pd.keys():
        if k:
            print(u"#: %s"%fn)
            print(u'msgid "%s"'%k)
            print(u'msgstr "%s"'%pd[k][1])
            print(u'')

    for s in findstrings(fn):
        if s not in k:
            print("#: %s"%fn)
            print('msgid "%s"'%s)
            print('msgstr ""')
            print("")

