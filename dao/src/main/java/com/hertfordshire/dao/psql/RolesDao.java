package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.Role;
import com.hertfordshire.utils.lhenum.RoleTypeConstant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesDao extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role as r WHERE r.roleType = ?1")
    Role findByRoleType(RoleTypeConstant roleType);

}
