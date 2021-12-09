package com.example.springbootconsumer.exception;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;


import static com.example.springbootconsumer.exception.ExceptionMessage.ExceptionType.UNEXPECTED_EXCEPTION;



public class ExceptionMessage {

    public static final String EXCEPTION_MESSAGE_DELIMITER = "<[ExceptionMessage]>: ";

    public static class Builder {

        public static Builder newBuilder() {
            return new Builder();
        }

        private ExceptionMessage exceptionMessage;

        private Builder() {
            exceptionMessage = new ExceptionMessage();
        }

        public Builder withSourceTopic(String sourceTopic) {
            exceptionMessage.setSourceTopic(sourceTopic);
            return this;
        }

        public Builder withOffset(Long offset) {
            exceptionMessage.setOffset(offset);
            return this;
        }

        public Builder withPartition(Integer partition) {
            exceptionMessage.setPartition(partition);
            return this;
        }

        public Builder withTimestamp(Long timestamp) {
            exceptionMessage.setTimestamp(timestamp);
            return this;
        }

        public Builder withKey(byte[] value) {
            String key = null;
            if (value != null) {
                key = new String(value, StandardCharsets.UTF_8);
            }
            exceptionMessage.setKey(key);
            return this;
        }

        public Builder withOriginalRecord(byte[] value) {
            String originalRecord = null;
            if (value != null) {
                originalRecord = new String(value, StandardCharsets.UTF_8);
            }
            exceptionMessage.setOriginalRecord(originalRecord);
            return this;
        }

        public Builder withOriginalRecord(String value) {
            exceptionMessage.setOriginalRecord(value);
            return this;
        }

        public Builder withStackTrace(Throwable t) {


            return this;
        }

        public Builder withAdditionalInfo(String additionalInfo) {
            exceptionMessage.setAdditionalInfo(additionalInfo);

            return this;
        }

        public Builder withSerializationException() {
            exceptionMessage.setExceptionType(ExceptionType.SERIALIZATION_EXCEPTION);
            return this;
        }


        public Builder withUnexpectedException() {
            exceptionMessage.setExceptionType(UNEXPECTED_EXCEPTION);
            return this;
        }

        public ExceptionMessage build() {
            return exceptionMessage;
        }
    }

    public enum ExceptionType {
        SERIALIZATION_EXCEPTION, PRODUCTION_EXCEPTION, UNEXPECTED_EXCEPTION
    }

    private static final ObjectMapper om = new ObjectMapper();

    private String sourceTopic;
    private Long offset;
    private Integer partition;
    private Long timestamp;
    private String key;
    private String originalRecord;
    private String stackTrace;
    private ExceptionType exceptionType;
    private String additionalInfo;


    public String getSourceTopic() {
        return sourceTopic;
    }

    public void setSourceTopic(String sourceTopic) {
        this.sourceTopic = sourceTopic;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOriginalRecord() {
        return originalRecord;
    }

    public void setOriginalRecord(String originalRecord) {
        this.originalRecord = originalRecord;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public ExceptionType getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(ExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }



    public String toJson() {
        try {
            return om.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * maybe it makes sense to add this method here????
     *
     * @param exceptionMessageJsonWithDelimiter
     * @return
     */
    public static ExceptionMessage toExceptionMessageRemoveDelimiter(String exceptionMessageJsonWithDelimiter) {

        var exceptionMessageJsonDelimiterRemoved = exceptionMessageJsonWithDelimiter.replace(EXCEPTION_MESSAGE_DELIMITER, "");

        // this needs more consideration....because its no bueno but was just a quick thing to show the test
        try {
            return om.readValue(exceptionMessageJsonDelimiterRemoved, ExceptionMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String toLogMessage() {
        return EXCEPTION_MESSAGE_DELIMITER + toJson();
    }
}
