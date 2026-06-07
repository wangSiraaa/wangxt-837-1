package com.maritime.bunkering.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maritime.bunkering.common.BusinessException;
import com.maritime.bunkering.entity.ShipCert;
import com.maritime.bunkering.mapper.ShipCertMapper;
import com.maritime.bunkering.rule.CertExpireValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ShipCertService {

    @Autowired
    private ShipCertMapper shipCertMapper;

    @Autowired
    private CertExpireValidator certExpireValidator;

    public ShipCert getById(String id) {
        ShipCert cert = shipCertMapper.selectById(id);
        if (cert == null) {
            throw new BusinessException("船舶证书不存在: " + id);
        }
        return cert;
    }

    public List<ShipCert> getByShipCode(String shipCode) {
        return shipCertMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ShipCert>()
                        .eq("ship_code", shipCode)
                        .eq("deleted", 0)
                        .orderByDesc("create_time")
        );
    }

    public IPage<ShipCert> queryPage(int pageNum, int pageSize, String shipCode, Integer certStatus) {
        Page<ShipCert> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ShipCert> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (shipCode != null && !shipCode.isEmpty()) {
            wrapper.eq("ship_code", shipCode);
        }
        if (certStatus != null) {
            wrapper.eq("cert_status", certStatus);
        }
        wrapper.eq("deleted", 0);
        wrapper.orderByDesc("create_time");
        return shipCertMapper.selectPage(page, wrapper);
    }

    public void validateShipCerts(String shipCode, Date applyDate) {
        certExpireValidator.validate(shipCode, applyDate);
    }

    public List<ShipCert> getExpiredCerts(String shipCode) {
        Date now = new Date();
        return shipCertMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ShipCert>()
                        .eq("ship_code", shipCode)
                        .eq("deleted", 0)
                        .and(w -> w.eq("cert_status", 0)
                                .or()
                                .lt("expiry_date", now))
                        .orderByAsc("expiry_date")
        );
    }

    public ShipCert create(ShipCert cert) {
        if (cert.getId() == null || cert.getId().isEmpty()) {
            cert.setId("CERT" + System.currentTimeMillis());
        }
        if (cert.getCertStatus() == null) {
            cert.setCertStatus(1);
        }
        shipCertMapper.insert(cert);
        return cert;
    }

    public ShipCert update(ShipCert cert) {
        ShipCert existing = shipCertMapper.selectById(cert.getId());
        if (existing == null) {
            throw new BusinessException("船舶证书不存在: " + cert.getId());
        }
        shipCertMapper.updateById(cert);
        return shipCertMapper.selectById(cert.getId());
    }
}
