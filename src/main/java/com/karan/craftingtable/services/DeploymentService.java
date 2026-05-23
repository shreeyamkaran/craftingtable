package com.karan.craftingtable.services;

import com.karan.craftingtable.models.responses.DeploymentResponseDTO;

public interface DeploymentService {

    DeploymentResponseDTO deploy(Long projectId);

}
