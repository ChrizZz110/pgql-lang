package oracle.pgql.lang.temporal;

import oracle.pgql.lang.AbstractPgqlTest;
import oracle.pgql.lang.PgqlResult;
import oracle.pgql.lang.ir.ExpAsVar;
import oracle.pgql.lang.ir.QueryExpression;
import oracle.pgql.lang.ir.QueryExpression.PropTimeAccess;
import oracle.pgql.lang.ir.QueryVertex;
import oracle.pgql.lang.ir.SelectQuery;
import oracle.pgql.lang.ir.TimeProperty;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for accessing the temporal attributes of an graph element's property.
 */
public class PropTimeAccessTest extends AbstractPgqlTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testSinglePropertyPeriodAccessInSelect() throws Exception {
    String query = "SELECT v.prop.tx_time FROM MATCH (v) ON testGraph";

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());
    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();
    final List<ExpAsVar> elements = selectQuery.getProjection().getElements();
    assertEquals(1, elements.size());
    QueryExpression propTimeExp = elements.get(0).getExp();
    assertTrue(propTimeExp instanceof PropTimeAccess);
    PropTimeAccess propTimeAccess = (PropTimeAccess) propTimeExp;
    assertEquals(TimeProperty.TX_TIME, propTimeAccess.getTimeProperty());
    assertEquals("V", propTimeAccess.getPropertyAccess().getVariable().getName());
    assertEquals("PROP", propTimeAccess.getPropertyAccess().getPropertyName());
  }

  @Test
  public void testSinglePropertyPeriodAccessInWhere() throws Exception {
    String query = "SELECT v.prop.tx_time FROM MATCH (v) ON testGraph " +
      "WHERE v.prop.tx_time > v.prop.val_time";

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());
    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression.RelationalExpression.Greater relationalExpression =
      (QueryExpression.RelationalExpression.Greater) selectQuery.getGraphPattern().getConstraints()
        .toArray(new QueryExpression[0])[0];

    assertTrue(relationalExpression.getExp1() instanceof PropTimeAccess);
    assertTrue(relationalExpression.getExp2() instanceof PropTimeAccess);

    PropTimeAccess pta1 = (PropTimeAccess) relationalExpression.getExp1();
    PropTimeAccess pta2 = (PropTimeAccess) relationalExpression.getExp2();
    assertEquals(TimeProperty.TX_TIME, pta1.getTimeProperty());
    assertEquals("V", pta1.getPropertyAccess().getVariable().getName());
    assertEquals("PROP", pta1.getPropertyAccess().getPropertyName());
    assertEquals(TimeProperty.VAL_TIME, pta2.getTimeProperty());
    assertEquals("V", pta2.getPropertyAccess().getVariable().getName());
    assertEquals("PROP", pta2.getPropertyAccess().getPropertyName());

  }

  @Test
  public void testGetExpType() throws Exception {
    PropTimeAccess propTimeAccess = new QueryExpression.PropTimeAccess(
      new QueryExpression.PropertyAccess(new QueryVertex("X", false), "prop"), TimeProperty.TX_TIME);
    assertEquals(QueryExpression.ExpressionType.PROP_TIME_ACCESS, propTimeAccess.getExpType());
  }

  @Test
  public void testToString() throws Exception {
    PropTimeAccess propTimeAccess = new QueryExpression.PropTimeAccess(
      new QueryExpression.PropertyAccess(new QueryVertex("X", false), "prop"), TimeProperty.TX_TIME);
    assertEquals("x.\"prop\".TX_TIME", propTimeAccess.toString());
  }
}
