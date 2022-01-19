/*
 * Copyright (C) 2020-2022 OpenPeppol AISBL (www.peppol.eu)
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

import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.iterate.ArrayIterator;
import com.helger.commons.string.StringHelper;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.poi.excel.ExcelReadHelper;

/**
 * Read an Excel into memory
 *
 * @author Philip Helger
 */
public class InMemoryXLSX
{
  private static final Logger LOGGER = LoggerFactory.getLogger (InMemoryXLSX.class);

  private final String [] m_aShortNames;
  private final ICommonsList <String []> m_aPayload;

  protected InMemoryXLSX (@Nonnull final String [] aShortNames, @Nonnull final ICommonsList <String []> aPayload)
  {
    ValueEnforcer.notEmptyNoNullValue (aShortNames, "ShortNames");
    ValueEnforcer.notEmptyNoNullValue (aPayload, "Payload");
    m_aShortNames = aShortNames;
    m_aPayload = aPayload;
  }

  @Nonnull
  public Iterable <String> getShortNames ()
  {
    return new ArrayIterator <> (m_aShortNames);
  }

  @Nonnull
  public Iterable <String []> getPayload ()
  {
    return m_aPayload;
  }

  @Nonnull
  @ReturnsMutableCopy
  public <T> ICommonsList <T> getAsList (@Nonnull final Function <String [], T> aProvider)
  {
    return new CommonsArrayList <> (m_aPayload, aProvider);
  }

  @Nonnull
  public static InMemoryXLSX read (@Nonnull final Sheet aExcelSheet, final int nColumnCount)
  {
    final String [] aShortNameRowData = new String [nColumnCount];

    // Read mandatory short names
    {
      final Row aExcelRow = aExcelSheet.getRow (0);
      for (int nIndex = 0; nIndex < nColumnCount; ++nIndex)
      {
        final String sShortName = ExcelReadHelper.getCellValueString (aExcelRow.getCell (nIndex));
        aShortNameRowData[nIndex] = StringHelper.trim (sShortName);
      }
    }

    // Read payload
    final ICommonsList <String []> aPayload = new CommonsArrayList <> (1024);

    // Determine the row where reading should start
    int nRowIndex = 1;
    while (true)
    {
      // Read a single excel row
      final Row aExcelRow = aExcelSheet.getRow (nRowIndex++);
      if (aExcelRow == null)
        break;

      // Create Genericode row
      final String [] aRowData = new String [nColumnCount];
      for (int nIndex = 0; nIndex < nColumnCount; ++nIndex)
      {
        final String sValue = ExcelReadHelper.getCellValueString (aExcelRow.getCell (nIndex));
        aRowData[nIndex] = StringHelper.trim (sValue);
      }
      aPayload.add (aRowData);
    }

    LOGGER.info ("Successfully read " + aPayload.size () + " rows");

    return new InMemoryXLSX (aShortNameRowData, aPayload);
  }

  @Nonnull
  public static InMemoryXLSX createForProcessIDs (@Nonnull final ICommonsList <IProcessIdentifier> aProcIDs)
  {
    final ICommonsList <String []> aPayload = new CommonsArrayList <> (aProcIDs.size ());
    for (final IProcessIdentifier aItem : aProcIDs)
      aPayload.add (new String [] { aItem.getScheme (), aItem.getValue () });
    return new InMemoryXLSX (new String [] { "Identifier Scheme", "Identifier Value" }, aPayload);
  }
}
