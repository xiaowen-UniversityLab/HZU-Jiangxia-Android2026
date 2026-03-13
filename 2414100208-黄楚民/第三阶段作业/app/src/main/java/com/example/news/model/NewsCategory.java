package com.example.news.model;

public class NewsCategory {
    public static final String TOP = "top";
    public static final String GUONEI = "guonei";
    public static final String GUOJI = "guoji";
    public static final String YULE = "yule";
    public static final String TIYU = "tiyu";
    public static final String JUNSHI = "junshi";
    public static final String KEJI = "keji";
    public static final String CAIJING = "caijing";
    public static final String YOUXI = "youxi";
    public static final String QICHE = "qiche";
    public static final String JIANKANG = "jiankang";
    
    public static final String[] CATEGORIES = {
        TOP, GUONEI, GUOJI, YULE, TIYU, JUNSHI, KEJI, CAIJING, YOUXI, QICHE, JIANKANG
    };
    
    public static final String[] CATEGORY_NAMES = {
        "推荐", "国内", "国际", "娱乐", "体育", "军事", "科技", "财经", "游戏", "汽车", "健康"
    };
    
    public static String getCategoryName(String category) {
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i].equals(category)) {
                return CATEGORY_NAMES[i];
            }
        }
        return category;
    }
}
