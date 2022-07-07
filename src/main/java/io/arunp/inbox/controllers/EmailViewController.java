package io.arunp.inbox.controllers;

import io.arunp.inbox.email.Email;
import io.arunp.inbox.email.EmailRepository;
import io.arunp.inbox.email.EmailService;
import io.arunp.inbox.emailslist.EmailListItem;
import io.arunp.inbox.emailslist.EmailListItemKey;
import io.arunp.inbox.emailslist.EmailListItemRepository;
import io.arunp.inbox.folders.Folder;
import io.arunp.inbox.folders.FolderRepository;
import io.arunp.inbox.folders.FolderService;
import io.arunp.inbox.folders.UnreadEmailStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class EmailViewController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @Autowired
    private UnreadEmailStatsRepository unreadEmailStatsRepository;

    @GetMapping(value = "/emails/{id}")
    public String emailViewPage(@RequestParam String folder, @PathVariable UUID id, @AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";
        String userId = principal.getAttribute("login");

        // Fetch Folders
        List<Folder> userFolders = folderRepository.findAllByUserId(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);
        model.addAttribute("userName",principal.getAttribute("name"));

        // Fetch Email
        Optional<Email> optionalEmail = emailRepository.findById(id);
        if(optionalEmail.isEmpty())
            return "redirect:/";
        Email email = optionalEmail.get();
        String toIDs = String.join(",",email.getTo());

        //Check whether user is allowed to view this email
        assert userId != null;
        if(!emailService.doesHaveAccess(email,userId))
            return "redirect:/";

        model.addAttribute("email", email);
        model.addAttribute("id", email.getID());
        model.addAttribute("toIDs", toIDs);

        EmailListItemKey key = new EmailListItemKey();
        key.setUserId(userId);
        key.setLabel(folder);
        key.setTimeUUID(email.getID());

        Optional<EmailListItem> optionalEmailListItem = emailListItemRepository.findById(key);
        if(optionalEmailListItem.isPresent()){
            EmailListItem emailListItem = optionalEmailListItem.get();
            if(emailListItem.isUnread()){
                emailListItem.setUnread(false);
                emailListItemRepository.save(emailListItem);
                unreadEmailStatsRepository.decrementUnreadCount(userId,folder);
            }
        }
        model.addAttribute("stats",folderService.mapCountToLabel(userId));

        return "email-page";
    }
}
