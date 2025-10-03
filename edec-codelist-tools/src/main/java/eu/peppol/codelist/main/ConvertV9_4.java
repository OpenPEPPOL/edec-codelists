/*
 * Copyright (C) 2020-2025 OpenPeppol AISBL (www.peppol.org)
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

import com.helger.base.version.Version;

/**
 * Handle V9.x code lists
 *
 * @author Philip Helger
 */
public final class ConvertV9_4 extends AbstractConvertV9
{
  private static final int MAJOR = 9;
  private static final int MINOR = 4;
  private static final int MICRO = 0;
  private static final String STR_MICRO = MICRO < 1 ? "" : "." + MICRO;
  public static final Version CODE_LIST_VERSION = new Version (MAJOR, MINOR, MICRO);
  public static final String DESTINATION_BASE_PATH = "created-codelists/v" + MAJOR + "." + MINOR + STR_MICRO + "/";
  public static final String DESTINATION_FILENAME_SUFFIX = " v" + MAJOR + "." + MINOR + STR_MICRO;

  public ConvertV9_4 ()
  {
    super (CODE_LIST_VERSION, DESTINATION_BASE_PATH, DESTINATION_FILENAME_SUFFIX);
  }
}
