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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nonnull;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.version.Version;
import com.helger.genericode.Genericode10CodeListMarshaller;
import com.helger.genericode.excel.ExcelReadOptions;
import com.helger.genericode.excel.ExcelSheetToCodeList10;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.Row;
import com.helger.genericode.v10.UseType;
import com.helger.peppolid.CIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.namespace.MapBasedNamespaceContext;

/**
 * Utility class to create the Genericode files from the Excel code list. Also
 * creates Java source files with the predefined identifiers.
 *
 * @author Philip Helger
 */
public final class MainProcessExcel_v7 extends AbstractProcessor
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainProcessExcel_v7.class);
  private static final Version CODELIST_VERSION = new Version (7);
  private static final String CODELIST_FILE_SUFFIX = " draft";
  private static final String RESULT_DIRECTORY = "created-codelists/";
  private static final String FILENAME_SUFFIX = "V7";
  private static final ICommonsMap <IProcessIdentifier, ICommonsList <String>> KNOWN_PROCESS_IDS = new CommonsLinkedHashMap <> ();

  @Nonnull
  private static ICommonsList <IProcessIdentifier> _getProcIDs (@Nonnull final String sProcessIDs)
  {
    final ICommonsList <IProcessIdentifier> ret = new CommonsArrayList <> ();
    for (final String s : StringHelper.getExploded ('\n', StringHelper.replaceAll (sProcessIDs, '\r', '\n')))
    {
      final String sProcessID = s.trim ();
      if (StringHelper.hasNoText (sProcessID))
        throw new IllegalStateException ("Found empty process ID in '" + sProcessIDs + "'");
      final IProcessIdentifier aProcID = PeppolIdentifierFactory.INSTANCE.parseProcessIdentifier (sProcessID);
      if (aProcID == null)
        throw new IllegalStateException ("Failed to parse process ID '" + sProcessID + "'");
      ret.add (aProcID);
    }
    if (ret.isEmpty ())
      throw new IllegalStateException ("Found no single process ID in '" + sProcessIDs + "'");
    return ret;
  }

  private static void _writeGenericodeFile (@Nonnull final CodeListDocument aCodeList, @Nonnull final String sFilename)
  {
    final MapBasedNamespaceContext aNsCtx = new MapBasedNamespaceContext ();
    aNsCtx.setDefaultNamespaceURI ("");
    aNsCtx.addMapping ("gc", "http://docs.oasis-open.org/codelist/ns/genericode/1.0/");
    aNsCtx.addMapping ("ext", "urn:www.helger.com:schemas:genericode-ext");

    final Genericode10CodeListMarshaller aMarshaller = new Genericode10CodeListMarshaller ();
    aMarshaller.setNamespaceContext (aNsCtx);
    aMarshaller.setFormattedOutput (true);
    if (aMarshaller.write (aCodeList, new File (sFilename)).isFailure ())
      throw new IllegalStateException ("Failed to write file " + sFilename);
    LOGGER.info ("Wrote Genericode file " + sFilename);
  }

  private static void _handleDocumentTypes (final Sheet aDocumentSheet) throws URISyntaxException
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
    final CodeListDocument aCodeList = ExcelSheetToCodeList10.convertToSimpleCodeList (aDocumentSheet,
                                                                                       aReadOptions,
                                                                                       "PeppolDocumentTypeIdentifier",
                                                                                       CODELIST_VERSION.getAsString (),
                                                                                       new URI ("urn:peppol.eu:names:identifier:documenttypes"),
                                                                                       new URI ("urn:peppol.eu:names:identifier:documenttypes-2.0"),
                                                                                       null);
    _writeGenericodeFile (aCodeList, RESULT_DIRECTORY + "PeppolDocumentTypeIdentifier" + FILENAME_SUFFIX + ".gc");

    // Save as XML
    {
      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", CODELIST_VERSION.getAsString ());
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ())
      {
        final String sProfileCode = Helper.getRowValue (aRow, "profilecode");
        final String sScheme = Helper.getRowValue (aRow, "scheme");
        final String sID = Helper.getRowValue (aRow, "id");
        final String sSince = Helper.getRowValue (aRow, "since");
        final boolean bDeprecated = Helper.parseDeprecated (Helper.getRowValue (aRow, "deprecated"));
        final String sDeprecatedSince = Helper.getRowValue (aRow, "deprecated-since");
        if (bDeprecated && StringHelper.hasNoText (sDeprecatedSince))
          throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");
        final boolean bIssuedByOpenPEPPOL = Helper.parseIssuedByOpenPEPPOL (Helper.getRowValue (aRow,
                                                                                                "issued-by-openpeppol"));
        final String sBISVersion = Helper.getRowValue (aRow, "bis-version");
        if (bIssuedByOpenPEPPOL && StringHelper.hasNoText (sBISVersion))
          throw new IllegalStateException ("If issued by OpenPEPPOL, a BIS version is required");
        if (StringHelper.hasText (sBISVersion) && !StringParser.isUnsignedInt (sBISVersion))
          throw new IllegalStateException ("Code list entry has an invalid BIS version number - must be numeric");
        final String sDomainCommunity = Helper.getRowValue (aRow, "domain-community");
        final String sProcessIDs = Helper.getRowValue (aRow, "process-ids");

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
        final ICommonsList <IProcessIdentifier> aProcIDs = _getProcIDs (sProcessIDs);
        for (final IProcessIdentifier aProcID : aProcIDs)
        {
          eAgency.appendElement ("process-id")
                 .setAttribute ("scheme", aProcID.getScheme ())
                 .setAttribute ("value", aProcID.getValue ());
          KNOWN_PROCESS_IDS.computeIfAbsent (aProcID, k -> new CommonsArrayList <> ())
                           .add (CIdentifier.getURIEncoded (sScheme, sID));
        }
      }
      MicroWriter.writeToFile (aDoc,
                               new File (RESULT_DIRECTORY + "PeppolDocumentTypeIdentifier" + FILENAME_SUFFIX + ".xml"));
    }

  }

  private static void _writeValidationPartyIdFile (final Sheet aParticipantSheet) throws URISyntaxException
  {
    // Read excel file
    final ExcelReadOptions <UseType> aReadOptions = new ExcelReadOptions <UseType> ().setLinesToSkip (1)
                                                                                     .setLineIndexShortName (0);
    aReadOptions.addColumn (0, "code", UseType.REQUIRED, "string", true);
    aReadOptions.addColumn (2, "name", UseType.OPTIONAL, "string", false);

    // Convert to GeneriCode
    final CodeListDocument aCodeList = ExcelSheetToCodeList10.convertToSimpleCodeList (aParticipantSheet,
                                                                                       aReadOptions,
                                                                                       "Scheme Agency",
                                                                                       CODELIST_VERSION.getAsString (),
                                                                                       new URI ("PEPPOL"),
                                                                                       new URI ("PEPPOL-" +
                                                                                                CODELIST_VERSION.getAsString ()),
                                                                                       new URI ("PartyID.gc"));
    _writeGenericodeFile (aCodeList, RESULT_DIRECTORY + "PartyID" + FILENAME_SUFFIX + ".gc");
  }

  private static void _handleParticipantIdentifierSchemes (final Sheet aParticipantSheet) throws URISyntaxException
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

    // Convert to GeneriCode
    final CodeListDocument aCodeList = ExcelSheetToCodeList10.convertToSimpleCodeList (aParticipantSheet,
                                                                                       aReadOptions,
                                                                                       "PeppolIdentifierIssuingAgencies",
                                                                                       CODELIST_VERSION.getAsString (),
                                                                                       new URI ("urn:peppol.eu:names:identifier:participantidentifierschemes"),
                                                                                       new URI ("urn:peppol.eu:names:identifier:participantidentifierschemes-2.0"),
                                                                                       null);
    _writeGenericodeFile (aCodeList, RESULT_DIRECTORY + "PeppolParticipantIdentifierSchemes" + FILENAME_SUFFIX + ".gc");

    _writeValidationPartyIdFile (aParticipantSheet);

    // Save data also as XML
    {
      final IMicroDocument aDoc = new MicroDocument ();
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", CODELIST_VERSION.getAsString ());
      for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ())
      {
        final String sSchemeID = Helper.getRowValue (aRow, "schemeid");
        final String sISO6523 = Helper.getRowValue (aRow, "iso6523");
        final String sCountryCode = Helper.getRowValue (aRow, "country");
        final String sSchemeName = Helper.getRowValue (aRow, "schemename");
        final String sIssuingAgency = Helper.getRowValue (aRow, "issuingagency");
        final String sSince = Helper.getRowValue (aRow, "since");
        final boolean bDeprecated = Helper.parseDeprecated (Helper.getRowValue (aRow, "deprecated"));
        final String sDeprecatedSince = Helper.getRowValue (aRow, "deprecated-since");
        final String sStructure = Helper.getRowValue (aRow, "structure");
        final String sDisplay = Helper.getRowValue (aRow, "display");
        final String sExamples = Helper.getRowValue (aRow, "examples");
        final String sValidationRules = Helper.getRowValue (aRow, "validation-rules");
        final String sUsage = Helper.getRowValue (aRow, "usage");

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
      MicroWriter.writeToFile (aDoc,
                               new File (RESULT_DIRECTORY +
                                         "PeppolParticipantIdentifierSchemes" +
                                         FILENAME_SUFFIX +
                                         ".xml"));
    }
  }

  private static void _handleTransportProfileIdentifiers (final Sheet aTPSheet) throws URISyntaxException
  {
    final ExcelReadOptions <UseType> aReadOptions = new ExcelReadOptions <UseType> ().setLinesToSkip (1)
                                                                                     .setLineIndexShortName (0);
    aReadOptions.addColumn (0, "protocol", UseType.REQUIRED, "string", false);
    aReadOptions.addColumn (1, "profileversion", UseType.REQUIRED, "string", false);
    aReadOptions.addColumn (2, "profileid", UseType.REQUIRED, "string", true);
    aReadOptions.addColumn (3, "since", UseType.REQUIRED, "string", false);
    aReadOptions.addColumn (4, "deprecated", UseType.REQUIRED, "boolean", false);
    aReadOptions.addColumn (5, "deprecated-since", UseType.OPTIONAL, "string", false);

    final CodeListDocument aCodeList = ExcelSheetToCodeList10.convertToSimpleCodeList (aTPSheet,
                                                                                       aReadOptions,
                                                                                       "PeppolTransportProfileIdentifier",
                                                                                       CODELIST_VERSION.getAsString (),
                                                                                       new URI ("urn:peppol.eu:names:identifier:transportprofile"),
                                                                                       new URI ("urn:peppol.eu:names:identifier:transportprofile-1.0"),
                                                                                       null);
    _writeGenericodeFile (aCodeList, RESULT_DIRECTORY + "PeppolTransportProfileIdentifier" + FILENAME_SUFFIX + ".gc");

    // Save as XML
    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.appendComment (DO_NOT_EDIT);
    final IMicroElement eRoot = aDoc.appendElement ("root");
    eRoot.setAttribute ("version", CODELIST_VERSION.getAsString ());
    for (final Row aRow : aCodeList.getSimpleCodeList ().getRow ())
    {
      final String sProtocol = Helper.getRowValue (aRow, "protocol");
      final String sProfileVersion = Helper.getRowValue (aRow, "profileversion");
      final String sProfileID = Helper.getRowValue (aRow, "profileid");
      final String sSince = Helper.getRowValue (aRow, "since");
      final boolean bDeprecated = Helper.parseDeprecated (Helper.getRowValue (aRow, "deprecated"));
      final String sDeprecatedSince = Helper.getRowValue (aRow, "deprecated-since");

      if (bDeprecated && StringHelper.hasNoText (sDeprecatedSince))
        throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");

      final IMicroElement eAgency = eRoot.appendElement ("process");
      eAgency.setAttribute ("protocol", sProtocol);
      eAgency.setAttribute ("profileversion", sProfileVersion);
      eAgency.setAttribute ("profileid", sProfileID);
      eAgency.setAttribute ("since", sSince);
      eAgency.setAttribute ("deprecated", bDeprecated);
      eAgency.setAttribute ("deprecated-since", sDeprecatedSince);
    }
    MicroWriter.writeToFile (aDoc,
                             new File (RESULT_DIRECTORY +
                                       "PeppolTransportProfileIdentifier" +
                                       FILENAME_SUFFIX +
                                       ".xml"));
  }

  private static void _handleProcessIdentifiers ()
  {
    LOGGER.info ("Handling " + KNOWN_PROCESS_IDS.size () + " Process Identifiers");
    // TODO
  }

  public MainProcessExcel_v7 ()
  {}

  @Override
  protected void init () throws Exception
  {
    // Ensure target directory exists
    FileOperationManager.INSTANCE.createDirRecursiveIfNotExisting (new File (RESULT_DIRECTORY));
  }

  @Override
  protected void convert () throws Exception
  {
    // DocumentType must be before Processes to fill the static list
    final String sFilenameVersion = CODELIST_VERSION.getAsString (false) + CODELIST_FILE_SUFFIX;
    for (final CodeListFile aCLF : new CodeListFile [] { new CodeListFile ("Document types",
                                                                           sFilenameVersion,
                                                                           MainProcessExcel_v7::_handleDocumentTypes),
                                                         new CodeListFile ("Participant identifier schemes",
                                                                           sFilenameVersion,
                                                                           MainProcessExcel_v7::_handleParticipantIdentifierSchemes),
                                                         new CodeListFile ("Transport profiles",
                                                                           sFilenameVersion,
                                                                           MainProcessExcel_v7::_handleTransportProfileIdentifiers) })
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

  public static void main (final String [] args) throws Exception
  {
    new MainProcessExcel_v7 ().run ();
  }
}
