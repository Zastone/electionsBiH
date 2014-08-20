import csv
import json
import sys

filename = "../Municipality IDs/election-types.csv"

def create_translation(l):
    with open(filename) as f:
        r=csv.DictReader(f)
        d=filter(lambda x: x['Language']==l, r)
    data = {
        "messages": {
            "": {
                "domain":"messages",
                "lang": l,
                "plural-forms": "nplurals=2; plural=(n !=1 );"
                },
            
            }
            }
    for i in d:
        data['messages'][i['election_type']]=i['Title']
        data['messages']["%s_description"%i['election_type']]=i['Description']
    return json.dumps(data)
    
if __name__=="__main__":
    lang=sys.argv[1]
    print create_translation(lang)
