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

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.string.StringHelper;
import com.helger.commons.version.Version;
import com.helger.genericode.CGenericode;
import com.helger.genericode.Genericode10CodeListMarshaller;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.xml.microdom.IMicroNode;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.namespace.MapBasedNamespaceContext;

import eu.peppol.codelist.excel.XLSXToGC;

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

  @Nonnull
  protected static ICommonsList <IProcessIdentifier> getAllProcessIDsFromMultilineString (@Nonnull final String sProcessIDs)
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

  /**
   * Write a Genericode 1.0 Document to disk
   *
   * @param aCodeList
   *        The GC code list
   * @param sBasename
   *        The filename to write to, relative to the result directory, no
   *        extension.
   */
  protected final void writeGenericodeFile (@Nonnull final CodeListDocument aCodeList, @Nonnull final String sBasename)
  {
    final MapBasedNamespaceContext aNsCtx = new MapBasedNamespaceContext ();
    aNsCtx.addMapping ("gc", CGenericode.GENERICODE_10_NAMESPACE_URI);
    aNsCtx.addMapping ("ext", XLSXToGC.ANNOTATION_NS);

    final Genericode10CodeListMarshaller aMarshaller = new Genericode10CodeListMarshaller ();
    aMarshaller.setNamespaceContext (aNsCtx);
    aMarshaller.setFormattedOutput (true);

    final File aDstFile = new File (m_aResultDir, sBasename + m_sFilenameSuffix + ".gc");
    if (aMarshaller.write (aCodeList, aDstFile).isFailure ())
      throw new IllegalStateException ("Failed to write file '" + aDstFile.getPath () + "'");
    LOGGER.info ("Wrote Genericode file '" + aDstFile.getPath () + "'");
  }

  protected final void writeXMLFile (@Nonnull final IMicroNode aNode, @Nonnull final String sBasename)
  {
    final File aDstFile = new File (m_aResultDir, sBasename + m_sFilenameSuffix + ".xml");
    if (MicroWriter.writeToFile (aNode, aDstFile).isFailure ())
      throw new IllegalStateException ("Failed to write file '" + aDstFile.getPath () + "'");
    LOGGER.info ("Wrote XML file '" + aDstFile.getPath () + "'");
  }

  protected void init () throws Exception
  {}

  protected abstract void convert () throws Exception;

  protected void done () throws Exception
  {}

  public final void run () throws Exception
  {
    init ();
    convert ();
    done ();
    LOGGER.info ("Successfully finished creation");
  }
}
