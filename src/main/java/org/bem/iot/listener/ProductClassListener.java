package org.bem.iot.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.bem.iot.entity.ProductClassImp;

import java.util.ArrayList;
import java.util.List;

public class ProductClassListener extends AnalysisEventListener<ProductClassImp> {
    public final List<ProductClassImp> list = new ArrayList<>();

    @Override
    public void invoke(ProductClassImp productClass, AnalysisContext analysisContext) {
        list.add(productClass);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
