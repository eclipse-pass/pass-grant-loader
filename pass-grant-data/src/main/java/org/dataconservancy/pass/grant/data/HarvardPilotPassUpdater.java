/*
 * Copyright 2019 Johns Hopkins University
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

import static org.dataconservancy.pass.grant.data.CoeusFieldNames.C_USER_EMAIL;
import static org.dataconservancy.pass.grant.data.CoeusFieldNames.C_USER_FIRST_NAME;
import static org.dataconservancy.pass.grant.data.CoeusFieldNames.C_USER_LAST_NAME;

import java.util.Map;

import org.dataconservancy.pass.client.PassClient;
import org.dataconservancy.pass.model.User;
import org.dataconservancy.pass.model.support.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HarvardPilotPassUpdater extends BasicPassUpdater {

    private static final Logger LOG = LoggerFactory.getLogger(HarvardPilotPassUpdater.class);
    private static final String DOMAIN = "harvard.edu";

    public HarvardPilotPassUpdater() {
        super(new HarvardPilotPassEntityUtil());
        super.setDomain(DOMAIN);
    }

    public HarvardPilotPassUpdater(PassClient passClient) {
        super(new HarvardPilotPassEntityUtil(), passClient);
        super.setDomain(DOMAIN);
    }

    @Override
    User buildUser(Map<String, String> rowMap) {
        User user = new User();
        user.setFirstName(rowMap.get(C_USER_FIRST_NAME));
        //user.setMiddleName(rowMap.getOrDefault(C_USER_MIDDLE_NAME, null));
        user.setLastName(rowMap.get(C_USER_LAST_NAME));
        user.setDisplayName(rowMap.get(C_USER_FIRST_NAME) + " " + rowMap.get(C_USER_LAST_NAME));
        String email = rowMap.get(C_USER_EMAIL);
        user.setEmail(email);
        //
        //Build the List of locatorIds - put the most reliable ids first
        //for the pilot, we construct the eppn locatorId from the email address
        String eppn = email.split("@")[0];
        if (eppn != null) {
            String INSTITUTIONAL_ID_TYPE = "jhed";
            user.getLocatorIds().add(new Identifier(DOMAIN, INSTITUTIONAL_ID_TYPE, eppn).serialize());
        }
        user.getRoles().add(User.Role.SUBMITTER);
        LOG.debug("Built user with institutional ID " + eppn);
        return user;
    }

}
