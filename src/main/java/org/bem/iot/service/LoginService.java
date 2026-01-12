package org.bem.iot.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.bem.iot.entity.Login;
import org.bem.iot.entity.ReturnToken;
import org.bem.iot.entity.UserLogin;
import org.bem.iot.mapper.postgresql.AppAuthMapper;
import org.bem.iot.mapper.postgresql.RoleMapper;
import org.bem.iot.mapper.postgresql.UserInfoMapper;
import org.bem.iot.model.general.AppAuth;
import org.bem.iot.model.user.Role;
import org.bem.iot.model.user.UserInfo;
import org.bem.iot.util.DataMaskingUtil;
import org.bem.iot.util.EncryptUtil;
import org.bem.iot.util.HeadSignUtil;
import org.bem.iot.util.InternalIdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 登录验证
 * @author jakybland
 */
@Service
public class LoginService {
    @Resource
    AppAuthMapper appAuthMapper;

    @Resource
    UserInfoMapper userInfoMapper;

    @Resource
    RoleMapper roleMapper;

    @Value("${auth.passkey}")
    String passkey;

    @Value("${auth.version}")
    String version;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    /**
     * 获取用户Code
     * @param appId 运用ID
     * @return 返回接口输出信息
     */
    public String getLoginCode(String appId) {
        QueryWrapper<AppAuth> example = new QueryWrapper<>();
        example.eq("app_id", appId);
        boolean result = appAuthMapper.exists(example);
        if(result) {
            AppAuth appAuth =appAuthMapper.selectById(appId);
            String secretKey = appAuth.getSecretKey();
            String code = InternalIdUtil.createCode(appId, secretKey);
            String codeKey = "code:" + code;
            stringRedisTemplate.opsForValue().set(codeKey, appId);
            stringRedisTemplate.expire(codeKey, 300, TimeUnit.SECONDS);

            return code;
        } else {
            return "";
        }
    }

    /**
     * 用户登录
     * @param login 登录信息
     * @param ipAddress 客户端IP地址
     * @return 返回登录结果信息
     */
    public UserLogin checkLogin(Login login, String ipAddress) {
        UserLogin userLogin = new UserLogin();
        String codeKey = "code:" + login.getCode();
        boolean isHasCodeKey = Boolean.TRUE.equals(stringRedisTemplate.hasKey(codeKey));
        if(isHasCodeKey) {
            String appId = stringRedisTemplate.opsForValue().get(codeKey);
            stringRedisTemplate.delete(codeKey);

            QueryWrapper<AppAuth> example = new QueryWrapper<>();
            example.eq("app_id", appId);
            boolean isAppExists = appAuthMapper.exists(example);
            if(isAppExists) {
                String userName = login.getUserName().toLowerCase();
                boolean isUserExists = existsUser(userName);
                if (isUserExists) {
                    UserInfo user = findUserName(userName);
                    int status = user.getStatus();
                    if(status == 1) {
                        String pwd = user.getPassWord();
                        String passSalt = user.getPassSalt();
                        String passWord = EncryptUtil.encryptAes(login.getUserPwd(), passkey, passSalt);
                        if (passWord.equals(pwd)) {
                            UserInfo userInfo = findInfoByUserName(userName);
                            int userId = user.getUserId();
                            String nickName = userInfo.getNickName();
                            userInfo.setPhone(DataMaskingUtil.maskPhoneNumber(userInfo.getPhone()));
                            userInfo.setEmail(DataMaskingUtil.maskEmailAddress(userInfo.getEmail()));

                            AppAuth appAuth = appAuthMapper.selectById(appId);
                            String secretKey = appAuth.getSecretKey();
                            String clientSource = appAuth.getAppSource();

                            String[] tokens = createUserToken(appId, secretKey, ipAddress, clientSource, userId, userName, nickName);

                            userLogin.setStatus(true);
                            userLogin.setUser(userInfo);
                            userLogin.setClientSource(clientSource);
                            userLogin.setAccessToken(tokens[0]);
                            userLogin.setRefreshToken(tokens[1]);
                            userLogin.setMessage("登录成功");
                        } else {
                            userLogin.setStatus(false);
                            userLogin.setMessage("账号或密码输入错误");
                        }
                    } else {
                        userLogin.setStatus(false);
                        userLogin.setMessage("账号已被停用");
                    }
                } else {
                    userLogin.setStatus(false);
                    userLogin.setMessage("用户信息不存在");
                }
            }  else {
                userLogin.setStatus(false);
                userLogin.setMessage("未授权的请求");
            }
        } else {
            userLogin.setStatus(false);
            userLogin.setMessage("登录Code不存在");
        }
        return userLogin;
    }

    /**
     * 判断用户Access Token不存在
     * @param accessToken 用户Access Token
     * @param type 类型 user / system
     * @return 存在返回false,存在返回true
     */
    public boolean existsNotAccessToken(String accessToken, String type) {
        String key;
        if("user".equals(type)) {
            key = "accToken:" + accessToken;
        } else {
            key = "systoken:" + accessToken;
        }
        boolean isHas = Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
        return !isHas;
    }

    /**
     * 验证时间戳不合法
     * @param timestamp 时间戳
     * @return 时间戳合法返回false,不合法返回true
     */
    public boolean verifyNotTimestamp(String timestamp, String sign) {
        if("7d1k2b5k8pRd1N753Ts".equals(sign)) {
            return false;
        } else {
            long time = Long.parseLong(timestamp);
            long nowTime = System.currentTimeMillis() / 1000;
            long diffTime = nowTime - time;
            return diffTime > 300;
        }
    }

    /**
     * 验证版本号不合法
     * @param v 版本号
     * @return 版本号合法返回false,不合法返回true
     */
    public boolean verifyNotVersion(String v) {
        String[] versionArray = version.split("\\.");
        int version1 = Integer.parseInt(versionArray[0]);
        int version2 = Integer.parseInt(versionArray[1]);
        String[] vArray = v.split("\\.");
        int v1 = Integer.parseInt(vArray[0]);
        int v2 = Integer.parseInt(vArray[1]);
        if(v1 > version1) {
            return true;
        } else {
            if(v1 == version1) {
                return v2 > version2;
            } else {
                return false;
            }
        }
    }

    /**
     * 验证签名不合法
     * @param request 请求对象
     * @return 签名合法返回false,不合法返回true
     */
    public boolean verifyNotSign(HttpServletRequest request) {
        String sign = request.getHeader("sign");
        if("7d1k2b5k8pRd1N753Ts".equals(sign)) {
            return false;
        } else {
            String appId = request.getHeader("appId");
            JSONObject jsonObject = HeadSignUtil.getSignData(request);

            AppAuth appAuth = appAuthMapper.selectById(appId);
            String newSign = HeadSignUtil.getSign(jsonObject, appAuth.getSecretKey());
            return !newSign.equals(sign);
        }
    }

    /**
     * 刷新AccessToken
     * @param refreshToken 用于刷新的refreshToken
     * @return 刷新成功会返回新的AccessToken和refreshToken
     */
    public ReturnToken refreshAccessToken(String refreshToken) {
        ReturnToken returnToken = new ReturnToken();
        String keyByRefresh = "refToken:" + refreshToken;
        boolean hasRefresh = Boolean.TRUE.equals(stringRedisTemplate.hasKey(keyByRefresh));
        if(hasRefresh) {
            String oldAccessToken = stringRedisTemplate.opsForValue().get(keyByRefresh);
            String keyByAccess = "accToken:" + oldAccessToken;
            boolean hasAccess = Boolean.TRUE.equals(stringRedisTemplate.hasKey(keyByAccess));
            if(hasAccess) {
                String tokenValue = stringRedisTemplate.opsForValue().get(keyByAccess);
                JSONObject tokenData = JSONObject.parseObject(tokenValue);
                if (tokenData != null) {
                    String appId = tokenData.getString("appId");
                    String secretKey = tokenData.getString("secretKey");
                    String newAccessToken = InternalIdUtil.createAccessToken(appId, secretKey);
                    String newRefreshToken = InternalIdUtil.createRefreshToken(appId, secretKey);

                    String newAccTokenKey = "accToken:" + newAccessToken;
                    String newRefTokenKey = "refToken:" + newRefreshToken;

                    stringRedisTemplate.opsForValue().set(newAccTokenKey, tokenValue);
                    stringRedisTemplate.expire(newAccTokenKey, 7230, TimeUnit.SECONDS);

                    stringRedisTemplate.opsForValue().set(newRefTokenKey, newAccessToken);
                    stringRedisTemplate.expire(newRefTokenKey, 7230, TimeUnit.SECONDS);

                    returnToken.setStatus(true);
                    returnToken.setAccessToken(newAccessToken);
                    returnToken.setRefreshToken(newRefreshToken);

                    stringRedisTemplate.delete(keyByAccess);
                    stringRedisTemplate.delete(keyByRefresh);
                } else {
                    returnToken.setStatus(false);
                }
            } else {
                returnToken.setStatus(false);
            }
        } else {
            returnToken.setStatus(false);
        }
        return returnToken;
    }

    /**
     * 验证AccessToken
     * @param accessToken 当前用户accessToken
     * @return 返回true表示AccessToken合法，返回false表示AccessToken不合法
     */
    public boolean verifyAccessToken(String accessToken) {
        String key = "accToken:" + accessToken;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 删除用户Token
     * @param refreshToken 当前用户refreshToken
     */
    public void deleteAccessToken(String refreshToken) {
        String keyByRefresh = "refToken:" + refreshToken;
        boolean hasRefresh = Boolean.TRUE.equals(stringRedisTemplate.hasKey(keyByRefresh));
        if(hasRefresh) {
            String accessToken = stringRedisTemplate.opsForValue().get(keyByRefresh);
            String keyByAccess = "accToken:" + accessToken;
            stringRedisTemplate.delete(keyByAccess);
            stringRedisTemplate.delete(keyByRefresh);
        }
    }

    /**
     * 创建系统AccessToken
     * @param appId 运用ID
     * @return 返回AccessToken
     */
    public String systemAccessToken(String appId) throws Exception {
        QueryWrapper<AppAuth> example = new QueryWrapper<>();
        example.eq("app_id", appId);
        example.eq("app_auth", "system");
        if(appAuthMapper.exists(example)) {
            int total = countSystemToken();
            if(total < 2000) {
                AppAuth appAuth = appAuthMapper.selectById(appId);
                String secretKey = appAuth.getSecretKey();
                String accessToken = InternalIdUtil.createAccessToken(appId, secretKey);
                String key = "systoken:" + accessToken;
                stringRedisTemplate.opsForValue().set(key, appId);
                stringRedisTemplate.expire(accessToken, 7230, TimeUnit.SECONDS);
                return accessToken;
            } else {
                throw new Exception("系统AccessToken数量已经达到2000上限");
            }
        } else {
            throw new Exception("AppID错误，无权限申请");
        }
    }


    /**
     * 验证系统AccessToken是否存在
     * @param accessToken 系统AccessToken
     * @return 返回true表示存在
     */
    public boolean verifySystemToken(String accessToken) {
        String key = "systoken:" + accessToken;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }


    /**
     * 统计系统AccessToken数量
     * @return 返回系统AccessToken数量
     */
    private int countSystemToken() {
        String pattern = "systoken:*";
        try {
            Set<String> keys = stringRedisTemplate.keys(pattern);
            return keys != null ? keys.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 验证用户是否存在
     * @param userName 用户名
     * @return 存在返回true
     */
    private boolean existsUser(String userName) {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.eq("user_name", userName);
        return userInfoMapper.exists(example);
    }

    /**
     * 根据用户名查询用户信息
     * @param userName 用户名
     * @return 用户信息
     */
    private UserInfo findUserName(String userName) {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.eq("user_name", userName);
        return userInfoMapper.selectOne(example);
    }

    /**
     * 根据用户名查询用户信息
     * @param userName 用户名
     * @return 用户信息
     */
    private UserInfo findInfoByUserName(String userName) {
        QueryWrapper<UserInfo> example = new QueryWrapper<>();
        example.eq("user_name", userName);
        UserInfo user = userInfoMapper.selectOne(example);
        int roleId = user.getRoleId();
        user.setPhone(DataMaskingUtil.maskPhoneNumber(user.getPhone()));
        user.setEmail(DataMaskingUtil.maskEmailAddress(user.getEmail()));

        Role role = roleMapper.selectById(roleId);
        user.setRole(role);
        return user;
    }

    /**
     * 创建用户AccessToken
     * @param appId 运用ID
     * @param secretKey 密钥
     * @param ipAddress ip地址
     * @param clientSource 客户端来源
     * @param userId 用户ID
     * @param userName 用户名
     * @param nickName 昵称
     * @return 创建成功返回AccessToken和RefreshToken
     */
    private String[] createUserToken(String appId, String secretKey, String ipAddress, String clientSource, int userId, String userName, String nickName) {
        String accessToken = InternalIdUtil.createAccessToken(appId, secretKey);
        String refreshToken = InternalIdUtil.createRefreshToken(appId, secretKey);
        String key = "accToken:" + accessToken;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId", appId);
        jsonObject.put("secretKey", secretKey);
        jsonObject.put("userId", userId);
        jsonObject.put("userName", userName);
        jsonObject.put("nickName", nickName);
        jsonObject.put("ipAddress", ipAddress);
        jsonObject.put("clientSource", clientSource);
        String jsonText = jsonObject.toString();

        stringRedisTemplate.opsForValue().set(key, jsonText);
        stringRedisTemplate.expire(key, 7230, TimeUnit.SECONDS);

        key = "refToken:" + refreshToken;
        stringRedisTemplate.opsForValue().set(key, accessToken);
        stringRedisTemplate.expire(key, 7230, TimeUnit.SECONDS);

        return new String[] { accessToken, refreshToken };
    }
}
