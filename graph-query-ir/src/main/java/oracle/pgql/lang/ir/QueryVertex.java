/*
 * Copyright (C) 2013 - 2019 Oracle and/or its affiliates. All rights reserved.
 */
package oracle.pgql.lang.ir;

public class QueryVertex extends GraphPatternElement {

  public QueryVertex(String name, boolean anonymous) {
    this(name, name, anonymous, null);
  }

  public QueryVertex(String name, String uniqueIdentifier, boolean anonymous,
      QueryVariable correlationVariableFromOuterQuery) {
    super(name, uniqueIdentifier, anonymous, correlationVariableFromOuterQuery);
  }

  @Override
  public VariableType getVariableType() {
    return VariableType.VERTEX;
  }

  @Override
  public String toString() {
    if (isAnonymous()) {
      return "()";
    } else {
      return "(" + name + ")";
    }
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return 31;
  }

  public void accept(QueryExpressionVisitor v) {
    v.visit(this);
  }
}
