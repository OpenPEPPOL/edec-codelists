/*
 * Copyright (C) 2020-2025 OpenPeppol AISBL (www.peppol.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.peppol.codelist.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;

public abstract class AbstractModelRow implements IModelRow
{
  private static LocalDate JAN_1_1900 = PDTFactory.createLocalDate (1900, Month.JANUARY, 1);

  protected AbstractModelRow ()
  {}

  @Nullable
  protected static LocalDate getLocalDateFromExcel (@Nullable final String s)
  {
    if (StringHelper.hasNoText (s))
      return null;
    /**
     * If you choose to use MS Excel to check your work note 2 things: 1) Jan 1,
     * 1900 is day 1 (not the number of days since Jan 1, 1900) and 2) according
     * to Excel Feb 29, 1900 exists(a bug in their code they refuse to fix.)
     */
    return JAN_1_1900.plusDays (Long.parseLong (s) - 2);
  }

  @Nullable
  protected static String safeGetAtIndex (final String [] a, final int n)
  {
    return n < a.length ? a[n] : null;
  }

  @Nullable
  protected static String getDeprecationReleaseOrDate (@Nullable final String s)
  {
    if (s != null)
    {
      // By default this would be the deprecation release of the code list
      // However, sometimes dates are stored as well - they can be identified by
      // having a "large version number".
      // It's the number of days since 1.1.1900
      final int nNum = StringParser.parseInt (s, -1);
      if (nNum > 10_000)
      {
        // Interpret as date instead
        return DateTimeFormatter.ISO_LOCAL_DATE.format (JAN_1_1900.plusDays (nNum - 2));
      }
    }
    return s;
  }
}
