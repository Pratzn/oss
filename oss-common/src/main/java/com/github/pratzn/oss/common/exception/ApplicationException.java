package com.github.pratzn.oss.common.exception;

import org.springframework.core.NestedRuntimeException;

public class ApplicationException
  extends NestedRuntimeException
{
  private static final long serialVersionUID = 1L;
  
  public ApplicationException(Throwable cause)
  {
    this("", cause);
  }
  
  public ApplicationException(String msg, Throwable cause)
  {
    super(msg, cause);
  }
  
  public ApplicationException(String msg)
  {
    super(msg);
  }
}
