package com.github.pratzn.oss.common.aop;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ThrowsAdvice;

public class ExceptionLogAdvice
  implements ThrowsAdvice
{
  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionLogAdvice.class);
  private int maxExceptionCount = 1;
  private final ThreadLocal<Map<Exception, String>> alreadyLoggingException = new ThreadLocal<Map<Exception, String>>()
  {
    protected Map<Exception, String> initialValue()
    {
      return new LinkedHashMap<Exception, String>()
      {

		protected boolean removeEldestEntry(Map.Entry<Exception, String> eldest)
        {
          return size() > ExceptionLogAdvice.this.maxExceptionCount;
        }
      };
    }
  };
  
  public final void afterThrowing(Method method, Object[] args, Object target, Exception ex)
  {
    initLogging(method, args, target, ex);
    if (isEnableExceptionLogging(method, args, target, ex))
    {
      logging(method, args, target, ex);
      afterLogging(method, args, target, ex);
    }
  }
  
  protected void logging(Method method, Object[] args, Object target, Exception ex)
  {
    LOGGER.error("Exception occurred during the execution of the method. Method : " + method.toString(), ex);
  }
  
  protected void initLogging(Method method, Object[] args, Object target, Exception ex) {}
  
  protected void afterLogging(Method method, Object[] args, Object target, Exception ex)
  {
    getAlreadyLoggingException().put(ex, "exception");
  }
  
  protected boolean isEnableExceptionLogging(Method method, Object[] args, Object target, Exception ex)
  {
    return !getAlreadyLoggingException().containsKey(ex);
  }
  
  public int getMaxExceptionCount()
  {
    return this.maxExceptionCount;
  }
  
  public void setMaxExceptionCount(int maxExceptionCount)
  {
    this.maxExceptionCount = maxExceptionCount;
  }
  
  public Map<Exception, String> getAlreadyLoggingException()
  {
    return (Map)this.alreadyLoggingException.get();
  }
}
