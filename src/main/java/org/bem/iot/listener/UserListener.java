package org.bem.iot.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import org.bem.iot.entity.UserImp;

import java.util.ArrayList;
import java.util.List;

public class UserListener extends AnalysisEventListener<UserImp> {
    public final List<UserImp> list = new ArrayList<>();

    @Override
    public void invoke(UserImp user, AnalysisContext analysisContext) {
        list.add(user);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
    }
}
