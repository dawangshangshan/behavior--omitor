package com.dawang.monitor.domain.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GatherNodeExpressionVO {
    private String monitorId;
    private String monitorNodeId;
    private String gatherSystemName;
    private String gatherClazzName;
    private String gatherMethodName;
    private List<Filed> fileds;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Filed {
        // 日志名称
        private String logName;
        // 解析顺序；第几个字段
        private Integer logIndex;
        // 字段类型；Object、String
        private String logType;
        // 属性名称
        private String attributeName;
        // 属性字段
        private String attributeField;
        // 解析公式
        private String attributeOgnl;
    }
}
