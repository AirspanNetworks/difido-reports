package il.co.topq.report.business.archiver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import il.co.topq.report.Configuration;
import il.co.topq.report.Configuration.ConfigProps;
import il.co.topq.report.business.execution.ExecutionMetadata;
import il.co.topq.report.business.execution.MetadataPersistency;

@Component
public class ReportsArchiver implements Archiver {

	private final Logger log = LoggerFactory.getLogger(ReportsArchiver.class);

	private MetadataPersistency persistency;

	private boolean enabled = true;

	private ArchiverHttpClient client;

	@Autowired
	public ReportsArchiver(MetadataPersistency persistency) {
		this.persistency = persistency;
		this.client = new ArchiverHttpClient(Configuration.INSTANCE.readString(ConfigProps.ARCHIVER_DIFIDO_SERVER));
	}

	@Override
	public void archive() {
		if (!enabled) {
			return;
		}

		Map<Integer, ExecutionMetadata> remoteExecutions = client.get("/reports/meta.json",
				new TypeReference<Map<Integer, ExecutionMetadata>>() {
				});

		//@formatter:off
		List<ExecutionMetadata> executionsToArchive = remoteExecutions.values()
				.parallelStream()
				.filter(e -> !e.isActive())
				.filter(el -> persistency.getAll()
						.stream()
						.noneMatch(er -> er.getId() == el.getId()))
				.collect(Collectors.toList());
		
		executionsToArchive.forEach(e -> {e.setDirty(true); persistency.add(e);});
		executionsToArchive.forEach(e -> client.getFile("/api/reports/" + e.getId(), "execution_" + e.getId() + ".zip"));
		//@formatter:on

		// Get the meta.json from Difido
		// Remove all the executions that were already archived
		// search for all the executions that are older then the one configured
		// Copy as ZIP files all the HTML reports and extract to the appropriate
		// folder
		// Make sure that the zip reports were copied successfully.
		// Update the local meta.json with the new reports
		//
		//
	}


}
