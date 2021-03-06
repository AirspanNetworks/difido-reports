package il.co.topq.report.front.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.RestController;

import il.co.topq.difido.model.test.TestDetails;
import il.co.topq.report.business.execution.ExecutionMetadata;
import il.co.topq.report.business.execution.MetadataProvider;
import il.co.topq.report.events.TestDetailsCreatedEvent;

@RestController
@Path("api/executions/{execution}/details")
public class TestDetailsResource {

	private final Logger log = LoggerFactory.getLogger(TestDetailsResource.class);

	private final ApplicationEventPublisher publisher;

	private final MetadataProvider metadataProvider;

	@Autowired
	public TestDetailsResource(ApplicationEventPublisher publisher, MetadataProvider metadataProvider) {
		super();
		this.publisher = publisher;
		this.metadataProvider = metadataProvider;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void post(@PathParam("execution") int executionId, TestDetails details) {
		log.debug("POST - Add execution details to execution with id " + executionId);
		if (null == details) {
			log.error("Details can't be null");
			throw new WebApplicationException("Details can't be null");
		}
		ExecutionMetadata metadata = metadataProvider.getMetadata(executionId);
		if (null == metadata) {
			log.error("Can't update test details for execution " + executionId + " which is null");
		}
		publisher.publishEvent(new TestDetailsCreatedEvent(metadata, details));
	}

}
