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
package eu.peppol.codelist.gc;

import java.net.URI;

import javax.xml.namespace.QName;

import org.jspecify.annotations.NonNull;

import com.helger.base.version.Version;
import com.helger.genericode.Genericode10Helper;
import com.helger.genericode.v10.Annotation;
import com.helger.genericode.v10.AnyOtherContent;
import com.helger.genericode.v10.CodeListDocument;
import com.helger.genericode.v10.Column;
import com.helger.genericode.v10.ColumnSet;
import com.helger.genericode.v10.Identification;
import com.helger.genericode.v10.Key;
import com.helger.genericode.v10.SimpleCodeList;
import com.helger.genericode.v10.UseType;

import eu.peppol.codelist.AbstractCodeListConverter;
import eu.peppol.codelist.field.ECodeListDataType;
import jakarta.xml.bind.JAXBElement;

public class GCHelper
{
  public static final String ANNOTATION_NS = "urn:peppol.eu:codelist:gc-annotation";
  public static final QName QNAME_ANNOTATION = new QName (ANNOTATION_NS, "info");

  private GCHelper ()
  {}

  @NonNull
  public static CodeListDocument createEmptyCodeList (@NonNull final String sCodeListName,
                                                      @NonNull final Version aCodeListVersion,
                                                      @NonNull final URI aCanonicalUri)
  {
    final CodeListDocument ret = new CodeListDocument ();

    // create annotation
    final Annotation aAnnotation = new Annotation ();
    final AnyOtherContent aContent = new AnyOtherContent ();
    aContent.addAny (new JAXBElement <> (QNAME_ANNOTATION, String.class, null, AbstractCodeListConverter.DO_NOT_EDIT));
    aAnnotation.setAppInfo (aContent);
    ret.setAnnotation (aAnnotation);

    // create identification
    final Identification aIdentification = new Identification ();
    aIdentification.setShortName (Genericode10Helper.createShortName (sCodeListName));
    aIdentification.setVersion (aCodeListVersion.getAsString ());
    aIdentification.setCanonicalUri (aCanonicalUri.toString ());
    aIdentification.setCanonicalVersionUri (aCanonicalUri.toString () + aCodeListVersion.getAsString ());
    ret.setIdentification (aIdentification);

    final ColumnSet aColumnSet = new ColumnSet ();
    ret.setColumnSet (aColumnSet);

    final SimpleCodeList aSimpleCodeList = new SimpleCodeList ();
    ret.setSimpleCodeList (aSimpleCodeList);

    return ret;
  }

  public static void addHeaderColumn (@NonNull final ColumnSet aColumnSet,
                                      @NonNull final String sColumnID,
                                      final boolean bIsKeyColumn,
                                      final boolean bIsRequired,
                                      @NonNull final String sShortName,
                                      @NonNull final ECodeListDataType eDataType)
  {
    // No long name
    final String sLongName = null;

    // Create Genericode column set
    final Column aColumn = Genericode10Helper.createColumn (sColumnID,
                                                            bIsRequired ? UseType.REQUIRED : UseType.OPTIONAL,
                                                            sShortName,
                                                            sLongName,
                                                            eDataType.getID ());
    aColumnSet.addColumnChoice (aColumn);

    if (bIsKeyColumn)
    {
      // Create key definition
      final Key aKey = Genericode10Helper.createKey (sColumnID + "Key", sShortName, sLongName, aColumn);
      aColumnSet.addKeyChoice (aKey);
    }
  }
}
