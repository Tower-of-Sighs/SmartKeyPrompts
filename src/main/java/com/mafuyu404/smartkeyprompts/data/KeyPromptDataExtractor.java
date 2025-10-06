package com.mafuyu404.smartkeyprompts.data;


import com.mafuyu404.oelib.forge.data.mvel.FunctionUsageAnalyzer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class KeyPromptDataExtractor implements FunctionUsageAnalyzer.DataExpressionExtractor<KeyPromptData> {

    @Override
    public Map<String, String> extractVariables(KeyPromptData data) {
        return data.vars();
    }

    @Override
    public Set<String> extractAllExpressions(KeyPromptData data) {
        Set<String> expressions = new HashSet<>();

        // 提取变量中的表达式
        if (data.vars() != null) {
            expressions.addAll(data.vars().values());
        }

        // 提取条件和动作表达式
        if (data.entries() != null) {
            for (KeyPromptData.Entry entry : data.entries()) {
                // 提取条件表达式
                if (entry.when() != null) {
                    expressions.addAll(entry.when().values());
                }

                // 提取动作表达式
                if (entry.then() != null) {
                    expressions.addAll(entry.then());
                }
            }
        }

        return expressions;
    }
}