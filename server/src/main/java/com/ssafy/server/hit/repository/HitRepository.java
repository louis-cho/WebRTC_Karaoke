package com.ssafy.server.hit.repository;

import com.ssafy.server.hit.model.Hit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HitRepository extends JpaRepository<Hit, Integer> {

}
