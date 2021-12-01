/*
 * Copyright (C) 2020-2021 OpenPeppol AISBL (www.peppol.eu)
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
 * Handle V8.0 code lists
 *
 * @author Philip Helger
 */
public final class ConvertV8_0 extends AbstractConvertV8
{
  public static final Version CODE_LIST_VERSION = new Version (8, 0);
  public static final String DESTINATION_BASE_PATH = "created-codelists/v8.0/";
  public static final String DESTINATION_FILENAME_SUFFIX = " v8.0";

  public ConvertV8_0 ()
  {
    super (CODE_LIST_VERSION, DESTINATION_BASE_PATH, DESTINATION_FILENAME_SUFFIX);
  }
}
