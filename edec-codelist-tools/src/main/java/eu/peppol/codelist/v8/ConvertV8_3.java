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
package eu.peppol.codelist.v8;

import com.helger.commons.version.Version;

/**
 * Handle V8.3 code lists
 *
 * @author Philip Helger
 */
public final class ConvertV8_3 extends AbstractConvertV8
{
  private static final int MAJOR = 8;
  private static final int MINOR = 3;
  public static final Version CODE_LIST_VERSION = new Version (MAJOR, MINOR);
  public static final String DESTINATION_BASE_PATH = "created-codelists/v" + MAJOR + "." + MINOR + "/";
  public static final String DESTINATION_FILENAME_SUFFIX = " v" + MAJOR + "." + MINOR;

  public ConvertV8_3 ()
  {
    super (CODE_LIST_VERSION, DESTINATION_BASE_PATH, DESTINATION_FILENAME_SUFFIX);
  }
}
