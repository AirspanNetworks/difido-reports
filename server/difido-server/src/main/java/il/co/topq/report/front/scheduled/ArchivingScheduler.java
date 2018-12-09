package il.co.topq.report.front.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import il.co.topq.report.Configuration;
import il.co.topq.report.Configuration.ConfigProps;
import il.co.topq.report.business.archiver.Archiver;

@Component
public class ArchivingScheduler {

	private final static Logger log = LoggerFactory.getLogger(ArchivingScheduler.class);
	
	private static boolean enabled;
	
	@Autowired
	private Archiver archiver;
	
	static {
		// In the constructor?
		enabled = Configuration.INSTANCE.readBoolean(ConfigProps.ARCHIVER_ENABLED);
	}
	
	@Scheduled(fixedRate = 3000)
	public void setExecutionsToNotActive() {
		if (!enabled) {
			return;
		}
		archiver.archive();
		// Get the meta.json from Difido
		// Remove all the executions that were already archived
		// search for all the executions that are older then the one configured
		// Copy as ZIP files all the HTML reports and extract to the appropriate folder
		// Make sure that the zip reports were copied successfully.
		// Update the local meta.json with the new reports
		// 
		// 
	}


}
