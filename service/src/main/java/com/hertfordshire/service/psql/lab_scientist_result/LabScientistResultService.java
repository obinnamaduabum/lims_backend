package com.hertfordshire.service.psql.lab_scientist_result;


import com.hertfordshire.dto.OrderedLabTestSearchDto;
import com.hertfordshire.model.psql.LabScientistTestResultModel;
import com.hertfordshire.pojo.PaginationResponsePojo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LabScientistResultService {

    PaginationResponsePojo findByLabScientistResultWithPagination(OrderedLabTestSearchDto orderedLabTestSearchDto, Pageable pageable);

    Long countByLabScientistResultWithPagination(OrderedLabTestSearchDto orderedLabTestSearchDto);

    Optional<LabScientistTestResultModel> findById(Long id);
}
