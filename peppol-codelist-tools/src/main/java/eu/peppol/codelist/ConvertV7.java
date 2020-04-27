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

import javax.annotation.Nonnull;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.version.Version;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;

import eu.peppol.codelist.excel.InMemoryXLSX;
import eu.peppol.codelist.gc.GCHelper;
import eu.peppol.codelist.model.DocTypeRow;
import eu.peppol.codelist.model.ParticipantIdentifierSchemeRow;
import eu.peppol.codelist.model.ProcessRow;
import eu.peppol.codelist.model.TransportProfileRow;

/**
 * Handle V7 code list
 *
 * @author Philip Helger
 */
public final class ConvertV7 extends AbstractConverter
{
  private final ICommonsSet <IProcessIdentifier> m_aProcIDs = new CommonsLinkedHashSet <> ();

  public ConvertV7 ()
  {
    super (new Version (7), "created-codelists/v7/", "V7");
  }

  private void _handleDocumentTypes (@Nonnull final Sheet aDocumentSheet)
  {
    // Read Excel
    final InMemoryXLSX aXLSX = InMemoryXLSX.read (aDocumentSheet, 11);

    // Convert to domain object
    final ICommonsList <DocTypeRow> aRows = aXLSX.getAsList (DocTypeRow::createV7);

    // Collect all proc types
    for (final DocTypeRow aRow : aRows)
      aRow.addAllProcessIDs (m_aProcIDs);

    // Consistency checks
    for (final DocTypeRow aRow : aRows)
      aRow.checkConsistency ();

    // Create GC
    final CodeListDocument aCodeList = GCHelper.createEmptyCodeList (DocTypeRow.CODE_LIST_NAME,
                                                                     m_aCodeListVersion,
                                                                     DocTypeRow.CODE_LIST_URI);
    {
      DocTypeRow.addColumns (aCodeList);
      for (final DocTypeRow aRow : aRows)
        aCodeList.getSimpleCodeList ().addRow (aRow.getAsGCRow (aCodeList.getColumnSet ()));
    }

    // Create XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", m_aCodeListVersion.getAsString ());
      for (final DocTypeRow aRow : aRows)
        eRoot.appendChild (aRow.getAsElement ());
    }

    // Create JSON
    final IJsonObject aJson = new JsonObject ();
    {
      aJson.add ("version", m_aCodeListVersion.getAsString ());
      final IJsonArray aValues = new JsonArray ();
      for (final DocTypeRow aRow : aRows)
        aValues.add (aRow.getAsJson ());
      aJson.add ("values", aValues);
    }

    // Write at the end
    writeGenericodeFile (aCodeList, DocTypeRow.CODE_LIST_NAME);
    writeXMLFile (aDoc, DocTypeRow.CODE_LIST_NAME);
    writeJsonFile (aJson, DocTypeRow.CODE_LIST_NAME);
  }

  private void _handleParticipantIdentifierSchemes (@Nonnull final Sheet aParticipantSheet)
  {
    // Read Excel
    final InMemoryXLSX aXLSX = InMemoryXLSX.read (aParticipantSheet, 13);

    // Convert to domain object
    final ICommonsList <ParticipantIdentifierSchemeRow> aRows = aXLSX.getAsList (ParticipantIdentifierSchemeRow::createV7);

    // Consistency checks
    for (final ParticipantIdentifierSchemeRow aRow : aRows)
      aRow.checkConsistency ();

    // Create GC
    final CodeListDocument aCodeList = GCHelper.createEmptyCodeList (ParticipantIdentifierSchemeRow.CODE_LIST_NAME,
                                                                     m_aCodeListVersion,
                                                                     ParticipantIdentifierSchemeRow.CODE_LIST_URI);
    {
      ParticipantIdentifierSchemeRow.addColumns (aCodeList);
      for (final ParticipantIdentifierSchemeRow aRow : aRows)
        aCodeList.getSimpleCodeList ().addRow (aRow.getAsGCRow (aCodeList.getColumnSet ()));
    }

    // Create XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", m_aCodeListVersion.getAsString ());
      for (final ParticipantIdentifierSchemeRow aRow : aRows)
        eRoot.appendChild (aRow.getAsElement ());
    }

    // Create JSON
    final IJsonObject aJson = new JsonObject ();
    {
      aJson.add ("version", m_aCodeListVersion.getAsString ());
      final IJsonArray aValues = new JsonArray ();
      for (final ParticipantIdentifierSchemeRow aRow : aRows)
        aValues.add (aRow.getAsJson ());
      aJson.add ("values", aValues);
    }

    // Write at the end
    writeGenericodeFile (aCodeList, ParticipantIdentifierSchemeRow.CODE_LIST_NAME);
    writeXMLFile (aDoc, ParticipantIdentifierSchemeRow.CODE_LIST_NAME);
    writeJsonFile (aJson, ParticipantIdentifierSchemeRow.CODE_LIST_NAME);
  }

  private void _handleTransportProfileIdentifiers (@Nonnull final Sheet aTPSheet)
  {
    // Read Excel
    final InMemoryXLSX aXLSX = InMemoryXLSX.read (aTPSheet, 6);

    // Convert to domain object
    final ICommonsList <TransportProfileRow> aRows = aXLSX.getAsList (TransportProfileRow::createV7);

    // Consistency checks
    for (final TransportProfileRow aRow : aRows)
      aRow.checkConsistency ();

    // Create GC
    final CodeListDocument aCodeList = GCHelper.createEmptyCodeList (TransportProfileRow.CODE_LIST_NAME,
                                                                     m_aCodeListVersion,
                                                                     TransportProfileRow.CODE_LIST_URI);
    {
      TransportProfileRow.addColumns (aCodeList);
      for (final TransportProfileRow aRow : aRows)
        aCodeList.getSimpleCodeList ().addRow (aRow.getAsGCRow (aCodeList.getColumnSet ()));
    }

    // Create XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", m_aCodeListVersion.getAsString ());
      for (final TransportProfileRow aRow : aRows)
        eRoot.appendChild (aRow.getAsElement ());
    }

    // Create JSON
    final IJsonObject aJson = new JsonObject ();
    {
      aJson.add ("version", m_aCodeListVersion.getAsString ());
      final IJsonArray aValues = new JsonArray ();
      for (final TransportProfileRow aRow : aRows)
        aValues.add (aRow.getAsJson ());
      aJson.add ("values", aValues);
    }

    // Write at the end
    writeGenericodeFile (aCodeList, TransportProfileRow.CODE_LIST_NAME);
    writeXMLFile (aDoc, TransportProfileRow.CODE_LIST_NAME);
    writeJsonFile (aJson, TransportProfileRow.CODE_LIST_NAME);
  }

  private void _handleProcessIdentifiers ()
  {
    // Convert to domain object
    final ICommonsList <ProcessRow> aRows = new CommonsArrayList <> (m_aProcIDs, ProcessRow::createFromID);

    // Consistency checks
    for (final ProcessRow aRow : aRows)
      aRow.checkConsistency ();

    // Create GC
    final CodeListDocument aCodeList = GCHelper.createEmptyCodeList (ProcessRow.CODE_LIST_NAME,
                                                                     m_aCodeListVersion,
                                                                     ProcessRow.CODE_LIST_URI);
    {
      ProcessRow.addColumns (aCodeList);
      for (final ProcessRow aRow : aRows)
        aCodeList.getSimpleCodeList ().addRow (aRow.getAsGCRow (aCodeList.getColumnSet ()));
    }

    // Create XML
    final IMicroDocument aDoc = new MicroDocument ();
    {
      aDoc.appendComment (DO_NOT_EDIT);
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("version", m_aCodeListVersion.getAsString ());
      for (final ProcessRow aRow : aRows)
        eRoot.appendChild (aRow.getAsElement ());
    }

    // Create JSON
    final IJsonObject aJson = new JsonObject ();
    {
      aJson.add ("version", m_aCodeListVersion.getAsString ());
      final IJsonArray aValues = new JsonArray ();
      for (final ProcessRow aRow : aRows)
        aValues.add (aRow.getAsJson ());
      aJson.add ("values", aValues);
    }

    // Write at the end
    writeGenericodeFile (aCodeList, ProcessRow.CODE_LIST_NAME);
    writeXMLFile (aDoc, ProcessRow.CODE_LIST_NAME);
    writeJsonFile (aJson, ProcessRow.CODE_LIST_NAME);
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
        throw new IllegalStateException ("The Excel file '" + aCLF.getFile ().getAbsolutePath () + "' could not be found!");

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
