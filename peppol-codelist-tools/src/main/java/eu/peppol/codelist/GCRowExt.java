package eu.peppol.codelist;

import javax.annotation.Nonnull;

import com.helger.commons.string.StringHelper;
import com.helger.genericode.Genericode10Helper;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
import com.helger.genericode.v10.Value;

final class GCRowExt extends Row
{
  private final ColumnSet m_aColumnSet;

  public GCRowExt (@Nonnull final ColumnSet aColumnSet)
  {
    m_aColumnSet = aColumnSet;
  }

  void add (final String sColumnID, final String sValue)
  {
    if (StringHelper.hasText (sValue))
    {
      // Create a single value in the current row
      final Value aValue = new Value ();
      aValue.setColumnRef (Genericode10Helper.getColumnOfID (m_aColumnSet, sColumnID));
      aValue.setSimpleValue (Genericode10Helper.createSimpleValue (sValue));
      addValue (aValue);
    }
  }

  void add (final String sColumnID, final boolean bValue)
  {
    add (sColumnID, Boolean.toString (bValue));
  }
}
