package org.charlotte.e2ecore.utils;

import java.util.regex.Pattern;

public class MongoUtils {

    public static final String DOC_CASE = "d_case";
    public static final String DOC_PROJECT = "d_project";
    public static final String DOC_CHAIN = "d_chain";
    public static final String DOC_CHAIN_DATA = "d_chain_data";

    public static final String DOC_PROJECT_CASE_COUNT = "d_project_case_count";
    public static final String DOC_CATEGORY = "d_category";

    public static final Integer DEFAULT_LIMIT_NUM_FOR_EXECUTION_HISTORY = 5;

    public static final Integer DEFAULT_LIMIT_NUM_FOR_PROJECT_CASE_COUNT = 20;
    public static final Integer DEFAULT_LIMIT_NUM = 2000;

    public static Pattern buildFuzzySearchRegex(String searchKey) {
        String regexStr = ".*?" + searchKey + ".*";
        return Pattern.compile(regexStr);
    }
}
