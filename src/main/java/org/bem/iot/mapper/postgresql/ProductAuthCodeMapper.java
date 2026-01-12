package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.bem.iot.model.product.ProductAuthCode;

@Mapper
public interface ProductAuthCodeMapper extends BaseMapper<ProductAuthCode> {
}
