package org.charlotte.e2ecore.endpoint;

import org.charlotte.e2ecore.service.CollectionService;
import org.charlotte.e2edomain.Env;
import org.charlotte.e2edomain.dto.EnvCreateDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/env")
public class EvnController {
    @Resource
    private CollectionService collectionService;

    @PostMapping("/create")
    public Env create(@RequestBody EnvCreateDTO dto) {
        Env category = Env.builder()
                .projectId(dto.getProjectId())
                .domain(dto.getDomain())
                .headers(dto.getHeaders())
                .cookies(dto.getCookies())
                .status("0")
                .build();

        return collectionService.saveOrUpdate(category);
    }
}