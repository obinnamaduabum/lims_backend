package com.hertfordshire.service.psql.lab_scientist_result;


import com.hertfordshire.dto.OrderedLabTestSearchDto;
import com.hertfordshire.pojo.PaginationResponsePojo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface LabScientistResultService {

    PaginationResponsePojo findByLabScientistResultWithPagination(OrderedLabTestSearchDto orderedLabTestSearchDto, Pageable pageable);

    Long countByLabScientistResultWithPagination(OrderedLabTestSearchDto orderedLabTestSearchDto);
}
