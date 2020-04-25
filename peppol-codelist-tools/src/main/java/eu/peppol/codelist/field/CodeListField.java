/**
 * Copyright (C) 2020 OpenPeppol AISBL (www.peppol.eu)
 * Copyright (C) 2015-2020 Philip Helger (www.helger.com)
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
package eu.peppol.codelist.field;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.ToStringGenerator;

/**
 * A single code list field.
 *
 * @author Philip Helger
 */
@Immutable
public final class CodeListField
{
  private final String m_sColumnID;
  private final boolean m_bRequired;
  private final ECodeListDataType m_eDataType;
  private final boolean m_bKeyColumn;

  public CodeListField (@Nonnull @Nonempty final String sColumnID,
                        final boolean bRequired,
                        @Nonnull final ECodeListDataType eDataType,
                        final boolean bKeyColumn)
  {
    ValueEnforcer.notEmpty (sColumnID, "ColumnID");
    ValueEnforcer.isFalse (!bRequired && bKeyColumn, "Optional columns cannot be key columns!");
    ValueEnforcer.notNull (eDataType, "DataType");
    m_sColumnID = sColumnID;
    m_bRequired = bRequired;
    m_eDataType = eDataType;
    m_bKeyColumn = bKeyColumn;
  }

  /**
   * @return The ID of this column to be used in the Genericode file.
   */
  @Nonnull
  @Nonempty
  public String getColumnID ()
  {
    return m_sColumnID;
  }

  /**
   * @return optional or required?
   */
  @Nonnull
  public boolean isRequired ()
  {
    return m_bRequired;
  }

  /**
   * @return The data type for this column.
   */
  @Nonnull
  public ECodeListDataType getDataType ()
  {
    return m_eDataType;
  }

  /**
   * @return <code>true</code> if this is a key column, <code>false</code>
   *         otherwise. Only required columns can be key columns.
   */
  public boolean isKeyColumn ()
  {
    return m_bKeyColumn;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("columnID", m_sColumnID)
                                       .append ("required", m_bRequired)
                                       .append ("dataType", m_eDataType)
                                       .append ("keyColumn", m_bKeyColumn)
                                       .getToString ();
  }
}
