package com.karan.craftingtable.mappers;

import com.karan.craftingtable.entities.PlanEntity;
import com.karan.craftingtable.entities.SubscriptionEntity;
import com.karan.craftingtable.models.responses.PlanResponseDTO;
import com.karan.craftingtable.models.responses.SubscriptionResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponseDTO toSubscriptionResponseDTO(SubscriptionEntity subscriptionEntity);

    PlanResponseDTO toPlanEntity(PlanEntity planEntity);

}
