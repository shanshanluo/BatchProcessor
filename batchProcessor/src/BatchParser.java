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


public class BatchParser
{
	private Batch batch;
	
	public Batch buildBatch(File batchFile) throws ProcessException
	{
		try {
			batch = new Batch();
			
			FileInputStream fis = new FileInputStream(batchFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fis);
			
			Element pnode = doc.getDocumentElement();
			NodeList nodes = pnode.getChildNodes();
			int num = nodes.getLength();
			for (int idx = 0; idx < nodes.getLength(); idx++) {
				Node node = nodes.item(idx);
				int type = node.getNodeType();
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					Command cmd = buildCommand(elem);
					batch.addCommands(cmd);
				}
			}
		}
		
		catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		return batch;
	}

	
	private Command buildCommand(Element elem) throws ProcessException
	{
		Command Cmd;
		
		String cmdName = elem.getNodeName();
		
		if (cmdName == null) {
			throw new ProcessException("unable to parse command from " + elem.getTextContent());
		}
		else if ("wd".equalsIgnoreCase(cmdName)) {
			System.out.println("Parsing wd");
			Cmd = new WDCommand();
		}
		else if ("file".equalsIgnoreCase(cmdName)) {
			System.out.println("Parsing file");
			Cmd = new FileCommand();
		}
		else if ("cmd".equalsIgnoreCase(cmdName)) {
			System.out.println("Parsing cmd");
			Cmd = new CmdCommand();
		}
		else if ("pipe".equalsIgnoreCase(cmdName)) {
			System.out.println("Parsing pipe");
			Cmd = new PipeCommand();
		}
		else {
			throw new ProcessException("Unknown command " + cmdName + " from: " + elem.getBaseURI());
		}
		
		Cmd.parse(elem);
		
		return Cmd;
	}
}

