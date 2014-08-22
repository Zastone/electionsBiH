##Readme

Candidate data for the 2014 Bosnian General Election. Data is extracted from PDF provided by the Election Commission.

The data was extracted out of PDF using [Tabula](http://tabula.nerdpower.org/) and was processed and cleaned with [Open Refine](http://openrefine.org/).

### Files
* 2014-elections.csv provides a list of elections and IDs. In some cases the Serb translation of the election name went onto a separate line and may be cut off in this file.
* 2014-parties.csv includes the list and ID of all parties and independent lists contesting the 2014 election
* 2014-candidate-list.csv lists each candidate along with his/her party-id, number on their list, and the id of the election that he/she is contesting. Where an individual is on multiple lists (such as a compensation list), it is listed as a separate record. Therefore this file has more records than there are unique candidates.

**Raw Data**
* Kandidatske_liste_2014.pdf - list provided by the Election Commission
* tabula-export.csv - raw tabula export
* unused_text.csv - includes unused headers and election translations that were cut off by Tabule

### Notable and Exceptions

БОШКОВИЋ ДАНКА is a parliamentary candidate for district 307. She is also listed her party's compensation list. In her district list her name is listed as БОШКОВИЋ ДАНКА. In the compensatory list her name is listed as БОШКОВИЋ (Павле) ДАНКА. This is probably to distinguish him from БОШКОВИЋ (Радо) ДАНКА in another party.
