package io.github.saltyJeff.music_sorter;

import org.schabi.newpipe.extractor.Downloader;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.services.youtube.YoutubePlaylistExtractor;
import org.schabi.newpipe.extractor.services.youtube.YoutubeService;
import org.schabi.newpipe.extractor.services.youtube.YoutubeStreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.zorvan.avl.AVLNode;
import org.zorvan.avl.AVLTree;

import javax.print.DocFlavor;
import javax.print.URIException;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MusicManager {
    public AVLTree tree = new AVLTree();
    private StreamingService service = new YoutubeService(1);
    private String makeRequest(String siteUrl) {
        try {
            String s = new Scanner(new URL(siteUrl).openStream(), "UTF-8").useDelimiter("\\A").next();
            //new PrintWriter("file.txt").println(s);
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    private Downloader downloader = new Downloader() {
        @Override
        public String download(String siteUrl, String language) throws IOException, ReCaptchaException  {
            return makeRequest(siteUrl);
        }

        @Override
        public String download(String siteUrl, Map<String, String> customProperties) throws IOException, ReCaptchaException {
            return makeRequest(siteUrl);
        }

        @Override
        public String download(String siteUrl) throws IOException, ReCaptchaException {
            return makeRequest(siteUrl);
        }
    };
    public MusicManager () {
        NewPipe.init(downloader);
    }
    public void createTree(String playlistUrl) throws IOException, ExtractionException {
        YoutubePlaylistExtractor extractor = new YoutubePlaylistExtractor(
                service,
                playlistUrl
        );
        extractor.fetchPage();
        System.out.println("Pulling: "+extractor.getName());
        System.out.println("By: "+extractor.getUploaderName());
        System.out.println();
        List<StreamInfoItem> streams = extractor.getInitialPage().getItems();
        for(StreamInfoItem infoItem : streams) {
            System.out.println(infoItem.getName());
            tree.insert(new SongNode(infoItem));
        }
    }
    public void playItem(SongNode node) {
    	StreamInfoItem i = node.item;
        try {
            System.out.println("Opening: "+i.getName());
            System.out.println("By: "+i.getUploaderName());
            System.out.println(i.getUrl());
            YoutubeStreamExtractor extractor = new YoutubeStreamExtractor(
                service,
                i.getUrl()
            );
            extractor.fetchPage();
            //URI toLoad = new URI(extractor.getVideoStreams().get(0).getUrl());
            Desktop.getDesktop().browse(new URI(extractor.getVideoStreams().get(0).url));
            //Desktop.getDesktop().browse(new URI(extractor.getAudioStreams().get(0).url));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        catch (ExtractionException e) {
            e.printStackTrace();
        }
    }
    public List<SongNode> search(String s) {
        LinkedList<SongNode> closest = new LinkedList<>();
        AVLNode key = new SongNode(s);

        AVLNode activeNode = tree.getRoot();
        if (activeNode == null) {
	    	return closest;
	    }
	    while (activeNode != null) {
		    closest.addFirst((SongNode)activeNode);
		    if (key.compareTo(activeNode) == 0) {
			    break;
	    	}
	    	else if (activeNode.compareTo(key) <= -1) {
	    		activeNode = activeNode.getRight();
	    	}
	    	else {
	    		activeNode = activeNode.getLeft();
		    }
	    }
        return closest;
    }
}
