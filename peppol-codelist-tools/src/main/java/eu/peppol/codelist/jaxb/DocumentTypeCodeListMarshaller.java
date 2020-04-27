package eu.peppol.codelist.jaxb;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.jaxb.GenericJAXBMarshaller;

public class DocumentTypeCodeListMarshaller extends GenericJAXBMarshaller <DocumentTypesType>
{
  public DocumentTypeCodeListMarshaller ()
  {
    super (DocumentTypesType.class, new CommonsArrayList <> (CCodelists.XSD), x -> new ObjectFactory ().createDocumentTypes (x));
  }
}
