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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base processor containing only version independent stuff.
 *
 * @author Philip Helger
 */
abstract class AbstractProcessor
{
  public static final String DO_NOT_EDIT = "This file was automatically generated.\nDo NOT edit!";
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractProcessor.class);

  protected AbstractProcessor ()
  {}

  protected void init () throws Exception
  {}

  protected abstract void convert () throws Exception;

  protected void done () throws Exception
  {}

  public final void run () throws Exception
  {
    init ();
    convert ();
    done ();
    LOGGER.info ("Successfully finished creation");
  }
}
