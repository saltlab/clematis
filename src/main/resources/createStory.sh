#!/bin/bash

cd clematis-output/ftrace/sequence_diagrams/
cp ../../../src/main/resources/sequence.pic .

ls *.pic > list
sed 's/\.pic//' < list > list2
sed 's/sequence//' < list2 > list 
for i in `cat list`
do 

size=`grep -c "message" ${i}.pic`

if [ $size -gt 120 ]; then
    pic2plot --font-size 1pt --line-width 0 -Tps "$i".pic > "$i".ps
else 
	if [ $size -gt 80 ]; then
    	pic2plot --font-size 2pt --line-width 0 -Tps "$i".pic > "$i".ps
	else
		if [ $size -gt 40 ]; then
    		pic2plot --font-size 3pt --line-width 0 -Tps "$i".pic > "$i".ps
		else
			if [ $size -gt 40 ]; then
    			pic2plot --font-size 5pt --line-width 0 -Tps "$i".pic > "$i".ps
			else
    			pic2plot --font-size 8pt --line-width 0 -Tps "$i".pic > "$i".ps
			fi
		fi
	fi
fi 

done

rm list2
rm list

ls *.ps > list
sed 's/\.ps//' < list > list2 
#for i in `cat list2`; do convert "$i".ps "$i".png ; done
rm list
rm list2

ls story_* > list
for i in `cat list`
do

sed 's/digraph G {/digraph G {\
\
  size="10,8.5";\
  rotate=90;\
  center=true;\
  compound=true;\
  rankdir="LR";\
  ranksep=1\.25;\
  label="CLEMATIS EPISODE RELATIONS";\
\
  node \[shape=plaintext\, fontsize=36\];\
\
  bgcolor=white;\
  edge \[arrowsize=1, color=black\];\
  /g' <$i >image_graph.dot

done
rm list

sed "s/E\([0-9]*\) \[ label=\"E\([0-9]*\)\" \];/subgraph cluster_E\1 \{label=\"E\1\"; labelloc=\"b\"; style=invis; E\1\_icon\};NEWLINEE\1\_icon \[label=\"\"\, shape=box\, shapefile=\"\1\.ps\"\];NEWLINE/g" image_graph.dot >flip.dot 

sed 's/NEWLINE/\
  /g' flip.dot >image_graph.dot

sed "s/E\([0-9]*\) ->/E\1\_icon ->/g" image_graph.dot > flip.dot
sed "s/-> E\([0-9]*\)/-> E\1\_icon/g" flip.dot >image_graph.dot

sed "s/E\([0-9]*\)_icon \[ label=\"Timing ID: \([0-9]*\)\" \];/E\1_icon \[ label=\"Timing ID: \2\" color=maroon weight=0\];/g" image_graph.dot > flip.dot 
sed "s/E\([0-9]*\)_icon \[ label=\"XHR ID: \([0-9]*\)\" \];/E\1_icon \[ label=\"XHR ID: \2\" color=maroon weight=0\];/g" flip.dot > image_graph.dot 

rm flip.dot
#rm *.ps
#rm sequence.pic

dot -Tps -oimage_graph.ps image_graph.dot
cd -

