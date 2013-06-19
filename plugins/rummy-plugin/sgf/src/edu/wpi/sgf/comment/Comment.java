package edu.wpi.sgf.comment;

import java.util.ArrayList;
import java.util.List;

public class Comment {

	String type = "generic"; //generic or game specific	
	int who; //1 for the user, 2 for the agent
	private String content; 
	private List<String> tags = new ArrayList<String>();
	
	public Comment(String content, List<String> tags, int who){
		this.content = content;
		for(String tag : tags)
			this.tags.add(tag);
		this.who = who; //1:user, 2:agent
	}

	public Comment(String content){
		this.content = content;
	}
	
	public String getContent(){
		return content;
	}
	
	public List<String> getTags(){
		return tags;
	}
	
	public void addTag(String tag){
		tags.add(tag);
	}
	
}
