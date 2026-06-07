package com.maritime.bunkering.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CertReviewDTO {

    @NotBlank(message = "申请ID不能为空")
    private String applyId;

    @NotNull(message = "复核状态不能为空")
    private Integer reviewStatus;

    private String reviewComment;

    private String reviewer;

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    public Integer getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(Integer reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }
}
