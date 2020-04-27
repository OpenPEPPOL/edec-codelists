package eu.peppol.codelist.jaxb;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.jaxb.GenericJAXBMarshaller;

public class ParticipantIdentifierSchemeCodeListMarshaller extends GenericJAXBMarshaller <ParticipantIdentifierSchemesType>
{
  public ParticipantIdentifierSchemeCodeListMarshaller ()
  {
    super (ParticipantIdentifierSchemesType.class,
           new CommonsArrayList <> (CCodelists.XSD),
           x -> new ObjectFactory ().createParticipantIdentifierSchemes (x));
  }
}
