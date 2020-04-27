package eu.peppol.codelist.jaxb;

import com.helger.commons.io.resource.ClassPathResource;

final class CCodelists
{
  public static final ClassPathResource XSD = new ClassPathResource ("/schemas/peppol-codelists-v1.xsd", CCodelists.class.getClassLoader ());

  private CCodelists ()
  {}
}
