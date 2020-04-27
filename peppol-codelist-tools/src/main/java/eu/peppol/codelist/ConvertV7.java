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

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.version.Version;
import com.helger.peppolid.IProcessIdentifier;

import eu.peppol.codelist.excel.InMemoryXLSX;
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

    // Create files
    createGenericodeFile (aRows, DocTypeRow.CODE_LIST_NAME, DocTypeRow::addColumns, DocTypeRow.CODE_LIST_URI);
    createXMLFile (aRows, DocTypeRow.CODE_LIST_NAME);
    createJsonFile (aRows, DocTypeRow.CODE_LIST_NAME);
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

    // Create files
    createGenericodeFile (aRows,
                          ParticipantIdentifierSchemeRow.CODE_LIST_NAME,
                          ParticipantIdentifierSchemeRow::addColumns,
                          ParticipantIdentifierSchemeRow.CODE_LIST_URI);
    createXMLFile (aRows, ParticipantIdentifierSchemeRow.CODE_LIST_NAME);
    createJsonFile (aRows, ParticipantIdentifierSchemeRow.CODE_LIST_NAME);
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

    // Create files
    createGenericodeFile (aRows, TransportProfileRow.CODE_LIST_NAME, TransportProfileRow::addColumns, TransportProfileRow.CODE_LIST_URI);
    createXMLFile (aRows, TransportProfileRow.CODE_LIST_NAME);
    createJsonFile (aRows, TransportProfileRow.CODE_LIST_NAME);
  }

  private void _handleProcessIdentifiers ()
  {
    // Convert to domain object
    final ICommonsList <ProcessRow> aRows = new CommonsArrayList <> (m_aProcIDs, ProcessRow::createFromID);

    // Consistency checks
    for (final ProcessRow aRow : aRows)
      aRow.checkConsistency ();

    // Create files
    createGenericodeFile (aRows, ProcessRow.CODE_LIST_NAME, ProcessRow::addColumns, ProcessRow.CODE_LIST_URI);
    createXMLFile (aRows, ProcessRow.CODE_LIST_NAME);
    createJsonFile (aRows, ProcessRow.CODE_LIST_NAME);
  }

  @Override
  protected void convert () throws Exception
  {
    final String sFilenameVersion = m_aCodeListVersion.getAsString (false) + " draft";

    new CodeListSource ("Document types", sFilenameVersion, this::_handleDocumentTypes).readExcelSheet ();
    new CodeListSource ("Participant identifier schemes", sFilenameVersion, this::_handleParticipantIdentifierSchemes).readExcelSheet ();
    new CodeListSource ("Transport profiles", sFilenameVersion, this::_handleTransportProfileIdentifiers).readExcelSheet ();

    _handleProcessIdentifiers ();
  }
}
