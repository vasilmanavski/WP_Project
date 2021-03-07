package com.churchevents.web;

import com.churchevents.service.EmailSenderService;
import com.churchevents.service.FileService;
import com.churchevents.service.SubscribersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;




@Controller
public class FileController {

    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    private final FileService fileService;
    private final EmailSenderService emailSenderService;
    private final SubscribersService subscribersService;

    public FileController(FileService fileService, EmailSenderService emailSenderService, SubscribersService subscribersService) {
        this.fileService = fileService;
        this.emailSenderService = emailSenderService;
        this.subscribersService = subscribersService;
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        this.fileService.uploadFile(file);

        Path copyLocation = Paths
                .get(uploadDir + File.separator + StringUtils.cleanPath(file.getOriginalFilename()));
        this.emailSenderService.newsletterMail(copyLocation.toString(), this.subscribersService.findAll());

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        redirectAttributes.addAttribute("filename", file.getOriginalFilename());
        return "redirect:/posts";
    }

    @RequestMapping(value = "/getPDF", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getPDF() throws IOException {
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "src/main/resources/templates/pdf/Newsletter.pdf";

        headers.add("Content-Disposition", "inline;filename=" + filename);


        File file = new File(filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(Files.readAllBytes(file.toPath()), headers, HttpStatus.OK);
        return response;
    }



}
