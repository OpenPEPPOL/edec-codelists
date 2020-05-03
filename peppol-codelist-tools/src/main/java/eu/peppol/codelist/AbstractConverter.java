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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.io.stream.NonBlockingBufferedOutputStream;
import com.helger.commons.version.Version;
import com.helger.genericode.CGenericode;
import com.helger.genericode.Genericode10CodeListMarshaller;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.json.serialize.JsonWriter;
import com.helger.json.serialize.JsonWriterSettings;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.namespace.MapBasedNamespaceContext;

import eu.peppol.codelist.gc.GCHelper;
import eu.peppol.codelist.model.IModelRow;

/**
 * Abstract base processor containing only version independent stuff.
 *
 * @author Philip Helger
 */
public abstract class AbstractConverter
{
  public static final String DO_NOT_EDIT = "This is an OpenPeppol EDEC Code List.\n" +
                                           "Official source: https://docs.peppol.eu/edelivery/codelists/\n" +
                                           "\n" +
                                           "This file was automatically generated.\n" +
                                           "Do NOT edit!";
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractConverter.class);

  protected final Version m_aCodeListVersion;
  private final File m_aResultDir;
  private final String m_sFilenameSuffix;

  protected AbstractConverter (@Nonnull final Version aCodeListVersion,
                               @Nonnull @Nonempty final String sResultDir,
                               @Nonnull final String sFilenameSuffix)
  {
    ValueEnforcer.notNull (aCodeListVersion, "CodeListVersion");
    ValueEnforcer.notEmpty (sResultDir, "ResultDir");
    ValueEnforcer.notNull (sFilenameSuffix, "FilenameSuffix");
    m_aCodeListVersion = aCodeListVersion;
    m_aResultDir = new File (sResultDir);
    m_sFilenameSuffix = sFilenameSuffix;

    // Ensure target directory exists
    FileOperationManager.INSTANCE.createDirRecursiveIfNotExisting (m_aResultDir);
  }

  protected final <T extends IModelRow> void createGenericodeFile (@Nonnull final ICommonsList <T> aRows,
                                                                   @Nonnull final String sCodeListName,
                                                                   @Nonnull final Consumer <CodeListDocument> aColumnProvider,
                                                                   @Nonnull final URI sCodeListURI)
  {
    final CodeListDocument aCodeList = GCHelper.createEmptyCodeList (sCodeListName, m_aCodeListVersion, sCodeListURI);
    aColumnProvider.accept (aCodeList);
    for (final T aRow : aRows)
      aCodeList.getSimpleCodeList ().addRow (aRow.getAsGCRow (aCodeList.getColumnSet ()));
    _writeGenericodeFile (aCodeList, sCodeListName);
  }

  /**
   * Write a Genericode 1.0 Document to disk
   *
   * @param aCodeList
   *        The GC code list
   * @param sBasename
   *        The filename to write to, relative to the result directory, no
   *        extension.
   */
  private void _writeGenericodeFile (@Nonnull final CodeListDocument aCodeList, @Nonnull final String sBasename)
  {
    final MapBasedNamespaceContext aNsCtx = new MapBasedNamespaceContext ();
    aNsCtx.addMapping ("gc", CGenericode.GENERICODE_10_NAMESPACE_URI);
    aNsCtx.addMapping ("ext", GCHelper.ANNOTATION_NS);

    final Genericode10CodeListMarshaller aMarshaller = new Genericode10CodeListMarshaller ();
    aMarshaller.setNamespaceContext (aNsCtx);
    aMarshaller.setFormattedOutput (true);

    final File aDstFile = new File (m_aResultDir, sBasename + m_sFilenameSuffix + ".gc");
    if (aMarshaller.write (aCodeList, aDstFile).isFailure ())
      throw new IllegalStateException ("Failed to write file '" + aDstFile.getPath () + "'");
    LOGGER.info ("Wrote Genericode file '" + aDstFile.getPath () + "'");
  }

  protected final <T extends IModelRow> void createXMLFile (@Nonnull final ICommonsList <T> aRows,
                                                            @Nonnull final String sCodeListName,
                                                            final String sRootElementName)
  {
    final IMicroDocument aDoc = new MicroDocument ();
    aDoc.appendComment (DO_NOT_EDIT);
    final IMicroElement eRoot = aDoc.appendElement (sRootElementName);
    eRoot.setAttribute ("version", m_aCodeListVersion.getAsString ());
    eRoot.setAttribute ("entry-count", aRows.size ());
    for (final T aRow : aRows)
      eRoot.appendChild (aRow.getAsElement ());
    _writeXMLFile (aDoc, sCodeListName);
  }

  private void _writeXMLFile (@Nonnull final IMicroNode aNode, @Nonnull final String sBasename)
  {
    final File aDstFile = new File (m_aResultDir, sBasename + m_sFilenameSuffix + ".xml");
    if (MicroWriter.writeToFile (aNode, aDstFile).isFailure ())
      throw new IllegalStateException ("Failed to write file '" + aDstFile.getPath () + "'");
    LOGGER.info ("Wrote XML file '" + aDstFile.getPath () + "'");
  }

  protected final <T extends IModelRow> void createJsonFile (@Nonnull final ICommonsList <T> aRows, @Nonnull final String sCodeListName)
  {
    final IJsonObject aJson = new JsonObject ();
    aJson.add ("version", m_aCodeListVersion.getAsString ());
    aJson.add ("entry-count", aRows.size ());
    final IJsonArray aValues = new JsonArray ();
    for (final T aRow : aRows)
      aValues.add (aRow.getAsJson ());
    aJson.add ("values", aValues);
    _writeJsonFile (aJson, sCodeListName);
  }

  private void _writeJsonFile (@Nonnull final IJsonObject aNode, @Nonnull final String sBasename)
  {
    final File aDstFile = new File (m_aResultDir, sBasename + m_sFilenameSuffix + ".json");
    try (final NonBlockingBufferedOutputStream aOS = FileHelper.getBufferedOutputStream (aDstFile))
    {
      // Manually add the header: ^\o/^
      aOS.write (("/* " + DO_NOT_EDIT + " */\n").getBytes (StandardCharsets.UTF_8));
      new JsonWriter (new JsonWriterSettings ().setIndentEnabled (true)).writeToStream (aNode, aOS, StandardCharsets.UTF_8);
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to write file '" + aDstFile.getPath () + "'", ex);
    }
    LOGGER.info ("Wrote JSON file '" + aDstFile.getPath () + "'");
  }

  protected abstract void convert () throws Exception;

  public final void run () throws Exception
  {
    convert ();
    LOGGER.info ("Successfully finished creation");
  }
}
