package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import org.bem.iot.model.device.Device;
import org.bem.iot.model.device.DeviceUser;
import org.bem.iot.model.user.UserInfo;

@Mapper
public interface DeviceUserMapper extends BaseMapper<DeviceUser> {
}
