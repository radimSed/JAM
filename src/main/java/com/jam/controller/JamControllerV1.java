package com.jam.controller;

import com.jam.dto.*;
import com.jam.exceptions.NotFoundException;
import com.jam.service.JamServiceV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/JAM/api/v1/")
public class JamControllerV1 {

    @Autowired
    JamServiceV1 jamService;

    public JamControllerV1(){
    }

    @GetMapping("test")
    public ResponseEntity<String> performTest(){
        String returnText = "JAM Controller seems to be ready";

        if(jamService != null) {
            returnText += "<br>" + jamService.testService();
        } else {
            returnText += "<br>JAM service not ready for some reason";
        }
        return new ResponseEntity<>(returnText, HttpStatus.OK);
    }

    @GetMapping("")
    public String loadHomePage() {
        Resource resource = new ClassPathResource("templates/index.html");
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] byteData = FileCopyUtils.copyToByteArray(inputStream);
            return new String(byteData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new NotFoundException("index.html not found");
        }
    }

    @GetMapping("status")
    @ResponseBody
    public ResponseEntity<List<StatusValue>> getStatusList(){
        List<StatusValue> statusList = jamService.getStatusList();
        return new ResponseEntity<>(statusList, HttpStatus.OK);
    }
}
