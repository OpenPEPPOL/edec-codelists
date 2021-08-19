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
package eu.peppol.codelist;

import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.poi.ss.usermodel.Sheet;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.version.Version;
import com.helger.peppolid.IProcessIdentifier;

import eu.peppol.codelist.excel.InMemoryXLSX;
import eu.peppol.codelist.model.DocTypeRow;
import eu.peppol.codelist.model.ParticipantIdentifierSchemeRow;
import eu.peppol.codelist.model.ProcessRow;
import eu.peppol.codelist.model.TransportProfileRow;

/**
 * Handle V7+V8 code lists
 *
 * @author Philip Helger
 */
public abstract class AbstractConvertV7andV8 extends AbstractConverter
{
  private final ICommonsMap <IProcessIdentifier, ICommonsList <DocTypeRow>> m_aProcIDs = new CommonsLinkedHashMap <> ();

  public AbstractConvertV7andV8 (@Nonnull final Version aCodeListVersion,
                            @Nonnull @Nonempty final String sResultDir,
                            @Nonnull final String sFilenameSuffix)
  {
    super (aCodeListVersion, sResultDir, sFilenameSuffix);
  }

  private void _handleDocumentTypes (@Nonnull final Sheet aDocumentSheet)
  {
    // Read Excel
    final InMemoryXLSX aXLSX = InMemoryXLSX.read (aDocumentSheet, 11);

    // Convert to domain object
    final ICommonsList <DocTypeRow> aRows = aXLSX.getAsList (DocTypeRow::createV7);

    // Collect all proc types
    for (final DocTypeRow aRow : aRows)
      for (final IProcessIdentifier aProcID : aRow.getAllProcessIDs ())
        m_aProcIDs.computeIfAbsent (aProcID, k -> new CommonsArrayList <> ()).add (aRow);

    // Consistency checks
    for (final DocTypeRow aRow : aRows)
      aRow.checkConsistency ();

    // Create files
    createGenericodeFile (aRows, DocTypeRow.CODE_LIST_NAME, DocTypeRow::addGCColumns, DocTypeRow.CODE_LIST_URI);
    createXMLFile (aRows, DocTypeRow.CODE_LIST_NAME, DocTypeRow.ROOT_ELEMENT_NAME);
    createJsonFile (aRows, DocTypeRow.CODE_LIST_NAME);
    createHtmlFile (aRows, DocTypeRow.CODE_LIST_NAME, DocTypeRow::getAsHtmlTableHeaderRow);
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
                          ParticipantIdentifierSchemeRow::addGCColumns,
                          ParticipantIdentifierSchemeRow.CODE_LIST_URI);
    createXMLFile (aRows, ParticipantIdentifierSchemeRow.CODE_LIST_NAME, ParticipantIdentifierSchemeRow.ROOT_ELEMENT_NAME);
    createJsonFile (aRows, ParticipantIdentifierSchemeRow.CODE_LIST_NAME);
    createHtmlFile (aRows, ParticipantIdentifierSchemeRow.CODE_LIST_NAME, ParticipantIdentifierSchemeRow::getAsHtmlTableHeaderRow);
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
    createGenericodeFile (aRows, TransportProfileRow.CODE_LIST_NAME, TransportProfileRow::addGCColumns, TransportProfileRow.CODE_LIST_URI);
    createXMLFile (aRows, TransportProfileRow.CODE_LIST_NAME, TransportProfileRow.ROOT_ELEMENT_NAME);
    createJsonFile (aRows, TransportProfileRow.CODE_LIST_NAME);
    createHtmlFile (aRows, TransportProfileRow.CODE_LIST_NAME, TransportProfileRow::getAsHtmlTableHeaderRow);
  }

  private void _handleProcessIdentifiers ()
  {
    // Convert to domain object
    final ICommonsList <ProcessRow> aRows = new CommonsArrayList <> ();
    for (final Map.Entry <IProcessIdentifier, ICommonsList <DocTypeRow>> aEntry : m_aProcIDs.entrySet ())
      aRows.add (ProcessRow.createFromID (aEntry.getKey (), aEntry.getValue ()));

    // Consistency checks
    for (final ProcessRow aRow : aRows)
      aRow.checkConsistency ();

    // Create files
    createGenericodeFile (aRows, ProcessRow.CODE_LIST_NAME, ProcessRow::addGCColumns, ProcessRow.CODE_LIST_URI);
    createXMLFile (aRows, ProcessRow.CODE_LIST_NAME, ProcessRow.ROOT_ELEMENT_NAME);
    createJsonFile (aRows, ProcessRow.CODE_LIST_NAME);
    createHtmlFile (aRows, ProcessRow.CODE_LIST_NAME, ProcessRow::getAsHtmlTableHeaderRow);
  }

  @Override
  protected void convert () throws Exception
  {
    final String sFilenameVersion = m_aCodeListVersion.getAsString (false, true);

    new CodeListSource ("Document types", sFilenameVersion, this::_handleDocumentTypes).readExcelSheet ();
    new CodeListSource ("Participant identifier schemes", sFilenameVersion, this::_handleParticipantIdentifierSchemes).readExcelSheet ();
    new CodeListSource ("Transport profiles", sFilenameVersion, this::_handleTransportProfileIdentifiers).readExcelSheet ();

    _handleProcessIdentifiers ();
  }
}
