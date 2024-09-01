package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.entity.Relation;

import java.util.Optional;

public interface RelationService {

    Optional<Relation> getRelationByUuid(String uuid);
}
