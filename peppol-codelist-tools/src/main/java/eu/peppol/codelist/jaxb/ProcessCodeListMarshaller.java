package eu.peppol.codelist.jaxb;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.jaxb.GenericJAXBMarshaller;

public class ProcessCodeListMarshaller extends GenericJAXBMarshaller <ProcessesType>
{
  public ProcessCodeListMarshaller ()
  {
    super (ProcessesType.class, new CommonsArrayList <> (CCodelists.XSD), x -> new ObjectFactory ().createProcesses (x));
  }
}
