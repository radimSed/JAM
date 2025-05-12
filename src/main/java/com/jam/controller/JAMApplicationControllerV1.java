package com.jam.controller;

import com.jam.JamApplication;
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/JAM/api/v1/")
public class JAMApplicationControllerV1 {

    @Autowired
    JamServiceV1 jamService;

    private final String appCardTemplate;

    public JAMApplicationControllerV1(){
        appCardTemplate = getResource("templates/appCard.html"); //reads template for app card
    }

    private String getResource(String res){
        Resource resource = new ClassPathResource(res);
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] byteData = FileCopyUtils.copyToByteArray(inputStream);
            return new String(byteData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new NotFoundException(res + " not found");
        }
    }

    @GetMapping("preview")
    @ResponseBody
    public ResponseEntity<List<AppCard>> getPersonList(
            @RequestParam(value = "id") int id,
            @RequestParam(value = "title") String title,
            @RequestParam(value = "posId") String posId,
            @RequestParam(value = "compName") String compName,
            @RequestParam(value = "persName") String persName,
            @RequestParam(value = "status") String status

    ){
        List<AppPreview> previewlist = jamService.getPreview(id, title, posId, compName, persName, status);

        List<AppCard> appCards = new ArrayList<>();
        for(AppPreview preview:previewlist){
            String s1 = appCardTemplate.replace("+id+", String.valueOf(preview.getId()));
            String s2 = s1.replace("+title+", preview.getTitle());
            String s3 = s2.replace("+positionId+", preview.getPositionId());
            String s4 = s3.replace("+compName+", preview.getCompanyName());
            String s5 = s4.replace("+personName+", preview.getPersonName());
            String s6 = s5.replace("+status+", preview.getStatus());
            appCards.add(new AppCard(preview.getId(), preview.getTitle(), preview.getPositionId(), preview.getCompanyName(), preview.getPersonName(),
                    preview.getStatus(), s6));
        }
        return new ResponseEntity<>(appCards, HttpStatus.OK);
    }

    @GetMapping("app/{id}")
    @ResponseBody
    public ResponseEntity<ApplicationDetail> getDetail(@PathVariable(value = "id") int id){
        return new ResponseEntity<>(jamService.getDetail(id), HttpStatus.OK);
    }

    @PostMapping("app")
    @ResponseBody
    public ResponseEntity<ResultMessage> postApplication(@RequestBody Application app){
        return new ResponseEntity<>(jamService.postApplication(app), HttpStatus.OK);
    }

    @PutMapping("app")
    @ResponseBody
    public ResponseEntity<ResultMessage> updateApplication(@RequestBody ApplicationUpdate appUpdate){
        return new ResponseEntity<>(jamService.updateApplication(appUpdate), HttpStatus.OK);
    }

    @DeleteMapping("app/{id}")
    @ResponseBody
    public ResponseEntity<ResultMessage> deleteApplication( @PathVariable(value = "id") int id){
        return new ResponseEntity<>(jamService.deleteApplication(id), HttpStatus.OK);
    }

}
