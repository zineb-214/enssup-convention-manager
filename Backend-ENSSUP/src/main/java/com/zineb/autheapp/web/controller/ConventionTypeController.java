package com.zineb.autheapp.web.controller;


import com.zineb.autheapp.dao.entities.AppConventionType;
import com.zineb.autheapp.Service.interfaces.ConventionTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@PreAuthorize("hasRole('ADMIN')")
public class ConventionTypeController {
    @Autowired
    private ConventionTypeService conventionTypeService;

    @GetMapping(path = "/types")
    public List<AppConventionType> getTypes(){
        return conventionTypeService.listTypes();
    }
    @PostMapping(path = "/addType")
    public AppConventionType addType(@RequestBody AppConventionType appConventionType){
        return conventionTypeService.addtype(appConventionType);
    }
    @DeleteMapping("/deleteType/{id}")
    public ResponseEntity<String> deleteType(@PathVariable Long id) {
        conventionTypeService.deleteType(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Type de convention supprimé.");
    }

    @PutMapping("/updateType/{id}")
    public AppConventionType updateType(@PathVariable Long id, @RequestBody AppConventionType updated) {
        return conventionTypeService.update(id, updated);
    }
    @GetMapping("/search")
    public Page<AppConventionType> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return conventionTypeService.filterTypes(name, code, pageable);
    }


}
