package cn.largequant.cloudadmin.service;

import cn.largequant.cloudadmin.base.Results;
import cn.largequant.cloudadmin.dto.RoleDto;
import cn.largequant.cloudadmin.model.SysRole;

public interface RoleService {

	Results<SysRole> getAllRoles();

	Results<SysRole> getAllRolesByPage(Integer offset, Integer limit);

	Results<SysRole> save(RoleDto roleDto);

	SysRole getRoleById(Integer id);

	Results update(RoleDto roleDto);

	Results delete(Integer id);

	Results<SysRole> getRoleByFuzzyRoleNamePage(String roleName, Integer offset, Integer limit);
}
