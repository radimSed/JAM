package com.jam.controller;

import com.jam.dto.*;
import com.jam.exceptions.NotFoundException;
import com.jam.service.JamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
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
@RequestMapping(value = "/JAM/")
public class JamController {

    @Autowired
    JamService jamService;

    private final String compCardTemplate;
    private final String personCardTemplate;
    private final String appCardTemplate;

    public JamController(){
        compCardTemplate = getResource("templates/compCard.html"); //reads template for company card
        personCardTemplate = getResource("templates/personCard.html"); //reads template for person card
        appCardTemplate = getResource("templates/appCard.html"); //reads template for app card
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

//*********************** Company related methods ************************************************
    @GetMapping("comp")
    @ResponseBody
    public ResponseEntity<List<CompanyCard>> getCompList(
            @RequestParam(value = "id") int id,
            @RequestParam(value = "name") String name
            ){
        List<Company> complist = jamService.getCompanies(id, name);

        List<CompanyCard> compCards = new ArrayList<>();
        for(Company company:complist){
            String s1 = compCardTemplate.replace("+id+", String.valueOf(company.getId()));
            String s2 = s1.replace("+name+", company.getName());
            compCards.add(new CompanyCard(company.getId(), company.getName(), s2));
        }
        return new ResponseEntity<>(compCards, HttpStatus.OK);
    }

    @PostMapping("comp")
    @ResponseBody
    public ResponseEntity<ResultMessage> postCompany(@RequestBody Company company){
        return new ResponseEntity<>(jamService.postCompany(company), HttpStatus.OK);
    }

    @DeleteMapping("comp/{id}")
    @ResponseBody
    public ResponseEntity<ResultMessage> deleteCompany( @PathVariable(value = "id") int id){
        return new ResponseEntity<>(jamService.deleteCompany(id), HttpStatus.OK);
    }

    @PutMapping("comp")
    @ResponseBody
    public ResponseEntity<ResultMessage> updateCompany( @RequestBody Company company){
        return new ResponseEntity<>(jamService.updateCompany(company), HttpStatus.OK);
    }

//*********************** Person related methods ************************************************
@GetMapping("person")
@ResponseBody
public ResponseEntity<List<PersonCard>> getPersonList(
        @RequestParam(value = "id") int id,
        @RequestParam(value = "name") String name,
        @RequestParam(value = "email") String email,
        @RequestParam(value = "phone") String phone
){
    List<Person> personlist = jamService.getPersons(id, name, email, phone);

    List<PersonCard> personCards = new ArrayList<>();
    for(Person person:personlist){
        String s1 = personCardTemplate.replace("+id+", String.valueOf(person.getId()));
        String s2 = s1.replace("+name+", person.getName());
        String s3 = s2.replace("+email+", person.getEmail());
        String s4 = s3.replace("+phone+", person.getPhone());
        personCards.add(new PersonCard(person.getId(), person.getName(),
                person.getEmail(), person.getPhone(), s4));
    }
    return new ResponseEntity<>(personCards, HttpStatus.OK);
}

    @PostMapping("person")
    @ResponseBody
    public ResponseEntity<ResultMessage> postPerson(@RequestBody Person person){
        return new ResponseEntity<>(jamService.postPerson(person), HttpStatus.OK);
    }

    @DeleteMapping("person/{id}")
    @ResponseBody
    public ResponseEntity<ResultMessage> deletePerson( @PathVariable(value = "id") int id){
        return new ResponseEntity<>(jamService.deletePerson(id), HttpStatus.OK);
    }

    @PutMapping("person")
    @ResponseBody
    public ResponseEntity<ResultMessage> updatePerson( @RequestBody Person person){
        return new ResponseEntity<>(jamService.updatePerson(person), HttpStatus.OK);
    }

//*********************** Application related methods ************************************************
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
    public ResponseEntity<ApplicationDetail> getDetail( @PathVariable(value = "id") int id){
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

    //*********************** Other methods ************************************************
    @GetMapping("status")
    @ResponseBody
    public ResponseEntity<List<StatusValue>> getStatusList(){
        List<StatusValue> statusList = jamService.getStatusList();
        return new ResponseEntity<>(statusList, HttpStatus.OK);
    }
}
