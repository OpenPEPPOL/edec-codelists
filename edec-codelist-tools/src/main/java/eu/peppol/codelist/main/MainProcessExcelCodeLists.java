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
package eu.peppol.codelist.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.peppol.codelist.v8.ConvertV8_5;

/**
 * Utility class to create the Genericode files from the Excel code list.
 *
 * @author Philip Helger
 */
public final class MainProcessExcelCodeLists
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainProcessExcelCodeLists.class);

  public static void main (final String [] args) throws Exception
  {
    new ConvertV8_5 ().run ();
    LOGGER.info ("Now run 'mvn license:format' on this project");
    LOGGER.info ("Than copy the output from 'ceated-codelists/vX.Y' to the respective 'publication/vX.Y' folder in the parent project");
  }
}
