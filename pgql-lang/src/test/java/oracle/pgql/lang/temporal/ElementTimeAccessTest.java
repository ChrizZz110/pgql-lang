package oracle.pgql.lang.temporal;

import oracle.pgql.lang.AbstractPgqlTest;
import oracle.pgql.lang.PgqlResult;
import oracle.pgql.lang.ir.ExpAsVar;
import oracle.pgql.lang.ir.QueryExpression;
import oracle.pgql.lang.ir.QueryExpression.ElemTimeAccess;
import oracle.pgql.lang.ir.QueryExpression.PropertyAccess;
import oracle.pgql.lang.ir.QueryVertex;
import oracle.pgql.lang.ir.SelectQuery;
import oracle.pgql.lang.ir.TimeProperty;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for accessing the temporal attributes of an graph element (variable).
 */
public class ElementTimeAccessTest extends AbstractPgqlTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testSingleElementPeriodAccessInSelect() throws Exception {
    String query = "SELECT v.tx_time FROM MATCH (v) ON testGraph";

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());
    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();
    final List<ExpAsVar> elements = selectQuery.getProjection().getElements();
    assertEquals(1, elements.size());
    QueryExpression elemTimeExp = elements.get(0).getExp();
    assertTrue(elemTimeExp instanceof ElemTimeAccess);
    ElemTimeAccess elemTimeAccess = (ElemTimeAccess) elemTimeExp;
    assertEquals(TimeProperty.TX_TIME, elemTimeAccess.getTimeProperty());
    assertEquals("V", elemTimeAccess.getVariable().getName());
  }

  @Test
  public void testSingleElementPeriodBoundsAccessInSelect() throws Exception {
    String query = "SELECT v.tx_from, v.tx_to FROM MATCH (v) ON testGraph";

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());
    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();
    final List<ExpAsVar> elements = selectQuery.getProjection().getElements();
    assertEquals(2, elements.size());
    QueryExpression elemTimeExpStart = elements.get(0).getExp();
    QueryExpression elemTimeExpEnd = elements.get(1).getExp();
    assertTrue(elemTimeExpStart instanceof ElemTimeAccess);
    assertTrue(elemTimeExpEnd instanceof ElemTimeAccess);
    ElemTimeAccess elemTimeAccessStart = (ElemTimeAccess) elemTimeExpStart;
    ElemTimeAccess elemTimeAccessEnd = (ElemTimeAccess) elemTimeExpEnd;
    assertEquals(TimeProperty.TX_FROM, elemTimeAccessStart.getTimeProperty());
    assertEquals("V", elemTimeAccessStart.getVariable().getName());
    assertEquals(TimeProperty.TX_TO, elemTimeAccessEnd.getTimeProperty());
    assertEquals("V", elemTimeAccessEnd.getVariable().getName());
  }

  @Test
  public void testMultipleElementsAccessBesidesProperties() throws Exception {
    String query = "SELECT v1.tx_time, e.tx_time, v2.tx_time, v1.myProp, e.myOtherProp, v2.myCoolProp, " +
      "v1.val_from, v1.val_to, v1.val_time FROM MATCH (v1)-[e]->(v2)";

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());
    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();
    final List<ExpAsVar> elements = selectQuery.getProjection().getElements();
    assertEquals(9, elements.size());

    final List<QueryExpression> queryExpressions =
      elements.stream().map(ExpAsVar::getExp).collect(Collectors.toList());

    assertTrue(queryExpressions.get(0) instanceof ElemTimeAccess);
    assertTrue(queryExpressions.get(1) instanceof ElemTimeAccess);
    assertTrue(queryExpressions.get(2) instanceof ElemTimeAccess);
    assertTrue(queryExpressions.get(3) instanceof PropertyAccess);
    assertTrue(queryExpressions.get(4) instanceof PropertyAccess);
    assertTrue(queryExpressions.get(5) instanceof PropertyAccess);
    assertTrue(queryExpressions.get(6) instanceof ElemTimeAccess);
    assertTrue(queryExpressions.get(7) instanceof ElemTimeAccess);
    assertTrue(queryExpressions.get(8) instanceof ElemTimeAccess);

    ElemTimeAccess eta0 = ((ElemTimeAccess) queryExpressions.get(0));
    assertEquals(TimeProperty.TX_TIME, eta0.getTimeProperty());
    assertEquals("V1", eta0.getVariable().getName());

    ElemTimeAccess eta1 = ((ElemTimeAccess) queryExpressions.get(1));
    assertEquals(TimeProperty.TX_TIME, eta1.getTimeProperty());
    assertEquals("E", eta1.getVariable().getName());

    ElemTimeAccess eta2 = ((ElemTimeAccess) queryExpressions.get(2));
    assertEquals(TimeProperty.TX_TIME, eta2.getTimeProperty());
    assertEquals("V2", eta2.getVariable().getName());

    PropertyAccess pa3 = ((PropertyAccess) queryExpressions.get(3));
    assertEquals("MYPROP", pa3.getPropertyName());
    assertEquals("V1", pa3.getVariable().getName());

    PropertyAccess pa4 = ((PropertyAccess) queryExpressions.get(4));
    assertEquals("MYOTHERPROP", pa4.getPropertyName());
    assertEquals("E", pa4.getVariable().getName());

    PropertyAccess pa5 = ((PropertyAccess) queryExpressions.get(5));
    assertEquals("MYCOOLPROP", pa5.getPropertyName());
    assertEquals("V2", pa5.getVariable().getName());

    ElemTimeAccess eta6 = ((ElemTimeAccess) queryExpressions.get(6));
    assertEquals(TimeProperty.VAL_FROM, eta6.getTimeProperty());
    assertEquals("V1", eta6.getVariable().getName());

    ElemTimeAccess eta7 = ((ElemTimeAccess) queryExpressions.get(7));
    assertEquals(TimeProperty.VAL_TO, eta7.getTimeProperty());
    assertEquals("V1", eta7.getVariable().getName());

    ElemTimeAccess eta8 = ((ElemTimeAccess) queryExpressions.get(8));
    assertEquals(TimeProperty.VAL_TIME, eta8.getTimeProperty());
    assertEquals("V1", eta8.getVariable().getName());
  }

  @Test
  public void testSingleElementTimeAccessInWhere() throws Exception {
    String query = "SELECT v.id FROM MATCH (v) WHERE v.tx_time > v.val_time";

    final PgqlResult pgqlResult = pgql.parse(query);
    assertTrue(pgqlResult.isQueryValid());
    assertTrue(pgqlResult.getGraphQuery() instanceof SelectQuery);
    SelectQuery selectQuery = (SelectQuery) pgqlResult.getGraphQuery();

    QueryExpression.RelationalExpression.Greater relationalExpression =
      (QueryExpression.RelationalExpression.Greater) selectQuery.getGraphPattern().getConstraints()
        .toArray(new QueryExpression[0])[0];

    assertTrue(relationalExpression.getExp1() instanceof ElemTimeAccess);
    assertTrue(relationalExpression.getExp2() instanceof ElemTimeAccess);

    ElemTimeAccess eta1 = (ElemTimeAccess) relationalExpression.getExp1();
    ElemTimeAccess eta2 = (ElemTimeAccess) relationalExpression.getExp2();
    assertEquals(TimeProperty.TX_TIME, eta1.getTimeProperty());
    assertEquals("V", eta1.getVariable().getName());
    assertEquals(TimeProperty.VAL_TIME, eta2.getTimeProperty());
    assertEquals("V", eta2.getVariable().getName());

  }

  @Test
  public void testGetExpType() throws Exception {
    ElemTimeAccess elemTimeAccess = new ElemTimeAccess(new QueryVertex("X", false), TimeProperty.TX_FROM);
    assertEquals(QueryExpression.ExpressionType.ELEM_TIME_ACCESS, elemTimeAccess.getExpType());
  }

  @Test
  public void testToString() throws Exception {
    ElemTimeAccess elemTimeAccess = new ElemTimeAccess(new QueryVertex("X", false), TimeProperty.TX_FROM);
    assertEquals("x.TX_FROM", elemTimeAccess.toString());
  }
}
