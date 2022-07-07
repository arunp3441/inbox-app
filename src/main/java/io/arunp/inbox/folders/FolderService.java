package io.arunp.inbox.folders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    public List<Folder> fetchDefaultFolders(String userId) {
        return Arrays.asList(
                new Folder(userId, "Inbox", "white"),
                new Folder(userId, "Sent Items", "green"),
                new Folder(userId, "Important", "red")
        );
    }

    public Map<String,Integer> mapCountToLabel(String userId){
        List<UnreadEmailStats> stats = unreadEmailStatsRepository.findAllByUserId(userId);
        return stats.stream().collect(Collectors.toMap(UnreadEmailStats::getLabel,UnreadEmailStats::getUnreadCount));
    }
}
