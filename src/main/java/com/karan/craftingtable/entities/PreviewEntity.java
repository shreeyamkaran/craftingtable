package com.karan.craftingtable.entities;

import com.karan.craftingtable.enums.PreviewStatusEnum;

import java.time.Instant;

public class PreviewEntity extends AuditableBaseEntity {

    private Long id;
    private ProjectEntity project;
    private String namespace;
    private String podName;
    private String previewUrl;
    private PreviewStatusEnum previewStatus;
    private Instant startedAt;
    private Instant endedAt;

}
