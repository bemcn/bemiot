package org.bem.iot.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.bem.iot.entity.UserImp;
import org.bem.iot.mapper.postgresql.ProductAuthCodeMapper;
import org.bem.iot.mapper.postgresql.RoleMapper;
import org.bem.iot.mapper.postgresql.UserInfoMapper;
import org.bem.iot.model.product.ProductAuthCode;
import org.bem.iot.model.user.Role;
import org.bem.iot.model.user.UserInfo;
import org.bem.iot.util.EncryptUtil;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户信息
 * @author jakybland
 */
@Service
public class UserInfoService {
    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    RoleMapper roleMapper;

    @Resource
    ProductAuthCodeMapper productAuthCodeMapper;

    @Resource
    SystemConfigService systemConfigService;

    @Value("${auth.passkey}")
    String passkey;

    /**
     * 统计用户数量
     * @param example 查询条件
     * @return 用户数量
     */
    public long count(QueryWrapper<UserInfo> example) {
        return userInfoMapper.selectCount(example);
    }

    /**
     * 查询用户信息
     * @param example 查询条件
     * @return 用户信息列表
     */
    public List<UserInfo> select(QueryWrapper<UserInfo> example) {
        List<UserInfo> list = userInfoMapper.selectList(example);
        for (UserInfo userInfo : list) {
            int roleId = userInfo.getRoleId();
            Role role = roleMapper.selectById(roleId);
            userInfo.setRole(role);
            userInfo.setPassWord("");
            userInfo.setPassSalt("");
        }
        return list;
    }

    /**
     * 查询用户名数组
     * @return 用户信息列表
     */
    public List<String> selectUserNameArray() {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.eq("status", 1);
        List<UserInfo> userList = userInfoMapper.selectList(example);
        List<String> list = new ArrayList<>();
        for (UserInfo userInfo : userList) {
            list.add(userInfo.getUserName());
        }
        return list;
    }

    /**
     * 根据ID列表查询用户信息
     * @param idList 用户ID列表
     * @return 用户信息列表
     */
    public List<UserInfo> selectIds(List<Integer> idList) {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.in("user_id", idList);
        List<UserInfo> list = userInfoMapper.selectList(example);
        for (UserInfo userInfo : list) {
            int roleId = userInfo.getRoleId();
            Role role = roleMapper.selectById(roleId);
            userInfo.setRole(role);
            userInfo.setPassWord("");
            userInfo.setPassSalt("");
        }
        return list;
    }

    /**
     * 分页查询用户信息
     * @param example 查询条件
     * @param index 页码
     * @param size 每页数量
     * @return 用户信息列表
     */
    public IPage<UserInfo> selectPage(QueryWrapper<UserInfo> example, long index, long size) {
        Page<UserInfo> page = new Page<>(index, size);
        IPage<UserInfo> pageData = userInfoMapper.selectPage(page, example);
        List<UserInfo> list = pageData.getRecords();
        for (UserInfo userInfo : list) {
            int roleId = userInfo.getRoleId();
            Role role = roleMapper.selectById(roleId);
            userInfo.setRole(role);
            userInfo.setPassWord("");
            userInfo.setPassSalt("");
        }
        pageData.setRecords(list);
        return pageData;
    }

    /**
     * 判断用户ID是否存在
     * @param userId 用户ID
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotUserId(int userId) {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.eq("user_id", userId);
        return !userInfoMapper.exists(example);
    }

    /**
     * 判断用户名是否存在
     * @param userName 用户名
     * @return 存在返回false，不存在返回true
     */
    public boolean existsNotUserName(String userName) {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.eq("user_name", userName);
        return !userInfoMapper.exists(example);
    }
    private boolean existsUserName(String userName) {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.eq("user_name", userName);
        return userInfoMapper.exists(example);
    }
    private boolean existsNotRoleName(String roleName) {
        QueryWrapper<Role> example = new QueryWrapper<>();
        example.eq("role_name", roleName);
        return !roleMapper.exists(example);
    }
    private int getRoleById(String roleName) {
        QueryWrapper<Role> example = new QueryWrapper<>();
        example.eq("role_name", roleName);
        Role role = roleMapper.selectOne(example);
        return role.getRoleId();
    }

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public UserInfo find(@Param("userId") int userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        int roleId = userInfo.getRoleId();
        Role role = roleMapper.selectById(roleId);
        userInfo.setRole(role);
        userInfo.setPassWord("");
        userInfo.setPassSalt("");
        return userInfo;
    }

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    public UserInfo findMeta(@Param("userId") int userId) {
        return userInfoMapper.selectById(userId);
    }

    /**
     * 插入用户信息
     * @param record 用户信息
     * @throws Exception 异常信息
     */
    public void insert(UserInfo record) throws Exception {
        record.setUserId(null);
        int ret = userInfoMapper.insert(record);
        if(ret < 1) {
            throw new Exception("新增用户失败");
        }
    }

    /**
     * 导入用户信息
     * @param user 待导入单挑条用户信息
     * @return 成功返回true
     */
    public boolean insertImport(UserImp user) {
        try {
            if(StrUtil.isEmpty(user.getUserName()) || StrUtil.isEmpty(user.getNickName()) || StrUtil.isEmpty(user.getRoleName())) {
                return false;
            } else if(existsUserName(user.getUserName().toLowerCase())) {
                return false;
            } else if(existsNotRoleName(user.getRoleName())) {
                return false;
            } else {
                int roleId = getRoleById(user.getRoleName());
                String initPassword = systemConfigService.find("initPassword");

                String passSalt = InternalIdUtil.createPassSalt();
                Date createTime = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date lastLoginTime = format.parse("2000-01-01");
                String passWord = EncryptUtil.encryptAes(initPassword, passkey, passSalt);
                String userName = user.getUserName().toLowerCase();

                UserInfo record = new UserInfo();
                record.setUserId(null);
                record.setUserName(userName);
                record.setPassWord(passWord);
                record.setPassSalt(passSalt);
                record.setRoleId(roleId);
                record.setNickName(user.getNickName());
                record.setHeadImg("/src/assets/images/schoolboy.png");
                if ("男".equals(user.getSex())) {
                    record.setSex(1);
                } else if ("女".equals(user.getSex())) {
                    record.setSex(2);
                } else {
                    record.setSex(0);
                }
                record.setPhone(user.getPhone());
                record.setEmail(user.getEmail());
                record.setStatus(1);
                record.setRemark(user.getRemark());
                record.setCreateTime(createTime);
                record.setLastLoginIp("0.0.0.0");
                record.setLastLoginTime(lastLoginTime);

                int ret = userInfoMapper.insert(record);
                return ret > 0;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 修改用户信息
     * @param record 用户信息
     * @return 返回用户信息
     */
    public UserInfo update(@Param("record") UserInfo record) {
        userInfoMapper.updateById(record);
        return record;
    }

    /**
     * 删除用户信息
     * @param userId 用户ID
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int del(@Param("userId") int userId) throws Exception {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.eq("user_id", userId);
        example.gt("role_id", 1);
        long count = userInfoMapper.selectCount(example);
        if(count > 0L) {
            userInfoMapper.delete(example);
            delByAuthCode(userId);
            return (int) count;
        } else {
            throw new Exception("默认系统内置用户禁止删除");
        }
    }

    /**
     * 批量删除用户信息
     * @param idList 用户ID列表
     * @return 删除数量
     * @throws Exception 异常信息
     */
    public int delArray(List<Integer> idList) throws Exception {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.in("user_id", idList);
        example.gt("role_id", 1);
        long count = userInfoMapper.selectCount(example);
        if(count > 0L) {
            userInfoMapper.delete(example);
            delByAuthCodeArray(idList);
            return (int) count;
        } else {
            throw new Exception("包含默认系统内置用户，禁止删除");
        }
    }

    private void delByAuthCode(int userId) {
        QueryWrapper<ProductAuthCode> example = new QueryWrapper<>();
        example.eq("user_id", userId);
        productAuthCodeMapper.delete(example);
    }

    private void delByAuthCodeArray(List<Integer> idList) {
        QueryWrapper<ProductAuthCode> example = new QueryWrapper<>();
        example.in("user_id", idList);
        productAuthCodeMapper.delete(example);
    }
}
