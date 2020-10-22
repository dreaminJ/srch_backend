package com.miracle.srch.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeywordDao extends JpaRepository<Keyword, String> {
}
