package edu.wpi.always.user;

import java.io.*;

public class UserUtil {
	public static final File userHomeDir = new File(System.getProperty("user.home"));
	public static final File userProgramDataRoot = new File(userHomeDir, "AlwaysOn");
	static{
		userProgramDataRoot.mkdirs();
		System.out.println("storing user data at "+userProgramDataRoot.getPath());
	}
	public static File getUserFile(String path){
		return new File(userProgramDataRoot, path);
	}
}
