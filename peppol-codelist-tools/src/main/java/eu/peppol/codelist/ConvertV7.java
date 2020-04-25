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

import eu.peppol.codelist.excel.InMemoryXLSX;
import eu.peppol.codelist.excel.XLSXReadOptions;
import eu.peppol.codelist.excel.XLSXToGC;
import eu.peppol.codelist.field.EDocTypeField;
import eu.peppol.codelist.field.EParticipantIDSchemeField;
import eu.peppol.codelist.field.EProcessIDField;
import eu.peppol.codelist.field.ETransportProfilesField;

/**
 * Handle V7 code list
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
    for (final EDocTypeField e : EDocTypeField.values ())
      aReadOptions.addColumn (e.field ());
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
        final String sProfileCode = getGCRowValue (aRow, EDocTypeField.NAME);
        final String sScheme = getGCRowValue (aRow, EDocTypeField.SCHEME);
        final String sID = getGCRowValue (aRow, EDocTypeField.ID);
        final String sSince = getGCRowValue (aRow, EDocTypeField.SINCE);
        final boolean bDeprecated = parseDeprecated (getGCRowValue (aRow, EDocTypeField.DEPRECATED));
        final String sDeprecatedSince = getGCRowValue (aRow, EDocTypeField.DEPRECATED_SINCE);
        final boolean bIssuedByOpenPEPPOL = parseIssuedByOpenPEPPOL (getGCRowValue (aRow,
                                                                                    EDocTypeField.ISSUED_BY_OPENPEPPOL));
        final String sBISVersion = getGCRowValue (aRow, EDocTypeField.BIS_VERSION);
        final String sDomainCommunity = getGCRowValue (aRow, EDocTypeField.DOMAIN_COMMUNITY);
        final String sProcessIDs = getGCRowValue (aRow, EDocTypeField.PROCESS_IDs);

        if (bDeprecated && StringHelper.hasNoText (sDeprecatedSince))
          throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");
        if (bIssuedByOpenPEPPOL && StringHelper.hasNoText (sBISVersion))
          throw new IllegalStateException ("If issued by OpenPEPPOL, a BIS version is required");
        if (StringHelper.hasText (sBISVersion) && !StringParser.isUnsignedInt (sBISVersion))
          throw new IllegalStateException ("Code list entry has an invalid BIS version number - must be numeric");

        final IMicroElement eAgency = eRoot.appendElement ("document-type");
        eAgency.setAttribute (EDocTypeField.NAME.field ().getColumnID (), sProfileCode);
        eAgency.setAttribute (EDocTypeField.SCHEME.field ().getColumnID (), sScheme);
        eAgency.setAttribute (EDocTypeField.ID.field ().getColumnID (), sID);
        eAgency.setAttribute (EDocTypeField.SINCE.field ().getColumnID (), sSince);
        eAgency.setAttribute (EDocTypeField.DEPRECATED.field ().getColumnID (), bDeprecated);
        eAgency.setAttribute (EDocTypeField.DEPRECATED_SINCE.field ().getColumnID (), sDeprecatedSince);
        eAgency.setAttribute (EDocTypeField.ISSUED_BY_OPENPEPPOL.field ().getColumnID (), bIssuedByOpenPEPPOL);
        eAgency.setAttribute (EDocTypeField.BIS_VERSION.field ().getColumnID (), sBISVersion);
        eAgency.setAttribute (EDocTypeField.DOMAIN_COMMUNITY.field ().getColumnID (), sDomainCommunity);
        final ICommonsList <IProcessIdentifier> aProcIDs = getAllProcessIDsFromMultilineString (sProcessIDs);
        for (final IProcessIdentifier aProcID : aProcIDs)
        {
          eAgency.appendElement ("process-id")
                 .setAttribute (EProcessIDField.SCHEME.field ().getColumnID (), aProcID.getScheme ())
                 .setAttribute (EProcessIDField.VALUE.field ().getColumnID (), aProcID.getValue ());
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
    for (final EParticipantIDSchemeField e : EParticipantIDSchemeField.values ())
      aReadOptions.addColumn (e.field ());
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
        final String sSchemeID = getGCRowValue (aRow, EParticipantIDSchemeField.SCHEME_ID);
        final String sISO6523 = getGCRowValue (aRow, EParticipantIDSchemeField.ISO6523);
        final String sCountryCode = getGCRowValue (aRow, EParticipantIDSchemeField.COUNTRY);
        final String sSchemeName = getGCRowValue (aRow, EParticipantIDSchemeField.SCHEME_NAME);
        final String sIssuingAgency = getGCRowValue (aRow, EParticipantIDSchemeField.ISSUING_AGENCY);
        final String sSince = getGCRowValue (aRow, EParticipantIDSchemeField.SINCE);
        final boolean bDeprecated = parseDeprecated (getGCRowValue (aRow, EParticipantIDSchemeField.DEPRECATED));
        final String sDeprecatedSince = getGCRowValue (aRow, EParticipantIDSchemeField.DEPRECATED_SINCE);
        final String sStructure = getGCRowValue (aRow, EParticipantIDSchemeField.STRUCTURE);
        final String sDisplay = getGCRowValue (aRow, EParticipantIDSchemeField.DISPLAY);
        final String sExamples = getGCRowValue (aRow, EParticipantIDSchemeField.EXAMPLES);
        final String sValidationRules = getGCRowValue (aRow, EParticipantIDSchemeField.VALIDATION_RULES);
        final String sUsage = getGCRowValue (aRow, EParticipantIDSchemeField.USAGE);

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
        eAgency.setAttribute (EParticipantIDSchemeField.SCHEME_ID.field ().getColumnID (), sSchemeID);
        eAgency.setAttribute (EParticipantIDSchemeField.COUNTRY.field ().getColumnID (), sCountryCode);
        eAgency.setAttribute (EParticipantIDSchemeField.SCHEME_NAME.field ().getColumnID (), sSchemeName);
        eAgency.setAttribute (EParticipantIDSchemeField.ISSUING_AGENCY.field ().getColumnID (), sIssuingAgency);
        eAgency.setAttribute (EParticipantIDSchemeField.ISO6523.field ().getColumnID (), sISO6523);
        eAgency.setAttribute (EParticipantIDSchemeField.SINCE.field ().getColumnID (), sSince);
        eAgency.setAttribute (EParticipantIDSchemeField.DEPRECATED.field ().getColumnID (), bDeprecated);
        eAgency.setAttribute (EParticipantIDSchemeField.DEPRECATED_SINCE.field ().getColumnID (), sDeprecatedSince);
        if (StringHelper.hasText (sStructure))
          eAgency.appendElement (EParticipantIDSchemeField.STRUCTURE.field ().getColumnID ()).appendText (sStructure);
        if (StringHelper.hasText (sDisplay))
          eAgency.appendElement (EParticipantIDSchemeField.DISPLAY.field ().getColumnID ()).appendText (sDisplay);
        if (StringHelper.hasText (sExamples))
          eAgency.appendElement (EParticipantIDSchemeField.EXAMPLES.field ().getColumnID ()).appendText (sExamples);
        if (StringHelper.hasText (sValidationRules))
          eAgency.appendElement (EParticipantIDSchemeField.VALIDATION_RULES.field ().getColumnID ())
                 .appendText (sValidationRules);
        if (StringHelper.hasText (sUsage))
          eAgency.appendElement (EParticipantIDSchemeField.USAGE.field ().getColumnID ()).appendText (sUsage);
      }
    }

    // Write at the end
    writeGenericodeFile (aCodeList, sCodeListName);
    writeXMLFile (aDoc, sCodeListName);
  }

  private void _handleTransportProfileIdentifiers (final Sheet aTPSheet)
  {
    final XLSXReadOptions aReadOptions = new XLSXReadOptions ();
    for (final ETransportProfilesField e : ETransportProfilesField.values ())
      aReadOptions.addColumn (e.field ());
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
        final String sProtocol = getGCRowValue (aRow, ETransportProfilesField.PROTOCOL);
        final String sProfileVersion = getGCRowValue (aRow, ETransportProfilesField.PROFILE_VERSION);
        final String sProfileID = getGCRowValue (aRow, ETransportProfilesField.PROFILE_ID);
        final String sSince = getGCRowValue (aRow, ETransportProfilesField.SINCE);
        final boolean bDeprecated = parseDeprecated (getGCRowValue (aRow, ETransportProfilesField.DEPRECATED));
        final String sDeprecatedSince = getGCRowValue (aRow, ETransportProfilesField.DEPRECATED_SINCE);

        if (bDeprecated && StringHelper.hasNoText (sDeprecatedSince))
          throw new IllegalStateException ("Code list entry is deprecated but there is no deprecated-since entry");

        final IMicroElement eAgency = eRoot.appendElement ("transport-profile");
        eAgency.setAttribute (ETransportProfilesField.PROTOCOL.field ().getColumnID (), sProtocol);
        eAgency.setAttribute (ETransportProfilesField.PROFILE_VERSION.field ().getColumnID (), sProfileVersion);
        eAgency.setAttribute (ETransportProfilesField.PROFILE_ID.field ().getColumnID (), sProfileID);
        eAgency.setAttribute (ETransportProfilesField.SINCE.field ().getColumnID (), sSince);
        eAgency.setAttribute (ETransportProfilesField.DEPRECATED.field ().getColumnID (), bDeprecated);
        eAgency.setAttribute (ETransportProfilesField.DEPRECATED_SINCE.field ().getColumnID (), sDeprecatedSince);
      }
    }

    // Write at the end
    writeGenericodeFile (aCodeList, sCodeListName);
    writeXMLFile (aDoc, sCodeListName);
  }

  private void _handleProcessIdentifiers ()
  {
    final XLSXReadOptions aReadOptions = new XLSXReadOptions ();
    for (final EProcessIDField e : EProcessIDField.values ())
      aReadOptions.addColumn (e.field ());

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
        eProcess.setAttribute (EProcessIDField.SCHEME.field ().getColumnID (), aProcID.getScheme ());
        eProcess.setAttribute (EProcessIDField.VALUE.field ().getColumnID (), aProcID.getValue ());
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
