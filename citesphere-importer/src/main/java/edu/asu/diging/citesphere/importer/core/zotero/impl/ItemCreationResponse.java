package edu.asu.diging.citesphere.importer.core.zotero.impl;

import java.util.Map;

public class ItemCreationResponse {

    private Map<String, Object> successful;
    private Map<String, String> unchanged;
    private Map<String, FailedMessage> failed;
    
    public Map<String, Object> getSuccessful() {
        return successful;
    }
    public void setSuccess(Map<String, Object> success) {
        this.successful = success;
    }
    public Map<String, String> getUnchanged() {
        return unchanged;
    }
    public void setUnchanged(Map<String, String> unchanged) {
        this.unchanged = unchanged;
    }
    public Map<String, FailedMessage> getFailed() {
        return failed;
    }
    public void setFailed(Map<String, FailedMessage> failed) {
        this.failed = failed;
    }
    
    public static class FailedMessage {
        private Integer code;
        private String message;
        
        public Integer getCode() {
            return code;
        }
        public void setCode(Integer code) {
            this.code = code;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        
        @Override
        public String toString() {
            return "FailedMessage [code=" + code + ", message=" + message + "]";
        }
        
    }

}
