package com.miracle.srch.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountDao extends JpaRepository<Keyword, Integer> {

    List<Keyword> findTop10ByOrderByCountDesc();
}
