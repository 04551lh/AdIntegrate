package com.example.adintegrate.bean;

import java.util.List;

/**
 * Created by dell on 2019/11/29 14:57
 * Description:
 * Emain: 1187278976@qq.com
 */
public class TaskBean {


    /**
     * taskstatus : 0
     * data : {"status":200,"message":{"exec_type":"imp_url","exec_code":"http://active.bjddcy.com/rtc/rand_req_test.php","ua":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/604.4.7 (KHTML, like Gecko) Version/11.0.2 Safari/604.4.7","exec_times":"6","tid":"2","resolution":{"h":900,"w":1440},"stay_time":"60","app_name":"","referer":"https://www.baidu.com"}}
     */

    private int taskstatus;
    private DataBean data;

    public int getTaskstatus() {
        return taskstatus;
    }

    public void setTaskstatus(int taskstatus) {
        this.taskstatus = taskstatus;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * status : 200
         * message : {"exec_type":"imp_url","exec_code":"http://active.bjddcy.com/rtc/rand_req_test.php","ua":"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/604.4.7 (KHTML, like Gecko) Version/11.0.2 Safari/604.4.7","exec_times":"6","tid":"2","resolution":{"h":900,"w":1440},"stay_time":"60","app_name":"","referer":"https://www.baidu.com"}
         */

        private int status;
        private MessageBean message;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public MessageBean getMessage() {
            return message;
        }

        public void setMessage(MessageBean message) {
            this.message = message;
        }

        public static class MessageBean {
            /**
             * tid : 12338
             * exec_type : imp_url
             * exec_code : http://www.baidu.com
             * exec_times : 6
             * stay_time : 60
             * referer : {"values":["http://g.cn","https://www.cctv.com"],"target":"ALL"}
             * ua :
             * device_id : {"imei":"902376503427590432","idfa":"idfa2393-0983248-394903-32948"}
             * app_name : mojitianqi
             * resolution : {"w":"720","h":"1080"}
             * replace : [{"tag":"{ip}","value":"__IP__","target":"DIRECT"},{"tag":"{{UUID}}","value":"8jer23Feopsr90234eQEWr0923","target":"INDIRECT"},{"tag":"{{_os_}}","value":"__OS__","target":"ALL"}]
             */

            private String tid;
            private String exec_type;
            private String exec_code;
            private String exec_times;
            private String stay_time;
            private RefererBean referer;
            private String ua;
            private DeviceIdBean device_id;
            private String app_name;
            private ResolutionBean resolution;
            private List<ReplaceBean> replace;

            public String getTid() {
                return tid;
            }

            public void setTid(String tid) {
                this.tid = tid;
            }

            public String getExec_type() {
                return exec_type;
            }

            public void setExec_type(String exec_type) {
                this.exec_type = exec_type;
            }

            public String getExec_code() {
                return exec_code;
            }

            public void setExec_code(String exec_code) {
                this.exec_code = exec_code;
            }

            public String getExec_times() {
                return exec_times;
            }

            public void setExec_times(String exec_times) {
                this.exec_times = exec_times;
            }

            public String getStay_time() {
                return stay_time;
            }

            public void setStay_time(String stay_time) {
                this.stay_time = stay_time;
            }

            public RefererBean getReferer() {
                return referer;
            }

            public void setReferer(RefererBean referer) {
                this.referer = referer;
            }

            public String getUa() {
                return ua;
            }

            public void setUa(String ua) {
                this.ua = ua;
            }

            public DeviceIdBean getDevice_id() {
                return device_id;
            }

            public void setDevice_id(DeviceIdBean device_id) {
                this.device_id = device_id;
            }

            public String getApp_name() {
                return app_name;
            }

            public void setApp_name(String app_name) {
                this.app_name = app_name;
            }

            public ResolutionBean getResolution() {
                return resolution;
            }

            public void setResolution(ResolutionBean resolution) {
                this.resolution = resolution;
            }

            public List<ReplaceBean> getReplace() {
                return replace;
            }

            public void setReplace(List<ReplaceBean> replace) {
                this.replace = replace;
            }

            public static class RefererBean {
                /**
                 * values : ["http://g.cn","https://www.cctv.com"]
                 * target : ALL
                 */

                private String target;
                private List<String> values;

                public String getTarget() {
                    return target;
                }

                public void setTarget(String target) {
                    this.target = target;
                }

                public List<String> getValues() {
                    return values;
                }

                public void setValues(List<String> values) {
                    this.values = values;
                }
            }

            public static class DeviceIdBean {
                /**
                 * imei : 902376503427590432
                 * idfa : idfa2393-0983248-394903-32948
                 */

                private String imei;
                private String idfa;

                public String getImei() {
                    return imei;
                }

                public void setImei(String imei) {
                    this.imei = imei;
                }

                public String getIdfa() {
                    return idfa;
                }

                public void setIdfa(String idfa) {
                    this.idfa = idfa;
                }
            }

            public static class ResolutionBean {
                /**
                 * w : 720
                 * h : 1080
                 */

                private String w;
                private String h;

                public String getW() {
                    return w;
                }

                public void setW(String w) {
                    this.w = w;
                }

                public String getH() {
                    return h;
                }

                public void setH(String h) {
                    this.h = h;
                }
            }

            public static class ReplaceBean {
                /**
                 * tag : {ip}
                 * value : __IP__
                 * target : DIRECT
                 */

                private String tag;
                private String value;
                private String target;

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
            }
        }
    }
}
