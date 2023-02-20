package eu.peppol.codelist.model;

import java.time.LocalDate;
import java.time.Month;

import javax.annotation.Nullable;

import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.string.StringHelper;

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
}
