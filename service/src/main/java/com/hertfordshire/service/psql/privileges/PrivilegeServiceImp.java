package com.hertfordshire.service.psql.privileges;

import com.hertfordshire.dto.PrivilegeDto;
import com.hertfordshire.model.psql.Privilege;
import com.hertfordshire.dao.psql.PrivilegeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Service
public class PrivilegeServiceImp implements PrivilegeService {

    @Autowired
    private PrivilegeDao privilegeDao;

//    PrivilegeServiceImp(PrivilegeDao privilegeDao) {
//        this.privilegeDao = privilegeDao;
//    }

    @Override
    public Privilege save(PrivilegeDto privilegeDto) {
        Privilege privilege = new Privilege();
        privilege.setName(privilegeDto.getName());
        return this.privilegeDao.save(privilege);
    }

    @Override
    public Collection<Privilege> saveAll(List<PrivilegeDto> privilegeDtoList) {
        return null;
    }

    @Override
    public Privilege findName(String name) {
        return this.privilegeDao.findPrivilegeByName(name.toLowerCase());
    }
}
