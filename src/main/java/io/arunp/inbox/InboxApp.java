package io.arunp.inbox;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.arunp.inbox.email.Email;
import io.arunp.inbox.email.EmailRepository;
import io.arunp.inbox.email.EmailService;
import io.arunp.inbox.emailslist.EmailListItem;
import io.arunp.inbox.emailslist.EmailListItemKey;
import io.arunp.inbox.emailslist.EmailListItemRepository;
import io.arunp.inbox.folders.Folder;
import io.arunp.inbox.folders.FolderRepository;
import io.arunp.inbox.folders.UnreadEmailStatsRepository;
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
	private EmailService emailService;

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
		folderRepository.save(new Folder("arunp3441","Work","blue"));
		folderRepository.save(new Folder("arunp3441","Home","green"));
		folderRepository.save(new Folder("arunp3441","Family","yellow"));

		for(int i=1; i<=10;++i){
			emailService.sendEmail("arunpp26", Arrays.asList("arunp3441","akhilp9790","sreepriyap"),
					"Hello "+i,"Hope you are doing good");
		}
		emailService.sendEmail("arunpp26", Arrays.asList("akhilp9790","sreepriyap"),
				"Hello","Hope you are doing good");
	}
}
