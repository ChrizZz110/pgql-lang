/*
 * Copyright (C) 2013 - 2019 Oracle and/or its affiliates. All rights reserved.
 */
package oracle.pgql.lang.ir;

public abstract class GraphPatternElement extends QueryVariable {

  QueryVariable correlationVariableFromOuterQuery; // null if this variable is not correlated

  public GraphPatternElement(String name, boolean anonymous) {
    super(name, anonymous);
  }

  public GraphPatternElement(String name, String uniqueIdentifier, boolean anonymous,
      QueryVariable correlationVariableFromOuterQuery) {
    super(name, uniqueIdentifier, anonymous);
    this.correlationVariableFromOuterQuery = correlationVariableFromOuterQuery;
  }

  public QueryVariable getCorrelationVariableFromOuterQuery() {
    return correlationVariableFromOuterQuery;
  }

  public void setCorrelationVariableFromOuterQuery(QueryVariable correlationVariableFromOuterQuery) {
    this.correlationVariableFromOuterQuery = correlationVariableFromOuterQuery;
  }
}
