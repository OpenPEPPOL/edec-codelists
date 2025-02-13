/*
 * Copyright (C) 2020-2024 OpenPeppol AISBL (www.peppol.eu)
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
package eu.peppol.codelist.jaxb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.peppol.codelist.main.ConvertV9_1;
import eu.peppol.codelist.model.DocTypeRow;
import eu.peppol.codelist.model.ParticipantIdentifierSchemeRow;
import eu.peppol.codelist.model.ProcessRow;
import eu.peppol.codelist.model.TransportProfileRow;

/**
 * Check if the created lists match the XSD.
 *
 * @author Philip Helger
 */
public final class ConvertV9ResultFuncTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConvertV9ResultFuncTest.class);

  @Test
  public void testReadDocTypes ()
  {
    final File f = new File (ConvertV9_1.DESTINATION_BASE_PATH,
                             DocTypeRow.CODE_LIST_NAME + ConvertV9_1.DESTINATION_FILENAME_SUFFIX + ".xml");
    assertTrue (f.exists ());
    LOGGER.info ("Trying to read and validate " + f.getAbsolutePath ());

    final DocumentTypeCodeListMarshaller m = new DocumentTypeCodeListMarshaller ();
    final DocumentTypesType aList = m.read (f);
    assertNotNull (aList);
  }

  @Test
  public void testReadParticipantIdentifierSchemes ()
  {
    final File f = new File (ConvertV9_1.DESTINATION_BASE_PATH,
                             ParticipantIdentifierSchemeRow.CODE_LIST_NAME +
                                                                ConvertV9_1.DESTINATION_FILENAME_SUFFIX +
                                                                ".xml");
    assertTrue (f.exists ());
    LOGGER.info ("Trying to read and validate " + f.getAbsolutePath ());

    final ParticipantIdentifierSchemeCodeListMarshaller m = new ParticipantIdentifierSchemeCodeListMarshaller ();
    final ParticipantIdentifierSchemesType aList = m.read (f);
    assertNotNull (aList);
  }

  @Test
  public void testReadProcesses ()
  {
    final File f = new File (ConvertV9_1.DESTINATION_BASE_PATH,
                             ProcessRow.CODE_LIST_NAME + ConvertV9_1.DESTINATION_FILENAME_SUFFIX + ".xml");
    assertTrue (f.exists ());
    LOGGER.info ("Trying to read and validate " + f.getAbsolutePath ());

    final ProcessCodeListMarshaller m = new ProcessCodeListMarshaller ();
    final ProcessesType aList = m.read (f);
    assertNotNull (aList);
  }

  @Test
  public void testReadTransportProfiles ()
  {
    final File f = new File (ConvertV9_1.DESTINATION_BASE_PATH,
                             TransportProfileRow.CODE_LIST_NAME + ConvertV9_1.DESTINATION_FILENAME_SUFFIX + ".xml");
    assertTrue (f.exists ());
    LOGGER.info ("Trying to read and validate " + f.getAbsolutePath ());

    final TransportProfileCodeListMarshaller m = new TransportProfileCodeListMarshaller ();
    final TransportProfilesType aList = m.read (f);
    assertNotNull (aList);
  }
}
