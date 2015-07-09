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


public class BatchProcessor
{
	
	public static void main(String[] args)
	{
		try{
			String s = args[0];
			File bf = new File (s);
		
			BatchParser parser = new BatchParser();
		
			Batch batch1 = parser.buildBatch(bf);
			executeBatch(batch1);
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public static void executeBatch(Batch batch) throws ProcessException
	{
		int idx;
		List<Command> cmdList = batch.getCommands();
		
		for(idx = 0; idx < cmdList.size(); idx++){
			Command cmd = cmdList.get(idx);
			cmd.execute(batch);
		}	
	}
	
}
	