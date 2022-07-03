package io.arunp.inbox.controllers;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.arunp.inbox.emailslist.EmailListItem;
import io.arunp.inbox.emailslist.EmailListItemRepository;
import io.arunp.inbox.folders.Folder;
import io.arunp.inbox.folders.FolderRepository;
import io.arunp.inbox.folders.FolderService;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class InboxController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailListItemRepository emailListItemRepository;

    @GetMapping(value = "/")
    public String homePage(@AuthenticationPrincipal OAuth2User principal, Model model){
        if(principal ==  null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";
        String userId = principal.getAttribute("login");

        // Fetch Folders
        List<Folder> userFolders = folderRepository.findAllByUserId(userId);
        model.addAttribute("userFolders" , userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders" , defaultFolders);

        //Fetch emails

        List<EmailListItem> emailList = emailListItemRepository.findAllByKey_UserIdAndKey_Label(userId,"Inbox");
        model.addAttribute("emailList" , emailList);

        PrettyTime p = new PrettyTime();
        emailList.stream().forEach(email -> {
            UUID timeUUID = email.getKey().getTimeUUID();
            Date emailDateTime = new Date(Uuids.unixTimestamp(timeUUID));
            email.setAgoTimeString(p.format(emailDateTime));
        });

        return "inbox-page";
    }
}
