import lxml.etree
import sys

namespaces={"gpx":"http://www.topografix.com/GPX/1/1"}
if __name__=="__main__":
    r=None
    for file in sys.argv[1:]:
        if not r:
            r=lxml.etree.parse(file)
        else:
            r1=lxml.etree.parse(file)
            for rte in r1.xpath("//gpx:rte",namespaces=namespaces):
                r.getroot().append(rte)
    print lxml.etree.tostring(r)
