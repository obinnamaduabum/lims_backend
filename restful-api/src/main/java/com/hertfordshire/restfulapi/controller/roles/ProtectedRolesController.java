package com.hertfordshire.restfulapi.controller.roles;

import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.model.psql.Role;
import com.hertfordshire.pojo.RolePojo;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProtectedRolesController extends ProtectedBaseApiController {

    @Autowired
    private RolesDao rolesDao;

    @Autowired
    private MessageUtil messageUtil;

    @GetMapping("/default/portal-user/roles")
    public ResponseEntity<?> index() {
        ApiError apiError = null;

        List<Role> roleList = this.rolesDao.findAll();

        List<RolePojo> rolePojos = new ArrayList<>();

        for (Role role : roleList) {
                RolePojo rolePojo = new RolePojo();
                rolePojo.setName(role.getRoleName());
                rolePojo.setType(role.getRoleType().toString());
                rolePojos.add(rolePojo);
        }

        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.roles", "en"),
                true, new ArrayList<>(), rolePojos);

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
