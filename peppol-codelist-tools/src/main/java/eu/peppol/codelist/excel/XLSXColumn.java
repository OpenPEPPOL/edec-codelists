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
package eu.peppol.codelist.excel;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.string.ToStringGenerator;

import eu.peppol.codelist.field.CodeListField;

/**
 * This class represents a single column definition when converting an Excel
 * sheet into a code list.
 *
 * @author Philip Helger
 */
@Immutable
public final class XLSXColumn
{
  private final int m_nIndex;
  private final CodeListField m_aField;

  public XLSXColumn (@Nonnegative final int nIndex, @Nonnull final CodeListField aField)
  {
    ValueEnforcer.isGE0 (nIndex, "Index");
    ValueEnforcer.notNull (aField, "Field");
    m_nIndex = nIndex;
    m_aField = aField;
  }

  /**
   * @return The 0-based index of this column.
   */
  @Nonnegative
  public int getIndex ()
  {
    return m_nIndex;
  }

  /**
   * @return Code list field.
   */
  @Nonnull
  public CodeListField field ()
  {
    return m_aField;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("Index", m_nIndex).append ("Field", m_aField).getToString ();
  }
}
