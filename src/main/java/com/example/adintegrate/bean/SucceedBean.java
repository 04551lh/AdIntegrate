package com.example.adintegrate.bean;

import java.io.Serializable;

/**
 * Created by dell on 2019/12/2 11:04
 * Description:
 * Emain: 1187278976@qq.com
 */
public class SucceedBean implements Serializable {


    /**
     * flow : 128
     * data : {"tid":12338,"cid":"3e09PwOEwl3hi8o","ip":"127.0.0.1","is_succeed":true,"exec_time":60,"url":"http://baidu.com"}
     */
    private int flow;
    private DataBean data;

    public int getFlow() {
        return flow;
    }

    public void setFlow(int flow) {
        this.flow = flow;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * tid : 12338
         * cid : 3e09PwOEwl3hi8o
         * ip : 127.0.0.1
         * is_succeed : true
         * exec_time : 60
         * url : http://baidu.com
         */

        private int tid;
        private String cid;
        private String ip;
        private boolean is_succeed;
        private int exec_time;
        private String url;

        public int getTid() {
            return tid;
        }

        public void setTid(int tid) {
            this.tid = tid;
        }

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public boolean isIs_succeed() {
            return is_succeed;
        }

        public void setIs_succeed(boolean is_succeed) {
            this.is_succeed = is_succeed;
        }

        public int getExec_time() {
            return exec_time;
        }

        public void setExec_time(int exec_time) {
            this.exec_time = exec_time;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
