package io.arunp.inbox;

import java.nio.file.Path;
import java.util.List;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.arunp.inbox.email.Email;
import io.arunp.inbox.email.EmailRepository;
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

	@Autowired
	private EmailRepository emailRepository;

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
			item.setTo(List.of("arunpp26","akhilp","sreepriyap"));
			item.setSubject("Subject "+i);
			item.setUnread(true);

			Email email = new Email();
			email.setID(key.getTimeUUID());
			email.setSubject(item.getSubject());
			email.setFrom("arunp3441");
			email.setTo(item.getTo());
			email.setBody("Body "+i);

			emailRepository.save(email);
			emailListItemRepository.save(item);
		}
	}
}
