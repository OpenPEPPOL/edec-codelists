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

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.url.URLHelper;
import com.helger.commons.version.Version;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.Row;
import com.helger.peppolid.CIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;

import eu.peppol.codelist.excel.ECodeListDataType;
import eu.peppol.codelist.excel.InMemoryXLSX;
import eu.peppol.codelist.excel.XLSXReadOptions;
import eu.peppol.codelist.excel.XLSXToGC;

/**
 * Utility class to create the Genericode files from the Excel code list. Also
 * creates Java source files with the predefined identifiers.
 *
 * @author Philip Helger
 */
public final class ConvertV7 extends AbstractConverter
{
  private final ICommonsMap <IProcessIdentifier, ICommonsList <String>> m_aProcIDs = new CommonsLinkedHashMap <> ();

  public ConvertV7 ()
  {
    super (new Version (7), "created-codelists/v7/", "V7");
  }

  private void _handleDocumentTypes (final Sheet aDocumentSheet)
  {
    // Create GeneriCode file
    final XLSXReadOptions aReadOptions = new XLSXReadOptions ();
    aReadOptions.addColumn ("profilecode", false, ECodeListDataType.STRING);
    aReadOptions.addKeyColumn ("scheme", true, ECodeListDataType.STRING);
    aReadOptions.addKeyColumn ("id", true, ECodeListDataType.STRING);
    aReadOptions.addColumn ("since", true, ECodeListDataType.STRING);
    aReadOptions.addColumn ("deprecated", true, ECodeListDataType.BOOLEAN);
    aReadOptions.addColumn ("deprecated-since", false, ECodeListDataType.STRING);
    aReadOptions.addColumn ("comment", false, ECodeListDataType.STRING);
    aReadOptions.addColumn ("issued-by-openpeppol", true, ECodeListDataType.BOOLEAN);
    aReadOptions.addColumn ("bis-version", false, ECodeListDataType.INT);
    aReadOptions.addColumn ("domain-community", true, ECodeListDataType.STRING);
    aReadOptions.addColumn ("process-ids", true, ECodeListDataType.STRING);
    final InMemoryXLSX aXLSX = InMemoryXLSX.read (aReadOptions, aDocumentSheet);

    final String sCodeListName = "PeppolDocumentTypes";
    final CodeListDocument aCodeList = XLSXToGC.convertToSimpleCodeList (aXLSX,
                                                                         aReadOptions.getAllColumns (),
                                                                         sCodeListName,
                                                                         m_aCodeListVersion,
                                                                         URLHelper.getAsURI ("urn:peppol.eu:names:identifier:documenttypes"));

    // Save as XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", m_aCodeListVersion.getAsString ());
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ())
      {
        final String sProfileCode = getGCRowValue (aRow, "profilecode");
        final String sScheme = getGCRowValue (aRow, "scheme");
        final String sID = getGCRowValue (aRow, "id");
        final String sSince = getGCRowValue (aRow, "since");
        final boolean bDeprecated = parseDeprecated (getGCRowValue (aRow, "deprecated"));
        final String sDeprecatedSince = getGCRowValue (aRow, "deprecated-since");
        if (bDeprecated && StringHelper.hasNoText (sDeprecatedSince))
          throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");
        final boolean bIssuedByOpenPEPPOL = parseIssuedByOpenPEPPOL (getGCRowValue (aRow, "issued-by-openpeppol"));
        final String sBISVersion = getGCRowValue (aRow, "bis-version");
        if (bIssuedByOpenPEPPOL && StringHelper.hasNoText (sBISVersion))
          throw new IllegalStateException ("If issued by OpenPEPPOL, a BIS version is required");
        if (StringHelper.hasText (sBISVersion) && !StringParser.isUnsignedInt (sBISVersion))
          throw new IllegalStateException ("Code list entry has an invalid BIS version number - must be numeric");
        final String sDomainCommunity = getGCRowValue (aRow, "domain-community");
        final String sProcessIDs = getGCRowValue (aRow, "process-ids");

        final IMicroElement eAgency = eRoot.appendElement ("document-type");
        eAgency.setAttribute ("profilecode", sProfileCode);
        eAgency.setAttribute ("scheme", sScheme);
        eAgency.setAttribute ("id", sID);
        eAgency.setAttribute ("since", sSince);
        eAgency.setAttribute ("deprecated", bDeprecated);
        eAgency.setAttribute ("deprecated-since", sDeprecatedSince);
        eAgency.setAttribute ("issued-by-openpeppol", bIssuedByOpenPEPPOL);
        eAgency.setAttribute ("bis-version", sBISVersion);
        eAgency.setAttribute ("domain-community", sDomainCommunity);
        final ICommonsList <IProcessIdentifier> aProcIDs = getAllProcessIDsFromMultilineString (sProcessIDs);
        for (final IProcessIdentifier aProcID : aProcIDs)
        {
          eAgency.appendElement ("process-id")
                 .setAttribute ("scheme", aProcID.getScheme ())
                 .setAttribute ("value", aProcID.getValue ());
          m_aProcIDs.computeIfAbsent (aProcID, k -> new CommonsArrayList <> ())
                    .add (CIdentifier.getURIEncoded (sScheme, sID));
        }
      }
    }

    // Write at the end
    writeGenericodeFile (aCodeList, sCodeListName);
    writeXMLFile (aDoc, sCodeListName);
  }

  private void _handleParticipantIdentifierSchemes (final Sheet aParticipantSheet)
  {
    // Read excel file
    final XLSXReadOptions aReadOptions = new XLSXReadOptions ();
    aReadOptions.addKeyColumn ("schemeid", true, ECodeListDataType.STRING);
    aReadOptions.addKeyColumn ("iso6523", true, ECodeListDataType.STRING);
    aReadOptions.addColumn ("country", true, ECodeListDataType.STRING);
    aReadOptions.addKeyColumn ("schemename", true, ECodeListDataType.STRING);
    aReadOptions.addColumn ("issuingagency", false, ECodeListDataType.STRING);
    aReadOptions.addColumn ("since", true, ECodeListDataType.STRING);
    aReadOptions.addColumn ("deprecated", true, ECodeListDataType.BOOLEAN);
    aReadOptions.addColumn ("deprecated-since", false, ECodeListDataType.STRING);
    aReadOptions.addColumn ("structure", false, ECodeListDataType.STRING);
    aReadOptions.addColumn ("display", false, ECodeListDataType.STRING);
    aReadOptions.addColumn ("examples", false, ECodeListDataType.STRING);
    aReadOptions.addColumn ("validation-rules", false, ECodeListDataType.STRING);
    aReadOptions.addColumn ("usage", false, ECodeListDataType.STRING);
    final InMemoryXLSX aXLSX = InMemoryXLSX.read (aReadOptions, aParticipantSheet);

    final String sCodeListName = "PeppolParticipantIdentifierSchemes";
    final CodeListDocument aCodeList = XLSXToGC.convertToSimpleCodeList (aXLSX,
                                                                         aReadOptions.getAllColumns (),
                                                                         sCodeListName,
                                                                         m_aCodeListVersion,
                                                                         URLHelper.getAsURI ("urn:peppol.eu:names:identifier:participantidentifierschemes"));

    // Save data also as XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", m_aCodeListVersion.getAsString ());
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ())
      {
        final String sSchemeID = getGCRowValue (aRow, "schemeid");
        final String sISO6523 = getGCRowValue (aRow, "iso6523");
        final String sCountryCode = getGCRowValue (aRow, "country");
        final String sSchemeName = getGCRowValue (aRow, "schemename");
        final String sIssuingAgency = getGCRowValue (aRow, "issuingagency");
        final String sSince = getGCRowValue (aRow, "since");
        final boolean bDeprecated = parseDeprecated (getGCRowValue (aRow, "deprecated"));
        final String sDeprecatedSince = getGCRowValue (aRow, "deprecated-since");
        final String sStructure = getGCRowValue (aRow, "structure");
        final String sDisplay = getGCRowValue (aRow, "display");
        final String sExamples = getGCRowValue (aRow, "examples");
        final String sValidationRules = getGCRowValue (aRow, "validation-rules");
        final String sUsage = getGCRowValue (aRow, "usage");

        if (StringHelper.hasNoText (sSchemeID))
          throw new IllegalStateException ("schemeID");
        if (sSchemeID.indexOf (' ') >= 0)
          throw new IllegalStateException ("Scheme IDs are not supposed to contain spaces!");
        if (StringHelper.hasNoText (sISO6523))
          throw new IllegalStateException ("ISO6523Code");
        if (!RegExHelper.stringMatchesPattern ("[0-9]{4}", sISO6523))
          throw new IllegalStateException ("The ISO 6523 code '" + sISO6523 + "' does not consist of 4 numbers");
        if (bDeprecated && StringHelper.hasNoText (sDeprecatedSince))
          throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");

        final IMicroElement eAgency = eRoot.appendElement ("identifier-scheme");
        eAgency.setAttribute ("schemeid", sSchemeID);
        eAgency.setAttribute ("country", sCountryCode);
        eAgency.setAttribute ("schemename", sSchemeName);
        // legacy name
        eAgency.setAttribute ("agencyname", sIssuingAgency);
        eAgency.setAttribute ("iso6523", sISO6523);
        eAgency.setAttribute ("since", sSince);
        eAgency.setAttribute ("deprecated", bDeprecated);
        eAgency.setAttribute ("deprecated-since", sDeprecatedSince);
        if (StringHelper.hasText (sStructure))
          eAgency.appendElement ("structure").appendText (sStructure);
        if (StringHelper.hasText (sDisplay))
          eAgency.appendElement ("display").appendText (sDisplay);
        if (StringHelper.hasText (sExamples))
          eAgency.appendElement ("examples").appendText (sExamples);
        if (StringHelper.hasText (sValidationRules))
          eAgency.appendElement ("validation-rules").appendText (sValidationRules);
        if (StringHelper.hasText (sUsage))
          eAgency.appendElement ("usage").appendText (sUsage);
      }
    }

    // Write at the end
    writeGenericodeFile (aCodeList, sCodeListName);
    writeXMLFile (aDoc, sCodeListName);
  }

  private void _handleTransportProfileIdentifiers (final Sheet aTPSheet)
  {
    final XLSXReadOptions aReadOptions = new XLSXReadOptions ();
    aReadOptions.addColumn ("protocol", true, ECodeListDataType.STRING);
    aReadOptions.addColumn ("profileversion", true, ECodeListDataType.STRING);
    aReadOptions.addKeyColumn ("profileid", true, ECodeListDataType.STRING);
    aReadOptions.addColumn ("since", true, ECodeListDataType.STRING);
    aReadOptions.addColumn ("deprecated", true, ECodeListDataType.BOOLEAN);
    aReadOptions.addColumn ("deprecated-since", false, ECodeListDataType.STRING);
    final InMemoryXLSX aXLSX = InMemoryXLSX.read (aReadOptions, aTPSheet);

    final String sCodeListName = "PeppolTransportProfiles";
    final CodeListDocument aCodeList = XLSXToGC.convertToSimpleCodeList (aXLSX,
                                                                         aReadOptions.getAllColumns (),
                                                                         sCodeListName,
                                                                         m_aCodeListVersion,
                                                                         URLHelper.getAsURI ("urn:peppol.eu:names:identifier:transportprofile"));

    // Save as XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", m_aCodeListVersion.getAsString ());
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ())
      {
        final String sProtocol = getGCRowValue (aRow, "protocol");
        final String sProfileVersion = getGCRowValue (aRow, "profileversion");
        final String sProfileID = getGCRowValue (aRow, "profileid");
        final String sSince = getGCRowValue (aRow, "since");
        final boolean bDeprecated = parseDeprecated (getGCRowValue (aRow, "deprecated"));
        final String sDeprecatedSince = getGCRowValue (aRow, "deprecated-since");

        if (bDeprecated && StringHelper.hasNoText (sDeprecatedSince))
          throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");

        final IMicroElement eAgency = eRoot.appendElement ("transport-profile");
        eAgency.setAttribute ("protocol", sProtocol);
        eAgency.setAttribute ("profileversion", sProfileVersion);
        eAgency.setAttribute ("profileid", sProfileID);
        eAgency.setAttribute ("since", sSince);
        eAgency.setAttribute ("deprecated", bDeprecated);
        eAgency.setAttribute ("deprecated-since", sDeprecatedSince);
      }
    }

    // Write at the end
    writeGenericodeFile (aCodeList, sCodeListName);
    writeXMLFile (aDoc, sCodeListName);
  }

  private void _handleProcessIdentifiers ()
  {
    final XLSXReadOptions aReadOptions = new XLSXReadOptions ();
    aReadOptions.addKeyColumn ("scheme", true, ECodeListDataType.STRING);
    aReadOptions.addKeyColumn ("value", true, ECodeListDataType.STRING);

    final ICommonsList <IProcessIdentifier> aProcIDs = new CommonsArrayList <> (m_aProcIDs.keySet ());
    final InMemoryXLSX aXLSX = InMemoryXLSX.createForProcessIDs (aProcIDs);

    final String sCodeListName = "PeppolProcessIdentifiers";
    final CodeListDocument aCodeList = XLSXToGC.convertToSimpleCodeList (aXLSX,
                                                                         aReadOptions.getAllColumns (),
                                                                         sCodeListName,
                                                                         m_aCodeListVersion,
                                                                         URLHelper.getAsURI ("urn:peppol.eu:names:identifier:process"));

    // Save as XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", m_aCodeListVersion.getAsString ());
      for (final IProcessIdentifier aProcID : aProcIDs)
      {
        final IMicroElement eProcess = eRoot.appendElement ("process");
        eProcess.setAttribute ("scheme", aProcID.getScheme ());
        eProcess.setAttribute ("value", aProcID.getValue ());
      }
    }

    // Write at the end
    writeGenericodeFile (aCodeList, sCodeListName);
    writeXMLFile (aDoc, sCodeListName);
  }

  @Override
  protected void convert () throws Exception
  {
    final String sFilenameVersion = m_aCodeListVersion.getAsString (false) + " draft";

    for (final CodeListSource aCLF : new CodeListSource [] { new CodeListSource ("Document types",
                                                                                 sFilenameVersion,
                                                                                 this::_handleDocumentTypes),
                                                             new CodeListSource ("Participant identifier schemes",
                                                                                 sFilenameVersion,
                                                                                 this::_handleParticipantIdentifierSchemes),
                                                             new CodeListSource ("Transport profiles",
                                                                                 sFilenameVersion,
                                                                                 this::_handleTransportProfileIdentifiers) })
    {
      // Where is the Excel?
      final IReadableResource aExcel = new FileSystemResource (aCLF.getFile ());
      if (!aExcel.exists ())
        throw new IllegalStateException ("The Excel file '" +
                                         aCLF.getFile ().getAbsolutePath () +
                                         "' could not be found!");

      // Interpret as Excel
      try (final Workbook aWB = new XSSFWorkbook (aExcel.getInputStream ()))
      {
        // Check whether all required sheets are present
        final Sheet aSheet = aWB.getSheetAt (0);
        if (aSheet == null)
          throw new IllegalStateException ("The first sheet could not be found!");

        aCLF.handle (aSheet);
      }
    }

    _handleProcessIdentifiers ();
  }
}
