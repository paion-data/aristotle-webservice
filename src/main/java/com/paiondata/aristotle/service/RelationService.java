package com.paiondata.aristotle.service;

import com.paiondata.aristotle.model.entity.Relation;

import java.util.List;
import java.util.Optional;

public interface RelationService {

    Optional<List<Relation>> getRelationByUuid(String uuid);
}
