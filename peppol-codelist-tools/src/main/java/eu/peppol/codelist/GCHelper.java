package eu.peppol.codelist;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import com.helger.commons.version.Version;
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

import eu.peppol.codelist.field.ECodeListDataType;

public class GCHelper
{
  public static final String ANNOTATION_NS = "urn:peppol.eu:codelist:gc-annotation";
  public static final QName QNAME_ANNOTATION = new QName (ANNOTATION_NS, "info");

  private GCHelper ()
  {}

  @Nonnull
  static CodeListDocument createEmptyCodeList (@Nonnull final String sCodeListName,
                                               @Nonnull final Version aCodeListVersion,
                                               @Nonnull final URI aCanonicalUri)
  {
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

    final ColumnSet aColumnSet = new ColumnSet ();
    ret.setColumnSet (aColumnSet);

    final SimpleCodeList aSimpleCodeList = new SimpleCodeList ();
    ret.setSimpleCodeList (aSimpleCodeList);

    return ret;
  }

  static void addHeaderColumn (@Nonnull final ColumnSet aColumnSet,
                               @Nonnull final String sColumnID,
                               final boolean bIsKeyColumn,
                               final boolean bIsRequired,
                               @Nonnull final String sShortName,
                               @Nonnull final ECodeListDataType eDataType)
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
