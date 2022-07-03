package io.arunp.inbox.emailslist;

import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface EmailListItemRepository extends CassandraRepository<EmailListItem,EmailListItemKey> {

    List<EmailListItem> findAllByKey_UserIdAndKey_Label(String userId,String label);
}
