package io.arunp.inbox.controllers;

import io.arunp.inbox.email.EmailService;
import io.arunp.inbox.folders.Folder;
import io.arunp.inbox.folders.FolderRepository;
import io.arunp.inbox.folders.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ComposeController {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FolderService folderService;

    @Autowired
    private EmailService emailService;

    @GetMapping(value="/compose")
    public String getComposeMessage(@RequestParam(required = false) String to, @AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return "index";
        String userId = principal.getAttribute("login");

        // Fetch Folders
        List<Folder> userFolders = folderRepository.findAllByUserId(userId);
        model.addAttribute("userFolders", userFolders);
        List<Folder> defaultFolders = folderService.fetchDefaultFolders(userId);
        model.addAttribute("defaultFolders", defaultFolders);
        model.addAttribute("stats",folderService.mapCountToLabel(userId));
        model.addAttribute("userName",principal.getAttribute("name"));

        List<String> uniqueToIds = splitIds(to);
        model.addAttribute("toIds", String.join(",", uniqueToIds));

        return "compose-page";
    }

    private List<String> splitIds(String ids){
        if(!StringUtils.hasText(ids)) {
            return new ArrayList<>();
        }
        String[] splitIds = ids.split(",");
        return Arrays.stream(splitIds).map(StringUtils::trimWhitespace).filter(StringUtils::hasText)
                .distinct().collect(Collectors.toList());
    }

    @PostMapping(value="/sendEmail")
    public ModelAndView sendEmail(@RequestBody MultiValueMap<String,String> formData,@AuthenticationPrincipal OAuth2User principal){
        if (principal == null || !StringUtils.hasText(principal.getAttribute("login")))
            return new ModelAndView("redirect:/");
        String from = principal.getAttribute("login");
        List<String> toIds = splitIds(formData.getFirst("toIds"));
        String subject = formData.getFirst("subject");
        String body = formData.getFirst("body");
        emailService.sendEmail(from,toIds,subject,body);
        return new ModelAndView("redirect:/");
    }
}
