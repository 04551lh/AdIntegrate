package com.example.adintegrate.bean;

import java.io.Serializable;

/**
 * Created by dell on 2019/12/13 17:06
 * Description:
 * Emain: 1187278976@qq.com
 */
public class ReplaceUrlBean implements Serializable {
    private String tag;

    public ReplaceUrlBean(String tag, String value, String target) {
        this.tag = tag;
        this.value = value;
        this.target = target;
    }

    @Override
    public String toString() {
        return "ReplaceUrlBean{" +
                "tag='" + tag + '\'' +
                ", value='" + value + '\'' +
                ", target='" + target + '\'' +
                '}';
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    private String value;
    private String target;
}
