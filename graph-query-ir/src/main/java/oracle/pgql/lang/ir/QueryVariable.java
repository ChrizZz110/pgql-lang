/*
 * Copyright (C) 2013 - 2019 Oracle and/or its affiliates. All rights reserved.
 */
package oracle.pgql.lang.ir;

public abstract class QueryVariable {

  public enum VariableType {
    VERTEX,
    EDGE,
    PATH,
    EXP_AS_VAR
  }

  protected String name;

  protected boolean anonymous;

  protected String uniqueIdentifier; // an identifier that is unique throughout the query (note: variable names are not)

  public QueryVariable(String name, String uniqueIdentifier, boolean anonymous) {
    this.name = name;
    this.uniqueIdentifier = uniqueIdentifier;
    this.anonymous = anonymous;
  }

  public QueryVariable(String name, boolean anonymous) {
    this(name, name, false);
  }

  public QueryVariable(String name) {
    this(name, false);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isAnonymous() {
    return anonymous;
  }

  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }

  public void setUniqueIdentifier(String uniqueIdentifier) {
    this.uniqueIdentifier = uniqueIdentifier;
  }

  public String getUniqueIdentifier() {
    return uniqueIdentifier;
  }

  public abstract VariableType getVariableType();

  @Override
  public int hashCode() {
    return 31;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    QueryVariable other = (QueryVariable) obj;
    if (anonymous != other.anonymous)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    }
    if (!name.equals(other.name))
      return false;
    else if (!uniqueIdentifier.equals(other.uniqueIdentifier)) {
      return false;
    }
    return true;
  }

  public abstract void accept(QueryExpressionVisitor v);
}
