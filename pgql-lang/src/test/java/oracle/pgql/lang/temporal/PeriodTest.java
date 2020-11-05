package oracle.pgql.lang.temporal;

import oracle.pgql.lang.AbstractPgqlTest;
import oracle.pgql.lang.PgqlResult;
import oracle.pgql.lang.ir.QueryExpression;
import oracle.pgql.lang.ir.QueryExpression.Period;
import oracle.pgql.lang.ir.SelectQuery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test the PERIOD(...,...) constructor.
 */
public class PeriodTest extends AbstractPgqlTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testPeriodFunctionInProjection() throws Exception {
    String query = "SELECT PERIOD(TIMESTAMP '2020-01-01 12:00:00', TIMESTAMP '2020-01-01 13:00:00'), " +
      "PERIOD(v.val_from, TIMESTAMP '2020-01-01 13:00:00'), " +
      "PERIOD(v.val_from, v.val_to) " +
      "FROM MATCH (v)";
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();
    QueryExpression exp0 = selectQuery.getProjection().getElements().get(0).getExp();
    QueryExpression exp1 = selectQuery.getProjection().getElements().get(1).getExp();
    QueryExpression exp2 = selectQuery.getProjection().getElements().get(2).getExp();
    assertTrue(exp0 instanceof QueryExpression.Period);
    assertTrue(exp1 instanceof QueryExpression.Period);
    assertTrue(exp2 instanceof QueryExpression.Period);

    assertTrue(((Period) exp0).getBeginningBound() instanceof QueryExpression.Constant.ConstTimestamp);
    assertTrue(((Period) exp0).getEndingBound() instanceof QueryExpression.Constant.ConstTimestamp);

    assertTrue(((Period) exp1).getBeginningBound() instanceof QueryExpression.ElemTimeAccess);
    assertTrue(((Period) exp1).getEndingBound() instanceof QueryExpression.Constant.ConstTimestamp);

    assertTrue(((Period) exp2).getBeginningBound() instanceof QueryExpression.ElemTimeAccess);
    assertTrue(((Period) exp2).getEndingBound() instanceof QueryExpression.ElemTimeAccess);
  }

  @Test
  public void testPeriodFunctionInSelection() throws Exception {
    String query = "SELECT v.id FROM MATCH (v) " +
      "WHERE PERIOD(TIMESTAMP '2020-01-01 12:00:00', TIMESTAMP '2020-01-01 13:00:00') = v.val_time AND " +
      "      PERIOD(v.val_from, TIMESTAMP '2020-01-01 13:00:00') = v.val_time AND " +
      "      PERIOD(v.val_from, v.val_to) = v.val_time";
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    final QueryExpression[] constraints =
      selectQuery.getGraphPattern().getConstraints().toArray(new QueryExpression[0]);
    assertEquals(3, constraints.length);

    Period period0 = (Period) ((QueryExpression.RelationalExpression.Equal) constraints[0]).getExp1();
    Period period1 = (Period) ((QueryExpression.RelationalExpression.Equal) constraints[1]).getExp1();
    Period period2 = (Period) ((QueryExpression.RelationalExpression.Equal) constraints[2]).getExp1();

    assertTrue(period0.getBeginningBound() instanceof QueryExpression.Constant.ConstTimestamp);
    assertTrue(period0.getEndingBound() instanceof QueryExpression.Constant.ConstTimestamp);

    assertTrue(period1.getBeginningBound() instanceof QueryExpression.ElemTimeAccess);
    assertTrue(period1.getEndingBound() instanceof QueryExpression.Constant.ConstTimestamp);

    assertTrue(period2.getBeginningBound() instanceof QueryExpression.ElemTimeAccess);
    assertTrue(period2.getEndingBound() instanceof QueryExpression.ElemTimeAccess);
  }
}
