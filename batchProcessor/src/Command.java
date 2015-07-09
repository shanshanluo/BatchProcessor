//package src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Command
{
	public void describe()
	{
	}
	
	public void execute(Batch batch) throws ProcessException
	{		
	}
	
	public void parse(Element element) throws ProcessException
	{
	}
}

class WDCommand extends Command {
	private String id;
	private String path;
	
	public void describe()
	{
		System.out.println("-execution of WD command done.");
	}
	
	public void parse(Element element) throws ProcessException
	{
		id = element.getAttribute("id");
		if (id == null || id.isEmpty()) {
			throw new ProcessException("Missing ID in WD Command");
		}
		
		path = element.getAttribute("path");
		if (path == null || path.isEmpty()) {
			throw new ProcessException("Missing PATH in WD Command");
		}
		
		System.out.format("-Parse %s done.\n", id);
	}		
	
	public void execute(Batch batch) throws ProcessException
	{
		batch.setWorkingDir(path);
		describe();
	}		
}

class FileCommand extends Command {
	private String id;
	private String path;
	
	public void describe()
	{
		System.out.format("-execution of %s done.\n", id);
	}
	
	public void parse(Element element) throws ProcessException
	{
		id = element.getAttribute("id");
		if (id == null || id.isEmpty()) {
			throw new ProcessException("Missing ID in FILE Command");
		}
		
		path = element.getAttribute("path");
		if (path == null || path.isEmpty()) {
			throw new ProcessException("Missing PATH in FILE Command");
		}
		
		System.out.format("-Parse %s done.\n", id);
	}	
	
	public void execute(Batch batch) throws ProcessException
	{
		batch.addFileInfo(id, path);
		describe();
	}
}

class CmdCommand extends Command {
	private String id;
	private String cmd;
	private List<String> cmdArgs;
	private String inID;
	private String outID;
	
	public void describe()
	{
		System.out.format("-execution of %s done.\n", id);
	}
	 
	public void parse(Element element) throws ProcessException
	{
		id = element.getAttribute("id");
		if (id == null || id.isEmpty()) {
			throw new ProcessException("Missing ID in CMD Command");
		}
		
		cmd = element.getAttribute("path");
		if (cmd == null || cmd.isEmpty()) {
			throw new ProcessException("Missing PATH in CMD Command");
		}
		
		cmdArgs = new ArrayList<String>();
		String arg = element.getAttribute("args");
		if (!(arg == null || arg.isEmpty())) {
			StringTokenizer st = new StringTokenizer(arg);
			while (st.hasMoreTokens()) {
				String tok = st.nextToken();
				cmdArgs.add(tok);
			}
		}
		
		inID = element.getAttribute("in");
		if (inID == null || inID.isEmpty()) {
			System.out.println("inID is NULL!");
		}

		outID = element.getAttribute("out");
		if (outID == null || outID.isEmpty()) {
			System.out.println("outID is NULL!");
		}
		
		System.out.format("-Parse %s done.\n", id);
	}	
	
	public void execute(Batch batch) throws ProcessException
	{		
		List<String> cmdL = new ArrayList<String>();
		
		cmdL.add(cmd);
		for(int idx=0; idx<cmdArgs.size(); idx++)
			cmdL.add(cmdArgs.get(idx));
		
		int fileNum = batch.getOPFileNum();					
		
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(cmdL);
		
		builder.directory(new File(batch.getWorkingDir()));	
		// error file path
		builder.redirectError(new File(batch.getWorkingDir() + File.separator +"error.txt"));
						
		int idx = 0;
			
		try {
			// add input file path
			if(!(inID == null || inID.isEmpty())){
				while(idx < fileNum){
					if(!(inID.matches(batch.getOPFileID(idx))))
						idx++;
					else{
						builder.redirectInput(new File(batch.getWorkingDir() + File.separator +batch.getOPFileName(idx)));
						break;
					}
				}
				if(idx >= fileNum)
					throw new ProcessException(String.format("input file '%s' doesn't exist. Failed to execute '%s'!\n", inID, id));
			}
			
			idx = 0;
			// add output file path 
			if(!(outID == null || outID.isEmpty())){
				while(idx < fileNum){
					if(!(outID.matches(batch.getOPFileID(idx)))) 
						idx++;
					else{
						builder.redirectOutput(new File(batch.getWorkingDir() + File.separator +batch.getOPFileName(idx)));
						break;
					}
				}				
				if(idx >= fileNum)
					throw new ProcessException(String.format("output file '%s' doesn't exist. Failed to execute '%s'!\n", outID, id));
			}
						
			Process process = builder.start();
			process.waitFor();
		
			describe();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}	
}

class PipeCommand extends Command {
	private String id;
	private List<PipeCmdCommand> pipeCmd;
	
	public PipeCommand()
	{
		pipeCmd = new ArrayList<PipeCmdCommand>();
	}
	public void describe()
	{
		System.out.format("-execution of %s done.\n", id);
	}
	
	public void parse(Element element) throws ProcessException
	{
		id = element.getAttribute("id");
		if (id == null || id.isEmpty()) {
			throw new ProcessException("Missing ID in PIPE Command");
		}
		
		NodeList nodes = element.getChildNodes();
		for (int idx = 0; idx < nodes.getLength(); idx++) {
			Node node = nodes.item(idx);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) node;
				PipeCmdCommand cmd = new PipeCmdCommand();
				cmd.parse(elem);
				pipeCmd.add(cmd);				
			}
		}
			
		System.out.format("-Parse %s done.\n", id);		
	}	
	
	public void execute(Batch batch) throws ProcessException
	{

		try{
			PipeCmdCommand cmd1 = pipeCmd.get(0);
			ProcessBuilder pb1 = cmd1.pipeCmdExe(batch);
		
			PipeCmdCommand cmd2 = pipeCmd.get(1);
			ProcessBuilder pb2 = cmd2.pipeCmdExe(batch);
				
			Process process2 = pb2.start();
			Process process1 = pb1.start();
			
			OutputStream os = process2.getOutputStream();
			InputStream is = process1.getInputStream();		
		
			int achar;
			while ((achar = is.read()) != -1) {
				os.write(achar);
			}
			os.close();
		
			process1.waitFor();
			process2.waitFor();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		describe();	
	}	
}

class PipeCmdCommand extends Command {
	private String id;
	private String cmd;
	private List<String> cmdArgs;
	private String inId;
	private String outId;
	
	public void describe()
	{
		System.out.format("-execution of %s done.\n", id);
	}
	
	public void parse(Element element) throws ProcessException
	{
		id = element.getAttribute("id");
		if (id == null || id.isEmpty()) {
			throw new ProcessException("Missing ID in CMD Command");
		}
		
		cmd = element.getAttribute("path");
		if (cmd == null || cmd.isEmpty()) {
			throw new ProcessException("Missing PATH in CMD Command");
		}
		
		cmdArgs = new ArrayList<String>();
		String arg = element.getAttribute("args");
		if (!(arg == null || arg.isEmpty())) {
			StringTokenizer st = new StringTokenizer(arg);
			while (st.hasMoreTokens()) {
				String tok = st.nextToken();
				cmdArgs.add(tok);
			}
		}
		
		inId = element.getAttribute("in");
		if (inId == null || inId.isEmpty()) {
			inId = null;
			System.out.println("input from pipe!");
		} else {
			System.out.format("input from file '%s'.\n", inId);
		}

		outId = element.getAttribute("out");
		if (outId == null || outId.isEmpty()) {
			outId = null;
			System.out.println("output to pipe!");
		}else {
			System.out.format("output to file '%s'.\n", outId);
		}
		
		System.out.format("-Parse pipecmd '%s' done.\n", id);
	}
	
	public ProcessBuilder pipeCmdExe(Batch batch) throws ProcessException
	{
		List<String> cmdL = new ArrayList<String>();
		
		cmdL.add(cmd);
		for(int idx=0; idx<cmdArgs.size(); idx++)
			cmdL.add(cmdArgs.get(idx));
		
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(cmdL);
		
		builder.directory(new File(batch.getWorkingDir()));	
		// error file path
		builder.redirectError(new File(batch.getWorkingDir() + File.separator +"error.txt"));
		
		// redirect input if needed
		int idx = 0;
		int fileNum = batch.getOPFileNum();
		if(!(inId == null || inId.isEmpty())){
			while(idx < fileNum){
				if(!(inId.matches(batch.getOPFileID(idx)))) 
					idx++;
				else{
					builder.redirectInput(new File(batch.getWorkingDir() + File.separator + batch.getOPFileName(idx)));
					break;
				}
			}				
			if(idx >= fileNum)
				throw new ProcessException(String.format("output file '%s' doesn't exist. Failed to execute '%s'!\n", inId, id));
		}
		
		//redirect output if needed
		idx = 0;
		if(!(outId == null || outId.isEmpty())){
			while(idx < fileNum){
				if(!(outId.matches(batch.getOPFileID(idx)))) 
					idx++;
				else{
					builder.redirectOutput(new File(batch.getWorkingDir() + File.separator +batch.getOPFileName(idx)));
					break;
				}
			}				
			if(idx >= fileNum)
				throw new ProcessException(String.format("output file '%s' doesn't exist. Failed to execute '%s'!\n", outId, id));
		}
		describe();	
		
		return builder;
	}
}
