package eu.peppol.codelist.jaxb;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import eu.peppol.codelist.ConvertV7;
import eu.peppol.codelist.model.DocTypeRow;
import eu.peppol.codelist.model.ParticipantIdentifierSchemeRow;

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
}
