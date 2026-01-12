package org.bem.iot.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.bem.iot.entity.RoleImp;

import java.util.ArrayList;
import java.util.List;

public class RoleListener extends AnalysisEventListener<RoleImp> {
    public final List<RoleImp> list = new ArrayList<>();

    @Override
    public void invoke(RoleImp role, AnalysisContext analysisContext) {
        list.add(role);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
