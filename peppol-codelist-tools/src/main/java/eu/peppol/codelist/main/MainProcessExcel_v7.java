/**
 * Copyright (C) 2020 OpenPeppol AISBL (www.peppol.eu)
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

import eu.peppol.codelist.ConvertV7;

/**
 * Utility class to create the Genericode files from the Excel code list.
 *
 * @author Philip Helger
 */
public final class MainProcessExcel_v7
{
  public static void main (final String [] args) throws Exception
  {
    new ConvertV7 ().run ();
  }
}
