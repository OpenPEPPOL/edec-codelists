package eu.peppol.codelist.excel;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;

public enum ECodeListDataType
{
  STRING ("string"),
  BOOLEAN ("boolean"),
  INT ("int");

  private final String m_sID;

  ECodeListDataType (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }
}
