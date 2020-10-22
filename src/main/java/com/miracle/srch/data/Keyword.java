package com.miracle.srch.data;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
@Slf4j
public class Keyword {
    @Id
    private String title;
    Integer count;

    @Builder
    public Keyword ( String title ) {
        this.title = title;
        this.count = 1;
        log.info("keyword insert "+title);
    }
}
