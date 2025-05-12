package com.jam.controller;

import com.jam.dto.Person;
import com.jam.dto.PersonCard;
import com.jam.dto.ResultMessage;
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
@RequestMapping(value = "/JAM/api/v1/person")
public class JAMPersonControllerV1 {

    @Autowired
    JamServiceV1 jamService;

    private final String personCardTemplate;

    public JAMPersonControllerV1(){
        personCardTemplate = getResource("templates/personCard.html"); //reads template for person card
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

    @GetMapping
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

    @PostMapping
    @ResponseBody
    public ResponseEntity<ResultMessage> postPerson(@RequestBody Person person){
        return new ResponseEntity<>(jamService.postPerson(person), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ResultMessage> deletePerson( @PathVariable(value = "id") int id){
        return new ResponseEntity<>(jamService.deletePerson(id), HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<ResultMessage> updatePerson( @RequestBody Person person){
        return new ResponseEntity<>(jamService.updatePerson(person), HttpStatus.OK);
    }


}
