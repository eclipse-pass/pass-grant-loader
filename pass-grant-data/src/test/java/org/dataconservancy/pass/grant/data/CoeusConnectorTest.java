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

import org.junit.Test;

public class CoeusConnectorTest {

    @Test
    public void testCoeusConnector(){

    }

    @Test
    public void testBuildString() {

      CoeusConnector connector = new CoeusConnector(null);
        System.out.println(connector.buildQueryString("12/12/2012", "12/13/2012"));

    }

}