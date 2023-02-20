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
  protected static LocalDate getAsDate (@Nullable final String s)
  {
    return StringHelper.hasNoText (s) ? null : JAN_1_1900.plusDays (Long.parseLong (s));
  }

  @Nullable
  protected static String getAt (final String [] a, final int n)
  {
    return n < a.length ? a[n] : null;
  }
}
