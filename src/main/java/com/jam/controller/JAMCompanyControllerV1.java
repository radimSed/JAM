package com.jam.controller;

import com.jam.dto.Company;
import com.jam.dto.CompanyCard;
import com.jam.dto.ResultMessage;
import com.jam.service.JAMCompanyServiceV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;




@RestController
@CrossOrigin
@RequestMapping(value = "/JAM/api/v1/comp")
public class JAMCompanyControllerV1 extends JAMAbstractDataController{

    @Autowired
    JAMCompanyServiceV1 jamService;

    private final String compCardTemplate;

    public JAMCompanyControllerV1(){
        super();
        compCardTemplate = getResource("templates/compCard.html"); //reads template for company card
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
