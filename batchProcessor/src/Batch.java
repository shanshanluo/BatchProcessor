//package src;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Batch
{
	private String workingDir;
	private List<Command> cmdList;
	private List<OPFile> fileList;
	
	public Batch()
	{
		cmdList = new ArrayList<Command>();
		fileList = new ArrayList<OPFile>();
	}
	
	public void addCommands(Command command)
	{
		cmdList.add(command);
	}
	
	public String getWorkingDir() throws ProcessException
	{
		return workingDir;
	}
	
	public List<Command> getCommands()
	{
		return cmdList;
	}
	
	public void setWorkingDir(String dir)
	{
		workingDir = dir;
	}
	
	public void addFileInfo(String id, String name) 
	{
		OPFile f = new OPFile();
		f.fileID = id;
		f.fileName = name;
		fileList.add(f);
	}
	
	public int getOPFileNum()
	{
		return fileList.size();
	}

	public String getOPFileID(int idx)
	{
		return fileList.get(idx).fileID;
	}
	
	public String getOPFileName(int idx)
	{
		return fileList.get(idx).fileName;
	}
}


class OPFile{
	String fileID;
	String fileName;
}
	