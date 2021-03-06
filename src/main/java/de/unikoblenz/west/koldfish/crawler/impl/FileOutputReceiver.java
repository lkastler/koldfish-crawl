/**
 * 
 */
package de.unikoblenz.west.koldfish.crawler.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.koldfish.dam.Receiver;
import de.unikoblenz.west.koldfish.dam.impl.messages.ModelReportMessage;
import de.unikoblenz.west.koldfish.dam.messages.ReportMessage;

/**
 * @author lkastler@uni-koblenz.de
 *
 */
public class FileOutputReceiver implements Receiver {

	private final File file;
	private final RDFFormat format;
	
	private static final Logger log = LoggerFactory.getLogger(FileOutputReceiver.class);
	
	public FileOutputReceiver(String fileName, RDFFormat format) {
		log.debug("creating: " + fileName);
		file = new File(fileName);
		this.format = format;
	}
	
	public FileOutputReceiver(String fileName) {
		this(fileName, RDFFormat.TTL);
	}
	
	/* (non-Javadoc)
	 * @see de.unikoblenz.west.koldfish.dam.Receiver#report(de.unikoblenz.west.koldfish.dam.messages.ReportMessage)
	 */
	@Override
	public void report(ReportMessage<?> rm) {
		if(rm instanceof ModelReportMessage) {
			try {
				OutputStream out = new FileOutputStream(file, true);
				Model m = ((ModelReportMessage)rm).getPayload();
				
				RDFDataMgr.write(out, m, format);
				
				out.flush();
				out.close();
			} catch (IOException e) {
				log.error(e.toString(),e);
			}
		}
	}

}
