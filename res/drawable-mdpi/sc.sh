#! /bin/bash
for i in *.jpg
do
	convert $i -resize 56x78 $i
done
