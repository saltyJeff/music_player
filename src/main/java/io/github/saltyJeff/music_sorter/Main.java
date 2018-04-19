package io.github.saltyJeff.music_sorter;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MusicManager man = new MusicManager();
        Scanner input = new Scanner(System.in);
        try {
            man.createTree("<INSERT URL HERE>");
            System.out.println("\n\nWelcome to the YT Song Searcher\n");
            System.out.println("Type . to leave");
            while(true) {
	            System.out.println("Enter a search term:");
	            System.out.print("> ");
	            String searchTerm = input.nextLine();
	            if(searchTerm.equals(".")) {
	            	break;
	            }
	            System.out.println("Results for: "+searchTerm);
	            List<SongNode> results = man.search(searchTerm);
	            for(int i = 0; i < results.size(); i++) {
	            	System.out.print("["+i+"] ");
	            	System.out.println(results.get(i));
	            }
	            System.out.println();
	            System.out.println("Enter the number of the song you want to listen to, or -1 to search again");
	            System.out.print(">> ");
	            int songIndex = input.nextInt();
	            input.nextLine();
	            if(songIndex == -1) {
	            	continue;
	            }
	            man.playItem(results.get(songIndex));
            }
            //man.playItem(rootItem.get(0).item);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}