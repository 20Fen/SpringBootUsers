package com.config;

import java.util.Map;

/**
 * Description:
 */
public class AjaxResult {

    public static final int		AJAX_STATUS_SUCCESS		= 0;
    public static final int		AJAX_STATUS_ERROR		= -999;
    public static final String	AJAX_MESSAGE_SUCCESS	= "success";
    /**
     * 返回的中文消息
     */
    private String				message;
    /**
     * 成功时携带的数据
     */
    private Object				results;
    /**
     * 返回状态码
     */
    private int					statuscode;

    public AjaxResult(){
    }

    public AjaxResult(int statuscode){
        this.statuscode = statuscode;
    }

    public AjaxResult(String message){
        this.message = message;
    }

    public Object getResults() {
        return results;
    }

    public AjaxResult setResults(Object results) {
        this.results = results;
        return this;
    }

    public int getStatuscode() {
        return statuscode;
    }

    public AjaxResult setStatuscode(int statuscode) {
        this.statuscode = statuscode;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public AjaxResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public AjaxResult addSuccess(String message) {
        this.statuscode = AJAX_STATUS_SUCCESS;
        this.message = message;
        return this;
    }

    public AjaxResult addError(String message) {
        this.statuscode = AJAX_STATUS_ERROR;
        this.message = message;
        this.results = null;
        return this;
    }

    public AjaxResult addFail(String message) {
        this.statuscode = AJAX_STATUS_ERROR;
        this.message = message;
        this.results = null;
        return this;
    }

    public AjaxResult addWarn(String message) {
        this.statuscode = AJAX_STATUS_ERROR;
        this.message = message;
        this.results = null;
        return this;
    }

    public AjaxResult success(Object data) {
        this.message = AJAX_MESSAGE_SUCCESS;
        this.results = data;
        return this;
    }

    /**
     * 项目: SF_Common
     * 描述: ajax响应重构
     * 作者: zhangyi183790
     * 时间: 2019年3月13日 下午3:22:41
     * 版本: v1.0
     * JDK: 1.8
     */
    public static class PAjaxResult extends AjaxResult {
        private int		pageNum;
        private int		pageSize;
        private long	total;

        public PAjaxResult(){
            super(AJAX_STATUS_SUCCESS);
        }

        public PAjaxResult(int status){
            super(status);
        }

        public PAjaxResult(int pageNum, int pageSize, long total, Object results){
            this();
            this.pageNum = pageNum;
            this.pageSize = pageSize;
            this.total = total;
            super.results = results;
        }

        public int getPageNum() {
            return pageNum;
        }

        public PAjaxResult setPageNum(int pageNum) {
            this.pageNum = pageNum;
            return this;
        }

        public int getPageSize() {
            return pageSize;
        }

        public PAjaxResult setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public long getTotal() {
            return total;
        }

        public PAjaxResult setTotal(long total) {
            this.total = total;
            return this;
        }
    }
}
