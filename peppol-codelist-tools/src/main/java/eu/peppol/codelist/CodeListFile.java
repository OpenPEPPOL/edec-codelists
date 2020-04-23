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
package eu.peppol.codelist;

import java.io.File;

import javax.annotation.Nonnull;

import org.apache.poi.ss.usermodel.Sheet;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.functional.IThrowingConsumer;

/**
 * Represent a single CodeList Excel source file.
 *
 * @author Philip Helger
 */
public final class CodeListFile
{
  private final File m_aFile;
  private final IThrowingConsumer <? super Sheet, Exception> m_aHandler;

  public CodeListFile (@Nonnull final String sFilenamePart,
                       @Nonnull final String sFilenameVersion,
                       @Nonnull final IThrowingConsumer <? super Sheet, Exception> aHandler)
  {
    // TODO change absolute path
    m_aFile = new File ("../../documentation/Code Lists/PEPPOL Code Lists - " +
                        sFilenamePart +
                        " v" +
                        sFilenameVersion +
                        ".xlsx").getAbsoluteFile ();
    ValueEnforcer.isTrue (m_aFile.exists (), () -> "File '" + m_aFile.getAbsolutePath () + "' does not exist!");
    m_aHandler = aHandler;
  }

  @Nonnull
  public File getFile ()
  {
    return m_aFile;
  }

  public void handle (@Nonnull final Sheet aSheet) throws Exception
  {
    m_aHandler.accept (aSheet);
  }
}
