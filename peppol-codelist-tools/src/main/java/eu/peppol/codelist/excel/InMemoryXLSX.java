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

import javax.annotation.Nonnull;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.iterate.ArrayIterator;
import com.helger.poi.excel.ExcelReadHelper;

/**
 * Read an Excel into memory
 *
 * @author Philip Helger
 */
public class InMemoryXLSX
{
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
  public static InMemoryXLSX read (final XLSXReadOptions aReadOptions, @Nonnull final Sheet aExcelSheet)
  {
    ValueEnforcer.notNull (aReadOptions, "ReadOptions");

    final ICommonsList <XLSXColumn> aExcelColumns = aReadOptions.getAllColumns ();
    final int nCols = aExcelColumns.size ();

    final String [] aShortNameRowData = new String [nCols];

    // Read mandatory short names
    {
      final Row aExcelRow = aExcelSheet.getRow (aReadOptions.getLineIndexShortName ());
      for (final XLSXColumn aExcelColumn : aExcelColumns)
      {
        final int nIndex = aExcelColumn.getIndex ();
        final String sShortName = ExcelReadHelper.getCellValueString (aExcelRow.getCell (nIndex));
        aShortNameRowData[nIndex] = sShortName;
      }
    }

    // Read payload
    final ICommonsList <String []> aPayload = new CommonsArrayList <> (1024);

    // Determine the row where reading should start
    int nRowIndex = aReadOptions.getLinesToSkip ();
    while (true)
    {
      // Read a single excel row
      final Row aExcelRow = aExcelSheet.getRow (nRowIndex++);
      if (aExcelRow == null)
        break;

      // Create Genericode row
      final String [] aRowData = new String [nCols];
      for (final XLSXColumn aExcelColumn : aExcelColumns)
      {
        final int nIndex = aExcelColumn.getIndex ();
        final String sValue = ExcelReadHelper.getCellValueString (aExcelRow.getCell (nIndex));
        aRowData[nIndex] = sValue;
      }
      aPayload.add (aRowData);
    }

    return new InMemoryXLSX (aShortNameRowData, aPayload);
  }
}
