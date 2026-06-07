package com.maritime.bunkering.rule;

import com.maritime.bunkering.entity.ShipCert;
import com.maritime.bunkering.mapper.ShipCertMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class CertExpireValidator {

    @Autowired
    private ShipCertMapper shipCertMapper;

    public ValidationResult validate(String shipCode, Date applyDate) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);

        List<ShipCert> certs = shipCertMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ShipCert>()
                        .eq("ship_code", shipCode)
                        .eq("deleted", 0)
        );

        if (certs == null || certs.isEmpty()) {
            result.setValid(false);
            result.setMessage("船舶【" + shipCode + "】未查询到有效证书，无法进行加注作业");
            return result;
        }

        StringBuilder expiredCerts = new StringBuilder();
        for (ShipCert cert : certs) {
            if (cert.getCertStatus() != null && cert.getCertStatus() == 0) {
                if (expiredCerts.length() > 0) {
                    expiredCerts.append("、");
                }
                expiredCerts.append(cert.getCertType());
                continue;
            }
            if (cert.getExpiryDate() != null && applyDate.after(cert.getExpiryDate())) {
                if (expiredCerts.length() > 0) {
                    expiredCerts.append("、");
                }
                expiredCerts.append(cert.getCertType())
                        .append("(有效期至:")
                        .append(cert.getExpiryDate())
                        .append(")");
            }
        }

        if (expiredCerts.length() > 0) {
            result.setValid(false);
            result.setMessage(
                    "船舶证书已过期，拦截加注申请。过期证书: " + expiredCerts.toString() +
                            "。请更新证书后重新提交申请。"
            );
        }

        return result;
    }

    public static class ValidationResult {
        private boolean valid;
        private String message;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
