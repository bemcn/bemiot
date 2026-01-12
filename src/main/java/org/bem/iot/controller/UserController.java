package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.entity.User;
import org.bem.iot.entity.UserExp;
import org.bem.iot.entity.UserImp;
import org.bem.iot.listener.UserListener;
import org.bem.iot.model.user.UserInfo;
import org.bem.iot.service.LoginService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.service.UserInfoService;
import org.bem.iot.util.EncryptUtil;
import org.bem.iot.util.ExcelUtil;
import org.bem.iot.util.InternalIdUtil;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 用户管理
 * @author jakybland
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    UserInfoService userInfoService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    LoginService loginService;

    @Value("${auth.passkey}")
    String passkey;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取用户列表
     * @param roleId 角色ID
     * @param status 状态 0：停用 1：启用
     * @param filed 查询字段 userName/nickName/phone/email
     * @param key 关键字
     * @param dateType 查询事件类型 0：创建事件 1：最近登录时间
     * @param startDate 查询开始日期
     * @param endDate 查询截止日期
     */
    @GetMapping("/getUserList")
    @PublicHeadLimit
    @ResponseBody
    public void getUserList(@RequestParam(name="roleId", defaultValue="0") int roleId,
                            @RequestParam(name="status", defaultValue="1") int status,
                            @RequestParam(name="filed", defaultValue="") String filed,
                            @RequestParam(name="key", defaultValue="") String key,
                            @RequestParam(name="dateType", defaultValue="0") int dateType,
                            @RequestParam(name="startDate", defaultValue="") String startDate,
                            @RequestParam(name="endDate", defaultValue="") String endDate) {
        JSONObject jsonObject;
        try {
            QueryWrapper<UserInfo> example = createExample(roleId, status, filed, key, dateType, startDate, endDate);
            List<UserInfo> list = userInfoService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取用户分页列表
     * @param roleId 角色ID
     * @param status 状态 0：停用 1：启用
     * @param filed 查询字段 userName/nickName/phone/email
     * @param key 关键字
     * @param dateType 查询事件类型 0：创建事件 1：最近登录时间
     * @param startDate 查询开始日期
     * @param endDate 查询截止日期
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getUserPageList")
    @PublicHeadLimit
    @ResponseBody
    public void getUserPageList(@RequestParam(name="roleId", defaultValue="0") int roleId,
                                @RequestParam(name="status", defaultValue="1") int status,
                                @RequestParam(name="filed", defaultValue="") String filed,
                                @RequestParam(name="key", defaultValue="") String key,
                                @RequestParam(name="dateType", defaultValue="0") int dateType,
                                @RequestParam(name="startDate", defaultValue="") String startDate,
                                @RequestParam(name="endDate", defaultValue="") String endDate,
                                @RequestParam(name="index", defaultValue = "1") Integer index,
                                @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<UserInfo> example = createExample(roleId, status, filed, key, dateType, startDate, endDate);
            IPage<UserInfo> page = userInfoService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    private QueryWrapper<UserInfo> createExample(int roleId, int status, String filed, String key, int dateType, String startDate, String endDate) {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.eq("status", status);
        if(roleId > 0) {
            example.eq("role_id", roleId);
        }
        if(!StrUtil.isEmpty(filed) && !StrUtil.isEmpty(key)) {
            switch(filed) {
                case "userName":
                    example.like("user_name", key);
                    break;
                case "nickName":
                    example.like("nick_name", key);
                    break;
                case "phone":
                    example.like("phone", key);
                    break;
                case "email":
                    example.like("email", key);
                    break;
            }
        }
        if(!StrUtil.isEmpty(startDate) && !StrUtil.isEmpty(endDate)) {
            Date starTime;
            Date endTime;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                starTime = format.parse(startDate);
                endTime = format.parse(endDate);
                if(dateType == 0) {
                    example.between("create_time", starTime, endTime);
                } else {
                    example.between("last_login_time", starTime, endTime);
                }
            } catch (ParseException ignored) {
            }
        }
        if(dateType == 0) {
            example.orderByDesc("create_time");
        } else {
            example.orderByDesc("last_login_time");
        }
        return example;
    }

    /**
     * 获取指定多个用户列表
     * @param ids id集合
     */
    @GetMapping("/getUserListByIds")
    @PublicHeadLimit
    @ResponseBody
    public void getUserListByIds(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        if(StrUtil.isEmpty(ids)) {
            jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
        } else {
            List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
            List<UserInfo> list = userInfoService.selectIds(idList);
            jsonObject = ResponseUtil.getSuccessJson(list);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/getUserNameArray")
    @PublicHeadLimit
    @ResponseBody
    public void getUserNameArray() {
        JSONObject jsonObject;
        try {
            List<String> list = userInfoService.selectUserNameArray();
            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取用户
     * @param id id
     */
    @GetMapping("/getUser")
    @PublicHeadLimit
    @ResponseBody
    public void getUser(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(userInfoService.existsNotUserId(id)) {
            jsonObject = ResponseUtil.getErrorJson("用户不存在");
        } else {
            UserInfo data = userInfoService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增用户
     * @param record 用户
     */
    @PostMapping("/addUser")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addUser(@Validated(Add.class) @Valid User record) {
        JSONObject jsonObject;
        try {
            String passSalt = InternalIdUtil.createPassSalt();
            Date createTime = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date lastLoginTime = format.parse("2000-01-01");
            String passWord = record.getPassWord();
            passWord = EncryptUtil.encryptAes(passWord, passkey, passSalt);
            String userName = record.getUserName().toLowerCase();

            JSONObject obj = JSONObject.from(record);
            obj.put("userName", userName);
            obj.put("passWord", passWord);
            obj.put("passSalt", passSalt);
            obj.put("status", 1);
            obj.put("createTime", createTime);
            obj.put("lastLoginIp", "");
            obj.put("lastLoginTime", lastLoginTime);
            UserInfo userInfo = JSON.parseObject(obj.toString(), UserInfo.class);
            userInfoService.insert(userInfo);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "用户信息", "新增", "新增用户，【账号】" + userName);

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑用户
     * @param record 用户
     */
    @PostMapping("/editUser")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editUser(@Validated(Edit.class) @Valid User record) {
        JSONObject jsonObject;
        try {
            UserInfo user = userInfoService.findMeta(record.getUserId());
            String userName = user.getUserName();
            if(!StrUtil.isEmpty(record.getPassWord())) {
                String passWord = record.getPassWord();
                passWord = EncryptUtil.encryptAes(passWord, passkey, user.getPassSalt());
                user.setPassWord(passWord);
            }
            user.setRoleId(record.getRoleId());
            user.setNickName(record.getNickName());
            userInfoService.update(user);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "用户信息", "修改", "修改用户，【账号】" + userName);

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除用户
     * @param id id
     */
    @GetMapping("/delUser")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delUser(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        try {
            if(!userInfoService.existsNotUserId(id)) {
                UserInfo user = userInfoService.findMeta(id);
                String userName = user.getUserName();
                int count = userInfoService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "用户信息", "删除", "删除用户，【账号】" + userName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个用户
     * @param ids id集合
     */
    @GetMapping("/delUsers")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delUsers(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = userInfoService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "用户信息", "删除", "批量删除" + count + "条用户信息");
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
                String[] headArray = {"账号", "昵称", "角色", "性别", "手机号码", "电子邮箱", "备注"};
                ExcelUtil.downloadModelExcel(headArray, "用户信息", response);
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
                        UserListener userListener = new UserListener();
                        EasyExcel.read(file.getInputStream(), UserImp.class, userListener).sheet().doRead();
                        List<UserImp> list = userListener.list;

                        if(!list.isEmpty()) {
                            //验证是否有带必填参数的数据，如果有，表示格式正确
                            boolean isFormat = false;
                            for (UserImp user : list) {
                                if (StrUtil.isNotEmpty(user.getUserName()) && StrUtil.isNotEmpty(user.getNickName()) && StrUtil.isNotEmpty(user.getRoleName())) {
                                    isFormat = true;
                                    break;
                                }
                            }

                            if (isFormat) {
                                List<UserImp> errorList = new ArrayList<>();  // 错误列表
                                boolean status = true;  // 状态
                                int successCount = 0;  // 成功数量
                                int errorCount = 0;  // 错误数量
                                for (UserImp user : list) {
                                    if (StrUtil.isEmpty(user.getUserName()) && StrUtil.isEmpty(user.getNickName()) && StrUtil.isEmpty(user.getRoleName())) {
                                        status = false;
                                        errorCount++;
                                        errorList.add(user);
                                    } else {
                                        boolean isInsert = userInfoService.insertImport(user);
                                        if (isInsert) {
                                            successCount++;
                                        } else {
                                            status = false;
                                            errorCount++;
                                            errorList.add(user);
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
                                    resultJson = ExcelUtil.importInitExcel("用户信息", suffix, successCount, errorCount, UserImp.class, errorList);
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
    public void exportExcel(@RequestParam(name="roleId", defaultValue="0") int roleId,
                            @RequestParam(name="status", defaultValue="1") int status,
                            @RequestParam(name="filed", defaultValue="") String filed,
                            @RequestParam(name="key", defaultValue="") String key,
                            @RequestParam(name="dateType", defaultValue="0") int dateType,
                            @RequestParam(name="startDate", defaultValue="") String startDate,
                            @RequestParam(name="endDate", defaultValue="") String endDate,
                            @RequestParam(name="token", defaultValue="") String token) throws IOException {
        if(StrUtil.isNotEmpty(token)) {
            if(loginService.verifyAccessToken(token)) {
                QueryWrapper<UserInfo> example = createExample(roleId, status, filed, key, dateType, startDate, endDate);
                List<UserInfo> userList = userInfoService.select(example);

                List<UserExp> list = new ArrayList<>();
                for(UserInfo userInfo : userList) {
                    UserExp user = new UserExp();
                    user.setUserId(userInfo.getUserId());
                    user.setUserName(userInfo.getUserName());
                    user.setNickName(userInfo.getNickName());
                    user.setRoleName(userInfo.getRole().getRoleName());
                    if(userInfo.getSex() == 1) {
                        user.setSex("男");
                    } else if(userInfo.getSex() == 2) {
                        user.setSex("女");
                    } else {
                        user.setSex("");
                    }
                    user.setPhone(userInfo.getPhone());
                    user.setEmail(userInfo.getEmail());
                    user.setRemark(userInfo.getRemark());
                    list.add(user);
                }

                // 设置响应头
                response.setContentType("application/vnd.ms-excel");
                response.setCharacterEncoding("utf-8");
                response.setHeader("Content-disposition", "attachment;filename=" + ExcelUtil.getExcelFileName("用户信息") + ".xlsx");

                // 写入Excel
                EasyExcel.write(response.getOutputStream(), UserExp.class)
                        .useDefaultStyle(false)
                        .sheet("Sheet1")
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .doWrite(list);
            }
        }
    }
}
