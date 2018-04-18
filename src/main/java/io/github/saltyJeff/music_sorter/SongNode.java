package io.github.saltyJeff.music_sorter;

import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.zorvan.avl.AVLNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SongNode extends AVLNode<SongNode> {
    private String name;
    public StreamInfoItem item;
    public SongNode(StreamInfoItem i) {
        item = i;
        name = hashName(item.getName());
    }
    public SongNode(String n) {
        name = hashName(n);
    }
    private static String hashName(String s) {
    	//need to strip out uselessness in the name
    	s = s.toLowerCase();
    	//strip out the word lyrics
	    s = s.replace(" lyrics", "");
    	//strip out MV for music video
    	s = s.replace(" mv", "");
    	//strip out author name when the song is surrounded by single quotes or delimited by hyphen
    	s = stripStart(" '", s);
    	s = stripStart(" -", s);
    	//strip out the subtitle data
	    s = stripEnd(" [", s);
		s = stripEnd(" (", s);

	    List<Character> chars = s.chars()
			    .mapToObj(e -> (char) e)
			    .filter(e -> Character.isAlphabetic(e) || Character.isDigit(e))
			    .collect(Collectors.toList());
        Collections.sort(chars);
        StringBuilder returnString = new StringBuilder();
        for(char c : chars) {
        	returnString.append(c);
        }
        return returnString.toString();
    }
    private static String stripStart(String stripFrom, String original) {
    	int stripStart = original.indexOf(stripFrom);
    	if(stripStart != -1 && stripStart > 2) {
    		original = original.substring(stripStart+stripFrom.length());
	    }
	    return original;
    }
	private static String stripEnd(String stripTo, String original) {
		int stripEnd = original.lastIndexOf(stripTo);
		if(stripEnd != -1 && stripEnd < original.length() - 2) {
			original = original.substring(0, stripEnd);
		}
		return original;
	}
    @Override
    public int compareTo(SongNode o) {
       return name.compareTo(o.name);
    }
    @Override
    public String toString() {
       return item.getName()+"\t("+item.getUploaderName()+")";
    }
    @Override
    public int getValue() {
       return name.hashCode();
    }
}
