package edu.wpi.sgf.comment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class CommentLibraryHandler {

	private final static String CommentLibraryFile =
			"genericCommentLibrary.xml";
	
	private List<Comment> allComments = new ArrayList<Comment>();

	public CommentLibraryHandler(){

		SAXBuilder builder = new SAXBuilder();
		File genericFile = new File(CommentLibraryFile);
		
		try {
			
			Document xmldoc = (Document) builder.build(genericFile);
			Element rootNode = xmldoc.getRootElement();
			List<Element> retrievedCommentsFromFile = rootNode.getChildren("comment");
			List<String> tempTagsList = new ArrayList<String>();
			int who = 0;
			
			for(Element cm : retrievedCommentsFromFile){
			   who = 0;
				tempTagsList.clear();
				for(Attribute att : cm.getAttributes()){
					if(att.getValue().trim().contains("T") 
							&& !att.getName().trim().contains("usr") 
								&& !att.getName().trim().contains("own"))
						tempTagsList.add(att.getName().trim());
					if(att.getName().trim().contains("usr") 
							&& att.getValue().trim().contains("T"))
						who = 1 + who;
					if(att.getName().trim().contains("own") 
							&& att.getValue().trim().contains("T"))
						who = 2 + who;
					//who = 3 if for both usr and agent
				}
				allComments.add(new Comment(cm.getText().trim(), tempTagsList, 0, 1, who));
			}
			
		} catch (JDOMException | IOException e) {
			System.out.println("Library load error.");
			e.printStackTrace();
		}
	}

	public List<Comment> retrieveAllCommentsFromLibrary(){
		return allComments;
	}

	private void visualize() {
		for(Comment cm : allComments){
			System.out.print("\n" + cm.getContent() + " -- TAGS: ");
			for(String tag : cm.getTags())
				System.out.print(tag + " - ");
			System.out.print(" >>> " + cm.who);
		}
	}

	public static void main(String[] args) {
		new CommentLibraryHandler().visualize();
	}

}




