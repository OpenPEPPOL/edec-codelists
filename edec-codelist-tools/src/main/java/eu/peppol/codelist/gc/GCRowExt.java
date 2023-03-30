/*
 * Copyright (C) 2020-2023 OpenPeppol AISBL (www.peppol.eu)
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
package eu.peppol.codelist.gc;

import javax.annotation.Nonnull;

import com.helger.commons.string.StringHelper;
import com.helger.genericode.Genericode10Helper;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Row;
import com.helger.genericode.v10.Value;

public final class GCRowExt extends Row
{
  private final ColumnSet m_aColumnSet;

  public GCRowExt (@Nonnull final ColumnSet aColumnSet)
  {
    m_aColumnSet = aColumnSet;
  }

  public void add (final String sColumnID, final String sValue)
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

  public void add (final String sColumnID, final boolean bValue)
  {
    add (sColumnID, Boolean.toString (bValue));
  }
}
