package oracle.pgql.lang.temporal;

import oracle.pgql.lang.AbstractPgqlTest;
import oracle.pgql.lang.PgqlResult;
import oracle.pgql.lang.ir.QueryExpression;
import oracle.pgql.lang.ir.QueryExpression.PeriodLengthExpression;
import oracle.pgql.lang.ir.QueryExpression.PeriodLengthExpression.TimeUnit;
import oracle.pgql.lang.ir.SelectQuery;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class to test the period length syntax extensions.
 */
@RunWith(Parameterized.class)
public class LengthTest extends AbstractPgqlTest {

  /**
   * The string representation of the time unit.
   */
  private final String unitString;

  /**
   * The default time unit.
   */
  private final TimeUnit defaultUnit = TimeUnit.MICROSECOND;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      {"YEAR"}, {"QUARTER"}, {"WEEK"}, {"DAY"}, {"HOUR"}, {"MINUTE"}, {"SECOND"}, {"MILLISECOND"},
      {"MICROSECOND"}});
  }

  public LengthTest(String unit) {
    this.unitString = unit;
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testElementPeriodLengthInProjection() throws Exception {
    String query = "SELECT LENGTH(v.tx_time) FROM MATCH (v)";
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();
    QueryExpression exp = selectQuery.getProjection().getElements().get(0).getExp();
    assertTrue(exp instanceof PeriodLengthExpression);
    PeriodLengthExpression lengthExpression = (PeriodLengthExpression) exp;
    assertEquals(defaultUnit, lengthExpression.getTimeUnit());
    assertTrue(lengthExpression.getExp() instanceof QueryExpression.ElemTimeAccess);
  }

  @Test
  public void testPropertyPeriodLengthInProjection() throws Exception {
    String query = "SELECT LENGTH(v.prop.tx_time) FROM MATCH (v)";
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();
    QueryExpression exp = selectQuery.getProjection().getElements().get(0).getExp();
    assertTrue(exp instanceof PeriodLengthExpression);
    PeriodLengthExpression lengthExpression = (PeriodLengthExpression) exp;
    assertEquals(defaultUnit, lengthExpression.getTimeUnit());
    assertTrue(lengthExpression.getExp() instanceof QueryExpression.PropTimeAccess);
  }

  @Test
  public void testElementPeriodLengthWithUnitInProjection() throws Exception {
    String query = String.format("SELECT LENGTH(%s, v.tx_time) FROM MATCH (v)", unitString);
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();
    QueryExpression exp = selectQuery.getProjection().getElements().get(0).getExp();
    assertTrue(exp instanceof PeriodLengthExpression);
    PeriodLengthExpression lengthExpression = (PeriodLengthExpression) exp;
    assertEquals(unitString, lengthExpression.getTimeUnit().name());
    assertTrue(lengthExpression.getExp() instanceof QueryExpression.ElemTimeAccess);
  }

  @Test
  public void testPropertyPeriodLengthWithUnitInProjection() throws Exception {
    String query = String.format("SELECT LENGTH(%s, v.prop.tx_time) FROM MATCH (v)", unitString);
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();
    QueryExpression exp = selectQuery.getProjection().getElements().get(0).getExp();
    assertTrue(exp instanceof PeriodLengthExpression);
    PeriodLengthExpression lengthExpression = (PeriodLengthExpression) exp;
    assertEquals(unitString, lengthExpression.getTimeUnit().name());
    assertTrue(lengthExpression.getExp() instanceof QueryExpression.PropTimeAccess);
  }

  @Test
  public void testElementPeriodLengthInSelection() throws Exception {
    String query = "SELECT v.id FROM MATCH (v) WHERE LENGTH(v.val_time) > 100";
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression.RelationalExpression.Greater relationalExpression =
      (QueryExpression.RelationalExpression.Greater) selectQuery.getGraphPattern().getConstraints()
        .toArray(new QueryExpression[0])[0];

    assertTrue(relationalExpression.getExp1() instanceof PeriodLengthExpression);
    assertTrue(relationalExpression.getExp2() instanceof QueryExpression.Constant.ConstInteger);

    PeriodLengthExpression lengthExpression = (PeriodLengthExpression) relationalExpression.getExp1();
    assertEquals(defaultUnit, lengthExpression.getTimeUnit());
    assertTrue(lengthExpression.getExp() instanceof QueryExpression.ElemTimeAccess);
  }

  @Test
  public void testPropertyPeriodLengthInSelection() throws Exception {
    String query = "SELECT v.id FROM MATCH (v) WHERE LENGTH(v.prop.val_time) > 100";
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression.RelationalExpression.Greater relationalExpression =
      (QueryExpression.RelationalExpression.Greater) selectQuery.getGraphPattern().getConstraints()
        .toArray(new QueryExpression[0])[0];

    assertTrue(relationalExpression.getExp1() instanceof PeriodLengthExpression);
    assertTrue(relationalExpression.getExp2() instanceof QueryExpression.Constant.ConstInteger);

    PeriodLengthExpression lengthExpression = (PeriodLengthExpression) relationalExpression.getExp1();
    assertEquals(defaultUnit, lengthExpression.getTimeUnit());
    assertTrue(lengthExpression.getExp() instanceof QueryExpression.PropTimeAccess);
  }

  @Test
  public void testElementPeriodLengthWithUnitInSelection() throws Exception {
    String query = String.format("SELECT v.id FROM MATCH (v) WHERE LENGTH(%s, v.val_time) > 100", unitString);
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression.RelationalExpression.Greater relationalExpression =
      (QueryExpression.RelationalExpression.Greater) selectQuery.getGraphPattern().getConstraints()
        .toArray(new QueryExpression[0])[0];

    assertTrue(relationalExpression.getExp1() instanceof PeriodLengthExpression);
    assertTrue(relationalExpression.getExp2() instanceof QueryExpression.Constant.ConstInteger);

    PeriodLengthExpression lengthExpression = (PeriodLengthExpression) relationalExpression.getExp1();
    assertEquals(unitString, lengthExpression.getTimeUnit().name());
    assertTrue(lengthExpression.getExp() instanceof QueryExpression.ElemTimeAccess);
  }

  @Test
  public void testPropertyPeriodLengthWithUnitInSelection() throws Exception {
    String query = String.format("SELECT v.id FROM MATCH (v) WHERE LENGTH(%s, v.prop.val_time) > 100",
      unitString);
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression.RelationalExpression.Greater relationalExpression =
      (QueryExpression.RelationalExpression.Greater) selectQuery.getGraphPattern().getConstraints()
        .toArray(new QueryExpression[0])[0];

    assertTrue(relationalExpression.getExp1() instanceof PeriodLengthExpression);
    assertTrue(relationalExpression.getExp2() instanceof QueryExpression.Constant.ConstInteger);

    PeriodLengthExpression lengthExpression = (PeriodLengthExpression) relationalExpression.getExp1();
    assertEquals(unitString, lengthExpression.getTimeUnit().name());
    assertTrue(lengthExpression.getExp() instanceof QueryExpression.PropTimeAccess);
  }

  @Test
  public void testToString() throws Exception {
    String query = String.format("SELECT LENGTH(%s, v.val_time) FROM MATCH (v)", unitString);
    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());

    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();
    PeriodLengthExpression lengthExpression =
      (PeriodLengthExpression) selectQuery.getProjection().getElements().get(0).getExp();

    assertEquals("LENGTH(" + unitString +", v.VAL_TIME)", lengthExpression.toString());
  }

}
