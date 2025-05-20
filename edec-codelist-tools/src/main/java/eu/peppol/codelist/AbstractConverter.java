/*
 * Copyright (C) 2020-2025 OpenPeppol AISBL (www.peppol.org)
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
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.FileHelper;
import com.helger.commons.io.file.FileOperationManager;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.commons.io.stream.NonBlockingBufferedOutputStream;
import com.helger.commons.url.SimpleURL;
import com.helger.commons.version.Version;
import com.helger.genericode.CGenericode;
import com.helger.genericode.Genericode10CodeListMarshaller;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.html.EHTMLVersion;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.hc.config.HCConversionSettings;
import com.helger.html.hc.html.embedded.EHCCORSSettings;
import com.helger.html.hc.html.forms.HCButton;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.metadata.EHCLinkType;
import com.helger.html.hc.html.metadata.HCLink;
import com.helger.html.hc.html.metadata.HCMeta;
import com.helger.html.hc.html.metadata.HCStyle;
import com.helger.html.hc.html.root.HCHtml;
import com.helger.html.hc.html.script.HCScriptInline;
import com.helger.html.hc.html.sections.HCH1;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.html.textlevel.HCEM;
import com.helger.html.hc.render.HCRenderer;
import com.helger.html.js.UnparsedJSCodeProvider;
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
import com.helger.xml.serialize.write.EXMLSerializeIndent;

import eu.peppol.codelist.gc.GCHelper;
import eu.peppol.codelist.model.IModelRow;
import eu.peppol.codelist.model.ModelHelper;

/**
 * Abstract base processor containing only version independent stuff.
 *
 * @author Philip Helger
 */
public abstract class AbstractConverter
{
  public static final String DO_NOT_EDIT = "This is an OpenPeppol eDEC Code List.\n" +
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

  private void _writeXMLFile (@Nonnull final IMicroNode aNode, @Nonnull final String sBasename)
  {
    final File aDstFile = new File (m_aResultDir, sBasename + m_sFilenameSuffix + ".xml");
    if (MicroWriter.writeToFile (aNode, aDstFile).isFailure ())
      throw new IllegalStateException ("Failed to write file '" + aDstFile.getPath () + "'");
    LOGGER.info ("Wrote XML file '" + aDstFile.getPath () + "'");
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

  private void _writeJsonFile (@Nonnull final IJsonObject aNode, @Nonnull final String sBasename)
  {
    final File aDstFile = new File (m_aResultDir, sBasename + m_sFilenameSuffix + ".json");
    try (final NonBlockingBufferedOutputStream aOS = FileHelper.getBufferedOutputStream (aDstFile))
    {
      // Manually add the header: ^\o/^
      if (false)
        aOS.write (("/* " + DO_NOT_EDIT + " */\n").getBytes (StandardCharsets.UTF_8));
      new JsonWriter (JsonWriterSettings.DEFAULT_SETTINGS_FORMATTED).writeToStream (aNode, aOS, StandardCharsets.UTF_8);
    }
    catch (final IOException ex)
    {
      throw new IllegalStateException ("Failed to write file '" + aDstFile.getPath () + "'", ex);
    }
    LOGGER.info ("Wrote JSON file '" + aDstFile.getPath () + "'");
  }

  protected final <T extends IModelRow> void createJsonFile (@Nonnull final ICommonsList <T> aRows,
                                                             @Nonnull final String sCodeListName)
  {
    final IJsonObject aJson = new JsonObject ();
    aJson.add ("version", m_aCodeListVersion.getAsString ());
    aJson.add ("entry-count", aRows.size ());
    aJson.addJson ("values", new JsonArray ().addAllMapped (aRows, T::getAsJson));
    _writeJsonFile (aJson, sCodeListName);
  }

  private void _writeHtmlFile (@Nonnull final HCHtml aNode, @Nonnull final String sBasename)
  {
    final File aDstFile = new File (m_aResultDir, sBasename + m_sFilenameSuffix + ".html");
    final HCConversionSettings aConversionSettings = new HCConversionSettings (EHTMLVersion.HTML5);
    aConversionSettings.getXMLWriterSettings ().setIndent (EXMLSerializeIndent.ALIGN_ONLY);
    final String sHtml = HCRenderer.getAsHTMLString (aNode, aConversionSettings);
    if (SimpleFileIO.writeFile (aDstFile, sHtml, StandardCharsets.UTF_8).isFailure ())
      throw new IllegalStateException ("Failed to write file '" + aDstFile.getPath () + "'");

    LOGGER.info ("Wrote Html file '" + aDstFile.getPath () + "'");
  }

  protected final <T extends IModelRow> void createHtmlFile (@Nonnull final ICommonsList <T> aRows,
                                                             @Nonnull final String sCodeListName,
                                                             @Nonnull final Supplier <HCRow> aHeaderRowProvider)
  {
    // Add number column only if the amount of entries reaches a certain size
    final boolean bAddNumColumn = aRows.size () > 20;

    final HCHtml aHtml = new HCHtml ();
    aHtml.head ().metaElements ().add (new HCMeta ().setCharset (StandardCharsets.UTF_8.name ()));
    aHtml.head ()
         .metaElements ()
         .add (new HCMeta ().setName ("viewport").setContent ("width=device-width, initial-scale=1"));
    aHtml.head ().setPageTitle (sCodeListName);
    aHtml.head ()
         .links ()
         .add (new HCLink ().setRel (EHCLinkType.STYLESHEET)
                            .setHref (new SimpleURL ("https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"))
                            .setIntegrity ("sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH")
                            .setCrossOrigin (EHCCORSSettings.ANONYMOUS));
    if (false)
      aHtml.head ().addCSS (new HCStyle (""));

    final HCDiv aCont = aHtml.body ()
                             .addAndReturnChild (new HCDiv ().addClass (DefaultCSSClassProvider.create ("container-fluid")));

    aCont.addChild (new HCH1 ().addChild (aHtml.head ().getPageTitle ()));

    final int nDeprecatedRows = aRows.getCount (x -> x.getState ().isDeprecated () ||
                                                     x.getState ().isScheduledForDeprecation ());
    final boolean bHasDeprecated = nDeprecatedRows > 0;
    final int nRemovedRows = aRows.getCount (x -> x.getState ().isRemoved ());
    final boolean bHasRemoved = nRemovedRows > 0;

    aCont.addChild (new HCDiv ().addChild (new HCDiv ().addChild ("Version " +
                                                                  m_aCodeListVersion.getAsString () +
                                                                  " with " +
                                                                  aRows.size () +
                                                                  " entries"))
                                .addChild (bHasDeprecated ? new HCDiv ().addChild ("Info: the " +
                                                                                   nDeprecatedRows +
                                                                                   " yellow rows are the ones that contain deprecated entries")
                                                          : null)
                                .addChild (bHasRemoved ? new HCDiv ().addChild ("Info: the " +
                                                                                nRemovedRows +
                                                                                " red rows are the ones that contain removed entries")
                                                       : null)
                                .addChild (bAddNumColumn ? new HCDiv ().addChild ("Info: The first column is a counter for easier orientation in the list and has NO meaning at all.")
                                                         : null)
                                .addClass (DefaultCSSClassProvider.create ("alert"))
                                .addClass (DefaultCSSClassProvider.create ("alert-info")));

    if (bHasDeprecated || bHasRemoved)
    {
      final HCDiv aButtonRow = new HCDiv ().addClass (DefaultCSSClassProvider.create ("my-3"));
      String sJS = "";
      if (bHasDeprecated)
      {
        final String sHide = "Hide " + nDeprecatedRows + " Deprecated";
        final String sShow = "Show " + nDeprecatedRows + " Deprecated";
        final String sIDButtonDeprecated = "btdep";
        sJS += "var showDep=true;" +
               "function toggleDep(){" +
               "let rows=document.querySelectorAll('tr.table-warning'),i;" +
               "for(i=0;i<rows.length;++i)" +
               "rows[i].style.display=rows[i].style.display==='none'?'table-row':'none';" +
               "showDep=!showDep;" +
               "document.getElementById('" +
               sIDButtonDeprecated +
               "').firstChild.data=showDep?'" +
               sHide +
               "':'" +
               sShow +
               "';" +
               "}";
        aButtonRow.addChild (new HCButton (sHide).setID (sIDButtonDeprecated)
                                                 .setOnClick (new UnparsedJSCodeProvider ("toggleDep();return false;")));
      }
      if (bHasRemoved)
      {
        final String sHide = "Hide " + nRemovedRows + " Removed";
        final String sShow = "Show " + nRemovedRows + " Removed";
        final String sIDButtonRemoved = "btrem";
        sJS += "var showRem=true;" +
               "function toggleRem(){" +
               "let rows=document.querySelectorAll('tr.table-danger'),i;" +
               "for(i=0;i<rows.length;++i)" +
               "rows[i].style.display=rows[i].style.display==='none'?'table-row':'none';" +
               "showRem=!showRem;" +
               "document.getElementById('" +
               sIDButtonRemoved +
               "').firstChild.data=showRem?'" +
               sHide +
               "':'" +
               sShow +
               "';" +
               "}";
        aButtonRow.addChild (new HCButton (sHide).setID (sIDButtonRemoved)
                                                 .setOnClick (new UnparsedJSCodeProvider ("toggleRem();return false;")));
      }
      aHtml.head ().addJS (new HCScriptInline (new UnparsedJSCodeProvider (sJS)));
      aCont.addChild (aButtonRow);
    }

    final HCTable aTable = aCont.addAndReturnChild (new HCTable ());
    aTable.addClass (DefaultCSSClassProvider.create ("table"));
    if (false)
      aTable.addClass (DefaultCSSClassProvider.create ("thead-light"));
    aTable.addClass (DefaultCSSClassProvider.create ("table-striped"));
    if (false)
      aTable.addClass (DefaultCSSClassProvider.create ("table-responsive"));
    aTable.getHead ().addClass (DefaultCSSClassProvider.create ("table-light"));
    {
      final HCRow aHeaderRow = aHeaderRowProvider.get ();
      if (bAddNumColumn)
        aHeaderRow.addCellAt (0, "Num#");
      aTable.addHeaderRow (aHeaderRow);
      int nRowNum = 1;
      for (final T aRow : aRows)
      {
        final HCRow aBodyRow = aRow.getAsHtmlTableBodyRow ();
        if (bAddNumColumn)
        {
          final String sRowNum = Integer.toString (nRowNum);
          aBodyRow.addCellAt (0)
                  .addChild (new HCA ().setName (sRowNum).addChild (sRowNum))
                  .addClass (ModelHelper.CSS_TEXT_END);
        }
        aTable.addBodyRow (aBodyRow);
        ++nRowNum;
      }
    }
    aCont.addChild (new HCDiv ().addChild (new HCEM ().addChild ("This document was created automatically."))
                                .addChild (" The official version is located at ")
                                .addChild (new HCA (new SimpleURL ("https://docs.peppol.eu/edelivery/codelists/")).addChild ("https://docs.peppol.eu/edelivery/codelists/"))
                                .addClasses (DefaultCSSClassProvider.create ("alert"),
                                             DefaultCSSClassProvider.create ("alert-secondary")));
    _writeHtmlFile (aHtml, sCodeListName);
  }

  protected abstract void convert () throws Exception;

  public final void run () throws Exception
  {
    convert ();
    LOGGER.info ("Successfully finished creation");
  }
}
