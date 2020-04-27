package eu.peppol.codelist.jaxb;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.jaxb.GenericJAXBMarshaller;

public class TransportProfileCodeListMarshaller extends GenericJAXBMarshaller <TransportProfilesType>
{
  public TransportProfileCodeListMarshaller ()
  {
    super (TransportProfilesType.class, new CommonsArrayList <> (CCodelists.XSD), x -> new ObjectFactory ().createTransportProfiles (x));
  }
}
