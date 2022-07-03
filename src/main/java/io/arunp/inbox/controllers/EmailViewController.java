package io.arunp.inbox.controllers;

import io.arunp.inbox.email.Email;
import io.arunp.inbox.email.EmailRepository;
import io.arunp.inbox.folders.Folder;
import io.arunp.inbox.folders.FolderRepository;
import io.arunp.inbox.folders.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    private EmailRepository emailRepository;

    @GetMapping(value = "/emails/{id}")
    public String emailViewPage(@PathVariable UUID id, @AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";
        String userId = principal.getAttribute("login");

        // Fetch Folders
        List<Folder> userFolders = folderRepository.findAllByUserId(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);

        // Fetch Email
        Optional<Email> optionalEmail = emailRepository.findById(id);
        if(optionalEmail.isEmpty())
            return "inbox-page";
        Email email = optionalEmail.get();
        String toIDs = String.join(",",email.getTo());
        model.addAttribute("email", email);
        model.addAttribute("toIDs", toIDs);
        return "email-page";
    }
}
