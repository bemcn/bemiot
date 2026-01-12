package org.bem.iot.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.bem.iot.config.head.PublicHeadLimit;
import org.bem.iot.model.general.Certificate;
import org.bem.iot.service.CertificateService;
import org.bem.iot.service.LogSystemService;
import org.bem.iot.util.ResponseUtil;
import org.bem.iot.validate.group.Add;
import org.bem.iot.validate.group.Edit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 证书管理
 * @author jakybland
 */
@RestController
@RequestMapping("/certificate")
public class CertificateController {
    @Resource
    CertificateService certificateService;

    @Resource
    LogSystemService logSystemService;

    @Resource
    HttpServletRequest request;

    @Resource
    HttpServletResponse response;

    /**
     * 获取证书列表
     */
    @GetMapping("/getCertificateList")
    @PublicHeadLimit
    @ResponseBody
    public void getCertificateList(@RequestParam(name="key", defaultValue = "") String key) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Certificate> example = new QueryWrapper<>();
            if(!StrUtil.isEmpty(key)) {
                example.like("certificate_name", key);
            }
            example.orderByDesc("certificate_id");
            List<Certificate> list = certificateService.select(example);

            jsonObject = ResponseUtil.getSuccessJson(list);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取证书分页列表
     * @param key 关键字
     * @param index 页码
     * @param size 每页显示数量
     */
    @GetMapping("/getCertificatePageList")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getCertificatePageList(@RequestParam(name="key", defaultValue = "") String key,
                                @RequestParam(name="index", defaultValue = "1") Integer index,
                                @RequestParam(name="size", defaultValue = "20") Integer size) {
        JSONObject jsonObject;
        try {
            QueryWrapper<Certificate> example = new QueryWrapper<>();
            if(!StrUtil.isEmpty(key)) {
                example.like("certificate_name", key);
            }
            example.orderByDesc("certificate_id");

            IPage<Certificate> page = certificateService.selectPage(example, index, size);

            jsonObject = ResponseUtil.getSuccessJson(page);
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 获取证书
     * @param id id
     */
    @GetMapping("/getCertificate")
    @PublicHeadLimit("user")
    @ResponseBody
    public void getCertificate(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        if(id < 1) {
            jsonObject = ResponseUtil.getErrorJson("id不能为空或小于1");
        } else if(certificateService.existsNotCertificateId(id)) {
            jsonObject = ResponseUtil.getErrorJson("证书信息不存在");
        } else {
            Certificate data = certificateService.find(id);
            jsonObject = ResponseUtil.getSuccessJson(data);
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 新增证书
     * @param record 证书
     */
    @PostMapping("/addCertificate")
    @PublicHeadLimit("user")
    @ResponseBody
    public void addCertificate(@Validated(Add.class) @Valid Certificate record) {
        JSONObject jsonObject;
        try {
            certificateService.insert(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "证书管理", "新增", "新增SSL证书，【名称】" + record.getCertificateName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 编辑证书
     * @param record 证书
     */
    @PostMapping("/editCertificate")
    @PublicHeadLimit("user")
    @ResponseBody
    public void editCertificate(@Validated(Edit.class) @Valid Certificate record) {
        JSONObject jsonObject;
        try {
            certificateService.update(record);

            String accessToken = request.getHeader("accessToken");
            logSystemService.insert(accessToken, "证书管理", "修改", "修改SSL证书，【名称】" + record.getCertificateName());

            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除证书
     * @param id id
     */
    @GetMapping("/delCertificate")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delCertificate(@RequestParam(name="id", defaultValue="0") int id) {
        JSONObject jsonObject;
        try {
            if(!certificateService.existsNotCertificateId(id)) {
                Certificate certificate = certificateService.find(id);
                String certificateName = certificate.getCertificateName();
                int count = certificateService.del(id);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "证书管理", "删除", "删除SSL证书，【名称】" + certificateName);
                }
            }
            jsonObject = ResponseUtil.getSuccessJson();
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }

    /**
     * 删除多个证书
     * @param ids id集合
     */
    @GetMapping("/delCertificates")
    @PublicHeadLimit("user")
    @ResponseBody
    public void delCertificates(@RequestParam(name="ids", defaultValue="") String ids) {
        JSONObject jsonObject;
        try {
            if(StrUtil.isEmpty(ids)) {
                jsonObject = ResponseUtil.getErrorJson("id集合不能为空");
            } else {
                List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).toList();
                int count = certificateService.delArray(idList);
                if(count > 0) {
                    String accessToken = request.getHeader("accessToken");
                    logSystemService.insert(accessToken, "证书管理", "删除", "批量删除" + count + "个SSL证书");
                }
                jsonObject = ResponseUtil.getSuccessJson();
            }
        } catch (Exception e) {
            jsonObject = ResponseUtil.getErrorJson(e.getMessage());
        }
        ResponseUtil.responseData(jsonObject, response);
    }
}
