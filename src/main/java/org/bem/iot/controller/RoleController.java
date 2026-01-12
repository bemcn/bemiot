package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.entity.RoleImp;
import org.bem.iot.listener.RoleListener;
import org.bem.iot.model.user.Role;
import org.bem.iot.service.LoginService;
import org.bem.iot.service.RoleService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ExcelUtil;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 角色权限
 * @author jakybland
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    RoleService roleService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    LoginService loginService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取角色权限列表
     */
    @GetMapping("/getRoleList")
    @PublicHeadLimit
    @ResponseBody
    public void getRoleList() {
        JSONObject jsonObject;
        try {
            QueryWrapper<Role> example = new QueryWrapper<>();
            example.gt("role_id", 1);
            example.orderByAsc("order_num");
            List<Role> list = roleService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取角色权限分页列表
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getRolePageList")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getRolePageList(@RequestParam(name="key", defaultValue = "") String key,
                                @RequestParam(name="index", defaultValue = "1") Integer index,
                                @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Role> example = new QueryWrapper<>();
            example.gt("role_id", 1);
            if(!StrUtil.isEmpty(key)) {
                example.like("role_name", key);
            }
            example.orderByAsc("order_num");

            IPage<Role> page = roleService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取角色权限
     * @param id id
     */
    @GetMapping("/getRole")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getRole(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(roleService.existsNotRoleId(id)) {
            jsonObject = ResponseUtil.getErrorJson("角色权限不存在");
        } else {
            Role data = roleService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增角色权限
     * @param record 角色权限
     */
    @PostMapping("/addRole")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addRole(@Validated(Add.class) @Valid Role record) {
        JSONObject jsonObject;
        try {
            roleService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "角色权限", "新增", "新增角色权限，【名称】" + record.getRoleName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑角色权限
     * @param record 角色权限
     */
    @PostMapping("/editRole")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editRole(@Validated(Edit.class) @Valid Role record) {
        JSONObject jsonObject;
        try {
            roleService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "角色权限", "修改", "修改角色的基本信息，【名称】" + record.getRoleName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑角色权限
     * @param id id
     * @param roleAuth 权限
     */
    @PostMapping("/editRoleAuth")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editRoleAuth(@RequestParam(name="id", defaultValue="0") int id, @RequestParam(name="roleAuth", defaultValue="") String roleAuth) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(roleService.existsNotRoleId(id)) {
            jsonObject = ResponseUtil.getErrorJson("角色权限不存在");
        } else if(StrUtil.isEmpty(roleAuth)) {
            jsonObject = ResponseUtil.getErrorJson("权限内容不能为空");
        } else {
            try {
                Role record = roleService.find(id);
                String roleName = record.getRoleName();
                record.setRoleAuth(roleAuth);
                roleService.update(record);

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "角色权限", "修改", "修改角色的权限内容，【名称】" + roleName);

                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 修改排序值
     * @param id id
     * @param orderNum 排序值
     */
    @GetMapping("/updateOrderNumber")
    @PublicHeadLimit("user")
    @ResponseBody
    public void updateOrderNumber(@RequestParam(name="id", defaultValue="0") int id, @RequestParam(name="orderNum", defaultValue="1") int orderNum) {
        JSONObject jsonObject;
        if (id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if (roleService.existsNotRoleId(id)) {
            jsonObject = ResponseUtil.getErrorJson("角色权限不存在");
        } else {
            try {
                Role role = roleService.updateOrder(id, orderNum);
                String roleName = role.getRoleName();

                String accessToken = request.getHeader("accessToken");
                logSystemService.insert(accessToken, "角色权限", "修改", "修改角色的排序为" + orderNum + "，【名称】" + roleName);

                jsonObject = ResponseUtil.getSuccessJson();
            } catch (Exception e) {
                jsonObject = ResponseUtil.getErrorJson(e.getMessage());
            }
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除角色权限
     * @param id id
     */
    @GetMapping("/delRole")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delRole(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        try {
            if(!roleService.existsNotRoleId(id)) {
                Role role = roleService.find(id);
                String roleName = role.getRoleName();
                int count = roleService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "角色权限", "删除", "删除角色信息，【名称】" + roleName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个角色权限
     * @param ids id集合
     */
    @GetMapping("/delRoles")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delRoles(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = roleService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "角色权限", "删除", "批量删除" + count + "条角色信息");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 导入模板下载
     * @param token 用户令牌
     */
    @GetMapping("/modelExcel")
    public void modelExcel(@RequestParam(name="token", defaultValue="") String token) throws IOException {
        if(StrUtil.isNotEmpty(token)) {
            if(loginService.verifyAccessToken(token)) {
                String[] headArray = {"角色名称", "备注"};
                ExcelUtil.downloadModelExcel(headArray, "角色权限", response);
            }
        }
    }

    /**
     * excel导入数据
     * @param file 导入文件
     * @param token 用户令牌
     */
    @PostMapping("/importExcel")
    @ResponseBody
    public void importExcel(@RequestParam("file") MultipartFile file, @RequestParam(name="token", defaultValue="") String token) {
        JSONObject jsonObject;
        if(StrUtil.isNotEmpty(token)) {
            if(loginService.verifyAccessToken(token)) {
                JSONObject verifyObj = ExcelUtil.verifyImportExcel(file);
                boolean verify = verifyObj.getBoolean("state");
                if(!verify) {
                    jsonObject = ResponseUtil.getErrorJson(2000, verifyObj.getString("message"));
                } else {
                    String suffix = verifyObj.getString("suffix");

                    try {
                        RoleListener roleListener = new RoleListener();
                        EasyExcel.read(file.getInputStream(), RoleImp.class, roleListener).sheet().doRead();
                        List<RoleImp> list = roleListener.list;

                        if(!list.isEmpty()) {
                            //验证是否有带必填参数的数据，如果有，表示格式正确
                            boolean isFormat = false;
                            for (RoleImp role : list) {
                                if (StrUtil.isNotEmpty(role.getRoleName())) {
                                    isFormat = true;
                                    break;
                                }
                            }

                            if (isFormat) {
                                List<RoleImp> errorList = new ArrayList<>();  // 错误列表
                                boolean status = true;  // 状态
                                int successCount = 0;  // 成功数量
                                int errorCount = 0;  // 错误数量
                                for (RoleImp role : list) {
                                    if (StrUtil.isEmpty(role.getRoleName())) {
                                        status = false;
                                        errorCount++;
                                        errorList.add(role);
                                    } else {
                                        boolean isInsert = roleService.insertImport(role);
                                        if (isInsert) {
                                            successCount++;
                                        } else {
                                            status = false;
                                            errorCount++;
                                            errorList.add(role);
                                        }
                                    }
                                }
                                JSONObject resultJson = new JSONObject();
                                if (status) {
                                    if(successCount > 0) {
                                        resultJson.put("successCount", successCount);
                                        jsonObject = ResponseUtil.getSuccessJson(resultJson);
                                    } else {
                                        jsonObject = ResponseUtil.getErrorJson(2000, "Excel中没有数据内容");
                                    }
                                } else {
                                    resultJson = ExcelUtil.importInitExcel("角色权限", suffix, successCount, errorCount, RoleImp.class, errorList);
                                    jsonObject = ResponseUtil.getErrorJson(3000, "导入数据失败", resultJson);
                                }
                            } else {
                                jsonObject = ResponseUtil.getErrorJson(2000, "待导入数据的格式错误，缺少必填项");
                            }
                        } else {
                            jsonObject = ResponseUtil.getErrorJson(2000, "Excel中没有数据内容");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        jsonObject = ResponseUtil.getErrorJson(2000, "导入数据文件错误");
                    }
                }
            } else {
                jsonObject = ResponseUtil.getErrorJson(2000, "无权限的请求");
            }
        } else {
            jsonObject = ResponseUtil.getErrorJson(2000, "无权限的请求");
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 导出Excel
     * @param key 关键字
     * @param token 用户令牌
     */
    @GetMapping("/exportExcel")
    public void exportExcel(@RequestParam(name="key", defaultValue="") String key, @RequestParam(name="token", defaultValue="") String token) throws IOException {
        if(StrUtil.isNotEmpty(token)) {
            if(loginService.verifyAccessToken(token)) {
                QueryWrapper<Role> example = new QueryWrapper<>();
                example.gt("role_id", 1);
                if (!StrUtil.isEmpty(key)) {
                    example.like("role_name", key);
                }
                example.orderByAsc("order_num");
                List<Role> list = roleService.select(example);

                // 设置响应头
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-disposition", "attachment;filename=" + ExcelUtil.getExcelFileName("角色权限") + ".xlsx");

                // 写入Excel
                EasyExcel.write(response.getOutputStream(), Role.class)
                        .useDefaultStyle(false)
                        .sheet("Sheet1")
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .doWrite(list);
            }
        }
    }
}
