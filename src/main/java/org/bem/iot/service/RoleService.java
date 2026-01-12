package org.bem.iot.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.entity.RoleImp;
import org.bem.iot.mapper.postgresql.RoleMapper;
import org.bem.iot.mapper.postgresql.UserInfoMapper;
import org.bem.iot.model.user.Role;
import org.bem.iot.model.user.UserInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色服务
 * @author jakybland
 */
@Service
public class RoleService {
    @Resource
    RoleMapper roleMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    /**
     * 统计角色数量
     * @param example 统计条件
     * @return 角色数量
     */
    public long count(QueryWrapper<Role> example) {
        return roleMapper.selectCount(example);
    }

    /**
     * 查询角色列表
     * @param example 查询条件
     * @return 角色列表
     */
    public List<Role> select(QueryWrapper<Role> example) {
        return roleMapper.selectList(example);
    }

    /**
     * 分页查询角色列表
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 角色列表
     */
    public IPage<Role> selectPage(QueryWrapper<Role> example, long index, long size) {
        Page<Role> page = new Page<>(index, size);
        return roleMapper.selectPage(page, example);
    }

    /**
     * 判断角色ID是否存在
     * @param roleId 角色ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotRoleId(int roleId) {
        QueryWrapper<Role> example = new QueryWrapper<>();
        example.eq("role_id", roleId);
        return !roleMapper.exists(example);
    }
    private boolean existsRoleName(String roleName) {
        QueryWrapper<Role> example = new QueryWrapper<>();
        example.eq("role_name", roleName);
        return roleMapper.exists(example);
    }

    /**
     * 查询角色
     * @param roleId 角色ID
     * @return 角色信息
     */
    public Role find(int roleId) {
        return roleMapper.selectById(roleId);
    }

    /**
     * 添加角色
     * @param record 角色信息
     */
    public void insert(Role record) {
        String auth = "[]";
        record.setRoleId(null);
        record.setRoleAuth(auth);
        roleMapper.insert(record);
    }

    /**
     * 添加角色
     * @param roleImp 角色信息
     */
    public boolean insertImport(RoleImp roleImp) {
        try {
            if(StrUtil.isEmpty(roleImp.getRoleName())) {
                return false;
            } else if(existsRoleName(roleImp.getRoleName())) {
                return false;
            } else {
                int orderNum = roleMapper.selectMax() + 1;
                String auth = "[]";
                Role record = new Role();
                record.setRoleId(null);
                record.setRoleName(roleImp.getRoleName());
                record.setRoleAuth(auth);
                record.setOrderNum(orderNum);
                record.setRemark(roleImp.getRemark());
                int ret = roleMapper.insert(record);
                return ret > 0;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 修改角色
     * @param record 角色信息
     */
    public void update(Role record) {
        roleMapper.updateById(record);
    }

    /**
     * 修改角色排序
     * @param roleId 角色ID
     * @param orderNum 排序值
     */
    public Role updateOrder(int roleId, int orderNum) {
        Role record = roleMapper.selectById(roleId);
        record.setOrderNum(orderNum);
        roleMapper.updateById(record);
        return record;
    }

    /**
     * 删除角色 (删除前需验证是否存在用户)
     * @param roleId 角色ID
     * @return 删除数量
     */
    public int del(int roleId) {
        int count = 0;
        if(roleId > 0) {
            count = roleMapper.deleteById(roleId);
            if(count > 0) {
                deleteByUser(roleId);
            }
        }
        return count;
    }

    /**
     * 批量删除角色 (删除前需验证是否存在用户)
     * @param idList 角色ID列表
     * @return 删除数量
     */
    public int delArray(List<Integer> idList) {
        int count = roleMapper.deleteBatchIds(idList);
        if(count > 0) {
            deleteByUser(idList);
        }
        return count;
    }

    private void deleteByUser(int roleId) {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.eq("role_id", roleId);
        userInfoMapper.delete(example);
    }

    private void deleteByUser(List<Integer> idList) {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.in("role_id", idList);
        userInfoMapper.delete(example);
    }
}
