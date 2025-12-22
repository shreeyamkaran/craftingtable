package com.karan.craftingtable.entities;

public class UsageLogEntity {

    private Long id;
    private UserEntity user;
    private ProjectEntity project;
    private String action;
    private Integer tokenUsed;
    private Integer durationMs;
    private String metaData; // e.g. {model_used, prompt_used}

}
