package com.certimate.repository;

import com.certimate.domain.UserCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserCertificationRepository extends JpaRepository<UserCertification, Long> {
    int countByUser_Id(Long userId);
    void deleteByUser_Id(Long userId);
}
