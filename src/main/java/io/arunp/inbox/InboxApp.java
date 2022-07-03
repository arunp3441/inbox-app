package io.arunp.inbox;

import java.nio.file.Path;
import java.util.Arrays;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.arunp.inbox.emailslist.EmailListItem;
import io.arunp.inbox.emailslist.EmailListItemKey;
import io.arunp.inbox.emailslist.EmailListItemRepository;
import io.arunp.inbox.folders.Folder;
import io.arunp.inbox.folders.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@SpringBootApplication
@RestController
public class InboxApp {

	@Autowired
	private FolderRepository folderRepository;

	@Autowired
	private EmailListItemRepository emailListItemRepository;

	public static void main(String[] args) {
		SpringApplication.run(InboxApp.class, args);
	}

	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@PostConstruct
	public void init(){
		folderRepository.save(new Folder("arunp3441","Inbox","blue"));
		folderRepository.save(new Folder("arunp3441","Sent","green"));
		folderRepository.save(new Folder("arunp3441","Important","yellow"));
		for(int i=0; i<=10;++i){
			EmailListItemKey key = new EmailListItemKey();
			key.setUserId("arunp3441");
			key.setLabel("Inbox");
			key.setTimeUUID(Uuids.timeBased());

			EmailListItem item = new EmailListItem();
			item.setKey(key);
			item.setTo(Arrays.asList("arunpp26"));
			item.setSubject("Subject "+i);
			item.setUnread(true);

			emailListItemRepository.save(item);
		}
	}
}
