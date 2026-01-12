package org.bem.iot.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.bem.iot.entity.BlacklistImp;

import java.util.ArrayList;
import java.util.List;

public class BlacklistListener extends AnalysisEventListener<BlacklistImp> {
    public final List<BlacklistImp> list = new ArrayList<>();

    @Override
    public void invoke(BlacklistImp blacklist, AnalysisContext analysisContext) {
        list.add(blacklist);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
