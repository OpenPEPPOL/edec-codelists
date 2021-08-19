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
package eu.peppol.codelist;

import com.helger.commons.version.Version;

/**
 * Handle V7.4 code lists
 *
 * @author Philip Helger
 */
@Deprecated
public final class ConvertV7_4 extends AbstractConvertV7andV8
{
  public static final Version CODE_LIST_VERSION = new Version (7, 4);
  public static final String DESTINATION_BASE_PATH = "created-codelists/v7.4/";
  public static final String DESTINATION_FILENAME_SUFFIX = " v7.4";

  public ConvertV7_4 ()
  {
    super (CODE_LIST_VERSION, DESTINATION_BASE_PATH, DESTINATION_FILENAME_SUFFIX);
  }
}
