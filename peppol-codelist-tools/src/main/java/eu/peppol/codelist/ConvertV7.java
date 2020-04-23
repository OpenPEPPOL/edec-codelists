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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.version.Version;
import com.helger.genericode.excel.ExcelReadOptions;
import com.helger.genericode.excel.ExcelSheetToCodeList10;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.Row;
import com.helger.genericode.v10.UseType;
import com.helger.peppolid.CIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;

/**
 * Utility class to create the Genericode files from the Excel code list. Also
 * creates Java source files with the predefined identifiers.
 *
 * @author Philip Helger
 */
public final class ConvertV7 extends AbstractConverter
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ConvertV7.class);
  private static final Version CODELIST_VERSION = new Version (7);
  private static final String CODELIST_FILE_SUFFIX = " draft";
  private static final String FILENAME_SUFFIX = "V7";
  private static final ICommonsMap <IProcessIdentifier, ICommonsList <String>> KNOWN_PROCESS_IDS = new CommonsLinkedHashMap <> ();

  private void _handleDocumentTypes (final Sheet aDocumentSheet) throws URISyntaxException
  {
    // Create GeneriCode file
    final ExcelReadOptions <UseType> aReadOptions = new ExcelReadOptions <UseType> ().setLinesToSkip (1)
                                                                                     .setLineIndexShortName (0);
    {
      int nCol = 0;
      aReadOptions.addColumn (nCol++, "profilecode", UseType.OPTIONAL, "string", false);
      aReadOptions.addColumn (nCol++, "scheme", UseType.REQUIRED, "string", true);
      aReadOptions.addColumn (nCol++, "id", UseType.REQUIRED, "string", true);
      aReadOptions.addColumn (nCol++, "since", UseType.REQUIRED, "string", false);
      aReadOptions.addColumn (nCol++, "deprecated", UseType.REQUIRED, "boolean", false);
      aReadOptions.addColumn (nCol++, "deprecated-since", UseType.OPTIONAL, "string", false);
      aReadOptions.addColumn (nCol++, "comment", UseType.OPTIONAL, "string", false);
      aReadOptions.addColumn (nCol++, "issued-by-openpeppol", UseType.REQUIRED, "boolean", false);
      aReadOptions.addColumn (nCol++, "bis-version", UseType.OPTIONAL, "int", false);
      aReadOptions.addColumn (nCol++, "domain-community", UseType.REQUIRED, "string", false);
      aReadOptions.addColumn (nCol++, "process-ids", UseType.REQUIRED, "string", false);
    }
    final String sCodeListName = "PeppolDocumentTypes";
    final CodeListDocument aCodeList = ExcelSheetToCodeList10.convertToSimpleCodeList (aDocumentSheet,
                                                                                       aReadOptions,
                                                                                       sCodeListName,
                                                                                       CODELIST_VERSION.getAsString (),
                                                                                       new URI ("urn:peppol.eu:names:identifier:documenttypes"),
                                                                                       new URI ("urn:peppol.eu:names:identifier:documenttypes-" +
                                                                                                CODELIST_VERSION.getAsString ()),
                                                                                       null);

    // Save as XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", CODELIST_VERSION.getAsString ());
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ())
      {
        final String sProfileCode = CodeListHelper.getGCRowValue (aRow, "profilecode");
        final String sScheme = CodeListHelper.getGCRowValue (aRow, "scheme");
        final String sID = CodeListHelper.getGCRowValue (aRow, "id");
        final String sSince = CodeListHelper.getGCRowValue (aRow, "since");
        final boolean bDeprecated = CodeListHelper.parseDeprecated (CodeListHelper.getGCRowValue (aRow, "deprecated"));
        final String sDeprecatedSince = CodeListHelper.getGCRowValue (aRow, "deprecated-since");
        if (bDeprecated && StringHelper.hasNoText (sDeprecatedSince))
          throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");
        final boolean bIssuedByOpenPEPPOL = CodeListHelper.parseIssuedByOpenPEPPOL (CodeListHelper.getGCRowValue (aRow,
                                                                                                                  "issued-by-openpeppol"));
        final String sBISVersion = CodeListHelper.getGCRowValue (aRow, "bis-version");
        if (bIssuedByOpenPEPPOL && StringHelper.hasNoText (sBISVersion))
          throw new IllegalStateException ("If issued by OpenPEPPOL, a BIS version is required");
        if (StringHelper.hasText (sBISVersion) && !StringParser.isUnsignedInt (sBISVersion))
          throw new IllegalStateException ("Code list entry has an invalid BIS version number - must be numeric");
        final String sDomainCommunity = CodeListHelper.getGCRowValue (aRow, "domain-community");
        final String sProcessIDs = CodeListHelper.getGCRowValue (aRow, "process-ids");

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
          KNOWN_PROCESS_IDS.computeIfAbsent (aProcID, k -> new CommonsArrayList <> ())
                           .add (CIdentifier.getURIEncoded (sScheme, sID));
        }
      }
    }

    // Write at the end
    writeGenericodeFile (aCodeList, sCodeListName + FILENAME_SUFFIX + ".gc");
    writeXMLFile (aDoc, sCodeListName + FILENAME_SUFFIX + ".xml");
  }

  private void _handleParticipantIdentifierSchemes (final Sheet aParticipantSheet) throws URISyntaxException
  {
    // Read excel file
    final ExcelReadOptions <UseType> aReadOptions = new ExcelReadOptions <UseType> ().setLinesToSkip (1)
                                                                                     .setLineIndexShortName (0);
    {
      int nCol = 0;
      aReadOptions.addColumn (nCol++, "schemeid", UseType.REQUIRED, "string", true);
      aReadOptions.addColumn (nCol++, "iso6523", UseType.REQUIRED, "string", true);
      aReadOptions.addColumn (nCol++, "country", UseType.REQUIRED, "string", true);
      aReadOptions.addColumn (nCol++, "schemename", UseType.REQUIRED, "string", true);
      aReadOptions.addColumn (nCol++, "issuingagency", UseType.OPTIONAL, "string", false);
      aReadOptions.addColumn (nCol++, "since", UseType.REQUIRED, "string", false);
      aReadOptions.addColumn (nCol++, "deprecated", UseType.REQUIRED, "boolean", false);
      aReadOptions.addColumn (nCol++, "deprecated-since", UseType.OPTIONAL, "string", false);
      aReadOptions.addColumn (nCol++, "structure", UseType.OPTIONAL, "string", false);
      aReadOptions.addColumn (nCol++, "display", UseType.OPTIONAL, "string", false);
      aReadOptions.addColumn (nCol++, "examples", UseType.OPTIONAL, "string", false);
      aReadOptions.addColumn (nCol++, "validation-rules", UseType.OPTIONAL, "string", false);
      aReadOptions.addColumn (nCol++, "usage", UseType.OPTIONAL, "string", false);
    }

    final String sCodeListName = "PeppolParticipantIdentifierSchemes";
    final CodeListDocument aCodeList = ExcelSheetToCodeList10.convertToSimpleCodeList (aParticipantSheet,
                                                                                       aReadOptions,
                                                                                       sCodeListName,
                                                                                       CODELIST_VERSION.getAsString (),
                                                                                       new URI ("urn:peppol.eu:names:identifier:participantidentifierschemes"),
                                                                                       new URI ("urn:peppol.eu:names:identifier:participantidentifierschemes-" +
                                                                                                CODELIST_VERSION.getAsString ()),
                                                                                       null);

    // Save data also as XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", CODELIST_VERSION.getAsString ());
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ())
      {
        final String sSchemeID = CodeListHelper.getGCRowValue (aRow, "schemeid");
        final String sISO6523 = CodeListHelper.getGCRowValue (aRow, "iso6523");
        final String sCountryCode = CodeListHelper.getGCRowValue (aRow, "country");
        final String sSchemeName = CodeListHelper.getGCRowValue (aRow, "schemename");
        final String sIssuingAgency = CodeListHelper.getGCRowValue (aRow, "issuingagency");
        final String sSince = CodeListHelper.getGCRowValue (aRow, "since");
        final boolean bDeprecated = CodeListHelper.parseDeprecated (CodeListHelper.getGCRowValue (aRow, "deprecated"));
        final String sDeprecatedSince = CodeListHelper.getGCRowValue (aRow, "deprecated-since");
        final String sStructure = CodeListHelper.getGCRowValue (aRow, "structure");
        final String sDisplay = CodeListHelper.getGCRowValue (aRow, "display");
        final String sExamples = CodeListHelper.getGCRowValue (aRow, "examples");
        final String sValidationRules = CodeListHelper.getGCRowValue (aRow, "validation-rules");
        final String sUsage = CodeListHelper.getGCRowValue (aRow, "usage");

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
    writeGenericodeFile (aCodeList, sCodeListName + FILENAME_SUFFIX + ".gc");
    writeXMLFile (aDoc, sCodeListName + FILENAME_SUFFIX + ".xml");
  }

  private void _handleTransportProfileIdentifiers (final Sheet aTPSheet) throws URISyntaxException
  {
    final ExcelReadOptions <UseType> aReadOptions = new ExcelReadOptions <UseType> ().setLinesToSkip (1)
                                                                                     .setLineIndexShortName (0);
    {
      int nCol = 0;
      aReadOptions.addColumn (nCol++, "protocol", UseType.REQUIRED, "string", false);
      aReadOptions.addColumn (nCol++, "profileversion", UseType.REQUIRED, "string", false);
      aReadOptions.addColumn (nCol++, "profileid", UseType.REQUIRED, "string", true);
      aReadOptions.addColumn (nCol++, "since", UseType.REQUIRED, "string", false);
      aReadOptions.addColumn (nCol++, "deprecated", UseType.REQUIRED, "boolean", false);
      aReadOptions.addColumn (nCol++, "deprecated-since", UseType.OPTIONAL, "string", false);
    }

    final String sCodeListName = "PeppolTransportProfiles";
    final CodeListDocument aCodeList = ExcelSheetToCodeList10.convertToSimpleCodeList (aTPSheet,
                                                                                       aReadOptions,
                                                                                       sCodeListName,
                                                                                       CODELIST_VERSION.getAsString (),
                                                                                       new URI ("urn:peppol.eu:names:identifier:transportprofile"),
                                                                                       new URI ("urn:peppol.eu:names:identifier:transportprofile-" +
                                                                                                CODELIST_VERSION.getAsString ()),
                                                                                       null);

    // Save as XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", CODELIST_VERSION.getAsString ());
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ())
      {
        final String sProtocol = CodeListHelper.getGCRowValue (aRow, "protocol");
        final String sProfileVersion = CodeListHelper.getGCRowValue (aRow, "profileversion");
        final String sProfileID = CodeListHelper.getGCRowValue (aRow, "profileid");
        final String sSince = CodeListHelper.getGCRowValue (aRow, "since");
        final boolean bDeprecated = CodeListHelper.parseDeprecated (CodeListHelper.getGCRowValue (aRow, "deprecated"));
        final String sDeprecatedSince = CodeListHelper.getGCRowValue (aRow, "deprecated-since");

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
    writeGenericodeFile (aCodeList, sCodeListName + FILENAME_SUFFIX + ".gc");
    writeXMLFile (aDoc, sCodeListName + FILENAME_SUFFIX + ".xml");
  }

  private void _handleProcessIdentifiers ()
  {
    final String sCodeListName = "PeppolProcessIdentifiers";

    // Save as XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", CODELIST_VERSION.getAsString ());
      for (final Map.Entry <IProcessIdentifier, ICommonsList <String>> aEntry : KNOWN_PROCESS_IDS.entrySet ())
      {
        final String sScheme = aEntry.getKey ().getScheme ();
        final String sValue = aEntry.getKey ().getValue ();

        final IMicroElement eProcess = eRoot.appendElement ("process");
        eProcess.setAttribute ("scheme", sScheme);
        eProcess.setAttribute ("value", sValue);
      }
    }

    // Write at the end
    writeXMLFile (aDoc, sCodeListName + FILENAME_SUFFIX + ".xml");
  }

  public ConvertV7 ()
  {
    super ("created-codelists/v7/");
  }

  @Override
  protected void convert () throws Exception
  {
    // DocumentType must be before Processes to fill the static list
    final String sFilenameVersion = CODELIST_VERSION.getAsString (false) + CODELIST_FILE_SUFFIX;
    for (final CodeListFile aCLF : new CodeListFile [] { new CodeListFile ("Document types",
                                                                           sFilenameVersion,
                                                                           this::_handleDocumentTypes),
                                                         new CodeListFile ("Participant identifier schemes",
                                                                           sFilenameVersion,
                                                                           this::_handleParticipantIdentifierSchemes),
                                                         new CodeListFile ("Transport profiles",
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
