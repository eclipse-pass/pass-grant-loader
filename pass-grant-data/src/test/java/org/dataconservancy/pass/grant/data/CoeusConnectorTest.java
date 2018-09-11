/*
 * Copyright 2018 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataconservancy.pass.grant.data;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the COEUS connector
 *
 * @author jrm@jhu.edu
 */
public class CoeusConnectorTest {

    private CoeusConnector connector;

    @Before
    public void setup(){
        connector = new CoeusConnector(null);
    }

    @Test
    public void testCoeusConnector(){

    }

    /**
     * Test that the query string produces is as expected
     */
    @Test
    public void testBuildGrantString() {

        String expectedQueryString= "SELECT A.AWARD_ID, A.AWARD_STATUS, A.GRANT_NUMBER, A.TITLE, A.AWARD_DATE," +
                " A.AWARD_START, A.AWARD_END, A.SPONSOR, A.SPOSNOR_CODE, A.UPDATE_TIMESTAMP, B.ABBREVIATED_ROLE, B.EMPLOYEE_ID," +
                " C.FIRST_NAME, C.MIDDLE_NAME, C.LAST_NAME, C.EMAIL_ADDRESS, C.JHED_ID, D.SPONSOR_NAME, D.SPONSOR_CODE" +
                " FROM" +
                " COEUS.JHU_FACULTY_FORCE_PROP A INNER JOIN " +
                " (SELECT GRANT_NUMBER, MAX(UPDATE_TIMESTAMP) AS MAX_UPDATE_TIMESTAMP" +
                    " FROM COEUS.JHU_FACULTY_FORCE_PROP" +
                    " GROUP BY GRANT_NUMBER) LATEST" +
                " ON A.UPDATE_TIMESTAMP = LATEST.MAX_UPDATE_TIMESTAMP AND A.GRANT_NUMBER = LATEST.GRANT_NUMBER" +
                " INNER JOIN COEUS.JHU_FACULTY_FORCE_PRSN B" +
                " ON A.INST_PROPOSAL = B.INST_PROPOSAL" +
                " INNER JOIN COEUS.JHU_FACULTY_FORCE_PRSN_DETAIL C" +
                " ON B.EMPLOYEE_ID = C.EMPLOYEE_ID" +
                " LEFT JOIN COEUS.SWIFT_SPONSOR D" +
                " ON A.PRIME_SPONSOR_CODE = D.SPONSOR_CODE" +
                " WHERE A.UPDATE_TIMESTAMP > TIMESTAMP '2018-06-01 06:00:00.0'" +
                " AND TO_DATE(A.AWARD_END, 'MM/DD/YYYY') >= TO_DATE('01/01/2011', 'MM/DD/YYYY')" +
                " AND A.PROPOSAL_STATUS = 'Funded'" +
                " AND (B.ABBREVIATED_ROLE = 'P' OR B.ABBREVIATED_ROLE = 'C' OR REGEXP_LIKE (UPPER(B.ROLE), '^CO ?-?INVESTIGATOR$'))" +
                " AND A.GRANT_NUMBER IS NOT NULL";

        Assert.assertEquals(expectedQueryString, connector.buildQueryString("2018-06-01 06:00:00.0", "grant"));

    }

    @Test
    public void testBuildExistingGrantString() {

        String expectedQueryString= "SELECT A.AWARD_ID, A.AWARD_STATUS, A.GRANT_NUMBER, A.TITLE, A.AWARD_DATE," +
                " A.AWARD_START, A.AWARD_END, A.SPONSOR, A.SPOSNOR_CODE, A.UPDATE_TIMESTAMP, B.ABBREVIATED_ROLE, B.EMPLOYEE_ID," +
                " C.FIRST_NAME, C.MIDDLE_NAME, C.LAST_NAME, C.EMAIL_ADDRESS, C.JHED_ID, D.SPONSOR_NAME, D.SPONSOR_CODE" +
                " FROM" +
                " COEUS.JHU_FACULTY_FORCE_PROP A INNER JOIN " +
                " (SELECT GRANT_NUMBER, MAX(UPDATE_TIMESTAMP) AS MAX_UPDATE_TIMESTAMP" +
                " FROM COEUS.JHU_FACULTY_FORCE_PROP" +
                " GROUP BY GRANT_NUMBER) LATEST" +
                " ON A.UPDATE_TIMESTAMP = LATEST.MAX_UPDATE_TIMESTAMP AND A.GRANT_NUMBER = LATEST.GRANT_NUMBER" +
                " INNER JOIN COEUS.JHU_FACULTY_FORCE_PRSN B" +
                " ON A.INST_PROPOSAL = B.INST_PROPOSAL" +
                " INNER JOIN COEUS.JHU_FACULTY_FORCE_PRSN_DETAIL C" +
                " ON B.EMPLOYEE_ID = C.EMPLOYEE_ID" +
                " LEFT JOIN COEUS.SWIFT_SPONSOR D" +
                " ON A.PRIME_SPONSOR_CODE = D.SPONSOR_CODE" +
                " WHERE A.UPDATE_TIMESTAMP > TIMESTAMP '2018-06-01 06:00:00.0'" +
                " AND TO_DATE(A.AWARD_END, 'MM/DD/YYYY') >= TO_DATE('06/01/2018', 'MM/DD/YYYY')" +
                " AND A.PROPOSAL_STATUS = 'Funded'" +
                " AND (B.ABBREVIATED_ROLE = 'P' OR B.ABBREVIATED_ROLE = 'C' OR REGEXP_LIKE (UPPER(B.ROLE), '^CO ?-?INVESTIGATOR$'))" +
                " AND A.GRANT_NUMBER IS NOT NULL";

        Assert.assertEquals(expectedQueryString, connector.buildQueryString("2018-06-01 06:00:00.0", "existing-grant"));

    }


    @Test
    public void testBuildUserQueryString(){

        String expectedQueryString = "SELECT FIRST_NAME, MIDDLE_NAME, LAST_NAME, EMAIL_ADDRESS, JHED_ID, EMPLOYEE_ID, " +
                "UPDATE_TIMESTAMP FROM COEUS.JHU_FACULTY_FORCE_PRSN_DETAIL " +
                "WHERE UPDATE_TIMESTAMP > TIMESTAMP '2018-13-14 06:00:00.0'";
        Assert.assertEquals(expectedQueryString, connector.buildQueryString("2018-13-14 06:00:00.0", "user"));

    }

}
