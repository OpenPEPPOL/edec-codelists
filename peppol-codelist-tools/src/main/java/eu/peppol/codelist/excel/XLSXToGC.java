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
package eu.peppol.codelist.excel;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.version.Version;
import com.helger.genericode.Genericode10Helper;
import com.helger.genericode.v10.Annotation;
import com.helger.genericode.v10.AnyOtherContent;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.Column;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Identification;
import com.helger.genericode.v10.Key;
import com.helger.genericode.v10.Row;
import com.helger.genericode.v10.SimpleCodeList;
import com.helger.genericode.v10.UseType;
import com.helger.genericode.v10.Value;

import eu.peppol.codelist.AbstractConverter;

/**
 * A utility class to convert a simple Excel sheet into a code list v1.0
 * consisting of simple values. Please note that merged cells are currently not
 * supported!
 *
 * @author Philip Helger
 */
@Immutable
public final class XLSXToGC
{
  public static final String ANNOTATION_NS = "urn:peppol.eu:codelist:gc-annotation";
  public static final QName QNAME_ANNOTATION = new QName (ANNOTATION_NS, "info");

  private XLSXToGC ()
  {}

  @Nonnull
  public static CodeListDocument convertToSimpleCodeList (@Nonnull final InMemoryXLSX aExcelSheet,
                                                          @Nonnull final ICommonsList <XLSXColumn> aExcelColumns,
                                                          @Nonnull final String sCodeListName,
                                                          @Nonnull final Version aCodeListVersion,
                                                          @Nonnull final URI aCanonicalUri)
  {
    ValueEnforcer.notNull (aExcelSheet, "ExcelSheet");
    ValueEnforcer.notEmptyNoNullValue (aExcelColumns, "aExcelColumns");

    final CodeListDocument ret = new CodeListDocument ();

    // create annotation
    final Annotation aAnnotation = new Annotation ();
    final AnyOtherContent aContent = new AnyOtherContent ();
    aContent.addAny (new JAXBElement <> (QNAME_ANNOTATION, String.class, null, AbstractConverter.DO_NOT_EDIT));
    aAnnotation.setAppInfo (aContent);
    ret.setAnnotation (aAnnotation);

    // create identification
    final Identification aIdentification = new Identification ();
    aIdentification.setShortName (Genericode10Helper.createShortName (sCodeListName));
    aIdentification.setVersion (aCodeListVersion.getAsString ());
    aIdentification.setCanonicalUri (aCanonicalUri.toString ());
    aIdentification.setCanonicalVersionUri (aCanonicalUri.toString () + aCodeListVersion.getAsString ());
    ret.setIdentification (aIdentification);

    // create columns
    final ColumnSet aColumnSet = new ColumnSet ();
    int nColIndex = 0;
    for (final String sShortName : aExcelSheet.getShortNames ())
    {
      final XLSXColumn aExcelColumn = aExcelColumns.get (nColIndex);

      // No long name
      final String sLongName = null;

      // Create Genericode column set
      final Column aColumn = Genericode10Helper.createColumn (aExcelColumn.field ().getColumnID (),
                                                              aExcelColumn.field ().isRequired () ? UseType.REQUIRED
                                                                                                  : UseType.OPTIONAL,
                                                              sShortName,
                                                              sLongName,
                                                              aExcelColumn.field ().getDataType ().getID ());
      aColumnSet.addColumnChoice (aColumn);

      if (aExcelColumn.field ().isKeyColumn ())
      {
        // Create key definition
        final Key aKey = Genericode10Helper.createKey (aExcelColumn.field ().getColumnID () + "Key",
                                                       sShortName,
                                                       sLongName,
                                                       aColumn);
        aColumnSet.addKeyChoice (aKey);
      }
      ++nColIndex;
    }
    ret.setColumnSet (aColumnSet);

    // Read items
    final SimpleCodeList aSimpleCodeList = new SimpleCodeList ();

    // Determine the row where reading should start
    for (final String [] aExcelRow : aExcelSheet.getPayload ())
    {
      // Create Genericode row
      final Row aRow = new Row ();
      for (final XLSXColumn aExcelColumn : aExcelColumns)
      {
        final String sValue = aExcelRow[aExcelColumn.getIndex ()];
        if (StringHelper.hasText (sValue) || aExcelColumn.field ().isRequired ())
        {
          // Create a single value in the current row
          final Value aValue = new Value ();
          aValue.setColumnRef (Genericode10Helper.getColumnOfID (aColumnSet, aExcelColumn.field ().getColumnID ()));
          aValue.setSimpleValue (Genericode10Helper.createSimpleValue (sValue));
          aRow.addValue (aValue);
        }
      }
      aSimpleCodeList.addRow (aRow);
    }
    ret.setSimpleCodeList (aSimpleCodeList);

    return ret;
  }
}
