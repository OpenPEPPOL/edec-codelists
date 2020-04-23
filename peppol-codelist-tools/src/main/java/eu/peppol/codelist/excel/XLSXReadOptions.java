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

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * This class contains the options that are used to read the Excel file.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class XLSXReadOptions implements Serializable
{
  private int m_nLinesToSkip = 1;
  private int m_nLineIndexShortName = 0;
  private final ICommonsList <XLSXColumn> m_aColumns = new CommonsArrayList <> ();

  /**
   * Constructor
   */
  public XLSXReadOptions ()
  {}

  /**
   * Set the number of lines to skip before the header row starts
   *
   * @param nLinesToSkip
   *        Must be &ge; 0.
   * @return this
   */
  @Nonnull
  public XLSXReadOptions setLinesToSkip (@Nonnegative final int nLinesToSkip)
  {
    ValueEnforcer.isGE0 (nLinesToSkip, "LinesToSkip");

    m_nLinesToSkip = nLinesToSkip;
    return this;
  }

  /**
   * @return The number of lines to skip before the header row starts.
   */
  @Nonnegative
  public int getLinesToSkip ()
  {
    return m_nLinesToSkip;
  }

  @Nonnull
  public XLSXReadOptions setLineIndexShortName (@Nonnegative final int nLineIndexShortName)
  {
    ValueEnforcer.isGE0 (nLineIndexShortName, "LineIndexShortName");

    m_nLineIndexShortName = nLineIndexShortName;
    return this;
  }

  @Nonnegative
  public int getLineIndexShortName ()
  {
    return m_nLineIndexShortName;
  }

  /**
   * Add a single column definition.
   *
   * @param sColumnID
   *        The ID of the column in Genericode.
   * @param bRequired
   *        Optional or required?
   * @param eDataType
   *        The XSD data type to be used in Genericode. Use "string" if you're
   *        unsure.
   * @param bKeyColumn
   *        <code>true</code> if this is a key column, <code>false</code>
   *        otherwise. Only required columns can be key columns.
   * @return this
   */
  @Nonnull
  private XLSXReadOptions _addColumn (@Nonnull @Nonempty final String sColumnID,
                                      final boolean bRequired,
                                      @Nonnull final ECodeListDataType eDataType,
                                      final boolean bKeyColumn)
  {
    final int nIndex = m_aColumns.size ();
    m_aColumns.add (new XLSXColumn (nIndex, sColumnID, bRequired, eDataType, bKeyColumn));
    return this;
  }

  @Nonnull
  public XLSXReadOptions addColumn (@Nonnull @Nonempty final String sColumnID,
                                    final boolean bRequired,
                                    @Nonnull final ECodeListDataType eDataType)
  {
    return _addColumn (sColumnID, bRequired, eDataType, false);
  }

  @Nonnull
  public XLSXReadOptions addKeyColumn (@Nonnull @Nonempty final String sColumnID,
                                       final boolean bRequired,
                                       @Nonnull final ECodeListDataType eDataType)
  {
    return _addColumn (sColumnID, bRequired, eDataType, true);
  }

  /**
   * @return A list of all defined columns, sorted ascending by index.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <XLSXColumn> getAllColumns ()
  {
    return m_aColumns.getClone ();
  }
}
