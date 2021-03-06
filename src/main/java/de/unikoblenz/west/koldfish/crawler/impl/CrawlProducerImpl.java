package de.unikoblenz.west.koldfish.crawler.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.jena.iri.IRIException;
import org.apache.jena.iri.IRIFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unikoblenz.west.koldfish.crawler.CrawlProducer;
import de.unikoblenz.west.koldfish.crawler.CrawlQueue;
import de.unikoblenz.west.koldfish.crawler.impl.messages.CrawlIri;
import de.unikoblenz.west.koldfish.dam.impl.messages.ModelReportMessage;
import de.unikoblenz.west.koldfish.dam.messages.ReportMessage;

/**
 * Receiver implementation that can listen to a Negotiator to create CrawlMessages from incoming information.
 * @author lkastler@uni-koblenz.de
 *
 */
public class CrawlProducerImpl implements CrawlProducer {

	private static final Logger log = LoggerFactory.getLogger(CrawlProducerImpl.class);
	
	private final Executor exec = Executors.newCachedThreadPool();
	private final IRIFactory fac = IRIFactory.iriImplementation();
	private final CrawlQueue queue;
	
	
	/**
	 * creates new CrawlProducerImpl object, binding it to the given CrawlQueue.
	 * @param queue - bind CrawlProducerImpl to this CrawlQueue.
	 */
	public CrawlProducerImpl(CrawlQueue queue) {
		this.queue = queue;
	}

	@Override
	public void report(ReportMessage<?> rm) {
		exec.execute(new Runnable() {

			@Override
			public void run() {
				if(rm instanceof ModelReportMessage) {
					ModelReportMessage mrm = (ModelReportMessage)rm;
					log.debug("got message: " + mrm);
					
					NodeIterator it = mrm.getPayload().listObjects();
					
					while(it.hasNext()) {
						RDFNode r = it.next();
						
						try {
							if(r.isURIResource()) {
								queue.add(new CrawlIri(fac.construct(r.asResource().getURI())));
							}
						}catch(IRIException e) {
							log.warn(e.getLocalizedMessage(),e);
						}
					}
					
					it.close();
				}
			}
			
		});
	}

	@Override
	public void start() throws Exception {
		log.debug("start");
	}

	@Override
	public void terminate() throws Exception {
		log.debug("terminate");
	}

	

}
