package org.bem.iot.mapper.postgresql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.bem.iot.model.product.ProductClass;

@Mapper
public interface ProductClassMapper extends BaseMapper<ProductClass> {
    @Select("SELECT COALESCE(MAX(order_num), 0) FROM product_class WHERE level_id=#{levelId}")
    Integer selectMax(@Param("levelId") Integer levelId);
}
