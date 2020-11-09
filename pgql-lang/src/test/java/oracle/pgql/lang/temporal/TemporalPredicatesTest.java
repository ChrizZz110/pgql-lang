package oracle.pgql.lang.temporal;

import oracle.pgql.lang.AbstractPgqlTest;
import oracle.pgql.lang.PgqlResult;
import oracle.pgql.lang.ir.QueryExpression;
import oracle.pgql.lang.ir.QueryExpression.RelationalExpression.Overlaps;
import oracle.pgql.lang.ir.QueryExpression.RelationalExpression.Equals;
import oracle.pgql.lang.ir.QueryExpression.RelationalExpression.Precedes;
import oracle.pgql.lang.ir.QueryExpression.RelationalExpression.Succeeds;
import oracle.pgql.lang.ir.QueryExpression.ExpressionType;
import oracle.pgql.lang.ir.SelectQuery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests the temporal predicates like overlaps, precedes, contains, etc.
 */
@RunWith(Parameterized.class)
public class TemporalPredicatesTest extends AbstractPgqlTest {

  private String arg0;
  private String arg1;
  private ExpressionType typeArg0;
  private ExpressionType typeArg1;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    final String period = "PERIOD(TIMESTAMP '2020-01-01 01:00:00', TIMESTAMP '2020-01-01 02:00:00')";
    return Arrays.asList(new Object[][] {
      {"x.val_time", "y.val_time", ExpressionType.ELEM_TIME_ACCESS, ExpressionType.ELEM_TIME_ACCESS},
      {"x.pro.val_time", "y.pro.val_time", ExpressionType.PROP_TIME_ACCESS, ExpressionType.PROP_TIME_ACCESS},
      {period, period, ExpressionType.PERIOD, ExpressionType.PERIOD},
      {"x.val_time", "y.prop.val_time", ExpressionType.ELEM_TIME_ACCESS, ExpressionType.PROP_TIME_ACCESS},
      {"x.prop.val_time", period, ExpressionType.PROP_TIME_ACCESS, ExpressionType.PERIOD},
      {period, "x.val_time", ExpressionType.PERIOD, ExpressionType.ELEM_TIME_ACCESS}
    });
  }

  public TemporalPredicatesTest(String arg0, String arg1, ExpressionType typeArg0, ExpressionType typeArg1) {
    this.arg0 = arg0;
    this.arg1 = arg1;
    this.typeArg0 = typeArg0;
    this.typeArg1 = typeArg1;
  }

  @Test
  public void testOverlaps() throws Exception {
    final String query = String
      .format("SELECT x.id as id1, e.id as id2, y.id as id3 FROM MATCH (x)-[e]->(y) WHERE %s OVERLAPS %s",
        arg0, arg1);

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression[] queryExps =
      selectQuery.getGraphPattern().getConstraints().toArray(new QueryExpression[0]);

    assertEquals(1, queryExps.length);
    assertTrue(queryExps[0] instanceof Overlaps);

    Overlaps castedExp = (Overlaps) queryExps[0];
    assertEquals(typeArg0, castedExp.getExp1().getExpType());
    assertEquals(typeArg1, castedExp.getExp2().getExpType());
  }

  @Test
  public void testEquals() throws Exception {
    final String query = String
      .format("SELECT x.id as id1, e.id as id2, y.id as id3 FROM MATCH (x)-[e]->(y) WHERE %s EQUALS %s", arg0,
        arg1);

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression[] queryExps =
      selectQuery.getGraphPattern().getConstraints().toArray(new QueryExpression[0]);

    assertEquals(1, queryExps.length);
    assertTrue(queryExps[0] instanceof Equals);

    Equals castedExp = (Equals) queryExps[0];
    assertEquals(typeArg0, castedExp.getExp1().getExpType());
    assertEquals(typeArg1, castedExp.getExp2().getExpType());
  }

  @Test
  public void testPrecedes() throws Exception {
    final String query = String
      .format("SELECT x.id as id1, e.id as id2, y.id as id3 FROM MATCH (x)-[e]->(y) WHERE %s PRECEDES %s",
        arg0, arg1);

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression[] queryExps =
      selectQuery.getGraphPattern().getConstraints().toArray(new QueryExpression[0]);

    assertEquals(1, queryExps.length);
    assertTrue(queryExps[0] instanceof Precedes);

    Precedes castedExp = (Precedes) queryExps[0];
    assertEquals(typeArg0, castedExp.getExp1().getExpType());
    assertEquals(typeArg1, castedExp.getExp2().getExpType());
    assertFalse(castedExp.isImmediately());
  }

  @Test
  public void testImmediatelyPrecedes() throws Exception {
    final String query = String.format(
      "SELECT x.id as id1, e.id as id2, y.id as id3 FROM MATCH (x)-[e]->(y) WHERE %s IMMEDIATELY PRECEDES %s",
      arg0, arg1);

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression[] queryExps =
      selectQuery.getGraphPattern().getConstraints().toArray(new QueryExpression[0]);

    assertEquals(1, queryExps.length);
    assertTrue(queryExps[0] instanceof Precedes);

    Precedes castedExp = (Precedes) queryExps[0];
    assertEquals(typeArg0, castedExp.getExp1().getExpType());
    assertEquals(typeArg1, castedExp.getExp2().getExpType());
    assertTrue(castedExp.isImmediately());
  }

  @Test
  public void testSucceeds() throws Exception {
    final String query = String
      .format("SELECT x.id as id1, e.id as id2, y.id as id3 FROM MATCH (x)-[e]->(y) WHERE %s SUCCEEDS %s",
        arg0, arg1);

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression[] queryExps =
      selectQuery.getGraphPattern().getConstraints().toArray(new QueryExpression[0]);

    assertEquals(1, queryExps.length);
    assertTrue(queryExps[0] instanceof Succeeds);

    Succeeds castedExp = (Succeeds) queryExps[0];
    assertEquals(typeArg0, castedExp.getExp1().getExpType());
    assertEquals(typeArg1, castedExp.getExp2().getExpType());
    assertFalse(castedExp.isImmediately());
  }

  @Test
  public void testImmediatelySucceeds() throws Exception {
    final String query = String.format(
      "SELECT x.id as id1, e.id as id2, y.id as id3 FROM MATCH (x)-[e]->(y) WHERE %s IMMEDIATELY SUCCEEDS %s",
      arg0, arg1);

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression[] queryExps =
      selectQuery.getGraphPattern().getConstraints().toArray(new QueryExpression[0]);

    assertEquals(1, queryExps.length);
    assertTrue(queryExps[0] instanceof Succeeds);

    Succeeds castedExp = (Succeeds) queryExps[0];
    assertEquals(typeArg0, castedExp.getExp1().getExpType());
    assertEquals(typeArg1, castedExp.getExp2().getExpType());
    assertTrue(castedExp.isImmediately());
  }
}
