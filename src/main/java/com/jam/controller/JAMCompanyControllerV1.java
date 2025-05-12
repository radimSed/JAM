package com.jam.controller;

import com.jam.dto.Company;
import com.jam.dto.CompanyCard;
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
@RequestMapping(value = "/JAM/api/v1/comp")
public class JAMCompanyControllerV1 {

    @Autowired
    JamServiceV1 jamService;

    private final String compCardTemplate;

    public JAMCompanyControllerV1(){
        compCardTemplate = getResource("templates/compCard.html"); //reads template for company card
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

    @PostMapping
    @ResponseBody
    public ResponseEntity<ResultMessage> postCompany(@RequestBody Company company){
        return new ResponseEntity<>(jamService.postCompany(company), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<ResultMessage> deleteCompany( @PathVariable(value = "id") int id){
        return new ResponseEntity<>(jamService.deleteCompany(id), HttpStatus.OK);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<ResultMessage> updateCompany( @RequestBody Company company){
        return new ResponseEntity<>(jamService.updateCompany(company), HttpStatus.OK);
    }

}
