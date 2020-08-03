package com.hertfordshire.service.psql.privileges;


import com.hertfordshire.dto.PrivilegeDto;
import com.hertfordshire.model.psql.Privilege;

import java.util.Collection;
import java.util.List;

public interface PrivilegeService {

    Privilege save(PrivilegeDto privilegeDto);

    Collection<Privilege> saveAll(List<PrivilegeDto> privilegeDtoList);

    Privilege findName(String name);
}
