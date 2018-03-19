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

import org.dataconservancy.pass.model.Funder;
import org.dataconservancy.pass.model.Grant;
import org.dataconservancy.pass.model.Person;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.dataconservancy.pass.grant.data.DateTimeUtil.createJodaDateTime;

/**
 * This class is responsible for taking the ResultSet from the database query and constructing a corresponding
 * List of Grant objects.
 *
 * @author jrm@jhu.edu
 */
public class GrantModelBuilder {

    private ResultSet rs;
    private String latestUpdateString = "";
    private String report = "";

    public GrantModelBuilder(ResultSet resultSet) {
        this.rs = resultSet;
    }

    /**
     * Build a List of Grants from a ResultSet
     * Because we need to make sure we catch any updates to fields referenced by URIs, we construct
     * these and check to see if any of the fields on these objects need updating.
     * @return the List of constructed Grants
     * @throws SQLException if the database had an issue with our SQL query
     */
    public List<Grant> buildGrantList() throws SQLException {
        //populate model
        List<Grant> grantList = new ArrayList<>();

        int i=0;
        while (rs.next()) {
            Grant grant = new Grant();
            Person person = new Person();
            Funder directFunder = new Funder();
            Funder primaryFunder = new Funder();

            grant.setAwardNumber(rs.getString("GRANT_NUMBER"));
            switch (rs.getString("AWARD_STATUS")) {
                case "Active":
                    grant.setAwardStatus(Grant.AwardStatus.ACTIVE);
                    break;
                case "Pre-Award":
                    grant.setAwardStatus(Grant.AwardStatus.PRE_AWARD);
                    break;
                case "Terminated":
                    grant.setAwardStatus(Grant.AwardStatus.TERMINATED);
            }
            grant.setLocalAwardId(rs.getString("AWARD_NUMBER"));
            grant.setProjectName(rs.getString("TITLE"));
            grant.setAwardDate(createJodaDateTime(rs.getString("AWARD_DATE")));
            grant.setStartDate(createJodaDateTime(rs.getString("AWARD_START")));
            grant.setEndDate(createJodaDateTime(rs.getString("AWARD_END")));

            person.setFirstName(rs.getString("FIRST_NAME"));
            person.setMiddleName(rs.getString("MIDDLE_NAME"));
            person.setLastName(rs.getString("LAST_NAME"));
            person.setDisplayName(rs.getString("PRINCIPAL_INV"));
            person.setEmail(rs.getString("EMAIL_ADDRESS"));
            person.setInstitutionalId(rs.getString("JHED_ID"));
            //person.setOrcidId();
            person.setAffiliation(rs.getString("DIVISION"));

            directFunder.setLocalId("");
            directFunder.setName("");

            grantList.add(grant);

            //see if this is the latest grant updated
            String grantUpdateString = rs.getString("UPDATE_TIMESTAMP");
            String baseString = "1980-01-01 00:00:00.0";//just some random long ago timestamp to handle the first record
            latestUpdateString = latestUpdateString.length()==0 ? baseString : returnLaterUpdate(grantUpdateString, latestUpdateString);

            //increment the number of records processed
            i++;
            }
        //success - we capture some information to report
        report = format("%s grant records processed; most recent update in this batch has timestamp %s", String.valueOf(i), getLatestUpdate());
        return grantList;
    }

    /**
     * Compare two timestamps and return the later of them
     * @param currentUpdateString the current latest timestamp string
     * @param latestUpdateString the new timestamp to be compared against the current latest timestamp
     * @return the later of the two parameters
     */
    protected static String returnLaterUpdate(String currentUpdateString, String latestUpdateString) {
        DateTime grantUpdateTime = createJodaDateTime(currentUpdateString);
        DateTime previousLatestUpdateTime = createJodaDateTime(latestUpdateString);
        return grantUpdateTime.isAfter(previousLatestUpdateTime)? currentUpdateString : latestUpdateString;
    }

    /**
     * This method provides the latest timestamp of all records processed. After processing, this timestamp
     * will be used to be tha base timestamp for the next run of the app
     * @return the latest update timestamp string
     */
    public String getLatestUpdate(){
        return this.latestUpdateString;
    }

    /**
     * This returns the final statistics of the processing of the Grant List
     * @return the report
     */
    public String getReport(){
        return report;
    }

}
