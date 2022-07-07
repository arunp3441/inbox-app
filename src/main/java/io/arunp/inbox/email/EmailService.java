package io.arunp.inbox.email;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.arunp.inbox.emailslist.EmailListItem;
import io.arunp.inbox.emailslist.EmailListItemKey;
import io.arunp.inbox.emailslist.EmailListItemRepository;
import io.arunp.inbox.folders.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public void sendEmail(String from, List<String> to, String subject, String body){
        Email email = new Email();
        email.setTo(to);
        email.setFrom(from);
        email.setSubject(subject);
        email.setBody(body);
        email.setID(Uuids.timeBased());
        emailRepository.save(email);

        to.forEach(toId -> {
            EmailListItem emailListItem = createEmailListItem(to, subject, email, toId, "Inbox", true);
            emailListItemRepository.save(emailListItem);
            unreadEmailStatsRepository.incrementUnreadCount(toId,"Inbox");
        });

        EmailListItem sendEmailListItem = createEmailListItem(to, subject, email, from, "Sent Items", false);
        emailListItemRepository.save(sendEmailListItem);
    }

    private EmailListItem createEmailListItem(List<String> to, String subject, Email email, String userId
            ,String label,boolean isUnread) {
        EmailListItemKey emailListItemKey = new EmailListItemKey();
        emailListItemKey.setTimeUUID(email.getID());
        emailListItemKey.setUserId(userId);
        emailListItemKey.setLabel(label);
        EmailListItem emailListItem = new EmailListItem();
        emailListItem.setUnread(isUnread);
        emailListItem.setKey(emailListItemKey);
        emailListItem.setSubject(subject);
        emailListItem.setTo(to);
        return emailListItem;
    }

    public boolean doesHaveAccess(Email email,String userId){
        return (userId.equals(email.getFrom()) || email.getTo().contains(userId));
    }

    public String getReplySubject(String subject){
        return "RE: "+subject;
    }

    public String getReplyBody(Email email){
        return "\n\n\n------------------------------------------------------------------------\n" +
                "From : " + email.getFrom() + "\nTo : " + email.getTo() + "\n"
                + "Subject : " + email.getSubject() + "\n\n" + email.getBody();

    }
}
