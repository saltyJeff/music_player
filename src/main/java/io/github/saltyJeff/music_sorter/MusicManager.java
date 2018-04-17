package io.github.saltyJeff.music_sorter;

import org.schabi.newpipe.extractor.Downloader;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.exceptions.ReCaptchaException;
import org.schabi.newpipe.extractor.services.youtube.YoutubePlaylistExtractor;
import org.schabi.newpipe.extractor.services.youtube.YoutubePlaylistInfoItemExtractor;
import org.schabi.newpipe.extractor.services.youtube.YoutubeService;
import org.schabi.newpipe.extractor.services.youtube.YoutubeStreamExtractor;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItemCollector;
import org.schabi.newpipe.extractor.stream.StreamInfoItemExtractor;
import org.schabi.newpipe.extractor.stream.VideoStream;
import org.zorvan.avl.AVLNode;
import org.zorvan.avl.AVLTree;

import javax.print.DocFlavor;
import javax.print.URIException;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MusicManager {
    public AVLTree tree = new AVLTree();
    private StreamingService service = new YoutubeService(1, "yt");
    private String httpDownload(String u) {
        try {
            URL url = new URL(u);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.setReadTimeout(1000);
            BufferedReader breader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while((line = breader.readLine()) != null) {
                stringBuilder.append(line);
            }
            breader.close();
            return stringBuilder.toString();
        }
        catch(IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    private Downloader downloader = new Downloader() {
        @Override
        public String download(String siteUrl, String language) throws IOException, ReCaptchaException {
            return httpDownload(siteUrl);
        }

        @Override
        public String download(String siteUrl, Map<String, String> customProperties) throws IOException, ReCaptchaException {
            return httpDownload(siteUrl);
        }

        @Override
        public String download(String siteUrl) throws IOException, ReCaptchaException {
            return httpDownload(siteUrl);
        }
    };
    public MusicManager () {
        NewPipe.init(downloader);
    }
    public void createTree(String playlistUrl) throws IOException, ExtractionException {
        YoutubePlaylistExtractor extractor = new YoutubePlaylistExtractor(
                service,
                playlistUrl,
                playlistUrl
        );
        extractor.onFetchPage(downloader);
        System.out.println("Pulling: "+extractor.getName());
        System.out.println("By: "+extractor.getUploaderName());
        System.out.println();
        StreamInfoItemCollector streamInfoItemCollector = extractor.getStreams();
        for(StreamInfoItem infoItem : streamInfoItemCollector.getStreamInfoItemList()) {
            System.out.println(infoItem.getName());
            tree.insert(new SongNode(infoItem));
        }
    }
    public void playItem(SongNode node) {
    	StreamInfoItem i = node.item;
        try {
            System.out.println("Opening: "+i.getName());
            System.out.println("By: "+i.getUploaderName());
            YoutubeStreamExtractor extractor = new YoutubeStreamExtractor(
                service,
                i.getUrl()
            );
            extractor.onFetchPage(downloader);
            extractor.fetchPage();
            URI toLoad = new URI(extractor.getVideoStreams().get(0).getUrl());
            Desktop.getDesktop().browse(toLoad);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (ExtractionException e) {
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
