package com.jam.controller;

import com.jam.exceptions.NotFoundException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

abstract public class JAMAbstractDataController {
    protected String getResource(String res){
        Resource resource = new ClassPathResource(res);
        try {
            InputStream inputStream = resource.getInputStream();
            byte[] byteData = FileCopyUtils.copyToByteArray(inputStream);
            return new String(byteData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new NotFoundException(res + " not found");
        }
    }

}
