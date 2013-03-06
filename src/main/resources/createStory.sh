#!/bin/bash

cd metis-output/ftrace/sequence_diagrams/
cp ../../../src/main/resources/sequence.pic .

ls *.pic > list
sed 's/\.pic//' < list > list2
sed 's/sequence//' < list2 > list 
for i in `cat list`; do pic2plot -Tps "$i".pic > "$i".ps ; done
rm list2
rm list

ls *.ps > list
sed 's/\.ps//' < list > list2 
for i in `cat list2`; do convert "$i".ps "$i".png ; done
rm list
rm list2

ls story_* > list
for i in `cat list`
do

sed 's/digraph G {/digraph G {\
\
  compound=true;\
  rankdir="LR";\
  ranksep=1\.25;\
  label="METIS EPISODE RELATIONS";\
\
  node \[shape=plaintext\, fontsize=36\];\
\
  bgcolor=white;\
  edge \[arrowsize=1, color=black\];\
  /g' <$i >image_graph.dot

done
rm list

sed "s/E\([0-9]*\) \[ label=\"E\([0-9]*\)\" \];/subgraph cluster_E\1 \{label=\"E\1\"; labelloc=\"b\"; style=invis; E\1\_icon\};NEWLINEE\1\_icon \[label=\"\"\, shape=box\, shapefile=\"\1\.png\"\];NEWLINE/g" image_graph.dot >flip.dot 

sed 's/NEWLINE/\
  /g' flip.dot >image_graph.dot

sed "s/E\([0-9]*\) ->/E\1\_icon ->/g" image_graph.dot > flip.dot
sed "s/-> E\([0-9]*\)/-> E\1\_icon/g" flip.dot >image_graph.dot

sed "s/E\([0-9]*\)_icon \[ label=\"Timing ID: \([0-9]*\)\" \];/E\1_icon \[ label=\"Timing ID: \2\" color=maroon weight=0\];/g" image_graph.dot > flip.dot 
sed "s/E\([0-9]*\)_icon \[ label=\"XHR ID: \([0-9]*\)\" \];/E\1_icon \[ label=\"XHR ID: \2\" color=maroon weight=0\];/g" flip.dot > image_graph.dot 

rm flip.dot
#rm *.ps
#rm sequence.pic

dot -Tpng -oimage_graph.png image_graph.dot
cd -
