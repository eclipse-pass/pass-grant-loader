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

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class DateTimeUtilTest {

    DateTimeUtil underTest = new DateTimeUtil();

    @Test
    public void testVerifyDateTimeFormat() {
        //check that zeroes in the time of day are ok
        String date = "2018-12-22 00:00:00.0";
        Assert.assertTrue(underTest.verifyDateTimeFormat(date));

        //check that the "." is correctly escaped
        date = "2018-01-01 12:14:55h1";
        Assert.assertFalse(underTest.verifyDateTimeFormat(date));

        //zero month not allowed
        date = "2018-00-01 12:14:55.1";
        Assert.assertFalse(underTest.verifyDateTimeFormat(date));

        //zero day not allowed
        date = "2018-12-00 00:00:00.0";
        Assert.assertFalse(underTest.verifyDateTimeFormat(date));

        //24 hour times work
        date = "2018-01-01 23:14:55.0";
        Assert.assertTrue(underTest.verifyDateTimeFormat(date));

        //hours limited to 23
        date = "2018-01-01 24:14:55.1";
        Assert.assertFalse(underTest.verifyDateTimeFormat(date));

        //59 minutes work ...
        date = "2018-01-01 23:59:55.0";
        Assert.assertTrue(underTest.verifyDateTimeFormat(date));

        // ... but 60 do not
        date = "2018-01-01 23:60:55.1";
        Assert.assertFalse(underTest.verifyDateTimeFormat(date));

        //59 seconds work ...
        date = "2018-01-01 23:59:59.0";
        Assert.assertTrue(underTest.verifyDateTimeFormat(date));

        // ... but 60 do not
        date = "2018-01-01 23:59:60.1";
        Assert.assertFalse(underTest.verifyDateTimeFormat(date));

        // three decimal places for milliseconds are ok ...
        date = "2018-01-01 07:59:16.199";
        Assert.assertTrue(underTest.verifyDateTimeFormat(date));

        // .. but not 4
        date = "2018-01-01 07:59:16.1998";
        Assert.assertFalse(underTest.verifyDateTimeFormat(date));
    }

    @Test
    public void testCreateJodaDateTime() {
        String date = "2018-01-30 23:59:58.0";
        DateTime dateTime = underTest.createJodaDateTime(date);
        Assert.assertTrue(dateTime instanceof DateTime);

        Assert.assertEquals(2018, dateTime.getYear());
        Assert.assertEquals(01, dateTime.getMonthOfYear());
        Assert.assertEquals(30, dateTime.getDayOfMonth());

        Assert.assertEquals(23, dateTime.getHourOfDay());
        Assert.assertEquals(59, dateTime.getMinuteOfHour());
        Assert.assertEquals(58, dateTime.getSecondOfMinute());

        Assert.assertEquals(0, dateTime.getMillisOfSecond());

    }
}