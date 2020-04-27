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
package eu.peppol.codelist.jaxb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import eu.peppol.codelist.ConvertV7;
import eu.peppol.codelist.model.DocTypeRow;
import eu.peppol.codelist.model.ParticipantIdentifierSchemeRow;
import eu.peppol.codelist.model.ProcessRow;
import eu.peppol.codelist.model.TransportProfileRow;

/**
 * Check if the create lists match the XSD.
 *
 * @author Philip Helger
 */
public final class ConvertV7ResultFuncTest
{
  @Test
  public void testReadDocTypes ()
  {
    final DocumentTypeCodeListMarshaller m = new DocumentTypeCodeListMarshaller ();
    final File f = new File (ConvertV7.DESTINATION_BASE_PATH, DocTypeRow.CODE_LIST_NAME + ConvertV7.DESTINATION_FILENAME_SUFFIX + ".xml");
    assertTrue (f.exists ());
    final DocumentTypesType aList = m.read (f);
    assertNotNull (aList);
  }

  @Test
  public void testReadParticipantIdentifierSchemes ()
  {
    final ParticipantIdentifierSchemeCodeListMarshaller m = new ParticipantIdentifierSchemeCodeListMarshaller ();
    final File f = new File (ConvertV7.DESTINATION_BASE_PATH,
                             ParticipantIdentifierSchemeRow.CODE_LIST_NAME + ConvertV7.DESTINATION_FILENAME_SUFFIX + ".xml");
    assertTrue (f.exists ());
    final ParticipantIdentifierSchemesType aList = m.read (f);
    assertNotNull (aList);
  }

  @Test
  public void testReadProcesses ()
  {
    final ProcessCodeListMarshaller m = new ProcessCodeListMarshaller ();
    final File f = new File (ConvertV7.DESTINATION_BASE_PATH, ProcessRow.CODE_LIST_NAME + ConvertV7.DESTINATION_FILENAME_SUFFIX + ".xml");
    assertTrue (f.exists ());
    final ProcessesType aList = m.read (f);
    assertNotNull (aList);
  }

  @Test
  public void testReadTransportProfiles ()
  {
    final TransportProfileCodeListMarshaller m = new TransportProfileCodeListMarshaller ();
    final File f = new File (ConvertV7.DESTINATION_BASE_PATH,
                             TransportProfileRow.CODE_LIST_NAME + ConvertV7.DESTINATION_FILENAME_SUFFIX + ".xml");
    assertTrue (f.exists ());
    final TransportProfilesType aList = m.read (f);
    assertNotNull (aList);
  }
}
