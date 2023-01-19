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
package eu.peppol.codelist;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.functional.IThrowingConsumer;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;

/**
 * Represent a single CodeList Excel source file.
 *
 * @author Philip Helger
 */
public final class CodeListSource
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CodeListSource.class);

  private final File m_aFile;
  private final IThrowingConsumer <? super Sheet, Exception> m_aHandler;

  public CodeListSource (@Nonnull final String sFilenamePart,
                         @Nonnull final String sFilenameVersion,
                         @Nonnull final IThrowingConsumer <? super Sheet, Exception> aHandler) throws IOException
  {
    m_aFile = new File ("../work-in-progress/Peppol Code Lists - " +
                        sFilenamePart +
                        " v" +
                        sFilenameVersion +
                        ".xlsx").getCanonicalFile ();
    ValueEnforcer.isTrue (m_aFile.exists (), () -> "File '" + m_aFile.getAbsolutePath () + "' does not exist!");
    m_aHandler = aHandler;
  }

  public void readExcelSheet () throws Exception
  {
    LOGGER.info ("Reading Excel file '" + m_aFile.getAbsolutePath () + "'");

    // Where is the Excel?
    final IReadableResource aExcel = new FileSystemResource (m_aFile);
    if (!aExcel.exists ())
      throw new IllegalStateException ("The Excel file '" + m_aFile.getAbsolutePath () + "' could not be found!");

    // Interpret as Excel
    try (final Workbook aWB = new XSSFWorkbook (aExcel.getInputStream ()))
    {
      // Check whether all required sheets are present
      final Sheet aSheet = aWB.getSheetAt (0);
      if (aSheet == null)
        throw new IllegalStateException ("The first sheet could not be found!");

      m_aHandler.accept (aSheet);
    }
  }
}
