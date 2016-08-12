package ch.hsr.isf.serepo.client.webapp.view.consistencies.relations;

import java.util.List;

import ch.hsr.isf.serepo.data.restinterface.consistency.relation.RelationInconsistency;

public interface IRelationConsistencyView {

  void setInconsistencies(List<RelationInconsistency> inconsistencies);

}
