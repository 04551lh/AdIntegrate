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
     * data : {"status":200,"message":{"exec_type":"imp_url","replace":[{"value":"172.16.0.10","target":"ALL","tag":"__IP__"},{"value":"0","target":"ALL","tag":"__OS__"},{"value":"356759048293097","target":"ALL","tag":"__IMEI__"}],"exec_code":["http://gogo-cd.com/impression.html","http://gogo-cd.com/impression.html?w=1"],"ua":"Mozilla/5.0 (Linux; U; Android 4.2; zh-CN; HUAWEI Y511-U10 Build/HUAWEIY511-U10) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1","exec_times":"5","ignore_list":["*.jpeg","https://pics4.baidu.com/feed/*"],"tid":"8","mock_url":"","stay_time":{"min":"10","max":"50"},"resolution":{"h":"720","w":"1280"},"referer":{"target":"DIRECT","values":["http://www.sohu.com/","http://www.zol.com.cn/","http://www.autohome.com.cn/"]}}}
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
         * message : {"exec_type":"imp_url","replace":[{"value":"172.16.0.10","target":"ALL","tag":"__IP__"},{"value":"0","target":"ALL","tag":"__OS__"},{"value":"356759048293097","target":"ALL","tag":"__IMEI__"}],"exec_code":["http://gogo-cd.com/impression.html","http://gogo-cd.com/impression.html?w=1"],"ua":"Mozilla/5.0 (Linux; U; Android 4.2; zh-CN; HUAWEI Y511-U10 Build/HUAWEIY511-U10) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1","exec_times":"5","ignore_list":["*.jpeg","https://pics4.baidu.com/feed/*"],"tid":"8","mock_url":"","stay_time":{"min":"10","max":"50"},"resolution":{"h":"720","w":"1280"},"referer":{"target":"DIRECT","values":["http://www.sohu.com/","http://www.zol.com.cn/","http://www.autohome.com.cn/"]}}
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
             * exec_type : imp_url
             * replace : [{"value":"172.16.0.10","target":"ALL","tag":"__IP__"},{"value":"0","target":"ALL","tag":"__OS__"},{"value":"356759048293097","target":"ALL","tag":"__IMEI__"}]
             * exec_code : ["http://gogo-cd.com/impression.html","http://gogo-cd.com/impression.html?w=1"]
             * ua : Mozilla/5.0 (Linux; U; Android 4.2; zh-CN; HUAWEI Y511-U10 Build/HUAWEIY511-U10) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1
             * exec_times : 5
             * ignore_list : ["*.jpeg","https://pics4.baidu.com/feed/*"]
             * tid : 8
             * mock_url :
             * stay_time : {"min":"10","max":"50"}
             * resolution : {"h":"720","w":"1280"}
             * referer : {"target":"DIRECT","values":["http://www.sohu.com/","http://www.zol.com.cn/","http://www.autohome.com.cn/"]}
             */

            private String exec_type;
            private String ua;
            private String exec_times;
            private String tid;
            private String mock_url;
            private StayTimeBean stay_time;
            private ResolutionBean resolution;
            private RefererBean referer;
            private List<ReplaceBean> replace;
            private List<String> exec_code;
            private List<String> ignore_list;

            public String getExec_type() {
                return exec_type;
            }

            public void setExec_type(String exec_type) {
                this.exec_type = exec_type;
            }

            public String getUa() {
                return ua;
            }

            public void setUa(String ua) {
                this.ua = ua;
            }

            public String getExec_times() {
                return exec_times;
            }

            public void setExec_times(String exec_times) {
                this.exec_times = exec_times;
            }

            public String getTid() {
                return tid;
            }

            public void setTid(String tid) {
                this.tid = tid;
            }

            public String getMock_url() {
                return mock_url;
            }

            public void setMock_url(String mock_url) {
                this.mock_url = mock_url;
            }

            public StayTimeBean getStay_time() {
                return stay_time;
            }

            public void setStay_time(StayTimeBean stay_time) {
                this.stay_time = stay_time;
            }

            public ResolutionBean getResolution() {
                return resolution;
            }

            public void setResolution(ResolutionBean resolution) {
                this.resolution = resolution;
            }

            public RefererBean getReferer() {
                return referer;
            }

            public void setReferer(RefererBean referer) {
                this.referer = referer;
            }

            public List<ReplaceBean> getReplace() {
                return replace;
            }

            public void setReplace(List<ReplaceBean> replace) {
                this.replace = replace;
            }

            public List<String> getExec_code() {
                return exec_code;
            }

            public void setExec_code(List<String> exec_code) {
                this.exec_code = exec_code;
            }

            public List<String> getIgnore_list() {
                return ignore_list;
            }

            public void setIgnore_list(List<String> ignore_list) {
                this.ignore_list = ignore_list;
            }

            public static class StayTimeBean {
                /**
                 * min : 10
                 * max : 50
                 */

                private String min;
                private String max;

                public String getMin() {
                    return min;
                }

                public void setMin(String min) {
                    this.min = min;
                }

                public String getMax() {
                    return max;
                }

                public void setMax(String max) {
                    this.max = max;
                }
            }

            public static class ResolutionBean {
                /**
                 * h : 720
                 * w : 1280
                 */

                private String h;
                private String w;

                public String getH() {
                    return h;
                }

                public void setH(String h) {
                    this.h = h;
                }

                public String getW() {
                    return w;
                }

                public void setW(String w) {
                    this.w = w;
                }
            }

            public static class RefererBean {
                /**
                 * target : DIRECT
                 * values : ["http://www.sohu.com/","http://www.zol.com.cn/","http://www.autohome.com.cn/"]
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

            public static class ReplaceBean {
                /**
                 * value : 172.16.0.10
                 * target : ALL
                 * tag : __IP__
                 */

                private String value;
                private String target;
                private String tag;

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

                public String getTag() {
                    return tag;
                }

                public void setTag(String tag) {
                    this.tag = tag;
                }
            }
        }
    }
}
