package kr.co.ggabi.addon.Repository;

import kr.co.ggabi.addon.Domain.Addon;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddonRepository extends JpaRepository<Addon, Long> {

    Optional<Addon> findByUserNameAndMailIdAndFileName(String userName, String mailId, String fileName);
    Optional<List<Addon>> findByUserNameAndMailId(String userName, String mailId);

}